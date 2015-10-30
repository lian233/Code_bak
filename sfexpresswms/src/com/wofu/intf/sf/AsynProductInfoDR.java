package com.wofu.intf.sf;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sf.integration.warehouse.service.GetoutsideToLscService;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynProductInfoDR extends Thread {
	
	private static String jobname = "同步商品资料作业";
	private static String serviceType="SyncProductInfo";
	
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {	
			//Log.info(System.getProperty("file.encoding"));
			
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				
				//取得要处理的数据的单号  这个单号对应barcodetranlist的sheetid
				List infsheetlist=IntfUtils.getintfsheetlist(conn,Params.interfacesystem,"22012");
				//每一个单号发送一次请求
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					String sheetid=(String) it.next();
					Log.info("sheetid: "+sheetid);
		
					String sql = "select top 1 action from BarcodeTranList where sheetid='"+sheetid+"'";
					String action = SQLHelper.strSelect(conn, sql);
					conn.setAutoCommit(false);		//每张单都开启一个事物
					
					StringBuffer bizData=new StringBuffer();
					bizData.append("<wmsMerchantCatalogBatchRequest>")
					.append("<checkword>").append(Params.checkword).append("</checkword>")
					.append("<company>").append(Params.company).append("</company>")
					.append("<interface_action_code>").append("A".equalsIgnoreCase(action)?"NEW":"SAVE").append("</interface_action_code>")
					.append("<itemlist>");
					
					
					sql= new StringBuilder().append("select b.").append(Params.isBarcodeId?"barcodeid,":"custombc,")
					.append("b.goodsname,b.customno,b.deptname,b.barcodeid,b.goodsid,")
						.append("b.colorname,b.sizename,b.pkspec,b.baseprice,b.pkname,isnull(b.weigh,0) weight from BarcodeTranList a with(nolock),v_barcodeall b with(nolock)")
						.append(" where a.sheetid='")
						.append(sheetid)
						.append("' and a.barcodeid=b.barcodeid").toString();
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					Boolean isSuccess=false;
					int num=0;
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						
						String custombc=htsku.get(Params.isBarcodeId?"barcodeid":"custombc").toString().trim();
						String name=htsku.get("goodsname").toString().trim();

						String customno=htsku.get("customno").toString().trim();
						String deptname=htsku.get("deptname").toString().trim();
						String barcodeid=htsku.get("barcodeid").toString().trim();

						String goodsid=htsku.get("goodsid").toString();
						String colorname=htsku.get("colorname").toString().trim();
						String sizename=htsku.get("sizename").toString().trim();
						String pkname=htsku.get("pkname").toString().trim();
						//销售规格
						String pkspec=htsku.get("pkspec").toString().trim();
						String baseprice=htsku.get("baseprice").toString();
						String weight=htsku.get("weight").toString();

						bizData.append("<item>");
						bizData.append("<item>"+custombc+"</item>");
						/**
						 * item_category1  赠品/非赠品 
						 */
						//xml特殊字符转义
						name=name.replaceAll("&", "&amp;");
						name=name.replaceAll("\"", "&quot;");
						name=name.replaceAll("’", "&apos;");
						name=name.replaceAll("<", "&lt;");
						name=name.replaceAll(">", "&gt;");
						bizData.append("<description>"+name+"</description>");
						customno=customno.replaceAll("&", "&amp;");
						customno=customno.replaceAll("\"", "&quot;");
						customno=customno.replaceAll("’", "&apos;");
						customno=customno.replaceAll("<", "&lt;");
						customno=customno.replaceAll(">", "&gt;");
						bizData.append("<department>"+deptname+"</department>");
						bizData.append("<division>"+pkspec+"</division>");
						//bizData.append("<item_class>"+deptname+"</item_class>");	//种类	
						//先不填写,真实环境再维护bizData.append("<item_class>"+"虚拟货品"+"</item_class>");
						bizData.append("<item_color>"+colorname+"</item_color>");
						bizData.append("<item_size>").append(sizename).append("</item_size>");
						bizData.append("<storage_template>").append("条").append("</storage_template>");
						bizData.append("<x_ref_item_1>"+barcodeid+"</x_ref_item_1>");
						//这个值要顺风系统提前维护的bizData.append("<weight_1>"+weight+"</weight_1>");
						bizData.append("<quantity_um_1>").append("条").append("</quantity_um_1>");
						bizData.append("</item>");
						num++;
						if(num>=100){
							bizData.append("</itemlist></wmsMerchantCatalogBatchRequest>");
							Log.info("bizData: "+bizData.toString());
							
							String result=GetoutsideToLscService.getoutsideToLscServices(bizData.toString());
							Log.info("result:　"+result);
							Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
							Element productinforspele = productinforspdoc.getDocumentElement();
							
							String flag=DOMHelper.getSubElementVauleByName(productinforspele, "result");
							
							if (flag.equalsIgnoreCase("2")) //失败
							{	
								isSuccess=false;
								if (DOMHelper.ElementIsExists(productinforspele, "itemlist"))
								{
									Element productsele=(Element) productinforspele.getElementsByTagName("itemlist").item(0);
									
									for (int j=0;j<productsele.getElementsByTagName("item").getLength();j++)
									{
										Element productele=(Element) productsele.getElementsByTagName("item").item(j);
										String skucode=DOMHelper.getSubElementVauleByName(productele, "item");
										String errorCode = DOMHelper.getSubElementVauleByName(productele, "remark");
										Log.info(jobname,"同步商品资料失败,接口单号:"+sheetid+", SKU:"+skucode+"错误信息: "+errorCode);
									}
								}
								else
								{
									Log.info(jobname,"同步商品资料失败,接口单号:"+sheetid);
								}
								
						
								Log.error(jobname, "同步商品资料失败,接口单号:"+sheetid);
								
							}else   //同步成功，备份接口数据，写入ecs_bestlogisticsmsg表
							{
								isSuccess=true;
								if (DOMHelper.ElementIsExists(productinforspele, "itemlist"))
								{
									Element productsele=(Element) productinforspele.getElementsByTagName("itemlist").item(0);
									
									for (int j=0;j<productsele.getElementsByTagName("item").getLength();j++)
									{
										Element productele=(Element) productsele.getElementsByTagName("product").item(j);
										String skucode=DOMHelper.getSubElementVauleByName(productele, "item");
										
										Log.info(jobname,"同步商品资料成功,接口单号:"+sheetid+", SKU:"+skucode);
									}
								}
								else
								{
									Log.info(jobname,"同步商品资料成功,接口单号:"+sheetid);
								}
							}
							num=0;
							bizData.delete(bizData.indexOf("<itemlist>")+10, bizData.length());
							Log.info("删除后的数据为:　"+bizData.toString());
						}					
						}
					if(bizData.indexOf("<item>")!=-1){
						//Log.info("尾数:　");
						bizData.append("</itemlist></wmsMerchantCatalogBatchRequest>");
						Log.info("bizData: "+bizData.toString());
						String result=GetoutsideToLscService.getoutsideToLscServices(bizData.toString());
						Log.info("result:　"+result);
						Document productinforspdoc = DOMHelper.newDocument(result, Params.encoding);
						Element productinforspele = productinforspdoc.getDocumentElement();
						
						String flag=DOMHelper.getSubElementVauleByName(productinforspele, "result");
						if (flag.equalsIgnoreCase("2")) //失败
						{	
							isSuccess=false;
							if (DOMHelper.ElementIsExists(productinforspele, "itemlist"))
							{
								Element productsele=(Element) productinforspele.getElementsByTagName("itemlist").item(0);
								NodeList lists = productsele.getElementsByTagName("item");
								Log.info("size: "+lists.getLength());
								for (int j=0;j<lists.getLength();j++)
								{
									Element productele=(Element) lists.item(j);
									if(!DOMHelper.ElementIsExists(productele,"item")) continue;
									String skucode=DOMHelper.getSubElementVauleByName(productele, "item");
									String errorCode = new String(DOMHelper.getSubElementVauleByName(productele, "remark").getBytes("GBK"),"GBK");
									Log.info(jobname,"同步商品资料失败,接口单号:"+sheetid+", SKU:"+skucode+"错误信息: "+errorCode);
								}
							}
							else
							{
								Log.info(jobname,"同步商品资料失败,接口单号:"+sheetid);
							}
							
					
							Log.error(jobname, "同步商品资料失败,接口单号:"+sheetid);
						}else   //同步成功，备份接口数据，写入ecs_bestlogisticsmsg表
						{
							isSuccess=true;
							if (DOMHelper.ElementIsExists(productinforspele, "itemlist"))
							{
								Element productsele=(Element) productinforspele.getElementsByTagName("itemlist").item(0);
								
								for (int j=0;j<productsele.getElementsByTagName("item").getLength();j++)
								{
									Element productele=(Element) productsele.getElementsByTagName("item").item(j);
									String skucode=DOMHelper.getSubElementVauleByName(productele, "item");
									
									Log.info(jobname,"同步商品资料成功,接口单号:"+sheetid+", SKU:"+skucode);
								}
							}
							else
							{
								Log.info(jobname,"同步商品资料成功,接口单号:"+sheetid);
							}
						}
					}
					
					if(isSuccess){
						IntfUtils.backupIntfSheetList(conn,sheetid,Params.interfacesystem,"2201");
						
						sfUtil.recordMsg(conn,CommHelper.getMsgid(),sheetid,2201,serviceType);
					}
					
					conn.commit();
					conn.setAutoCommit(true);
				}
				
			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit())
						conn.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
}
