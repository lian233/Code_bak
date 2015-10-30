package com.wofu.ecommerce.ordermanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.wofu.base.util.BusinessObject;
import com.wofu.common.tools.util.FileUtil;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;

public class ECS_OrderDiffExport extends BusinessObject {

	public void export() throws Exception {
		String datestr = "订单比较_"
				+ Formatter.format(new Date(), Formatter.DATE_FORMAT);
		String tempxlsfile = this.getRequest().getServletContext().getRealPath(
				"/temp/" + datestr + ".xls");
		


		this.getResponse().reset();
		this.getResponse().setContentType(
				"application/vnd.ms-excel;charset=GBK");
		this.getResponse().setHeader(
				"Content-disposition",
				"attachment; filename="
						+ new String((datestr + ".xls").getBytes("utf-8"),
								"GBK"));

		FileInputStream fin = new FileInputStream(tempxlsfile);
		int bufSize = 204800;
		byte[] buf = new byte[bufSize];
		int ret;
		


		while (true) {
			ret = fin.read(buf);
			if (ret != bufSize) {
				this.getResponse().getOutputStream().write(buf, 0, bufSize);
				break;
			} else {
				this.getResponse().getOutputStream().write(buf);
			}
		}
		
		fin.close();
		this.getResponse().getOutputStream().flush();
		this.getResponse().getOutputStream().close();
		
		
	}

	public void upload() throws Exception {
		int status = -1;
		String result = "";
		String msg = "";

		try {

			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(4096);

			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(100000000);
			upload.setFileSizeMax(500000000);
			upload.setHeaderEncoding("GBK");

			List items = upload.parseRequest(this.getRequest());

			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				// 如果是文件字段
				String filename = item.getName();

				filename = filename.substring(filename.lastIndexOf("\\") + 1);

				Log.info(filename);

				if (!FileUtil.getExtensionName(filename)
						.equalsIgnoreCase("TXT")
						&& !filename.equalsIgnoreCase(""))
					throw new JException("非TXT格式:" + filename);

				// 获取数据
				if (FileUtil.getFileNameNoEx(filename).equalsIgnoreCase("出库订单")) {
					getOutStockOrderInfo(item.getInputStream());
				} else if (FileUtil.getFileNameNoEx(filename).equalsIgnoreCase(
						"淘宝订单")) {
					getTaoBaoOrderInfo(item.getInputStream());
				} else if (FileUtil.getFileNameNoEx(filename).equalsIgnoreCase(
						"比较数据")) {
					compare(item.getInputStream());
				}

			}

			// 生成excel
			makeDiffTable();

			status = 1;

		} catch (FileUploadException e) {
			msg = "读取上传文件失败!" + e.getMessage();
		} catch (Exception e) {

			msg = "上传文件失败!" + e.getMessage();
		}

		if (status == 1)
			result = "{success:true,status:" + status + ",\"msg\":\"" + msg
					+ "\"";
		else
			result = "{success:false,status:" + status + ",\"msg\":\"" + msg
					+ "\"";

		result = result.concat("}");

		try {
			this.OutputStr(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getOutStockOrderInfo(InputStream ins) throws Exception {
		// 删除临时表
		String sql = "if object_id( 'tempdb..#tmp_outstockorderinfo') is not null drop table #tmp_outstockorderinfo";
		this.getDao().execute(sql);

		// 创建退货临时表
		sql = "create table #tmp_outstockorderinfo" + "("
				+ "	sdate					varchar(64)			not null	default '',"
				+ "	tid						varchar(64)			not null	default '',"
				+ "	outprice				dec(12,2)			not null	default 0.00,"
				+ "	postfee					dec(12,2)			not null	default 0.00" + ")";
		this.getDao().execute(sql);

		BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

		String line = null;
		int i = 1;

		while ((line = reader.readLine()) != null) {

			String tid = "";
			String sdate = "";
			String outprice = "0.00";
			String postfee = "0.00";

			String[] data = line.split("	");

			sdate = data[0].trim();

			tid = data[1].trim();

			outprice = data[2].trim();

			postfee = data[3].trim();

			if (tid.equalsIgnoreCase(""))
				continue;

			if (outprice.equalsIgnoreCase(""))
				outprice = "0.00";
			if (postfee.equalsIgnoreCase(""))
				postfee = "0.00";
			if (sdate.equalsIgnoreCase(""))
				sdate = "";

			sql = "select count(*) from #tmp_outstockorderinfo where tid='"
					+ tid + "'";
			if (this.getDao().intSelect(sql) > 0) {
				sql = "update #tmp_outstockorderinfo set outprice=outprice+"
						+ outprice + ",postfee=postfee+" + postfee
						+ " where tid='" + tid + "'";
			} else {
				sql = "insert into #tmp_outstockorderinfo(tid,sdate,outprice,postfee) "
						+ "values('"
						+ tid
						+ "','"
						+ sdate
						+ "',"
						+ outprice
						+ "," + postfee + ")";
			}
			this.getDao().execute(sql);

			if (Math.floor(Double.valueOf(String.valueOf(i)).doubleValue()/10000)
					==Math.ceil(Double.valueOf(String.valueOf(i)).doubleValue()/10000))
				Log.info(String.valueOf(i));

			i++;
		}

		/*
		 * Workbook wb=Workbook.getWorkbook(ins); Sheet sheet = wb.getSheet(0);
		 * 
		 * 
		 * 
		 * for (int i=0;i<sheet.getRows();i++) {
		 * 
		 * String tid=""; String sdate=""; String outprice="0.00"; String
		 * postfee="0.00";
		 * 
		 * 
		 * for (int j=0;j<sheet.getColumns();j++) {
		 * 
		 * 
		 * if (j==0) sdate=sheet.getCell(j, i).getContents().trim(); if (j==1)
		 * tid=sheet.getCell(j, i).getContents().trim(); else if(j==2)
		 * outprice=sheet.getCell(j, i).getContents().trim(); else if(j==3)
		 * postfee=sheet.getCell(j, i).getContents().trim(); }
		 * 
		 * if (tid.equalsIgnoreCase("")) continue;
		 * 
		 * if (outprice.equalsIgnoreCase("")) outprice="0.00"; if
		 * (postfee.equalsIgnoreCase("")) postfee="0.00"; if
		 * (sdate.equalsIgnoreCase("")) sdate="";
		 * 
		 * sql="select count(*) from #tmp_outstockorderinfo where
		 * tid='"+tid+"'"; if (this.getDao().intSelect(sql)>0) { sql="update
		 * #tmp_outstockorderinfo set
		 * outprice=outprice+"+outprice+",postfee=postfee+"+postfee +" where
		 * tid='"+tid+"'"; }else { sql="insert into
		 * #tmp_outstockorderinfo(tid,sdate,outprice,postfee) "
		 * +"values('"+tid+"','"+sdate+"',"+outprice+","+postfee+")"; }
		 * this.getDao().execute(sql);
		 * 
		 * Log.info(String.valueOf(i)); }
		 */
	}

	private void getTaoBaoOrderInfo(InputStream ins) throws Exception {
		// 删除临时表
		String sql = "if object_id( 'tempdb..#tmp_taobaoorderinfo') is not null drop table #tmp_taobaoorderinfo";
		this.getDao().execute(sql);

		// 创建退货临时表
		sql = "create table #tmp_taobaoorderinfo" + "("
				+ "	tid						varchar(64)			not null	default '',"
				+ "	payment					dec(12,2)			not null	default 0.00" + ")";
		this.getDao().execute(sql);

		BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

		String line = null;
		int i = 1;

		while ((line = reader.readLine()) != null) {

			String tid = "";
			String payment = "0.00";

			String[] data = line.split("	");

			tid = data[0].trim();

			payment = data[1].trim();

			if (tid.equalsIgnoreCase(""))
				continue;

			if (payment.equalsIgnoreCase(""))
				payment = "0.00";

			sql = "insert into #tmp_taobaoorderinfo(tid,payment) " + "values('"
					+ tid + "'," + payment + ")";
			this.getDao().execute(sql);

			if (Math.floor(Double.valueOf(String.valueOf(i)).doubleValue()/10000)
					==Math.ceil(Double.valueOf(String.valueOf(i)).doubleValue()/10000))
				Log.info(String.valueOf(i));

			i++;
		}

		/*
		 * Workbook wb = Workbook.getWorkbook(ins); Sheet sheet =
		 * wb.getSheet(0); for (int i = 0; i < sheet.getRows(); i++) { String
		 * tid = ""; String payment = "0.00"; for (int j = 0; j <
		 * sheet.getColumns(); j++) { if (j == 0) tid = sheet.getCell(j,
		 * i).getContents().trim(); else if (j == 1) payment = sheet.getCell(j,
		 * i).getContents().trim();
		 *  }
		 * 
		 * if (tid.equalsIgnoreCase("")) continue;
		 * 
		 * if (payment.equalsIgnoreCase("")) payment = "0.00";
		 * 
		 * sql = "insert into #tmp_taobaoorderinfo(tid,payment) " + "values('" +
		 * tid + "'," + payment + ")"; this.getDao().execute(sql);
		 * 
		 * Log.info(String.valueOf(i)); }
		 */
	}

	private void compare(InputStream ins) throws Exception {
		// 删除临时表
		String sql = "if object_id( 'tempdb..#tmp_compare') is not null drop table #tmp_compare";
		this.getDao().execute(sql);

		// 创建退货临时表
		sql = "create table #tmp_compare" + "("
				+ "	tid						varchar(64)			not null	default '',"
				+ "	outprice				dec(12,2)			not null	default 0.00,"
				+ "	postfee					dec(12,2)			not null	default 0.00,"
				+ "	payment					dec(12,2)			not null	default 0.00,"
				+ "	diffvalue				dec(12,2)			not null	default 0.00,"
				+ "	notes					varchar(64)			not null	default ''" + ")";
		this.getDao().execute(sql);

		BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

		String line = null;
		int i = 1;

		while ((line = reader.readLine()) != null) {

			String tid = "";
			String outprice = "0.00";
			String postfee = "0.00";
			String payment = "0.00";
			String diffvalue = "0.00";
			String notes = "";

			String[] data = line.split("	");

			tid = data[0].trim();

			outprice = data[1].trim();

			postfee = data[2].trim();

			payment = data[3].trim();

			diffvalue = data[4].trim();

			notes = data[5].trim();

			if (tid.equalsIgnoreCase(""))
				continue;

			if (outprice.equalsIgnoreCase(""))
				outprice = "0.00";
			if (postfee.equalsIgnoreCase(""))
				postfee = "0.00";
			if (payment.equalsIgnoreCase(""))
				payment = "0.00";
			if (diffvalue.equalsIgnoreCase(""))
				diffvalue = "0.00";

			sql = "insert into #tmp_compare(tid,outprice,postfee,payment,diffvalue,notes) "
					+ "values('"
					+ tid
					+ "',"
					+ outprice
					+ ","
					+ postfee
					+ ","
					+ payment + "," + diffvalue + "," + notes + ")";
			this.getDao().execute(sql);

			if (Math.floor(Double.valueOf(String.valueOf(i)).doubleValue()/10000)
					==Math.ceil(Double.valueOf(String.valueOf(i)).doubleValue()/10000))
				Log.info(String.valueOf(i));

			i++;
		}

		/*
		 * Workbook wb = Workbook.getWorkbook(ins); Sheet sheet =
		 * wb.getSheet(0); for (int i = 1; i < sheet.getRows(); i++) // 忽略掉第一行 {
		 * String tid = ""; String outprice = "0.00"; String postfee = "0.00";
		 * String payment = "0.00"; String diffvalue = "0.00"; String notes =
		 * ""; for (int j = 0; j < sheet.getColumns(); j++) { if (j == 0) tid =
		 * sheet.getCell(j, i).getContents().trim(); else if (j == 1) outprice =
		 * sheet.getCell(j, i).getContents().trim(); else if (j == 2) postfee =
		 * sheet.getCell(j, i).getContents().trim(); else if (j == 3) payment =
		 * sheet.getCell(j, i).getContents().trim(); else if (j == 4) diffvalue =
		 * sheet.getCell(j, i).getContents().trim(); else if (j == 5) notes =
		 * sheet.getCell(j, i).getContents().trim(); }
		 * 
		 * if (tid.equalsIgnoreCase("")) continue;
		 * 
		 * if (outprice.equalsIgnoreCase("")) outprice = "0.00"; if
		 * (postfee.equalsIgnoreCase("")) postfee = "0.00"; if
		 * (payment.equalsIgnoreCase("")) payment = "0.00"; if
		 * (diffvalue.equalsIgnoreCase("")) diffvalue = "0.00";
		 * 
		 * sql = "insert into
		 * #tmp_compare(tid,outprice,postfee,payment,diffvalue,notes) " +
		 * "values('" + tid + "'," + outprice + "," + postfee + "," + payment +
		 * "," + diffvalue + "," + notes + ")"; this.getDao().execute(sql); }
		 */
	}

	private void makeDiffTable() throws Exception {
		String datestr = "订单比较_"
				+ Formatter.format(new Date(), Formatter.DATE_FORMAT);
		String tempxlsfile = this.getRequest().getServletContext().getRealPath(
				"/temp/" + datestr + ".xls");

		File fout = new File(tempxlsfile);

		if (fout.exists())
			fout.delete();

		FileOutputStream fio = new FileOutputStream(fout);

		WorkbookSettings workbookSettings = new WorkbookSettings();
		// ISO-8859-1
		workbookSettings.setEncoding("GBK"); // 解决中文乱码，或GBK
		// 创建文件
		WritableWorkbook book = Workbook.createWorkbook(fio, workbookSettings);

		WritableSheet sheet = book.createSheet("差异", 0);
		// 将第一行的高度设为200
		// sheet.setRowView(0, 500);
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 20);
		sheet.setColumnView(2, 10);
		sheet.setColumnView(3, 10);
		sheet.setColumnView(4, 10);
		sheet.setColumnView(5, 10);
		sheet.setColumnView(6, 20);

		// 字体样式
		jxl.write.WritableFont wfctitle = new jxl.write.WritableFont(
				WritableFont.createFont("宋体"), 10, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
		jxl.write.WritableCellFormat wcfFCtitle = new jxl.write.WritableCellFormat(
				wfctitle);
		wcfFCtitle.setBorder(jxl.format.Border.ALL,
				jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
		wcfFCtitle.setBackground(jxl.format.Colour.GREEN);
		// 把水平对齐方式指定为居中
		wcfFCtitle.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		wcfFCtitle.setAlignment(jxl.format.Alignment.LEFT);
		// 设置自适应大小
		wcfFCtitle.setShrinkToFit(true);
		// 自动换行
		wcfFCtitle.setWrap(true);

		// 字体样式
		jxl.write.WritableFont wfc = new jxl.write.WritableFont(WritableFont
				.createFont("宋体"), 10, WritableFont.NO_BOLD, false,
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

		// 生成第一行标题数据
		Label label00 = new Label(0, 0, "淘宝单号", wcfFCtitle);
		sheet.addCell(label00);

		Label label10 = new Label(1, 0, "日期", wcfFCtitle);
		sheet.addCell(label10);

		Label label20 = new Label(2, 0, "出库金额", wcfFCtitle);
		sheet.addCell(label20);

		Label label30 = new Label(3, 0, "邮费", wcfFCtitle);
		sheet.addCell(label30);

		Label label40 = new Label(4, 0, "付款金额", wcfFCtitle);
		sheet.addCell(label40);

		Label label50 = new Label(5, 0, "差异", wcfFCtitle);
		sheet.addCell(label50);

		Label label60 = new Label(6, 0, "备注", wcfFCtitle);
		sheet.addCell(label60);

		String sql = "create index i1_tmp on #tmp_outstockorderinfo(sdate)";
		this.getDao().execute(sql);

		boolean needcompare = false;

		sql = "create index i2_tmp on #tmp_taobaoorderinfo(tid)";
		this.getDao().execute(sql);

		sql = "select count(*) from tempdb..sysobjects where id=object_id( 'tempdb..#tmp_compare')";
		if (this.getDao().intSelect(sql) > 0) {
			sql = "create index i3_tmp on #tmp_compare(tid)";
			this.getDao().execute(sql);
			needcompare = true;
		}

		try {
			sql = "select tid,sdate,outprice,postfee from #tmp_outstockorderinfo order by sdate";

			Vector vt = this.getDao().multiRowSelect(sql);


			for (int i = 0; i < vt.size(); i++) {
				
			

				Hashtable ht = (Hashtable) vt.get(i);

				String tid = ht.get("tid").toString();
				String sdate = ht.get("sdate").toString();
				String outprice = ht.get("outprice").toString();
				String postfee = ht.get("postfee").toString();

				String payment = "0.00";

				sql = "select count(*) from #tmp_taobaoorderinfo where tid='"
						+ tid + "'";

				if (this.getDao().intSelect(sql) > 0) {
					sql = "select payment from #tmp_taobaoorderinfo where tid='"
							+ tid + "'";
					payment = this.getDao().strSelect(sql);
				}

				double diffvalue = Double.valueOf(outprice).doubleValue()
						+ Double.valueOf(postfee).doubleValue()
						- Double.valueOf(payment).doubleValue();

				Label label1 = new Label(0, i + 1, tid, wcfFC);
				sheet.addCell(label1);

				Label label2 = new Label(1, i + 1, sdate, wcfFC);
				sheet.addCell(label2);

				Label label3 = new Label(2, i + 1, outprice, wcfFC);
				sheet.addCell(label3);

				Label label4 = new Label(3, i + 1, postfee, wcfFC);
				sheet.addCell(label4);

				Label label5 = new Label(4, i + 1, payment, wcfFC);
				sheet.addCell(label5);

				Label label6 = new Label(5, i + 1, String.valueOf(diffvalue),
						wcfFC);
				sheet.addCell(label6);

				if (needcompare) {
					sql = "select notes from #tmp_compare where tid='" + tid
							+ "'";

					String notes = this.getDao().strSelect(sql);

					Label label7 = new Label(6, i + 1, notes, wcfFC);
					sheet.addCell(label7);
				}
				
				if (Math.floor(Double.valueOf(String.valueOf(i)).doubleValue()/10000)
						==Math.ceil(Double.valueOf(String.valueOf(i)).doubleValue()/10000))
					Log.info(String.valueOf(i));

			}
		} finally {
			book.write();
			book.close();
			fio.close();
		}

	}
}
