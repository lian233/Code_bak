package com.wofu.ecommerce.oauthpaipai;
import java.util.HashMap;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.wofu.base.job.Executer;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.oauthpaipai.api.oauth.PaiPaiOpenApiOauth;

public class GetSingleItemExcuter extends Executer {
	private String itemId="";
	private String spid="";
	private String secretkey="";
	private String token="";
	private String orgId="";
	private String url="";
	private String uid="";
	private String encoding="";
	private String tradecontactid="";
	private final String jobName = "��ȡ����ָ������Ʒ";
	public void run(){
		
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		itemId = prop.getProperty("itemId") ;
		orgId = prop.getProperty("orgId") ;
		spid = prop.getProperty("spid") ;
		secretkey = prop.getProperty("secretkey") ;
		token = prop.getProperty("token") ;
		url = prop.getProperty("url") ;
		uid = prop.getProperty("uid") ;
		encoding = prop.getProperty("encoding") ;
		tradecontactid = prop.getProperty("tradecontactid") ;
		
		try{
			//���δ�붩��
			updateJobFlag(1);
			
			PaiPaiOpenApiOauth sdk = new PaiPaiOpenApiOauth(spid,secretkey, token, Long.valueOf(uid));
			
			sdk.setCharset(encoding);
			
			HashMap<String, Object> params = sdk.getParams("/item/getItem.xhtml");
			
			params.put("itemCode", itemId);
			params.put("sellerUin", uid);
			
			String result = sdk.invoke();
			//Log.info("result: "+result);
			
			Document itemlistdoc = DOMHelper.newDocument(result.toString(),Params.encoding);
			Element itemlisturlset = itemlistdoc.getDocumentElement();
			String errorcode = DOMHelper.getSubElementVauleByName(itemlisturlset, "errorCode");
			String errormessage = DOMHelper.getSubElementVauleByName(itemlisturlset, "errorMessage");
			
			if(!errorcode.equals("0"))
			{	
					Log.info("ȡ�������ָ������Ʒ,��ƷID: "+itemId);
			}else{
				String title = DOMHelper.getSubElementVauleByName(itemlisturlset, "itemName");
				String itemCode = DOMHelper.getSubElementVauleByName(itemlisturlset, "itemLocalCode");
				String stock = DOMHelper.getSubElementVauleByName(itemlisturlset, "stockCount");
				Log.info("ȡ����Ʒ,����:��"+itemCode);
				StockManager.stockConfig(this.getDao(), Integer.parseInt(orgId), Integer.parseInt(tradecontactid), itemId, itemCode, title, Integer.parseInt(stock));
				NodeList notes = itemlisturlset.getElementsByTagName("stock");
				for(int i=0;i<notes.getLength();i++){
					Element skuItem = (Element)notes.item(i);
					String skuId = DOMHelper.getSubElementVauleByName(skuItem, "skuId");
					String stockLocalCode = DOMHelper.getSubElementVauleByName(skuItem, "stockLocalCode");
					String itemStock = DOMHelper.getSubElementVauleByName(skuItem, "stockCount");
					Log.info("����: "+itemId+"sku: "+stockLocalCode);
					StockManager.addStockConfigSku(this.getDao(),Integer.parseInt(orgId),itemId,skuId,stockLocalCode,Integer.parseInt(itemStock));
				}
			}
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		}catch(Exception ex){
			try{
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
			}catch(Exception e){
				Log.error(jobName, ex.getMessage());
			}
				
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(ex));
		}finally {
			try
			{
				if(!this.getConnection().getAutoCommit()) this.getConnection().setAutoCommit(true);
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName,"���´����־ʧ��");
			}
			
			try {
				if (this.getConnection() != null)
					this.getConnection().close();
				if (this.getExtconnection() != null)
					this.getExtconnection().close();
				
			} catch (Exception e) {
				Log.error(jobName,"�ر����ݿ�����ʧ��");
			}
		}
		
		
	}
}
