package MyTest;

/*
 * Created on 2005-6-12
 * Author stephen
 * Email zhoujianqiang AT gmail DOT com
 * CopyRight(C)2005-2008 , All rights reserved.
 */
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * java反射机制的测试类.
 * 
 * @author stephen
 * @version 1.0.0
 */
public class ReflectTest {
	
	/**
	 * 数组参数的标志字符和实际对应的类型.
	 * 其中'L'表示对象数组，其余表示java基础类型的数组.
	 */
	public static final char[]   CODES = new char[]
	{'Z',      'B',   'C',  'L','D',     'F',    'I',   'J',  'S'};
	public static final String[] VALUES =          
	{"boolean","byte","char","","double","float","int","long","short"};
	
	/**
	 * 解析方法的参数.
	 * @param parameter 待解析的参数.
	 * @return
	 */
	public static String parseParameter(String parameter) {
		/* 在java.lang.Class类中关于参数的说明补充
		 * ------------------------------------
		 * Examples:
	     * String.class.getName()
	     *     returns "java.lang.String"
	     * byte.class.getName()
	     *     returns "byte"
	     * (new Object[3]).getClass().getName()
	     *     returns "[Ljava.lang.Object;"
	     * (new int[3][4][5][6][7][8][9]).getClass().getName()
	     *     returns "[[[[[[[I"
	     * ------------------------------------
	     * 1个[表示1维数组，2个[表示2维数组，...，依此类推。
		 */
		boolean isArray = false;
		//如果是数组的可能会在最后带个分号过来,需要去掉.
		if(parameter.charAt(parameter.length()-1)==';'){
			//去掉最后的分号
			parameter = parameter.substring(0,parameter.length()-1);
		}
		while(parameter.indexOf('[')==0){
			isArray = true;
			parameter = parameter.substring(1)+"[]";
		}
		if(isArray){
			char code = parameter.charAt(0);
			for(int i=0;i<CODES.length;i++){
				if(CODES[i]==code){
					parameter=VALUES[i]+parameter.substring(1);
					break;
				}
			}
		}
		return parameter;
	}
	
	/**
	 * 解析属性的类型.
	 * @param parameter 待解析的属性.
	 * @return
	 */
	public static String parseFieldParameter(String parameter) {
		return parseParameter(parameter);
	}
	
	/**
	 * 解析方法返回值的类型.
	 * @param parameter 待解析的方法返回值类型.
	 * @return
	 */
	public static String parseMethodParameter(String parameter) {
		return parseFieldParameter(parameter);
	}
	

	/**
	 * main方法.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
    		StringBuffer debugInfo = new StringBuffer();
    		
    		String className = "java.lang.StringBuffer";//填写要测试的类
			Class c = Class.forName(className);//载入类
			
			debugInfo.append("****************************************\n");
			debugInfo.append("*  "+className+" \n*  通过java反射机制取出的信息"+"\n");
			debugInfo.append("****************************************\n");
			
			//获取包和类名
			Package thePackage = c.getPackage();//获取类的包
			String[] names = c.getName().split("[.]");
			String name = names[names.length-1];
			//获取类的修饰符[如 public final private等等]，
			//具体的含义请查看 [java.lang.reflect.Modifier] 中定义的修饰符常量
			//java.lang.String的修饰符是 public final
			int modifiers = c.getModifiers();
			debugInfo.append(Modifier.toString(modifiers)+" class "+ name);
			
			//取出类的父类(extends)
			Class superClass = c.getSuperclass();
			if(superClass!=null && (!"java.lang.Object".equals(superClass.getName()))){
				debugInfo.append(" extends ");
				debugInfo.append(superClass.getName());
			}
			
			//取出类的接口(implements)
			Class[] interfaces = c.getInterfaces();
			if(interfaces!=null && interfaces.length>0){
				debugInfo.append(" implements ");
				for(int i=0;i<interfaces.length;i++){
					if(i>0) debugInfo.append(",");
					debugInfo.append(interfaces[i].getName());
				}
			}
			
			debugInfo.append(" {\n");
			
			//取出所有定义的属性
			debugInfo.append("    //取出所有定义的属性\n");
			Field[] fields = c.getDeclaredFields();
			for(int i=0;fields!=null && i<fields.length;i++){
				//取出属性
				debugInfo.append("    "+Modifier.toString(fields[i].getModifiers())
						+" "+parseFieldParameter(fields[i].getType().getName())
						+" "+fields[i].getName());
				debugInfo.append(";\n");
			}
			
			//获取构造器方法
			debugInfo.append("    //取出所有的构造器方法\n");
			Constructor[] constructors = c.getConstructors();//取出所有定义的构造器方法
			for(int i=0;constructors!=null && i<constructors.length;i++){
				//取出构造器
				debugInfo.append("    "+Modifier.toString(constructors[i].getModifiers())
						+" "+name+"(");
				Class[] parameterTypes = constructors[i].getParameterTypes();
				for(int j=0;parameterTypes!=null&&j<parameterTypes.length;j++){
					if(j>0) debugInfo.append(",");
					debugInfo.append(parseParameter(parameterTypes[j].getName()));//构造器参数
				}
				debugInfo.append("){}\n");
			}
			
			//取出构造器以外的所有方法
			debugInfo.append("    //取出构造器以外的所有方法\n");
			Method[] methods = c.getDeclaredMethods();
			for(int i=0;methods!=null && i<methods.length;i++){
				//取出方法
				debugInfo.append("    "+Modifier.toString(methods[i].getModifiers())+" "
						+ parseMethodParameter(methods[i].getReturnType().getName()) +" "
						+methods[i].getName()+"(");
				Class[] parameterTypes = methods[i].getParameterTypes();
				for(int j=0;parameterTypes!=null&&j<parameterTypes.length;j++){
					if(j>0) debugInfo.append(",");
					debugInfo.append(parseParameter(parameterTypes[j].getName()));//构造器参数
				}
				debugInfo.append("){}\n");
			}
			debugInfo.append("}\n");
			
			System.out.println(debugInfo.toString()+"\n");//输出debug信息
			
			//利用反射机制来调用类中的方法
			//创建一个StringBuffer对象实例 相当于 StringBuffer o = new StringBuffer();
			Object o = c.newInstance();
			//调用方法 o.append(String):认识当参数为对象类型怎么调用的
			Class[] paramTypes = {Class.forName("java.lang.String")};//定义append方法需要的参数
			Object[] values = {new String("hello world")};//定义append方法需要的参数对应的数据
			//调用append(String)方法 相当于 o.append("hello world");
			o.getClass().getMethod("append",paramTypes).invoke(o,values);
			//这里将输出 hello world
			System.out.println("o.append(\""+values[0]+"\").toString()="+o.toString());
			//调用方法 o.append(int):认识当参数为int,float,double,char,boolean等基础类型时候怎么调用的
			paramTypes = new Class[]{int.class};
			values = new Object[]{new Integer(100)};
			o.getClass().getMethod("append",paramTypes).invoke(o,values);
			//这里将输出 hello world100
			System.out.println("o.append(\""+values[0]+"\").toString()="+o.toString());
			
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
