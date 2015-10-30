/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
 //allData变量声明
 //用于存储返回的初始化数据
 if (typeof allData === "undefined") allData = {};
 
 //品牌列表
 var gradelist = null;
 
 //产品线列表
 var productLineList = null;

 //字段索引定义
var idxCode = 1;
var idxName = 2;
var idxLine = 3;
var idxGrade = 4;


//每页行数设定
var pageSizeSetting = 12;

//弹出提示框
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
	//最后搜索的条件参数
	var lastSearchParam = null;
	
	//翻页功能回调函数(ok)
	page_ctrl_to_callback = function(pn){
		if (lastSearchParam){
			lastSearchParam.pn = pn;

			$.ajax({
				url: "./qryCustomerProductGrade.do", 
				type: "post", 
				data: JSON.stringify(lastSearchParam), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						clearGrid();
						fillGrid(rsp.data);
						
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
	
	//初始化数据
	function init(){
		if (allData){
			//当前登录的管理员信息
			if ("curLogin" in allData){
				var info = $(".acount_info");
				info.html(allData.curLogin.Name+"["+allData.curLogin.CName+"]");
				info.attr("data", allData.curLogin.ID);

				var logo = $("#systemLogo");
				logo.attr("src",allData.curLogin.SystemLogo );
				logo = $("#systemName");
				logo.text(allData.curLogin.SystemName);
			}

			
			//初始化菜单栏
			if ("menu" in allData){
				setActiveMenu("资料管理", "客户分销产品");
			}

			
			//读取等级信息
			if ("grade" in allData){
				gradelist = allData.grade;
				var selectlist = $("#selGrade");
				var selectlist2 = $("#selAddGrade");
				var ln = gradelist.length;
				
				for(var i=0; i<ln; i++){
					$("<option value='"+gradelist[i].id+"'>"+$.trim(gradelist[i].name)+"</option>").appendTo(selectlist);
					$("<option value='"+gradelist[i].id+"'>"+$.trim(gradelist[i].name)+"</option>").appendTo(selectlist2);
				}
			}
			
			//读取线条列表
			if ("productLine" in allData){
				productLineList = allData.productLine;
				var selectlist = $("#selProductLine");
				var selectlistB = $("#selAddProductLine");
				var ln = productLineList.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+productLineList[i].ID+"'>"+$.trim(productLineList[i].Name)+"</option>").appendTo(selectlist);
					$("<option value='"+productLineList[i].ID+"'>"+$.trim(productLineList[i].Name)+"</option>").appendTo(selectlistB);
					
				}
			}
			
			//设置分页
			if (typeof(resetPageCtrl)!=="undefined"){
				resetPageCtrl({rowCnt:0, page:0, psize:pageSizeSetting});
			}
			
			//按情况隐藏操作栏
			hideCustomer();
		}

	};
	
	//全选(ok)
	$("#all_check").click(function(){
		var _this = this;
		var v = this.checked;
		
		$("table :checkbox").each(function(){
			if (this!==_this) this.checked = v;
		});
	});
	
	//禁止编辑表格(ok)
	function disableGrid(){
		$("table input").attr("disabled", "disabled");
		$("table select").attr("disabled", "disabled");
	}
	
	//允许编辑表格(ok)
	function enableGrid(){
		$("table input").removeAttr("disabled");
		$("table select").removeAttr("disabled");
	}
	
	//清除表格所有数据(ok)
	function clearGrid(){
		selectedRow = null;
		$("#CustomerList tr:gt(0)").each(function(){
			$(this).remove();
		});
		var chk_all = $("#all_check")[0];
		chk_all.checked = false;
	}
	
	//添加新行(ok)
	function addGridRow(){
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td></tr>";
		$("#CustomerList tbody").append(txt);
		var tr = $("#CustomerList tr:last");
		var cells = tr.find("td");
		//$(cells[0]).addClass("");
		
		//复选框
		$(cells[0]).html("<input type=\"checkbox\" class=\"checkbox\"/>");

		/*
		//客户编码
		$(cells[idxCode]).html("<label></label>");
		//客户名称
		$(cells[idxName]).html("<label></label>");
		//标题
		$(cells[idxTitle]).html("<label></label>");
		//产品线
		$(cells[idxLine]).html("<label></label>");
		//等级
		$(cells[idxGrade]).html("<label></label>");
		*/

		hideCustomer();
		
		return tr;
	}
	
	//填充数据到表格(ok)
	function fillGrid(data){
		var ln = data.length;
		for(var i=0; i<ln; i++)
		{
			var tr = addGridRow();
			var CustomerList = data[i];
			
			var cells = tr.find("td");
			$(cells[idxCode]).html(CustomerList.DetailID);
			$(cells[idxName]).html(CustomerList.CustomerName);
			$(cells[idxLine]).html(CustomerList.ProductLineName);
			$(cells[idxGrade]).html(CustomerList.GradeName);

			var field = $(cells[0]);
			field.attr("CustomerID",CustomerList.CustomerID);
			field.attr("ProductLineID",CustomerList.ProductLineID);
			field.attr("GradeID",CustomerList.GradeID);
		}
	}
	

	//选择的行高亮显示(ok)
	$("#CustomerList").on("click","tr:gt(0)",function(){
		if (selectedRow!==this){
			if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
			
			$(this).addClass("tr_high_light");
			selectedRow = this;
		}
	});
	
	//查询按钮(ok)
	$("#search").click(function(){
		//取参数		
		var pid = $("#selProductLine").val();
		var grade = $("#selGrade").val();
		var name = $("#txtCustomerName").val();
		var distribution = document.getElementById("chkDistribution").checked;

		lastSearchParam = {};
		//填充参数
		lastSearchParam.pn = 0;
		lastSearchParam.pageSize =  parseInt($("#pageSize").val());;
		//产品线
		if(parseInt(pid) >= 0)
			lastSearchParam.productLineID = parseInt(pid);
		//等级
		if(parseInt(grade) >= 0)
			lastSearchParam.gradeID = parseInt(grade);
		//名称
		if(name.length > 0)
			lastSearchParam.customerName = name;
		//是否分销
		if(distribution)
			lastSearchParam.IsDistribute = 1;

		//刷新列表
		refList();
	});
	
	//执行查询,刷新列表(ok)
	function refList(){
		$.ajax({
			url: "./qryCustomerProductGrade.do", 
			type: "post", 
			data: JSON.stringify(lastSearchParam), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					clearGrid();
					fillGrid(rsp.data);
					
					if (typeof(resetPageCtrl)!="undefined"){
						resetPageCtrl(rsp.pageInfo);
					}
				}
			}, 
			error: function(){
				showTips("请求出错了");
			}, 
			dataType: "json"
		});
	}
	
	//加入分销(ok)
	$("#btnAdd").click(function(){
		//例行检查
		var selbool = false;
		$("#CustomerList tbody :checkbox").each(function(){
			if(this.checked)
			{
				selbool = true;
			}
		});

		if(!selbool)
		{
			showTips("请先勾选至少一个客户!");
			return;
		}
		
		//得到对话框对象
		var dlg = $("#AddCustomer_dialog");
		var dbody = $(dlg.find(".dialog_body"));

		//设置显示位置
		dbody.css("left", ($(document).width()- parseInt(dbody.css("width").replace("px","")))/2+"px");
		dbody.css("top",  "30px");
		
		//显示窗口
		dlg.show();
	});
	
	//取消加入分销操作(ok)
	$("#btnAddCancel").click(function(){
		var dlg = $("#AddCustomer_dialog");
		dlg.hide();
	});
	
	//确定加入分销操作(ok)
	$("#btnAddConfirm").click(function(){
		var dlg = $("#AddCustomer_dialog");
		var sel = $("#selAddProductLine");
		if(sel.val() == -1)
		{
			showTips("请选择要加入的产品线!");
			return;
		}

		var sel2 = $("#selAddGrade");
		if(sel2.val() == -1)
		{
			showTips("请选择要加入的等级!");
			return;
		}
		
		var data = {Customers:[]};
		$("#CustomerList tr:gt(0)").each(function(){
			var chkbox = $(this).find(".checkbox");
			if(chkbox[0].checked)
			{
				var cells = $(this).find("td");//?
				var cid = $(cells[0]).attr("CustomerID");
				if(typeof(cid) !== "undefined")
				{
					data.Customers.push(cid);
				}
			}
		});
		data.ProductLineID=parseInt(sel.val());
		data.GradeID =parseInt(sel2.val());
		
		$.ajax({
			url: "./addCustomerProductGrade.do", 
			type: "post", 
			data: JSON.stringify(data), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					showTips("操作成功!");
					dlg.hide();
					refList();
				}
			}, 
			error: function(){
				showTips("请求出错了");
			}, 
			dataType: "json"
		});
	
	});
	

	//删除 分销操作(ok)
	$("#btnDel").click(function(){		
		var data = {Customers:[]};
		$("#CustomerList tr:gt(0)").each(function(){
			var chkbox = $(this).find(".checkbox");
			if(chkbox[0].checked)
			{
				var cells = $(this).find("td");//?
				var cid = $(cells[0]).attr("CustomerID");
				var pid = $(cells[0]).attr("ProductLineID");
				var param = {};

				if(typeof(cid) !== "undefined" && typeof(pid) !== "undefined")
				{
					param.CustomerID = parseInt(cid);
					param.ProductLineID = parseInt(pid);
					data.Customers.push(param);
				}
			}
		});
		
		$.ajax({
			url: "./delCustomerProductGrade.do", 
			type: "post", 
			data: JSON.stringify(data), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					showTips("操作成功!");
					dlg.hide();
					refList();
				}
			}, 
			error: function(){
				showTips("请求出错了");
			}, 
			dataType: "json"
		});
	
	});

	//按情况隐藏操作栏(ok)
	function hideCustomer(){
		if("curLogin" in allData)
			if (allData.curLogin.CustomerID > 0)
			{
				var foot = $(".foot");
				foot.hide();
				$("#lblCustomerName").hide();
				$("#txtCustomerName").hide();
				disableGrid();

			}
	}
	
	//关闭窗口(ok)
	$(".dialog_close").click(function(){
		var dialogbody = $(this).parent("div");
		$(dialogbody).parent("div").hide();
	});
	$(".dlg_Close_btn").click(function(){
		$(this).parents(".dialog").hide();
	});
	
	//开始初始化
	init();
});
