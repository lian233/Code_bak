package com.wofu.retail.baseinformation;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.wofu.base.file.ECS_File;
import com.wofu.base.util.DataRelation;
import com.wofu.base.util.PageBusinessObject;
import com.wofu.business.stock.StockManager;
import com.wofu.common.tools.util.FileUtil;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.ImageUtil;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import com.wofu.ecommerce.stockmanager.ECS_StockConfig;
import com.wofu.ecommerce.stockmanager.ECS_StockConfigSku;
import com.wofu.ecommerce.suning.StockUtils;


public class ECS_Item extends PageBusinessObject {
	private int itemid;
	private String itemname;
	private String shortname;
	private String customid;
	private String customcode;
	private int itemtypeid;
	private int catid;
	private int brandid;
	private int stid;
	private int sid;
	private int kindid;
	private int seasonid;
	private String seriesid;
	private int projectid;
	private String designer;
	private int yearid;
	private String origin;
	private int materialid;
	private int measuretype;
	private double baseprice;
	private double contractcost;
	private String unitname;
	private int status;
	private String creator;
	private Date createtime;
	private String updator;
	private Date updatetime;
	private int merchantid;
	
	private DataRelation itemorgofitems =new DataRelation("itemorgofitem","com.wofu.retail.baseinformation.ECS_ItemOrg");
	private DataRelation itemimageofitems =new DataRelation("itemimageofitem","com.wofu.retail.baseinformation.ECS_ItemImage");
	private DataRelation itemskuofitems =new DataRelation("itemskuofitem","com.wofu.retail.baseinformation.ECS_ItemSku");

	public ECS_Item()
	{			
		this.uniqueFields1="customcode";
		this.uniqueFields2="itemname";
	}
	
	public void getItemInfo() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String inputValue=prop.getProperty("inputValue");
		
		String rsql="";
		boolean isexists=false;
		
		String sql="select count(*) from ecs_itemsku with(nolock) where custombc='"+inputValue+"'";
		
		if (this.getDao().intSelect(sql)>0)
		{
			rsql="select a.itemid,b.skuid,b.customid skucustomid,"
				+"a.customid itemcustomid,a.itemname,"
				+"a.catid,a.status,a.unitname,a.baseprice,a.itemtypeid,"
				+"a.measuretype,a.customcode,b.custombc,b.sizeid,b.colorid,"
				+"b.cupid,c.name catname,d.name sizename,e.name colorname,"
				+"isnull(f.name,'') cupname,isnull(g.qty,0) qty,isnull(g.lockqty,0) lockqty "
				+"from ecs_item a with(nolock),ecs_itemsku b with(nolock),"
				+"ecs_category c with(nolock),ecs_size d with(nolock),"
				+"ecs_color e with(nolock),ecs_cup f with(nolock),"
				+"ecs_inventorybc g with(nolock) "
				+"where a.itemid=b.itemid and a.catid=c.catid "
				+"and b.sizeid=d.sizeid and b.colorid=e.colorid "
				+"and b.cupid*=f.cupid and b.skuid*=g.skuid "
				+"and b.custombc='"+inputValue+"'";
			isexists=true;
		}
		else
		{
			sql="select count(*) from ecs_itemsku with(nolock) where customid='"+inputValue+"'";
			if (this.getDao().intSelect(sql)>0)
			{
				rsql="select a.itemid,b.skuid,b.customid skucustomid,"
					+"a.customid itemcustomid,a.itemname,"
					+"a.catid,a.status,a.unitname,a.baseprice,a.itemtypeid,"
					+"a.measuretype,a.customcode,b.custombc,b.sizeid,b.colorid,"
					+"b.cupid,c.name catname,d.name sizename,e.name colorname,"
					+"isnull(f.name,'') cupname,isnull(g.qty,0) qty,isnull(g.lockqty,0) lockqty "
					+"from ecs_item a with(nolock),ecs_itemsku b with(nolock),"
					+"ecs_category c with(nolock),ecs_size d with(nolock),"
					+"ecs_color e with(nolock),ecs_cup f with(nolock),"
					+"ecs_inventorybc g with(nolock) "
					+"where a.itemid=b.itemid and a.catid=c.catid "
					+"and b.sizeid=d.sizeid and b.colorid=e.colorid "
					+"and b.cupid*=f.cupid and b.skuid*=g.skuid "
					+"and b.customid='"+inputValue+"'";
				isexists=true;
			}
			else
			{
				sql="select count(*) from ecs_item with(nolock) where customcode='"+inputValue+"'";
				if (this.getDao().intSelect(sql)>0)
				{
					rsql="select a.itemid,b.skuid,b.customid skucustomid,"
						+"a.customid itemcustomid,a.itemname,"
						+"a.catid,a.status,a.unitname,a.baseprice,a.itemtypeid,"
						+"a.measuretype,a.customcode,b.custombc,b.sizeid,b.colorid,"
						+"b.cupid,c.name catname,d.name sizename,e.name colorname,"
						+"isnull(f.name,'') cupname,isnull(g.qty,0) qty,isnull(g.lockqty,0) lockqty "
						+"from ecs_item a with(nolock),ecs_itemsku b with(nolock),"
						+"ecs_category c with(nolock),ecs_size d with(nolock),"
						+"ecs_color e with(nolock),ecs_cup f with(nolock),"
						+"ecs_inventorybc g with(nolock) "
						+"where a.itemid=b.itemid and a.catid=c.catid "
						+"and b.sizeid=d.sizeid and b.colorid=e.colorid "
						+"and b.cupid*=f.cupid and b.skuid*=g.skuid "
						+"and a.customcode='"+inputValue+"'";
					isexists=true;
				}
				else
				{
					sql="select count(*) from ecs_item with(nolock) where customid='"+inputValue+"'";
					if (this.getDao().intSelect(sql)>0)
					{
						rsql="select a.itemid,b.skuid,b.customid skucustomid,"
							+"a.customid itemcustomid,a.itemname,"
							+"a.catid,a.status,a.unitname,a.baseprice,a.itemtypeid,"
							+"a.measuretype,a.customcode,b.custombc,b.sizeid,b.colorid,"
							+"b.cupid,c.name catname,d.name sizename,e.name colorname,"
							+"isnull(f.name,'') cupname,isnull(g.qty,0) qty,isnull(g.lockqty,0) lockqty "
							+"from ecs_item a with(nolock),ecs_itemsku b with(nolock),"
							+"ecs_category c with(nolock),ecs_size d with(nolock),"
							+"ecs_color e with(nolock),ecs_cup f with(nolock),"
							+"ecs_inventorybc g with(nolock) "
							+"where a.itemid=b.itemid and a.catid=c.catid "
							+"and b.sizeid=d.sizeid and b.colorid=e.colorid "
							+"and b.cupid*=f.cupid and b.skuid*=g.skuid "
							+"and a.customid='"+inputValue+"'";
						isexists=true;
					}
				}
			}
		}
		
		if (!isexists) throw new JException("未找到该商品资料!");
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(rsql)));		
	}
	
	public void getItems() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String inputValue=prop.getProperty("inputValue");
		String sqlwhere=prop.getProperty("sqlwhere");
		
		String sql="select a.itemid,b.skuid,b.customid skucustomid,"
			+"a.customid itemcustomid,a.itemname,"
			+"a.catid,a.status,a.unitname,a.baseprice,a.itemtypeid,"
			+"a.measuretype,a.customcode,b.custombc,b.sizeid,b.colorid,"
			+"b.cupid,c.name catname,d.name sizename,e.name colorname,"
			+"isnull(f.name,'') cupname,isnull(g.qty,0) qty,isnull(g.lockqty,0) lockqty "
			+"from ecs_item a with(nolock),ecs_itemsku b with(nolock),"
			+"ecs_category c with(nolock),ecs_size d with(nolock),"
			+"ecs_color e with(nolock),ecs_cup f with(nolock),"
			+"ecs_inventorybc g with(nolock) "
			+"where a.itemid=b.itemid and a.catid=c.catid "
			+"and b.sizeid=d.sizeid and b.colorid=e.colorid "
			+"and b.cupid*=f.cupid and b.skuid*=g.skuid "
			+"and (a.customcode like '%"+inputValue+"%' or a.itemname like '%"+inputValue+"%')"
			+sqlwhere
			+" order by a.customcode,b.colorid,b.sizeid,b.cupid";
	
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getCategory() throws Exception
	{
		String sql="select catid,name from ecs_category where catlevel=3 order by catid";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getBrand() throws Exception
	{
		String sql="select brandid,name from ecs_brand order by brandid";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	
	public void getSeason() throws Exception
	{
		String sql="select seasonid,name from ecs_season order by seasonid";
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
	}
	public void uploadImage() throws Exception
	{

		String currDir ="";
	
		int status=-1;
		String result="";
		String itemid="0";
		int flag=1;
		int i=0;
		String path="";
		
		ArrayList ldesc=new ArrayList();
		
		StringBuffer files=new StringBuffer();
			
		DiskFileItemFactory factory = new DiskFileItemFactory();  
		factory.setSizeThreshold(4096);

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(1000000);
		upload.setFileSizeMax(500000);
		upload.setHeaderEncoding("GBK");
		
		List items = upload.parseRequest(this.getRequest());

		Iterator iter = items.iterator();  
		while (iter.hasNext()) {  
		    FileItem item = (FileItem) iter.next();  
		  
		    if (item.isFormField()) {  
		        //如果是普通表单字段   
		        String name = item.getFieldName();  
		        String value = item.getString("GBK");  
	     
		        if (name.equalsIgnoreCase("customcode"))
		        {		 
		        	
		        	currDir=this.getRequest().getServletContext().getContext("/material").getRealPath("product/"+value);
		        	path=this.getRequest().getRequestURL().toString().replaceAll("/TinyWebServer", "") + "/material/product/"+value;
		    		
		    		File file=new File(currDir);
		    		
		    		
		    		if (!file.exists()) FileUtil.mkdir(file);
		        }
		        if (name.equalsIgnoreCase("itemid"))
		        	itemid=value;
		        if (name.indexOf("desc")>=0)
		        	ldesc.add(value);
		    } else {  
		        //如果是文件字段   
		    	
	            String filename =new String(item.getName().getBytes("GBK"));
	            	      	  
	            filename=filename.replace("\\", "/");

	            if (!FileUtil.getExtensionName(filename).equalsIgnoreCase("gif")
	            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("jpg")
	            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("bmp")
	            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("jpeg")
	            		&&!FileUtil.getExtensionName(filename).equalsIgnoreCase("png"))
	            	throw new JException("文件非图片格式:"+filename);
	            
	            String[] splits=filename.split("/");
	            	
	            String name=splits[splits.length-1];
	  
	            
	            File uploadedFile = new File(currDir+"/"+name);  
	            item.write(uploadedFile);   
	            
	            ImageUtil.makeSmallImage(currDir+"/"+name, currDir+"/"+"small-"+name);
	            
	            ECS_File ecsfile= new ECS_File();
	            ecsfile.setFileid(this.getDao().IDGenerator(ecsfile, "fileid"));
	            ecsfile.setName(ldesc.get(i).toString());
	    		
	            ecsfile.setPath(path+"/"+name);
	            ecsfile.setSmallpath(path+"/"+"small-"+name);
	            ecsfile.setFiletype(FileUtil.getExtensionName(filename));
	            ecsfile.setFilesize(Long.valueOf(item.getSize()).intValue());
	            ecsfile.setNote(ldesc.get(i).toString());
	            
	            this.getDao().insert(ecsfile);
	            
	            files.append("{\"itemid\":\""+itemid+"\",\"fileid\":\""+ecsfile.getFileid()+"\",\"description\":\""+ldesc.get(i).toString()+"\",\"flag\":\""+flag+"\"},");
	            
	            if (flag==1) flag=0;
	            
	            i=i+1;
	            
	            status=0;
		    }  
		}
		status=1;


		
		if (status==1)
			result="{\"success\":\"true\",\"status\":\""+status+"\"";
		else
			result="{\"success\":\"false\",\"status\":\""+status+"\"";
		
		if (files.toString().indexOf(",")>=0)
			result=result.concat(",\"data\":["+files.toString().substring(0, files.toString().length()-1)+"]");  //去掉最后一个逗号
		
		result=result.concat("}");
		
		this.OutputStr(result);
		
	}
	
	public void getProductImage() throws Exception
	{
		String reqdata = this.getReqData();		
		Properties prop=StringUtil.getIniProperties(reqdata);
		String fileid=prop.getProperty("fileid");
		
		String sql="select * from ecs_file where fileid="+fileid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void select() throws Exception
	{
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String itemid=prop.getProperty("itemid");

		
		String sql="select * from ecs_item with(nolock) where itemid="+itemid;
		this.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_itemorg with(nolock) where itemid="+itemid;
		this.itemorgofitems.setRelationData(this.getDao().multiRowSelect(sql));
	

		sql="select * from ecs_itemimage with(nolock) where itemid="+itemid;
		this.itemimageofitems.setRelationData(this.getDao().multiRowSelect(sql));
		
		sql="select * from ecs_itemsku with(nolock) where itemid="+itemid;
		this.itemskuofitems.setRelationData(this.getDao().multiRowSelect(sql));
		
		String s=this.toJSONObject();

		
		this.OutputStr(s);
	}
	
	private String makeBarcodeID(int itemid,int serialid,int itemtypeid) throws Exception
	{
		String outbarcodeid="";
		
		ECS_ItemType itemtype=new ECS_ItemType();
		
		String sql="select * from ecs_itemtype where id="+itemtypeid;
		
		itemtype.getMapData(this.getDao().oneRowSelect(sql));

		String s=StringUtil.replicate("0", 6-String.valueOf(itemid).length()).concat(String.valueOf(itemid));
		
		outbarcodeid=String.valueOf(itemtype.getBarcodehead()).trim().concat(s);
		

		
		String s1=StringUtil.replicate("0", 4-String.valueOf(serialid).length()).concat(String.valueOf(serialid));
		

		
		outbarcodeid=outbarcodeid.concat(s1);
		
		
		
		outbarcodeid=verifyBarcode(outbarcodeid);
		
		return outbarcodeid;
	}
	
	private String verifyBarcode(String in_sBarcode12) 
	{
		String sStr="";
		int nNum1 = Integer.valueOf(in_sBarcode12.substring(11,12)).intValue();
		int nNum2 = Integer.valueOf(in_sBarcode12.substring(9,10)).intValue();
		int nNum3 = Integer.valueOf(in_sBarcode12.substring(7,8)).intValue();
		int nNum4 = Integer.valueOf(in_sBarcode12.substring(5,6)).intValue();
		int nNum5 = Integer.valueOf(in_sBarcode12.substring(3,4)).intValue();
		int nNum6 = Integer.valueOf(in_sBarcode12.substring(1,2)).intValue();


		int nNum = ( nNum1+nNum2+nNum3+nNum4+nNum5+nNum6 )*3;
	  
		nNum1 = Integer.valueOf(in_sBarcode12.substring(10,11)).intValue();
		nNum2 = Integer.valueOf(in_sBarcode12.substring(8,9)).intValue();
		nNum3 = Integer.valueOf(in_sBarcode12.substring(6,7)).intValue();
		nNum4 = Integer.valueOf(in_sBarcode12.substring(4,5)).intValue();
		nNum5 = Integer.valueOf(in_sBarcode12.substring(2,3)).intValue();
		nNum6 = Integer.valueOf(in_sBarcode12.substring(0,1)).intValue();


		nNum = nNum+nNum1+nNum2+nNum3+nNum4+nNum5+nNum6;
		nNum1 = nNum/10;
		nNum = 10-(nNum - nNum1*10);
		if (nNum ==10)
		    sStr = "0";
		  else
			sStr=String.valueOf(nNum);

		String out_sBarCode13=in_sBarcode12.concat(sStr);
		
		return out_sBarCode13;
	}
	
	public void insert() throws Exception
	{
		this.getJSONData();
		this.itemid=this.getDao().IDGenerator(this, "itemid");
		this.creator=this.getUserInfo().getLogin();
		this.createtime=new Date(System.currentTimeMillis());
		this.updatetime=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();

		
		this.merchantid=this.getUserInfo().getMerchantid();

		//检查唯一性
		this.checkUnique(this);
		
		this.getDao().insert(this);
		


		for (int i=0;i<this.itemimageofitems.getRelationData().size();i++)
		{
			ECS_ItemImage itemimage=(ECS_ItemImage) this.itemimageofitems.getRelationData().get(i);
			itemimage.setItemid(this.itemid);
			this.getDao().insert(itemimage);
		}


		for (int i=0;i<this.itemorgofitems.getRelationData().size();i++)
		{
			ECS_ItemOrg itemorg=(ECS_ItemOrg) this.itemorgofitems.getRelationData().get(i);
			itemorg.setItemid(this.itemid);
			this.getDao().insert(itemorg);
		}


		for (int i=0;i<this.itemskuofitems.getRelationData().size();i++)
		{
			ECS_ItemSku itemsku=(ECS_ItemSku) this.itemskuofitems.getRelationData().get(i);
			itemsku.setSkuid(this.getDao().IDGenerator(itemsku, "skuid"));			
			this.checkUnique(itemsku);
			if (itemsku.getCustomid().equals("待编码") ||
					itemsku.getCustomid().equals("") )
				itemsku.setCustomid(this.makeBarcodeID(this.itemid, i+1, this.itemtypeid));
			
			itemsku.setItemid(this.itemid);
			this.getDao().insert(itemsku);		
		}
		

		
		String sql="select * from ecs_item where itemid="+this.itemid;
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	
	public void update() throws Exception
	{

		this.getJSONData();

		
		this.updatetime=new Date(System.currentTimeMillis());
		this.updator=this.getUserInfo().getLogin();	

		//检查唯一性
		this.checkUnique(this);
		
		this.getDao().update(this);
	
		
		
		String sql="delete from ecs_itemimage where itemid="+this.itemid;
		this.getDao().execute(sql);
		
		for (int i=0;i<this.itemimageofitems.getRelationData().size();i++)
		{
			ECS_ItemImage itemimage=(ECS_ItemImage) this.itemimageofitems.getRelationData().get(i);			
			itemimage.setItemid(this.itemid);
			this.getDao().insert(itemimage);
		}
		
	
		
		
		sql="delete from ecs_itemorg where itemid="+this.itemid;
		this.getDao().execute(sql);
		
		for (int i=0;i<this.itemorgofitems.getRelationData().size();i++)
		{
			ECS_ItemOrg itemorg=(ECS_ItemOrg) this.itemorgofitems.getRelationData().get(i);			
			itemorg.setItemid(this.itemid);
			this.getDao().insert(itemorg);
		}
	
		
		for (int i=0;i<this.itemskuofitems.getRelationData().size();i++)
		{
			boolean isAdd=false;
			ECS_ItemSku itemsku=(ECS_ItemSku) this.itemskuofitems.getRelationData().get(i);			
			if (itemsku.getSkuid()==0)
			{
				itemsku.setSkuid(this.getDao().IDGenerator(itemsku, "skuid"));
				isAdd=true;
			}
			
			this.checkUnique(itemsku);
			
			if (itemsku.getCustomid().equals("待编码") ||
					itemsku.getCustomid().equals("") )
				itemsku.setCustomid(this.makeBarcodeID(this.itemid, i+1, this.itemtypeid));
			
			itemsku.setItemid(this.itemid);
			
			if (isAdd)
				this.getDao().insert(itemsku);
			else
				this.getDao().update(itemsku);

		}
		

	
		sql="select * from ecs_item where itemid="+this.itemid;
		
		this.OutputStr(this.toJSONObject(this.getDao().oneRowSelect(sql)));
	}
	//下载各网店的商品
	public void downLoadProduct() throws Exception{
		String reqdata = this.getReqData();
		Properties prop =StringUtil.getIniProperties(reqdata);
	}
	
	//取商品的可用库存
	public String getItemStock() throws Exception{
		
		return "";
	}
	
	public void synStock() throws Exception
	{
		String reqdata = this.getReqData();	

		Properties prop=StringUtil.getIniProperties(reqdata);
		int orgid=Integer.valueOf(prop.getProperty("orgid")).intValue();
		String itemid=prop.getProperty("itemid");
		String synstyle=prop.getProperty("synstyle");
		String synvalue=prop.getProperty("synvalue");
		int isupdateconfig=Integer.valueOf(prop.getProperty("isupdateconfig")).intValue();
	

		String sql="select a.*,b.shortname,c.orgname from ecs_org_params a with(nolock),ecs_platform b with(nolock),ecs_org c with(nolock) "
			+"where a.platformid=b.platformid and a.orgid="+orgid+" and a.orgid=c.orgid";
		Hashtable htparams=this.getDao().oneRowSelect(sql);
		
		String platformname=htparams.get("shortname").toString();
		String orgname=htparams.get("orgname").toString();
		
		sql="select tradecontactid from ecs_tradecontactorgcontrast with(nolock) where orgid="+orgid;
		
		int tradecontactid=this.getDao().intSelect(sql);

		
		Log.info("同步部分库存:"+orgname);
		
		if (platformname.equals("taobao"))
		{
			updateTaobaoStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}
		else if (platformname.equals("360buy"))
		{
			updateJingdongStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}
		else if (platformname.equals("dangdang"))
		{
			updateDangdangStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}
		else if (platformname.equals("paipai"))
		{
			updatePaipaiStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}
		else if (platformname.equals("amazon"))
		{
			updateAmazonStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}
		else if (platformname.equals("taobaofenxiao"))
		{
			updateTaobaoFenXiaoStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}
		else if (platformname.equals("val"))
		{
			updateValStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}else if (platformname.equals("yhd"))
		{
			updateYhdStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}else if (platformname.equals("lefeng"))
		{
			updateLefengStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}else if(platformname.equals("coo8")){
			updatecoo8Stock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}else if(platformname.equals("suning")){
			updateSuningStock(orgid,orgname,tradecontactid,htparams,itemid,synstyle,synvalue);
		}		
		if (isupdateconfig==1)
		{
			sql="update ecs_stockconfig set isneedsyn=1 where orgid="+orgid+" and itemid='"+itemid+"'";
			this.getDao().execute(sql);
		}

	}
	private void updateSuningStock(int orgid, String orgname,
			int tradecontactid, Hashtable htparams, String itemid2,
			String synstyle, String synvalue) throws Exception{
		
		String url = htparams.get("url").toString();
		String appKey = htparams.get("appkey").toString();
		String secretKey = htparams.get("appsecret").toString();
		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid2+"'";
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		
		Log.info("商品ID:"+stockconfig.getItemid()+" 货号:"+stockconfig.getItemcode());
		
		sql="select count(*) from ecs_stockconfigsku with(nolock) where orgId="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		if (this.getDao().intSelect(sql)==0)    //如果商品在ecs_stockconfigsku中没有对应的记录，则它会直接在条形码表中有相应的记录，
		{
			Log.info(orgname,"货号:"+stockconfig.getItemcode()+" 原库存:"+stockconfig.getStockcount());
			boolean ismulti=false;
			boolean isfind=true;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfig.getItemcode()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfig.getItemcode()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfig.getItemcode()+"】对应的条码,商品标题:"+stockconfig.getTitle());	
		
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfig.getItemcode()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgId,itemid");
					isfind=false;
				}
				else							
					ismulti=true;
			}

			int qty =0;
			
			if (isfind)
			{
				if (ismulti)
				{
					int minqty=1000000;
					sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfig.getItemcode()+"'";
					Vector multiskulist=this.getDao().multiRowSelect(sql);
					for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
					{
						Hashtable skuref=(Hashtable) itmulti.next();
						String customercode= skuref.get("customercode").toString();
						double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
						qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
						
						qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
						
						if (qty<minqty)
						{
							minqty=qty;
						}
					}
					
					qty=minqty;
				}
				else
				{
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfig.getItemcode());
				}
			}
				
			if (qty<0) qty=0;
			
			if (synstyle.equals("2"))   //可用库存加上某个数
			{	
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			
			if (qty<0) qty=0;

			com.wofu.ecommerce.suning.StockUtils.updateItemStock(orgname,this.getDao(),orgid,url,"josn",stockconfig,appKey,secretKey,qty);
				
		}
		
		else     
			
		{
			sql="select * from ecs_stockconfigsku with(nolock) where orgId="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
			
			Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
			int skuSize= vtstockconfigsku.size();
			for(int j=0;j<skuSize;j++)
			{
				try{
					Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
					
					ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
					stockconfigsku.getMapData(htstockconfigsku);
							
					Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
						
					boolean ismulti=false;
					boolean isfind=true;
					
					sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
					if (this.getDao().intSelect(sql)==0)
					{
						sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
						if (this.getDao().intSelect(sql)==0)
						{
							Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());	
							stockconfigsku.setErrflag(1);
							stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
							this.getDao().updateByKeys(stockconfigsku, "orgId,itemid,skuid");
							
							stockconfig.setErrflag(1);
							stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
							this.getDao().updateByKeys(stockconfig, "orgId,itemid");
							
							isfind=false;
						}else								
							ismulti=true;
					}
					
					int qty =0;
					
					
					if(isfind)
					{
						if (ismulti)
						{
							int minqty=1000000;
							sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
							Vector multiskulist=this.getDao().multiRowSelect(sql);
							for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
							{
								Hashtable skuref=(Hashtable) itmulti.next();
								String customercode= skuref.get("customercode").toString();
								double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
								qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
								
								qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
								
								if (qty<minqty)
								{
									minqty=qty;  
								}
							}
							
							qty=minqty;
						}
						else
						{	
							
							qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
						}
					}
						
					if (synstyle.equals("2"))   //可用库存加上某个数
					{	
						if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
						{
							qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
						}
						else
							qty=qty+Integer.valueOf(synvalue).intValue();
					}
					else if (synstyle.equals("3"))   //按配置
					{
						int addstockqty=0;
						if(Math.abs(stockconfig.getAddstockqty())<1)
							addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
						else
							addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
						
						//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
						if ((qty+addstockqty)<=stockconfig.getAlarmqty())
						{
							qty=0;
						}
						else
						{
							qty=qty+addstockqty;
						}
					}
					
					if (qty<0) qty=0;
					if(j==skuSize-1){   //最后一个sku把主货号的库存也一起更新
						com.wofu.ecommerce.suning.StockUtils.updateSkuStock(orgname,this.getDao(),orgid,url,"json",appKey,secretKey,stockconfig,stockconfigsku,qty,true);
					}else{
						com.wofu.ecommerce.suning.StockUtils.updateSkuStock(orgname,this.getDao(),orgid,url,"json",appKey,secretKey,stockconfig,stockconfigsku,qty,false);
					}
				}catch(Exception ex){
					if(this.getDao().getConnection()!=null && !this.getDao().getConnection().getAutoCommit())
						this.getDao().getConnection().rollback();
					Log.error(orgname, ex.getMessage());
					continue;
				}
				
				
			}
		}
		
		
	}

	//更新库巴库存
	private void updatecoo8Stock(int orgid, String orgname, int tradecontactid,
			Hashtable htparams, String itemid2, String synstyle, String synvalue) throws Exception{
		String url = htparams.get("url").toString();
		String appKey = htparams.get("appkey").toString();
		String secretKey = htparams.get("appsecret").toString();
		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid2+"'";
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		
		Log.info("商品ID:"+stockconfig.getItemid()+" 货号:"+stockconfig.getItemcode());
					
									
		if (stockconfig.getIsneedsyn()==0)
			{
					Log.info(orgname,"配置不需要同步库存,货号:"+stockconfig.getItemcode());
					return;  //不需要同步
			}
	
				sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
				
				Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
				
				for(int j=0;j<vtstockconfigsku.size();j++)
				{
						Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
						
						ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
						stockconfigsku.getMapData(htstockconfigsku);
								
						Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
							
						boolean ismulti=false;
						
						sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
						if (this.getDao().intSelect(sql)==0)
						{
							sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
							if (this.getDao().intSelect(sql)==0)
							{
								Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());	
								stockconfigsku.setErrflag(1);
								stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
								this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
								
								stockconfig.setErrflag(1);
								stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
								this.getDao().updateByKeys(stockconfig, "orgid,itemid");
								
								continue;
							}
							
							ismulti=true;
						}
						
						int qty =0;
						
						
						if (ismulti)
						{
							int minqty=1000000;
							sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
							Vector multiskulist=this.getDao().multiRowSelect(sql);
							for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
							{
								Hashtable skuref=(Hashtable) itmulti.next();
								String customercode= skuref.get("customercode").toString();
								double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
								qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
								
								qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
								
								if (qty<minqty)
								{
									minqty=qty;
								}
							}
							
							qty=minqty;
						}
						else
						{
							qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
						}
						
						if (synstyle.equals("2"))   //可用库存加上某个数
						{
							if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
							{
								qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
							}
							else
								qty=qty+Integer.valueOf(synvalue).intValue();
						}
						else if (synstyle.equals("3"))   //按配置
						{
							int addstockqty=0;
							if(Math.abs(stockconfig.getAddstockqty())<1)
								addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
							else
								addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
							
							//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
							if ((qty+addstockqty)<=stockconfig.getAlarmqty())
							{
								qty=0;
							}
							else
							{
								qty=qty+addstockqty;
							}
						}
						
						if (qty<0) qty=0;	
				
						com.wofu.ecommerce.coo8.StockUtil.updateStock(this.getDao(),stockconfigsku,stockconfig,url,appKey,secretKey,qty);
					
					
				}
			
			
	
		
	}

	private void updateYhdStock(int orgid,String orgname,int tradecontactid,Map params,
			String itemid,String synstyle,String synvalue) throws Exception
	{
		String url = params.get("url").toString() ;
		String app_key=params.get("appkey").toString();
		String app_secret=params.get("appsecret").toString();
		String token=params.get("token").toString();
		String format="json";		
		String ver="1.0";
		
		StringBuffer updateItemsXML=new StringBuffer();

		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		
		Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
		
		for(int j=0;j<vtstockconfigsku.size();j++)
		{
			Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
			
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(htstockconfigsku);
					
			Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
				
			boolean ismulti=false;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
					
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					
					continue;
				}
				
				ismulti=true;
			}
			
			int qty =0;
			
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
			}
		
			if (synstyle.equals("2"))   //可用库存加上某个数
			{
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
				
			
			if (qty<0) qty=0;	
	
			
			Object[] skuid=StringUtil.split(stockconfigsku.getSkuid(),"-").toArray();
			
										
			updateItemsXML.append(stockconfigsku.getSku()+":"+String.valueOf(skuid[1])+":"+qty+",");

		}
		
		String outerstocklist=updateItemsXML.toString();
		outerstocklist=outerstocklist.substring(0, outerstocklist.length()-1);
		com.wofu.ecommerce.yhd.StockUtils.batchUpdateStock(this.getDao(),orgid,url, app_key,token,app_secret,
			format,outerstocklist,ver,1);
	
	}
	
	private void updateValStock(int orgid,String orgname,int tradecontactid,Map params,
			String itemid,String synstyle,String synvalue) throws Exception
	{
		
		String supplierid = params.get("uname").toString() ;
		String passWord = params.get("pwd").toString() ;
		String URI = params.get("url").toString() ;

		String swsSupplierID = params.get("swsSupplierID").toString() ;
		String strkey = params.get("decryptkey").toString();

		String striv = params.get("decryptRandomCode").toString() ;
		String wsurl = params.get("webserviceurl").toString() ;
	
		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		
		Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
		
		for(int j=0;j<vtstockconfigsku.size();j++)
		{
			Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
			
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(htstockconfigsku);
					
			Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
				
			boolean ismulti=false;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
					
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					
					continue;
				}
				
				ismulti=true;
			}
			
			int qty =0;
			
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
			}
		
			if (synstyle.equals("2"))   //可用库存加上某个数
			{
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			
			if (qty<0) qty=0;	
	
			
										
			com.wofu.ecommerce.vjia.StockUtils.updateStock(this.getDao(),URI,wsurl,supplierid,
					passWord,swsSupplierID,strkey,striv,stockconfig,stockconfigsku,qty);
		}
		
	}
	
	private void updateTaobaoStock(int orgid,String orgname,int tradecontactid,Map params,String itemid,String synstyle,String synvalue) throws Exception
	{
		//店铺同步比例
		double synrate=1;
		String url=params.get("url").toString();
		String appkey=params.get("appkey").toString();
		String appsecret=params.get("appsecret").toString();
		String authcode=params.get("token").toString();

		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		//店铺同步比例表
		sql  = "select synrate from ecs_shopglobalconfig where shopOrgId="+orgid;
		String temp = this.getDao().strSelect(sql);
		if(!"".equals(temp))
		synrate = Float.parseFloat(temp);
		
		sql="select count(*) from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		if (this.getDao().intSelect(sql)==0)
		{
			Log.info(orgname,"货号:"+stockconfig.getItemcode()+" 原库存:"+stockconfig.getStockcount());
			
			boolean ismulti=false;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfig.getItemcode()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfig.getItemcode()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfig.getItemcode()+"】对应的条码,商品标题:"+stockconfig.getTitle());	
		
					/**
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfig.getItemcode()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					**/
					this.getDao().deleteByKeys(stockconfig, "orgid,itemid");
					return;
				}
				
				ismulti=true;
			}


			int qty =0;
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfig.getItemcode()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfig.getItemcode());
			}
			Log.info("qty: "+qty);
			if (synstyle.equals("2"))   //可用库存加上某个数
			{	
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
			
			if (qty<0) qty=0;
			
			
			com.wofu.ecommerce.taobao.StockUtils.updateItemStock(this.getDao(),url,appkey,appsecret,authcode,stockconfig,qty,1);
		}
		else
		{
			sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
			
			Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
			
			for(int j=0;j<vtstockconfigsku.size();j++)
			{
				Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
				
				ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
				stockconfigsku.getMapData(htstockconfigsku);
						
				Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
					
				boolean ismulti=false;
				
				sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
					if (this.getDao().intSelect(sql)==0)
					{
						Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
						/**
						stockconfigsku.setErrflag(1);
						stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
						this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
						*/
						this.getDao().deleteByKeys(stockconfigsku, "orgid,itemid,skuid");
						stockconfig.setErrflag(1);
						stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
						this.getDao().updateByKeys(stockconfig, "orgid,itemid");
						
						continue;
					}
					
					ismulti=true;
				}
				
				int qty =0;
				
			
				if (ismulti)
				{
					int minqty=1000000;
					sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
					Vector multiskulist=this.getDao().multiRowSelect(sql);
					for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
					{
						Hashtable skuref=(Hashtable) itmulti.next();
						String customercode= skuref.get("customercode").toString();
						double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
						qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
						
						qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
						
						if (qty<minqty)
						{
							minqty=qty;
						}
					}
					
					qty=minqty;
				}
				else
				{
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
				}
				Log.info("sku: "+stockconfigsku.getSku()+" 的可用库存为: "+qty);
				

				
				if (synstyle.equals("2"))   //可用库存加上某个数
				{
					if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
					{
						qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
					}
					else
						qty=qty+Integer.valueOf(synvalue).intValue();
				}
				else if (synstyle.equals("3"))   //按配置
				{
					int addstockqty=0;
					if(Math.abs(stockconfig.getAddstockqty())<1)
						addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
					else
						addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
					
					//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
					if ((qty+addstockqty)<=stockconfig.getAlarmqty())
					{
						qty=0;
					}
					else
					{
						qty=qty+addstockqty;
					}
				}
				qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
				
				if (qty<0) qty=0;	
				com.wofu.ecommerce.taobao.StockUtils.updateSkuStock(this.getDao(),url,appkey,appsecret,authcode,stockconfig,stockconfigsku,qty,1);
			}
		
		}
	}
	

	private void updateTaobaoFenXiaoStock(int orgid,String orgname,int tradecontactid,Map params,
			String itemid,String synstyle,String synvalue) throws Exception
	{
		String url=params.get("url").toString();
		String appkey=params.get("appkey").toString();
		String appsecret=params.get("appsecret").toString();
		String authcode=params.get("token").toString();
		//店铺同步比例
		double synrate=1;

		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		//店铺同步比例表
		sql  = "select synrate from ecs_shopglobalconfig where shopOrgId="+orgid;
		String temp = this.getDao().strSelect(sql);
		if(!"".equals(temp))
		synrate = Float.parseFloat(temp);
		
		sql="select count(*) from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		if (this.getDao().intSelect(sql)==0)
		{

			Log.info(orgname,"货号:"+stockconfig.getItemcode()+" 原库存:"+stockconfig.getStockcount());
			
			boolean ismulti=false;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfig.getItemcode()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfig.getItemcode()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfig.getItemcode()+"】对应的条码,商品标题:"+stockconfig.getTitle());	
		
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfig.getItemcode()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					return;
				}
				
				ismulti=true;
			}


			int qty =0;
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfig.getItemcode()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfig.getItemcode());
			}
			
			if (synstyle.equals("2"))   //可用库存加上某个数
			{
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			Log.info("old qty: "+qty);
			qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
			Log.info("new qty: "+qty);
			
			if (qty<0) qty=0;
			
			
			com.wofu.ecommerce.taobao.StockUtils.updateDistributionItemStock(this.getDao(),url,appkey,appsecret,authcode,stockconfig,qty);
		}
		else
		{
			sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
			
			Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
			
			for(int j=0;j<vtstockconfigsku.size();j++)
			{
				Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
				
				ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
				stockconfigsku.getMapData(htstockconfigsku);
						
				Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
					
				boolean ismulti=false;
				
				sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
					if (this.getDao().intSelect(sql)==0)
					{
						Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
						
						stockconfigsku.setErrflag(1);
						stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
						this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
						
						stockconfig.setErrflag(1);
						stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
						this.getDao().updateByKeys(stockconfig, "orgid,itemid");
						
						continue;
					}
					
					ismulti=true;
				}
				
				int qty =0;
				
				
				if (ismulti)
				{
					int minqty=1000000;
					sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
					Vector multiskulist=this.getDao().multiRowSelect(sql);
					for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
					{
						Hashtable skuref=(Hashtable) itmulti.next();
						String customercode= skuref.get("customercode").toString();
						double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
						qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
						
						qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
						
						if (qty<minqty)
						{
							minqty=qty;
						}
					}
					
					qty=minqty;
				}
				else
				{
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
				}
			
				if (synstyle.equals("2"))   //可用库存加上某个数
				{
					if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
					{
						qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
					}
					else
						qty=qty+Integer.valueOf(synvalue).intValue();
				}
				else if (synstyle.equals("3"))   //按配置
				{
					int addstockqty=0;
					if(Math.abs(stockconfig.getAddstockqty())<1)
						addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
					else
						addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
					
					//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
					if ((qty+addstockqty)<=stockconfig.getAlarmqty())
					{
						qty=0;
					}
					else
					{
						qty=qty+addstockqty;
					}
				}
				Log.info("old qty: "+qty);
				qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
				Log.info("new qty: "+qty);
				if (qty<0) qty=0;	
										
				com.wofu.ecommerce.taobao.StockUtils.updateDistributionSkuStock(this.getDao(),url,appkey,appsecret,authcode,stockconfig,stockconfigsku,qty);
			}
		
		}
		
	}
	
	
	private void updateJingdongStock(int orgid,String orgname,int tradecontactid,Map params,
			String itemid,String synstyle,String synvalue) throws Exception 
	{
		//店铺同步比例
		double synrate=1;
		String url=params.get("url").toString();
		String appkey=params.get("appkey").toString();

		String appsecret=params.get("appsecret").toString();
		String token=params.get("token").toString();
		
		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		//店铺同步比例表
		sql  = "select synrate from ecs_shopglobalconfig where shopOrgId="+orgid;
		String temp = this.getDao().strSelect(sql);
		if(!"".equals(temp))
		synrate = Float.parseFloat(temp);

		sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		
		Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
		
		for(int j=0;j<vtstockconfigsku.size();j++)
		{
			Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
			
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(htstockconfigsku);
					
			Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
				
			boolean ismulti=false;
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
					
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					
					continue;
				}
				
				ismulti=true;
			}
			
			int qty =0;
			
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
			}
		
			if (synstyle.equals("2"))   //可用库存加上某个数
			{
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			Log.info("old qty: "+qty);
			qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
			Log.info("new qty: "+qty);
			if (qty<0) qty=0;	
									
			com.wofu.ecommerce.jingdong.StockUtils.updateStock(this.getDao(),url,token,appkey,appsecret,stockconfig,stockconfigsku,qty);
		}
		
	}
	
	private void updateDangdangStock(int orgid,String orgname,int tradecontactid,Map params,
			String itemid,String synstyle,String synvalue) throws Exception
	{
		//店铺同步比例
		double synrate=1;
		String url=params.get("url").toString();
		String encoding=params.get("encoding").toString();
		String session=params.get("token").toString();
		String app_key=params.get("appkey").toString();
		String app_Secret=params.get("appsecret").toString();
		
		StringBuffer updateItemsXML=new StringBuffer();

		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		//店铺同步比例表
		sql  = "select synrate from ecs_shopglobalconfig where shopOrgId="+orgid;
		String temp = this.getDao().strSelect(sql);
		if(!"".equals(temp))
		synrate = Float.parseFloat(temp);

		sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		
		Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
		
		for(int j=0;j<vtstockconfigsku.size();j++)
		{
			Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
			
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(htstockconfigsku);
					
			Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
				
			boolean ismulti=false;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
					
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					
					continue;
				}
				
				ismulti=true;
			}
			
			int qty =0;
			
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
			}
		
			if (synstyle.equals("2"))   //可用库存加上某个数
			{
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			Log.info("old qty: "+qty);
			qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
			Log.info("new qty: "+qty);
			if (qty<0) qty=0;	
	
			
			updateItemsXML.append("<ItemUpadteInfo>");							
			updateItemsXML.append("<outerItemID>"+stockconfigsku.getSku()+"</outerItemID>");
			updateItemsXML.append("<stockCount>"+qty+"</stockCount>");
			updateItemsXML.append("</ItemUpadteInfo>");	
			if(j>=30){
				submitDangdangData(orgid,url,encoding,updateItemsXML.toString(),session,app_key,app_Secret);
				updateItemsXML.delete(0, updateItemsXML.length());
			}
			
		}
		if (updateItemsXML.length()>0){
			submitDangdangData(orgid,url,encoding,updateItemsXML.toString(),session,app_key,app_Secret);
		}
			
		
	}
	
	private void submitDangdangData(int orgid,String url,String encoding,String updateItemsstr,String session,String app_key,String app_Secret) throws Exception
	{
		StringBuffer updateItemsXML=new StringBuffer();
		updateItemsXML.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		updateItemsXML.append("<request><functionID>updateMultiItemsStock</functionID>");
		updateItemsXML.append("<time>"+Formatter.format(new Date(), Formatter.DATE_TIME_FORMAT)+"</time>");
		updateItemsXML.append("<ItemsList>");
		updateItemsXML.append(updateItemsstr);
		updateItemsXML.append("</ItemsList>");
		updateItemsXML.append("</request>");
		com.wofu.ecommerce.dangdang.StockUtils.batchUpdateStock(this.getDao().getConnection(),orgid,url,updateItemsXML.toString(),encoding,session,app_key,app_Secret);
	}
	
	private void updatePaipaiStock(int orgid,String orgname,int tradecontactid,Map params,String itemid,String synstyle,String synvalue) throws Exception
	{
		//店铺同步比例
		double synrate=1;
		String url=params.get("url").toString();
		String spid=params.get("sellerid").toString();
		String secretkey=params.get("secretaccesskey").toString();
		String token=params.get("token").toString();
		String uid=params.get("uid").toString();
		String encoding=params.get("encoding").toString();

		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		//店铺同步比例表
		sql  = "select synrate from ecs_shopglobalconfig where shopOrgId="+orgid;
		String temp = this.getDao().strSelect(sql);
		if(!"".equals(temp))
		synrate = Float.parseFloat(temp);

		sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		
		Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
		
		for(int j=0;j<vtstockconfigsku.size();j++)
		{
			Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
			
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(htstockconfigsku);
					
			Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
				
			boolean ismulti=false;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
					
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					
					continue;
				}
				
				ismulti=true;
			}
			
			int qty =0;
			
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
			}
		
			if (synstyle.equals("2"))   //可用库存加上某个数
			{
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			Log.info("old qty: "+qty);
			qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
			Log.info("new qty: "+qty);
			if (qty<0) qty=0;	
									
			com.wofu.ecommerce.oauthpaipai.StockUtils.updateStock(this.getDao(),spid,secretkey,token,uid,encoding,stockconfig,stockconfigsku,qty);
		}
		
	}
	
	private void updateAmazonStock(int orgid,String orgname,int tradecontactid,Map params,String itemid,String synstyle,String synvalue) throws Exception
	{
		//店铺同步比例
		double synrate=1;
		String serviceurl=params.get("url").toString();
		String accesskeyid=params.get("accesskeyid").toString();
		String secretaccesskey=params.get("secretaccesskey").toString();
		String applicationname=params.get("applicationname").toString();
		String applicationversion=params.get("applicationversion").toString();
		String sellerid=params.get("sellerid").toString();
		String marketplaceid=params.get("marketplaceid").toString();
		//店铺同步比例表
		String sql  = "select synrate from ecs_shopglobalconfig where shopOrgId="+orgid;
		String temp = this.getDao().strSelect(sql);
		if(!"".equals(temp))
		synrate = Float.parseFloat(temp);
		Vector<Map> inventoryitems=new Vector<Map>();
		
		sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		
		Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
		
		for(int j=0;j<vtstockconfigsku.size();j++)
		{
			Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
			
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(htstockconfigsku);
					
			Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
				
			boolean ismulti=false;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
					
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					
					continue;
				}
				
				ismulti=true;
			}
			
			int qty =0;
			
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
			}
		
			if (synstyle.equals("2"))   //可用库存加上某个数
			{
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			Log.info("old qty: "+qty);
			qty = Double.valueOf(Math.floor(qty*synrate)).intValue();
			Log.info("new qty: "+qty);
			
			if (qty<0) qty=0;	
			
			Hashtable inventoryitem=new Hashtable();
			inventoryitem.put("sku",stockconfigsku.getSku());
			inventoryitem.put("qty", String.valueOf(qty));
			
			inventoryitems.add(inventoryitem);
											
		}
		
		com.wofu.ecommerce.amazon.StockUtils.updateStock(this.getDao(),orgid,serviceurl, accesskeyid, secretaccesskey, 
				applicationname, applicationversion, 
				sellerid, marketplaceid, inventoryitems);
	}
	
	private void updateLefengStock(int orgid,String orgname,int tradecontactid,Map params,String itemid,String synstyle,String synvalue) throws Exception
	{
		String shopid=params.get("gshopid").toString();
		String url=params.get("url").toString();
		String secretkey=params.get("secretaccesskey").toString();
		String encoding=params.get("encoding").toString();

		
		Vector<Map> inventoryitems=new Vector<Map>();
		
		String sql="select * from ecs_stockconfig with(nolock) where orgid="+orgid+" and itemid='"+itemid+"'";
		
		ECS_StockConfig stockconfig=new ECS_StockConfig();
		
		stockconfig.getMapData(this.getDao().oneRowSelect(sql));
		

		sql="select * from ecs_stockconfigsku with(nolock) where orgid="+orgid+" and itemid='"+stockconfig.getItemid()+"'";
		
		Vector vtstockconfigsku=this.getDao().multiRowSelect(sql);
		
		for(int j=0;j<vtstockconfigsku.size();j++)
		{
			Hashtable htstockconfigsku=(Hashtable) vtstockconfigsku.get(j);
			
			ECS_StockConfigSku stockconfigsku=new ECS_StockConfigSku();
			stockconfigsku.getMapData(htstockconfigsku);
					
			Log.info(orgname,"SKU:"+stockconfigsku.getSku()+" 原库存:"+stockconfigsku.getStockcount());
				
			boolean ismulti=false;
			
			sql="select count(*) from barcode with(nolock) where custombc='"+stockconfigsku.getSku()+"'";
			if (this.getDao().intSelect(sql)==0)
			{
				sql="select count(*) from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				if (this.getDao().intSelect(sql)==0)
				{
					Log.warn(orgname,"找不到SKU【"+stockconfigsku.getSku()+"】对应的条码,商品标题:"+stockconfig.getTitle());		
					
					stockconfigsku.setErrflag(1);
					stockconfigsku.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfigsku, "orgid,itemid,skuid");
					
					stockconfig.setErrflag(1);
					stockconfig.setErrmsg("找不到SKU【"+stockconfigsku.getSku()+"】对应的条码");
					this.getDao().updateByKeys(stockconfig, "orgid,itemid");
					
					continue;
				}
				
				ismulti=true;
			}
			
			int qty =0;
			
			
			if (ismulti)
			{
				int minqty=1000000;
				sql="select customercode,qty from MultiSKURef where refcustomercode='"+stockconfigsku.getSku()+"'";
				Vector multiskulist=this.getDao().multiRowSelect(sql);
				for(Iterator itmulti=multiskulist.iterator();itmulti.hasNext();)
				{
					Hashtable skuref=(Hashtable) itmulti.next();
					String customercode= skuref.get("customercode").toString();
					double skurefqty= Double.valueOf(skuref.get("qty").toString()).doubleValue();
					qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(),customercode);
					
					qty=(Double.valueOf(Math.ceil(qty/skurefqty))).intValue();
					
					if (qty<minqty)
					{
						minqty=qty;
					}
				}
				
				qty=minqty;
			}
			else
			{
				qty=StockManager.getTradeContactUseableStock(this.getDao().getConnection(), Integer.valueOf(tradecontactid).intValue(), stockconfigsku.getSku());
			}
		
			if (synstyle.equals("2"))   //可用库存加上某个数
			{
				if (Math.abs(Double.valueOf(synvalue).doubleValue())<1)
				{
					qty=qty+Double.valueOf(Math.floor(qty*Double.valueOf(synvalue).doubleValue())).intValue();
				}
				else
					qty=qty+Integer.valueOf(synvalue).intValue();
			}
			else if (synstyle.equals("3"))   //按配置
			{
				int addstockqty=0;
				if(Math.abs(stockconfig.getAddstockqty())<1)
					addstockqty=Double.valueOf(Math.floor(qty*stockconfig.getAddstockqty())).intValue();
				else
					addstockqty=Double.valueOf(stockconfig.getAddstockqty()).intValue();
				
				//如果可用库存加上需增加的库存小于等于警戒库存,则将库存同步为0
				if ((qty+addstockqty)<=stockconfig.getAlarmqty())
				{
					qty=0;
				}
				else
				{
					qty=qty+addstockqty;
				}
			}
			
			if (qty<0) qty=0;	
			
			com.wofu.ecommerce.lefeng.StockUtils.updateStock(this.getDao(),url, shopid, secretkey, encoding, stockconfig, stockconfigsku,qty, 0);
											
		}
		

	}
	
	
	public int getItemid() {
		return itemid;	
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public String getItemname() {
		return itemname;	
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}


	public String getCustomcode() {
		return customcode;	
	}
	public void setCustomcode(String customcode) {
		this.customcode = customcode;
	}

	public int getItemtypeid() {
		return itemtypeid;
	}
	public void setItemtypeid(int itemtypeid) {
		this.itemtypeid = itemtypeid;
	}
	public int getCatid() {
		return catid;	
	}
	public void setCatid(int catid) {
		this.catid = catid;
	}
	public int getBrandid() {
		return brandid;	
	}
	public void setBrandid(int brandid) {
		this.brandid = brandid;
	}
	public int getStid() {
		return stid;	
	}
	public void setStid(int stid) {
		this.stid = stid;
	}
	public int getKindid() {
		return kindid;	
	}
	public void setKindid(int kindid) {
		this.kindid = kindid;
	}
	public int getSeasonid() {
		return seasonid;	
	}
	public void setSeasonid(int seasonid) {
		this.seasonid = seasonid;
	}
	public String getSeriesid() {
		return seriesid;	
	}
	public void setSeriesid(String seriesid) {
		this.seriesid = seriesid;
	}
	public int getProjectid() {
		return projectid;	
	}
	public void setProjectid(int projectid) {
		this.projectid = projectid;
	}
	public String getDesigner() {
		return designer;	
	}
	public void setDesigner(String designer) {
		this.designer = designer;
	}
	public int getYearid() {
		return yearid;	
	}
	public void setYearid(int yearid) {
		this.yearid = yearid;
	}
	public String getOrigin() {
		return origin;	
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public int getMaterialid() {
		return materialid;	
	}
	public void setMaterialid(int materialid) {
		this.materialid = materialid;
	}
	public int getMeasuretype() {
		return measuretype;	
	}
	public void setMeasuretype(int measuretype) {
		this.measuretype = measuretype;
	}
	public double getBaseprice() {
		return baseprice;	
	}
	public void setBaseprice(double baseprice) {
		this.baseprice = baseprice;
	}
	public double getContractcost() {
		return contractcost;	
	}
	public void setContractcost(double contractcost) {
		this.contractcost = contractcost;
	}
	public String getUnitname() {
		return unitname;	
	}
	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}
	public int getStatus() {
		return status;	
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreator() {
		return creator;	
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreatetime() {
		return createtime;	
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getUpdator() {
		return updator;	
	}
	public void setUpdator(String updator) {
		this.updator = updator;
	}
	public Date getUpdatetime() {
		return updatetime;	
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public int getMerchantid() {
		return merchantid;	
	}
	public void setMerchantid(int merchantid) {
		this.merchantid = merchantid;
	}
	public String getCustomid() {
		return customid;
	}
	public void setCustomid(String customid) {
		this.customid = customid;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public DataRelation getItemimageofitems() {
		return itemimageofitems;
	}

	public void setItemimageofitems(DataRelation itemimageofitems) {
		this.itemimageofitems = itemimageofitems;
	}

	public DataRelation getItemorgofitems() {
		return itemorgofitems;
	}

	public void setItemorgofitems(DataRelation itemorgofitems) {
		this.itemorgofitems = itemorgofitems;
	}

	public DataRelation getItemskuofitems() {
		return itemskuofitems;
	}

	public void setItemskuofitems(DataRelation itemskuofitems) {
		this.itemskuofitems = itemskuofitems;
	}

}
