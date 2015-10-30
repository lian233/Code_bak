/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var currData = null;
var currRow = null;

//列变量
var ci_choose = 0;
var ci_refsheetid = 1;
var ci_buyer = 2;
var ci_delivery = 3;
var ci_receiver = 4;
var ci_goods = 5;
var ci_note = 6;
var ci_qty = 7;
var ci_flag = 8;
var ci_time = 9;
var ci_detail = 10;
var isCheckClick = false;

var UIDisabled = false;

if (typeof allData === "undefined") allData = {};

function showTips(text){
	var dlg = $("#tips_dlg");
	dlg.show();

	var dbody = dlg.find(".dialog_body");
	dbody.css("left", ($(document).width()-dbody.width())/2+"px");
	dbody.css("top", "100px");

	$(".tips").html(text);
}

function getOrderData(index){
	return currData ? currData[index] : null;
}

function getShops(){
	return ("shop" in allData) ? allData.shop : null;
}

function getDeliverys(){
	return ("delivery" in allData) ? allData.delivery : null;
}

function onFlashCall(msg){
	setTimeout(function(){
		showTips(msg);
	}, 100);
}

function closeSearchDlg(){
	var dlg = $("#search_dialog");
	dlg.hide();
}

var advSearchCallback = null;
function advSearch(param){
	closeSearchDlg();
	advSearchCallback(param);
}


function disableUI(){
	UIDisabled = true;
}

function enableUI(){
	UIDisabled = false;
}

function showWait(b,i){
	var w ;
	if (i==null)
	{
		w = $("#imgWait");
	}
	else{
		w = $("#"+i);
	}
	

	if (b)
	{
		if (UIDisabled)
		{
			return false;
		}

		disableUI();		
		w.show();
	}
	else{
		w.hide();
		enableUI();
	}

	return true;
}

$(function(){
	var lastParam = {};

	page_ctrl_to_callback = function(pn){
		if (lastParam){
			if (!showWait(true))
			{
				return;
			}

			lastParam.pn = pn;
			lastParam.query = 0;//表示不是按查询按钮的

			$.ajax({
				url:"./qryDecOrder.do",
				type:"post",
				dataType:"json",
				data:JSON.stringify(lastParam),
				success:function(rsp){
					showWait(false);
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						clearGrid();
						fillGrid(rsp.data);
						
						if (typeof(afterChangedPage)!=="undefined"){
							afterChangedPage(rsp.pageInfo);
						}
					}
				},
				error:function(xhr, msg){
					showWait(false);
					showTips(msg);
				}
			});
		}	
	};



	advSearchCallback = function(param){
		lastParam = param;

		lastParam.pn = 0;
		//lastParam.pageSize = 6;
		lastParam.pageSize = parseInt($("#pageSize").val());
		lastParam.query = 1;//表示是按查询按钮的

		lastParam.shopID = parseInt($("#shops").val());
		lastParam.deliveryID = parseInt($("#deliverys").val());
		lastParam.flag = parseInt($("#flags").val());
		lastParam.range = parseInt($("#ranges").val());
		lastParam.key = $.trim($("#keyword").val());
		
		if (lastParam.shopID===-1) delete lastParam["shopID"];
		if (lastParam.deliveryID===-1) delete lastParam["deliveryID"];
		if (lastParam.flag===-1) delete lastParam["flag"];
		if (lastParam.range===-1) delete lastParam["range"];
		if (lastParam.key.length===0) delete lastParam["key"];

		if (!showWait(true))
		{
			return;
		}
		
		$.ajax({
			url:"./qryDecOrder.do",
			type:"post",
			dataType:"json",
			data:JSON.stringify(lastParam),
			success:function(rsp){
				showWait(false);
				if (rsp.errorCode!=0) showTips(rsp.msg);
				else{
					clearGrid();
					fillGrid(rsp.data);
						
					if (typeof(resetPageCtrl)!=="undefined"){
						resetPageCtrl(rsp.pageInfo);
					}
				}
			},
			error:function(xhr, msg){
				showWait(false);
				showTips(msg);
			}
		});
	};

	function clearGrid(){
		$("#order_list tr:gt(0):not(:last)").each(function(){
			$(this).remove();
		});
	}
	
	function getStatusText(flag){
		var stateText = "";
		switch(flag){
			case 0:
				stateText = "新单";
				break;
			case 10:
				stateText = "确认";
				break;
			case 20:
				stateText = "已打印";
				break;
			case 95:
				stateText = "删除";
				break;
			case 97:
				stateText = "人工取消";
				break;
			case 98:
				stateText = "自动取消";
				break;
			case 100:
				stateText = "完成结束";
				break;
		}	
		return stateText;
	}

	function addGridRow(data){
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		$("#order_list tr:last").before(txt);
		
		var tr = $("#order_list tr:last").prev();
		tr.attr("data",data.id);
		var cells = tr.find("td");
		
		$(cells[ci_choose]).html("<input id='ckbBill"+tr.index()+"' type=\"checkbox\" class=\"checkbox\"/><label><br>"+(tr.index()+1)+"</label>");
		$(cells[ci_refsheetid]).html(data.refSheetID+"<br>"+data.sellerNick);
		$(cells[ci_buyer]).html(data.buyerNick);
		
		var delivery = "";
		for(var i=0; i<allData.delivery.length; i++){
			if (allData.delivery[i].ID==data.deliveryID) delivery = allData.delivery[i].Name;
		}
		
		$(cells[ci_delivery]).html(delivery+"<br/>"+data.deliverySheetID);
		$(cells[ci_receiver]).html(data.linkMan+"&nbsp;&nbsp;"+data.mobile+"<br/><span style='color:gray'>"+data.state+data.city+data.district+data.address+"</span>");
		
		var pics = data.picNote.split("`");
		var goods = data.itemContent.split("\n");
		var html = "";
		for(var i=0; i<goods.length; i++){
			html += "<div class='goods_content'><img class='goods_photo' src='";
			if (pics.length>i){
				var p = pics[i].indexOf("~");
				if (p>0) pics[i] = pics[i].substr(0, p);
				
				html += pics[i]+"'/>";
			}else{
				html += "./images/goods_default.jpg'/>";
			}
			html += "<div class=\"label_multi_line\">"+goods[i]+"</div></div>";
		}
		
		$(cells[ci_goods]).html(html);
		$(cells[ci_qty]).html(data.totalQty);
		
		
		var state = parseInt(data.flag);
		var stateText = getStatusText(state);		

		if (data.refundFlag != null){
			if (data.refundFlag==1)
			{
				stateText=stateText+"<br><img class='row_icon' src='./images/refund.jpg'/><span style='color:red'>退款</span>"
			}
		}		
		if (data.printTimes != null){
			if (data.printTimes>=1)
			{
				stateText=stateText+"<br><img class='row_icon' src='./images/printed.jpg'/><span style='color:blue'>"+data.printTimes+"</span>"
			}
		}
		if (data.merFlag != null){
			if (data.merFlag==1)
			{
				stateText=stateText+"<br><img class='row_icon' src='./images/merFlag1.jpg'/><span style='color:red'>合单</span>"
			}
			else if (data.merFlag==2)
			{
				stateText=stateText+"<br><img class='row_icon' src='./images/merFlag2.jpg'/><span style='color:red'>同客户单</span>"
			}
		}		
		
		$(cells[ci_flag]).html(stateText);
		$(cells[ci_flag]).attr("data",data.flag);

		if (data.payTime != null){
			var dt = new Date();
			dt.setTime(data.payTime.time);
			var d  = data.payTime;
			//alert(d.hours);
			//alert(dt.getHours());
			//$(cells[8]).html(dt.getFullYear()+"-"+(dt.getMonth()+1)+"-"+dt.getDate()+"<br/>"+dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds());
			var dd = (dt.getFullYear()-2000)+"-"+(dt.getMonth()+1)+"-"+d.date+" "+d.hours+":"+d.minutes+":"+d.seconds;
			if (data.sendTime !=null)
			{
				dt.setTime(data.sendTime.time);
				d  = data.sendTime;
				dd = dd + "<br><span style='color:blue'>" + (dt.getFullYear()-2000)+"-"+(dt.getMonth()+1)+"-"+d.date+" "+d.hours+":"+d.minutes+":"+d.seconds+"</span>";
			}
			$(cells[ci_time]).html(dd);
		}
		
		var desc = "";
		var br = "";
		if (data.sellerFlag>0){
			desc="<img src='./images/flag"+data.sellerFlag+".png'/>";
		}
		if ($.trim(data.buyerMessage).length>0){ desc += "<span style='color:gray'>买家留言：</span><br/>"+data.buyerMessage; br="<br>";}
		if ($.trim(data.buyerMemo).length>0){ desc += br+"<span style='color:gray'>买家备注：</span><br/>"+data.buyerMemo;br="<br>";}
		if ($.trim(data.sellerMemo).length>0){ desc +=br+"<span style='color:gray'>卖家备注：</span><br/>"+data.sellerMemo;br="<br>";}
		if ($.trim(data.tradeMemo).length>0){ desc +=br+"<span style='color:gray'>交易备注：</span><br/>"+data.tradeMemo;br="<br>";}
		if ($.trim(data.note).length>0){ desc +=br+"<span style='color:gray'>备注：</span><br/>"+data.note;br="<br>";}
		
		
		$(cells[ci_note]).html(desc);
		
		
		$(cells[ci_detail]).html("<img class='row_icon' src='./images/add_1.png'/>");
		
		return tr;
	}

	function refreshGridRow(tr,data){
		var cells = tr.find("td");
		
		$(cells[ci_refsheetid]).html(data.refSheetID+"<br>"+data.sellerNick);
		$(cells[ci_buyer]).html(data.buyerNick);
		
		var delivery = "";
		for(var i=0; i<allData.delivery.length; i++){
			if (allData.delivery[i].ID==data.deliveryID) delivery = allData.delivery[i].Name;
		}
		
		$(cells[ci_delivery]).html(delivery+"<br/>"+data.deliverySheetID);
		$(cells[ci_receiver]).html(data.linkMan+"&nbsp;&nbsp;"+data.mobile+"<br/><span style='color:gray'>"+data.state+data.city+data.district+data.address+"</span>");
		
		
		
		
		
		if ($.trim(data.buyerMessage).length>0){ desc += "<span style='color:gray'>买家留言：</span><br/>"+data.buyerMessage; br="<br>";}
		if ($.trim(data.buyerMemo).length>0){ desc += br+"<span style='color:gray'>买家备注：</span><br/>"+data.buyerMemo;br="<br>";}
		if ($.trim(data.sellerMemo).length>0){ desc +=br+"<span style='color:gray'>卖家备注：</span><br/>"+data.sellerMemo;br="<br>";}
		if ($.trim(data.tradeMemo).length>0){ desc +=br+"<span style='color:gray'>交易备注：</span><br/>"+data.tradeMemo;br="<br>";}
		if ($.trim(data.note).length>0){ desc +=br+"<span style='color:gray'>备注：</span><br/>"+data.note;br="<br>";}
		
		
		$(cells[ci_note]).html(desc);
				
	}
	
	function fillGrid(orders){
		currData = orders;
		
		for(var i=0; i<orders.length; i++){
			var tr = addGridRow(orders[i]);
			tr.attr("index", i);
		}
	}
	
	function requeryCurrPage(){
		if ("pn" in lastParam){
			page_ctrl_to_callback(lastParam.pn);
		}
	}
	
	function findLocalCodeByID(id){
		var ln = allData.delivery.length;
		for(var i=0; i<ln; i++){
			if (allData.delivery[i].ID==id) return allData.delivery[i].LocalCode;
		}
		return null;
	}
	
	$.ajaxSetup({async: true});//设置AJAX请求为同步请求，以防操作顺序混乱
	
	(function(){
		if (allData){
			if ("curLogin" in allData){
				var info = $(".acount_info");
				info.html(allData.curLogin.Name+"["+allData.curLogin.CName+"]");
				info.attr("data", allData.curLogin.ID);

				
				var logo = $("#systemLogo");
				logo.attr("src",allData.curLogin.SystemLogo );
				logo = $("#systemName");
				logo.text(allData.curLogin.SystemName);

			}
			
			if ("shop" in allData){
				var select = $("#shops");
				var ln = allData.shop.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.shop[i].ID+"'>"+allData.shop[i].Name+"</option>").appendTo(select);
				}
			}
			
			if ("delivery" in allData){
				var select = $("#deliverys");
				var selModifyDelivery = $("#selModifyDelivery");
				var ln = allData.delivery.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.delivery[i].ID+"'>"+allData.delivery[i].Name+"</option>").appendTo(select);
					$("<option value='"+allData.delivery[i].ID+"'>"+allData.delivery[i].Name+"</option>").appendTo(selModifyDelivery);
				}
			}
			
			if ("menu" in allData){
				setActiveMenu("订单管理", "订单处理");
			}
			
			if (typeof(resetPageCtrl)!=="undefined"){
				resetPageCtrl({rowCnt:0, page:0, psize:10});
			}
		}
	})();
	


	$("#search").click(function(){
		if (!showWait(true))
		{
			return;
		}
		
		lastParam = {};
		lastParam.shopID = parseInt($("#shops").val());
		lastParam.deliveryID = parseInt($("#deliverys").val());
		lastParam.flag = parseInt($("#flags").val());
		lastParam.range = parseInt($("#ranges").val());
		lastParam.key = $.trim($("#keyword").val());
		lastParam.inDays = parseInt($.trim($("#inDays").val()));
		
		if (lastParam.shopID===-1) delete lastParam["shopID"];
		if (lastParam.deliveryID===-1) delete lastParam["deliveryID"];
		if (lastParam.flag===-1) delete lastParam["flag"];
		if (lastParam.range===-1) delete lastParam["range"];
		if (lastParam.key.length===0) delete lastParam["key"];
		if (isNaN(lastParam.inDays)) delete lastParam["inDays"];
		
		lastParam.pn = 0;
		//lastParam.pageSize = 6;
		lastParam.pageSize = parseInt($("#pageSize").val());
		lastParam.query = 1;//表示是按查询按钮的
		
		
		$.ajax({
			url:"./qryDecOrder.do",
			type:"post",
			dataType:"json",
			data:JSON.stringify(lastParam),
			success:function(rsp){
				showWait(false);
				if (rsp.errorCode!=0) showTips(rsp.msg);
				else{
					clearGrid();
					fillGrid(rsp.data);
						
					if (typeof(resetPageCtrl)!=="undefined"){
						resetPageCtrl(rsp.pageInfo);
					}
				}
			},
			error:function(xhr, msg){
				showWait(false);
				showTips(msg);
			}
		});
	});
	
	$("#adv_search").click(function(){
		//var test=[{"errorCode":0,"ID":66,"deliverySheetID":"ttttt",flag:10},{"errorCode":0,"ID":67,"deliverySheetID":"cccccc",flag:10},
		//	{"errorCode":1,"msg":"error","ID":29,"deliverySheetID":"cccccc",flag:10}];
		//alert(afterConfirm(test));

		if (UIDisabled)
		{
			return false;
		}

		var dlg = $("#search_dialog");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "10px");
	});
	
	$(".dialog_close").click(function(){
		var dlg = $(this).parents(".dialog");
		dlg.hide();
		
		if (dlg.attr("id")==="edit_dialog"){//是编辑订单窗口
			var frm = $(".pop_frame_edit_bill");
			var order = frm[0].contentWindow.orderData ;
			if (frm[0].contentWindow.isModified){//订单有变动，则重查询
				setTimeout(function(){
					//requeryCurrPage();
					//刷新数据
					refreshGridRow(currRow , order);

				}, 100);
			}
			
			frm.attr("src", "about:blank");
		}
	});
	$("#dlg_btn").click(function(){
		$(this).parents(".dialog").hide();
	});
	
	$("#all_check").click(function(){
		var _this = this;
		var v = this.checked;
		
		$(":checkbox").each(function(index, e){
			if (this!==_this) this.checked = v;
			var tr= $("#order_list tr:eq("+index+")");
			if (v)
			{
				tr.addClass("tr_check");
			}
			else{
				tr.removeClass("tr_check");
			}
		});
	});


	//$("table").on("click","tr:gt(0):not(:last)",function(){
	$("table").on("click","tr",function(){
		if (isCheckClick)
		{
			isCheckClick=false;
			return;
		}
		var c = $(this);
		var ch = $("#ckbBill"+c.index());
		if (ch.prop("checked")==true)
		{
			ch.prop("checked",false);
			c.removeClass("tr_check");
		}
		else{
			ch.prop("checked",true);
			c.addClass("tr_check");
		}
		
	});
	

	$("table").on("click", ".checkbox", function(){
		var c = $(this);
		//alert(c.attr("checked"));
		//alert(c.attr("class"));
		if (this.checked)
		{
			c.parent().parent().addClass("tr_check");
		}else{
			c.parent().parent().removeClass("tr_check");
		}
		isCheckClick=true;
		//alert(c.parent().attr("type"));
		
		//$("td:parent")
	})

	$("table").on("click", ".row_icon", function(){
		var dlg = $("#edit_dialog");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "10px");
		
		currRow = $(this).parents("tr");
		//alert(currRow.attr("index"));
		var ind = parseInt(currRow.attr("index"));
		//var ind = parseInt($(this).parents("tr").attr("index"));
		$(".pop_frame_edit_bill").attr("src", "./editBill.html?index="+ind);
	});
	
	$("#manual_bill").click(function(){
		if (UIDisabled)
		{
			return false;
		}

		if ("shop" in allData){
			var dlg = $("#edit_dialog");
			dlg.show();

			var dbody = dlg.find(".dialog_body");
			dbody.css("left", ($(document).width()-dbody.width())/2+"px");
			dbody.css("top", "10px");

			var ind = parseInt($(this).attr("index"));
			$(".pop_frame_edit_bill").attr("src", "./editBill.html?index=-1");
		}else{
			showTips("");
		}
	});
	
	$("#send").click(function(){
		if (!showWait(true))
		{
			return;
		}

		var inds = [];
		
		$("tbody :checkbox").each(function(index, ele){
			if (ele.checked){
				inds.push(index);
			}
		});

		var selIDs = [];
		for(var i=0; i<inds.length; i++){
			selIDs.push(currData[inds[i]].id);
		}
		
		if (selIDs.length>0){
			$.ajax({
				url : "sendDecOrder.do",
				type : "post",
				dataType : "json",
				data : JSON.stringify({orders:selIDs}),
				success : function(rsp){
					showWait(false);
					if (rsp.errorCode!=0) showTips("发货失败："+rsp.msg);
					else{
						var msg="";
						if (rsp.data){
							for(var i=0; i<rsp.data.length; i++){
								var d = rsp.data[i];
								if (d.errorCode!=0) {
									msg=msg+d.msg+"。";
								}
							}
						}
						
						//requeryCurrPage();
						if (msg=="")
						{
							showTips("发货成功");
						}else{
							showTips(msg);
						}
						
					}
				},
				error : function(xhr, msg){
					showWait(false);
					showTips(msg);
				}
			});
		}

	});
	
	/*
	$("#confirm").click(function(){
		if ("pn" in lastParam){
			$.ajax({
				url : "confirmDecOrder.do",
				type : "post",
				dataType : "json",
				data : "",
				success : function(rsp){
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						requeryCurrPage();
						showTips("订单确认成功");
					}
				},
				error : function(xhr, msg){
					showTips(msg);
				}
			});
		}
	});
	*/

	$("#confirm").click(function(){
		if (UIDisabled)
		{
			return false;
		}


		if (!showWait(true))
		{
			return;
		}

		var inds = [];
		
		$("tbody :checkbox").each(function(index, ele){
			if (ele.checked){
				inds.push(index);
			}
		});

		var selIDs = [];
		for(var i=0; i<inds.length; i++){
			selIDs.push(currData[inds[i]].id);
		}
		
		if (selIDs.length>0){
			$.ajax({
				url : "confirmDecOrder.do",
				type : "post",
				dataType : "json",
				data : JSON.stringify({orders:selIDs}),
				success : function(rsp){
					showWait(false);
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						//requeryCurrPage();
						var msg=afterConfirm(rsp.data);
						if (msg=="")
						{
							showTips("订单确认成功");
						}
						else{
							showTips(msg);
						}
						
					}
				},
				error : function(xhr, msg){
					showWait(false);
					showTips(msg);
				}
			});
		}
		
	});

	
	function afterConfirm(ret){
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
			else{
				//写入到表中
				$("#order_list tr:gt(0):not(:last)").each(function(){
					var tr = $(this);
					if (r.ID==tr.attr("data"))
					{
						var cells = tr.find("td");
						var cell= $(cells[ci_delivery]);
						var h= cell.html();						
						if(h.indexOf(r.deliverySheetID)<0){ 
							h=h+"<br>"+r.deliverySheetID;
							cell.html(h);
						}
						//$(cells[3]).html(delivery+"<br/>"+data.deliverySheetID);
						//更新状态
						cell= $(cells[ci_flag]);
						if (cell.attr("data")!=r.flag)
						{
							h = cell.html();
							var i = h.indexOf("<br>");
							var t = getStatusText(r.flag);
							if (i<0){
								h = t;
							}
							else{
								h = t + h.substring(i);
							}
							cell.html(h);
						}
					}
					
					
				})
			}
		}

		return msg;
	}
	
	$("#cancel").click(function(){
		var inds = [];
		
		$("tbody :checkbox").each(function(index, ele){
			if (ele.checked){
				inds.push(index);
			}
		});

		var selIDs = [];
		for(var i=0; i<inds.length; i++){
			selIDs.push(currData[inds[i]].id);
		}
		
		if (selIDs.length>0){
			$.ajax({
				url : "cancelDecOrder.do",
				type : "post",
				dataType : "json",
				data : JSON.stringify({orders:selIDs}),
				success : function(rsp){
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						//requeryCurrPage();
						showTips("取消订单成功");
					}
				},
				error : function(xhr, msg){
					showTips(msg);
				}
			});
		}		
	});
	
	$("#merge").click(function(){
		if ("pn" in lastParam){
			$.ajax({
				url : "mergeDecOrder.do",
				type : "post",
				dataType : "json",
				data : "",
				success : function(rsp){
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						//requeryCurrPage();
						showTips("合并订单成功");
					}
				},
				error : function(xhr, msg){
					showTips(msg);
				}
			});
		}
	});
	
	$("#print_batch").click(function(){
		if ("pn" in lastParam){
			if ("deliveryID" in lastParam){
				if ("flag" in lastParam){
					var localCode = findLocalCodeByID(lastParam.deliveryID);
					if (localCode){
						if (document["print"]){
							if (document["print"].isPrinting()===0){
								$.ajax({
									url : "printConfrimDecOrder.do",
									type : "post",
									dataType : "json",
									data : JSON.stringify({LocalCode:localCode, Flag:lastParam.flag}),
									success : function(rsp){
										if (rsp.errorCode!=0) showTips(rsp.msg);
										else{
											showTips("打印任务已安排，请耐心等待......");

											var tpl = JSON.stringify(rsp.data.printFormat);
											//var url = "http://wolfdhc.eicp.net:8003/springmvc/getDecOrderPrintInfo.do";
											var url = allData.curLogin.SystemUrl+"getDecOrderPrintInfo.do";
											

											document["print"].startPrint(rsp.data.session, lastParam.flag, tpl, url);
										}
									},
									error : function(xhr, msg){
										showTips(msg);
									}
								});
							}else{
								showTips("上一个打印任务尚未完成，请稍后再试");
							}
						}else{
							showTips("无法访问flash播放器，请检查是否正确安装");
						}
					}else{
						showTips("找不到所选快递公司的编码");
					}
				}else{
					showTips("请先选择快递公司和订单状态进行搜索");
				}
			}else{
				showTips("请先选择快递公司和订单状态进行搜索");
			}
		}else{
			showTips("请先执行搜索");
		}		
	});
	
	$("#print_select").click(function(){

		var inds = [];
		
		$("tbody :checkbox").each(function(index, ele){
			if (ele.checked){
				inds.push(index);
			}
		});

		var selIDs = [];
		var hadPrint = "";
		for(var i=0; i<inds.length; i++){
			selIDs.push(currData[inds[i]].id);
			if (currData[inds[i]].printTimes>=1)
			{
				hadPrint = hadPrint+currData[inds[i]].refSheetID+"," + currData[inds[i]].linkMan +";";
			}
		}

		if (hadPrint!="")
		{
			if(!confirm("【"+hadPrint+"】已打印，确认重新打印吗?")){
				return ;
			}
		}
		
		if (!showWait(true))
		{
			return;
		}

		if (selIDs.length>0){
			if ("deliveryID" in lastParam){
				//if ("flag" in lastParam){
				if (1==1){
					var localCode = findLocalCodeByID(lastParam.deliveryID);
					var ps = parseInt($("#printSort").val());
					if (localCode){
						if (document["print"]){
							if (document["print"].isPrinting()===0){
								$.ajax({
									url : "printDecOrder.do",
									type : "post",
									dataType : "json",
									data : JSON.stringify({LocalCode:localCode, orders:selIDs,printSort:ps}),
									success : function(rsp){
										showWait(false);
										if (rsp.errorCode!=0) showTips(rsp.msg);
										else{
											showTips("打印任务已安排，请耐心等待......");

											var tpl = JSON.stringify(rsp.data.printFormat);
											//var url = "http://wolfdhc.eicp.net:8003/springmvc/getDecOrderPrintInfo.do";
											var url = allData.curLogin.SystemUrl+"getDecOrderPrintInfo.do";
											
											var flag=lastParam.flag;
											if (flag == null)
											{
												flag=-100;
											}
											document["print"].startPrint(rsp.data.session, flag, tpl, url);
										}
									},
									error : function(xhr, msg){
										showWait(false);
										showTips(msg);
									}
								});
							}else{
								showTips("上一个打印任务尚未完成，请稍后再试");
								showWait(false);
							}
						}else{
							showTips("无法访问flash播放器，请检查是否正确安装");
							showWait(false);
						}
					}else{
						showTips("找不到所选快递公司的编码");
						showWait(false);
					}
				}else{
					//showTips("请先选择快递公司和订单状态进行搜索");
					showTips("请先选择快递公司进行搜索");
					showWait(false);
				}
			}else{
				showTips("请先选择快递公司和订单状态进行搜索");
				showWait(false);
			}
		}
		
		
	});

	$("#btnModiStatus").click(function(){
		if (!showWait(true))
		{
			return;
		}

		var inds = [];
		
		$("tbody :checkbox").each(function(index, ele){
			if (ele.checked){
				inds.push(index);
			}
		});

		var selIDs = [];
		for(var i=0; i<inds.length; i++){
			selIDs.push(currData[inds[i]].id);
		}
		
		if (selIDs.length>0){
			var flag = parseInt($("#modiflag").val());
			$.ajax({
				url : "modifyDecOrderFlag.do",
				type : "post",
				dataType : "json",
				data : JSON.stringify({"orders":selIDs,"flag":flag}),
				success : function(rsp){
					showWait(false);
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						//requeryCurrPage();
						showTips("修改订单状态成功");
					}
				},
				error : function(xhr, msg){
					showWait(false);
					showTips(msg);
				}
			});
		}else{
			showTips("请勾选修改状态的订单！");
		}

		
	});
	
	$("#export").click(function(){
		if (!showWait(true))
		{
			return;
		}

		//window.location.href="exportDecOrder.do";
		$.ajax({
				type : "POST",
				url : "exportDecOrder.do",
				dataType:'json',
				contentType:"application/json;charset=UTF-8",
				data:JSON.stringify(lastParam),
				async : false,
				success : function(data) {
					showWait(false);
					if (data.errorCode==0)
					{
						window.location.href=data.data;
					}else{
						showTips(data.msg);
					}										
				},error:function(data){
					showWait(false);
					showTips("导出错误" );
				}
			});

	});

	$("#exportStaSku").click(function(){
		if (!showWait(true))
		{
			return;
		}

		$.ajax({
				type : "POST",
				url : "exportStaDecOrderSku.do",
				dataType:'json',
				contentType:"application/json;charset=UTF-8",
				data:{},
				async : false,
				success : function(data) {
					if (data.errorCode==0)
					{
						showWait(false);
						window.location.href=data.data;
					}else{
						showWait(false);
						showTips(data.msg);
					}										
				},error:function(data){
					showTips("导出错误" );
				}
			});


	});

	$("#import").click(function(){
		if (UIDisabled)
		{
			return false;
		}

		var dlg = $("#import_dlg");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "30px");
	});

	//修改快递界面 
	$("#btnModiDelivery").click(function(){
		if (UIDisabled)
		{
			return false;
		}

		//检查
		var ischeck=0;
		var msg = "";
		$("tbody :checkbox").each(function(index, ele){
			if (ele.checked){
				if (currData[index].tradeMemo=="" && currData[index].buyerMessage==""  && currData[index].buyerMemo==""  && currData[index].sellerMemo==""  
					&& currData[index].note=="" )
				{
					msg="订单【"+currData[index].refSheetID+"】没有备注，不能批量修改快递！";
					return ;
				}
				ischeck = 1;
			}
		});

		if (msg!="")
		{
			showTips(msg);
			return;
		}
		if (ischeck==0)
		{
			showTips("请勾选要修改快递的订单！");
			return;
		}

		var dlg = $("#ModifyDelivery_dialog");
		var dbody = $(dlg.find(".dialog_body"));

		//设置显示位置
		dbody.css("left", ($(document).width()- parseInt(dbody.css("width").replace("px","")))/2+"px");
		dbody.css("top",  "30px");
		dlg.show();
	});


	//取消修改快递
	$("#btnCancelModifyDelivery").click(function(){
		var dlg = $("#ModifyDelivery_dialog");
		dlg.hide();
	});

	//修改快递操作
	$("#btnModifyDelivery").click(function(){
		if (!showWait(true))
		{
			return;
		}

		var dlg = $("#ModifyDelivery_dialog");
		var sel = $("#selModifyDelivery");
		if(sel.val() == -1)
		{
			showTips("请选择要修改的快递!");
			return;
		}

		var inds = [];
		
		$("tbody :checkbox").each(function(index, ele){
			if (ele.checked){
				inds.push(index);
			}
		});

		var selIDs = [];
		for(var i=0; i<inds.length; i++){
			selIDs.push(currData[inds[i]].id);
		}
		var data = {orders:selIDs};
		var did = parseInt($("#selModifyDelivery").val());	
		data.deliveryID = did;

		if (document.getElementById("chkCreateDelivery").checked)
		{
			data.isCreate = 1;
		}
		else{
			data.isCreate = 0;
		}
		
		if (selIDs.length>0){
			$.ajax({
				url : "modifyDelivey.do",
				type : "post",
				dataType : "json",
				data : JSON.stringify(data),
				success : function(rsp){
					showWait(false);
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						var msg=afterConfirm(rsp.data);
						if (msg=="")
						{
							showTips("修改快递成功");
						}
						else{
							showTips(msg);
						}
						
					}
				},
				error : function(xhr, msg){
					showWait(false);
					showTips(msg);
				}
			});
		}
		else{
			showTips("请勾选要修改快递的订单！");
		}
	
	});
	


	$("#import_upload_btn").click(function(){
		if (!showWait(true,"imgWait2"))
		{
			return;
		}

		var _this = this;
		
		var file = $("#file_for_import").val();
		if (file.length>0){
			$("#uploadForm").ajaxSubmit({
				url : "importDecOrder.do",
				secureuri : false,
				fileElementId : 'file_for_import',
				dataType : 'json',
				success : function(data) {
					showWait(false);
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
								showTips(data.msg+"。<br>"+msg);
							}
						}
						
					}else{
						showTips(data.msg);
					}
				},
				error : function(xhr, msg){
					showWait(false);
					showTips(msg);
				}
			});
		}else{
			showTips("请选择一个文件");
		}

		
	});



});
