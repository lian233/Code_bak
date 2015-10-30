/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
 //allData变量声明
 //用于存储返回的初始化数据
 if (typeof allData === "undefined") allData = {};
 
 var currData = null;

  //列变量
var ci_flag = 0;
var ci_delivery = 1;
var ci_deliverysheetid = 2;
var ci_time = 3;
var ci_qty = 4;
var ci_amount = 5;
var ci_note = 6;
var ci_detail = 7;


 //品牌列表
 var brandlist = null;
 
 //产品线列表
 var productLineList = null;

 //字段索引定义
var idxStatus= 1;
var idxOrderNo = 2;
var idxExpress = 3;
var idxTime = 4;
var idxQty = 5;
var idxMoney = 6;

//每页行数设定
var pageSizeSetting = 12;


function getRefundData(index){
	return currData ? currData[index] : null;
}

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
				url: "./queryDistributeGoods.do", 
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
				setActiveMenu("订单管理", "退货单");
			}
			
		}

	};
	
	
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
		$("#Goods_List tr:gt(0)").each(function(){
			$(this).remove();
		});
		//var chk_all = $("#all_check")[0];
		//chk_all.checked = false;
	}
	
	//添加新行(ok)
	function addGridRow(){
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		$("#Goods_List tbody").append(txt);
		var tr = $("#Goods_List tr:last");
		return tr;
	}
	
	//填充数据到表格(ok)
	function fillGrid(data){
		currData = data;
		var ln = data.length;
		for(var i=0; i<ln; i++)
		{
			var tr = addGridRow();
			
			tr.attr("index", i);
			var cells = tr.find("td");
			
			var refundList = data[i];
			tr.attr("ID",refundList.ID);
			var fs = "";
			if (refundList.flag==0){
				fs  = "新单"
			}
			else if (refundList.flag==10){
				fs  = "确认"
			}
			if (refundList.flag==97){
				fs  = "人工取消"
			}
			if (refundList.flag==100){
				fs  = "完成"
			}
			$(cells[ci_flag]).html(fs);

			$(cells[ci_deliverysheetid]).html(refundList.deliverySheetID);
			$(cells[ci_delivery]).html(refundList.delivery);

			var qty=refundList.totalQty;
			if (refundList.totalRefundQty != null){
				qty = qty + "<br><span style='color:blue'>" + refundList.totalRefundQty+"</span>";
			}
			$(cells[ci_qty]).html(qty);
			
			var amount = refundList.totalAmount;
			if (refundList.totalRefundAmount != null){
				amount = amount + "<br><span style='color:blue'>" + refundList.totalRefundAmount+"</span>";
			}
			$(cells[ci_amount]).html(amount);

			$(cells[ci_note]).html(refundList.note);

			//时间
			if (refundList.editTime != null){
				var dt = new Date();
				var d  = refundList.editTime;
				dt.setTime(d.time);
				
				var dd = (dt.getFullYear()-2000)+"-"+(dt.getMonth()+1)+"-"+d.date+" "+d.hours+":"+d.minutes+":"+d.seconds;

				if (refundList.CheckTime !=null)
				{
					dt.setTime(refundList.CheckTime.time);
					d  = refundList.CheckTime;
					dd = dd + "<br><span style='color:blue'>" + (dt.getFullYear()-2000)+"-"+(dt.getMonth()+1)+"-"+d.date+" "+d.hours+":"+d.minutes+":"+d.seconds+"</span>";
				}

				if (refundList.EndTime !=null)
				{
					dt.setTime(refundList.EndTime.time);
					d  = refundList.EndTime;
					dd = dd + "<br><span style='color:green'>" + (dt.getFullYear()-2000)+"-"+(dt.getMonth()+1)+"-"+d.date+" "+d.hours+":"+d.minutes+":"+d.seconds+"</span>";
				}
				$(cells[ci_time]).html(dd);
			}
			$(cells[ci_detail]).html("<img class='row_icon' src='./images/add_1.png'/>");			
		}
	}

	$(".dialog_close").click(function(){
		var dlg = $(this).parents(".dialog");
		dlg.hide();
		
		if (dlg.attr("id")==="edit_dialog"){//是编辑订单窗口
			var frm = $(".pop_frame_edit_bill");
			if (frm[0].contentWindow.isModified){//订单有变动，则重查询
				setTimeout(function(){
					requeryCurrPage();
				}, 100);
			}
			
			frm.attr("src", "about:blank");
		}
	});

	$("table").on("click", ".row_icon", function(){
		var dlg = $("#edit_dialog");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "10px");
		
		var ind = parseInt($(this).parents("tr").attr("index"));
		$(".pop_frame_edit_bill").attr("src", "./reFundEditBill.html?index="+ind);
	});

	

	
	//查询按钮(ok)
	$("#search").click(function(){
		//取参数		
		var expCorp = $("#txtExpCorp").val();
		var expNo = $("#txtExpNo").val();
		var status = $("#selStatus").val();
		var makeTime = $("#selMakeTime").val();
		var beginTime = $("#beginTime").val();
		var endTime = $("#endTime").val();
		var ranges = $("#ranges").val();
		lastSearchParam = {};
		
		lastSearchParam.pn = 0;
		lastSearchParam.pageSize = parseInt($("#pageSize").val());;
		//快递公司
		if(expCorp.length > 0) {lastSearchParam.delivery = expCorp;}
		//快递单号
		if(expNo.length > 0) {lastSearchParam.deliverySheetID = expNo;}
		//状态
		lastSearchParam.flag = parseInt(status);
		//时间类型
		lastSearchParam.timeType = parseInt(makeTime);
		//结果
		lastSearchParam.range = parseInt(ranges);

		//开始时间
		if(beginTime.length > 0) 
		{
			lastSearchParam.beginTime = ($.trim($("#beginTime").val())).replace(/T/, " ");
			
		} 
		//结束时间
		if(endTime.length > 0) {lastSearchParam.endTime = ($.trim($("#endTime").val())).replace(/T/, " ")};
		//刷新列表
		refList();
	});
	
	//执行查询,刷新列表(ok)
	function refList(){
		$.ajax({
			url: "./qryRefundSheet.do", 
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
	

	//录单：
	$("#manual_bill").click(function(){
		
			var dlg = $("#edit_dialog");
			dlg.show();

			var dbody = dlg.find(".dialog_body");
			dbody.css("left", ($(document).width()-dbody.width())/2+"px");
			dbody.css("top", "10px");

			var ind = parseInt($(this).attr("index"));
			$(".pop_frame_edit_bill").attr("src", "./reFundEditBill.html?index=-1");
	});

	//Goods_List选择的行高亮显示(ok)
	$("#Goods_List").on("click","tr:gt(0)",function(){
		if (selectedRow!==this){
			if (selectedRow!==null) $(selectedRow).removeClass("tr_check");
			
			$(this).addClass("tr_check");
			selectedRow = this;
		}
	});

	function afterCheck(ret){
		//return "";
		if (ret==null)
		{
			return "";
		}
		var ln = ret.length;
		var msg = "";	
		for(var i=0; i<ln; i++){
			var r = ret[i];
			if (r.errorCode!=0)
			{
				msg = msg + r.msg+'。';
			}
		}

		return msg;
	}

	//审核 
	$("#check").click(function(){
		if (selectedRow==null)
		{
			showTips("请选择要审核的单据！");
			return;
		}
		
		if(!confirm("确认审核当前退货单吗?")){
			return ;
		}

		var _this = this;
		var ID = $(selectedRow).attr("ID");
		
		if(typeof(ID) !== "undefined")
		{
			$.ajax({
				url : "checkRefundSheet.do",
				type : "post",
				dataType : "json",
				data : JSON.stringify({RefundSheets:[parseInt(ID)]}),
				success : function(rsp){
					if (rsp.errorCode==0){
						var msg=afterCheck(rsp.data);
						if (msg=="")
						{
							showTips("审核成功");
						}
						else{
							showTips(msg);
						}					
					}
					else{
						showTips(rsp.msg);						
					}
				},
				error : function(xhr, msg){
					showTips(msg);
				}
			});
		}
	
	});

	//取消
	$("#cancel").click(function(){
		if (selectedRow==null)
		{
			showTips("请选择要取消的单据！");
			return;
		}
		
		if(!confirm("确认取消当前退货单吗?")){
			return ;
		}

		var _this = this;
		var ID = $(selectedRow).attr("ID");
		
		if(typeof(ID) !== "undefined")
		{
			$.ajax({
				url : "cancelRefundSheet.do",
				type : "post",
				dataType : "json",
				data : JSON.stringify({RefundSheets:[parseInt(ID)]}),
				success : function(rsp){
					if (rsp.errorCode==0){
						var msg=afterCheck(rsp.data);
						if (msg=="")
						{
							showTips("取消成功");
						}
						else{
							showTips(msg);
						}					
					}
					else{
						showTips(rsp.msg);						
					}
				},
				error : function(xhr, msg){
					showTips(msg);
				}
			});
		}
	
	});


	$("#export").click(function(){
		$.ajax({
				type : "POST",
				url : "exportRefundSheet.do",
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

	$(".dlg_Close_btn").click(function(){
		$(this).parents(".dialog").hide();
	});

	//开始初始化
	init();
});
