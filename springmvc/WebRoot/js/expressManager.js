 //客户列表
var customerlist = {};

//初始化后得到的数据
if (typeof allData === "undefined") allData = {};

//分页设定
var pageSizeSettingA = 12;
var pageSizeSettingB = 20;

//提示框
function showTips(text){
	var dlg = $("#tips_dlg");
	dlg.show();

	var dbody = dlg.find(".dialog_body");
	dbody.css("left", ($(document).width()-dbody.width())/2+"px");
	dbody.css("top", "100px");

	$(".tips").html(text);
}

$(function(){
	//alert(allData.curLogin.CustomerID);
	if(allData.curLogin.CustomerID > 0) 
	{
		$("#cu_id_div").empty();
		$("#cu_id_div1").empty();
		$("#cu_id_div1").attr("class","");
		$("#cu_id_div2").empty();
		$("#cu_id_div2").attr("class","");
		$("#cu_id_div3").empty();
		$("#cu_id_div3").attr("class","");
		$("#cu_id_div4").empty();
		$("#cu_id_div4").attr("class","");
		$("#qty_add").empty();
		$("#qty_add").attr("class","");
		$("#qty_add").attr("hidden","");
		$("#btn_save").empty();
		$("#btn_save").attr("class","");	
	}
	//当前选中的行
	var selectedRow = null;
	//当前查询条件
	var lastSearchParamA = null;	//客户表格
	var lastSearchParamB = null;	//快递记录
	//客户记录的页面控制
	page_ctrl_to_callback = function(pn){
		if (lastSearchParamA){
			lastSearchParamA.pn = pn;

			$.ajax({
				url: "./qryCustomerDeliveryNum.do", 
				type: "post", 
				data: JSON.stringify(lastSearchParamA), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						clearGrid();	//清除客户表
						fillGrid(rsp.data);		//填充新数据
						
						//翻页后的相应操作调用pageCtrl.js
						if (typeof(afterChangedPage)!=="undefined"){
							afterChangedPage(rsp.pageInfo);
						}
					}
				}, 
				error: function(){
					showTips("请求出错了");
				},
				dataType: "json"
			});
		}
	};
	
	//详细记录的页面控制
	page_ctrl_to_callback_detail = function(pn){
		if (lastSearchParamB){
			lastSearchParamB.pn = pn;

			$.ajax({
				url: "./qryCustomerDeliveryNumBook.do", 
				type: "post", 
				data: JSON.stringify(lastSearchParamB), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						clearDetailGrid();	//清除详细表
						fillDetailGrid(rsp.data);		//填充详细表
						
						//翻页后的相应操作调用pageCtrl.js
						if (typeof(afterChangedPage_detail)!=="undefined"){
							afterChangedPage_detail(rsp.pageInfo);
						}
					}
				}, 
				error: function(){
					showTips("请求出错了");
				},
				dataType: "json"
			});
		}
	};

	$.ajaxSetup({async: false});
	
	//"客户"下拉菜单(调用inputSelect.js)
	registInputSelect({
		id:"customers", 
		url:"./qryCustomerList.do", 
		onload:function(rsp, itemHtmls){
			if (rsp instanceof Array){
				for(var i=0; i<rsp.length; i++){
					//input_select_list_item_html是全局方法，可生成列表子项的html描述
					itemHtmls.push(input_select_list_item_html(rsp[i].ID, $.trim(rsp[i].Name)));
				}
			}
		}
	});
	
	//获取所有客户列表
	function getCustomerlist(){
			$.ajax({
				url: "./qryCustomerList.do", 
				type: "post", 
				data: "{}", 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						customerlist = rsp.data;	//获取到的客户列表放到全局变量中
					}
				}, 
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
	}
	
	//初始化函数
	function init(){
		if (allData)
		{
			//登录用户信息
			if ("curLogin" in allData)
			{
				var info = $(".acount_info");
				info.html(allData.curLogin.Name+"["+allData.curLogin.CName+"]");
				info.attr("data", allData.curLogin.ID);

				var logo = $("#systemLogo");
				logo.attr("src",allData.curLogin.SystemLogo );
				logo = $("#systemName");
				logo.text(allData.curLogin.SystemName);
			}
			
			//读取快递代号列表
			if ("delivery" in allData)
			{
				var selectlist = $("#deliverycodeSelect");
				var selectlistB = $("#deliverycodeSelectB");
				var ln = allData.delivery.length;
				$("<option>请选择</option>").appendTo(selectlist);
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.delivery[i].id+"'>"+allData.delivery[i].name+"</option>").appendTo(selectlist);
				}
			}

			if ("menu" in allData){
				setActiveMenu("业务管理", "快递记录");
			}

			
			//初始化分页器
			if (typeof(resetPageCtrl)!=="undefined")
			{
				resetPageCtrl({rowCnt:0, page:0, psize:10});	//客户表
				resetPageCtrl_detail({rowCnt:0, page:0, psize:10});		//记录表
			}
		}
		//初始化日期输入框：
		var myDate = new Date();
		$("#date_time_start").val(myDate.getFullYear() + "-" + (myDate.getMonth()+1) + "-" + (myDate.getDate()-1)); 
		$("#date_time_end").val(myDate.getFullYear() + "-" + (myDate.getMonth()+1) + "-" + myDate.getDate()); 
	};
	

	
	//清除客户表
	function clearGrid(){
		selectedRow = null;
		$("#account_list tr:gt(0)").each(function(){
			$(this).remove();
		});
	}
	
	//清除详细表
	function clearDetailGrid(){
		$("#detail_list tr:gt(0)").each(function(){
			$(this).remove();
		});
	}
	
	//添加行数据(客户表)
	function addGridRow(){
		var txt = "<tr> <td hidden></td><td></td><td></td><td></td><td></td> </tr>";
		$("#account_list tbody").append(txt);
		var tr = $("#account_list tr:last");
		var cells = tr.find("td");
		
		$(cells[0]).addClass("tb_fix_width_short");
		$(cells[0]).html("<label hidden></label>");	//客户ID
		$(cells[1]).html("<label></label>");	//客户名称
		$(cells[2]).html("<label></label>");	//快递名称
		$(cells[3]).html("<label></label>");	//快递标识
		$(cells[4]).html("<label></label>");	//剩余数量
		
		return tr;
	}
	
	//添加行数据(记录表)
	function addDetailGridRow(){
		var txt = "<tr> <td hidden></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td> </tr>";
		$("#detail_list tbody").append(txt);
		var tr = $("#detail_list tr:last");
		var cells = tr.find("td");

		$(cells[0]).addClass("tb_fix_width_short");	// 客户标识
		$(cells[0]).html("<label hidden></label>");
		$(cells[1]).addClass("tb_fix_width_short");	// 快递标识
		$(cells[1]).html("<label></label>");		// 客户名称
		$(cells[2]).html("<label></label>");		// 快递名称
		$(cells[3]).html("<label></label>");		// 发生数量
		$(cells[4]).html("<label></label>");		// 结存数量
		$(cells[5]).html("<label></label>");		// 业务类型
		$(cells[6]).html("<label></label>");		// 时间
		$(cells[7]).html("<label></label>");		// 备注
		$(cells[8]).html("<label></label>");		// 单号
		
		return tr;
	}

	//填充数据(客户表)
	function fillGrid(data){
		var ln = data.length;
		for(var i=0; i<ln; i++)
		{
			var tr = addGridRow();
			var label = tr.find("label");
			var record = data[i];
			
			//客户ID
			var field = $(label[0]);
			field.attr("cid_data",record.CustomerID);	//存储客户ID
			field.attr("dID_data",record.DeliveryID);	//存储快递ID
			field.text(record.CustomerID);
			//客户名称
			field = $(label[1]);
			field.text(record.CustomerName);
			//快递名称
			field = $(label[2]);
			field.text(record.DeliveryName);
			//快递标识
			field = $(label[3]);
			//field.text(record.DeliveryID);
			field.text(record.DeliveryCode);
			//剩余数量
			field = $(label[4]);
			field.text(record.Qty);
		}
	}
	
	//填充数据(记录表)
	function fillDetailGrid(data){
		var ln = data.length;
		for(var i=0; i<ln; i++)
		{
			var tr = addDetailGridRow();
			var label = tr.find("label");
			var record = data[i];
			//客户标识
			field = $(label[0]);
			field.text(record.CustomerID);
			//快递标识
			field = $(label[1]);
			//field.text(record.DeliveryID);
			field.text(record.DeliveryCode);
			//客户名称
			field = $(label[2]);
			field.text(record.CustomerName);
			//快递名称
			field = $(label[3]);
			field.text(record.DeliveryName);
			//发生数量
			field = $(label[4]);
			field.text(record.Qty);
			//结存数量
			field = $(label[5]);
			field.text(record.CloseQty);
			//业务类型
			field = $(label[6]);
			field.text(record.SheetType);
			//时间
			field = $(label[7]);
			field.text((1900+record.STime.year)+" - "+(1+record.STime.month)+" - "+(record.STime.date)) + ", " + (record.STime.hours+0) + ":" + (record.STime.minutes+0) + ":" + (record.STime.seconds+0);
			//备注
			field = $(label[8]);
			field.text(record.Note);
			//单号
			field = $(label[9]);
			field.text(record.SID);
		}
	}
	
	//选择的行高亮显示(ok)
	$("table").on("click","tr:gt(0)",function(){
		if (selectedRow!==this){
			if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
			
			$(this).addClass("tr_high_light");
			selectedRow = this;
		}
	});
	
	//关闭提示窗口
	$(".dialog_close").click(function(){
		$("#tips_dlg").hide();
	});
	$("#dlg_btn").click(function(){
		$("#tips_dlg").hide();
	});
	
	//客户快递剩余数量查询
	$("#search").click(function(){
		var cName = $.trim($("#customers").val());		//客户名称
		var dCode = $("#deliverycodeSelect option:selected").text();		//快递code
		//要提交的参数
		lastSearchParamA = {};
		//分页设置
		lastSearchParamA.pn = 0;
		lastSearchParamA.pageSize = pageSizeSettingA;

		//客户名称
		if(cName.length > 0)
		{
			lastSearchParamA.customerName = cName;
		}
		//快递code
		if(dCode !== "请选择")
		{
			lastSearchParamA.deliverycode = dCode;
		}
		//发送请求
		$.ajax({
			url: "./qryCustomerDeliveryNum.do", 
			type: "post", 
			data: JSON.stringify(lastSearchParamA), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					clearGrid();
					fillGrid(rsp.data);
					//alert(rsp.data)
					if (typeof(resetPageCtrl)!=="undefined"){
						resetPageCtrl(rsp.pageInfo);
					}
				}
			}, 
			error: function(){
				showTips("请求出错了");
			}, 
			dataType: "json"
		});
	});
	
	//流水记录查询
	$("#btn_detail").click(function()
	{//格式:{"customerID":0, deliveryID:1, beginTime:"2015-04-01 00:00:00",endTime:"2015-05-01 00:00:00"}
		//判断是否有选择一条记录
		if (selectedRow==null)
		{
			showTips("请在上方列表选择一条记录！");
			return;
		}
		//判断时间是否有选择
		if($("#date_time_start").val() === "" || $("#date_time_end").val() === "")
		{
			showTips("请选择时间!");
			return;
		}
		var label = $(selectedRow).find("label");
		var cid = $(label[0]).attr("cid_data");
		var dID = $(label[0]).attr("dID_data");
		var date_start=$("#date_time_start").val()+" 00:00:00";		//*开始时间
		var date_end=$("#date_time_end").val()+" 23:59:59";		//*结束时间
		//准备要提交的数据
		lastSearchParamB = {};
		//分页设置
		lastSearchParamB.pn = 0;
		lastSearchParamB.pageSize = pageSizeSettingB;
		//客户ID
		if (typeof(cid)!=="undefined" && cid != null && cid.length > 0)
			lastSearchParamB.customerID = parseInt(cid);
		else
		{
			showTips("请在上方列表选择一条记录！");
			return;
		}
		//快递ID
		if (typeof(dID)!=="undefined" && dID != null && dID.length > 0)
			lastSearchParamB.deliveryID = parseInt(dID);
		else
		{
			showTips("请在上方列表选择一条记录！");
			return;
		}
		//时间
		lastSearchParamB.beginTime = date_start;
		lastSearchParamB.endTime = date_end;
		
		//发送请求
		$.ajax({
			url: "./qryCustomerDeliveryNumBook.do", 
			type: "post", 
			data: JSON.stringify(lastSearchParamB), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					clearDetailGrid();	        //这里需要改成清除DetailGrid的函数
					fillDetailGrid(rsp.data)
					if (typeof(resetPageCtrl_detail)!=="undefined"){
						resetPageCtrl_detail(rsp.pageInfo);
					}
				}
			}, 
			error: function(){
				showTips("请求出错了");
			}, 
			dataType: "json"
		});
	});
	
	//添加数量
	$("#btn_save").click(function()
	{
		//判断是否有选择一条记录
		//if (selectedRow==null)
		//{
			//showTips("请选择一项进行操作！");
			//return;
		//}
		//var label = $(selectedRow).find("label");
		//var cid = $(label[0]).attr("cid_data");
		//var dID = $(label[0]).attr("dID_data");
		var cid = $("#customers").attr("data");
		var dID = $("#deliverycodeSelect").val();
		var dVal = $("#qty_add").val();
		var save_cmd={};
		if (typeof(cid)!=="undefined" && cid != null && cid.length > 0)
			save_cmd.customerID = parseInt(cid);
		else
		{
			showTips("请选择一项进行操作！");
			return;
		}
		if (typeof(dID)!=="undefined" && dID != null && dID.length > 0)
			save_cmd.deliveryID = parseInt(dID);
		else
		{
			showTips("请选择一项进行操作！");
			return;
		}
		if (dVal != null && dVal.length > 0)
			save_cmd.qty = parseInt(dVal);
		else
		{
			showTips("请输入要添加的数量！");
			return;
		}
		
		//发送请求
		$.ajax
		({

			url: "./addCustomerDeliveryNum.do", 
			type: "post", 
			data: JSON.stringify(save_cmd),
			success: function(rsp)
			{
				if (rsp.errorCode!=0)
					showTips(rsp.msg);
				else
				{
					$("#qty_add").val("")
					showTips("增加快递条数成功!");
				}
			}, 
			error: function()
			{
				showTips("请求出错了");
			},
			dataType: "json"
		});
		//刷新列表
		page_ctrl_to_callback();
	});
	
	//初始化
	init();
});

