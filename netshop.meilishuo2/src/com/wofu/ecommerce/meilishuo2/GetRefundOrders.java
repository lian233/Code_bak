package com.wofu.ecommerce.meilishuo2;

import java.sql.Connection;
import java.util.Date;

import com.wofu.business.util.PublicUtils;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.meilishuo2.util.Utils;

public class GetRefundOrders extends Thread 
{
	private static String jobname = "��ȡ����˵�˻�����ҵ";
	
	public void run()
	{
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do
		{
			Connection connection = null;
			try
			{
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.meilishuo2.Params.dbname);
				getRefund(connection);

			} catch (Exception e)
			{
				try
				{
					e.printStackTrace();
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1)
				{
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally
			{

				try
				{
					if (connection != null)
						connection.close();
				} catch (Exception e)
				{
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000 * Params.timeInterval))
				try
				{
					sleep(1000L);
				} catch (Exception e)
				{
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}
	
	public void getRefund(Connection conn) throws Exception
	{
		String sql = "" ;
		String resultText = "" ;
		int pageIndex = 1 ;
		boolean hasNextPage = true ;
		String status = "1" ;//����״̬ 1:������ 2:�Ѵ��� ���ڵ�����̨���ͬ���ͬ��
		Log.info("��ȡ����˵�˻���ʼ!");
		for(int k=0;k<10;)
		{
			try 
			{	
				while(hasNextPage)
				{
					JSONObject object=new JSONObject(PublicUtils.getConfig(conn, "����˵Token��Ϣ2", ""));
					resultText =  Utils.sendbyget(Params.url, Params.appKey, Params.appsecret, "meilishuo.aftersales.list.get", object.optString("access_token"), new Date(), null, null, "", "", "", "");
					//Log.info("JSON: "+resultText);
					JSONObject responseObj = new JSONObject(resultText);
					Log.info("total_num: "+responseObj.getJSONObject("aftersales_list_get_response").getInt("total_num"));
					try
					{
						String errormessage = responseObj.getJSONObject("error_response").getString("message"); // ���û������try������ִ�гɹ����д��
						Log.error(jobname, "��ȡ����˵�˻�������ʧ��,������Ϣ:"+errormessage) ;
						return ;
					}catch(Exception e)
					{
						
					}
					JSONArray info = responseObj.getJSONObject("aftersales_list_get_response").getJSONArray("info");
					//Log.info("info: "+info.toString());
		            for(int i=0;i<info.length();i++)
		            {
						sql="declare @Err int ; declare @NewSheetID char(16); execute  @Err = TL_GetNewSheetID 1105, @NewSheetID output;select @NewSheetID;";			
			            String sheetid = SQLHelper.strSelect(conn, sql);
//						if (sheetid.trim().equals(""))
//							throw new JSQLException(sql,"ȡ�ӿڵ��ų���!");

//						sql=new StringBuilder().append("insert into ns_Refund(SheetID , RefundID , Oid , AlipayNo , BuyerNick , ")
//						.append("Created , Modified , OrderStatus , Status , GoodStatus , ")
//	                    .append(" HasGoodReturn ,RefundFee , Payment , Reason,Description ,")
//	                    .append(" Title , Price , Num , GoodReturnTime , Sid , ")
//	                    .append(" TotalFee , Iid , OuterIid , OuterSkuId , CompanyName ,") 
//	                    .append(" Address , ReturnAddress , InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo)")
//	                    .append(" values('").append(sheetid ).append("','").append(sheetid).append("','','','").append(info.getJSONObject(i).getString("buyer_nick")).append("','")
//	                    .append( Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)).append("','").append(Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)).append("','").append(info.getJSONObject(i).getString("status")).append("','',''")
//	                    .append("'1','").append(info.getJSONObject(i).getString("refund_fee")).append("','").append(info.getJSONObject(i).getString("total_fee")).append("','").append(info.getJSONObject(i).getString("reason")).append("','','")
//	                    .append(info.getJSONObject(i).getString("goods_title")).append("','").append((Double.parseDouble(info.getJSONObject(i).getString("total_fee"))/Double.parseDouble(info.getJSONObject(i).getString("goods_num")))).append("','").append(info.getJSONObject(i).getString("goods_num")).append("','").append(Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)).append("','").append("")
//	                    .append("','").append(info.getJSONObject(i).getString("refund_fee")).append("','").append(info.getJSONObject(i).getString("refund_id")).append("','','").append(info.getJSONObject(i).getString("oid")).append("','")
//	                    .append("'','").append(info.getJSONObject(i).getString("address")).append(/*inshopid*/"").append("','").append(info.getJSONObject(i).getString("tid")).append("'").append(info.getJSONObject(i).getString("buyer_nick")).append("','").append(info.getJSONObject(i).getString("goods_num")).append("','')").toString();

//			            sql = "insert into ns_Refund(" +
//			            		"SheetID , RefundID , Oid , AlipayNo , BuyerNick , Created , Modified , " +
//			            		"OrderStatus , Status , GoodStatus ,  HasGoodReturn ,RefundFee, Payment , " +
//			            		"Reason,Description , Title , Price , Num , GoodReturnTime , Sid ,  " +
//			            		"TotalFee , Iid , OuterIid , OuterSkuId , CompanyName , Address , " +
//			            		"ReturnAddress ,InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo) " +
//			            		"values('?','?','?','?','?','?','?'," +
//			            			   "'?','?','?',?,?,?," +
//			            			   "'?','?','?',?,?,'?','?'," +
//			            			   "?,'?','?','?','?','?'," +
//			            			   "'?','?','?','?','?','?')";
						
			            sql = "insert into ns_Refund(" +
	            		"SheetID , RefundID , Oid , AlipayNo , BuyerNick , Created , Modified , " +
	            		"OrderStatus , Status , GoodStatus ,  HasGoodReturn ,RefundFee, Payment , " +
	            		"Reason,Description , Title , Price , Num , GoodReturnTime , Sid ,  " +
	            		"TotalFee , Iid , OuterIid , OuterSkuId , CompanyName , Address , " +
	            		"ReturnAddress ,InShopID , Tid , LinkMan , LinkTele,BuyerAlipayNo) " +
	            		"values('"+sheetid+"','"+sheetid+"','','','"+info.getJSONObject(i).getString("buyer_nick")+"','"+Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)+"','"+Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)+"'," +
	            			   "'"+info.getJSONObject(i).getString("status")+"','"+info.getJSONObject(i).getString("status")+"','"+info.getJSONObject(i).getString("status")+"',"+info.getJSONObject(i).getInt("has_good_return")+","+info.getJSONObject(i).getString("total_fee")+","+info.getJSONObject(i).getString("total_fee")+"," +
	            			   "'"+info.getJSONObject(i).getString("reason")+"','"+info.getJSONObject(i).getString("reason")+"','"+info.getJSONObject(i).getString("goods_title")+"',"+info.getJSONObject(i).getString("total_fee")+","+info.getJSONObject(i).getString("goods_num")+",'"+Formatter.format(info.getJSONObject(i).getString("ctime"), Formatter.DATE_TIME_FORMAT)+"','"+info.getJSONObject(i).getString("sid")+"'," +
	            			   info.getJSONObject(i).getString("total_fee")+",'"+info.getJSONObject(i).getString("refund_id")+"','"+info.getJSONObject(i).getString("oid")+"','"+info.getJSONObject(i).getString("sid")+"','"+info.getJSONObject(i).getString("company_name")+"','"+info.getJSONObject(i).getString("address")+"'," +
	            			   "'"+info.getJSONObject(i).getString("address")+"','','"+info.getJSONObject(i).getString("tid")+"','"+info.getJSONObject(i).getString("buyer_nick")+"','','')";
			            
						Log.info("�˻���sql: "+sql) ;
						System.out.println("--");
						SQLHelper.executeSQL(conn,sql);
						
						//���뵽֪ͨ��     �˻���־Ϊ2
			            sql = "insert into it_downnote(Owner , sheetid , sheettype , sender , receiver , notetime , handletime) values('yongjun','"
			                + sheetid +"',2 , '"+Params.tradecontactid+"' , 'yongjun' , getdate() , null) ";				
						//Log.info(sql) ;
						SQLHelper.executeSQL(conn,sql);
						
						Log.info(jobname,"�ӿڵ���:"+sheetid+" �˻�������:"+info.getJSONObject(i).getString("tid")+"����������ʱ��:"+Formatter.format(info.getJSONObject(i).getString("ctime"),Formatter.DATE_TIME_FORMAT));
						conn.commit();
						conn.setAutoCommit(true);
		            }
		            hasNextPage = false;
		            k=10;
		            break;
				}
			}catch(Exception e)
			{
				if (!conn.getAutoCommit())
					try
					{
						conn.rollback();
					}
					catch (Exception e2) { }
				try
				{
					conn.setAutoCommit(true);
				}
				catch (Exception e3) { }
				throw new JSQLException("�����˻� �ӿ�����ʧ��!"+e.getMessage());
			}
		}
	}
}
