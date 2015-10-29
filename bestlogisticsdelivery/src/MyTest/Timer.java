package MyTest;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.wofu.common.tools.conv.Secret;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
public class Timer {

	/**
	 * md5解码
	 */
	public static void main(String[] args) throws Exception{
		try {
			/**
			System.out.println(new Date(1423431439*1000L));
			System.out.println(new Date(1423470339*1000L));
			System.out.println(new Date(1423471495*1000L));
			**/
			//
			System.out.println(Secret.decrypt("gbZyhQkeSThdOF6df+mUBA=="));
			System.out.println(Secret.encrypt("djz-djz"));
			//System.out.println(new String("\u66fe\u4e3d\u82b3".getBytes()));
			//ECS_StockConfigSku stockConfigSku = new ECS_StockConfigSku();
			//System.out.println(stockConfigSku.getSynrate());
			//System.out.println(Float.parseFloat("0.8"));
			String test= "as ss ee  要我要";
			String[] tests = test.split(" ");
			System.out.println(tests.length);
			for(String e:tests){
				
				System.out.print(e);
				System.out.print("1");
			}
			/**
			 * 2014-11-10 01
Tue Nov 11 11:00:00 CST 2014
			 */
			
			//SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			//System.out.println(simp.format(Formatter.parseDate("2014-11-10 13:15:13",Formatter.DATE_TIME_FORMAT)));
			//System.out.println(simp.parse("2014-11-11 11:09"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
