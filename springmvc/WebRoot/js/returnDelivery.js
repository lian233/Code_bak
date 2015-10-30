 //客户列表
var customerlist = {};

//列变量
var ci_cname = 0;
var ci_dname = 1;
var ci_dcode = 2;
var ci_dsheetid = 3;
var ci_stime = 4;
var ci_rflag = 5;
var ci_address = 6;
var ci_linkman = 7;


//初始化后得到的数据
if (typeof allData === "undefined") allData = {};

//分页设定
var pageSizeSetting = 100;

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
		return ;
	}

	//当前选中的行
	var selectedRow = null;
	//当前查询条件
	var lastSearchParam = null;	

	//客户记录的页面控制
	page_ctrl_to_callback = function(pn){
		if (lastSearchParam){
			lastSearchParam.pn = pn;

			$.ajax({
				url: "./qryCustomerDeliveryNumBook.do", 
				type: "post", 
				data: JSON.stringify(lastSearchParam), 
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
				var ln = allData.delivery.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.delivery[i].id+"'>"+allData.delivery[i].name+"</option>").appendTo(selectlist);
				}
			}

			if ("menu" in allData){
				setActiveMenu("业务管理", "快递回收");
			}

			
			//初始化分页器
			if (typeof(resetPageCtrl)!=="undefined")
			{
				resetPageCtrl({rowCnt:0, page:0, psize:10});	//客户表
			}
		}
	};
	

	
	//清除客户表
	function clearGrid(){
		selectedRow = null;
		$("#account_list tr:gt(0)").each(function(){
			$(this).remove();
		});
	}
	
	
	//添加行数据
	function addGridRow(){
		var txt = "<tr><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td></tr>";
		$("#account_list tbody").append(txt);
		var tr = $("#account_list tr:last");
		var cells = tr.find("td");
		
		
		return tr;
	}

	//填充数据
	function fillGrid(data){
		var ln = data.length;
		for(var i=0; i<ln; i++)
		{
			var tr = addGridRow();
			var label = tr.find("label");
			var record = data[i];
					
			var field = $(label[ci_cname]);
			field.text(record.CustomerName);
			
			field = $(label[ci_dname]);
			field.text(record.DeliveryName);
			
			field = $(label[ci_dcode]);
			field.text(record.DeliveryCode);

			field = $(label[ci_dsheetid]);
			field.text(record.DeliverySheetID);

			var dt = new Date();
			dt.setTime(record.STime.time);
			var d  = record.STime;
			var dd = (dt.getFullYear()-2000)+"-"+(dt.getMonth()+1)+"-"+d.date+" "+d.hours+":"+d.minutes+":"+d.seconds;

			field = $(label[ci_stime]);
			field.text(dd);
			
			var temp = "";
			field = $(label[ci_rflag]);
			if (record.RouteFlag==0){
				temp = "占用";
			}
			else if (record.RouteFlag==1){
				temp = "路由";
			}
			else if (record.RouteFlag==2){
				temp = "完成";
			}
			else if (record.RouteFlag==3){
				temp = "可回收";
			}
			else if (record.RouteFlag==4){
				temp = "取消";
			}
			else if (record.RouteFlag==100){
				temp = "回收";
			}
			else{
				temp = record.RouteFlag;
			}
			field.text(temp);
			
			field = $(label[ci_address]);
			field.text(record.State + " " + record.City + " "  + record.District + " "  + record.Address );

			field = $(label[ci_linkman]);
			field.text(record.LinkMan + " " + record.Mobile);
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
		$("#import_dlg").hide();
		$("#tips_dlg").hide();
	});
	$("#dlg_btn").click(function(){
		$("#import_dlg").hide();
		$("#tips_dlg").hide();
	});
	
	//查询
	$("#search").click(function(){
		var customerID = parseInt($("#customers").attr("data"));
		var deliveryID=parseInt($("#deliverycodeSelect").val());
		var outDays = parseInt($.trim($("#outDays").val()));

		//要提交的参数
		lastSearchParam = {};
		//分页设置
		lastSearchParam.pn = 0;
		lastSearchParam.pageSize = pageSizeSetting;

		lastSearchParam.deliveryID = deliveryID;
		lastSearchParam.customerID = customerID;
		lastSearchParam.sheetType=400100;
		lastSearchParam.canReturn=1;
		lastSearchParam.outDays = outDays;

		if (lastSearchParam.deliveryID===-1) delete lastSearchParam["deliveryID"];
		if (isNaN(lastSearchParam.customerID))
		{
			delete lastSearchParam["customerID"];
		}

		//发送请求
		$.ajax({
			url: "./qryCustomerDeliveryNumBook.do", 
			type: "post", 
			data: JSON.stringify(lastSearchParam), 
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
	
	//导出
	$("#export").click(function(){
		var customerID = parseInt($("#customers").attr("data"));
		var deliveryID=parseInt($("#deliverycodeSelect").val());
		var outDays = parseInt($.trim($("#outDays").val()));

		//要提交的参数
		lastSearchParam = {};
		//分页设置
		lastSearchParam.pn = 0;
		lastSearchParam.pageSize = pageSizeSetting;

		lastSearchParam.deliveryID = deliveryID;
		lastSearchParam.customerID = customerID;
		lastSearchParam.sheetType=400100;
		lastSearchParam.canReturn=1;
		lastSearchParam.outDays = outDays;

		if (lastSearchParam.deliveryID===-1) delete lastSearchParam["deliveryID"];
		if (isNaN(lastSearchParam.customerID))
		{
			delete lastSearchParam["customerID"];
		}

		$.ajax({
				type : "POST",
				url : "exportCustomerDeliveryNumBook.do",
				dataType:'json',
				contentType:"application/json;charset=UTF-8",
				data:JSON.stringify(lastSearchParam),
				async : false,
				success : function(data) {
					if (data.errorCode==0)
					{
						window.location.href=data.data;
					}else{
						showTips(data.msg);
					}										
				},error:function(data){
					showTips("导出错误" );
				}
			});

	});
	
	$("#import").click(function(){
		var dlg = $("#import_dlg");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "30px");
	});

	$("#import_upload_btn").click(function(){
		var _this = this;
		
		var file = $("#file_for_import").val();
		if (file.length>0){
			$("#uploadForm").ajaxSubmit({
				url : "importReturnDelivery.do",
				secureuri : false,
				fileElementId : 'file_for_import',
				dataType : 'json',
				success : function(data) {
					if(data.errorCode == 0){
						$(_this).parents(".dialog").hide();
										
						var result = data.data;
						var ln = result.length;
						if (ln<=0)
						{
							showTips("导入文件成功："+data.msg);
						}else{
							var msg = "";							
							for(var i=0; i<ln; i++){
								var r = result[i];
								if (r.errorCode!=0)
								{
									msg = msg + r.msg+'。';
								}
							}
							if (msg=="")
							{
								showTips("导入文件成功："+data.msg);
							}else{
								showTips(data.msg+"<br>"+msg);
							}
						}
						
					}else{
						showTips(data.msg);
					}
				},
				error : function(xhr, msg){
					showTips(msg);
				}
			});
		}else{
			showTips("请选择一个文件");
		}

		
	});


	//初始化
	init();
});

