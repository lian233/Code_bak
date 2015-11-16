package com.wofu.base.util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import com.wofu.base.dbmanager.DataCentre;
import com.wofu.base.dbmanager.ECSDao;
import com.wofu.base.systemmanager.UserInfo;
import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.common.service.Params;
import com.wofu.common.tools.conv.Convert;
import com.wofu.common.tools.sql.PoolHelper;
import com.wofu.common.tools.util.FileUtil;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.StreamUtil;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.log.Log;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class BusinessObject implements BusinessClass {

	private HttpServletRequest request = null;

	private HttpServletResponse response = null;

	private Connection connection = null;

	private Connection extconnection = null;

	private DataCentre dao = null;

	private DataCentre extdao = null;

	private UserInfo userinfo = null;

	protected String extdsname;
	
	private int moduleid;
	
	protected String searchOrderFieldName = ""; // 搜索排序字段
	protected String orderMode="asc";					//排序

	protected String exportQuerySQL = ""; // 导出查询sql

	protected String uniqueFields1 = "";// 唯一性字段
	
	protected String uniqueFields2 = "";// 唯一性字段
	
	protected String uniqueFields3 = "";// 唯一性字段
	
	
	public int getModuleid() {
		return moduleid;
	}

	public void setModuleid(int moduleid) {
		this.moduleid = moduleid;
	}

	public String getUniqueFields1()
	{
		return this.uniqueFields1;
	}
	
	public String getUniqueFields2()
	{
		return this.uniqueFields2;
	}

	
	public String getUniqueFields3()
	{
		return this.uniqueFields3;
	}
	
	public void batchCheckUnique() throws Exception
	{
		String reqdata = this.getReqData();
				
		JSONArray jsonarr = new JSONArray(reqdata);
		
		String tablename = this.getClass().getSimpleName();

		String sql = "select count(*) from " + tablename + " with(nolock) where 1=1 ";
				
		Vector existdata=new Vector();
		
		for(int i=0;i<jsonarr.length();i++)
		{
			JSONObject jo=jsonarr.getJSONObject(i);
			
			Hashtable ht=new Hashtable();
			StringBuffer sqlwhere=new StringBuffer();
			
			for (Iterator it = jo.keys(); it.hasNext();) {
								
				String fieldname = (String) it.next();
				
				String fieldvalue=jo.getString(fieldname);
				
				sqlwhere.append(" and ");
				sqlwhere.append(fieldname);
				sqlwhere.append("='");
				sqlwhere.append(fieldvalue);
				sqlwhere.append("'");
				
				ht.put(fieldname, fieldvalue);
				
			}
			
			if(this.getDao().intSelect(sql+sqlwhere)>0)
				existdata.add(ht);
		}
		
		
		this.OutputStr(this.toJSONArray(existdata));
		
	}

	public void checkUnique(BusinessClass obj) throws Exception {
		if (!obj.getUniqueFields1().equals("")) {
			this.checkUnique(obj,obj.getUniqueFields1());
		}
		
		if (!obj.getUniqueFields2().equals("")) {
			this.checkUnique(obj,obj.getUniqueFields2());
		}
		
		if (!obj.getUniqueFields3().equals("")) {
			this.checkUnique(obj,obj.getUniqueFields3());
		}
	}
	
	private void checkUnique(BusinessClass obj,String checkfields) throws Exception {
		String tablename = obj.getClass().getSimpleName();

	
		String sql="";
		
		if (!this.getRequest().getParameter("pname").equals("insert"))
		{
			sql = "select keyname from ecs_idlist with(nolock) where tablename='"
				+ tablename + "'";
			
			String keyname=this.getDao().strSelect(sql);	
				
			
			//去掉本身自己
			sql = "select count(*) from " + tablename + " with(nolock) "
				+"where "+keyname+"<>"+String.valueOf(this.getFieldValue(obj,keyname));
		}
		else
			sql = "select count(*) from " + tablename + " with(nolock) where 1=1 ";
		
	
		StringBuffer sqlwhere=new StringBuffer();
		
		StringBuffer values=new StringBuffer();
		
		String[] fields=checkfields.split(",");
		for(int i=0;i<fields.length;i++)
		{
			sqlwhere.append(" and ");
			sqlwhere.append(fields[i]);
			sqlwhere.append("='");
			sqlwhere.append(String.valueOf(this.getFieldValue(obj,fields[i])));
			sqlwhere.append("'");
			
			values.append(String.valueOf(this.getFieldValue(obj,fields[i])));
			values.append(",");
		}
		
		sql=sql.concat(sqlwhere.toString());
		
		values.deleteCharAt(values.length() - 1);
		
	
		if(this.getDao().intSelect(sql)>0)
			throw new JException("["+values.toString()+"]重复!");
	}

	private Object getFieldValue(BusinessClass obj,String fldname) throws Exception {

		Object fieldvalue=null;
		Field[] fields = obj.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			String fieldname = fields[i].getName();

			if (fldname.equalsIgnoreCase(fieldname)) {
				String getmethodname = "get"
						+ fieldname.substring(0, 1).toUpperCase()
						+ fieldname.substring(1, fieldname.length());

				Class cls = fields[i].getType();

				if (cls == InputStream.class) {
					Method th = obj.getClass().getMethod(getmethodname);
					Object inputstreamobj = th.invoke(obj);

					if ((inputstreamobj == null)
							|| (((InputStream) inputstreamobj).available() == 0)) {

						// 输入流一旦写入系统就被关闭，需重新取出数据
						String tablename = obj.getClass().getSimpleName();

						String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
								+ tablename + "'";

						String keyname = this.getDao().strSelect(sql);

						if (keyname.equals(""))
							throw new JException("table:[" + tablename
									+ "] not key config");

						String keyGetmethodname = "get"
								+ keyname.substring(0, 1).toUpperCase()
								+ keyname.substring(1, keyname.length());
						Method keyth = obj.getClass().getMethod(
								keyGetmethodname);
						Object keyobj = keyth.invoke(obj);

						sql = "select " + fieldname + " from " + tablename
								+ " with(nolock) where " + keyname + "='"
								+ String.valueOf(keyobj) + "'";
						Hashtable ht = this.getDao().oneRowSelect(sql);

						InputStream in = (InputStream) ht.get(fieldname);

						fieldvalue = StreamUtil.InputStreamToStr(in, "GBK");

					} else {

						fieldvalue = StreamUtil.InputStreamToStr(
								(InputStream) inputstreamobj, "GBK");

					}
				} else {
					Method th = obj.getClass().getMethod(getmethodname);
					fieldvalue = th.invoke(obj);
				}

			}
		}
		return fieldvalue;
	}

	public void dataImport() throws Exception {
		int startline = 1;
		InputStream in = null;
		String importcolumns = "";
		String excelversion = "";

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(4096);

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(1000000);
		upload.setFileSizeMax(10000000);
		upload.setHeaderEncoding("UTF-8");

		List items = upload.parseRequest(this.getRequest());

		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
				// 如果是普通表单字段
				String name = item.getFieldName();
				String value = item.getString("GBK");

				if (name.equals("startline"))
					startline = Integer.valueOf(value).intValue();

				if (name.equals("importcolumns"))
					importcolumns = value;
			} else {
				// 如果是文件字段

				String filename = item.getName();

				
				
				if (!FileUtil.getExtensionName(filename)
						.equalsIgnoreCase("xls")
						&& !FileUtil.getExtensionName(filename)
								.equalsIgnoreCase("xlsx"))
					throw new JException("文件格式不对:" + filename);

				excelversion = FileUtil.getExtensionName(filename);

				in = item.getInputStream();
			}
		}


		
		String[] cols = importcolumns.split(",");
		

		List data = null;
		if (excelversion.equalsIgnoreCase("xls"))
			data = getDataFromXLS(in, cols, startline);
		else
			data = getDataFromXLSX(in, cols, startline);
		
	

		this.OutputStr("{\"success\":\"true\",\"data\":"
				+ this.toJSONArray(data) + "}");

	}

	private List getDataFromXLSX(InputStream in, String[] cols, int startline)
			throws Exception {
		Vector data = new Vector();
		XSSFWorkbook book = new XSSFWorkbook(in);
		XSSFSheet sheet = book.getSheetAt(0); // 2007第一sheet是0
		for (int i = 0; i < sheet.getLastRowNum(); i++) {

			if (i + 1 < startline)
				continue;

			XSSFRow row = sheet.getRow(i);
			Hashtable ht = new Hashtable();

			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {

				XSSFCell cell = row.getCell(j);

				String cellValue = "";
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_NUMERIC:
					cellValue = String.valueOf(new DecimalFormat("#")
							.format(cell.getNumericCellValue()));
					break;
				case XSSFCell.CELL_TYPE_STRING:
					cellValue = cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_BLANK: // 空值
					cellValue = "";
					break;
				default:
					System.out
							.println(cell.getRichStringCellValue().toString());
				}

				ht.put(cols[j], cellValue);
			}

			data.add(ht);

		}
		return data;

	}

	private List getDataFromXLS(InputStream in, String[] cols, int startline)
			throws Exception {
		Vector data = new Vector();
		HSSFWorkbook book = new HSSFWorkbook(in);
		HSSFSheet sheet = book.getSheetAt(0); // 2003第一sheet是1
		
		for (int i = 0; i < sheet.getLastRowNum(); i++) {

			if (i + 1 < startline)
				continue;

			HSSFRow row = sheet.getRow(i);
			Hashtable ht = new Hashtable();

			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {

				HSSFCell cell = row.getCell(j);

				String cellValue = "";
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_NUMERIC:
					cellValue = String.valueOf(new DecimalFormat("#")
							.format(cell.getNumericCellValue()));
					break;
				case XSSFCell.CELL_TYPE_STRING:
					cellValue = cell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_BLANK: // 空值
					cellValue = "";
					break;
				default:
					cellValue=cell.getRichStringCellValue().toString();
				}
							
				
				ht.put(cols[j], cellValue);
			}

			data.add(ht);
			
	
		}
		return data;

	}

	public void export() throws Exception {
		String reqdata = this.getReqData();

		
		Properties prop = StringUtil.getIniProperties(reqdata);
		String exportcondition = prop.getProperty("exportcondition");
		String exportcolumns = prop.getProperty("exportcolumns");
		String exportcolumnnames = prop.getProperty("exportcolumnnames");
		String exportcolumnwidths = prop.getProperty("exportcolumnwidths");

		if (this.exportQuerySQL.equals(""))
			throw new JException("对象未定义导出SQL:" + this.getClass().getName());

		String filename = this.getRequest().getSession().getId() + "_"
				+ Formatter.format(new Date(), Formatter.DATE_FORMAT);
		String tempxlsfile = this.getRequest().getServletContext().getRealPath(
				"/temp/" + filename + ".xls");

		FileOutputStream fio = null;
		WritableWorkbook book = null;
		try {

			File fout = new File(tempxlsfile);

			if (fout.exists())
				fout.delete();

			fio = new FileOutputStream(fout);

			WorkbookSettings workbookSettings = new WorkbookSettings();
			// ISO-8859-1
			workbookSettings.setEncoding("GBK"); // 解决中文乱码，或GBK
			// 创建文件
			book = Workbook.createWorkbook(fio, workbookSettings);

			WritableSheet sheet = book.createSheet("数据", 0);

			String[] colwidths = exportcolumnwidths.split(",");

			for (int n = 0; n < colwidths.length; n++) {
				sheet.setColumnView(n, Double.valueOf(
						Double.valueOf(colwidths[n]).doubleValue() / 6)
						.intValue());
			}

			// 字体样式
			jxl.write.WritableFont wfctitle = new jxl.write.WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.BOLD,
					false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
			jxl.write.WritableCellFormat wcfFCtitle = new jxl.write.WritableCellFormat(
					wfctitle);
			wcfFCtitle.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			wcfFCtitle.setBackground(jxl.format.Colour.GREEN);
			// 把水平对齐方式指定为居中
			wcfFCtitle
					.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			wcfFCtitle.setAlignment(jxl.format.Alignment.LEFT);
			// 设置自适应大小
			wcfFCtitle.setShrinkToFit(true);
			// 自动换行
			wcfFCtitle.setWrap(true);

			// 字体样式
			jxl.write.WritableFont wfc = new jxl.write.WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD,
					false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
			jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
					wfc);
			wcfFC.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			// 把水平对齐方式指定为居中
			wcfFC.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			wcfFC.setAlignment(jxl.format.Alignment.LEFT);
			// 设置自适应大小
			wcfFC.setShrinkToFit(true);
			// 自动换行
			wcfFC.setWrap(true);

			// 生成第一行标题数据
			String[] columnNames = exportcolumnnames.split(",");
			for (int i = 0; i < columnNames.length; i++) {
				Label LblTitle = new Label(i, 0, columnNames[i], wcfFCtitle);
				sheet.addCell(LblTitle);
			}

			String tablename = this.getClass().getSimpleName();

			String searchSQL = "select * from " + tablename
					+ " with(nolock) where 1=1 " + exportcondition;

			this.exportQuerySQL = StringUtil.replace(this.exportQuerySQL,
					"{searchSQL}", "(" + searchSQL + ")");

			Vector exportData = this.getDao().multiRowSelect(
					this.exportQuerySQL);

			String[] cols = exportcolumns.split(",");

			for (int j = 0; j < exportData.size(); j++) {

				Hashtable ht = (Hashtable) exportData.get(j);

				for (int k = 0; k < cols.length; k++) {
					
	

					Label LblContent = new Label(k, j + 1, StringUtil.replace(ht.get(cols[k])
							.toString(),"%enter%","\r\n"), wcfFC);

					sheet.addCell(LblContent);

				}

				if (Math
						.floor(Double.valueOf(String.valueOf(j)).doubleValue() / 50) == Math
						.ceil(Double.valueOf(String.valueOf(j)).doubleValue() / 50))
					Log.info(String.valueOf(j));
			}

			book.write();

		} finally {
			if (book != null) {
				book.close();
			}

			if (fio != null)
				fio.close();
		}

		String fileurl = this.getRequest().getRequestURL().toString()
				.replaceAll("/TinyWebServer", "")
				+ "/temp/" + filename + ".xls";

		this.OutputStr(fileurl);

	}

	public void search() throws Exception {
		String reqdata = this.getReqData();
		Properties prop = StringUtil.getIniProperties(reqdata);
		String sqlwhere = prop.getProperty("sqlwhere");
		
		if (prop.containsKey("ordermode"))
			orderMode=prop.getProperty("ordermode");

		if (prop.containsKey("searchorderfieldname"))
			searchOrderFieldName=prop.getProperty("searchorderfieldname");
		
		String tablename = this.getClass().getSimpleName();

		String sql="select count(*) from sysobjects a,syscolumns b "
			+"where a.id=b.id and a.name='"+tablename+"' and b.name='merchantid'";
		if (this.getDao().intSelect(sql)>0)
			sql = "select * from " + tablename + " with(nolock) where merchantid="+this.getUserInfo().getMerchantid()
				+ sqlwhere;
		else
			sql = "select * from " + tablename + " with(nolock) where 1=1 "+ sqlwhere;
		

		if (!searchOrderFieldName.equals(""))
			sql = sql + " order by " + searchOrderFieldName+" "+orderMode;
		
		this.getRequest().getSession().removeAttribute("search_sql_"+this.getModuleid());
		this.getRequest().getSession().setAttribute("search_sql_"+this.getModuleid(),sql);
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
		
	}
	
	public void batchAdjust() throws Exception
	{		
		String reqdata=this.getReqData();
		Properties prop=StringUtil.getIniProperties(reqdata);
		String adjustFieldName=prop.getProperty("adjustFieldName");
		String expression=prop.getProperty("expression");
		

		String searchsql=(String) this.getRequest().getSession().getAttribute("search_sql_"+this.getModuleid());

		
		if (searchsql.equals(""))
			throw new JException("不存在需要批量调整的数据,请先使用查找数据!");
		
		String tablename=this.getClass().getSimpleName();
		
		String sql="select count(*) from ecs_idlist where tablename='"+tablename+"'";
		
		if(this.getDao().intSelect(sql)==0)
			throw new JException("未配置业务关键字段,请联系系统管理员!");
		
		sql="select keyname from ecs_idlist where tablename='"+tablename+"'";
		
		String keyname=this.getDao().strSelect(sql);

		Vector vtsearch=this.getDao().multiRowSelect(searchsql);
		
		for(int i=0;i<vtsearch.size();i++)
		{
			Hashtable htsearch=(Hashtable) vtsearch.get(i);
			
			int keyvalue=Integer.valueOf(htsearch.get(keyname).toString()).intValue();
			
			this.getDataByID(keyvalue);
			
			//暂时不支持计算
			//Pattern p=Pattern.compile("/\\[\\S*\\]/ig");
			//Matcher m=p.matcher(expression);
			
			
			this.setFieldValue(this, adjustFieldName, expression);
			this.setFieldValue(this, "updator", this.getUserInfo().getName());
			this.setFieldValue(this, "updatetime", new Date());
			
			this.getDao().update(this, adjustFieldName+",updator,updatetime");
		}
	}

	public void doTransaction(String action) throws Exception {
		try {
			Method th = this.getClass().getMethod(action);
			th.invoke(this);
		} catch (Exception e) {

			if (e instanceof InvocationTargetException) {
				throw new Exception(((InvocationTargetException) e)
						.getTargetException().getMessage());
			} else {
				throw e;
			}

		}
	}

	public DataCentre getDao() throws Exception {
		if (this.dao == null) {
			if (this.connection == null)
			{
				this.connection = PoolHelper.getInstance().getConnection(
						Params.getInstance().getProperty("dbname"));		
			}
			this.dao = new ECSDao(this.connection);
		}
		return this.dao;
	}

	public void setDao(DataCentre datacentre) {
		this.dao = datacentre;
	}

	public DataCentre getExtDao(String dsname) throws Exception {

		if (this.extdao == null || !dsname.equals(this.extdsname)) {

			if (this.extconnection == null || !dsname.equals(this.extdsname)) {
				this.extconnection = PoolHelper.getInstance().getConnection(
						dsname);
				this.extdsname = dsname;
			}
			this.extdao = new ECSDao(extconnection);
		}

		return this.extdao;
	}

	public void setExtDao(DataCentre datacentre) {
		this.extdao = datacentre;
	}

	public void setReqeust(HttpServletRequest req) {
		this.request = req;
	}

	public HttpServletRequest getRequest() {
		return this.request;
	}

	public void setResponse(HttpServletResponse res) {
		this.response = res;
	}

	public HttpServletResponse getResponse() {
		return this.response;
	}

	public void setConnection(Connection conn) {
		this.connection = conn;
	}
	
	public Connection getConnection() {
		return this.connection;
	}

	public void OutputStr(String str) throws Exception {  //启用GZIP或zip压缩
		String encoding = this.request.getHeader("Accept-Encoding");
		OutputStream out=response.getOutputStream();
		if(encoding!=null && encoding.indexOf("gzip")!=-1){
			response.setHeader("Content-Encoding", "gzip");
			out= new GZIPOutputStream(out);
			
		}else if(encoding!=null && encoding.indexOf("compress")!=-1){
			response.setHeader("Content-Encoding","compress");
			out = new ZipOutputStream(out);
		}
		this.response.setContentType("text/html; charset=gb2312");
		out.write(str.toString().getBytes());
		out.flush();
		out.close();
	}

	public void OutputStream(InputStream in) throws Exception {
		// BufferedReader reader =new BufferedReader(new InputStreamReader(in));

		byte buf[] = new byte[in.available()];
		in.read(buf, 0, in.available());
		this.response.setContentType("application/octet-stream");
		this.response.getOutputStream().write(buf);
		this.response.getOutputStream().flush();

	}


	public UserInfo getUserInfo() throws Exception {
		if (userinfo != null)
			return userinfo;
		userinfo = new UserInfo();
		Hashtable ht = (Hashtable) this.getRequest().getSession().getAttribute(
				"logininfo");
		String userid = ht.get("userid").toString();
		boolean ismember = ((Boolean) ht.get("ismember")).booleanValue();

		String sql = "";
		if (ismember)
			sql = "select status enabled,nickname login,5 maxlogcount,"
					+ "name,password from ecs_member with(nolock) where memberid="
					+ userid;
		else
			sql = "select * from ecs_user with(nolock) where userid=" + userid;
		Hashtable htinfo = this.getDao().oneRowSelect(sql);
		userinfo.setEnabled(Integer.valueOf(htinfo.get("enabled").toString()));
		userinfo.setLogin(htinfo.get("login").toString());
		userinfo.setLogintime(ht.get("logintime").toString());
		userinfo.setMaxlogcount(Integer.valueOf(htinfo.get("maxlogcount")
				.toString()));
		userinfo
				.setMerchantid(Integer.valueOf(ht.get("merchantid").toString()));
		userinfo.setMerchantname(ht.get("merchantname").toString());
		userinfo.setName(htinfo.get("name").toString());
		userinfo.setPassword(htinfo.get("password").toString());
		userinfo.setUserid(Integer.valueOf(userid));
		return userinfo;
	}

	public String toJSONArray(List lst) throws Exception {
		StringBuffer strbuf = new StringBuffer();
		if (lst.size() > 0) {
			strbuf.append("[");
			for (int i = 0; i < lst.size(); i++) {
				Object dataobj = (Object) lst.get(i);
				if (dataobj instanceof Map) {
					Map mp = (Map) dataobj;
					strbuf.append("{");
					for (Iterator it = mp.keySet().iterator(); it.hasNext();) {
						String keyname = (String) it.next();
						if (keyname.equalsIgnoreCase("merchantid"))
							continue;
						Object obj = mp.get(keyname);
						String keyvalue = "";
						if (obj instanceof InputStream) {
							keyvalue = StreamUtil.InputStreamToStr(
									(InputStream) obj, "GBK");
						} else
							keyvalue = obj.toString();
						keyvalue = StringUtil.replace(keyvalue,"'", " ");
						keyvalue = StringUtil.replace(keyvalue,"%enter%", "\\r\\n");
						strbuf.append("\"" + keyname + "\":\"" + keyvalue
								+ "\",");
					}
					strbuf.deleteCharAt(strbuf.length() - 1);
					strbuf.append("},");
				} else if (dataobj instanceof BusinessClass) {
					BusinessClass businessclass = (BusinessClass) dataobj;
					strbuf.append(businessclass.toJSONObject()).append(",");
				}
			}
			strbuf.deleteCharAt(strbuf.length() - 1);
			strbuf.append("]");
		} else {
			strbuf.append("0");
		}
		return strbuf.toString();
	}

	public String toJSONObject(Map mp) throws Exception {
		StringBuffer strbuf = new StringBuffer();
		if (mp.size() > 0) {
			strbuf.append("{");
			for (Iterator it = mp.keySet().iterator(); it.hasNext();) {
				String keyname = (String) it.next();
				if (keyname.equalsIgnoreCase("merchantid"))
					continue;
				Object obj = mp.get(keyname);
				String keyvalue = "";
				if (obj instanceof InputStream)
					keyvalue = StreamUtil.InputStreamToStr((InputStream) obj,
							"GBK");
				else
					keyvalue = obj.toString();
				keyvalue = StringUtil.replace(keyvalue,"'", " ");
				keyvalue = StringUtil.replace(keyvalue,"%enter%", "\\r\\n");
				strbuf.append("\"" + keyname + "\":\"" + keyvalue + "\",");
			}
			strbuf.deleteCharAt(strbuf.length() - 1);
			strbuf.append("}");
		} else {
			strbuf.append("0");
		}
		return strbuf.toString();
	}

	public String toJSONObject(String jsonfields) throws Exception {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("{");
		Method th = null;
		Field[] fields = this.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			String fieldname = fields[i].getName();
			
			//System.out.println("start:"+fieldname);

			String getmethodname = "get"
					+ fieldname.substring(0, 1).toUpperCase()
					+ fieldname.substring(1, fieldname.length());

			if (fieldname.equalsIgnoreCase("merchantid"))
				continue;

			if (!jsonfields.equals("")) {
				Collection jsonfieldlist = StringUtil.split(jsonfields, ",");
				if (!jsonfieldlist.contains(fieldname))
					continue;
			}
			

			
			Class cls = fields[i].getType();

			if (cls == DataRelation.class) {
				th = this.getClass().getMethod(getmethodname);
				DataRelation dr = (DataRelation) th.invoke(this);
				strbuf.append(dr.toJson());
				strbuf.append(",");
			} else {
				String fieldvalue = "";

				if (cls == boolean.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = String.valueOf((Boolean) th.invoke(this));
				} else if (cls == double.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = String.valueOf((Double) th.invoke(this));
				} else if (cls == java.math.BigDecimal.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = String.valueOf((java.math.BigDecimal) th
							.invoke(this));
				} else if (cls == float.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = String.valueOf((Float) th.invoke(this));
				} else if (cls == int.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = String.valueOf((Integer) th.invoke(this));
				} else if (cls == Integer.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = String.valueOf((Integer) th.invoke(this));
				}
				else if (cls == long.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = String.valueOf((Long) th.invoke(this));
				} else if (cls == java.math.BigInteger.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = String.valueOf((java.math.BigInteger) th
							.invoke(this));
				} else if (cls == Date.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = Formatter.format((Date) th.invoke(this),
							Formatter.DATE_TIME_MS_FORMAT);
				} else if (cls == java.sql.Timestamp.class) {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = Formatter.format((Date) th.invoke(this),
							Formatter.DATE_TIME_MS_FORMAT);
				} else if (cls == InputStream.class) {
					th = this.getClass().getMethod(getmethodname);
					Object inputstreamobj = th.invoke(this);

					// InputStreamReader reader=new
					// InputStreamReader(((InputStream) inputstreamobj), "GBK");
					// int k=reader.read();

					if (inputstreamobj == null) {

						// 输入流一旦写入系统就被关闭，需重新取出数据
						String tablename = this.getClass().getSimpleName();

						String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
								+ tablename + "'";

						String keyname = this.getDao().strSelect(sql);

						if (keyname.equals(""))
							throw new JException("table:[" + tablename
									+ "] not key config");

						String keyGetmethodname = "get"
								+ keyname.substring(0, 1).toUpperCase()
								+ keyname.substring(1, keyname.length());
						Method keyth = this.getClass().getMethod(
								keyGetmethodname);
						Object keyobj = keyth.invoke(this);

						sql = "select " + fieldname + " from " + tablename
								+ " with(nolock) where " + keyname + "='"
								+ String.valueOf(keyobj) + "'";
						Hashtable ht = this.getDao().oneRowSelect(sql);

						InputStream in = (InputStream) ht.get(fieldname);

						fieldvalue = StreamUtil.InputStreamToStr(in, "GBK");

					} else {

						fieldvalue = StreamUtil.InputStreamToStr(
								(InputStream) inputstreamobj, "GBK");

					}
				} else {
					th = this.getClass().getMethod(getmethodname);
					fieldvalue = (String) th.invoke(this);
				}

				fieldvalue = StringUtil.replace(fieldvalue,"'", " ");
				fieldvalue = StringUtil.replace(fieldvalue,"%enter%", "\\r\\n");
				if(cls == int.class ||cls == int.class)
					strbuf.append("\"" + fieldname).append(
							"\":" + fieldvalue + ",");
				else
					strbuf.append("\"" + fieldname).append(
						"\":\"" + fieldvalue + "\",");
			}
			
			//System.out.println("end:"+fieldname);

		}
		strbuf.deleteCharAt(strbuf.length() - 1);
		strbuf.append("}");

		return strbuf.toString();
	}

	public String toJSONObject() throws Exception {
		return toJSONObject("");
	}

	public void getJSONData() throws Exception {
		String reqdata = this.getReqData();

		JSONObject jo = new JSONObject(reqdata);

		setObjValue(this, jo);
	}

	public String getReqData() throws Exception {
		String reqdata = new String(Convert.streamToBytes(this.getRequest()
				.getInputStream()), "utf-8");

		reqdata = reqdata.replaceAll("\r\n", "%enter%");
		String action = this.getRequest().getParameter("pname");

		if (!((action.equalsIgnoreCase("insert") || action
				.equalsIgnoreCase("update")))) {

			reqdata = reqdata.replaceAll("%incline%", "/");

			reqdata = reqdata.replaceAll("%singlequot%", "'");
		}
		Log.info("reqdata: "+reqdata);
		return reqdata;
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
					if (fieldvalue == null || fieldvalue.equals("null") || fieldvalue.equals("") || fieldvalue.equals("0")) fieldvalue=new Date();
				}else {
					if (fieldvalue ==null || fieldvalue.equals("null")) fieldvalue="";
				}
				
				//System.out.println("fieldname:"+fieldname+" fieldvalue:"+fieldvalue);
				
			}
	
			setFieldValue(obj, fieldname, fieldvalue);
		
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

	public void setFieldValue(BusinessClass obj, String fieldname,
			Object fieldvalue) throws Exception {
		Field[] fields = obj.getClass().getDeclaredFields();
		String setmethodname = "set" + fieldname.substring(0, 1).toUpperCase()
				+ fieldname.substring(1, fieldname.length());
		Method th = null;
		

		for (int i = 0; i < fields.length; i++) {

			if (fields[i].getName().equalsIgnoreCase(fieldname)
					&& (fieldvalue != null)) {
				Class cls = fields[i].getType();

				if (cls == DataRelation.class) {
					if (fieldvalue.getClass() == DataRelation.class) {
						th = obj.getClass().getMethod(setmethodname,
								DataRelation.class);
						th.invoke(obj, (DataRelation) fieldvalue);
					} else if (fieldvalue.getClass() == JSONArray.class) {

						String getmethodname = "get"
								+ fieldname.substring(0, 1).toUpperCase()
								+ fieldname.substring(1, fieldname.length());
						th = obj.getClass().getMethod(getmethodname);
					

						DataRelation dr = (DataRelation) th.invoke(obj);
						ArrayList<BusinessClass> objarr = new ArrayList<BusinessClass>();
						JSONArray jsonarr = (JSONArray) fieldvalue;
						for (int j = 0; j < jsonarr.length(); j++) {
							String classname = dr.getClassName();
							
		
							
							BusinessClass businessclass = (BusinessClass) Class
									.forName(classname).newInstance();
							//businessclass.setConnection(obj.getConnection());
						
							//businessclass.setReqeust(obj.getRequest());
							//businessclass.setResponse(obj.getResponse());
							
							JSONObject jsobj = jsonarr.getJSONObject(j);

							setObjValue(businessclass, jsobj);

							objarr.add(businessclass);
						}
						dr.setRelationData(objarr);
					}
				}
		
				else
				{
		
	
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
					
					} else {
						th = obj.getClass().getMethod(setmethodname,
								String.class);
						th.invoke(obj, String.valueOf(fieldvalue));
					}
					
					//System.out.println("end:"+fieldname+"="+sfieldvalue);

				}

			}

		}
	}

	public void getMapData(Map mp) throws Exception {
		if (mp.size() > 0) {
			for (Iterator it = mp.keySet().iterator(); it.hasNext();) {
				String keyname = (String) it.next();
				Object keyvalue = mp.get(keyname);
				setFieldValue(this, keyname.toLowerCase(), keyvalue);
			}
		}
	}

	public String getJSONTree(String tablename, String idfieldname,
			String namefieldname, String parentfieldname,
			String parentfieldvalue,String wheresql) throws Exception {
		StringBuffer strbuf = new StringBuffer();
		String sql = "select count(*) from " + tablename + " with(nolock) "
				+ " where " + parentfieldname + "='" + parentfieldvalue + "' "+wheresql;
		if (this.getDao().intSelect(sql) > 0) {
			strbuf.append("[");
			sql = "select " + idfieldname + "," + namefieldname + " from "
					+ tablename + " with(nolock)" + " where " + parentfieldname
					+ "='" + parentfieldvalue + "' "+wheresql;
			Vector vt = this.getDao().multiRowSelect(sql);
			for (int i = 0; i < vt.size(); i++) {
				Hashtable ht = (Hashtable) vt.get(i);
				String idvalue = ht.get(idfieldname).toString();
				String namevalue = ht.get(namefieldname).toString();

				strbuf.append("{");
				strbuf.append("name:'" + namevalue + "',id:'" + idvalue + "'");

				sql = "select count(*) from " + tablename + " with(nolock) "
						+ " where " + parentfieldname + "='" + idvalue + "' "+wheresql;
				if (this.getDao().intSelect(sql) > 0) {
					strbuf.append(",children:");
					strbuf.append(getJSONTree(tablename, idfieldname,
							namefieldname, parentfieldname, idvalue,wheresql));
					strbuf.append(",leaf:false");
				} else
					strbuf.append(",leaf:true");
				strbuf.append("},");
			}
			strbuf.deleteCharAt(strbuf.length() - 1);
			strbuf.append("]");
		} else
			strbuf.append("0");
		return strbuf.toString();
	}

	public void copyTo(BusinessClass anotherobj) throws Exception {
		Field[] fields = this.getClass().getDeclaredFields();
		Method th = null;
		for (int i = 0; i < fields.length; i++) {
			String fieldname = fields[i].getName();
			String getmethodname = "get"
					+ fieldname.substring(0, 1).toUpperCase()
					+ fieldname.substring(1, fieldname.length());
			th = this.getClass().getMethod(getmethodname);
			Object vobj = th.invoke(this);
			this.setFieldValue(anotherobj, fieldname, vobj);

		}

	}

	public boolean BusiExists(int busid) throws Exception {
		boolean isexists = false;
		String tablename = this.getClass().getSimpleName();

		String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
				+ tablename + "'";

		String keyname = this.getDao().strSelect(sql);

		if (keyname.equals(""))
			throw new JException("table:[" + tablename + "] not key config");

		sql = "select count(*) from " + tablename + " with(nolock) where "
				+ keyname + "=" + busid;

		if (this.getDao().intSelect(sql) > 0)
			isexists = true;

		return isexists;
	}

	public void getDataByID(int busid) throws Exception {
		String tablename = this.getClass().getSimpleName();

		String sql = "select keyname from ecs_idlist with(nolock) where tablename='"
				+ tablename + "'";

		String keyname = this.getDao().strSelect(sql);

		if (keyname.equals(""))
			throw new JException("table:[" + tablename + "] not key config");

		sql = "select * from " + tablename + " with(nolock) where " + keyname
				+ "=" + busid;
		this.getMapData(this.getDao().oneRowSelect(sql));
	}
	
	public void getDataByID(int busid,String idfield) throws Exception {
		String tablename = this.getClass().getSimpleName();

		String sql = "select * from " + tablename + " with(nolock) where " + idfield
				+ "=" + busid;
		this.getMapData(this.getDao().oneRowSelect(sql));
	}
}