package MyTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckIP {
 public static void main(String[] args) {
  
  System.out.println(getV4IP());
 }
 
 
 public static String getV4IP() {
	 String ip = "";
	 String chinaz = "http://ip.chinaz.com/";
	 String inputLine = "";
	 String read = "";
	 try {
	 URL url = new URL(chinaz);
	 HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	 BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	 while ((read = in.readLine()) != null) {
	 inputLine += read;
	 }

	 } catch (Exception e) {
	 e.printStackTrace();
	 }

	 Pattern p = Pattern.compile("\\<strong class\\=\"red\">(.*?)\\<\\/strong>");
	 Matcher m = p.matcher(inputLine);
	 if(m.find()){
	 String ipstr = m.group(1);
	 System.out.println(ipstr);
	 }
	 return ip;
	 }

}