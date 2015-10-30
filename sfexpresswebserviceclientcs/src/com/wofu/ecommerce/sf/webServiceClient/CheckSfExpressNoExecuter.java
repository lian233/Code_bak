/**
 * ˳���ݴ򵥣����ؿ�ݵ���
 */
package com.wofu.ecommerce.sf.webServiceClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.sf.webServiceClient.GetExpressOrderId;
import com.wofu.ecommerce.sf.webServiceClient.util.webServiceclientUtils;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
public class CheckSfExpressNoExecuter extends Executer {

	private String account="";   //�ʺ�
	private String password="";    //����
	private String express_type="";    //����
	private String custid="";    //�½Ῠ��
	private static String jobName="ȡsf��ݵ���";
	@Override
	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		account=prop.getProperty("account");
		password=prop.getProperty("password");
		express_type=prop.getProperty("express_type");
		custid=prop.getProperty("custid");
		try {			 
			updateJobFlag(1);
			getsfExpressId();
			UpdateTimerJob();
			
			Log.info(jobName, "ִ����ҵ�ɹ� ["
					+ this.getExecuteobj().getActivetimes() + "] [" + this.getExecuteobj().getNotes()
					+ "] �´δ���ʱ��: "
					+ this.datetimeformat.format(this.getExecuteobj().getNextactive()));
		} catch (Exception e) {
			try {
				
				if (this.getConnection() != null && !this.getConnection().getAutoCommit())
					this.getConnection().rollback();
				
				if (this.getExtconnection() != null && !this.getExtconnection().getAutoCommit())
					this.getExtconnection().rollback();
			} catch (Exception e1) {
				Log.error(jobName,"�ع�����ʧ��");
				Log.error(jobName, e1.getMessage());
			}
			
			try{
				if (this.getExecuteobj().getSkip() == 1) {
					UpdateTimerJob();
				} else
					UpdateTimerJob(Log.getErrorMessage(e));
			}catch(Exception ex){
				Log.error(jobName,"����������Ϣʧ��");
				Log.error(jobName, ex.getMessage());
			}
			Log.error(jobName,"������Ϣ:"+Log.getErrorMessage(e));
			
			Log.error(jobName, "ִ����ҵʧ�� [" + this.getExecuteobj().getActivetimes()
					+ "] [" + this.getExecuteobj().getNotes() + "] \r\n  "
					+ Log.getErrorMessage(e));
		} finally {
			
			try
			{
				updateJobFlag(0);
			} catch (Exception e) {
				Log.error(jobName, e.getMessage());
				Log.error(jobName,"���´����־ʧ��");
				TimerRunner.modifiedErrVect(this.getExecuteobj().getId());
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
	
	//��ȡ���˵���д�뵽outstock0��outstocknote��
	private void getsfExpressId() throws Exception {
		String sql = "select top 1 a.sheetid,a.paymode,isnull(a.payfee,0) payfee,a.linkman,a.tele,a.address, isnull(b.address,'�㶫ʡ ������ ��خ�� ������ʯ����·����·��9�ţ����ֵ����ԣ�') j_address, isnull(b.LinkMan,'�ų�') j_linkman,isnull(b.MobileNo,'18939940107') j_mobile from outstock0 a,ownerdetail b  where  a.inid=b.detailID and b.chaintypeid=12 and a.delivery='sf' and a.flag>=10 and ISNULL(a.DeliverySheetID,'')=''";
		Vector result = this.getDao().multiRowSelect(sql);
		if(result.size()==0) return;
		HashMap<String,Object> map = new HashMap();
		StringBuffer sendInfo = new StringBuffer();
		ArrayList<String> arr = new ArrayList();
		sendInfo.append("<Request service='OrderService' lang='zh-CN'>")
		.append("<Head>")
		.append(account).append(",").append(password)
		.append("</Head><Body>")
		.append("<Order orderid='");
		Hashtable order =null;
		String sheetid=null;
		String paymode = null;
		String payfee = null;
		String d_contact = null;
		String d_tel = null;
		StringBuffer cargo=null;
		String[] add=null;
		String d_province=null;
		String d_city = null;
		String j_contact = null;
		String j_address = null;
		String j_province = null;
		String j_city = null;
		String j_mobile = null;
		Hashtable item = null;
		for(int i=0;i<result.size();i++){
			try{
				order = (Hashtable)result.get(i);
				sheetid = order.get("sheetid").toString();
				paymode = order.get("paymode").toString();
				payfee = order.get("payfee").toString();
				d_contact = order.get("linkman").toString();
				d_tel = order.get("tele").toString();
				if(d_tel.indexOf(" ")!=-1) {
					d_tel=d_tel.substring(0,d_tel.indexOf(" "));
	    		}
				String d_address = webServiceclientUtils.filterChar2(order.get("address").toString());
				if(d_address.indexOf(" ")>0){
					add = d_address.split(" ");
		    		d_province = add[0];
		    		d_city = add[1];
				}else{
					d_province = d_address.substring(0,d_address.indexOf("ʡ")+1);
					d_city = d_address.substring(d_address.indexOf("ʡ")+1,d_address.indexOf("��")+1);
				}
	    		j_contact = order.get("j_linkman").toString();
	    		j_address = webServiceclientUtils.filterChar2(order.get("j_address").toString());
	    		String[] j_add = j_address.split(" ");
	    		j_province=j_add[0];
	    		j_city=j_add[1];
	    		j_mobile=order.get("j_mobile").toString();
	    		StringBuffer cargo_count = new StringBuffer("'");
				//���ʽ
				sendInfo.append(sheetid).append("' ")
				.append("express_type='")
				.append(express_type).append("' ")
				.append("j_contact=' ")
				.append(j_contact).append("' ")
				.append("j_province='")
				.append(j_province).append("' ")
				.append("j_city='")
				.append(j_city).append("' ")
				.append(" j_tel='")
				.append(j_mobile).append("' ")
				.append("j_address='")
				.append(j_address).append("' ")
				.append("d_province='")
				.append(d_province).append("' ")
				.append("d_city='")
				.append(d_city).append("' ")
				.append("d_contact='")
				.append(d_contact).append("' ")
				.append("d_tel='")
				.append(d_tel).append("' ")
				.append("d_address='")
				.append(d_address).append("' ")
				.append("parcel_quantity='1' ")//��������������1������ĸ��
				//.append("parcel_quantity='2' ")
				.append("pay_method='1' ")  //�˷�ȫ���Ǽķ���
				.append("custid='").append(custid).append("'> ");//�½��ʺŸĵ�������
				String itemsql = "select notifyqty,title from outstockitem0 where sheetid='"+sheetid+"'";
				Vector  items = this.getDao().multiRowSelect(itemsql);
				for(int j=0;j<items.size();j++){
					cargo=new StringBuffer(" <Cargo ");
					item = (Hashtable)items.get(j);
					String qty = item.get("notifyqty").toString();
					cargo.append("name='").append(item.get("title").toString()).append("' ")
					.append("count='").append(qty.substring(0,qty.indexOf("."))).append("'>")
					.append("</Cargo>");
					sendInfo.append(cargo.toString());
					
				}
				if(paymode.equals("2")){
					sendInfo.append("<AddedService name='COD' value='")  //COD��ʾ��������Ľ��
					.append(payfee).append("' ")
					.append("value1='")
					.append(custid).append("'/> ");
				}
				sendInfo.append("</Order></Body></Request>");
				Log.info("����xml:��"+sendInfo.toString());
				String result1 = GetExpressOrderId.getExpressId(webServiceclientUtils.filterChar(sendInfo.toString()));
				Log.info("˳���ݷ��ؿ�ݵ��Ž��Ϊ:��"+result1);
				Document document = DOMHelper.newDocument(result1,"gbk");
				Element ele = document.getDocumentElement();
				String isSuccess = DOMHelper.getSubElementVauleByName(ele, "Head");
				Log.info("isSuccess: "+isSuccess);
				if("ok".equalsIgnoreCase(isSuccess)){
					Element response = DOMHelper.getSubElementsByName(ele,"Body",false)[0];
					Element OrderResponse = DOMHelper.getSubElementsByName(response,"OrderResponse",false)[0];
					String tid= OrderResponse.getAttribute("orderid");
					String outsid = OrderResponse.getAttribute("mailno");
					String destcode = OrderResponse.getAttribute("destcode");
					if(tid.equals(sheetid)){
						ECSDao dao=null;
						try{
							dao = (ECSDao)this.getDao();
							dao.setTransation(false);
							dao.execute(new StringBuilder().append("update outstock0 set deliverysheetid='").append(outsid)
									.append("',addressID='").append(destcode).append("' where sheetid='").append(sheetid).append("'").toString());
							dao.execute(new StringBuilder().append("update outstocknote set deliverysheetid='").append(outsid)
									.append("',addressID='").append(destcode).append("' where sheetid='").append(sheetid).append("'").toString());
							dao.commit();
							dao.setTransation(true);
							Log.info("ȡ��˳���ݵ���: "+outsid+"������Ϊ:��"+tid);
						}catch(Exception e){
							Log.error(jobName, e.getMessage());
							try{
								dao.rollback();
							}catch(Exception ex){
								Log.error(jobName, "����ع�ʧ�ܣ� "+ex.getMessage());
							}
							
						}finally{
							try{
								dao.setTransation(true);
							}catch(Exception ex){
								Log.error(jobName, "������������ʧ�ܣ� "+ex.getMessage());
							}
							dao=null;
						}
						
								
					}
				}else{
					Element error = DOMHelper.getSubElementsByName(ele, "ERROR", false)[0];
					Log.error(jobName, "�������: "+error.getAttribute("code")+" ������Ϣ: "+DOMHelper.getSubElementVauleByName(ele, "ERROR"));
				}
				sendInfo.delete(sendInfo.indexOf("<Order orderid='")+16, sendInfo.length());
			}catch(Exception e){
				Log.error(jobName, e.getMessage());
			}
			
		}
		order =null;
		sheetid=null;
		paymode = null;
		payfee = null;
		d_contact = null;
		d_tel = null;
		cargo=null;
		add=null;
		d_province=null;
		d_city = null;
		j_contact = null;
		j_address = null;
		j_province = null;
		j_city = null;
		j_mobile = null;
		item = null;
	}
		
}
