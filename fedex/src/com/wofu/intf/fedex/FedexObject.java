package com.wofu.intf.fedex;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.wofu.base.util.BusinessClass;
import com.wofu.common.json.JSONObject;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.StreamUtil;
public abstract class FedexObject {
	public void setFieldValue(Object obj, String fieldname,
			Object fieldvalue) throws Exception {
		Field[] fields = obj.getClass().getDeclaredFields();
		String setmethodname = "set" + fieldname.substring(0, 1).toUpperCase()
				+ fieldname.substring(1, fieldname.length());
		Method th = null;
		

		for (int i = 0; i < fields.length; i++) {

			if (fields[i].getName().equalsIgnoreCase(fieldname)
					&& (fieldvalue != null)) {
				Class cls = fields[i].getType();
				String sfieldvalue = String.valueOf(fieldvalue);
				if (cls == boolean.class) {
						th = obj.getClass().getMethod(setmethodname,
								boolean.class);
						th.invoke(obj, Boolean.valueOf(String
								.valueOf(fieldvalue)));
					} else if (cls == double.class) {
						th = obj.getClass().getMethod(setmethodname,
								double.class);
						th.invoke(obj, Double.valueOf(String
								.valueOf(fieldvalue)));
					} else if (cls == java.math.BigDecimal.class) {
						th = obj.getClass().getMethod(setmethodname,
								java.math.BigDecimal.class);
						th.invoke(obj, java.math.BigDecimal.valueOf(Double
								.valueOf((String.valueOf(fieldvalue)))));
					} else if (cls == float.class) {
						th = obj.getClass().getMethod(setmethodname,
								float.class);
						th.invoke(obj, Float
								.valueOf(String.valueOf(fieldvalue)));
					} else if (cls == int.class) {
						th = obj.getClass().getMethod(setmethodname, int.class);

						if (String.valueOf(fieldvalue).equalsIgnoreCase("true"))
							fieldvalue = "1";
						if (String.valueOf(fieldvalue)
								.equalsIgnoreCase("false"))
							fieldvalue = "0";

						th.invoke(obj, Integer.valueOf(String
								.valueOf(fieldvalue)));
					} else if (cls == Integer.class) {
						th = obj.getClass().getMethod(setmethodname,
								Integer.class);

						if (String.valueOf(fieldvalue).equalsIgnoreCase("true"))
							fieldvalue = "1";
						if (String.valueOf(fieldvalue)
								.equalsIgnoreCase("false"))
							fieldvalue = "0";

						th.invoke(obj, Integer.valueOf(String
								.valueOf(fieldvalue)));
					} else if (cls == long.class ||cls==Long.class) {
						th = obj.getClass()
								.getMethod(setmethodname, long.class);
						th
								.invoke(obj, Long.valueOf(String
										.valueOf(fieldvalue)));
					} else if (cls == java.math.BigInteger.class) {
						th = obj.getClass().getMethod(setmethodname,
								java.math.BigInteger.class);
						th.invoke(obj, java.math.BigInteger.valueOf(Long
								.valueOf((String.valueOf(fieldvalue)))));
					} else if (cls == Date.class) {
						th = obj.getClass()
								.getMethod(setmethodname, Date.class);
						if (fieldvalue.getClass() == Date.class){
							th.invoke(obj, (Date) fieldvalue);
						}
							
						else{
							if (String.valueOf(fieldvalue).length()==19)
								th.invoke(obj, Formatter.parseDate(String
										.valueOf(fieldvalue),
										Formatter.DATE_TIME_FORMAT));
							else if(String.valueOf(fieldvalue).length()<19){
								//补全0
									StringBuilder sb = new StringBuilder();
									String[] temp = String.valueOf(fieldvalue).split(" ");
									for(int k=0;k<2;k++){
										if(k==0){
											String[] t = temp[k].split("-");
											for(String e:t){
												if(e.length()==1) sb.append("0").append(e).append("-");
												else sb.append(e).append("-");
											}
											sb.deleteCharAt(sb.length()-1).append(" ");
										}else if(k==1){
											String[] t = temp[k].split(":");
											for(String e:t){
												if(e.length()==1) sb.append("0").append(e).append(":");
												else sb.append(e).append(":");
											}
											sb.deleteCharAt(sb.length()-1);
											
										}
									}
									if(sb.length()==19)
									th.invoke(obj, Formatter.parseDate(sb.toString(),Formatter.DATE_TIME_FORMAT));
							}
							else
								th.invoke(obj, Formatter.parseDate(String
									.valueOf(fieldvalue),
									Formatter.DATE_TIME_MS_FORMAT));
							
						}
					} else if (cls == java.sql.Date.class) {
						th = obj.getClass().getMethod(setmethodname,
								java.sql.Date.class);
						if (fieldvalue.getClass() == java.sql.Date.class)
							th.invoke(obj, (Date) fieldvalue);
						else
							if (String.valueOf(fieldvalue).length()==19)
								th.invoke(obj, new java.sql.Date(Formatter
										.parseDate(String.valueOf(fieldvalue),
												Formatter.DATE_TIME_FORMAT)
										.getTime()));
							else
								th.invoke(obj, new java.sql.Date(Formatter
									.parseDate(String.valueOf(fieldvalue),
											Formatter.DATE_TIME_MS_FORMAT)
									.getTime()));
					} else if (cls == java.sql.Timestamp.class) {
						th = obj.getClass().getMethod(setmethodname,
								java.sql.Timestamp.class);
						if (fieldvalue.getClass() == java.sql.Timestamp.class)
							th.invoke(obj, (Date) fieldvalue);
						else
							if (String.valueOf(fieldvalue).length()==19)
								th.invoke(obj, new java.sql.Timestamp(Formatter
										.parseDate(String.valueOf(fieldvalue),
												Formatter.DATE_TIME_FORMAT)
										.getTime()));
							else
								th.invoke(obj, new java.sql.Timestamp(Formatter
									.parseDate(String.valueOf(fieldvalue),
											Formatter.DATE_TIME_MS_FORMAT)
									.getTime()));
					} else if (cls == InputStream.class) {
						th = obj.getClass().getMethod(setmethodname,
								InputStream.class);
						if (fieldvalue instanceof InputStream)
							th.invoke(obj, new ByteArrayInputStream(StreamUtil.InputStreamToStr((InputStream) fieldvalue, "GBK").getBytes()));
						else
							th.invoke(obj, new ByteArrayInputStream(String.valueOf(
								fieldvalue).getBytes()));
					
					} else if (cls == List.class) {
						th = obj.getClass().getMethod(setmethodname,
								List.class);
						Type fc = fields[i].getGenericType();//获取Generic的类型
						if(fc==null){//不包含泛型信息
							ArrayList arr = new ArrayList();
							arr.add(fieldvalue);
							th.invoke(obj, arr);
						}else{
							if(fc instanceof ParameterizedType){
								ParameterizedType pt = (ParameterizedType)fc;
								Class c = (Class)pt.getActualTypeArguments()[0];
								ArrayList arr = new ArrayList();
								if(c ==Float.class){
									arr.add(Float.valueOf(String.valueOf(fieldvalue)));
								}
								th.invoke(obj, arr);
								
							}//如果是泛型
						}
							
							
							
					}else {
						th = obj.getClass().getMethod(setmethodname,
								String.class);
						th.invoke(obj, String.valueOf(fieldvalue));
					}
					
					//System.out.println("end:"+fieldname+"="+sfieldvalue);


			}

		}
	}
	
	public void getMapData(Map mp) throws Exception {
		if (mp.size() > 0) {
			for (Iterator it = mp.keySet().iterator(); it.hasNext();) {
				String keyname = (String) it.next();
				Object keyvalue = mp.get(keyname);
				System.out.println(keyname+":"+keyvalue);
				setFieldValue(this, keyname, keyvalue);
			}
		}
	}
	
	private Field getFieldByFieldName(BusinessClass obj,String fieldname)
	{
		Field field=null;
		
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {

			if (fields[i].getName().equalsIgnoreCase(fieldname))
				field=fields[i];
		}
		
		return field;
	}
	
	
	
	public void setObjValue(BusinessClass obj, JSONObject jsobj)
	throws Exception {

for (Iterator it = jsobj.keys(); it.hasNext();) {
	String fieldname = (String) it.next();

	
	Object fieldvalue = jsobj.optJSONArray(fieldname);
	// 如果是数组的话为子对象,将数据填入DataRelation对象中，否则直接给该对象域赋值
	if (fieldvalue == null)
	{

		Field field=getFieldByFieldName(obj,fieldname);

		if (field==null) continue;
		
		Class cls = field.getType();
		
		fieldvalue =String.valueOf(jsobj.opt(fieldname));
		
		if (cls == boolean.class) {
			if (fieldvalue ==null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=false; 
		} else if (cls == double.class) {
			if (fieldvalue ==null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=0.00;
		} else if (cls == java.math.BigDecimal.class) {
			if (fieldvalue ==null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=0.00;
		} else if (cls == float.class) {
			if (fieldvalue ==null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=0.00;
		} else if (cls == int.class) {
			if (fieldvalue ==null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=0;
		} else if (cls == Integer.class) {
			if (fieldvalue ==null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=0;
		} else if (cls == long.class || cls == Long.class) {
			if (fieldvalue ==null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=0;
		} else if (cls == java.math.BigInteger.class) {
			if (fieldvalue ==null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=0;				
		} else if(cls == java.util.Date.class){  //加上时间的判断
			if (fieldvalue == null || fieldvalue.equals("null") || fieldvalue.equals("")) fieldvalue=new Date();
		}else {
			if (fieldvalue ==null || fieldvalue.equals("null")) fieldvalue="";
		}
		
		//System.out.println("fieldname:"+fieldname+" fieldvalue:"+fieldvalue);
		
	}

	setFieldValue(obj, fieldname, fieldvalue);

}
}
}
