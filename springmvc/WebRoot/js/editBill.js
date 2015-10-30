/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var orderIndex = -1;
var isModified = false;
var orderData = null;

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
						orderIndex = str.substr(p+1);
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

	var shops = null;
	var deliverys = null;
	var goods = null;
	
	function addGridRow(data){
		var h = "";
		if (parent.allData.curLogin.SystemType != "1"){
			h = "hidden";
		}

		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td "+h+" ></td></tr>";
		$("#goods_list tr:last").before(txt);

		var tr = $("#goods_list tr:last").prev();
		var cells = tr.find("td");

		$(cells[0]).html("<input type=\"checkbox\" class=\"checkbox\"/>");
		$(cells[1]).html("<input type='text' class='text' value='"+data.outerSkuID+"' data='"+data.id+"'/>");
		$(cells[2]).html("<img class='goods_photo' src='"+data.picPath+"'/>");
		$(cells[3]).html("<input type='text' class='text' value='"+data.title+"'/>");
		$(cells[4]).html("<input type='text' class='text' value='"+data.skuPropertiesName+"'/>");
		$(cells[5]).html("<input type='text' class='text input_short' value='"+data.purQty+"'/>");
		$(cells[6]).html("<input type='text' class='text input_short' value='"+data.customPrice+"'/>");
		$(cells[7]).html(data.distributePrice);
	}
	
	function fillGrid(d){
		goods = d;
		
		for(var i=0; i<d.length; i++){
			addGridRow(d[i]);
		}
	}
	
	function getOrderData(){
		var order = {ID:orderData.id};
		
		if (orderData.payTime !=null)
		{
			var tvalue = getDateTimeLocalBy(orderData.payTime);
			var payTime = $.trim($("#pay_time").val());
			if (payTime!==tvalue){
				if (payTime.length>0) order.payTime = payTime.replace("T", " ");
				else{
					showTips("支付时间不正确");
					return false;
				}
			}
		}


		var shop = $("#shops").val();
		if (shop<0){
			showTips("必须选择店铺");
			return false;
		}
		if (shop!=orderData.shopID){//改变了店铺
			if (orderData.id!=-1){//不是新订单
				showTips("不是新订单，不能修改所属店铺");
				return false;
			}else{
				order.shopID = shop;
			}
		}
		
		var delivery = $("#deliverys").val();
		if (delivery<0){
			if (parent.allData.curLogin.SystemType!="1")
			{
				showTips("必须选择快递公司");
				return false;
			}
		}
		if (delivery!=orderData.deliveryID) order.deliveryID = delivery;
		
		var payMode = $("#pay_mode").val();
		if (payMode!=orderData.payMode) order.payMode = payMode;
		
		var invoiceFlag = $("#invoice_flag").val();
		if (invoiceFlag!=orderData.invoiceFlag) order.invoiceFlag = invoiceFlag;
		
		var refSheetID = $.trim($("#bill_num").val());
		if (refSheetID!=orderData.refSheetID){
			if (orderData.sheetFlag==1){
				showTips("不能修改网店单号");
				return false;
			}else{
				order.refSheetID = refSheetID;
			}
		}
		
		var buyerNick = $.trim($("#buyerNick").val());
		if (buyerNick!=orderData.buyerNick) order.buyerNick = buyerNick;
		
		var linkMan = $.trim($("#buyer").val());
		if (linkMan.length===0){
			showTips("必须输入收货人");
			return false;
		}
		if (linkMan!=orderData.linkMan) order.linkMan = linkMan;
		
		var state = $.trim($("#province").val());
		if (state.length===0){
			showTips("必须输入省份");
			return false;
		}
		if (state!=orderData.state) order.state = state;

		var deliverySheetID = $.trim($("#deliverySheetID").val());
		if (deliverySheetID!=orderData.deliverySheetID) order.deliverySheetID = deliverySheetID;
		
		var city = $.trim($("#city").val());
		if (city.length===0){
			showTips("必须输入城市");
			return false;
		}
		if (city!=orderData.city) order.city = city;
		
		var district = $.trim($("#district").val());
		if (district!=orderData.district) order.district = district;
		
		var address = $.trim($("#address").val());
		if (address.length===0){
			showTips("必须输入地址");
			return false;
		}
		if (address!=orderData.address) order.address = address;
		
		var phone = $.trim($("#tel").val());
		if (phone!=orderData.phone) order.phone = phone;
		
		var mobile = $.trim($("#mobile").val());
		if (mobile.length===0){
			showTips("必须输入手机");
			return false;
		}
		if (mobile!=orderData.mobile) order.mobile = mobile;
		
		var payFee = $.trim($("#pay_fee").val());
		if (payFee!=orderData.payFee) order.payFee = payFee;
		
		var invoiceTitle = $.trim($("#invoice_title").val());
		if (invoiceTitle!=orderData.invoiceTitle) order.invoiceTitle = invoiceTitle;
		
		var invoiceID = $.trim($("#invoice_num").val());
		if (invoiceID!=orderData.invoiceID) order.invoiceID = invoiceID;
		

		
		var note = $.trim($("#note").val());
		if (note!=orderData.note) order.note = note;
		
		return order;
	}
	
	function getGoodsData(){
		var gds = [];
		
		$("tbody tr:not(:last)").each(function(){
			var inputs = $(this).find("input");
			
			if (isNaN($.trim($(inputs[4]).val()))){
				showTips("商品数量不是有效数字");
				gds = false;
				return false;
			}
			if (isNaN($.trim($(inputs[5]).val()))){
				showTips("商品价格不是有效数字");
				gds = false;
				return false;
			}
			
			var gd = {};
			
			var id = $(inputs[1]).attr("data");
			gd.ID = ((typeof id==="undefined" || id.length===0) ? -1 : parseInt(id));
			gd.OuterSkuID = $.trim($(inputs[1]).val());
			gd.Title = $.trim($(inputs[2]).val());
			gd.SkuPropertiesName = $.trim($(inputs[3]).val());
			gd.PurQty = parseInt($.trim($(inputs[4]).val()));
			gd.CustomPrice = parseFloat($.trim($(inputs[5]).val()));
			gd.SheetID = orderData.sheetID;
			
			/*
			if (gd.OuterSkuID.length===0){
				showTips("SKU不能为空");
				gds = false;
				return false;
			}*/
			
			if (gd.Title.length===0){
				showTips("商品标题不能为空");
				gds = false;
				return false;
			}
			
			if (gd.SkuPropertiesName.length===0){
				showTips("SKU属性不能为空");
				gds = false;
				return false;
			}
			
			if (gd.PurQty.length===0){
				showTips("商品数量不能为空");
				gds = false;
				return false;
			}
			
			if (gd.CustomPrice.length===0){
				showTips("商品价格不能为空");
				gds = false;
				return false;
			}
			
			if (gd.ID!==-1){
				var oldGd = findGoodsById(gd.ID);
				if (oldGd.outerSkuID===gd.OuterSkuID && oldGd.title===gd.Title && 
						oldGd.skuPropertiesName===gd.SkuPropertiesName && 
						oldGd.purQty===gd.PurQty && oldGd.customPrice===gd.CustomPrice){
					return true;//continue
				}
			}
			
			gds.push(gd);
		});
		
		return gds;
	}
	
	function propCountOf(obj){
		var cnt = 0;
		for(var p in obj) cnt++;
		return cnt;
	}
	
	function afterSaveOrder(newID, newSheetID){
		if (typeof newID !== "undefined"){
			orderData.id = newID;
			orderData.sheetID = newSheetID;
		}
		
		var shop = $("#shops").val();
		orderData.shopID = shop;
		
		var delivery = $("#deliverys").val();
		orderData.deliveryID = delivery;
		
		var payMode = $("#pay_mode").val();
		orderData.payMode = payMode;
		
		var invoiceFlag = $("#invoice_flag").val();
		orderData.invoiceFlag = invoiceFlag;
		
		var refSheetID = $.trim($("#bill_num").val());
		orderData.refSheetID = refSheetID;
		
		var buyerNick = $.trim($("#buyerNick").val());
		orderData.buyerNick = buyerNick;
		
		var linkMan = $.trim($("#buyer").val());
		orderData.linkMan = linkMan;
		
		var state = $.trim($("#province").val());
		orderData.state = state;

		var deliverySheetID = $.trim($("#deliverySheetID").val());
		orderData.deliverySheetID = deliverySheetID;
		
		var city = $.trim($("#city").val());
		orderData.city = city;
		
		var district = $.trim($("#district").val());
		orderData.district = district;
		
		var address = $.trim($("#address").val());
		orderData.address = address;
		
		var phone = $.trim($("#tel").val());
		orderData.phone = phone;
		
		var mobile = $.trim($("#mobile").val());
		orderData.mobile = mobile;
		
		var payFee = $.trim($("#pay_fee").val());
		orderData.payFee = payFee;
		
		var invoiceTitle = $.trim($("#invoice_title").val());
		orderData.invoiceTitle = invoiceTitle;
		
		var invoiceID = $.trim($("#invoice_num").val());
		orderData.invoiceID = invoiceID;
		
		var payTime = $.trim($("#pay_time").val());
		if (payTime.length>0){
			payTime.replace("T", " ");
			payTime.replace(/-/g, "/");
			
			var d = new Date(payTime);
			orderData.payTime.time = d.getTime();
		}
		
		var note = $.trim($("#note").val());
		orderData.note = note;
	}
	
	function disableOrder(){
		$("#shops").attr("disabled", "disabled");
		$("#deliverys").attr("disabled", "disabled");
		$("#deliverySheetID").attr("disabled", "disabled");
		$("#pay_mode").attr("disabled", "disabled");
		$("#invoice_flag").attr("disabled", "disabled");
		$("#bill_num").attr("disabled", "disabled");
		$("#buyerNick").attr("disabled", "disabled");
		$("#buyer").attr("disabled", "disabled");
		$("#province").attr("disabled", "disabled");
		$("#city").attr("disabled", "disabled");
		$("#district").attr("disabled", "disabled");
		$("#address").attr("disabled", "disabled");
		$("#tel").attr("disabled", "disabled");
		$("#mobile").attr("disabled", "disabled");
		$("#pay_fee").attr("disabled", "disabled");
		$("#note").attr("disabled", "disabled");
		$("#invoice_title").attr("disabled", "disabled");
		$("#invoice_num").attr("disabled", "disabled");
		$("#pay_time").attr("disabled", "disabled");
	}
	
	function enableOrder(){
		$("#shops").removeAttr("disabled");
		$("#deliverys").removeAttr("disabled");
		$("#deliverySheetID").removeAttr("disabled");		
		$("#pay_mode").removeAttr("disabled");
		$("#invoice_flag").removeAttr("disabled");
		$("#bill_num").removeAttr("disabled");
		$("#buyerNick").removeAttr("disabled");
		$("#buyer").removeAttr("disabled");
		$("#province").removeAttr("disabled");
		$("#city").removeAttr("disabled");
		$("#district").removeAttr("disabled");
		$("#address").removeAttr("disabled");
		$("#tel").removeAttr("disabled");
		$("#mobile").removeAttr("disabled");
		$("#pay_fee").removeAttr("disabled");
		$("#note").removeAttr("disabled");
		$("#invoice_title").removeAttr("disabled");
		$("#invoice_num").removeAttr("disabled");
		$("#pay_time").removeAttr("disabled");
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
			url: "./saveDecOrderItem.do", 
			type: "post", 
			data: JSON.stringify({orderItems:params}), 
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
			if (goods[i].id==id) return goods[i];
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
		if (parent.allData.curLogin.SystemType!="1"){
			//隐藏分销价
			$("#coldp").hide();
		}

		shops = parent.getShops();
		deliverys = parent.getDeliverys();

		var select1 = $("#shops");
		var select2 = $("#deliverys");
		
		if (shops){
			var ln = shops.length;
			for(var i=0; i<ln; i++){
				$("<option value='"+shops[i].ID+"'>"+shops[i].Name+"</option>").appendTo(select1);
			}
		}

		if (parent.allData.curLogin.SystemType=="1")
		{
			$("#lblDelivery").hide();
			$("#deliverys").hide();
			$("#deliverySheetID").hide();
			$("#arrDelivery").hide();			
		}
		if (deliverys){
			var ln = deliverys.length;
			for(var i=0; i<ln; i++){
				$("<option value='"+deliverys[i].ID+"'>"+deliverys[i].Name+"</option>").appendTo(select2);
			}
		}
		
		if (orderIndex!=-1){//修改旧单
			orderData = parent.getOrderData(orderIndex);

			select1.val(orderData.shopID);
			select2.val(orderData.deliveryID);

			$("#pay_mode").val(orderData.payMode);
			$("#invoice_flag").val(orderData.invoiceFlag);
			$("#deliverySheetID").val(orderData.deliverySheetID);

			$("#bill_num").val(orderData.refSheetID);
			$("#buyerNick").val(orderData.buyerNick);
			$("#buyer").val(orderData.linkMan);
			$("#province").val(orderData.state);			
			$("#city").val(orderData.city);
			$("#district").val(orderData.district);
			$("#address").val(orderData.address);
			$("#tel").val(orderData.phone);
			$("#mobile").val(orderData.mobile);
			$("#pay_fee").val(orderData.payFee);
			$("#note").val(orderData.note);
			$("#invoice_title").val(orderData.invoiceTitle);
			$("#invoice_num").val(orderData.invoiceID);
			
			var tvalue = getDateTimeLocalBy(orderData.payTime);
			$("#pay_time").val(tvalue);

			$.ajax({
				url:"./qryDecOrderItem.do",
				type:"post",
				dataType:"json",
				data:JSON.stringify({sheetID:orderData.sheetID}),
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
			orderData = {id:-1,sheetID:"",shopID:-1,deliveryID:-1,payMode:-1,invoiceFlag:-1,refSheetID:-1,buyerNick:"",linkMan:"",
				state:"",city:"",district:"",address:"",phone:"",mobile:"",payFee:0,note:"",invoiceTitle:"",invoiceID:""};
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
		addGridRow({id:"",outerSkuID:"",skuPropertiesName:"",title:"",purQty:"",customPrice:"",picPath:""});
	});
	
	$("#del_btn").click(function(){
		if (!confirm("是否确定删除所选择的行？删除后不可恢复")){
			return;
		}
		
		var rows = [];
		
		$(":checkbox").each(function(index, ele){
			if (index>0 && ele.checked){
				rows.push($("#goods_list tr:eq("+index+")"));
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
				url: "./saveDecOrderItem.do", 
				type: "post", 
				data: JSON.stringify({orderItems:delID}), 
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
	
	$("#save_btn").click(function(){
		var order = getOrderData();
		if (order===false) return;
		
		var gds = getGoodsData();
		if (gds===false) return;
		
		if (propCountOf(order)>1){
			if (order.deliveryID !=null){
				if (order.deliveryID != orderData.deliveryID ){ //修改了快递
					if ($.trim($("#deliverySheetID").val()) != "")
					{
						showTips("修改快递必须删除快递单号！");
						return;
					}
				}
			}

			disableOrder();
		
			$.ajax({
				url:"./saveDecOrder.do",
				type:"post",
				dataType:"json",
				data:JSON.stringify({orders:[order]}),
				success:function(rsp){
					if (rsp.errorCode!=0) showTips(rsp.msg);
					else{
						if (rsp.data instanceof Array && rsp.data.length>0 && "ID" in rsp.data[0]){//有返回新的ID，表示是新订单
							afterSaveOrder(rsp.data[0].ID, rsp.data[0].SheetID);
							
							for(var i=0; i<gds.length; i++){
								gds[i].SheetID = rsp.data[0].SheetID;
							}
						}else{
							afterSaveOrder();
						}
						
						if (gds.length>0){
							setTimeout(function(){saveGrid(gds);}, 100);
						}else{
							showTips("保存订单成功");
						}
						
						isModified = true;
					}
					
					enableOrder();
				},
				error:function(xhr,msg){
					showTips(msg);
					
					enableOrder();
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