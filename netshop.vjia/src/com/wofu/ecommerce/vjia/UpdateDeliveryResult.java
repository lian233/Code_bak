package com.wofu.ecommerce.vjia;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sun.font.CreatedFontTracker;

import com.wofu.common.tools.conv.DesUtil;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.Executer;


public class UpdateDeliveryResult extends Executer {
	
	private static String strkey = "dishiniV" ;
	private static String striv = "VjiaInfo" ;
	private static String SupplierId="dishini";
	private static String wsurl = "http://lmsedi.vjia.com" ;
	private static String tradecontactid="7";
	private static String dbname="shop";
	private static String ickdKey = "79F706765C92A00C88EE8711DA7D63D0" ;
	private static String company = "EMS:EMS_HTKY:��ͨ����_POST:�й�����ƽ��_SF:˳������_STO:��ͨE����_YTO:Բͨ�ٵ�" ;
	private static String comCode = "EMS:ems_HTKY:huitong_POST:pingyou_SF:shunfeng_STO:shentong_YTO:yuantong" ;
	private static String jobname="";
	private static Hashtable<String, String> companyName = new Hashtable<String, String>() ;
	private static Hashtable<String, String> companyCode = new Hashtable<String, String>() ;
	private static long monthMillis = 30 * 24 * 60 * 60 * 1000L ;
	
	//�Ƿ������ʱ����
	private static final long interval = 5 * 24 * 60 * 60 * 1000L ;
	
	public void execute() throws Exception {
		Properties prop=StringUtil.getStringProperties(this.getExecuteobj().getParams());
		
		strkey=prop.getProperty("strkey");
		striv=prop.getProperty("striv");
		SupplierId=prop.getProperty("SupplierId");
		wsurl=prop.getProperty("wsurl");
		tradecontactid=prop.getProperty("tradecontactid");
		dbname=prop.getProperty("dbname");
		ickdKey=prop.getProperty("ickdKey");
		company=prop.getProperty("company");
		comCode=prop.getProperty("comCode");
		jobname=prop.getProperty("jobname");
		
		wsurl = wsurl + "/SupplierAccept.aspx" ;

		//��ݹ�˾����Ӧ�Ľӿڲ�ѯ����
		String code[] = comCode.split("_") ;
		for(int i = 0 ; i < code.length ; i++)
		{
			String s[] = code[i].split(":") ;
			companyCode.put(s[0], s[1]) ;
		}
		
		Connection conn=PoolHelper.getInstance().getConnection(dbname);	
		Date beginDate = new Date(System.currentTimeMillis() - monthMillis) ;
		String beginTime = Formatter.format(beginDate, Formatter.DATE_TIME_FORMAT) ;
		String sql = "select ordercode,companycode,outsid,trancompanycode,tranoutsid,createtime from ecs_deliveryresult with(nolock) where status='-2' and isupdate='0' and createTime >'"+ beginTime +"' order by createtime asc" ;
		Log.info(sql) ;
		for (int k=0;k<10;)
		{
			try
			{
				Vector<Hashtable<String, String>> v = SQLHelper.multiRowSelect(conn, sql) ;
				for(int i = 0 ; i < v.size() ; i++)
				{
					Hashtable<String, String> ht = v.get(i) ;
					String orderid = ht.get("ordercode").toString() ;
					String companycode = ht.get("companycode").toString() ;
					String outsid = ht.get("outsid").toString() ;//ԭ�����ݵ���
					String trancompanycode = String.valueOf(ht.get("trancompanycode")) ;//ת����ݹ�˾����
					String tranoutsid = String.valueOf(ht.get("tranoutsid")) ;//ת����ݵ���
					String createtime = String.valueOf(ht.get("createtime")) ;//��������
					
					//����ת��
					DeliveryResult deliveryResult ;
					if(!"".equals(trancompanycode) && trancompanycode != null)
					{
						companycode = trancompanycode ;
						outsid = tranoutsid ;
					}
					//��ȡ���������Ϣ,ֻ������ǩ�ն���
					deliveryResult = getDeliveryResultFromICKD(jobname, companycode, outsid,createtime,orderid, ickdKey) ;

					//��ѯʧ�ܣ�����
					if(!deliveryResult.getQueryState())
						continue ;
					//������
					saveLog(jobname, conn, deliveryResult) ;
				}
				Log.info("��ȡ�������ͽ�����");
				//����ѭ��
				k = 10 ;
			} catch (Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("Զ������ʧ��[" + k + "], 10����Զ�����. "+ Log.getErrorMessage(e));
				e.printStackTrace() ;
				Thread.sleep(10000L);
			} finally 
			{
				try 
				{
					if (conn != null)
						conn.close();
				} catch (Exception e) 
				{
					throw new JException("�ر����ݿ�����ʧ��");
				}
			}
		}
	}
		
	/**
	 * �ϴ�������ͽ��
	 * @param orderID	������
	 * @param status	״̬����	A3:���ύ���ͽ��ʱ�� A5:���գ��ύ���ͽ��ʱ�� 
	 * @param operateTime	���͹�˾�ύ״̬ʱ��
	 * @param expressCompanyName	���͹�˾����
	 * @param dispatchNo	���͹�˾�˵���
	 * @param rejectReasonCode	����ԭ�����
	 * @param url	post�����ַ
	 */
	public static boolean updateDeliveryResult(String orderID,String status ,String operateTime,String expressCompanyName,String dispatchNo,String rejectReasonCode,String url)
	{
		boolean flag = false ;
		try
		{
			StringBuffer lcData = new StringBuffer() ;
			lcData.append("<updateinfoforsupplier>") ;
			lcData.append("<ordermessages>") ;
			lcData.append("<ordermessage>") ;
			lcData.append("<formcode>").append(orderID).append("</formcode>");
			lcData.append("<status>").append(status).append("</status>") ;
			lcData.append("<rejectreasoncode>").append(rejectReasonCode).append("</rejectreasoncode>") ;
			lcData.append("<operatetime>").append(operateTime).append("</operatetime>") ;
			lcData.append("<expresscompanyname>").append(expressCompanyName).append("</expresscompanyname>") ;
			lcData.append("<dispatchno>").append(dispatchNo).append("</dispatchno>") ;
			lcData.append("</ordermessage>") ;
			lcData.append("</ordermessages>") ;
			lcData.append("</updateinfoforsupplier>") ;
			
			StringBuffer postData = new StringBuffer() ;
			postData.append("lcdata=");
			postData.append(URLEncoder.encode(DesUtil.DesEncode(lcData.toString(),strkey,striv), "UTF-8")) ;
			postData.append("&supplierid=");
			postData.append(SupplierId);
			
			String xml = CommHelper.getResponseData(url, postData.toString()) ;
			xml = DesUtil.DesDecode(URLDecoder.decode(xml,"UTF-8"), strkey,striv) ;
			
			Document resultdoc = DOMHelper.newDocument(xml);
			Element resultelement=resultdoc.getDocumentElement();
			Element result = (Element) resultelement.getElementsByTagName("result").item(0) ;
			String resultcode = DOMHelper.getSubElementVauleByName(result, "resultcode").trim() ;
			String formcode = DOMHelper.getSubElementVauleByName(result, "formcode").trim() ;
			
			if("TRUE".equalsIgnoreCase(resultcode))
			{
				Log.info("�������ͽ���ɹ�,������:"+orderID) ;
				flag = true ;
			}
			else if("B3".equals(resultcode) || "B5".equals(resultcode))
			{
				Log.info("�������ͽ��ʧ��,������:"+orderID+",������Ϣ:"+getReasonByErroeCode(resultcode)) ;
				flag = true ;
			}
			else
			{
				Log.info("�������ͽ��ʧ��,������:"+orderID+",������Ϣ:"+getReasonByErroeCode(resultcode)) ;
				flag = false ;
			}
			
		} catch (Exception e) {
			e.printStackTrace() ;
			flag = false ;
		}
		return flag;
	}
	
	//�Ӱ�����http://api.ickd.cn��ȡ��������
	private static DeliveryResult getDeliveryResultFromICKD(String jobname,String company,String deliveryID,String createTime,String orderID,String key)
	{
		DeliveryResult deliveryResult = new DeliveryResult() ;
		ArrayList<Hashtable<String, String>> list = new ArrayList<Hashtable<String,String>>() ;
		String result = "" ;
		try 
		{
			String companyID = companyCode.get(company) ; 
			if("".equals(companyID) || companyID == null)
			{
				Log.error(jobname,"��֧�ֵĿ�ݹ�˾,��ݡ�"+company+"��,��ݵ��š�"+deliveryID+"��,������"+ createTime +"��") ;
				deliveryResult.setQueryState(false) ;
				return deliveryResult ;
			}
			//URL�����ַ http://api.ickd.cn/?com=[]&nu=[]&id=[]&type=[]
			StringBuffer sb = new StringBuffer() ;
			sb.append("http://api.ickd.cn/") ;
			sb.append("?com=").append(companyID) ;
			sb.append("&nu=").append(deliveryID) ;
			sb.append("&id=").append(key) ;
			sb.append("&type=xml") ;
			sb.append("&ord=asc") ;
			result = CommHelper.getResponseData(sb.toString(), "") ;

			//����Ƿ���������
			if("".equals(result) || result == null || result.indexOf("<?xml version=\"1.0\" encoding=\"GBK\" ?>") < 0)
			{
				deliveryResult.setQueryState(false) ;
				Log.error(jobname,"��ȡ�����Ϣ��ʱ,��ݹ�˾:"+company+",��ݵ���:"+deliveryID+",��������:"+ createTime +",����ֵ:"+result) ;
				return deliveryResult ;
			}
			
			Document doc = DOMHelper.newDocument(result, "gbk") ;
			Element response = doc.getDocumentElement() ;
			String status = DOMHelper.getSubElementVauleByName(response, "status") ;
			String errCode = DOMHelper.getSubElementVauleByName(response, "errCode") ;
			String message = DOMHelper.getSubElementVauleByName(response, "message") ;
			
			if(!"0".equals(errCode))
			{
				deliveryResult.setQueryState(false) ;
				Log.error(jobname,"��ȡʧ��,��ݹ�˾:"+company+",��ݵ���:"+deliveryID+",����ʱ��:"+ createTime +",������Ϣ:"+message+sb.toString()) ;
				return deliveryResult ;
			}
			
			Element data  = (Element) response.getElementsByTagName("data").item(0) ;
			NodeList itemList = data.getElementsByTagName("item") ;
			Element item = (Element) itemList.item(itemList.getLength()-1) ;
			String time = DOMHelper.getSubElementVauleByName(item, "time");
			String context = DOMHelper.getSubElementVauleByName(item, "context");
			
			//���ʱ���ʽ
			time = formatTime(jobname, time) ;
			
			//״̬ -2:��; -1: ���� 0: ��Ͷ 1: �˻�
			if(context.indexOf("�˻�") > -1 || context.indexOf("δ��Ͷ") > -1 || context.indexOf("ʧ��") > -1 || context.indexOf("���ɹ�") > -1)
			{
				Log.info("��ѯ�ɹ�,���δǩ��,״̬:"+status+message+",��ݹ�˾:"+company+",��ݵ���:"+deliveryID+",���ͽ��:" + time + " " + context) ;
				long deliveryInterval = System.currentTimeMillis() - Formatter.parseDate(time, Formatter.DATE_TIME_FORMAT).getTime() ;
				//����ָ��ʱ����δ�и��¼�¼,��Ϊ�˿�����������
				if(deliveryInterval < interval)
					deliveryResult.setStatus(-2) ;
				else 
					deliveryResult.setStatus(1) ;
			}
			//status:��ѯ���״̬��0|1|2|3|4��0��ʾ��ѯʧ�ܣ�1������2�����У�3��ǩ�գ�4�˻أ�
			else if(context.indexOf("ǩ��") > -1 || context.indexOf(" ��Ͷ") > -1 )
			{
				Log.info("��ѯ�ɹ�,����Ѿ�ǩ��,��ݹ�˾:"+company+",��ݵ���:"+deliveryID+",���ͽ��:" + time + " " + context) ;
				deliveryResult.setStatus(0) ;
			}
			else if(context.indexOf("��") > -1 && context.indexOf("ǩ��") > -1)
			{
				Log.info("��ѯ�ɹ�,���δǩ��,״̬:"+status+message+",��ݹ�˾:"+company+",��ݵ���:"+deliveryID+",���ͽ��:" + time + " " + context) ;
				deliveryResult.setStatus(-1) ;
			}
			else
			{
				Log.info("��ѯ�ɹ�,���δǩ��,״̬:"+status+message+",��ݹ�˾:"+company+",��ݵ���:"+deliveryID+",���ͽ��:" + time + " " + context) ;
				deliveryResult.setStatus(-2) ;
			}
			//���ټ�¼
			for(int i=0;i<itemList.getLength();i++)
			{
				Hashtable<String, String> ht = new Hashtable<String, String>() ;
				item = (Element) itemList.item(i) ;
				time = DOMHelper.getSubElementVauleByName(item, "time");
				context = DOMHelper.getSubElementVauleByName(item, "context");
				
				//��ʽ��ʱ��
				time = formatTime(jobname, time) ;
				
				ht.put("time", time) ;
				ht.put("context", context) ;
				list.add(ht) ;
			}
			deliveryResult.setDeliveryNote(list) ;
			deliveryResult.setOrdercode(orderID) ;
			deliveryResult.setQueryState(true) ;
			return deliveryResult ;
		} catch (Exception e) {
			Log.error(jobname,"��ȡʧ��,��ݹ�˾:"+company+",��ݵ���:"+deliveryID + ",��������:" + createTime +"������ֵ:"+ result +"������Ϣ:"+e.getMessage()) ;
			deliveryResult.setQueryState(false) ;
			return deliveryResult ;
		}
	}

	//�������ͽ��
	private static boolean saveLog(String jobname,Connection conn ,DeliveryResult deliveryResult )
	{
		boolean flag = false ;
		if(!deliveryResult.getQueryState())
			return flag ;
		String sql = "" ;
		try
		{
			conn.setAutoCommit(false);
			sql = "update ecs_deliveryresult set status='" + deliveryResult.getStatus() + "' where ordercode='"+ deliveryResult.getOrdercode() +"'" ;

			SQLHelper.executeSQL(conn, sql) ;
			ArrayList<Hashtable<String, String>> list = deliveryResult.getDeliveryNote() ;
			
			sql = "select orgid from ecs_deliveryresult with(nolock) where ordercode='"+deliveryResult.getOrdercode()+"'" ;
			String orgid=SQLHelper.strSelect(conn, sql) ;
			
			sql = "select top 1 serialid from ecs_deliverynote where ordercode='"+deliveryResult.getOrdercode()+"' order by serialid desc" ;
			int i = 0 ;
			String serialid = SQLHelper.strSelect(conn, sql) ;
			if("".equals(serialid) || serialid == null)
				;
			else
				i = Integer.parseInt(serialid) ;
			for( ; i < list.size() ; i++)
			{
				Hashtable<String, String> ht = list.get(i) ;
				sql = "insert into ecs_deliverynote(serialid,orgid,ordercode,proctime,note) " 
					+ "values('"+ (i+1) +"','" + orgid + "','" + deliveryResult.getOrdercode() + "','"+ ht.get("time") +"','"+ ht.get("context") +"')";
				SQLHelper.executeSQL(conn, sql) ;
			}
			conn.commit();
			conn.setAutoCommit(true);
			flag = true ;
			Log.info("���涩�����ͽ���ɹ�,������:"+deliveryResult.getOrdercode() );
		
		} catch (Exception e) {
			try 
			{
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			} 
			catch (Exception e1) 
			{
				Log.error(jobname, "�ع�����ʧ��");
			}
			flag = false ;
			Log.error(jobname, "���������ͽ��ʧ�ܣ�������Ϣ:"+e.getMessage()) ;
		}
		return flag ;
	}

	public static String getReasonByErroeCode(String errorCode)
	{
		try 
		{
			Hashtable<String, String> errors = new Hashtable<String, String>() ;
			errors.put("B0", "�Ƿ��Ķ�����") ;
			errors.put("B2", "���ܽ��в�������ǰ״̬��������") ;
			errors.put("B3", "���ܽ��в�������ǰ״̬����Ͷ") ;
			errors.put("B5", "���ܽ��в�������ǰ״̬������") ;
			errors.put("B6", "���ܽ��в�������ǰ״̬���˻������") ;
			errors.put("B7", "���ܽ��в�������ǰ״̬���������") ;
			errors.put("S1", "�Ƿ���XML��ʽ") ;
			errors.put("S2", "�Ƿ�������ǩ��") ;
			errors.put("S3", "�Ƿ���SP") ;
			errors.put("S5", "�Ƿ�������") ;
			errors.put("S6", "�������������") ;
			errors.put("S7", "�ַ������ȳ����涨����") ;
			
			return errors.get(errorCode) ;
		} catch (Exception e) {
			Log.error("vjia�ϴ����ͽ��","��ʼ��������Ϣ����������Ϣ��"+e.getMessage());
			return null;
		}
	}

	//ƥ��ʱ���ʽyyyy-MM-dd HH:mm:ss
	private static String formatTime(String jobname, String time)
	{
		String regex = "\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}" ;
		try 
		{
			Pattern pattern = Pattern.compile(regex) ;
			Matcher matcher = pattern.matcher(time) ;
			if(matcher.find())
				return matcher.group() ;
			else
				return time + ":00" ;
		} 
		catch (Exception e) 
		{
			Log.error(jobname, "��ʽ��ʱ��ʧ��,������Ϣ:" + e.getMessage()) ;
			return time ;
		}
	}
}
