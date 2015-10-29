package MyTest;
import java.util.HashMap;
import java.util.Map;

import com.wofu.ecommerce.authorization.HttpUtil;
public class token {
	private final static String redirect_url ="http://120.26.193.249:30002/login.html";
	//
	public static void main(String[] args) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		param.put("grant_type", "authorization_code");
		param.put("code", "IOAPadiq55MXJRmMxLpNQMXO37123");
		param.put("client_id", "21520535");
		param.put("client_secret", "766bce17fd8ac852ea02a740277f1289" );
		param.put("redirect_uri", redirect_url);
		param.put("view", "web");
		param.put("state", "code");
		String result = HttpUtil.sendRequest("https://oauth.taobao.com/token", param, null,"utf-8");
		System.out.println("result: "+result);

	}


	
	
}
