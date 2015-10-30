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
			}
			
			//读取快递代号列表
			if ("delivery" in allData)
			{
				var selectlist = $("#deliverycodeSelect");
				var selectlistB = $("#deliverycodeSelectB");
				var ln = allData.delivery.length;
				$("<option>请选择</option>").appendTo(selectlist);
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.delivery[i].code+"'>"+allData.delivery[i].code+"</option>").appendTo(selectlist);
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
	
	//添加行数据(路由表)
	function addGridRow()
	{
		var txt = "<tr> <td></td><td></td><td></td><td></td><td></td> </tr>";
		$("#account_list tbody").append(txt);
		var tr = $("#account_list tr:last");
		var cells = tr.find("td");
		
		$(cells[0]).addClass("tb_fix_width_short");
		$(cells[0]).html("<label></label>");	//客户ID
		$(cells[1]).html("<label></label>");	//客户名称
		$(cells[2]).html("<label></label>");	//快递名称
		$(cells[3]).html("<label></label>");	//快递标识
		$(cells[4]).html("<label></label>");	//剩余数量
		
		return tr;
	}
	
	//添加行数据(记录表)
	function addDetailGridRow(){
		var txt = "<tr> <td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td> </tr>";
		$("#detail_list tbody").append(txt);
		var tr = $("#detail_list tr:last");
		var cells = tr.find("td");

		$(cells[0]).addClass("tb_fix_width_short");	// 客户标识
		$(cells[0]).html("<label></label>");
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
			field.attr("cid_data",record.mailNo);	//快递单号
			//field.attr("dID_data",record.DeliveryID);	//存储快递ID
			field.text(record.mailNo);
			var data_label1 = "";
			var data_label2 = "";
			var data_label3 = "";
			var data_label4 = "";
			for(var j=0;j<record.traces.length;j++)
			{
				data_label1 = data_label1 + (record.traces[i].acceptTime + "\n");
				data_label2 = data_label2 + (record.traces[i].acceptAddress + "\n");
				data_label3 = data_label3 + (record.traces[i].scanType + "\n");
				data_label4 = data_label4 + (record.traces[i].remark + "\n");
			}
			//
			field = $(label[1]);
			field.text(data_label1);
			//
			field = $(label[2]);
			field.text(data_label2);
			//
			field = $(label[3]);
			//field.text(record.DeliveryID);
			field.text(data_label3);
			//
			field = $(label[4]);
			field.text(data_label4);

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
	
	//获取按钮:
	$("#btn_get").click(function()
	{
		var de_cop_code = $("#deliverycodeSelect").val(); //快递公司代号简称
		var de_order	= $("#de_order").val();   //快递单号  
		//发送请求  {"companyCode":"HTKY",orders:["50041877905024"]}
		var cmd={orders:[]};
		cmd.companyCode = de_cop_code;
		var order = de_order.split("\n");  //订单数组
		for(var i=0;i<order.length;i++)
		{
			cmd.orders.push(order[i]);
		}
		//cmd.orders = order;   
		//alert(JSON.stringify(cmd));
		$.ajax
		({
			url: "./qrydeliveryRouteInfo.do", 
			type: "post", 
			//data: JSON.stringify(cmd), 
			data: JSON.stringify(cmd), 
			success: function(rsp)
			{
				rsp_result=$.parseJSON(rsp);
				if (rsp_result.errorCode!=0)
				{
					showTips(rsp_result.msg);
				}
				else
				{
					clearGrid();
					fillGrid(rsp_result.data);
				}
			}
		});
		
		
	});
	
	//初始化
	init();
});

