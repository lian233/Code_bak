package com.wofu.ecommerce.vjia;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.ecommerce.vjia.Params;
import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;

public class OrderDelivery extends Thread {

	private static String jobname = "vjia��������������ҵ";
	private boolean is_exporting = false;
	private static final String tradecontactid = String.valueOf(Params.tradecontactid);
	private static final String strkey = Params.strkey ;
	private static final String striv = Params.striv ;
	private static final String passWord = Params.suppliersign ;
	private static final String userName = Params.supplierid ;
	private static final String swsSupplierID = Params.swssupplierid ;
	private static final String URI = Params.uri; ;
	private static final String wsurl = Params.wsurl ;
	private static final Hashtable<String,String> companyName = Params.htCom ;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Connection connection = null;
			is_exporting = true;
			try {
				connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.vjia.Params.dbname);
				doDelivery(connection,getDeliveryOrders(connection));		
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_exporting = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.vjia.Params.waittime * 1000))
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	
	}
	
	//����vjia����������Ϣ
	private void doDelivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception
	{
		String sql = "" ;

		Hashtable<String, String> htinfo = new Hashtable<String, String>() ;
		htinfo.put("userName", userName) ;
		htinfo.put("passWord", passWord) ;
		htinfo.put("wsurl", wsurl) ;
		htinfo.put("strkey", strkey) ;
		htinfo.put("striv", striv) ;
		htinfo.put("URI", URI) ;
		htinfo.put("swsSupplierID", swsSupplierID) ;

		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			try 
			{
				Hashtable hto = (Hashtable) vdeliveryorder.get(i);
				String sheetID = hto.get("sheetid").toString() ;
				String orderID = hto.get("orderid").toString() ;
				String expressCompanyID = hto.get("post_company").toString().trim().toUpperCase() ;
				String expressCompanyName = companyName.get(expressCompanyID);
				String dispatchNo = hto.get("post_no").toString();
				String sheetType = String.valueOf(hto.get("sheetType"));
				
				boolean success = false ;
				//����
				if("3".equals(sheetType))
					success = StockUtils.delivery(jobname, orderID, expressCompanyName, dispatchNo, htinfo) ;
				//ת��
				else if("4".equals(sheetType))
					success = StockUtils.modifyExpressInfo(jobname, orderID, expressCompanyName, dispatchNo, htinfo) ;
				else
				{
					Log.error(jobname, "δ֪��������:"+sheetType) ;
					continue ;
				}

			    //��ǰ����������Ϣ����ʧ�ܣ��������һ��
			    if (success) 
			    {
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote with(nolock)"
							+ " where SheetID  in('"+ StringUtil.replace(sheetID, ",", "','")+ "') and SheetType ="+sheetType;
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID in('"+ StringUtil.replace(sheetID, ",", "','")+ "') and sheettype="+sheetType;
					SQLHelper.executeSQL(conn, sql);
					
					//���뵽ecs_deliveryresult,ͬ�����ͽ�� ȡ��
					/*
					sql = "select count(*) from ecs_deliveryresult with(nolock) where ordercode='"+hto.get("orderid").toString()+"'";
					if(SQLHelper.intSelect(conn, sql) <= 0)
					{
						sql = "select purdate from customerorder with(nolock) where refsheetid='"+ hto.get("orderid").toString() +"'" ;
						String createTime = SQLHelper.strSelect(conn, sql) ;
						sql = "insert into ecs_deliveryresult(orgid,ordercode,companycode,outsid,trancompanycode,tranoutsid,status,isupdate,resultflag,msg,createtime,updatetime) "
		            		+ "values('25','"+ hto.get("orderid").toString() +"','"+ expressCompanyID +"','"+ dispatchNo +"','','','-2','0','0','','"+ createTime +"','"+ createTime +"')" ;
		        		SQLHelper.executeSQL(conn, sql) ;
					}
					else
					{
						sql = "update ecs_deliveryresult set companycode='"+ expressCompanyID +"',outsid='"+ dispatchNo +"' where ordercode='"+ hto.get("orderid").toString() +"'" ;
						SQLHelper.executeSQL(conn, sql) ;
					}
					*/
	            	
					conn.commit();
					conn.setAutoCommit(true);						
				}
			   
			}
			catch (Exception e) 
			{
				Log.error(jobname, "�����������,������Ϣ:" + e.getMessage());
			}
		}
		
		
		
	}
	//ȡ����Ҫ�������Ķ���������Ϣ
	private static Vector<Hashtable> getDeliveryOrders(Connection conn)
	{
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			String oldoutsid="";
			String oldcompanycode="";
			String oldsheettype="";
			String orderids="";
			String sheetids="";
			sql = "select  a.sheetid,b.tid, b.companycode,b.outsid,a.sheettype from it_upnote a with(nolock), "
				+" ns_delivery b with(nolock),deliveryref c with(nolock)"
				+ "where (a.sheettype=3 or a.sheettype=4) and a.sheetid=b.sheetid and a.receiver='"
				+ tradecontactid + "' and b.companycode=c.companycode order by b.companycode,b.outsid";
			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
			
				Hashtable<String, String> hto = (Hashtable<String,String>) vt.get(i);
				String orderid=hto.get("tid").toString();
				String outsid=hto.get("outsid").toString();
				String companycode=hto.get("companycode").toString();
			
				String sheettype=String.valueOf(hto.get("sheettype"));
				String sheetid=hto.get("sheetid").toString();
				

				
				if (!oldoutsid.equals("") && !oldoutsid.equals(outsid))
				{
					Hashtable<String,String> ht=new Hashtable<String,String>();
					ht.put("sheetid", sheetids.substring(0, sheetids.length()-1));
					ht.put("orderid", orderids.substring(0, orderids.length()-1));
					ht.put("post_company", oldcompanycode);
					ht.put("post_no", oldoutsid);
					ht.put("sheetType", oldsheettype) ;
					vtorders.add(ht);
					orderids=orderid+",";
					sheetids=sheetid+",";
					oldoutsid=outsid;
					oldcompanycode=companycode;
					oldsheettype=sheettype;
		
				}
				else
				{
					orderids=orderids+orderid+",";
					sheetids=sheetids+sheetid+",";
					oldoutsid=outsid;
					oldcompanycode=companycode;
					oldsheettype=sheettype;

				}
				
				if (i==vt.size()-1)
				{
					Hashtable<String,String> ht=new Hashtable<String,String>();
					ht.put("sheetid", sheetids.substring(0, sheetids.length()-1));
					ht.put("orderid", orderids.substring(0, orderids.length()-1));
					ht.put("post_company", oldcompanycode);
					ht.put("post_no", oldoutsid);
					ht.put("sheetType", oldsheettype) ;

					vtorders.add(ht);
				}
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobname, "��ѯ��������Ϣ����:"+sqle.getMessage());
		}		
		return vtorders ;
	}

	
	}