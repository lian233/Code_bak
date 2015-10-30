
一：整合工程所需要的包：(放到lib目录下)
（1）spring3.2.8  复制lib目录下所有的jar到webinfo/lib下
（2)hibernate4.1.7  复制lib/request下的包到webinfo/lib下
二：配置web.xml：里面有配置springmvc的一个servlet的
DispatcherServlet：
使用Spring MVC,配置DispatcherServlet是第一步。
DispatcherServlet是一个Servlet,所以可以配置多个DispatcherServlet。
DispatcherServlet是前置控制器，配置在web.xml文件中的。拦截匹配的请求，Servlet拦截匹配规则要自已定义，把拦截下来的请求，依据某某规则分发到目标Controller(我们写的Action)来处理。
在DispatcherServlet的初始化过程中，框架会在web应用的 WEB-INF文件夹下寻找名为[servlet-name]-servlet.xml 的配置文件，
生成文件中定义的bean。

<load-on-startup>1</load-on-startup>是启动顺序，让这个Servlet随Servletp容器一起启动。
 <url-pattern>*.form</url-pattern> 会拦截*.form结尾的请求。
 
 <servlet-name>example</servlet-name>这个Servlet的名字是example，可以有多个DispatcherServlet，是通过名字来区分的。
 每一个DispatcherServlet有自己的WebApplicationContext上下文对象。同时保存的ServletContext中和Request对象中，关于key，以后说明。
 
在DispatcherServlet的初始化过程中，框架会在web应用的 WEB-INF文件夹下寻找名为[servlet-name]-servlet.xml 的配置文件，生成文件中定义的bean。

<servlet>  
    <servlet-name>springMVC</servlet-name>  
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>  
    <init-param>  
        <param-name>contextConfigLocation</param-name>  
        <param-value>classpath*:/springMVC.xml</param-value>  
    </init-param>  
    <load-on-startup>1</load-on-startup>  
</servlet>  
<servlet-mapping>  
    <servlet-name>springMVC</servlet-name>  
    <url-pattern>/</url-pattern>  
</servlet-mapping>  
在这上面这个例子中的DispatcherServlet中  指定了配置文件，没有使用默认的配置文件（servletname.xml)
指明了配置文件的文件名，不使用默认配置文件名，而使用springMVC.xml配置文件。
其中<param-value>**.xml</param-value> 这里可以使用多种写法
1、不写,使用默认值:/WEB-INF/<servlet-name>-servlet.xml
2、<param-value>/WEB-INF/classes/springMVC.xml</param-value>
3、<param-value>classpath*:springMVC-mvc.xml</param-value>
4、多个值用逗号分隔

Servlet拦截匹配规则可以自已定义，拦截哪种URL合适？ 
当映射为@RequestMapping("/user/add")时，为例：

1、拦截*.do、*.htm， 例如：/user/add.do
这是最传统的方式，最简单也最实用。不会导致静态文件（jpg,js,css）被拦截。
 
2、拦截/，例如：/user/add
可以实现现在很流行的REST风格。很多互联网类型的应用很喜欢这种风格的URL。
弊端：会导致静态文件（jpg,js,css）被拦截后不能正常显示。想实现REST风格，事情就是麻烦一些。后面有解决办法还算简单。
 
3、拦截/*，这是一个错误的方式，请求可以走到Action中，但转到jsp时再次被拦截，不能访问到jsp。



九、Spring中的拦截器：
Spring为我们提供了：
org.springframework.web.servlet.HandlerInterceptor接口，
org.springframework.web.servlet.handler.HandlerInterceptorAdapter适配器，
实现这个接口或继承此类，可以非常方便的实现自己的拦截器。
 
有以下三个方法：
 
Action之前执行:
 public boolean preHandle(HttpServletRequest request,
   HttpServletResponse response, Object handler);
 
生成视图之前执行
 public void postHandle(HttpServletRequest request,
   HttpServletResponse response, Object handler,
   ModelAndView modelAndView);
 
最后执行，可用于释放资源
 public void afterCompletion(HttpServletRequest request,
   HttpServletResponse response, Object handler, Exception ex)
 
 
分别实现预处理、后处理（调用了Service并返回ModelAndView，但未进行页面渲染）、返回处理（已经渲染了页面） 
在preHandle中，可以进行编码、安全控制等处理； 
在postHandle中，有机会修改ModelAndView； 
在afterCompletion中，可以根据ex是否为null判断是否发生了异常，进行日志记录。 
参数中的Object handler是下一个拦截器。




三：配置spring-base.xml文件

   spring-dao.xml文件  配置hibernate
   
   整合springsecure
   要启用SpringSecurity3,我们需要完成以下两步
   
   
   1.在web.xml中声明DelegatingFilterProxy. 

1
2
3
4
<filter-mapping>  
        <filter-name>springSecurityFilterChain</filter-name>  
        <url-pattern>/*</url-pattern>  
    </filter-mapping>
    
    
    表示项目中所有路径的资源都要经过SpringSecurity. 
2.导入指定的SpringSecurity配置 :spring-security.xml 

注意一点.最好是将DelegatingFilterProxy写在DispatcherServlet之前.否则 
SpringSecurity可能不会正常工作.

   
   
   