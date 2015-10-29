package com.wofu.intf.cainiao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.LogisticsService;
import com.taobao.api.domain.PackageItem;
import com.taobao.api.domain.PrintCheckInfo;
import com.taobao.api.domain.TradeOrderInfo;
import com.taobao.api.domain.WaybillAddress;
import com.taobao.api.domain.WaybillApplyCancelRequest;
import com.taobao.api.domain.WaybillApplyFullUpdateRequest;
import com.taobao.api.domain.WaybillApplyNewInfo;
import com.taobao.api.domain.WaybillApplyNewRequest;
import com.taobao.api.domain.WaybillApplyPrintCheckRequest;
import com.taobao.api.domain.WaybillApplyRequest;
import com.taobao.api.domain.WaybillDetailQueryRequest;
import com.taobao.api.domain.WaybillProductType;
import com.taobao.api.domain.WaybillProductTypeRequest;
import com.taobao.api.domain.WaybillServiceType;
import com.taobao.api.request.WlbWaybillICancelRequest;
import com.taobao.api.request.WlbWaybillIFullupdateRequest;
import com.taobao.api.request.WlbWaybillIGetRequest;
import com.taobao.api.request.WlbWaybillIPrintRequest;
import com.taobao.api.request.WlbWaybillIProductRequest;
import com.taobao.api.request.WlbWaybillIQuerydetailRequest;
import com.taobao.api.request.WlbWaybillISearchRequest;
import com.taobao.api.response.WlbWaybillICancelResponse;
import com.taobao.api.response.WlbWaybillIFullupdateResponse;
import com.taobao.api.response.WlbWaybillIGetResponse;
import com.taobao.api.response.WlbWaybillIPrintResponse;
import com.taobao.api.response.WlbWaybillIProductResponse;
import com.taobao.api.response.WlbWaybillIQuerydetailResponse;
import com.taobao.api.response.WlbWaybillISearchResponse;
import com.wofu.base.job.Executer;
import com.wofu.base.job.timer.TimerRunner;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

/**
 * ��������
 * @author Administrator
 *
 */
public class CaiNiao  extends Executer {

	/**
	 * @param args
	 * "cp_code": "POSTB",
    "shipping_address": {
        "address_detail": "��һ��·969��",
        "area": "�ຼ��",
        "city": "������",
        "province": "�㽭ʡ",
        "town": "��ǰ�ֵ�"
    }, 
	
	 */
	private static String jobName="ȡ�����ݵ���";
	private static String url="";
	private static String appkey="";
	private static String secret="";
	private static String sessionKey="";
	public void run(){
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		url=prop.getProperty("url");
		appkey=prop.getProperty("appkey");
		secret=prop.getProperty("secret");
		sessionKey=prop.getProperty("sessionKey");
		try {
			getWayBill();
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
	
	//��ȡ����浥
	/**
	 * ֧��������ȡ��ÿ������ȡ 10 ���浥��  
	 */
	private  void getWayBill() throws Exception{
		System.out.println("��ʼȡ�����浥");
		String sql= "select sheetid,linkman,tele,address from outstock0  where sheetid='01ISOX1304221551'";
		Vector  infsheetlist=this.getDao().multiRowSelect(sql);
		Log.info(infsheetlist.size()+"");
		for(Iterator it=infsheetlist.iterator();it.hasNext();)
		{
		Hashtable ht = (Hashtable)  it.next();
		String addresses = ht.get("address").toString();
		String sheetid = ht.get("sheetid").toString();
		String linkMan = ht.get("linkman").toString();
		String tele = ht.get("tele").toString();
		String addressesCut [] = addresses.split(" +");
		TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
		WlbWaybillIGetRequest req=new WlbWaybillIGetRequest();
		WaybillApplyNewRequest waybill_apply_new_request = new WaybillApplyNewRequest();
		WaybillAddress address = new WaybillAddress();//��ݵ�ַ��Ϣ
		address.setAddressFormat("json");

			if(addressesCut.length<4){
				Log.info("����: "+sheetid+",��ַ���淶!");
				continue;
			}
			else{
				address.setProvince(addressesCut[0]);
				address.setCity(addressesCut[1]);
				address.setArea(addressesCut[2]);//��
				address.setAddressDetail(addressesCut[3]);
				}
		//��ϵ��

		//address.setAreaCode(areaCode)
		TradeOrderInfo tradeOrderInfo = new TradeOrderInfo();
		tradeOrderInfo.setConsigneeName(linkMan);//
		tradeOrderInfo.setConsigneePhone(tele);
		tradeOrderInfo.setConsigneeAddress(address);
		tradeOrderInfo.setOrderChannelsType("TB");
		//���׶����б�
		List<String> tradeOrderList = new ArrayList<String>();
		tradeOrderList.add(sheetid);
		tradeOrderInfo.setTradeOrderList(tradeOrderList);
		//������Ʒ�б�  �����������Ʒ����
		sql ="select cast(notifyqty as int) notifyqty,b.name as title from outstockitem0 a ,Merchandise b where a.sheetid='"+sheetid+"' and a.mid=b.mid";
		Vector vtsku=this.getDao().multiRowSelect(sql);
		for (int i=0;i<vtsku.size();i++)
		{
			Hashtable htsku=(Hashtable) vtsku.get(i);
			String notifyqty = htsku.get("notifyqty").toString();
			String title = htsku.get("title").toString();
			long notifyqtylong = Long.valueOf(notifyqty).longValue();
			PackageItem item = new PackageItem();
			item.setCount(notifyqtylong);
			item.setItemName(title);
			List<PackageItem> packageList = new ArrayList<PackageItem>();
			packageList.add(item);
			tradeOrderInfo.setPackageItems(packageList);
		}
		
		tradeOrderInfo.setProductType("STANDARD_EXPRESS");//��ݷ����Ʒ���ͱ���
		tradeOrderInfo.setRealUserId(89346737L);//�浥ʹ����id
		waybill_apply_new_request.setShippingAddress(address);
		//waybill_apply_new_request.setCpId(123L);
		//waybill_apply_new_request.setSellerId(89346737L);
		waybill_apply_new_request.setCpCode("POSTB");//CP ��ݹ�˾����
		waybill_apply_new_request.setRealUserId(89346737L);
		waybill_apply_new_request.setAppKey(appkey);
		waybill_apply_new_request.setTradeOrderInfoCols(tradeOrderInfo);//��Ӧ���ݽṹʾ��JSON
		req.setWaybillApplyNewRequest(waybill_apply_new_request);
		WlbWaybillIGetResponse response = client.execute(req , sessionKey);
		Log.info(req.getWaybillApplyNewRequest());
		System.out.println(response.isSuccess());		
		if(response.isSuccess()){
		List<WaybillApplyNewInfo> waybillApplyNewInfo =response.getWaybillApplyNewCols();
		Iterator it1 = waybillApplyNewInfo.iterator();
		for(;it1.hasNext();){
			WaybillApplyNewInfo temp = (WaybillApplyNewInfo)it1.next();
			System.out.println(temp.getShortAddress());
			
			sql = "update outstock0 set deliverysheetid='"+temp.getWaybillCode()+"',addressID='"+temp.getPackageCenterCode()+"',ZoneCode='"+temp.getShortAddress()+"' where sheetid='"+sheetid+"'";
			this.getDao().execute(sql);
			sql = "update outstocknote set deliverysheetid='"+temp.getWaybillCode()+"',addressID='"+temp.getPackageCenterCode()+"',ZoneCode='"+temp.getShortAddress()+"' where sheetid='"+sheetid+"'";
			this.getDao().execute(sql);
			System.out.println("��ͷ����Ϣ: "+temp.getShortAddress()+"�浥��: "+temp.getWaybillCode()+"Ŀ�ĵر���: "+temp.getPackageCenterCode());
			
			//��ʼ��ӡ
			
			WlbWaybillIPrintRequest req2=new WlbWaybillIPrintRequest();
			WaybillApplyPrintCheckRequest waybill_apply_print_check_request = new WaybillApplyPrintCheckRequest();
			waybill_apply_print_check_request.setSellerId(2054718218L);
			//�浥������Ϣ����  һ�ο���ȷ�϶������
			List<PrintCheckInfo> infos = new ArrayList<PrintCheckInfo>();
			PrintCheckInfo printCheckInfo = new PrintCheckInfo();
			printCheckInfo.setConsigneeAddress(address);//�ջ���ַ
			printCheckInfo.setConsigneeName(linkMan);//�ջ���
			printCheckInfo.setConsigneePhone(tele);
			WaybillAddress shippingAddress = new WaybillAddress();//��ݵ�ַ��Ϣ
			shippingAddress.setAddressFormat("json");
			//address.setAreaCode(areaCode)
			shippingAddress.setProvince(addressesCut[0]);
			shippingAddress.setCity(addressesCut[1]);
			shippingAddress.setArea(addressesCut[2]);//��
			shippingAddress.setAddressDetail(addressesCut[3]);
			printCheckInfo.setShippingAddress(shippingAddress);
			printCheckInfo.setWaybillCode(temp.getWaybillCode());//��ݵ��š�
			printCheckInfo.setRealUserId(89346737L);//�ش�����
			infos.add(printCheckInfo);
			waybill_apply_print_check_request.setPrintCheckInfoCols(infos);
			waybill_apply_print_check_request.setCpCode("POSTB");//��ݹ�˾����
			req2.setWaybillApplyPrintCheckRequest(waybill_apply_print_check_request);
			System.out.println(req2.getWaybillApplyPrintCheckRequest());
			WlbWaybillIPrintResponse responsePri = client.execute(req2 , sessionKey);
			System.out.println(responsePri.getBody());
			if(responsePri.isSuccess()){
				System.out.println("���Ե��ô�ӡ�����д�ӡ......");
			}else{
				System.out.println("��ϢУ��ʧ�ܣ�������Ϣ: "+response.getSubMsg());
			}
		}
			}
				

	}

}}
