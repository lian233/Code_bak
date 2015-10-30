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

	private static String jobname = "家居就订单发货处理作业";
	private static String tradecontactid = Params.tradecontactid ;
	private boolean is_exporting = false;

	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");
		do {
			Date nowtime = new Date();
			if(Params.startTime.getTime() <= nowtime.getTime())
			{//符合或超过指定的启动时间
				Connection connection = null;
				//Log.info("开始本次家居就订单发货任务!");
				is_exporting = true;
				try {		
					connection = PoolHelper.getInstance().getConnection(com.wofu.ecommerce.jiaju.Params.dbname);
					doDelivery(connection);		
				} catch (Exception e) {
					try {
						if (connection != null && !connection.getAutoCommit())
							connection.rollback();
					} catch (Exception e1) {
						Log.error(jobname, "回滚事务失败");
					}
					Log.error("105", jobname, Log.getErrorMessage(e));
				} finally {
					is_exporting = false;
					try {
						if (connection != null)
							connection.close();
					} catch (Exception e) {
						Log.error(jobname, "关闭数据库连接失败");
					}
				}
				System.gc();
				long startwaittime = System.currentTimeMillis();
				while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.jiaju.Params.waittime * 1000))
					try {
						sleep(1000L);
					} catch (Exception e) {
						Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
					}
			}
			else
			{//等待启动
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
			}
		} while (true);
	}
	
	//发货操作
	private void doDelivery(Connection conn) throws Exception
	{
//		String sql = "select a.sheetid, b.tid, c.distributeTid, upper(ltrim(rtrim(b.companycode))) companycode, upper(ltrim(rtrim(b.outsid))) outsid "
//		+ "from it_upnote a with(nolock), ns_delivery b with(nolock), ns_customerorder c with(nolock)"
//		+ "where a.sheettype=3 and a.sheetid=b.sheetid and a.receiver='" + tradecontactid + "' and b.iswait=0 AND c.tid = b.tid";
		
		
		//获取需要发货的订单,tidList字段:短订单号(用于发货),TidList字段:长订单号
//		String sql = "SELECT upper(ltrim(rtrim(outsid))) outsid,upper(ltrim(rtrim(companycode))) companycode, "+	//快递单号,快递公司
//		"(SELECT tid + ',' FROM ns_delivery with(nolock) WHERE outsid=B.outsid FOR XML PATH('')) AS TidList, "+	//--长订单号
//		"(SELECT ns_customerorder.distributeTid + ',' FROM ns_delivery with(nolock) LEFT join ns_customerorder with(nolock) on ns_delivery.tid = ns_customerorder.tid "+	//短订单号
//		"WHERE outsid=B.outsid FOR XML PATH('')) AS tidList "+	//--短订单号
//		"FROM it_upnote A with(nolock) inner join ns_delivery B with(nolock) on A.sheetid = B.sheetid "+
//		"LEFT join ns_customerorder C with(nolock) on B.tid = C.tid "+
//		"WHERE A.sheettype=3 and A.receiver='7' and B.iswait=0 "+
//		"GROUP BY B.outsid,companycode";
		

		Log.info("正在获取需要发货的订单列表...");
		
		//获取快递单号列表(不重复的)
		String sql = "SELECT distinct upper(ltrim(rtrim(outsid))) outsid,upper(ltrim(rtrim(companycode))) companycode FROM it_upnote with(nolock) inner join ns_delivery with(nolock) on it_upnote.sheetid = ns_delivery.sheetid LEFT join ns_customerorder with(nolock) on ns_delivery.tid = ns_customerorder.tid WHERE it_upnote.sheettype=3 and it_upnote.receiver='7' and ns_delivery.iswait=0";
		Vector vdeliveryorder=SQLHelper.multiRowSelect(conn, sql);
		Log.info("本次要处理的发货订单数(合并订单后): "+vdeliveryorder.size());
		for (int i = 0; i < vdeliveryorder.size(); i++)
		{	//获取当前快递单号的订单号列表
			Hashtable hto = (Hashtable) vdeliveryorder.get(i);
			String post_no = hto.get("outsid").toString();				//快递单号
			String post_company = hto.get("companycode").toString();	//快递公司代号
			String tmpsql = "SELECT upper(ltrim(rtrim(outsid))) outsid,upper(ltrim(rtrim(companycode))) companycode,it_upnote.sheetid,ns_delivery.tid,distributeTid "+
							"FROM it_upnote with(nolock) inner join ns_delivery with(nolock) on it_upnote.sheetid = ns_delivery.sheetid "+
							"LEFT join ns_customerorder with(nolock) on ns_delivery.tid = ns_customerorder.tid "+
							"WHERE it_upnote.sheettype=3 and it_upnote.receiver='7' and ns_delivery.iswait=0 and outsid = '" + post_no + "'";
			Vector orderlist = SQLHelper.multiRowSelect(conn, tmpsql);
			Log.info("当前快递单号[" + post_no + "]的发货商品数量: "+orderlist.size());
			//把多个订单记录拼凑成一条用逗号分割的字符串
			String tidList = "",TidList = "",sheetidList="";
			for(int idxO = 0; idxO < orderlist.size(); idxO++)
			{
				Hashtable tmpht = (Hashtable) orderlist.get(idxO);
				//sheetid
				String sheetid = tmpht.get("sheetid").toString();
				//sheetidList:内部单号
				sheetidList += (idxO == 0? "":",") + sheetid;
				//tidList:短订单号(家居就特殊情况,用户发货)
				if(!tmpht.get("distributeTid").equals("") && tmpht.get("distributeTid") != null)
					tidList += (idxO == 0? "":",") + tmpht.get("distributeTid").toString();
				else
				{
					Log.warn("sheetid:" + sheetid + " 的短订单号(用于发货)为空!");
					OrderUtils.DelDeliveryOrder(conn,sheetid);
					Log.warn("sheetid:" + sheetid + " 已执行DelDeliveryOrder操作并备份!");
				}
				//TidList:长订单号(商家自用)
				if(!tmpht.get("tid").equals("") && tmpht.get("tid") != null)
					TidList += (idxO == 0? "":",") + tmpht.get("tid").toString();
				else
				{//一般不会为空
					Log.warn("sheetid:" + sheetid + " 的长订单为空!");
					OrderUtils.DelDeliveryOrder(conn,sheetid);
					Log.warn("sheetid:" + sheetid + " 已执行DelDeliveryOrder操作并备份!");
				}
			}
			Log.info("短订单号:" + tidList.trim());
			Log.info("长订单号:" + TidList.trim());
			//短订单号
			if(tidList.equals("") || tidList == null)
			{
				Log.warn("sheetid[" + sheetidList + "] 无可发货订单,跳过!");
				continue;
			}
			//长订单号
			if(TidList.equals("") || TidList == null)
			{
				Log.warn("sheetid[" + sheetidList + "] 无可发货订单,跳过!");
				continue;
			}
			//物流公司代号为空
			if (post_company.trim().equals(""))
			{
				Log.warn(jobname, "快递公司为空！发货订单号:" + tidList + " 订单号:" + TidList);
				continue;
			}
			//获取快递公司名称
			String ExpressName = getCompanyID(post_company);
			//物流公司为空
			if (ExpressName.trim().equals(""))
			{
				Log.warn(jobname, "快递公司未配置！快递公司：" + post_company + " 发货订单号:" + tidList + " 订单号:" + TidList);
				continue;
			}
			//快递单号未配置
			if(post_no.trim().equals(""))
			{
				Log.warn(jobname, "快递单号未配置，快递公司："+post_company+" 发货订单号:" + tidList + " 订单号:" + TidList);
				continue;
			}
			//准备要发送请求的内容
			HashMap<String, String> Data = new HashMap<String, String>();
			Data.put("service", "order_send");	//方法名
			Data.put("type", "MD5");	//数字签名处理方式(固定)
			Data.put("partner_id", Params.partner_id);	//合作方ID
			Data.put("doc", "json");	//返回数据格式(固定)
			Data.put("order_id", tidList.trim());		//发货订单号
			Data.put("ship_name", ExpressName.trim());		//快递公司名称
			Data.put("ship_no", post_no.trim());	//快递运单号
			//按Key排序
			String sortStr = CommHelper.sortKey(Data);
			//加上数字签名
			String signed = CommHelper.makeSign(sortStr, Params.Partner_pwd);
			//输出发送请求内容
			Log.info("发送请求:" + signed);
			//发送请求
			String responseText = CommHelper.sendByPost(Params.url, signed);
			//输出返回的结果
			//System.out.println(responseText);
			//解析返回的Json
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
						Log.info(jobname,"处理订单【" + TidList + "】发货成功,快递公司【"+ ExpressName.trim() + "】,快递单号【" + post_no + "】");
					}
					catch(Exception err)
					{
						Log.error(jobname, "写入数据库失败,订单发货成功!  订单号:" + TidList);
					}
				}
				else
				{
					Log.warn("订单发货失败!  订单号:" + TidList + " 错误信息:" + msg);
					
					if(msg.equals("订单被锁定或者订单有正在进行中得退款。"))
					{//自动截单
						String[] tidarr = TidList.split(",");
						String[] sidarr = sheetidList.split(",");
					Log.info(tidarr.length + "   " + sidarr.length);
						for(int idx=0;idx<tidarr.length;idx++)
						{
							Log.info("正在尝试取消订单:" + tidarr[idx]);
							sql = "declare @ret int; execute  @ret = IF_CancelCustomerOrder '" + tidarr[idx] + "';select @ret ret;";
							int resultCode =SQLHelper.intSelect(conn, sql) ;
							if(resultCode == 0)
							{
								//备份删除已经截单的订单,不要再发货
								Log.info("订单未审核-取消成功,单号:"+tidarr[idx]+"");
								OrderUtils.DelDeliveryOrder(conn,sidarr[idx]);
							}else if(resultCode == 1)
							{
								//备份删除已经截单的订单,不要再发货
								Log.info("订单已审核-截单,单号:"+tidarr[idx]+"");
								OrderUtils.DelDeliveryOrder(conn,sidarr[idx]);
							}else if(resultCode ==2)
							{
								Log.warn("订单已经出库-取消失败,单号:"+tidarr[idx]+"");
								OrderUtils.DelDeliveryOrder(conn,sidarr[idx]);
							}else if(resultCode ==3)
							{
								Log.info("订单不存在或已取消-取消失败,单号:"+tidarr[idx]+"");
								OrderUtils.DelDeliveryOrder(conn,sidarr[idx]);
							}
							else
							{
								Log.warn("取消失败,单号:"+tidarr[idx]+"");
							}
						}
					}
				}
			}
			catch(Exception jsonerr)
			{//返回结果不正常
				Log.warn("解析返回的Json失败,该订单发货失败!   sheetid:[" + sheetidList +"] 短订单号(发货用):" + tidList + " 长订单号:" + TidList);
				//jsonerr.printStackTrace();
			}
		}
		Log.info("本次家居就订单发货任务完毕!");
		Thread.sleep((int)(Params.waittime / 3 * 1000 * 60));
	}

	//按快递公司代号返回快递公司名称
	private String getCompanyID(String companycode) throws Exception
	{
		String companyid="";
		Object[] cys=StringUtil.split(Params.company, ";").toArray();
		for(int i=0;i<cys.length;i++)
		{
			String cy=(String) cys[i];
			
			Object[] cs=StringUtil.split(cy, ":").toArray();
			
			String ccode=(String) cs[0];	//快递代号
			String cid=(String) cs[1];		//快递公司名称
			
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
