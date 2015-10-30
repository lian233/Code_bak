 //客户列表
var customerlist = {};

//初始化后得到的数据
if (typeof allData === "undefined") allData = {};

//分页设定
var pageSizeSettingA = 5;
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
				url: "./qryDaysSta.do",
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


	$.ajaxSetup({async: false});

	//"客户"下拉菜单(调用inputSelect.js)
	registInputSelect({
		id:"customers",
		url:"./iniGoodsStaData.do",
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

			//店铺列表
			if ("shop" in allData)
			{
				var selectlist = $("#deliverycodeSelect");
				var selectlistB = $("#deliverycodeSelectB");
				var ln = allData.shop.length;
				$("<option value='-1'>请选择</option>").appendTo(selectlist);
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.shop[i].ID+"'>"+allData.shop[i].Name+"</option>").appendTo(selectlist);
				}
			}

			if ("menu" in allData){
				setActiveMenu("报表查询", "每日销售统计");
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

	
	//添加行数据(客户表)
	function addGridRow(){
		var txt = "<tr> <td hidden></td><td></td><td></td><td></td> </tr>";
		$("#account_list tbody").append(txt);
		var tr = $("#account_list tr:last");
		var cells = tr.find("td");
		$(cells[0]).addClass("tb_fix_width_short");
		$(cells[0]).html("<label hidden></label>");	//客户ID
		$(cells[1]).html("<label></label>");	//日期
		$(cells[2]).html("<label></label>");	//销售数量
		$(cells[3]).html("<label></label>");	//销售金额
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
//			field.attr("cid_data",record.CustomerID);	//存储客户ID
//			field.attr("dID_data",record.DeliveryID);	//存储快递ID
//			field.text(record.CustomerID);
			//日期
			field = $(label[1]);
			field.text(record.PayTime);
			//销售数量
			field = $(label[2]);
			field.text(record.Qty);
			//销售金额
			field = $(label[3]);
			//field.text(record.DeliveryID);
			field.text(record.Amount);
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
	
	//快递查询
	$("#search").click(function(){
        var dID = $("#deliverycodeSelect").val();      //店铺ID
		//要提交的参数
		lastSearchParamA = {};
		//店铺ID
		if(dID !== "-1")
		{
			lastSearchParamA.DeliveryID = parseInt(dID);
		}
         //时间
        var label = $(selectedRow).find("label");
        var shop = $("#deliverycodeSelect").val();  //店铺
        var date_start = $("#date_time_start").val();		//*开始时间
        var date_end = $("#date_time_end").val();		//*结束时间
        lastSearchParamA.BeginTime = date_start;
        lastSearchParamA.EndTime = date_end;
        //店铺
        if(parseInt(shop) >= 0)
            lastSearchParamA.ShopID  = parseInt(shop);

        //发送请求
		$.ajax({
			url: "./qryDaysSta.do",
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
	

//      	//判断时间是否有选择
//      	if($("#date_time_start").val() === "" || $("#date_time_end").val() === "")
//      	{
//      		showTips("请选择时间!");
//      		return;
//      	}

	
	//初始化
	init();
});

