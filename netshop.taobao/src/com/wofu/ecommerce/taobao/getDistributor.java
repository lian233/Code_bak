package com.wofu.ecommerce.taobao;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Cooperation;
import com.taobao.api.request.FenxiaoCooperationGetRequest;
import com.taobao.api.response.FenxiaoCooperationGetResponse;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.business.util.PublicUtils;
public class getDistributor extends Thread {

	private static String jobname = "��ȡ�Ա�����ȡ��������ҵ";
	
	private static long daymillis=24*60*60*1000L;
	
	private String lasttimeconfvalue=Params.username+"����ȡ����������ʱ��";
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean is_importing=false;
	
	private String lasttime;


	public getDistributor() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_importing = true;
			try {												
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.taobao.Params.dbname);
				lasttime=PublicUtils.getConfig(connection,lasttimeconfvalue,Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT));
	
					
				getDistributor(connection);
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

	
	/*
	 * ��ȡһ��֮������з�����
	 */
	private void getDistributor(Connection conn) throws Exception
	{		
		long pageno=1L;
		Date modified=Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT);
		for(int k=0;k<10;)
		{
			try
			{
				TaobaoClient client=new DefaultTaobaoClient(Params.url,Params.appkey, Params.appsecret,"xml");
				FenxiaoCooperationGetRequest req=new FenxiaoCooperationGetRequest();		
				Date startdate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+1000L);
				Date enddate=new Date(Formatter.parseDate(lasttime,Formatter.DATE_TIME_FORMAT).getTime()+daymillis);
				req.setStartDate(startdate);
				req.setEndDate(enddate);
				req.setPageNo(pageno);
				req.setPageSize(40L);
				FenxiaoCooperationGetResponse response = client.execute(req , Params.authcode);
				
		
				int i=1;
			
				while(true)
				{
								
					if (response.getCooperations()==null || response.getCooperations().size()<=0)
					{				
						if (i==1)		
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
					
					
					
					for(Iterator it=response.getCooperations().iterator();it.hasNext();)
					{
						Cooperation cop=(Cooperation) it.next();
												
						Log.info(cop.getDistributorId()+" "+cop.getDistributorNick()+" "+Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT));
						//taobao.fenxiao.distributors.get  api�շ�
						String shopname=OrderUtils.getDistributorShopName(Params.url,Params.appkey,Params.appsecret,Params.authcode,cop.getDistributorNick());
						
						shopname=StringUtil.replace(shopname, "'"," ");
						
						
						String sql="select count(*) from ecs_distributor with(nolock) where distributorid="+cop.getDistributorId();
						if (SQLHelper.intSelect(conn, sql)==0)
						{
							sql="insert into ecs_distributor(distributorid,distributorname,startdate,shopname,manager,creator,operator,updator) "
								+"values("+cop.getDistributorId()+",'"+cop.getDistributorNick()+"','"
								+Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT)+"','"+shopname+"','','system','system','system')";
							SQLHelper.executeSQL(conn, sql);
						}
						else
						{
							sql="update ecs_distributor set distributorname='"+cop.getDistributorNick()+"',startdate='"+
								Formatter.format(cop.getStartDate(),Formatter.DATE_TIME_FORMAT)+"',"
								+"updatetime='"+Formatter.format(new Date(),Formatter.DATE_TIME_FORMAT)+"', "
								+"shopname='"+shopname+"' "
								+"where distributorid="+cop.getDistributorId();
							SQLHelper.executeSQL(conn, sql);
						}
						
					}
				
					if (pageno==(Double.valueOf(Math.ceil(response.getTotalResults()/40.0))).intValue()) break;
					
					pageno++;
					req.setPageNo(pageno);
					response=client.execute(req , Params.authcode);
					i=i+1;
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
			}catch (JException e) {
				
				throw e;
				
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
