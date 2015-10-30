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
	private static String company = "EMS:EMS_HTKY:汇通快运_POST:中国邮政平邮_SF:顺丰速运_STO:申通E物流_YTO:圆通速递" ;
	private static String comCode = "EMS:ems_HTKY:huitong_POST:pingyou_SF:shunfeng_STO:shentong_YTO:yuantong" ;
	private static String jobname="";
	private static Hashtable<String, String> companyName = new Hashtable<String, String>() ;
	private static Hashtable<String, String> companyCode = new Hashtable<String, String>() ;
	private static long monthMillis = 30 * 24 * 60 * 60 * 1000L ;
	
	//是否完结快递时间间隔
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

		//快递公司所对应的接口查询代码
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
					String outsid = ht.get("outsid").toString() ;//原出库快递单号
					String trancompanycode = String.valueOf(ht.get("trancompanycode")) ;//转件快递公司代码
					String tranoutsid = String.valueOf(ht.get("tranoutsid")) ;//转件快递单号
					String createtime = String.valueOf(ht.get("createtime")) ;//创建日期
					
					//处理转件
					DeliveryResult deliveryResult ;
					if(!"".equals(trancompanycode) && trancompanycode != null)
					{
						companycode = trancompanycode ;
						outsid = tranoutsid ;
					}
					//获取快递配送信息,只返回已签收订单
					deliveryResult = getDeliveryResultFromICKD(jobname, companycode, outsid,createtime,orderid, ickdKey) ;

					//查询失败，忽略
					if(!deliveryResult.getQueryState())
						continue ;
					//保存结果
					saveLog(jobname, conn, deliveryResult) ;
				}
				Log.info("获取订单配送结果完成");
				//结束循环
				k = 10 ;
			} catch (Exception e)
			{
				if (++k >= 10)
					throw e;
				Log.warn("远程连接失败[" + k + "], 10秒后自动重试. "+ Log.getErrorMessage(e));
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
					throw new JException("关闭数据库连接失败");
				}
			}
		}
	}
		
	/**
	 * 上传快递配送结果
	 * @param orderID	订单号
	 * @param status	状态类型	A3:（提交配送结果时） A5:拒收（提交配送结果时） 
	 * @param operateTime	配送公司提交状态时间
	 * @param expressCompanyName	配送公司名称
	 * @param dispatchNo	配送公司运单号
	 * @param rejectReasonCode	拒收原因代码
	 * @param url	post请求地址
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
				Log.info("更新配送结果成功,订单号:"+orderID) ;
				flag = true ;
			}
			else if("B3".equals(resultcode) || "B5".equals(resultcode))
			{
				Log.info("更新配送结果失败,订单号:"+orderID+",错误信息:"+getReasonByErroeCode(resultcode)) ;
				flag = true ;
			}
			else
			{
				Log.info("更新配送结果失败,订单号:"+orderID+",错误信息:"+getReasonByErroeCode(resultcode)) ;
				flag = false ;
			}
			
		} catch (Exception e) {
			e.printStackTrace() ;
			flag = false ;
		}
		return flag;
	}
	
	//从爱查快递http://api.ickd.cn获取配送数据
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
				Log.error(jobname,"不支持的快递公司,快递【"+company+"】,快递单号【"+deliveryID+"】,发货【"+ createTime +"】") ;
				deliveryResult.setQueryState(false) ;
				return deliveryResult ;
			}
			//URL请求地址 http://api.ickd.cn/?com=[]&nu=[]&id=[]&type=[]
			StringBuffer sb = new StringBuffer() ;
			sb.append("http://api.ickd.cn/") ;
			sb.append("?com=").append(companyID) ;
			sb.append("&nu=").append(deliveryID) ;
			sb.append("&id=").append(key) ;
			sb.append("&type=xml") ;
			sb.append("&ord=asc") ;
			result = CommHelper.getResponseData(sb.toString(), "") ;

			//检查是否正常返回
			if("".equals(result) || result == null || result.indexOf("<?xml version=\"1.0\" encoding=\"GBK\" ?>") < 0)
			{
				deliveryResult.setQueryState(false) ;
				Log.error(jobname,"获取快递信息超时,快递公司:"+company+",快递单号:"+deliveryID+",发货日期:"+ createTime +",返回值:"+result) ;
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
				Log.error(jobname,"获取失败,快递公司:"+company+",快递单号:"+deliveryID+",发货时间:"+ createTime +",错误信息:"+message+sb.toString()) ;
				return deliveryResult ;
			}
			
			Element data  = (Element) response.getElementsByTagName("data").item(0) ;
			NodeList itemList = data.getElementsByTagName("item") ;
			Element item = (Element) itemList.item(itemList.getLength()-1) ;
			String time = DOMHelper.getSubElementVauleByName(item, "time");
			String context = DOMHelper.getSubElementVauleByName(item, "context");
			
			//检查时间格式
			time = formatTime(jobname, time) ;
			
			//状态 -2:在途 -1: 疑是 0: 妥投 1: 退回
			if(context.indexOf("退回") > -1 || context.indexOf("未妥投") > -1 || context.indexOf("失败") > -1 || context.indexOf("不成功") > -1)
			{
				Log.info("查询成功,快递未签收,状态:"+status+message+",快递公司:"+company+",快递单号:"+deliveryID+",配送结果:" + time + " " + context) ;
				long deliveryInterval = System.currentTimeMillis() - Formatter.parseDate(time, Formatter.DATE_TIME_FORMAT).getTime() ;
				//超过指定时间间隔未有更新记录,认为此快递配送已完结
				if(deliveryInterval < interval)
					deliveryResult.setStatus(-2) ;
				else 
					deliveryResult.setStatus(1) ;
			}
			//status:查询结果状态，0|1|2|3|4，0表示查询失败，1正常，2派送中，3已签收，4退回；
			else if(context.indexOf("签收") > -1 || context.indexOf(" 妥投") > -1 )
			{
				Log.info("查询成功,快递已经签收,快递公司:"+company+",快递单号:"+deliveryID+",配送结果:" + time + " " + context) ;
				deliveryResult.setStatus(0) ;
			}
			else if(context.indexOf("退") > -1 && context.indexOf("签收") > -1)
			{
				Log.info("查询成功,快递未签收,状态:"+status+message+",快递公司:"+company+",快递单号:"+deliveryID+",配送结果:" + time + " " + context) ;
				deliveryResult.setStatus(-1) ;
			}
			else
			{
				Log.info("查询成功,快递未签收,状态:"+status+message+",快递公司:"+company+",快递单号:"+deliveryID+",配送结果:" + time + " " + context) ;
				deliveryResult.setStatus(-2) ;
			}
			//跟踪记录
			for(int i=0;i<itemList.getLength();i++)
			{
				Hashtable<String, String> ht = new Hashtable<String, String>() ;
				item = (Element) itemList.item(i) ;
				time = DOMHelper.getSubElementVauleByName(item, "time");
				context = DOMHelper.getSubElementVauleByName(item, "context");
				
				//格式化时间
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
			Log.error(jobname,"获取失败,快递公司:"+company+",快递单号:"+deliveryID + ",发货日期:" + createTime +"。返回值:"+ result +"错误信息:"+e.getMessage()) ;
			deliveryResult.setQueryState(false) ;
			return deliveryResult ;
		}
	}

	//保存配送结果
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
			Log.info("保存订单配送结果成功,订单号:"+deliveryResult.getOrdercode() );
		
		} catch (Exception e) {
			try 
			{
				if (conn != null && !conn.getAutoCommit())
					conn.rollback();
			} 
			catch (Exception e1) 
			{
				Log.error(jobname, "回滚事务失败");
			}
			flag = false ;
			Log.error(jobname, "保存快递配送结果失败，错误信息:"+e.getMessage()) ;
		}
		return flag ;
	}

	public static String getReasonByErroeCode(String errorCode)
	{
		try 
		{
			Hashtable<String, String> errors = new Hashtable<String, String>() ;
			errors.put("B0", "非法的订单号") ;
			errors.put("B2", "不能进行操作，当前状态：配送中") ;
			errors.put("B3", "不能进行操作，当前状态：妥投") ;
			errors.put("B5", "不能进行操作，当前状态：拒收") ;
			errors.put("B6", "不能进行操作，当前状态：退换货入库") ;
			errors.put("B7", "不能进行操作，当前状态：拒收入库") ;
			errors.put("S1", "非法的XML格式") ;
			errors.put("S2", "非法的数字签名") ;
			errors.put("S3", "非法的SP") ;
			errors.put("S5", "非法的内容") ;
			errors.put("S6", "服务器处理错误") ;
			errors.put("S7", "字符串长度超出规定长度") ;
			
			return errors.get(errorCode) ;
		} catch (Exception e) {
			Log.error("vjia上传配送结果","初始化错误信息出错，错误信息："+e.getMessage());
			return null;
		}
	}

	//匹配时间格式yyyy-MM-dd HH:mm:ss
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
			Log.error(jobname, "格式化时间失败,错误信息:" + e.getMessage()) ;
			return time ;
		}
	}
}
