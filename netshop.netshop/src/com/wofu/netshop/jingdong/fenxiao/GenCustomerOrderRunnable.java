package com.wofu.netshop.jingdong.fenxiao;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.wofu.business.fenxiao.intf.IntfUtils;
import com.wofu.business.fenxiao.order.OrderManager;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.log.Log;
import com.wofu.netshop.jingdong.fenxiao.Params;

public class GenCustomerOrderRunnable implements Runnable{
	private String jobName="�ӿڶ������ɿͻ�������ҵ";
	private CountDownLatch watch;
	private String username="";
	private Params param;
	public GenCustomerOrderRunnable(CountDownLatch watch,Params param){
		this.watch=watch;
		this.param=param;
	}
	public void run() {

		Connection connection = null;
		try {
			connection = PoolHelper.getInstance().getConnection("shop");
			List vts=IntfUtils.getDownNotes(connection, param.shopid, 1);
			boolean is_success=false;
			Log.info(username,"���ι�Ҫ����ľ����ӿڶ���������ʱ��������Ϊ: "+vts.size(),null);
			int sheetid=0;
			for (int i=0;i<vts.size();i++) {
				try{
					sheetid=(Integer)vts.get(i);
					//��ʼ����
					connection.setAutoCommit(false);
					//���ɿͻ�����
					is_success=OrderManager.GenDecOrder(connection, sheetid);
					
					if (is_success)
					{
						//���ݽӿ�����
						IntfUtils.backupDownNote(connection, sheetid,1, "success");
						//�ύ����
						connection.commit();
						connection.setAutoCommit(true);
								
						Log.info(username,"���ɿͻ������ɹ�,�ӿڵ��š�" + sheetid + "��",null);
					}
					else
					{
						try {
							if (connection != null && !connection.getAutoCommit())
								connection.rollback();
						} catch (Exception e1) {
							Log.error(username,jobName+" �ع�����ʧ��",null);
						}
						Log.info(username,"���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��",null);
					}
				}catch(Exception ex){
					try {
						if (connection != null && !connection.getAutoCommit())
							connection.rollback();
					} catch (Exception e1) {
						Log.error(username,jobName+" �ع�����ʧ��",null);
					}
					Log.error(username,jobName+" "+ex.getMessage(),null);
					Log.info(username,"���ɿͻ�����ʧ��,�ӿڵ��š�" + sheetid + "��,��ϸ��Ϣ�� "+ex.getMessage(),null);
				}
				
			}
			
		} catch (Exception e) {
			try {
				if (connection != null && !connection.getAutoCommit())
					connection.rollback();
			} catch (Exception e1) {
				Log.error(username,jobName+" �ع�����ʧ��",null);
			}
			Log.error(username,jobName+" "+Log.getErrorMessage(e),null);
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				Log.error(username,jobName+" �ر����ݿ�����ʧ��");
			}
			watch.countDown();
		}
		
	
		
	}

}
