package com.wofu.ecommerce.groupon;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import meta.MD5Util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.groupon.domain.model.ws.DisneyRequestBean;
import com.groupon.ws.ObjBodyWriter;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class UpdateStockAll extends Thread{
	
	private static String jobname = "同步团宝库存作业";
	
	private boolean is_updating=false;
	

	public UpdateStockAll() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {		
			Connection connection = null;
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.groupon.Params.dbname);
				
				Hashtable itemht = getBusinessProjectInfo();
				for (Iterator it = itemht.keySet().iterator(); it.hasNext();) {
					String itemid = (String) it.next();
					List skulist=(List) itemht.get(itemid);
					Vector vtgoodsinfo=getGoodsInfo(connection,skulist);
					for(int i=0;i<vtgoodsinfo.size();i++)
					{						
						Hashtable htgoodsinfo=(Hashtable) vtgoodsinfo.get(i);
						if(doUpdateStock(itemid,htgoodsinfo))
						{						
							Log.info(jobname,"成功同步库存!");
						}
					}
				}				
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_updating = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.groupon.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}

	private boolean doUpdateStock(String itemid,Hashtable htgoodsinfo)
	{
		boolean update_flag=true;
		StringBuffer buffer = new StringBuffer();
		String key = Params.key;
		String time = String.valueOf(System.currentTimeMillis());
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buffer.append("<groupon>");
		buffer.append("<request>");
		buffer.append((new StringBuilder("<request_time>")).append(time)
				.append("</request_time>").toString());
		buffer.append((new StringBuilder("<sign>")).append(
				MD5Util.MD5Encode((new StringBuilder(itemid).append(time))
						.append(key).toString())).append("</sign>").toString());
		buffer.append((new StringBuilder("<itemId>")).append(itemid)
				.append("</itemId>").toString());
		buffer.append((new StringBuilder("<addFlag>")).append("0")
				.append("</addFlag>").toString());		
		buffer.append("<rule>");
		buffer.append("<ruleBean>");
		buffer.append((new StringBuilder("<color>")).append(htgoodsinfo.get("customno").toString().trim()+htgoodsinfo.get("colorcode").toString().trim())
				.append("</color>").toString());	
		buffer.append((new StringBuilder("<size>")).append(htgoodsinfo.get("sizecode").toString().trim())
				.append("</size>").toString());		
		buffer.append((new StringBuilder("<num>")).append(htgoodsinfo.get("stockqty").toString().trim())
				.append("</num>").toString());						
		buffer.append("</ruleBean>");
		buffer.append("</rule>");
		buffer.append("</request>");
		buffer.append("</groupon>");
		OMElement requestSoapMessage = ObjBodyWriter.toOMElement(buffer
				.toString(), "UTF-8");
		Options options = new Options();
		options.setTo(new EndpointReference(Params.wsurl));
		options.setAction("updateSaleRule");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			Log.info(requestSoapMessage.toString());
			OMElement result = sender.sendReceive(requestSoapMessage);
			Log.info(result.toString());
		} catch (Exception axisFault) {
			update_flag=false;
			Log.info(jobname,"同步库存失败!"+axisFault.getMessage());
		}
		return update_flag;
	}
	/*
	 * 返回参数为
	 * 参数1：itemid 项目id
	 * 参数2：List 项目对应的SKU列表
	 */
	private Hashtable getBusinessProjectInfo() {
		OMFactory soapFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = soapFactory.createOMNamespace(Params.namespace, "");
		OMElement soapResponse = soapFactory.createOMElement("groupon", omNs);
		DisneyRequestBean requestBean = new DisneyRequestBean();
		requestBean.setCategoryid(Params.categoryid);
		String s = (new StringBuilder(String
				.valueOf(System.currentTimeMillis()))).toString();
		requestBean.setRequest_time(s);
		requestBean.setSign(MD5Util.MD5Encode((new StringBuilder(
				Params.categoryid)).append(s).append(Params.key).toString()));
		soapResponse.addChild(ObjBodyWriter.convertBeanToXml(requestBean,
				"request"));
		Options options = new Options();
		options.setTo(new EndpointReference(Params.wsurl));
		options.setAction("getBusinessProjectInfo");
		options.setProperty("__CHUNKED__", Boolean.valueOf(false));
		ServiceClient sender = null;
		Hashtable<String,List> itemht=new Hashtable<String,List>();
		try {
			sender = new ServiceClient();
			sender.setOptions(options);
			OMElement result = sender.sendReceive(soapResponse);
			Document doc = DOMHelper.newDocument(result.toString(), Params.encoding);
			Element urlset = doc.getDocumentElement();
			NodeList datanodes = urlset.getElementsByTagName("data");			
			for (int i = 0; i < datanodes.getLength(); i++) {
				
				Element dataelement = (Element) datanodes.item(i);
				String itemid=dataelement.getElementsByTagName("itemId").item(0)
						.getChildNodes().item(0).getNodeValue();	
				NodeList itemlist=dataelement.getElementsByTagName("item");
				ArrayList<String> al=new ArrayList<String>();
				for(int j=0; j<itemlist.getLength();j++)
				{
					Element itemelement=(Element) itemlist.item(j);
					al.add(itemelement.getElementsByTagName("sku").item(0)
					.getChildNodes().item(0).getNodeValue());	
				}
				itemht.put(itemid, al);
			}
		} catch (Exception e) {
			Log.error(jobname, "取商品明细失败!"+e.getMessage());
		}
		return itemht;
	}

	/*返回货号，颜色编码，尺寸编码，库存数
	 * customno,colorcode,sizecode,stockqty
	 */
	private Vector getGoodsInfo(Connection conn,List skulist)
	{
		Vector<Hashtable> vinfo=new Vector<Hashtable>();
		try
		{			
			//取库存数
			for(Iterator it=skulist.iterator();it.hasNext();)
			{
				String sku=(String) it.next();
				Hashtable<String,String> htinfo=new Hashtable<String,String>();
				String sql="select b.custombc,a.customno,b.barcodeid,"
					  +"rtrim(ltrim(c.shortname)) colorcode,rtrim(ltrim(d.shortname)) sizecode "
					  +"from goods a,barcode b,color c,size d "
					  +"where a.goodsid=b.goodsid and b.colorid=c.id and b.sizeid=d.id and a.MeasureType=d.MeasureTypeid "
					  +"and b.custombc='"+sku+"'";
				htinfo.putAll(SQLHelper.oneRowSelect(conn, sql));
				String barcodeid=htinfo.get("barcodeid").toString();
				sql="select b.RationShopID from shop a,shop b,ContactShopContrast c "
					+"where c.ContactShopContrast='"+Params.tradecontactid+"' and a.id=c.shopid and a.headid=b.id";
				String dcshopid=SQLHelper.strSelect(conn, sql);
				sql="select dbo.TL_GetUseableStock('"+dcshopid+"','"+barcodeid+"',0) stockqty";
				int stockqty=SQLHelper.intSelect(conn,sql);
				htinfo.put("stockqty", String.valueOf(stockqty));
				vinfo.add(htinfo);
			}
		}
		catch(JSQLException e)
		{
			Log.error(jobname, "取商品库存信息出错:"+e.getMessage());
		}
		return vinfo;
	}
	public String toString()
	{
		return jobname + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
