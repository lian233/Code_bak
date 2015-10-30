/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var currData = null;

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

$(function(){
	var lastParam = {};

	page_ctrl_to_callback = function(pn){
		if (lastParam){
			lastParam.pn = pn;
			lastParam.query = 0;//表示不是按查询按钮的

			$.ajax({
				url:"./qryDecOrder.do",
				type:"post",
				dataType:"json",
				data:JSON.stringify(lastParam),
				success:function(rsp){
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
					showTips(msg);
				}
			});
		}	
	};

	advSearchCallback = function(param){
		lastParam = param;

		lastParam.pn = 0;
		lastParam.pageSize = 6;
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
		
		$.ajax({
			url:"./qryDecOrder.do",
			type:"post",
			dataType:"json",
			data:JSON.stringify(lastParam),
			success:function(rsp){
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
				showTips(msg);
			}
		});
	};

	function clearGrid(){
		$("#order_list tr:gt(0):not(:last)").each(function(){
			$(this).remove();
		});
	}
	
	function addGridRow(data){
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		$("#order_list tr:last").before(txt);
		
		var tr = $("#order_list tr:last").prev();
		var cells = tr.find("td");
		
		$(cells[0]).html("<input type=\"checkbox\" class=\"checkbox\"/>");
		$(cells[1]).html(data.refSheetID);
		$(cells[2]).html(data.buyerNick);
		
		var delivery = "";
		for(var i=0; i<allData.delivery.length; i++){
			if (allData.delivery[i].ID==data.deliveryID) delivery = allData.delivery[i].Name;
		}
		
		$(cells[3]).html(delivery+"<br/>"+data.deliverySheetID);
		$(cells[4]).html(data.linkMan+"&nbsp;&nbsp;"+data.mobile+"<br/><span style='color:gray'>"+data.state+data.city+data.district+data.address+"</span>");
		
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
		
		$(cells[5]).html(html);
		$(cells[6]).html(data.totalQty);
		
		var stateText = "";
		var state = parseInt(data.flag);
		switch(state){
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
		
		$(cells[7]).html(stateText);
		
		var desc = "";
		if (data.buyerMessage.length>0) desc += "<span style='color:gray'>买家留言：</span><br/>"+data.buyerMessage;
		if (data.buyerMemo.length>0) desc += "<br/><span style='color:gray'>买家备注：</span><br/>"+data.buyerMemo;
		if (data.sellerMemo.length>0) desc +="<br/><span style='color:gray'>卖家备注：</span><br/>"+data.sellerMemo;
		if (data.tradeMemo.length>0) desc +="<br/><span style='color:gray'>交易备注：</span><br/>"+data.tradeMemo;
		if (data.note.length>0) desc +="<br/><span style='color:gray'>备注：</span><br/>"+data.note;
		
		$(cells[8]).html(desc);
		$(cells[9]).html("<img class='row_icon' src='./images/add_1.png'/>");
		
		return tr;
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
	
	$.ajaxSetup({async: false});//设置AJAX请求为同步请求，以防操作顺序混乱
	
	(function(){
		if (allData){
			if ("curLogin" in allData){
				var info = $(".acount_info");
				info.html(allData.curLogin.Name+"["+allData.curLogin.CName+"]");
				info.attr("data", allData.curLogin.ID);
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
				var ln = allData.delivery.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.delivery[i].ID+"'>"+allData.delivery[i].Name+"</option>").appendTo(select);
				}
			}
			
			if (typeof(resetPageCtrl)!=="undefined"){
				resetPageCtrl({rowCnt:0, page:0, psize:10});
			}
		}
	})();
	
	$("#search").click(function(){
		lastParam = {};
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
		
		lastParam.pn = 0;
		lastParam.pageSize = 6;
		lastParam.query = 1;//表示是按查询按钮的
		
		$.ajax({
			url:"./qryDecOrder.do",
			type:"post",
			dataType:"json",
			data:JSON.stringify(lastParam),
			success:function(rsp){
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
				showTips(msg);
			}
		});
	});
	
	$("#adv_search").click(function(){
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
			if (frm[0].contentWindow.isModified){//订单有变动，则重查询
				setTimeout(function(){
					requeryCurrPage();
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
		
		$(":checkbox").each(function(){
			if (this!==_this) this.checked = v;
		});
	});
	
	$("table").on("click", ".row_icon", function(){
		var dlg = $("#edit_dialog");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "10px");
		
		var ind = parseInt($(this).parents("tr").attr("index"));
		$(".pop_frame_edit_bill").attr("src", "./editBill.html?index="+ind);
	});
	
	$("#manual_bill").click(function(){
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
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						requeryCurrPage();
						showTips("发货成功");
					}
				},
				error : function(xhr, msg){
					showTips(msg);
				}
			});
		}
	});
	
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
						requeryCurrPage();
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
											var url = "http://wolfdhc.eicp.net:8003/springmvc/getDecOrderPrintInfo.do";

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
		for(var i=0; i<inds.length; i++){
			selIDs.push(currData[inds[i]].id);
		}
		
		if (selIDs.length>0){
			if ("deliveryID" in lastParam){
				if ("flag" in lastParam){
					var localCode = findLocalCodeByID(lastParam.deliveryID);
					if (localCode){
						if (document["print"]){
							if (document["print"].isPrinting()===0){
								$.ajax({
									url : "printDecOrder.do",
									type : "post",
									dataType : "json",
									data : JSON.stringify({LocalCode:localCode, orders:selIDs}),
									success : function(rsp){
										if (rsp.errorCode!=0) showTips(rsp.msg);
										else{
											showTips("打印任务已安排，请耐心等待......");

											var tpl = JSON.stringify(rsp.data.printFormat);
											var url = "http://wolfdhc.eicp.net:8003/springmvc/getDecOrderPrintInfo.do";

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
		}		
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
				url : "importDecOrder.do",
				secureuri : false,
				fileElementId : 'file_for_import',
				dataType : 'json',
				success : function(data) {
					if(data.errorCode == 0){
						$(_this).parents(".dialog").hide();
			
						showTips("上传文件成功");
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
});
