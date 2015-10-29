package com.wofu.intf.fedex;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.ws.Holder;

import org.example.serviceforproduct.ErrorType;
import org.example.serviceforproduct.HeaderRequest;
import org.example.serviceforproduct.ProductInfo;
import org.example.serviceforproduct.ServiceForProduct;
import org.example.serviceforproduct.ServiceForProduct_Service;
import com.wofu.business.intf.IntfUtils;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
public class AsynProductInfo extends Thread {
	private static String jobname = "同步商品资料作业";
	public void run() {
		Log.info(jobname, "启动[" + jobname + "]模块");

		do {		
			Connection conn = null;
			try {					
				conn = PoolHelper.getInstance().getConnection(Params.dbname);	
				Holder ask = new Holder();
				Holder message = new Holder();
				Holder error = new Holder();
				HeaderRequest request = new HeaderRequest();
				request.setAppKey(Params.Key);
				request.setAppToken(Params.Token);
				request.setCustomerCode(Params.customercode);
				ServiceForProduct_Service service  = new ServiceForProduct_Service();
				ServiceForProduct info = service.getServiceForProductSOAP();
				//取得要处理的数据的单号  这个单号对应barcodetranlist的sheetid
				Vector infsheetlist=FedexUtil.getInfDownNote(conn,"9901");
				//每一个单号发送一次请求
				for(Iterator it=infsheetlist.iterator();it.hasNext();)
				{
					Hashtable ht = (Hashtable)it.next();
					String sheetid=ht.get("OperData").toString();
					Integer serialid = (Integer)ht.get("SerialID");
					Log.info("sheetid: "+sheetid);
					//skuCategory 产品分类   这个还没有表数据关联的
					String sql = " select CustomBC skuNo,g.Name skuName, g.Name skuEnName,rtrim(d.skuCategory) as skuCategory,g.customno,g.deptid, isnull(g.postTaxNo,'"
						+"GDO51311409230000003') applyEnterpriseCode,u.uom UOM ,0 barcodeType,substring(Spec,1,128) "
						+"specificationsAndModels,hsCode,g.Price productDeclaredValue,g.Name hsGoodsName,b.applyEnterpriseCodeCIQ,"
						+"cc.code originCountry,br.name brand,case when isnull(Weigh,1000)/1000<1 then 1 else isnull(Weigh,1000)"
						+"/1000 end as weight ,case when isnull(NetWeigh,1000)/1000<1 then 1 else isnull(NetWeigh,1000)/1000 end as netWeight"
						+" from BarcodeTranList a , Barcode b , Merchandise g , PostTariff p,unit u,"
						+"countrycode cc ,dept d ,brand br where a.BarcodeID = b.BarcodeID and b.MID = g.MID and g.unitname="
						+"u.unitname and cc.name=g.origin and g.PostTaxNo = p.Code and d.deptid=g.deptid and g.brandid=br.BrandID and SheetID = '"+sheetid+"'";
					boolean isSuccess = false;
					Vector vtsku=SQLHelper.multiRowSelect(conn, sql);
					for (int i=0;i<vtsku.size();i++)
					{
						Hashtable htsku=(Hashtable) vtsku.get(i);
						ProductInfo productInfo = new ProductInfo();
						productInfo.getMapData(htsku);
						info.createProduct(request, productInfo, ask, message, error);
						if("1".equals(ask.value)){
							isSuccess=true;
							/*
							conn.setAutoCommit(false);
							FedexUtil.bakcUpDownNote(conn,serialid);
							conn.commit();
							conn.setAutoCommit(true);
							**/
							Log.info("添加商品成功,sku: "+productInfo.getSkuNo());
						}else{
							isSuccess=false;
							Log.info("上传商品数据失败: "+message.value);
							List<ErrorType>  type = (List<ErrorType>)error.value;
							for(Iterator t =type.iterator();t.hasNext(); ){
								ErrorType err =(ErrorType) t.next();
								Log.info("上传商品数据失败: "+err.getErrorMessage());
							}
						}
					}
					if(isSuccess){//处理成功备份接口数据
						conn.setAutoCommit(false);
						FedexUtil.bakcUpDownNote(conn,serialid);
						conn.commit();
						conn.setAutoCommit(true);
					}
				}
				
			} catch (Exception e) {
				try {
					if (conn != null && !conn.getAutoCommit()){
						conn.rollback();
						conn.setAutoCommit(true);
					}
						
				} catch (Exception e1) {
					Log.error(jobname, "回滚事务失败");
				}
				e.printStackTrace();
				Log.error("105", jobname, Log.getErrorMessage(e));
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
					Log.error(jobname, "关闭数据库连接失败");
				}
			}
			System.gc();
			long startwaittime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startwaittime < (long) (Params.waittime * 1000))		
				try {
					sleep(1000L);
				} catch (Exception e) {
					Log.warn(jobname, "系统不支持休眠操作, 作业将严重影响机器性能");
				}
		} while (true);
	}
	
}
