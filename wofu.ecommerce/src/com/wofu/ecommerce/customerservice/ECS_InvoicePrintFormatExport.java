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
		
		//ɾ��������������
		String sql="delete from ecs_invoiceprintformatexport";
		
		this.getDao().execute(sql);

		//ɾ�����		
		sql="delete from ecs_idlist where tablename='ECS_InvoicePrintFormatExport'";
		this.getDao().execute(sql);

		
			
		//д�붩����
		
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
		String datestr="��Ʊ_"+Formatter.format(new Date(),Formatter.DATE_FORMAT);
		String tempxlsfile=this.getRequest().getServletContext().getRealPath("/temp/"+datestr+".xls");
		
		
		File fout=new File(tempxlsfile);
		
		if (fout.exists()) fout.delete();
		
		FileOutputStream fio=new FileOutputStream(fout);

		WorkbookSettings workbookSettings = new WorkbookSettings();
		// ISO-8859-1
		workbookSettings.setEncoding("GBK"); // ����������룬��GBK
		// �����ļ�
		WritableWorkbook book = Workbook.createWorkbook(fio, workbookSettings);
		
		WritableSheet sheet=book.createSheet("��Ʊ", 0);
		// ����һ�еĸ߶���Ϊ200
		//sheet.setRowView(0, 500);
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 10);
		sheet.setColumnView(2, 80);

		// ������ʽ
		jxl.write.WritableFont wfc = new jxl.write.WritableFont(WritableFont
				.createFont("΢���ź�"), 10, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
		jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
				wfc);
		wcfFC.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,
				jxl.format.Colour.BLACK);
		// ��ˮƽ���뷽ʽָ��Ϊ����
		wcfFC.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		wcfFC.setAlignment(jxl.format.Alignment.LEFT);
		// ��������Ӧ��С
		wcfFC.setShrinkToFit(true);
		// �Զ�����
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
				
				strbuf.append("��ַ:"+address);
				strbuf.append("\012");
				strbuf.append("����:"+linkman+"("+zipcode+")");
				strbuf.append("\012");
				strbuf.append("�绰:"+mobileno+" "+phone);
				strbuf.append("\012");
				strbuf.append("�ļ���ַ:�������������ݸׯһ��·116������������10¥");
				strbuf.append("\012");
				strbuf.append("         �㶫�������÷�չ���޹�˾(020-38458016) ��ʿ��");
				Label label1 = new Label(0, i, tid, wcfFC);
				
				// ������õĵ�Ԫ����ӵ���������
				sheet.addCell(label1);
				// �� ��Ԫ��column, row������Ԫ��column1, row1�����кϲ���
	
				//sheet.mergeCells(0, 0, 7, 0);
	
				Label label2 = new Label(1, i, linkman, wcfFC);
				// ������õĵ�Ԫ����ӵ���������
				sheet.addCell(label2);
	
				Label label3 = new Label(2, i, strbuf.toString(), wcfFC);
				// ������õĵ�Ԫ����ӵ���������
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
		String datestr="��Ʊ_"+Formatter.format(new Date(),Formatter.DATE_FORMAT);
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
