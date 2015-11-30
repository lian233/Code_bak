package MyTest;
import java.util.HashMap;
import java.util.Map;

import com.wofu.ecommerce.authorization.HttpUtil;
public class token2 {
	private final static String redirect_url ="http://120.26.193.249:30002/login.html";
	//
	public static void main(String[] args) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		param.put("grant_type", "authorization_code");
		param.put("code", "An6b1lrvPnCmxJ9H3q6Kb9eE1601621");
		param.put("client_id", "21520535");
		param.put("client_secret", "766bce17fd8ac852ea02a740277f1289" );
		param.put("redirect_uri", redirect_url);
		param.put("view", "web");
		param.put("state", "scope%3Ar1%2Cr2%2Cw1%2Cw2%3Bsign%3A9E91E2D9BFCCCE741F79F449BB6AE1A0%3BleaseId%3A0%3Btimestamp%3A1447829363086%3BversionNo%3A1%3Bouter_trade_code%3A%3BitemCode%3AFW_GOODS-1872240-1");
		String result = HttpUtil.sendRequest("https://oauth.taobao.com/token", param, null,"utf-8");
		System.out.println("result: "+result);

	}


	
	
}
