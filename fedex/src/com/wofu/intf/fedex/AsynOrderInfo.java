package com.wofu.intf.fedex;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.ws.Holder;
import org.example.servicefororder.ErrorType;
import org.example.servicefororder.HeaderRequest;
import org.example.servicefororder.OrderInfo;
import org.example.servicefororder.ProductDeatilType;
import org.example.servicefororder.ServiceForOrder;
import org.example.servicefororder.ServiceForOrder_Service;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynOrderInfo extends Thread {
	private static String jobname = "ͬ��������ҵ";
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");

		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				Holder<String> ask = new Holder<String>();
				Holder<String> message = new Holder<String>();
				Holder<List<ErrorType>> error = new Holder<List<ErrorType>>();
				Holder<String> orderCode = new Holder<String>();
				Holder<List<ErrorType>> referenceNo = new Holder<List<ErrorType>>();
				HeaderRequest request = new HeaderRequest();
				request.setAppKey(Params.Key);
				request.setAppToken(Params.Token);
				request.setCustomerCode(Params.customercode);
				ServiceForOrder_Service order_service  = new ServiceForOrder_Service();
				ServiceForOrder info = order_service.getServiceForOrderSOAP();
				//ȡ��Ҫ��������ݵĵ���  ������Ŷ�Ӧbarcodetranlist��sheetid
				Vector infsheetlist=FedexUtil.getInfDownNote(conn,"9902");
				//ÿһ�����ŷ���һ������
				String province="";
				String city="";
				String district="";
				String address="";
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					Hashtable ht = (Hashtable)it.next();
					String sheetid=ht.get("OperData").toString();
					Integer serialid = (Integer)ht.get("SerialID");
					Log.info("sheetid: "+sheetid);
					//skuCategory ��Ʒ����   �����û�б����ݹ�����
					String sql = "select '2' as serviceType, '1' as orderModel,'CN' as oabCounty,'FEDEX' as smCode,custompursheetid as referenceNo,isnull(c.weigh,100)/1000.0 as grossWt,"
						+"linkman as oabName,address,case when isnull(zipcode,'510000')='' then '510000' else isnull(zipcode,'510000')  end as oabPostcode,tele as oabPhone,'RMB' as currencyCode,'���֤' "
						+"as idType,certno as idNumber,2 as orderStatus,custombc as productSku,cast(notifyqty as smallint) as"
						+" opQuantity,b.price as dealPrice,case when (notifyqty*b.price)>1000 then 1000 else  notifyqty*b.price  end as transactionPrice"
						+" from outstock0 a,outstockitem0 b,barcode c where a.sheetid=b.sheetid and b.barcodeid=c.barcodeid and "
						+"a.sheetid='"+sheetid+"'";
					boolean isSuccess = false;
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					OrderInfo orderInfo = new OrderInfo();
					
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						if(i==0){
							orderInfo.getMapData(htsku);
							String addresses=(String)htsku.get("address");
							Pattern p = Pattern.compile("([\u4e00-\u9f5a]{1,}ʡ)([\u4e00-\u9f5a]{1,}��)([\u4e00-\u9f5a]{1,}��)([\\w\u4e00-\u9f5a]{1,})");
							Pattern p1 = Pattern.compile("([\u4e00-\u9f5a]{1,}��)([\u4e00-\u9f5a]{1,}��)([\u4e00-\u9f5a]{1,}��)([\\w\u4e00-\u9f5a]{1,})");
							Matcher m = p.matcher(addresses);
							Matcher m1 = p1.matcher(addresses);
							if(m.find()){
								System.out.println("ʡ: "+m.group(1)+",��: "+m.group(2)+",��: "+m.group(3)+",��ַ: "+m.group(4));
								province= m.group(1);
								city = m.group(2);
								district = m.group(3);
								address = m.group(4);
							}else if(m1.find()){
								System.out.println("ʡ: "+m1.group(1)+",��: "+m1.group(2)+",��: "+m1.group(3)+",��ַ: "+m1.group(4));
								province= m1.group(1);
								city = m1.group(2);
								district = m1.group(3);
								address = m1.group(4);
							}else if(addresses.indexOf(" ")>0){
								String[] addressTemp = addresses.split(" ");
								if(addressTemp.length<4){
									Log.info("����: "+sheetid+",��ַ���淶!");
									break;
								}else{
									province= addressTemp[0];
									city = addressTemp[1];
									district = addressTemp[2];
									address = addressTemp[3];
								}
							}else{
								Log.info("����: "+sheetid+",��ַ���淶!");
								break;
							}
							
							System.out.println("��ַ1��"+address);
							orderInfo.setOabStreetAddress1(address);
							orderInfo.setOabStateName(province.substring(0,2));
							orderInfo.setOabCity(city);
						}
						ProductDeatilType productDeatilType = new ProductDeatilType(); 
						productDeatilType.getMapData(htsku);
						orderInfo.getOrderProduct().add(productDeatilType);
					}
					if("".equals(province)) continue;
					info.createOrder(request, orderInfo, ask, message, orderCode, error, referenceNo);
					if("1".equals(ask.value)){
						isSuccess=true;
						conn.setAutoCommit(false);
						FedexUtil.bakcUpDownNote(conn,serialid);
						conn.commit();
						conn.setAutoCommit(true);
						Log.info("�ϴ������ɹ�,������: "+orderInfo.getReferenceNo());
					}else{
						isSuccess=false;
						Log.info("�ϴ������ɹ�ʧ��: "+message.value);
						List<ErrorType>  type = (List<ErrorType>)error.value;
						List<org.example.servicefororder.ErrorType> errorType = (List<org.example.servicefororder.ErrorType>)error.value;
						for(Iterator itt = errorType.iterator();itt.hasNext();){
							ErrorType typetemp =(org.example.servicefororder.ErrorType)itt.next();
							Log.info("�ϴ�����������Ϣ: "+typetemp.getErrorMessage());
							}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					if (conn != null && !conn.getAutoCommit()){
						conn.rollback();
						conn.setAutoCommit(true);
					}
						
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				e.printStackTrace();
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
}
