/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var refundIndex = -1;
var isModified = false;

var refundIndex = -1;
var isModified = false;

(function(){
	var uri = window.location.search;
	if (uri.indexOf("?")===0){
		uri = uri.substr(1);
		var params = uri.split('&');
		if (params.length>0){
			for(var i=0; i<params.length; i++){
				var str = params[i];
				var p = str.indexOf("=");
				if (p>0){
					if (str.substr(0, p)==="index"){
						refundIndex = str.substr(p+1);
						break;
					}
				}
			}
		}
	}


})();

function showTips(text){
	var dlg = $("#tips_dlg");
	dlg.show();

	var dbody = dlg.find(".dialog_body");
	dbody.css("left", ($(document).width()-dbody.width())/2+"px");
	dbody.css("top", "100px");

	$(".tips").html(text);
}

$(function(){
	var refundData = null;
	var shops = null;
	var deliverys = null;
	var goods = null;
	
	function addGridRow(data){
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		$("#refundList tr:last").before(txt);

		var tr = $("#refundList tr:last").prev();
		var cells = tr.find("td");

		$(cells[0]).html("<input type=\"checkbox\" class=\"checkbox\"/>");
		$(cells[1]).html("<input type='text' class='text' value='"+data.outerSkuID+"' data='"+data.ID+"'/>");
		$(cells[2]).html("<input type='text' class='text' value='"+data.notifyQty+"'/>");
		$(cells[3]).html("<input type='text' class='text' value='"+data.notifyPrice+"'/>");
		$(cells[4]).html("<input type='text' class='text input_short' value='"+data.note+"'/>");
		$(cells[5]).html(data.factQty);
		$(cells[6]).html(data.factPrice);
	}
	
	function fillGrid(d){
		goods = d;
		
		for(var i=0; i<d.length; i++){
			addGridRow(d[i]);
		}
	}
	
	function getRefundData(){
		var refund = {ID:refundData.ID};
		refund.Delivery = $.trim($("#Delivery").val());;
		refund.DeliverySheetID = $.trim($("#DeliverySheetID").val());;
		refund.Note = $.trim($("#Note").val());;
		
		return refund;
	}
	

	function propCountOf(obj){
		var cnt = 0;
		for(var p in obj) cnt++;
		return cnt;
	}
	
	function afterSaveRefund(newID, newSheetID){
		if (typeof newID !== "undefined"){
			refundData.ID = newID;
			refundData.SheetID = newSheetID;
		}
		
		refundData.Delivery = $.trim($("#Delivery").val());
		refundData.DeliverySheetID = $.trim($("#DeliverySheetID").val());
		refundData.Note = $.trim($("#Note").val());
		
	}
	
	function disableRefund(){
		$("#Delivery").attr("disabled", "disabled");
		$("#DeliverySheetID").attr("disabled", "disabled");
		$("#Note").attr("disabled", "disabled");
	}
	
	function enableRefund(){
		$("#Delivery").removeAttr("disabled");
		$("#DeliverySheetID").removeAttr("disabled");
		$("#Note").removeAttr("disabled");
	}
	
	function disableGrid(){
		$("tbody input").attr("disabled", "disabled");
	}
	
	function enableGrid(){
		$("tbody input").removeAttr("disabled");
	}
	
	function saveGrid(params){
		disableGrid();
		$.ajax({
			url: "./saveRefundSheetItem.do", 
			type: "post", 
			data: JSON.stringify({RefundSheetItems:params}), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					afterSaveGrid(rsp.data);
					
					showTips("保存数据成功");
					
					isModified = true;
				}
					
				enableGrid();
			}, 
			error: function(xhr, msg){
				showTips(msg);
				
				enableGrid();
			}, 
			dataType: "json"
		});
	}
	
	function findGoodsById(id){
		for(var i=0; i<goods.length; i++){
			if (goods[i].ID==id) return goods[i];
		}
		return null;
	}
	
	function afterSaveGrid(newIDs){
		var i = 0;
		
		$("tbody tr:not(:last)").each(function(index, ele){
			var inputs = $(this).find("input");
			
			var gd = null;
			var field = $(inputs[1]);
			
			var id = parseInt(field.attr("data"));
			if (isNaN(id)){//新行
				var ID = newIDs[i++];
				
				field.attr("data", ID);
				gd = {id:parseInt(ID), outerSkuID:null, title:null, skuPropertiesName:null, purQty:null, customPrice:null};
				
				if (goods===null) goods = [];
				goods.push(gd);
			}else{//已有行
				gd = findGoodsById(id);
			}
			
			if (gd){
				gd.outerSkuID = $.trim(field.val());
				
				field = $(inputs[2]);
				gd.title = $.trim(field.val());
				
				field = $(inputs[3]);
				gd.skuPropertiesName = $.trim(field.val());
				
				field = $(inputs[4]);
				gd.purQty = parseInt($.trim(field.val()));
				
				field = $(inputs[5]);
				gd.customPrice = parseFloat($.trim(field.val()));
			}
		});
	}
	
	function getDateTimeLocalBy(ddd){
		var dt = new Date();
		dt.setTime(ddd.time);
		
		var m = dt.getMonth()+1;
		var d = ddd.date;//dt.getDay();
		var h = ddd.hours;//dt.getHours();
		var i = ddd.minutes;//dt.getMinutes();
		var s = ddd.seconds;//dt.getSeconds();

		return dt.getFullYear()+"-"+(m<10?("0"+m):m)+"-"+(d<10?("0"+d):d)+"T"+(h<10?("0"+h):h)+":"+(i<10?("0"+i):i)+":"+(s<10?("0"+s):s);
	}
	
	(function(){
		if (refundIndex!=-1){//修改旧单
			refundData = parent.getRefundData(refundIndex);
			$("#Delivery").val(refundData.delivery);
			$("#DeliverySheetID").val(refundData.deliverySheetID);
			$("#Note").val(refundData.note);
			
			//var id = $(inputs[1]).attr("data");
			$.ajax({
				url:"./qryRefundSheetItem.do",
				type:"post",
				dataType:"json",
				data:JSON.stringify({sheetID:refundData.sheetID,flag:refundData.flag}),
				success:function(rsp){
					if (rsp.errorCode!==0) showTips(rsp.msg);
					else{
						fillGrid(rsp.data);
					}
				},
				error:function(xhr, msg){
					showTips(msg);
				}
			});

		}else{//手工制单
			refundData = {ID:-1,SheetID:"",Delivery:"",DeliverySheetID:"",Note:""};
		}
	})();			


	$("#all_check").click(function(){
		var _this = this;
		var v = this.checked;
		
		$(":checkbox").each(function(){
			if (this!==_this) this.checked = v;
		});
	});
	
	$("#add_btn").click(function(){
		addGridRow({ID:"",outerSkuID:"",notifyQty:"",notifyPrice:"",note:""});
	});
	
	$("#del_btn").click(function(){
		if (!confirm("是否确定删除所选择的行？删除后不可恢复")){
			return;
		}
		
		var rows = [];
		
		$(":checkbox").each(function(index, ele){
			if (index>0 && ele.checked){
				rows.push($("#refundList tr:eq("+index+")"));
			}
		});
		
		var delID = [];
		var ids = [];
		
		for(var i=0; i<rows.length; i++){
			var inputs = rows[i].find("input");
			
			var ID = $(inputs[1]).attr("data");//取得itemID
			if (typeof(ID)!=="undefined" && ID.length>0){
				delID.push({ID:parseInt(ID)});
				ids.push(parseInt(ID));
			}
		}
		
		if (delID.length===0){//没有选择旧帐号
			for(var i=0; i<rows.length; i++){
				rows[i].remove();
			}

			$("#all_check").attr("checked", false);
		}else{//选择了旧帐号
			$.ajax({
				url: "./saveRefundSheetItem.do", 
				type: "post", 
				data: JSON.stringify({RefundSheetItems:delID}), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						for(var n=0; n<goods.length; n++){//删除内存数据
							if (ids.indexOf(goods[n].id)>=0){
								goods.splice(n, 1);
								n--;
							}
						}
						
						for(var i=0; i<rows.length; i++){//删除表格行
							rows[i].remove();
						}
						$("#all_check").attr("checked", false);

						showTips("删除成功");
						
						isModified = true;
					}
				}, 
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
	});

	function getGoodsData(){
		var gds = [];
		
		$("tbody tr:not(:last)").each(function(){
			var inputs = $(this).find("input");
			
			if (isNaN($.trim($(inputs[2]).val()))){
				showTips("数量不是有效数字");
				gds = false;
				return false;
			}
			if (isNaN($.trim($(inputs[3]).val()))){
				showTips("价格不是有效数字");
				gds = false;
				return false;
			}
			
			var gd = {};
			
			var id = $(inputs[1]).attr("data");
			gd.ID = ((typeof id==="undefined" || id.length===0) ? -1 : parseInt(id));
			gd.OuterSkuID = $.trim($(inputs[1]).val());
			gd.NotifyQty = parseInt($.trim($(inputs[2]).val()));
			gd.NotifyPrice = parseFloat($.trim($(inputs[3]).val()));

			gd.SheetID = refundData.sheetID;
			
			if (gd.OuterSkuID.length===0){
				showTips("SKU不能为空");
				gds = false;
				return false;
			}			
			
			if (gd.NotifyQty.length===0){
				showTips("商品数量不能为空");
				gds = false;
				return false;
			}
			
			if (gd.NotifyPrice.length===0){
				showTips("商品价格不能为空");
				gds = false;
				return false;
			}
			

			if (gd.ID!==-1){
				var oldGd = findGoodsById(gd.ID);
				if (oldGd.outerSkuID===gd.OuterSkuID && oldGd.NotifyQty===gd.NotifyQty && oldGd.NotifyPrice===gd.NotifyPrice && oldGd.Note===gd.Note){
					return true;//continue
				}
			}
			
			gds.push(gd);
		});
		
		return gds;
	}
	
	$("#save_btn").click(function(){
		var refund = getRefundData();
		if (refund===false) return;
		
		var gds = getGoodsData();
		if (gds===false) return;
		
		if (propCountOf(refund)>1){
			disableRefund();
		
			$.ajax({
				url:"./saveRefundSheet.do",
				type:"post",
				dataType:"json",
				data:JSON.stringify({RefundSheets:[refund]}),
				success:function(rsp){
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						if (rsp.data instanceof Array && rsp.data.length>0 && "ID" in rsp.data[0]){//有返回新的ID，表示是新订单
							afterSaveRefund(rsp.data[0].ID, rsp.data[0].SheetID);
							
							for(var i=0; i<gds.length; i++){
								gds[i].SheetID = rsp.data[0].SheetID;
							}
						}else{
							afterSaveRefund();
						}
						
						if (gds.length>0){
							setTimeout(function(){saveGrid(gds);}, 100);
						}else{
							showTips("保存订单成功");
						}
						
						isModified = true;
					}
					
					enableRefund();
				},
				error:function(xhr,msg){
					showTips(msg);
					
					enableRefund();
				}
			});
		}else{
			if (gds.length>0){
				saveGrid(gds);
			}else{
				showTips("没有修改，不用保存");
			}
		}
	});
	
	$(".dialog_close").click(function(){
		$(this).parents(".dialog").hide();
	});
	$("#dlg_btn").click(function(){
		$(this).parents(".dialog").hide();
	});
});
