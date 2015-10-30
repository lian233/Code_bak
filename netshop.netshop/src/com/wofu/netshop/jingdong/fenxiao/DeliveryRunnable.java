package com.wofu.netshop.jingdong.fenxiao;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.jingdong.fenxiao.Params;

/**
 * ���������߳���
 * @author bolinli
 *
 */
public class DeliveryRunnable implements Runnable{
	private String jobName="��������������ҵ";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	public DeliveryRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {
		Connection conn=null;
		try{
			conn=PoolHelper.getInstance().getConnection("shop");
			delivery(conn,getDeliveryOrders(conn));
		}catch(Exception e){
			try {
				if(conn!=null && !conn.getAutoCommit())
				conn.rollback();
				conn.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					Log.error(username,"�ر����ݿ��������  "+e1.getMessage(),null);
				}
				Log.info(username,"�����̴߳���: "+e.getMessage(),null);
			}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Log.error(username,"�ر����ݿ����ӳ���: "+e.getMessage(),null);
				}
				watch.countDown();
		}
		
	}
	
	
	private void delivery(Connection conn,Vector<Hashtable> vdeliveryorder) throws Exception{
		Log.info("���η������� Ϊ��"+vdeliveryorder.size());
		String sql = "" ;
		
		for (int i = 0; i < vdeliveryorder.size(); i++) 
		{
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			int sheetid = (Integer)hto.get("id");
			String orderID = hto.get("orderid").toString().trim();
			String postCompany = hto.get("post_company").toString().trim().toUpperCase();
			String companycode = hto.get("companycode").toString().trim().toUpperCase();
			String postNo = hto.get("post_no").toString();
			String sheetType = String.valueOf(hto.get("sheetType"));
			try 
			{
				boolean success = false ;
				
				if (companycode.equalsIgnoreCase("JDKD"))
				{
					if(!param.jdkdNeedDelivery)
						success = true ;
					else
						success = StockUtils.SOPOrderDelivery(jobName, orderID, postCompany, postNo, param.SERVER_URL, param.token, param.appKey, param.appSecret) ;
				}
				else
				{
				
					//����
					if("3".equals(sheetType))
					{
						if (param.isLBP){
							success = StockUtils.LBPOrderDelivery(jobName, orderID, postCompany, postNo, param.SERVER_URL, param.token, param.appKey, param.appSecret) ;
						}
							
						else{
									success = StockUtils.SOPOrderDelivery(jobName, orderID, postCompany, postNo, param.SERVER_URL, param.token, param.appKey, param.appSecret) ;
							}
							
							
					}
					//ת��
					else if("4".equals(sheetType))
						success = StockUtils.SOPModifyExpressInfo(jobName, orderID, postCompany, postNo, param.SERVER_URL, param.token, param.appKey, param.appSecret) ;
					else
					{
						Log.error(jobName, "δ֪��������:"+sheetType) ;
						continue ;
					}
				}
				
				if(success)
				{
					conn.setAutoCommit(false);
					


					sql = "insert into Inf_UpNotebak(SerialID,SheetType,NoteTime,HandleTime,OperType,OperData,Flag,Result,Owner) "
						+ " select SerialID,SheetType , NoteTime , getdate() , OperType , OperData , 100 , 'success' , Owner from Inf_UpNote"
						+ " where operdata = "+ sheetid+ " and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);
		
					sql = "delete from Inf_UpNote where operdata='"+ sheetid + "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);	
				}
			}
			catch (Throwable e) 
			{
				if(conn!=null && !conn.getAutoCommit()) conn.rollback();
				Log.info("���·�����Ϣʧ�ܣ��������š�" + orderID + "������ݹ�˾��" + postCompany + "������ݵ��š�" + postNo + "����������룺" + e.getMessage()) ;
			}
			
		}
		
	}
	
	private Vector<Hashtable> getDeliveryOrders(Connection conn)
	{	

		
		Vector<Hashtable> vtorders=new Vector<Hashtable>();
		String sql="";
		try
		{
			
			sql = "select a.id,a.tid,a.companycode,a.outsid from itf_delivery a,Inf_UpNote b "
				+"where a.id=b.OperData and a.sheettype=3 and a.shopid="+param.shopid;

			Vector vt=SQLHelper.multiRowSelect(conn, sql);
			for(int i=0; i<vt.size();i++)
			{
				
				
				Hashtable<String,Object> ht=new Hashtable<String,Object>();
			
				Hashtable<String, Object> hto = (Hashtable<String,Object>) vt.get(i);
				
				
				
				ht.put("sheetid", hto.get("id"));
				ht.put("orderid", hto.get("tid").toString().trim());
				String companyid=getCompnayID(hto.get("companycode").toString().trim());
				
				if (companyid.equals("")) 
				{
					Log.info("δ����������˾����:"+hto.get("companycode").toString());
					continue;
				}
				
				ht.put("post_company", companyid);
				ht.put("companycode", hto.get("companycode").toString().trim());
				
				String postno=hto.get("outsid").toString().trim();
				if(postno.indexOf("-")!=-1){
					postno=postno.substring(0,postno.indexOf("-"));
				}
				ht.put("post_no", postno);
				ht.put("sheetType", String.valueOf(hto.get("sheettype"))) ;
				vtorders.add(ht);
			}
		}
		catch(SQLException sqle)
		{
			Log.error(jobName, "��ѯ��������Ϣ����:"+sqle.getMessage());
		}
		catch(Throwable e)
		{
			e.printStackTrace() ;
		}
		return vtorders;
	}
	
	private String getCompnayID(String companycode)
	{
		String companyid="";
		
	
		String com[] = param.companycode.split(";") ;
		for(int i = 0 ; i < com.length ; i++)
		{
			String s[] = com[i].split(":") ;
			if(s[0].equals(companycode))
			{
				companyid=s[1];
				break;
			}
		}
		
		return companyid;
		
	}

}
