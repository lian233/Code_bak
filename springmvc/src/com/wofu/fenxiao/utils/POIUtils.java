package com.wofu.fenxiao.utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jxl.CellView;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.DateFormat;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import com.wofu.common.tools.util.Formatter;
import com.wofu.common.tools.util.log.Log;
/**
 * 读写EXCEL文件
 */
public class POIUtils {
	
	
	private static final String position_title = "title";
	private static final String position_body = "body";
	/**
	 * 判断excel版本
	 * @param in
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private static Workbook openWorkbook(InputStream in, String filename)
			throws IOException {
		Workbook wb = null;
		if (filename.endsWith(".xlsx")) {
			wb = new XSSFWorkbook(in);// Excel 2007
		} else {
			wb = new HSSFWorkbook(in);// Excel 2003
		}
		return wb;
	}

	/**
	 * 根据文件路径和工作薄下标导入Excel数据
	 * @param fileName 文件名
	 * @param sheetIndex 工作薄下标
	 * @return
	 * @throws Exception
	 */
	public static List<List<String>> getExcelData(MultipartFile file , String fileName,int sheetIndex) throws Exception {
		List<List<String>> dataLst = new ArrayList<List<String>>();
		Workbook wb = openWorkbook(file.getInputStream(), fileName);
		Sheet sheet = (Sheet) wb.getSheetAt(sheetIndex);// 切换工作薄
		Row row = null;
		Cell cell = null;

		int totalRows = sheet.getPhysicalNumberOfRows();
		System.out.println("excel总行数： "+totalRows);
		/** 得到Excel的列数 */
		int totalCells = totalRows >= 1 && sheet.getRow(0) != null ? sheet
				.getRow(0).getPhysicalNumberOfCells() : 0;
		System.out.println("excel总列数： "+totalCells);
		for (int r = 0; r < totalRows; r++) {
			row = sheet.getRow(r);
			if (row == null || curRowInsideNull(row, totalCells))
				continue;
			List<String> rowLst = new ArrayList<String>();
			for (int c = 0; c < totalCells; c++) {
				cell = row.getCell(c);
				String cellValue = "";
				if (null != cell) {
					// 以下是判断数据的类型
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_NUMERIC: // 数字
						int cellStyle = cell.getCellStyle().getDataFormat();
						String cellStyleStr = cell.getCellStyle().getDataFormatString();
						if ("0.00_);[Red]\\(0.00\\)".equals(cellStyleStr)) {
							NumberFormat f = new DecimalFormat("#.##");
							cellValue = (f.format((cell.getNumericCellValue())) + "")
									.trim();
						} else if (HSSFDateUtil.isCellDateFormatted(cell)) {
							cellValue = HSSFDateUtil.getJavaDate(
									cell.getNumericCellValue()).toString();
						} else if ( cellStyle == 58 || cellStyle == 179 || "m\"月\"d\"日\";@".equals(cellStyleStr)) {
							// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd");
							double value = cell.getNumericCellValue();
							Date date = org.apache.poi.ss.usermodel.DateUtil
									.getJavaDate(value);
							cellValue = sdf.format(date);
//						} else if ((cellStyle == 181 || cellStyle == 177|| cellStyle == 176)&& cellStyleStr.endsWith("@")) { 
							//星期几 Excel中的日期自定义格式 cellStyle不固定，故采用  "[$-804]aaaa;@"来判断							
						} else if ("[$-804]aaaa;@".equals(cellStyleStr)) {
							SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
							double value = cell.getNumericCellValue();
							Date date = org.apache.poi.ss.usermodel.DateUtil
									.getJavaDate(value);
							cellValue = sdf.format(date);

						} else {
							NumberFormat f = new DecimalFormat("#.##");
							cellValue = (f.format((cell.getNumericCellValue())) + "")
									.trim();
						}
						break;
					case HSSFCell.CELL_TYPE_STRING: // 字符串
						cellValue = cell.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
						cellValue = cell.getBooleanCellValue() + "";
						break;
					case HSSFCell.CELL_TYPE_FORMULA: // 公式
						try {
							cellValue = String.valueOf(cell.getNumericCellValue());
						} catch (IllegalStateException e) {
							try {
								cellValue = String.valueOf(cell.getRichStringCellValue());
							} catch (Exception e1) {
								cellValue="";
							}
						}
						break;
					case HSSFCell.CELL_TYPE_BLANK: // 空值
//						cellValue = "";
						break;

					case HSSFCell.CELL_TYPE_ERROR: // 故障
//						cellValue = "非法字符";
						break;
					default:
//						cellValue = "未知类型";
						break;
					}
				}
				System.out.println("第"+r+"行,第"+c+"列: "+cellValue);
				rowLst.add(cellValue);
			}
			dataLst.add(rowLst);
		}
		return dataLst;
	}

	/**
	 * 判断当前行内所有单元格是否为空
	 * 
	 * @param row
	 * @param totalCells
	 * @return
	 */
	private static boolean curRowInsideNull(Row row, int totalCells) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < totalCells; i++) {
			row.getCell(i, HSSFRow.RETURN_BLANK_AS_NULL);
			Cell cell = row.getCell(i, HSSFRow.RETURN_BLANK_AS_NULL);
			if (cell != null) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					sb.append(cell.getStringCellValue().trim());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					sb.append(String.valueOf(cell.getNumericCellValue()));
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					sb.append(String.valueOf(cell.getBooleanCellValue()));
					break;
				case Cell.CELL_TYPE_FORMULA://判断公式生成的结果
					String value = "";
					try {
						value = String.valueOf(cell.getNumericCellValue());
					} catch (IllegalStateException e) {
						try {
							value = String.valueOf(cell.getRichStringCellValue());
						} catch (Exception e1) {
							value="";
						}
					}
					sb.append(value);
					break;
				default:
					break;
				}
			}
		}
		if (sb.toString().trim().equals(""))
			return true;
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public static HSSFWorkbook handleDataToExcel(List list,Class clazz,String sheetName,int pageSize) throws Exception{
		HSSFWorkbook workbook = null;
		workbook = new HSSFWorkbook();
		// 获取Excel标题
		List<ExcelHeader> headers = getHeaderList(clazz);
		Collections.sort(headers);
		// 
		if(null != list && list.size() > 0 ){
			int sheetCount = list.size() % pageSize == 0 ? list.size() / pageSize : list.size() / pageSize + 1;
			for(int i = 1; i <= sheetCount; i++){
				HSSFSheet sheet = null;
				if(!StringUtils.isEmpty(sheetName)){
					sheet = workbook.createSheet(sheetName + i);
				}else{
					sheet = workbook.createSheet();
				}
				
				HSSFRow row = sheet.createRow(0);
				// 写标题
				CellStyle titleStyle = setCellStyle(workbook,position_title);
				for(int j = 0; j < headers.size();j++){
					HSSFCell cell = row.createCell(j);
					cell.setCellStyle(titleStyle);
					cell.setCellValue(headers.get(j).getTitle());
					sheet.setColumnWidth(j, headers.get(j).getWidth()*256);
				}
				
				// 写内容
				Object obj = null;
				CellStyle bodyStyle = setCellStyle(workbook, position_body);
				int begin = (i - 1) * pageSize;
				int end = (begin + pageSize) > list.size() ? list.size() : (begin + pageSize);
				int rowCount = 1;
				for(int n = begin; n < end; n++){
					row = sheet.createRow(rowCount);
					rowCount++;
					obj = list.get(n);
					for(int x = 0; x < headers.size(); x++){
						Cell cell = row.createCell(x);
						cell.setCellStyle(bodyStyle);
						@SuppressWarnings("unchecked")
						Method method = clazz.getDeclaredMethod(headers.get(x).getMethodName());
						Object value = method.invoke(obj);
						if(value instanceof Date){
							 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							 String formattedDate = dateFormat.format(new Date());
							 cell.setCellValue((String)formattedDate);
						}else if(value instanceof Double){
							cell.setCellValue((Double)value);
						}else if(value instanceof String){
							cell.setCellValue((String)value);								
							/*if(((String)value).trim().length()>0 && isNumeric((String)value)){
								cell.setCellType(Cell.CELL_TYPE_STRING);		
								cell.setCellValue((String)value);
							}else{
							}*/
						}else if(value instanceof Integer){
							cell.setCellValue((Integer) value);
						}
						
//						cell.setCellValue(org.apache.commons.beanutils.BeanUtils.getProperty(obj,getPropertyName(headers.get(x))));
					}
				}
			}
		}		
		return workbook;
	}
	
	/**
	 * 导出excel
	 * header 导出的列数组
	 * lists  准备要导出的数据数列
	 */
	public static <T> HSSFWorkbook exportExcel(List<T> lists,String header) throws Exception{
		HSSFWorkbook workbook =null;
		HSSFCell cell=null;
		HSSFRow row=null;
		String[] cells = header.split(",");
		//添加列头信息
		workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		row = sheet.createRow(0);
		CellStyle style = setCellStyle(workbook, "title");
		for(int i=0;i<cells.length;i++){
			cell= row.createCell(i) ;
			cell.setCellValue(cells[i]);
			cell.setCellStyle(style);
		}
		//添加主体信息
		style = setCellStyle(workbook,"body");
		for(int j=1;j<=lists.size();j++){
			T t = lists.get(j-1);
			row = sheet.createRow(j);
			for(int k=0;k<cells.length;k++){
				cell = row.createCell(k);
				cell.setCellStyle(style);
				Method method = t.getClass().getDeclaredMethod("get"+cells[k]);
				Object obj  = method.invoke(t);
				if(obj instanceof Date){
					cell.setCellValue(Formatter.format(obj,Formatter.DATE_TIME_FORMAT));
				}else if(obj instanceof String){
					cell.setCellValue((String)obj);
				}else if(obj instanceof Integer){
					cell.setCellValue((Integer)obj);
				}else if(obj instanceof Double){
					cell.setCellValue((Double)obj);
				}
			}
			
		}
		return workbook;
	}
	
	/**
	 * 导出excel
	 * header 导出的列数组
	 * lists  准备要导出的数据数列
	 */
	public static <T> HSSFWorkbook exportExcelHead(List<T> lists,String field,String header) throws Exception{
		HSSFWorkbook workbook =null;
		HSSFCell cell=null;
		HSSFRow row=null;
		String[] cells = header.split(",");
		String[] fields = field.split(",");
		
		//添加列头信息
		workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		row = sheet.createRow(0);
		CellStyle style = setCellStyle(workbook, "title");
		for(int i=0;i<cells.length;i++){
			cell= row.createCell(i) ;
			cell.setCellValue(cells[i]);
			cell.setCellStyle(style);
		}
		//添加主体信息
		style = setCellStyle(workbook,"body");
		for(int j=1;j<=lists.size();j++){
			T t = lists.get(j-1);
			row = sheet.createRow(j);
			for(int k=0;k<fields.length;k++){
				cell = row.createCell(k);
				cell.setCellStyle(style);
				Method method = t.getClass().getDeclaredMethod("get"+fields[k]);
				Object obj  = method.invoke(t);
				if(obj instanceof Date){
					cell.setCellValue(Formatter.format(obj,Formatter.DATE_TIME_FORMAT));
				}else if(obj instanceof String){
					cell.setCellValue((String)obj);
				}else if(obj instanceof Integer){
					cell.setCellValue((Integer)obj);
				}else if(obj instanceof Double){
					cell.setCellValue((Double)obj);
				}
			}
			
		}
		return workbook;
	}	
	
	/**
	 * 注解形式导出Excel
	 * @param response HttpServletResponse
	 * @param fileName 导出Excel的文件名
	 * @param List<T> objs 某一个实体类数据集合
	 * @param clazz <T> 某一个实体类
	 * @param sheetName sheet的名称
	 * @param pageSize sheet显示多少条数据
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void exportToExcel(HttpServletResponse response,String fileName,List objs, Class clazz,
			String sheetName, int pageSize){
		OutputStream out = null;
		try {
			String tempName = new String(fileName.getBytes(), "ISO8859-1");
			response.setHeader("content-disposition", "attachment;filename=" + tempName + ".xls");
			response.setContentType("application/vnd.ms-excel");
			HSSFWorkbook workbook = handleDataToExcel(objs, clazz, "sheet", pageSize);
			out = response.getOutputStream();
			workbook.write(out);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> void exportToExcelT(HttpServletResponse response,String fileName,List<T> objs, String headers){
		OutputStream out = null;
		try {
			String tempName = new String(fileName.getBytes(), "ISO8859-1");
			response.setHeader("content-disposition", "attachment;filename=" + tempName + ".xls");
			response.setContentType("application/vnd.ms-excel");
			HSSFWorkbook workbook = exportExcel(objs, headers);
			out = response.getOutputStream();
			workbook.write(out);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> void exportToExcelHead(HttpServletResponse response,String fileName,List<T> objs, String fields,String headers){
		OutputStream out = null;
		try {
			String tempName = new String(fileName.getBytes(), "ISO8859-1");
			response.setHeader("content-disposition", "attachment;filename=" + tempName + ".xls");
			response.setContentType("application/vnd.ms-excel");
			HSSFWorkbook workbook = exportExcelHead(objs, fields , headers);
			out = response.getOutputStream();
			workbook.write(out);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
			}
		}
	}	
	
	/**

	 * 导出对象集合
	 * @param <T>
	 * @param tempxlsfile
	 * @param response
	 * @param request
	 * @param fileName
	 * @param objs
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static <T> String exportToExcelJxl(String tempxlsfile,HttpServletResponse response,HttpServletRequest request,String fileName,List<T> objs, String headers,int port)
	throws Exception{
		FileOutputStream fio = null;
		WritableWorkbook book = null;
		try {
			System.out.println("port: "+port);
			File fout = new File(tempxlsfile);

			if (fout.exists())
				fout.delete();

			fio = new FileOutputStream(fout);

			WorkbookSettings workbookSettings = new WorkbookSettings();
			// ISO-8859-1
			workbookSettings.setEncoding("utf-8"); 
			// 创建文件
			book = jxl.Workbook.createWorkbook(fio, workbookSettings);
			//自适应宽度
			CellView view = new CellView();
			view.setAutosize(true);
			WritableSheet sheet = book.createSheet("数据", 0);
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
			
			//整数数字单元格
			jxl.write.WritableCellFormat num_format = new jxl.write.WritableCellFormat(wfc,NumberFormats.INTEGER);
			num_format.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			// 把水平对齐方式指定为居中
			num_format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			num_format.setAlignment(jxl.format.Alignment.LEFT);
			// 设置自适应大小
			num_format.setShrinkToFit(true);
			// 自动换行
			num_format.setWrap(true);
			
			//浮点数单元格
			jxl.write.WritableCellFormat float_format= new jxl.write.WritableCellFormat(wfc,NumberFormats.FLOAT);
			float_format.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			// 把水平对齐方式指定为居中
			float_format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			float_format.setAlignment(jxl.format.Alignment.LEFT);
			// 设置自适应大小
			float_format.setShrinkToFit(true);
			// 自动换行
			float_format.setWrap(true);
			//日期单元格
			jxl.write.WritableCellFormat date_format= new jxl.write.WritableCellFormat(wfc,new DateFormat("yyyy-MM-dd HH:mm:ss"));
			date_format.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			// 把水平对齐方式指定为居中
			date_format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			date_format.setAlignment(jxl.format.Alignment.LEFT);
			// 设置自适应大小
			date_format.setShrinkToFit(true);
			
			String[] columnNames = headers.split(",");
			//设置列宽自适应
			for(int m=0;m<columnNames.length;m++){
				sheet.setColumnView(m, view);
			}
			// 生成第一行标题数据
			for (int i = 0; i < columnNames.length; i++) {
				Label LblTitle = new Label(i, 0, columnNames[i], wcfFCtitle);
				sheet.addCell(LblTitle);
			}
			String[] cols = headers.split(",");
			for (int j = 0; j < objs.size(); j++) {
				T t = objs.get(j);
				for (int k = 0; k < cols.length; k++) {
					Method method = t.getClass().getDeclaredMethod("get"+cols[k]);
					Object obj = method.invoke(t);
					if(obj instanceof Date){  //日期
						sheet.addCell(new jxl.write.DateTime(k,j+1,(Date)obj,date_format));
					}else if(obj instanceof String){
						Label LblContent = new Label(k, j + 1, (String)obj, wcfFC);
						sheet.addCell(LblContent);
					}else if(obj instanceof Integer){  //整数
						sheet.addCell(new jxl.write.Number(k,j+1,(Integer)obj, num_format));
					}else if(obj instanceof Double){   //浮点数
						Label LblContent = new Label(k, j + 1, String.valueOf(obj), float_format);
						sheet.addCell(LblContent);
					}
					
				}
			}
			book.write();
		} finally {
			if (book != null) {
				book.close();
			}

			if (fio != null)
				fio.close();
		}
		String path = request.getRequestURL().toString();
		String fileurl="";
		if(port>0){
			Pattern pattern = Pattern.compile("([1-9][0-9]{1,2}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})/\\w{1,}");
			Matcher matcher =pattern.matcher(path);
			String url ="";
			if(matcher.find()){
				url= matcher.group(1);
			}
			fileurl = request.getRequestURL().toString()
					.replaceAll("/\\w{1,}.do", "").replaceAll("[1-9][0-9]{1,2}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}",url+":"+port)
					+ "/temp/" + fileName + ".xls";
		}else{
			fileurl = path
			.replaceAll("/\\w{1,}.do", "")
			+ "/temp/" + fileName + ".xls";
		}
		
		
		return fileurl;
	}

	

	/**
	 * 导出Map集合
	 * @param <T>
	 * @param tempxlsfile
	 * @param response
	 * @param request
	 * @param fileName
	 * @param objs
	 * @param fields
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static String exportToExcelHeadJxlMap(String tempxlsfile,HttpServletResponse response,HttpServletRequest request,String fileName,List<HashMap> objs, 
		String fields , String headers,int port)
	throws Exception{
		FileOutputStream fio = null;
		WritableWorkbook book = null;
		try {

			File fout = new File(tempxlsfile);

			if (fout.exists())
				fout.delete();

			fio = new FileOutputStream(fout);

			WorkbookSettings workbookSettings = new WorkbookSettings();
			// ISO-8859-1
			workbookSettings.setEncoding("utf-8"); 
			// 创建文件
			book = jxl.Workbook.createWorkbook(fio, workbookSettings);
			//自适应宽度
			CellView view = new CellView();
			view.setAutosize(true);
			
			WritableSheet sheet = book.createSheet("数据", 0);
			
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
			//文本单元格
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
			
			//整数数字单元格
			jxl.write.WritableCellFormat num_format = new jxl.write.WritableCellFormat(wfc,NumberFormats.INTEGER);
			num_format.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			// 把水平对齐方式指定为居中
			num_format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			num_format.setAlignment(jxl.format.Alignment.LEFT);
			// 设置自适应大小
			num_format.setShrinkToFit(true);
			// 自动换行
			num_format.setWrap(true);
			
			//浮点数单元格
			jxl.write.WritableCellFormat float_format= new jxl.write.WritableCellFormat(wfc,NumberFormats.FLOAT);
			float_format.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			// 把水平对齐方式指定为居中
			float_format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			float_format.setAlignment(jxl.format.Alignment.LEFT);
			// 设置自适应大小
			float_format.setShrinkToFit(true);
			// 自动换行
			float_format.setWrap(true);
			//日期单元格
			jxl.write.WritableCellFormat date_format= new jxl.write.WritableCellFormat(wfc,new DateFormat("yyyy-MM-dd HH:mm:ss"));
			date_format.setBorder(jxl.format.Border.ALL,
					jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			// 把水平对齐方式指定为居中
			date_format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			date_format.setAlignment(jxl.format.Alignment.LEFT);
			// 设置自适应大小
			date_format.setShrinkToFit(true);
			
			String[] columnNames = headers.split(",");
			//设置列宽自适应
			for(int m=0;m<columnNames.length;m++){
				sheet.setColumnView(m, view);
			}
			// 生成第一行标题数据	
			for (int i = 0; i < columnNames.length; i++) {
				Label LblTitle = new Label(i, 0, columnNames[i], wcfFCtitle);
				sheet.addCell(LblTitle);
			}

			
			String[] cols = fields.split(",");
			for (int j = 0; j < objs.size(); j++) {
				HashMap<String,Object> obj = objs.get(j);
				for (int k = 0; k < cols.length; k++) {
					Object temp = obj.get(cols[k]);
					if(temp instanceof Date){  //日期
						//System.out.println("进入日期:" + temp);
						sheet.addCell(new jxl.write.DateTime(k,j+1,(Date)temp,date_format));
					}else if(temp instanceof String){
						//System.out.println("进入字符:" + temp);
						Label LblContent = new Label(k, j + 1, (String)temp, wcfFC);
						sheet.addCell(LblContent);
					}else if(temp instanceof Integer){  //整数
						//System.out.println("进入整数:" + temp);
						sheet.addCell(new jxl.write.Number(k,j+1,(Integer)temp, num_format));
					}else if(temp instanceof Double){   //浮点数
						//System.out.println("进入double:" + temp);
						Label LblContent = new Label(k, j + 1, String.valueOf(temp), float_format);
						sheet.addCell(LblContent);
					}
					else if(temp instanceof Float){   //浮点数
						//System.out.println("进入double:" + temp);
						Label LblContent = new Label(k, j + 1, String.valueOf(temp), float_format);
						sheet.addCell(LblContent);
					}
					else if(temp instanceof BigDecimal){   //浮点数
						//System.out.println("进入BigDecimal:" + temp);
						Label LblContent = new Label(k, j + 1, String.valueOf(temp), float_format);
						sheet.addCell(LblContent);
					}
					else{  
						//System.out.println("进入其它:" + temp);
						if (temp!=null){
							Label LblContent = new Label(k, j + 1, String.valueOf(temp), wcfFC);
							sheet.addCell(LblContent);													
						}
						else{
							Label LblContent = new Label(k, j + 1, "", wcfFC);
							sheet.addCell(LblContent);																				
						}
					}
					
				}
			}
			book.write();
		} finally {
			if (book != null) {
				book.close();
			}

			if (fio != null)
				fio.close();
		}
		String path = request.getRequestURL().toString();
		String fileurl="";
		if(port>0){
			Pattern pattern = Pattern.compile("([1-9][0-9]{1,2}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})/\\w{1,}");
			Matcher matcher =pattern.matcher(path);
			String url ="";
			if(matcher.find()){
				url= matcher.group(1);
			}
			fileurl = request.getRequestURL().toString()
					.replaceAll("/\\w{1,}.do", "").replaceAll("[1-9][0-9]{1,2}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}",url+":"+port)
					+ "/temp/" + fileName + ".xls";
		}else{
			fileurl = path
			.replaceAll("/\\w{1,}.do", "")
			+ "/temp/" + fileName + ".xls";
		}
		return fileurl;
	}
	
	/**

	 * 根据方法获取字段名
	 * @param excelHeader
	 * @return
	 */
	private static String getPropertyName(ExcelHeader excelHeader) {
		String temp = excelHeader.getMethodName().substring(3);
		return temp.substring(0, 1).toLowerCase() + temp.substring(1);
	}

	/**
	 * 设置表格样式
	 * @param workbook
	 * @param position
	 * @return
	 */
	private static CellStyle setCellStyle(HSSFWorkbook workbook,
			String position) {
		
		CellStyle style = workbook.createCellStyle();
		// 设置单元格字体水平、垂直居中
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 设置单元格边框
		style.setBorderBottom((short)1);
		style.setBorderLeft((short)1);
		style.setBorderRight((short)1);
		style.setBorderTop((short)1);
		//style.setShrinkToFit(true);
		// 设置单元格字体
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		/**
		if(position_title.equals(position)){
			font.setFontHeightInPoints((short)11);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		}else{
			font.setFontHeightInPoints((short)10);
		}**/
		style.setFont(font);
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index); 
		style.setWrapText(true);
		return style;
	}

	/**
	 * 获取excel标题列表
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List<ExcelHeader> getHeaderList(Class clazz) {
		
		List<ExcelHeader> headers = new ArrayList<ExcelHeader>();
		java.lang.reflect.Method [] ms = clazz.getDeclaredMethods();
		for(java.lang.reflect.Method m : ms){
			String mn = m.getName();
			if(mn.startsWith("get")){
				if(m.isAnnotationPresent(ExcelDataMapper.class)){
					ExcelDataMapper dataMapper = m.getAnnotation(ExcelDataMapper.class);
					headers.add(new ExcelHeader(dataMapper.title(), dataMapper.order(),dataMapper.width(), mn));
				}
			}
		}
		return headers;
	}
	

	private static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if(!isNum.matches()){
			return false;			
		}else{
			return true;
		}
			
	}
}