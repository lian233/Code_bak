package com.wofu.ecommerce.taobao;

import java.sql.Connection;
import java.sql.SQLException;

import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;

/**
 * ��Ʒ��������
 * @author Administrator
 *
 */
public class ItemUtils {
	public static void bakItem(String jobName,Connection conn,int batchid,Long num_iid){
		try{
			conn.setAutoCommit(false);
			String sql=new StringBuffer().append("insert into eco_rds_itembak select * from eco_rds_item where batchid=").append(batchid)
					.append(" and num_iid=").append(num_iid).toString();
			SQLHelper.executeSQL(conn, sql);
			sql = new StringBuffer().append("delete from eco_rds_item where num_iid=").append(num_iid).append(" and batchid=").append(batchid).toString();
			SQLHelper.executeSQL(conn, sql);
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception ex){
			Log.error(jobName, ex.getMessage());
		}finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.error(jobName, "�����Զ��ύ����ʧ��");
			}
		}
		
	}
}
