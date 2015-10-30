package com.wofu.ecommerce.groupon;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;
import meta.MD5Util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.groupon.domain.model.ws.DisneyRequestBean;
import com.groupon.ws.ObjBodyWriter;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerJob;

public class SynStockExecuter extends Executer {
	
	private String wsurl="";

	private String encoding="";

	private String key="";

	private String categoryid="";

	private String tradecontactid="";

	private String dbname="";
	
	private String username="";
	
	private String namespace="";



	public void execute() throws Exception {
		// TODO Auto-generated method stub
		TimerJob job=(TimerJob) this.getExecuteobj();
		Properties prop=StringUtil.getStringProperties(job.getParams());
		
		wsurl=prop.getProperty("wsurl");
		encoding=prop.getProperty("encoding");
		key=prop.getProperty("key");
		categoryid=prop.getProperty("categoryid");
		tradecontactid=prop.getProperty("tradecontactid");
		dbname=prop.getProperty("dbname");
		username=prop.getProperty("username");
		namespace=prop.getProperty("namespace");
		
		Connection conn=null;
		try {		
			conn= PoolHelper.getInstance().getConnection(dbname);
			getBusinessProjectInfo(conn);
		} catch (Exception e) {
			try {
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			} catch (Exception e1) {
				throw new JException("回滚事务失败");
			}
			throw new JException("同步团宝库存"+Log.getErrorMessage(e));
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				throw new JException("关闭数据库连接失败");
			}
		}
		
	}
	private void getBusinessProjectInfo(Connection conn) throws JException 
	{
		
		OMFactory soapFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = soapFactory.createOMNamespace(
				"http://www.groupon.cn/", "");
		OMElement soapResponse = soapFactory.createOMElement("groupon", omNs);
		DisneyRequestBean requestBean = new DisneyRequestBean();
		requestBean.setCategoryid(categoryid);
		String s = (new StringBuilder(String
				.valueOf(System.currentTimeMillis()))).toString();
		requestBean.setRequest_time(s);
		requestBean.setSign(MD5Util.MD5Encode((new StringBuilder(categoryid)).append(
				s).append(key).toString()));
		soapResponse.addChild(ObjBodyWriter.convertBeanToXml(requestBean,
				"request"));
		Options options = new Options();
		options.setTo(new EndpointReference(wsurl));
		options.setAction("getBusinessProjectInfo");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			OMElement result = sender.sendReceive(soapResponse);
			
			Document doc = DOMHelper.newDocument(result.toString(), "GBK");
			Element urlset = doc.getDocumentElement();
			NodeList datanodes = urlset.getElementsByTagName("data");
			for (int i = 0; i < datanodes.getLength(); i++) {
				
				Element dataelement = (Element) datanodes.item(i);
				String itemid=DOMHelper.getSubElementVauleByName(dataelement, "itemId");
				
				NodeList itemlist=dataelement.getElementsByTagName("item");
				for (int j=0;j<itemlist.getLength();j++)
				{
					
					Element itemelement=(Element) itemlist.item(j);
					String sku=DOMHelper.getSubElementVauleByName(itemelement, "sku");
					
					
					int[] syninfo=StockManager.getSynStockQty("同步团宝库存", conn, tradecontactid, sku, 0, -1);
					
					Log.info("同步团宝库存","SKU:"+sku+" 可用库存:"+String.valueOf(syninfo[1]));
					
					if (syninfo[0]==1)
					{
						Hashtable<String,String> skuinfo=getGoodsInfo(conn,sku);
						skuinfo.put("sku", sku);		
						skuinfo.put("addflag", "0");
						skuinfo.put("qty", String.valueOf(syninfo[1]));
						
						Hashtable<String,String> htwsinfo=new Hashtable<String,String>();
						htwsinfo.put("key", key);
						htwsinfo.put("namespace", namespace);
						htwsinfo.put("categoryid", categoryid);
						htwsinfo.put("wsurl", wsurl);
						htwsinfo.put("encoding", encoding);
						
						StockUtils.updateStock("同步团宝库存", htwsinfo, "0000000000000000000", itemid, skuinfo);
					}
					
				}
			}		
		}catch(JException je)
		{
			throw new JException("同步库存失败!"+je.getMessage());
		} catch (Exception axisFault) {
			throw new JException("远程服务访问失败,取项目信息失败!"+axisFault.getMessage());
		}

	}
	

	/*
	 * 返回货号customno，颜色编码colorcode，尺码sizecode
	 */	
	private Hashtable<String,String> getGoodsInfo(Connection conn,String sku) throws JException
	{
		Hashtable<String,String> htgoodsinfo=null;
		try
		{
			String sql="select a.customno,ltrim(rtrim(c.shortname)) colorcode,ltrim(rtrim(d.shortname)) sizecode "
						+" from goods a,barcode b,color c,size d "
						+" where a.goodsid=b.goodsid and b.sizeid=d.id and b.colorid=c.id "
						+" and a.measuretype=d.measuretypeid and b.custombc='"+sku+"'";
			htgoodsinfo=SQLHelper.oneRowSelect(conn, sql);
		}catch(JSQLException sqle)
		{
			throw new JException("取SKU【"+sku+"】商品信息出错!"+sqle.getMessage());
		}
		return htgoodsinfo;
	}
}
