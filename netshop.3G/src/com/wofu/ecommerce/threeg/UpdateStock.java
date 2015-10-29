package com.wofu.ecommerce.threeg;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.sql.JSQLException;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;



public class UpdateStock extends Thread{
	
	private static String jobname = "����3G�����ҵ";
	
	private boolean is_updating=false;
	

	public UpdateStock() {
		setDaemon(true);
		setName(jobname);
	}

	public void run() {
		Log.info(jobname, "����[" + jobname + "]ģ��");
		do {		
			Connection connection = null;
			is_updating = true;
			try {					
				connection = PoolHelper.getInstance().getConnection(
						com.wofu.ecommerce.threeg.Params.dbname);
				
				doUpdateStock(connection,getGoodsInfo(connection));
		
			} catch (Exception e) {
				try {
					if (connection != null && !connection.getAutoCommit())
						connection.rollback();
				} catch (Exception e1) {
					Log.error(jobname, "�ع�����ʧ��");
				}
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				is_updating = false;
				try {
					if (connection != null)
						connection.close();
				} catch (Exception e) {
					Log.error(jobname, "�ر����ݿ�����ʧ��");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (com.wofu.ecommerce.threeg.Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "ϵͳ��֧�����߲���, ��ҵ������Ӱ���������");
				}
		} while (true);
	}

	private void doUpdateStock(Connection conn,Vector vtinfo) 
		throws Exception
	{

		Hashtable<String,String> htwsinfo=new Hashtable<String,String>();
		htwsinfo.put("wsurl", Params.wsurl);
		htwsinfo.put("CustomerPrivateKeyPath", Params.CustomerPrivateKeyPath);
		htwsinfo.put("GGMallPublicKeyPath", Params.GGMallPublicKeyPath);
		htwsinfo.put("encoding", Params.encoding);
		htwsinfo.put("agentid", Params.agentid);
				
		for(int i=0;i<vtinfo.size();i++)
		{
			
			Hashtable htinfo=(Hashtable) vtinfo.get(i);
			
			String tid=htinfo.get("tid").toString();
			String sku=htinfo.get("sku").toString();
			String qty=htinfo.get("qty").toString();
	
			//�Ҳ�����Ӧ����ʱ����-1
			int quantity=StockUtils.getSkuInfo(htwsinfo, sku);
	
			if (quantity>=0)
			{
				int stockqty=quantity+Integer.valueOf(qty).intValue();
				if (stockqty<0) stockqty=0;
				
				htinfo.put("quantity", String.valueOf(quantity));
				htinfo.put("stockqty", String.valueOf(stockqty));
				htinfo.put("type", "2");				
				
				StockUtils.updateStock(jobname,htwsinfo,htinfo,tid);
				
				StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, tid, sku);												
			}else
			{
				StockManager.bakSynReduceStore(jobname, conn, Params.tradecontactid, tid, sku);
				Log.info("3G�̳��Ҳ�������Ʒ���ϻ���δ�ϼ�,SKU:"+sku);
			}
		
		}
	}
	
	/*����SKU�Ϳ������
	 * SKU qty
	 */
	private Vector getGoodsInfo(Connection conn)
	{
		Vector vtinfo=null;
		try
		{			
			String sql="select tid,sku,qty from ECO_SynReduceStore "
				+"where tradecontactid='"+Params.tradecontactid+"' "
				+"and synflag=0 and sku is not null and sku<>''";
			vtinfo=SQLHelper.multiRowSelect(conn, sql);
		}
		catch(JSQLException e)
		{
			Log.error(jobname, "ȡ��Ʒ��������Ϣ����:"+e.getMessage());
		}
		return vtinfo;
	}
	

	
	
	public String toString()
	{
		return jobname + " " + (is_updating ? "[updating]" : "[waiting]");
	}

}
