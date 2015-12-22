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
 * java������ƵĲ�����.
 * 
 * @author stephen
 * @version 1.0.0
 */
public class ReflectTest {
	
	/**
	 * ��������ı�־�ַ���ʵ�ʶ�Ӧ������.
	 * ����'L'��ʾ�������飬�����ʾjava�������͵�����.
	 */
	public static final char[]   CODES = new char[]
	{'Z',      'B',   'C',  'L','D',     'F',    'I',   'J',  'S'};
	public static final String[] VALUES =          
	{"boolean","byte","char","","double","float","int","long","short"};
	
	/**
	 * ���������Ĳ���.
	 * @param parameter �������Ĳ���.
	 * @return
	 */
	public static String parseParameter(String parameter) {
		/* ��java.lang.Class���й��ڲ�����˵������
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
	     * 1��[��ʾ1ά���飬2��[��ʾ2ά���飬...���������ơ�
		 */
		boolean isArray = false;
		//���������Ŀ��ܻ����������ֺŹ���,��Ҫȥ��.
		if(parameter.charAt(parameter.length()-1)==';'){
			//ȥ�����ķֺ�
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
	 * �������Ե�����.
	 * @param parameter ������������.
	 * @return
	 */
	public static String parseFieldParameter(String parameter) {
		return parseParameter(parameter);
	}
	
	/**
	 * ������������ֵ������.
	 * @param parameter �������ķ�������ֵ����.
	 * @return
	 */
	public static String parseMethodParameter(String parameter) {
		return parseFieldParameter(parameter);
	}
	

	/**
	 * main����.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
    		StringBuffer debugInfo = new StringBuffer();
    		
    		String className = "java.lang.StringBuffer";//��дҪ���Ե���
			Class c = Class.forName(className);//������
			
			debugInfo.append("****************************************\n");
			debugInfo.append("*  "+className+" \n*  ͨ��java�������ȡ������Ϣ"+"\n");
			debugInfo.append("****************************************\n");
			
			//��ȡ��������
			Package thePackage = c.getPackage();//��ȡ��İ�
			String[] names = c.getName().split("[.]");
			String name = names[names.length-1];
			//��ȡ������η�[�� public final private�ȵ�]��
			//����ĺ�����鿴 [java.lang.reflect.Modifier] �ж�������η�����
			//java.lang.String�����η��� public final
			int modifiers = c.getModifiers();
			debugInfo.append(Modifier.toString(modifiers)+" class "+ name);
			
			//ȡ����ĸ���(extends)
			Class superClass = c.getSuperclass();
			if(superClass!=null && (!"java.lang.Object".equals(superClass.getName()))){
				debugInfo.append(" extends ");
				debugInfo.append(superClass.getName());
			}
			
			//ȡ����Ľӿ�(implements)
			Class[] interfaces = c.getInterfaces();
			if(interfaces!=null && interfaces.length>0){
				debugInfo.append(" implements ");
				for(int i=0;i<interfaces.length;i++){
					if(i>0) debugInfo.append(",");
					debugInfo.append(interfaces[i].getName());
				}
			}
			
			debugInfo.append(" {\n");
			
			//ȡ�����ж��������
			debugInfo.append("    //ȡ�����ж��������\n");
			Field[] fields = c.getDeclaredFields();
			for(int i=0;fields!=null && i<fields.length;i++){
				//ȡ������
				debugInfo.append("    "+Modifier.toString(fields[i].getModifiers())
						+" "+parseFieldParameter(fields[i].getType().getName())
						+" "+fields[i].getName());
				debugInfo.append(";\n");
			}
			
			//��ȡ����������
			debugInfo.append("    //ȡ�����еĹ���������\n");
			Constructor[] constructors = c.getConstructors();//ȡ�����ж���Ĺ���������
			for(int i=0;constructors!=null && i<constructors.length;i++){
				//ȡ��������
				debugInfo.append("    "+Modifier.toString(constructors[i].getModifiers())
						+" "+name+"(");
				Class[] parameterTypes = constructors[i].getParameterTypes();
				for(int j=0;parameterTypes!=null&&j<parameterTypes.length;j++){
					if(j>0) debugInfo.append(",");
					debugInfo.append(parseParameter(parameterTypes[j].getName()));//����������
				}
				debugInfo.append("){}\n");
			}
			
			//ȡ����������������з���
			debugInfo.append("    //ȡ����������������з���\n");
			Method[] methods = c.getDeclaredMethods();
			for(int i=0;methods!=null && i<methods.length;i++){
				//ȡ������
				debugInfo.append("    "+Modifier.toString(methods[i].getModifiers())+" "
						+ parseMethodParameter(methods[i].getReturnType().getName()) +" "
						+methods[i].getName()+"(");
				Class[] parameterTypes = methods[i].getParameterTypes();
				for(int j=0;parameterTypes!=null&&j<parameterTypes.length;j++){
					if(j>0) debugInfo.append(",");
					debugInfo.append(parseParameter(parameterTypes[j].getName()));//����������
				}
				debugInfo.append("){}\n");
			}
			debugInfo.append("}\n");
			
			System.out.println(debugInfo.toString()+"\n");//���debug��Ϣ
			
			//���÷���������������еķ���
			//����һ��StringBuffer����ʵ�� �൱�� StringBuffer o = new StringBuffer();
			Object o = c.newInstance();
			//���÷��� o.append(String):��ʶ������Ϊ����������ô���õ�
			Class[] paramTypes = {Class.forName("java.lang.String")};//����append������Ҫ�Ĳ���
			Object[] values = {new String("hello world")};//����append������Ҫ�Ĳ�����Ӧ������
			//����append(String)���� �൱�� o.append("hello world");
			o.getClass().getMethod("append",paramTypes).invoke(o,values);
			//���ｫ��� hello world
			System.out.println("o.append(\""+values[0]+"\").toString()="+o.toString());
			//���÷��� o.append(int):��ʶ������Ϊint,float,double,char,boolean�Ȼ�������ʱ����ô���õ�
			paramTypes = new Class[]{int.class};
			values = new Object[]{new Integer(100)};
			o.getClass().getMethod("append",paramTypes).invoke(o,values);
			//���ｫ��� hello world100
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
