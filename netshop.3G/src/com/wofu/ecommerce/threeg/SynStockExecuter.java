package com.wofu.ecommerce.threeg;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.ecommerce.threeg.util.CommonHelper;
import com.wofu.ecommerce.threeg.util.Utility;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerJob;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class SynStockExecuter extends Executer {
	
	private String wsurl="";

	private String encoding="";

	private String agentid="";

	private String CustomerPrivateKeyPath="";

	private String tradecontactid="";

	private String dbname="";
	
	private String GGMallPublicKeyPath="";

	private static String productstockquerymethod="ProductStockQuery.ashx";


	public void execute() throws Exception {
		// TODO Auto-generated method stub
		TimerJob job=(TimerJob) this.getExecuteobj();
		Properties prop=StringUtil.getStringProperties(job.getParams());
		
		String workdir=System.getProperty("user.dir");
		workdir=StringUtil.replace(workdir,"\\", "/");
		
		wsurl=prop.getProperty("wsurl");
		encoding=prop.getProperty("encoding");
		CustomerPrivateKeyPath=workdir+"/"+prop.getProperty("CustomerPrivateKeyPath");
		GGMallPublicKeyPath=workdir+"/"+prop.getProperty("GGMallPublicKeyPath");		
		agentid=prop.getProperty("agentid");
		tradecontactid=prop.getProperty("tradecontactid");
		dbname=prop.getProperty("dbname");		
		
		Connection conn=null;
		try {		
			conn= PoolHelper.getInstance().getConnection(dbname);
			doUpdateAllStock(conn);
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
	private void doUpdateAllStock(Connection conn) throws Exception 
	{				
		String cmdcode="1004";
		
		String body=StockUtils.getQueryBody("1");
		
		String requestdata=CommonHelper.getXML(CustomerPrivateKeyPath, agentid, cmdcode, body);
			
		String s=CommonHelper.SendRequest(wsurl+productstockquerymethod, requestdata);
		
		String bodystr=s.substring(s.indexOf("<body>"), s.indexOf("</body>")+7);

		Document doc = DOMHelper.newDocument(s, encoding);
		Element msgelement = doc.getDocumentElement();
		Element bodyElement=(Element) msgelement.getElementsByTagName("body").item(0);
		Element ctrlElement=(Element) msgelement.getElementsByTagName("ctrl").item(0);		
		String messagedigest=DOMHelper.getSubElementVauleByName(ctrlElement, "md");
	
		if (!Utility.ValifyDigest(bodystr,messagedigest,GGMallPublicKeyPath))
		{
			throw new JException("签名验证失败!");
		}
		else
		{
			NodeList productlist=bodyElement.getElementsByTagName("ProductList");
			for (int i=0;i<productlist.getLength();i++)
			{
				NodeList productnodes =((Element) productlist.item(i)).getElementsByTagName("Product");
				for (int k=0;k<productnodes.getLength();k++)
				{
					Element product=(Element) productnodes.item(k);
					
					Element productmodellist=(Element) product.getElementsByTagName("ProductModelList").item(0);
					NodeList externallist=productmodellist.getElementsByTagName("ProductModel");
					for(int j=0;j<externallist.getLength();j++)
					{
						Element productmodel=(Element) externallist.item(j);
						String sku=DOMHelper.getSubElementVauleByName(productmodel, "ProductModelExternalId");					
						int stockqty=Integer.valueOf(DOMHelper.getSubElementVauleByName(productmodel, "Stock")).intValue();
						
						int qty=StockManager.getTradeContactUseableStock(conn, Integer.valueOf(tradecontactid).intValue(), sku);
						
						Log.info("同步3G库存","SKU:"+sku+" 原库存:"+String.valueOf(stockqty));
						
						
							Hashtable<String,String> skuinfo=new Hashtable<String,String>();
							skuinfo.put("sku", sku);		
							skuinfo.put("type", "1");
							skuinfo.put("stockqty", String.valueOf(qty));
							skuinfo.put("qty", String.valueOf(qty));
							skuinfo.put("quantity", String.valueOf(stockqty));
							
							Hashtable<String,String> htwsinfo=new Hashtable<String,String>();
							htwsinfo.put("wsurl", wsurl);
							htwsinfo.put("CustomerPrivateKeyPath",CustomerPrivateKeyPath);
							htwsinfo.put("GGMallPublicKeyPath", GGMallPublicKeyPath);
							htwsinfo.put("encoding", encoding);
							htwsinfo.put("agentid", agentid);
							
							StockUtils.updateStock("同步3G库存", htwsinfo, skuinfo,"0000000000000000000");
						
					}
				}
			}
		}
	}
	

	
}
