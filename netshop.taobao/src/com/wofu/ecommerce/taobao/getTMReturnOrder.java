package com.wofu.ecommerce.taobao;


import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.ReturnBill;
import com.taobao.api.domain.Trade;
import com.taobao.api.request.TmallEaiOrderRefundGoodReturnMgetRequest;
import com.taobao.api.response.TmallEaiOrderRefundGoodReturnMgetResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.util.PublicUtils;

public class getTMReturnOrder extends Thread {

	private static String jobname = "��ȡ��è�˻�������ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private static String lasttimeconfvalue=Params.username+"ȡ��è�˻���������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;
	
	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
				
				getReturnBillList(connection);
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_importing = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.taobao.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	
	/**
	 * ��ȡһ���˻��˻�����
	 * @param conn
	 * @throws Exception
	 */
	private void getReturnBillList(Connection conn)throws Exception{
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret,"json");	
				TmallEaiOrderRefundGoodReturnMgetRequest req=new TmallEaiOrderRefundGoodReturnMgetRequest();
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartTime(startdate);
				req.setEndTime(enddate);
				req.setPageNo(1L);
				req.setPageSize(40L);
				req.setUseHasNext(true);
				TmallEaiOrderRefundGoodReturnMgetResponse response = client.execute(req , Params.authcode);
				while(true)
				{
					if (response.getReturnBillList()==null || response.getReturnBillList().size()<=0)
					{				
						if (pageno==1)		
						{
							try
							{
								//��һ��֮�ڶ�ȡ�������������ҵ�ǰ����������죬��ȡ��������ʱ�����Ϊ��ǰ������
								if (this.dateformat.parse(Formatter.format(new Date(), Formatter.DATE_FORMAT)).
										compareTo(this.dateformat.parse(Formatter.format(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT),Formatter.DATE_FORMAT)))>0)
								{
									try
				                	{
										String value=Formatter.format((new Date(Formatter.parseDate(PublicUtils.getConfig(conn,lasttimeconfvalue,""),Formatter.DATE_TIME_FORMAT).getTime()+daymillis)),Formatter.DATE_FORMAT)+" 00:00:00";
										PublicUtils.setConfig(conn, lasttimeconfvalue, value);			    
				                	}catch(JException je)
				                	{
				                		Log.error(jobname, je.getMessage());
				                	}
								}
							}catch(ParseException e)
							{
								Log.error(jobname, "�����õ����ڸ�ʽ!"+e.getMessage());
							}
						}
						break;
					}
					
					
			
					for(Iterator it=response.getReturnBillList().iterator();it.hasNext();)
					{
						ReturnBill returnbill=(ReturnBill) it.next();
						
						Log.info(returnbill.getTid()+" "+returnbill.getStatus()+" "+returnbill.getModified(),Formatter.DATE_TIME_FORMAT);
						
						if (!RefundUtil.ReturnBillisCheck(conn, String.valueOf(returnbill.getTid()),String.valueOf(returnbill.getRefundId()),returnbill.getModified()))
						{
						
							//��ȡ������Ϣ
							Trade td=OrderUtils.getFullTrade(String.valueOf(returnbill.getTid()), Params.url, Params.address, Params.appsecret, Params.authcode);
							
							RefundUtil.createReturnBill(conn,returnbill,td,Params.tradecontactid);
								
						}
						if (Formatter.parseDate(returnbill.getModified(), Formatter.DATE_TIME_FORMAT).compareTo(modified)>0)
		                {
		                	modified=Formatter.parseDate(returnbill.getModified(), Formatter.DATE_TIME_FORMAT);
		                }
						
					}
					
					//�ж��Ƿ���һҳ
					if(response.getHasNext()){
						pageno++;
					}else{
						break;
					}
					
						
				}
				if (modified.compareTo(Formatter.parseDate(lasttime, Formatter.DATE_TIME_FORMAT))>0)
				{
					try
	            	{
	            		String value=Formatter.format(modified,Formatter.DATE_TIME_FORMAT);
	            		PublicUtils.setConfig(conn, lasttimeconfvalue, value);
	            	}catch(JException je)
	            	{
	            		Log.error(jobname,je.getMessage());
	            	}
				}
				//ִ�гɹ�����ѭ��
				break;
			} catch (Exception e) {
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				Thread.sleep(10000L);
				
			}
		}
	}
	
	public String toString()
	{
		return jobname + " " + (is_importing ? "[importing]" : "[waiting]");
	}
}
