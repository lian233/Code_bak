package com.wofu.ecommerce.jiaju;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.jiaju.utils.CommHelper;
import com.wofu.ecommerce.jiaju.Params;
public class OrderDelivery extends Thread {

	private static String jobname = "�ҾӾͶ�������������ҵ";
	private static String tradecontactid = Params.tradecontactid ;
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//���ϻ򳬹�ָ��������ʱ��
				Connection connection = null;
				//Log.info("��ʼ���μҾӾͶ�����������!");
				is_exporting = true;
				try {		
					connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.jiaju.Params.dbname);
					doDelivery(connection);		
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
				while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jiaju.Params.waittime * 1000))
					try {
						sleep(1000L);
					} catch (Exception e) {
						Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
					}
			}
			else
			{//�ȴ�����
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
			}
		} while (true);
	}
	
	//��������
	private void doDelivery(Connection conn) throws Exception
	{
//		String sql = "select a.sheetid, b.tid, c.distributeTid, upper(ltrim(rtrim(b.companycode))) companycode, upper(ltrim(rtrim(b.outsid))) outsid "
//		+ "from it_upnote a with(nolock), ns_delivery b with(nolock), ns_customerorder c with(nolock)"
//		+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='" + tradecontactid + "' and b.iswait=0 AND c.tid = b.tid";
		
		
		//��ȡ��Ҫ�����Ķ���,tidList�ֶ�:�̶�����(���ڷ���),TidList�ֶ�:��������
//		String sql = "SELECT upper(ltrim(rtrim(outsid))) outsid,upper(ltrim(rtrim(companycode))) companycode, "+	//��ݵ���,��ݹ�˾
//		"(SELECT tid + ',' FROM ns_delivery with(nolock) WHERE outsid=B.outsid FOR XML PATH('')) AS TidList, "+	//--��������
//		"(SELECT ns_customerorder.distributeTid + ',' FROM ns_delivery with(nolock) LEFT join ns_customerorder with(nolock) on ns_delivery.tid = ns_customerorder.tid "+	//�̶�����
//		"WHERE outsid=B.outsid FOR XML PATH('')) AS tidList "+	//--�̶�����
//		"FROM it_upnote A with(nolock) inner join ns_delivery B with(nolock) on A.sheetid = B.sheetid "+
//		"LEFT join ns_customerorder C with(nolock) on B.tid = C.tid "+
//		"WHERE A.sheettype=3 and A.receiver='7' and B.iswait=0 "+
//		"GROUP BY B.outsid,companycode";
		

		Log.info("���ڻ�ȡ��Ҫ�����Ķ����б�...");
		
		//��ȡ��ݵ����б�(���ظ���)
		String sql = "SELECT distinct upper(ltrim(rtrim(outsid))) outsid,upper(ltrim(rtrim(companycode))) companycode FROM it_upnote with(nolock) inner join ns_delivery with(nolock) on it_upnote.sheetid = ns_delivery.sheetid LEFT join ns_customerorder with(nolock) on ns_delivery.tid = ns_customerorder.tid WHERE it_upnote.sheettype=3 and it_upnote.receiver='7' and ns_delivery.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("����Ҫ����ķ���������(�ϲ�������): "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++)
		{	//��ȡ��ǰ��ݵ��ŵĶ������б�
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String post_no = hto.get("outsid").toString();				//��ݵ���
			String post_company = hto.get("companycode").toString();	//��ݹ�˾����
			String tmpsql = "SELECT upper(ltrim(rtrim(outsid))) outsid,upper(ltrim(rtrim(companycode))) companycode,it_upnote.sheetid,ns_delivery.tid,distributeTid "+
							"FROM it_upnote with(nolock) inner join ns_delivery with(nolock) on it_upnote.sheetid = ns_delivery.sheetid "+
							"LEFT join ns_customerorder with(nolock) on ns_delivery.tid = ns_customerorder.tid "+
							"WHERE it_upnote.sheettype=3 and it_upnote.receiver='7' and ns_delivery.iswait=0 and outsid = '" + post_no + "'";
			Vector orderlist = SQLHelper.multiRowSelect(conn, tmpsql);
			Log.info("��ǰ��ݵ���[" + post_no + "]�ķ�����Ʒ����: "+orderlist.size());
			//�Ѷ��������¼ƴ�ճ�һ���ö��ŷָ���ַ���
			String tidList = "",TidList = "",sheetidList="";
			for(int idxO = 0; idxO < orderlist.size(); idxO++)
			{
				Hashtable tmpht = (Hashtable) orderlist.get(idxO);
				//sheetid
				String sheetid = tmpht.get("sheetid").toString();
				//sheetidList:�ڲ�����
				sheetidList += (idxO == 0? "":",") + sheetid;
				//tidList:�̶�����(�ҾӾ��������,�û�����)
				if(!tmpht.get("distributeTid").equals("") && tmpht.get("distributeTid") != null)
					tidList += (idxO == 0? "":",") + tmpht.get("distributeTid").toString();
				else
				{
					Log.warn("sheetid:" + sheetid + " �Ķ̶�����(���ڷ���)Ϊ��!");
					OrderUtils.DelDeliveryOrder(conn,sheetid);
					Log.warn("sheetid:" + sheetid + " ��ִ��DelDeliveryOrder����������!");
				}
				//TidList:��������(�̼�����)
				if(!tmpht.get("tid").equals("") && tmpht.get("tid") != null)
					TidList += (idxO == 0? "":",") + tmpht.get("tid").toString();
				else
				{//һ�㲻��Ϊ��
					Log.warn("sheetid:" + sheetid + " �ĳ�����Ϊ��!");
					OrderUtils.DelDeliveryOrder(conn,sheetid);
					Log.warn("sheetid:" + sheetid + " ��ִ��DelDeliveryOrder����������!");
				}
			}
			Log.info("�̶�����:" + tidList.trim());
			Log.info("��������:" + TidList.trim());
			//�̶�����
			if(tidList.equals("") || tidList == null)
			{
				Log.warn("sheetid[" + sheetidList + "] �޿ɷ�������,����!");
				continue;
			}
			//��������
			if(TidList.equals("") || TidList == null)
			{
				Log.warn("sheetid[" + sheetidList + "] �޿ɷ�������,����!");
				continue;
			}
			//������˾����Ϊ��
			if (post_company.trim().equals(""))
			{
				Log.warn(jobname, "��ݹ�˾Ϊ�գ�����������:" + tidList + " ������:" + TidList);
				continue;
			}
			//��ȡ��ݹ�˾����
			String ExpressName = getCompanyID(post_company);
			//������˾Ϊ��
			if (ExpressName.trim().equals(""))
			{
				Log.warn(jobname, "��ݹ�˾δ���ã���ݹ�˾��" + post_company + " ����������:" + tidList + " ������:" + TidList);
				continue;
			}
			//��ݵ���δ����
			if(post_no.trim().equals(""))
			{
				Log.warn(jobname, "��ݵ���δ���ã���ݹ�˾��"+post_company+" ����������:" + tidList + " ������:" + TidList);
				continue;
			}
			//׼��Ҫ�������������
			HashMap<String, String> Data = new HashMap<String, String>();
			Data.put("service", "order_send");	//������
			Data.put("type", "MD5");	//����ǩ������ʽ(�̶�)
			Data.put("partner_id", Params.partner_id);	//������ID
			Data.put("doc", "json");	//�������ݸ�ʽ(�̶�)
			Data.put("order_id", tidList.trim());		//����������
			Data.put("ship_name", ExpressName.trim());		//��ݹ�˾����
			Data.put("ship_no", post_no.trim());	//����˵���
			//��Key����
			String sortStr = CommHelper.sortKey(Data);
			//��������ǩ��
			String signed = CommHelper.makeSign(sortStr, Params.Partner_pwd);
			//���������������
			Log.info("��������:" + signed);
			//��������
			String responseText = CommHelper.sendByPost(Params.url, signed);
			//������صĽ��
			//System.out.println(responseText);
			//�������ص�Json
			try
			{
				JSONObject responseObj = new JSONObject(responseText);
				String result = responseObj.get("status").toString();
				String msg = responseObj.get("message").toString();
				if(result.equals("true"))
				{
					try
					{
						OrderUtils.DelDeliveryOrder(conn,sheetidList);
						Log.info(jobname,"��������" + TidList + "�������ɹ�,��ݹ�˾��"+ ExpressName.trim() + "��,��ݵ��š�" + post_no + "��");
					}
					catch(Exception err)
					{
						Log.error(jobname, "д�����ݿ�ʧ��,���������ɹ�!  ������:" + TidList);
					}
				}
				else
				{
					Log.warn("��������ʧ��!  ������:" + TidList + " ������Ϣ:" + msg);
					
					if(msg.equals("�������������߶��������ڽ����е��˿"))
					{//�Զ��ص�
						String[] tidarr = TidList.split(",");
						String[] sidarr = sheetidList.split(",");
					Log.info(tidarr.length + "   " + sidarr.length);
						for(int idx=0;idx<tidarr.length;idx++)
						{
							Log.info("���ڳ���ȡ������:" + tidarr[idx]);
							sql = "declare @ret int; execute  @ret = IF_CancelCustomerOrder '" + tidarr[idx] + "';select @ret ret;";
							int resultCode =SQLHelper.intSelect(conn, sql) ;
							if(resultCode == 0)
							{
								//����ɾ���Ѿ��ص��Ķ���,��Ҫ�ٷ���
								Log.info("����δ���-ȡ���ɹ�,����:"+tidarr[idx]+"");
								OrderUtils.DelDeliveryOrder(conn,sidarr[idx]);
							}else if(resultCode == 1)
							{
								//����ɾ���Ѿ��ص��Ķ���,��Ҫ�ٷ���
								Log.info("���������-�ص�,����:"+tidarr[idx]+"");
								OrderUtils.DelDeliveryOrder(conn,sidarr[idx]);
							}else if(resultCode ==2)
							{
								Log.warn("�����Ѿ�����-ȡ��ʧ��,����:"+tidarr[idx]+"");
								OrderUtils.DelDeliveryOrder(conn,sidarr[idx]);
							}else if(resultCode ==3)
							{
								Log.info("���������ڻ���ȡ��-ȡ��ʧ��,����:"+tidarr[idx]+"");
								OrderUtils.DelDeliveryOrder(conn,sidarr[idx]);
							}
							else
							{
								Log.warn("ȡ��ʧ��,����:"+tidarr[idx]+"");
							}
						}
					}
				}
			}
			catch(Exception jsonerr)
			{//���ؽ��������
				Log.warn("�������ص�Jsonʧ��,�ö�������ʧ��!   sheetid:[" + sheetidList +"] �̶�����(������):" + tidList + " ��������:" + TidList);
				//jsonerr.printStackTrace();
			}
		}
		Log.info("���μҾӾͶ��������������!");
		Thread.sleep((int)(Params.waittime / 3 * 1000 * 60));
	}

	//����ݹ�˾���ŷ��ؿ�ݹ�˾����
	private String getCompanyID(String companycode) throws Exception
	{
		String companyid="";
		Object[] cys=StringUtil.split(Params.company, ";").toArray();
		for(int i=0;i<cys.length;i++)
		{
			String cy=(String) cys[i];
			
			Object[] cs=StringUtil.split(cy, ":").toArray();
			
			String ccode=(String) cs[0];	//��ݴ���
			String cid=(String) cs[1];		//��ݹ�˾����
			
			if(ccode.toUpperCase().equals(companycode))
			{
				companyid=cid;
				break;
			}
		}
		return companyid;
	}
	
	public String convert(String utfString)
	{
		StringBuilder sb = new StringBuilder();
		int i = -1;
		int pos = 0;
		
		while((i=utfString.indexOf("\\u", pos)) != -1){
			sb.append(utfString.substring(pos, i));
			if(i+5 < utfString.length()){
				pos = i+6;
				sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
			}
		}
		return sb.toString();
	}
	
	public String toString()
	{
		return jobname + " " + (is_exporting ? "[exporting]" : "[waiting]");
	}
}
