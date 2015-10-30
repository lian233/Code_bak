package com.wofu.ecommerce.weidian2;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.weidian2.utils.Utils;

public class OrderDelivery extends Thread
{
	private static String jobname = "΢�궩������������ҵ";
	private boolean is_exporting = false;
	@Override
	public void run()
	{
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do
		{
			Connection connection = null;
			is_exporting = true;
			try
			{
				connection = PoolHelper.getInstance().getConnection(
						Params.dbname);
				delivery(connection);

				// modifiRemark(connection);

			} catch (Exception e)
			{
				try
				{
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1)
				{
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			}finally
			{
				is_exporting = false;
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
			while (System.currentTimeMillis() - startwaittime < (Params.waittime * 1000))
				try
			{
					sleep(1000L);
			} catch (Exception e)
			{
				Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
			}
		}while (true);
	}
	private void delivery(Connection conn) throws Exception
	{	
		String sql = "select  a.sheetid,b.tid, upper(ltrim(rtrim(b.companycode))) companycode,"
			+ "upper(ltrim(rtrim(b.outsid))) outsid from it_upnote a with(nolock), ns_delivery b with(nolock)"
			+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='"
			+ Params.tradecontactid + "' and b.iswait=0";
		//System.out.println(sql);
		Vector vdeliveryorder = SQLHelper.multiRowSelect(conn, sql);
		Log.info("����Ҫ����Ķ�����������Ϊ: " + vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++)
		{	
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String sheetid = hto.get("sheetid").toString();
			String orderid = hto.get("tid").toString(); // ������ �����ƶ����ⲿ������
			String post_company = hto.get("companycode").toString();
			String company = company_name(post_company);
			String post_no = hto.get("outsid").toString();
			// ���������˾Ϊ������Դ���
			if (post_company.trim().equals(""))
			{
				Log.warn(jobname, "��ݹ�˾Ϊ�գ�������:" + orderid + "");
				continue;
			}
			String postcompanyid = getCompanyID(post_company);
			if (postcompanyid.equals(""))
			{
				// ���������˾Ϊ������Դ���
				if (post_company.trim().equals(""))
				{
					Log.warn(jobname, "��ݹ�˾δ���ã���ݹ�˾��" + company + " ������:"
							+ orderid + "");
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"
							+ sheetid
							+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='" + sheetid
							+ "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
					continue;
				}
			}
			if ("".equals(post_no))
			{
				Log.warn(jobname, "��ݵ���δ���ã���ݹ�˾��" + company + " ������:"
						+ orderid + "");
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select O`wner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"
						+ sheetid
						+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='" + sheetid
						+ "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
				continue;
			}
			if ("".equals(postcompanyid))
			{
				Log.warn(jobname, "��ݹ�˾���δ���ã���ݹ�˾��" + company + " ������:"
						+ orderid + "");
				continue;
			}
			StringBuffer  buffer = new StringBuffer();
			buffer.append("service=deliver&");
			buffer.append("vcode="+Params.vcode+"&");
			buffer.append("order_id="+orderid+"&");
			buffer.append("express_company="+URLEncoder.encode(post_company, "UTF-8")+"&");
			buffer.append("express_id="+post_no);
			String responseOrderListData = Utils.sendByPost("http://www.royalrose.com.cn/api/Deliver/PostDeliver",buffer.toString());
			char[] rsp_cleaned = responseOrderListData.replace("\\", "").toCharArray();
			JSONObject responseproduct = new JSONObject(String.valueOf(rsp_cleaned, 1, rsp_cleaned.length-2));
			/**������**/
			if (responseproduct.getString("msg").equals("�ŵ��ѷ������򶩵�������"))
			{	
				String errdesc = "";
				errdesc = errdesc + " "
						+ responseproduct.get("code").toString() + " "
						+ responseproduct.get("msg").toString();
				Log.warn("��������ʧ��,������:[" + orderid + "],��ݹ�˾:[" + company
						+ "],��ݵ���:[" + post_no + "] ������Ϣ:" + errdesc);
				if (errdesc.indexOf("����״̬�޷�����") >= 0/*
													 * ||errdesc.indexOf("��������ʧ�ܣ����Ҷ���ʧ�ܣ�"
													 * )>0
													 */)
				{
					conn.setAutoCommit(false);

					sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
							+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
							+ " where SheetID = '"
							+ sheetid
							+ "' and SheetType = 3";
					SQLHelper.executeSQL(conn, sql);

					sql = "delete from IT_UpNote where SheetID='" + sheetid
							+ "' and sheettype=3";

					SQLHelper.executeSQL(conn, sql);
					conn.commit();
					conn.setAutoCommit(true);
				}
				continue;
			}
			/**������**/
			try
			{
				conn.setAutoCommit(false);

				sql = "insert into IT_UpNoteBak(Owner,SheetID,SheetType,Sender,Receiver,Notetime,HandleTime,Flag) "
						+ " select Owner , SheetID , SheetType , Sender , Receiver , Notetime , getdate() , 1 from IT_UpNote "
						+ " where SheetID = '"
						+ sheetid
						+ "' and SheetType = 3";
				SQLHelper.executeSQL(conn, sql);

				sql = "delete from IT_UpNote where SheetID='" + sheetid
						+ "' and sheettype=3";

				SQLHelper.executeSQL(conn, sql);
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException sqle)
			{
				if (!conn.getAutoCommit())
					try
					{
						conn.rollback();
					} catch (Exception e1)
					{
					}
				try
				{
					conn.setAutoCommit(true);
				} catch (Exception e1)
				{
				}
				throw new JSQLException(sql, sqle);
			}
			Log.info(jobname, "��������" + orderid + "�������ɹ�,��ݹ�˾��" + company
					+ "��,��ݵ��š�" + post_no + "��");
		}

	}
	private String company_name(String post_company) {
		if(post_company.equals("ems"))
			post_company="EMS";
		if(post_company.equals("HTKY"))
			post_company="��ͨ����";
		if(post_company.equals("YUNDA")||post_company.equals("yunda"))
			post_company="�ϴ�";
		if(post_company.equals("POST"))
			post_company="�й�����ƽ��";
		if(post_company.equals("SF"))
			post_company="˳������";
		if(post_company.equals("STO")||post_company.equals("sto"))
			post_company="��ͨ";
		if(post_company.equals("YTO")||post_company.equals("yto"))
			post_company="Բͨ";
		if(post_company.equals("EMS*"))
			post_company="EMSJJ";
		if(post_company.equals("POSTB"))
			post_company="��������";
		
		return post_company;
	}
	private String getCompanyID(String companycode) throws Exception
	{	
		String companyid = "";
		Object[] cys = StringUtil.split(Params.company, ":").toArray();
		System.out.println("companycode:" + companycode);
		for (int i = 0; i < cys.length; i++)
		{
			String cy = (String) cys[i];
			companyid = cy;
			break;
			
		}
		return companyid;
		
	}
	
	@Override
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
