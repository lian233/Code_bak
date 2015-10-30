package com.wofu.ecommerce.vjia;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.base.job.Executer;
import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.stock.StockManager;
public class GetSingleItemsEveryDayExecuter extends Executer {
	private static String jobName = "每天检查接口表v+sku";
	private  String supplierid = "";
	private  String suppliersign = "";
	private  String uri = "";
	private  String swssupplierid = "";
	private  String pageSize = "";
	private  String wsurl = "";
	private  String tradecontactid = "";
	private  String username = "";
	private  String strkey ="";
	private  String striv = "";
	private  String barcode = "";

	public void run() {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		supplierid = prop.getProperty("supplierid") ;
		suppliersign = prop.getProperty("suppliersign") ;
		uri = prop.getProperty("uri") ;
		swssupplierid = prop.getProperty("swssupplierid") ;
		pageSize = prop.getProperty("pageSize") ;
		wsurl = prop.getProperty("wsurl") ;
		username = prop.getProperty("username") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		strkey = prop.getProperty("strkey") ;
		striv = prop.getProperty("striv") ;
			try 
			{	
				updateJobFlag(1);
				
				getSingleItems();
				UpdateTimerJob();
				
				Log.info(jobName, "执行作业成功 ["
						+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
						+ "] 下次处理时间: "
						+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
				
			} 
			catch (Exception e) {
				try {
					
					if (this.getConnection() != null && !this.getConnection().getAutoCommit())
						this.getConnection().rollback();
					
					if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
						this.getExtconnection().rollback();
					if (this.getExecuteobj().getSkip() == 1) {
						UpdateTimerJob();
					} else
						UpdateTimerJob(Log.getErrorMessage(e));
					
					
				} catch (Exception e1) {
					Log.error(jobName,"回滚事务失败");
				}
				Log.error(jobName,"错误信息:"+Log.getErrorMessage(e));
				
				
				Log.error(jobName, "执行作业失败 [" + this.getExecuteobj().getActivetimes()
						+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
						+ Log.getErrorMessage(e));
				
			} finally {
				try
				{
					updateJobFlag(0);
				} catch (Exception e) {
					Log.error(jobName,"更新处理标志失败");
				}
				
				try {
					if (this.getConnection() != null)
						this.getConnection().close();
					if (this.getExtconnection() != null)
						this.getExtconnection().close();
					
				} catch (Exception e) {
					Log.error(jobName,"关闭数据库连接失败");
				}
			}
		
	}

	private void getSingleItems() throws Exception
	{
		int pageIndex = 1 ;
		int i=0;
		Log.info(username+", 开始取当天接口中的v+商品资料");
		String sql="select orgid from ecs_tradecontactorgcontrast with(nolock) where tradecontactid="+tradecontactid;
		int orgid=this.getDao().intSelect(sql);
		for(int k=0;k<5;)
		{
			try 
			{
				sql = "select b.outerskuid from ns_customerorder a ,ns_orderitem b where a.SheetID=b.SheetID and a.TradeContactID="+tradecontactid+" and convert(varchar(10),getdate(),120)=CONVERT(varchar(10),a.created,120)";
				List<String> skus = this.getDao().oneListSelect(sql);
				if(skus.size()==0) return;
				Hashtable<String, String> bodyParams = new Hashtable<String, String>() ;
				SoapHeader soapHeader = new SoapHeader() ;
				soapHeader.setUname(supplierid) ;
				soapHeader.setPassword(suppliersign) ;
				soapHeader.setUri(uri) ;
				bodyParams.put("swsSupplierID", swssupplierid) ;
				bodyParams.put("pageSize", pageSize) ;
				bodyParams.put("page", String.valueOf(pageIndex));
				SoapBody soapBody = new SoapBody() ;
				soapBody.setRequestname("GetProductInfoByBarcode") ;
				soapBody.setUri(uri) ;
				SoapServiceClient client = new SoapServiceClient() ;
				client.setUrl(wsurl + "/GetProductInfoService.asmx") ;
				client.setSoapheader(soapHeader) ;
				for(int c=0;c<skus.size();c++){
					try{
						barcode=skus.get(c);
						Log.info("barcode:　"+barcode);
						bodyParams.put("barCode", DesUtil.DesEncode(barcode, strkey, striv)) ;
						soapBody.setBodyParams(bodyParams) ;
						client.setSoapbody(soapBody) ;
						String result = client.request() ;
						Log.info("商品信息: "+ result);
						Document resultdoc=DOMHelper.newDocument(result);
					    Element resultelement=resultdoc.getDocumentElement();
					    String resultcode = DOMHelper.getSubElementVauleByName(resultelement, "resultcode").trim() ;
					    if("-1234699".indexOf(resultcode) >= 0 || "57".indexOf(resultcode) >= 0 )
					    {
						   Log.error(jobName,"获取商品信息失败，错误代码："+ resultcode+"，错误信息："+DOMHelper.getSubElementVauleByName(resultelement, "resultmessage").trim()) ;
						   break ;
						}
					    Element product=(Element) resultelement.getElementsByTagName("product").item(0);
						String sku = DesUtil.DesDecode(DOMHelper
								.getSubElementVauleByName(product, "barcode"),
								strkey, striv);
						String skuid = DesUtil.DesDecode(DOMHelper
								.getSubElementVauleByName(product, "sku"),
								strkey, striv);
						String itemid = DesUtil.DesDecode(DOMHelper
								.getSubElementVauleByName(product,
										"productcode"), strkey, striv);
						String itemcode = DesUtil.DesDecode(
								DOMHelper.getSubElementVauleByName(product,
										"developid"), strkey, striv);
						String productname = DesUtil.DesDecode(DOMHelper
								.getSubElementVauleByName(product,
										"productname"), strkey, striv);
						String qty = DesUtil.DesDecode(DOMHelper
								.getSubElementVauleByName(product, "fororder"),
								strkey, striv);

						if (qty == null || qty.equals(""))
							qty = "0";

						Log.info("货号：" + itemcode + ", SKU " + sku);

						StockManager.stockConfig(this.getDao(), orgid, Integer
								.valueOf(tradecontactid), itemid, itemcode,
								productname, 0);

						StockManager.addStockConfigSku(this.getDao(), orgid,
								itemid, skuid, sku, Integer.valueOf(qty));

					}catch(Exception ex){
						if(this.getConnection()!=null && !this.getConnection().getAutoCommit()) this.getConnection().rollback();
						Log.error(jobName, ex.getMessage());
						continue;
					}
				}
				break;
			} catch (Exception e) {

				if (++k >= 5)
					throw e;
				if(this.getConnection()!=null && !this.getConnection().getAutoCommit()) this.getConnection().rollback();
				Log.warn(jobName+" ,远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
			}
		}
	}
	
	
}

