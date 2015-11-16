package com.wofu.base.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DataRelation implements Serializable {
	
	private static final long serialVersionUID=1L;
	protected String relationname;
	protected String classname;
	protected String jsonfields="";
	private List<BusinessClass> relationdata;
		
	public DataRelation()
	{
		
	}
	
	public DataRelation(String relationname,String classname)
	{
		this.relationname=relationname;
		this.classname=classname;
		this.relationdata=new Vector<BusinessClass>();
		this.jsonfields="";
	}
	public DataRelation(String relationname,String classname,String jsonfields)
	{
		this.relationname=relationname;
		this.classname=classname;
		this.relationdata=new Vector<BusinessClass>();
		this.jsonfields=jsonfields;
	}
	
	public void setClassName(String classname)
	{
		this.classname=classname;
	}
	public String getClassName()
	{
		return this.classname;
	}
	public List getRelationData()
	{
		return this.relationdata;
	}
	
	public String getRelationName()
	{
		return this.relationname;
	}
	//只限制sql查询出来生成的Vector
	public void setRelationData(Vector data)	throws Exception
	{
		this.relationdata.removeAll(this.relationdata);
		for (int i=0;i<data.size();i++)
		{
			Map mp=(Map) data.get(i);
			BusinessClass businessclass=(BusinessClass) Class.forName(this.classname).newInstance();
			businessclass.getMapData(mp);
			this.relationdata.add(businessclass);
		}
	}
	public void setRelationData(ArrayList<BusinessClass> data)	throws Exception
	{
		this.relationdata=data;
	}
	public void setRelationData(Object obj)	throws Exception
	{
		if (obj!=null)
		{
			if (obj instanceof DataRelation)
			{
				this.relationdata=((DataRelation) obj).getRelationData();
			}
			else if (obj instanceof List)
			{
				setRelationData((List) obj);
			}
			else
			{
				if (obj instanceof String)
					return;
				if (this.relationdata!=null)
					this.relationdata.clear();
			}
		}
	}
	public void setRelationName(String relaame)
	{
		this.relationname=relaame;
	}
	
	public List toList()
	{
		return this.relationdata;
	}
	public boolean isNULL()
	{
		return ((this.relationdata == null) || (this.relationdata.size() < 1));
	}
	public int size()
	{
		return this.relationdata.size();
	}
	
	public String toJson() throws Exception
	{
		StringBuffer strbuf=new StringBuffer();
		strbuf.append("\""+this.relationname+"\"");
		//strbuf.append("s");
		strbuf.append(":[");
		if (this.relationdata.size()>0)
		{
			for (int i=0;i<this.relationdata.size();i++)
			{
				BusinessClass bcls=(BusinessClass) this.relationdata.get(i);		
				strbuf.append(bcls.toJSONObject(this.jsonfields));
				strbuf.append(",");
			}
			strbuf.deleteCharAt(strbuf.length()-1);
		}
		strbuf.append("]");
		return strbuf.toString();
	}

	public String getJsonfields() {
		return jsonfields;
	}

	public void setJsonfields(String jsonfields) {
		this.jsonfields = jsonfields;
	}
}
