import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wofu.fenxiao.domain.Account;
@Controller//用于标识是处理器类
public class RequestMethodTest {
	//restful风格 这里用@PathVariable绑定参数
	//responseBody直接返回返回一个对象，spring会自动转换成json对象
	@RequestMapping(value="/get1/{username}/{password}/{sex}/{age}",method=RequestMethod.GET)//请求到处理器功能方法的映射规则；
	public @ResponseBody Account get1(@PathVariable(value="username") String username,@PathVariable String password,@PathVariable String sex,@PathVariable int age){
		System.out.println(username+" "+password+" "+sex+" "+age);
		return new Account();
	}
	//直接用servlet的方法获取  直接在方法形参中声明这个变量，就可以使用了
	/**
	 * request.getParams('xxx')
	 * @return
	 */
	@RequestMapping(value="/get2",method=RequestMethod.GET)
	public String get2(HttpServletRequest request ,HttpServletResponse response){
		System.out.println(request.getParameter("username")+"/"+request.getParameter("password")+"/"
	+request.getParameter("sex")+request.getParameter("age"));
		return "get2";
	}
	/**
	 * 使用@requestParam接收参数 springmvc会自动封装参数到这个变量中
	 * 请求参数到处理器功能处理方法的方法参数上的绑定；
	 * @param username
	 * @param password
	 * @param sex
	 * @param age
	 * @return
	 * value：参数名字，即入参的请求参数名字，如username表示请求的参数区中的名字为username的参数的值将传入；
       required：是否必须，默认是true，表示请求中一定要有相应的参数，否则将报404错误码；
       defaultValue：默认值，表示如果请求中没有同名参数时的默认值，默认值可以是SpEL表达式，如“#{systemProperties['java.vm.version']}”。
       
       如果请求中有多个同名的应该如何接收呢？如给用户授权时，可能授予多个权限，首先看下如下代码：
    public String requestparam7(@RequestParam(value="role") String roleList)
      如果请求参数类似于url?role=admin&rule=user，则实际roleList参数入参的数据为“admin,user”，即多个数据之间使用“，”分割；我们应该使用如下方式来接收多个请求参数：
   public String requestparam7(@RequestParam(value="role") String[] roleList)   
或
 
public String requestparam8(@RequestParam(value="list") List<String> list) 
	 */
	@RequestMapping(value="/get3",method=RequestMethod.GET)
	public String get3(@RequestParam(value="usernam",required=false) String username,@RequestParam("password") String password,
			@RequestParam("sex") String sex,@RequestParam("age") int[] ages,HttpServletRequest request){
		System.out.println(username+" "+password+" "+sex+" ");
		
		return "";
	}
	/*
	 * 接收多个同名的参数，url?role=admin&rule=user（实际传参：url?role=admin,user这里要用一个数组来接收这个参数了
	 * @CookieValue(value="JSESSIONID")  取某个cookie的值
	 * @RequestHeader(value="User-agent")  取请求头的值
	 */
	@RequestMapping(value={"/get4","/get5"},method=RequestMethod.GET)  //多个url对应一个方法 两者是或的关系
	public String get4(@RequestParam(value="username",required=false) String username,@RequestParam("password") String password,
			@RequestParam("sex") String sex,@RequestParam("age") int[] ages,HttpServletRequest request,
			@CookieValue(value="JSESSIONID") String sessionId,@RequestHeader(value="User-agent") String userAgent,
			@RequestHeader(value="Accept") String accept){
		System.out.println(username+" "+password+" "+sex+" ");
		for(int e:ages){
			System.out.println(e);
		}
		System.out.println("cookie: "+sessionId);
		System.out.println("UserAgent: "+userAgent);
		System.out.println("Accept: "+accept);
		return "";
	}
	
	@RequestMapping(value="get6/**")  //ant式url映射
	public String get6(@RequestParam(value="username") String username){
		System.out.println(username);
		return "";
	}
	
	@RequestMapping(value="/get7/{param:\\d+}-{pageSize:\\d+}")  //匹配正则表达式 /get7/11-11这样的url   变量的值可以用PathVariable提取
	public String get7(@PathVariable(value="param") String param,@PathVariable(value="pageSize") String pageSize){
		System.out.println(param+" "+pageSize);
		return "";
	}
	
	//限定请求中要含有create参数  如/url?create=sss;
	@RequestMapping(params="creates",method=RequestMethod.GET)
	public String get8(){
		System.out.println("ssss");
		return "";
	}
	
	//限定请求中不能含有create参数  如/url?create=sss;
		@RequestMapping(value="/get9",params="!create",method=RequestMethod.GET)
		public String get9(){
			System.out.println("no create!");
			return "";
		}
		
		//方法限定
		@RequestMapping(value="/get10",method={RequestMethod.GET,RequestMethod.POST})  //表示两个方法都可以请求
		public String get10(){
			return "";
		}
		
		//请求中指定参数名=参数值
		@RequestMapping(params="create=bolinli",method=RequestMethod.GET)
		public String get11(){
			System.out.println("指定参数名=参数值");
			return "";
		}
		
		//请求参数中参数名！=参数值
		@RequestMapping(value="/get12",params="create!=bolinli",method=RequestMethod.GET)
		public String get12(){
			System.out.println("指定参数名!=参数值");
			return "";
			
		}
		
		//参数限定组合，这里是&&的关系，两个都要满足
		@RequestMapping(params={"test1","test2=test2"})
		public String get13(){
			System.out.println("参数限定的组合使用，是且的关系，两个都要满足");
			return "";
		}
		
		//请求头限定  headers关键字
		@RequestMapping(value="/get14",headers="test")
		public String get14(){
			System.out.println("必须有请求头abc");
			return "";
		}
		
		//请求头不能包含有test
		@RequestMapping(value="/get15",headers="!test")
		public String get15(){
			System.out.println("请求头不能包含有test");
			return "";
		}
		
		//请求头中包含特定的请求头跟值
		@RequestMapping(value="/get16",headers="test=bolin")
		public String get16(){
			System.out.println("请求头必须含有test,且值为bolin");
			return "";
		}
		
		//请求头中包含多个指定的值 ，是且的关系，两个都要满足
		@RequestMapping(value="get17",headers={"test=bolin","test1=bolinli"})
		public String get17(HttpServletRequest request){
			System.out.println(request.getContentType());   //请求的内容类型
			System.out.println("同时满足多个请求头");
			return "";
		}
		
		//参数列表中直接使用InputStream ,OutputStream
		@RequestMapping(value="/get18")
		public  void get18(InputStream in,OutputStream out){
			try {
				StringBuilder  requestData  = new StringBuilder();
				String line=null;
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				//System.out.println();
				for(line = br.readLine();line!=null;){
					System.out.println(line);
					requestData.append(line);
					line = br.readLine();
				}
				System.out.println("请求参数为: "+requestData.toString());
				out.write("hello".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}
		
		//访问session  这个永远不为空
		@RequestMapping(value="/get19")
		public String get19(HttpSession session){
			System.out.println(session);
			return "";
		}
		
}
