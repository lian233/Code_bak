package com.wofu.ecommerce.customerservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.util.StringUtil;
import com.wofu.common.tools.util.Formatter;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.util.Date;

public class ECS_InvoicePrintFormatExport extends BusinessObject {
	
	private int serialid;
	private String tid;
	private String linkman;
	private String address;
	private String phone;
	private String zipcode;
	private String mobileno;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getSerialid() {
		return serialid;
	}
	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}

	
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public void makeInvoice() throws Exception
	{
		String reqdata=this.getReqData();
		
		
		String[] tids=reqdata.split("%enter%");
		
		//删除所有已有数据
		String sql="delete from ecs_invoiceprintformatexport";
		
		this.getDao().execute(sql);

		//删除序号		
		sql="delete from ecs_idlist where tablename='ECS_InvoicePrintFormatExport'";
		this.getDao().execute(sql);

		
			
		//写入订单号
		
		for (int i=0;i<tids.length;i++)
		{
			String tid=tids[i].trim();
			
			if (tid.equals("")) continue;
			
			this.serialid=this.getDao().IDGenerator(this, "serialid");
			this.tid=tid;
			
			
			if (this.getDao().intSelect("select count(*) from ns_customerorder where tid='"+tid+"'")>0)
			
				sql="select top 1 receiverstate+' '+receivercity+' '+receiverdistrict+' '+receiveraddress address, "
					+"receivername,receiverzip,receiverphone,receivermobile from ns_customerorder with(nolock) where tid='"+tid+"'";
			else
				sql="select top 1 receiverstate+' '+receivercity+' '+receiverdistrict+' '+receiveraddress address, "
					+"receivername,receiverzip,receiverphone,receivermobile from ns_customerorderbak with(nolock) where tid='"+tid+"'";
			
			Hashtable receiverinfo=this.getDao().oneRowSelect(sql);
			
			this.address=receiverinfo.get("address").toString();

			this.linkman=receiverinfo.get("receivername").toString();
			this.zipcode=receiverinfo.get("receiverzip").toString();

			this.phone=receiverinfo.get("receiverphone").toString();

			this.mobileno=receiverinfo.get("receivermobile").toString();
			
			this.getDao().insert(this);
			
		}
		String datestr="发票_"+Formatter.format(new Date(),Formatter.DATE_FORMAT);
		String tempxlsfile=this.getRequest().getServletContext().getRealPath("/temp/"+datestr+".xls");
		
		
		File fout=new File(tempxlsfile);
		
		if (fout.exists()) fout.delete();
		
		FileOutputStream fio=new FileOutputStream(fout);

		WorkbookSettings workbookSettings = new WorkbookSettings();
		// ISO-8859-1
		workbookSettings.setEncoding("GBK"); // 解决中文乱码，或GBK
		// 创建文件
		WritableWorkbook book = Workbook.createWorkbook(fio, workbookSettings);
		
		WritableSheet sheet=book.createSheet("发票", 0);
		// 将第一行的高度设为200
		//sheet.setRowView(0, 500);
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 10);
		sheet.setColumnView(2, 80);

		// 字体样式
		jxl.write.WritableFont wfc = new jxl.write.WritableFont(WritableFont
				.createFont("微软雅黑"), 10, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
		jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
				wfc);
		wcfFC.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,
				jxl.format.Colour.BLACK);
		// 把水平对齐方式指定为居中
		wcfFC.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		wcfFC.setAlignment(jxl.format.Alignment.LEFT);
		// 设置自适应大小
		wcfFC.setShrinkToFit(true);
		// 自动换行
		wcfFC.setWrap(true);
			

		try
		{
			sql="select tid,address,linkman,zipcode,phone,mobileno from ecs_invoiceprintformatexport order by serialid";
			
			Vector vt=this.getDao().multiRowSelect(sql);
			for (int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				
				StringBuffer strbuf=new StringBuffer();
				
				String linkman=ht.get("linkman").toString();
				String zipcode=ht.get("zipcode").toString();
				String phone=ht.get("phone").toString();
				String mobileno=ht.get("mobileno").toString();
				String address=ht.get("address").toString();
				String tid=ht.get("tid").toString();
				
				strbuf.append("地址:"+address);
				strbuf.append("\012");
				strbuf.append("姓名:"+linkman+"("+zipcode+")");
				strbuf.append("\012");
				strbuf.append("电话:"+mobileno+" "+phone);
				strbuf.append("\012");
				strbuf.append("寄件地址:广州市天河区东莞庄一横路116号生产力大厦10楼");
				strbuf.append("\012");
				strbuf.append("         广东永骏经济发展有限公司(020-38458016) 迪士尼");
				Label label1 = new Label(0, i, tid, wcfFC);
				
				// 将定义好的单元格添加到工作表中
				sheet.addCell(label1);
				// 把 单元格（column, row）到单元格（column1, row1）进行合并。
	
				//sheet.mergeCells(0, 0, 7, 0);
	
				Label label2 = new Label(1, i, linkman, wcfFC);
				// 将定义好的单元格添加到工作表中
				sheet.addCell(label2);
	
				Label label3 = new Label(2, i, strbuf.toString(), wcfFC);
				// 将定义好的单元格添加到工作表中
				sheet.addCell(label3);				
	
			}
		}finally
		{
			book.write();
			book.close();
			fio.close();
		}
		
		sql="select * from ecs_invoiceprintformatexport order by serialid";
		
		this.OutputStr(this.toJSONArray(this.getDao().multiRowSelect(sql)));
		
	}
	
	public void export() throws Exception
	{
		String datestr="发票_"+Formatter.format(new Date(),Formatter.DATE_FORMAT);
		String tempxlsfile=this.getRequest().getServletContext().getRealPath("/temp/"+datestr+".xls");
		
		this.getResponse().reset();
		this.getResponse().setContentType("application/vnd.ms-excel;charset=gbk");
		this.getResponse().setHeader("Content-disposition" , "attachment; filename=" + new String((datestr+".xls").getBytes("GBK") , "GBK"));
				
		FileInputStream fin = new FileInputStream(tempxlsfile);
		int bufSize = 204800;
		byte[] buf = new byte[bufSize];
		int ret;
		
		while(true) {
			ret = fin.read(buf);
			if(ret != bufSize) {
				this.getResponse().getOutputStream().write(buf , 0 , bufSize);
				break;
			}else {
				this.getResponse().getOutputStream().write(buf);
			}			
		}
		fin.close();
		this.getResponse().getOutputStream().flush();
		this.getResponse().getOutputStream().close();
		
	}

}
