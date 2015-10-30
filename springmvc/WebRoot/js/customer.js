/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

if (typeof allData === "undefined") allData = {};//{curLogin:{ID:1,Name:"admin",CName:"超级管理员",CustomerID:1},part:[{ID:1,Name:'role1'},{ID:2,Name:'role2'},{ID:3,Name:'role3'},{ID:4,Name:'role4'}]};

//列变量
var ci_name = 0;
var ci_groupid = 1;
var ci_deliveryid = 2;
var ci_state = 3;
var ci_city = 4;
var ci_district = 5;
var ci_address = 6;
var ci_tele = 7;
var ci_mobile = 8;
var ci_linkMan = 9;
var ci_status = 10;
var ci_note = 11;



//列输入变量
var cin_name = 0;
var cin_state = 1;
var cin_city = 2;
var cin_district = 3;
var cin_address = 4;
var cin_tele = 5;
var cin_mobile = 6;
var cin_linkMan = 7;
var cin_note = 8;




input_select_load_url = "./qryCustomerList.do";

input_select_load_callback = function(rsp, itemHtmls){
	if (rsp instanceof Array){
		for(var i=0; i<rsp.length; i++){
			itemHtmls.push(input_select_list_item_html(rsp[i].ID, rsp[i].Name));
		}
	}
};

function countNum(obj){
		var count=0;
		for(var i in obj){
			count++;
		}
		return count;
	};

function showTips(text){
	if('请求出错了'===text){
			if(countNum(allData)==0){
				window.location.href="default.html";
				return;
			}
			
		}
	var dlg = $("#tips_dlg");
	dlg.show();
	var dbody = dlg.find(".dialog_body");
	dbody.css("left", ($(document).width()-dbody.width())/2+"px");
	dbody.css("top", "100px");
	$(".tips").html(text);
}

$(function(){
	var selectedRow = null;
	var lastSearchParam = null;

	page_ctrl_to_callback = function(pn){
		if (lastSearchParam){
			lastSearchParam.pn = pn;

			$.ajax({
				url: "./qryCustomer.do", 
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

	function clearGrid(){
		$("#customer_list tr:gt(0):not(:last)").each(function(){
			$(this).remove();
		});
	}
	
	function restoreGrid(){
		$("#customer_list tr:gt(0):not(:last)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			
			var field = $(inputs[ci_name]);
			
			var id = field.attr("data");
			if (typeof(id)==="undefined"){//没有ID，是新的数据，则删除
				tr.remove();
				return true;//继续each.
			}
			
			field.val(field.attr("old"));

			field = $(inputs[cin_state]);
			field.val(field.attr("old"));
			
			field = $(inputs[cin_city]);
			field.val(field.attr("old"));
			
			field = $(inputs[cin_district]);
			field.val(field.attr("old"));
			
			field = $(inputs[cin_address]);
			field.val(field.attr("old"));

			field = $(inputs[cin_tele]);
			field.val(field.attr("old"));

			field = $(inputs[cin_mobile]);
			field.val(field.attr("old"));

			field = $(inputs[cin_linkMan]);
			field.val(field.attr("old"));

			field = $(inputs[cin_note]);
			field.val(field.attr("old"));
			
			var sel = tr.find("select");
			$(sel[0]).val($(sel[0]).attr("old"));
			$(sel[1]).val($(sel[1]).attr("old"));
			$(sel[2]).val($(sel[2]).attr("old"));
		});
	}

	function init(){
		if("errorCode" in allData){
			if(-100==allData.errorCode){
			window.location.href="default.html";
		}
		}
		
		//初始化数据
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

			if ("customerGroup" in allData){
				var group_id = $(".select_body_cu");
				var ln = allData.customerGroup.length;
				$("<option></option>").appendTo(group_id);
				//alert(ln);
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.customerGroup[i].ID+"'>"+allData.customerGroup[i].Name+"</option>").appendTo(group_id);
				}
				
			}
			if("menu" in allData){
				if (allData.curLogin.SystemType == "1"){
					setActiveMenu("资料管理","客户管理");
				}
				else{
					setActiveMenu("客户管理", "客户管理");
				}
			}
			
			if (typeof(resetPageCtrl)!=="undefined"){
				resetPageCtrl({rowCnt:0, page:0, psize:10});
			}
		}

	};
	
	function addGridRow(){
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		$("#customer_list tr:last").before(txt);
		var tr = $("#customer_list tr:last").prev();
		var cells = tr.find("td");
		//$(cells[0]).html("<input type=\"checkbox\" style=\”display:none;\"/>");
		$(cells[ci_name]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入客户名字\">");
		$(cells[ci_groupid]).addClass("tb_fix_width_short");
		$(cells[ci_groupid]).html("<div class=\"select_wraper1 remove_left_margin\"><div class=\"select_offset_right\"></div><div class=\"select_arrow1\"></div><select class=\"select_body_cu\"></select></div>");
		$(cells[ci_deliveryid]).addClass("tb_fix_width_short");
		$(cells[ci_deliveryid]).html("<div class=\"select_wraper1 remove_left_margin\"><div class=\"select_offset_right\"></div><div class=\"select_arrow1\"></div><select class=\"select_body_cu\"></select></div>");
		$(cells[ci_state]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入客户所在省份\">");
		$(cells[ci_city]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入客户所在市\">");
		$(cells[ci_district]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入客户所在区\">");
		$(cells[ci_address]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入客户所在地址\">");
		$(cells[ci_tele]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入联系电话\">");
		$(cells[ci_mobile]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入手机\">");
		$(cells[ci_linkMan]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入联系人\">");
		$(cells[ci_status]).addClass("tb_fix_width_short");
		$(cells[ci_status]).html("<div class=\"select_wraper1 remove_left_margin\"><div class=\"select_offset_right\"></div><div class=\"select_arrow1\"></div><select class=\"select_body_cu\"><option value='1'>正常</option><option value='0'>停用</option></select></div>");
		$(cells[ci_note]).addClass("tdnote");
		$(cells[ci_note]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入备注\">");
		return tr;
		
	}
	
	function fillGrid(data){
		
		var ln = data.length;
		//var ss = eval(data);
		for(var i=0; i<ln; i++){
			var tr = addGridRow();
			initPartData(tr);
			var inputs = tr.find("input");

			var cstm = data[i];
			var field = $(inputs[cin_name]);
			field.attr("data",cstm.id);
			field.attr("old",cstm.name);
			field.val(cstm.name);

			

//			field.attr("old",cstm.Password);
//			field.val(cstm.Password);

			field = $(inputs[cin_state]);
			//field.attr("data", cstm.zipCode);
			field.attr("old", cstm.state);
			//field.attr("oldText", cstm.State);
			field.val(cstm.state);

			field = $(inputs[cin_city]);
			field.attr("old", cstm.city);
			field.val(cstm.city);

			field = $(inputs[cin_district]);
			field.attr("old", cstm.district);
			field.val(cstm.district);

			field = $(inputs[cin_address]);
			field.attr("old", cstm.address);
			field.val(cstm.address);

			field = $(inputs[cin_tele]);
			field.attr("old", cstm.tele);
			field.val(cstm.tele);

			field = $(inputs[cin_mobile]);
			field.attr("old", cstm.mobile);
			field.val(cstm.mobile);

			field = $(inputs[cin_linkMan]);
			field.attr("old", cstm.linkMan);
			field.val(cstm.linkMan);

			field = $(inputs[cin_note]);
			field.attr("old", cstm.note);
			field.val(cstm.note);

			var sel = tr.find("select");
			field = $(sel[0]);
			field.attr("old",cstm.groupID);
			field.val(cstm.groupID);

			field = $(sel[1]);
			field.attr("old",cstm.deliveryGroupID);
			field.val(cstm.deliveryGroupID);

			field = $(sel[2]);
			field.attr("old",cstm.status);
			field.val(cstm.status);
		}
	}
	
	function getModifiedData(){
		var data = {customers:[]};
		$("#customer_list tr:gt(0):not(:last)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			var one = {};
			var field = $(inputs[ci_name]);
			var cid = field.attr("data");
			var old = field.attr("old");
			var val = $.trim(field.val());
			
			if (val.length===0){
				showTips("客户名字不能为空");
				data = false;
				return false;
			}
			if (typeof(cid)!=="undefined"){
				one.ID = parseInt(cid);
			}else{
				one.ID = -1;
			}
			
			if (typeof(old)!=="undefined"){
				if (old!==val) one.Name = val;
			}else{
				one.Name = val;
				//field.attr();
			}
			

			field = $(inputs[cin_state]);
			old = field.attr("old");
			val = $.trim(field.val());

			if (val.length===0){
				showTips("客户所在省份不能为空");
				data = false;
				return false;
			}
			if(typeof(old)!=="undefined") {
				if(old!==val){
					one.State=val;
				}
			}else{
				one.State = val;
			}
			field = $(inputs[cin_city]);
			old = field.attr("old");
			val = $.trim(field.val());

			if (val.length===0){
				showTips("客户所在市不能为空");
				data = false;
				return false;
			}

			if(typeof(old)!=="undefined") {
				if(old!==val){
					one.City=val;
				}
			}else{
				one.City = val;
			}

			field = $(inputs[cin_district]);
			old = field.attr("old");
			val = $.trim(field.val());

			if (val.length===0){
				showTips("客户所在区不能为空");
				data = false;
				return false;
			}

			if(typeof(old)!=="undefined"){
				if(old!==val){
					one.District=val;
				}
			}else{
				one.District = val;
			}

			field = $(inputs[cin_address]);
			old = field.attr("old");
			val = $.trim(field.val());

			if(typeof(old)!=="undefined") {
				if(old!==val){
					one.Address=val;
				}
			}else{
				one.Address = val;
			}

			field = $(inputs[cin_tele]);
			old = field.attr("old");
			val = $.trim(field.val());

			if(typeof(old)!=="undefined") {
				if(old!==val){
					one.Tele=val;
				}
			}else{
				one.Tele = val;
			}

			field = $(inputs[cin_mobile]);
			old = field.attr("old");
			val = $.trim(field.val());

			if (val.length===0){
				showTips("客户手机不能为空");
				data = false;
				return false;
			}

			if(typeof(old)!=="undefined") {
				if(old!==val){
					one.Mobile=val;
				}
			}else{
				one.Mobile = val;
			}
		
			field = $(inputs[cin_linkMan]);
			old = field.attr("old");
			val = $.trim(field.val());

			if (val.length===0){
				showTips("客户联系人不能为空");
				data = false;
				return false;
			}

			if(typeof(old)!=="undefined") {
				if(old!==val){
					one.LinkMan=val;
				}
			}else{
				one.LinkMan = val;
			}


			field = $(inputs[cin_note]);
			old = field.attr("old");
			val = $.trim(field.val());

			if(typeof(old)!=="undefined") {
				if(old!==val){
					one.Note=val;
				}
			}else{
				one.Note = val;
			}
			

			var sel=	tr.find("select")
			var g = $(sel[0]);
			old = parseInt(g.attr("old"));
			val = parseInt(g.val());
			if(typeof(old)!=="undefined"){
				if(old!==val){
					one.GroupID=val;
				}
			}else{
				one.GroupID=val;
			}

			var d = $(sel[1]);;//sel[1];
			old = parseInt(d.attr("old"));
			val = parseInt(d.val());
			if(typeof(old)!=="undefined"){
				if(old!==val){
					one.DeliveryGroupID=val;
				}
			}else{
				one.DeliveryGroupID=val;
			}

			var s = $(sel[2]);;//sel[1];
			old = parseInt(s.attr("old"));
			val = parseInt(s.val());
			if(typeof(old)!=="undefined"){
				if(old!==val){
					one.Status=val;
				}
			}else{
				
				one.Status=val;
			}
			
			if (one.ID==-1 || "Name" in one || "GroupID" in one || "Address" in one || "City" in one || "State" in one || "District" in one || "Tele" in one || "Moblie" in one || "LinkMan" in one || "Status" in one || "DeliveryGroupID" in one){
				data.customers.push(one);
			}
		});
		
		return data;
	}
	/**
	 *var cin_name = 0;
var cin_state = 1;
var cin_city = 2;
var cin_district = 3;
var cin_address = 4;
var cin_tele = 5;
var cin_mobile = 6;
var cin_linkMan = 7;
var cin_note = 8;
	 */
	function afterSaveGrid(newLoginID){
		var i = 0;
		$("#customer_list tr:gt(0):not(:last)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			
			var field = $(inputs[ci_name]);
			if (typeof(field.attr("data"))==="undefined")
				field.attr("data", newLoginID[i++]);  ////客户ID
				field.attr("old", $.trim(field.val()));

				field = $(inputs[cin_state]);
				field.attr("old", $.trim(field.val()));
			
				field = $(inputs[cin_city]);
				field.attr("old", $.trim(field.val()));
			
				field = $(inputs[cin_district]);
				field.attr("old", $.trim(field.val()));
				field.attr("oldText", $.trim(field.val()));

				field = $(inputs[cin_address]);
				field.attr("old", $.trim(field.val()));
				field.attr("oldText", $.trim(field.val()));

				field = $(inputs[cin_tele]);
				field.attr("old", $.trim(field.val()));
				field.attr("oldText", $.trim(field.val()));

				field = $(inputs[cin_mobile]);
				field.attr("old", $.trim(field.val()));
				field.attr("oldText", $.trim(field.val()));

				field = $(inputs[cin_linkMan]);
				field.attr("old", $.trim(field.val()));
				field.attr("oldText", $.trim(field.val()));

				field = $(inputs[cin_note]);
				field.attr("old", $.trim(field.val()));
				field.attr("oldText", $.trim(field.val()));
			
				var sel = tr.find("select");

				field = $(sel[0]);
				field.attr("old", $.trim(field.val()));

				field = $(sel[1]);
				field.attr("old", $.trim(field.val()));

				field = $(sel[2]);
				field.attr("old", $.trim(field.val()));
			
			
			
			
		});
	}
	
	function disableGrid(){
		$("table input").attr("disabled", "disabled");
		$("table select").attr("disabled", "disabled");
	}
	
	function enableGrid(){
		$("table input").removeAttr("disabled");
		$("table select").removeAttr("disabled");
	}
	
	function moveAllRightRolesToLeft(){
		var list1 = $("#list1");

		$("#list2 option").each(function(){
			$("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>").appendTo(list1);
		});
		
		$("#list2").empty();
	}
	
	function moveLeftRolesToRight(roleIds){
		var list2 = $("#list2");

		for(var i=0; i<roleIds.length; i++){//该用户的所有角色
			var pid = roleIds[i].PartID;

			var item = $("#list1 option[value="+pid+"]");
			if (item.length>0){
				var txt = item.text();
				item.remove();

				$("<option value='"+pid+"'>"+txt+"</option>").appendTo(list2);
			}
		}
	}
	
	$.ajaxSetup({async: false});//设置AJAX请求为同步请求，以防操作顺序混乱
	
	init();
	/**
	$(".menu").find("span").click(function(e){
		switch(e.target.id){
			case "mi_0"://55
				window.location.href = "home.html";
				break;
			case "mi_1"://145
				window.location.href = "logins.html";
				break;
			case "mi_2"://267
				window.location.href = "customerManage.html";
				break;
			case "mi_3"://373
				window.location.href = "shopManager.html";
				break;
			case "mi_4"://451
				window.location.href = "expressManager.html";
				break;
			case "mi_5":
				window.location.href = "bill.html";
				break;
			case "mi_6":
				window.location.href = "receiptConfig.html";
				break;
			case "mi_7":
				window.location.href = "default.html";
				break;
		}
	});
	**/

	$("#search").click(function(){
		var name = $.trim($("#customer_name").val());
		var groupId = $("#group_id").val();
		var customer_code = $.trim($("#customer_code").val());
		var linkman = $.trim($("#linkman").val());
		var address = $.trim($("#address").val());

		
		if(groupId!='')
			lastSearchParam = {name:name,groupId:parseInt(groupId),code:customer_code,address:address,linkman:linkman,pn:0,pageSize:12};
		else 
			lastSearchParam = {name:name,code:customer_code,address:address,linkman:linkman,pn:0,pageSize:12};
			
			$.ajax({
				url: "./qryCustomer.do", 
				type: "post", 
				data: JSON.stringify(lastSearchParam), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						clearGrid();
						fillGrid(rsp.data);
					if (typeof(resetPageCtrl)!=="undefined"){

							resetPageCtrl(rsp.pageInfo);
						}
					}
				}, 
				error: function(data){
					var ss =eval(data);
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		//}else{
			//showTips("请先输入查询条件再重试！");
		//}
	});
	
	$(".dialog_close").click(function(){
		$("#tips_dlg").hide();
	});
	$("#dlg_btn").click(function(){
		$("#tips_dlg").hide();
	});
	
	$("#btn_add").click(function(){
		var tr = addGridRow();
		initPartData(tr);
		
	});
	
	$("#all_check").click(function(){
		var _this = this;
		var v = this.checked;
		
		$(":checkbox").each(function(){
			if (this!==_this) this.checked = v;
		});
	});
	
	$("#btn_remove").click(function(){
		
		if (!confirm("是否确定删除所选择的行？")){
			return;
		}
		
		var rows = [];
		
		$(":checkbox").each(function(index, ele){
			if (index>0 && ele.checked){
				rows.push($("#customer_list tr:eq("+index+")"));
			}
		});
		//alert(rows.length);
		
		var delID = [];
		
		for(var i=0; i<rows.length; i++){
			var inputs = rows[i].find("input");
			var loginID = $(inputs[1]).attr("data");//取得loginID
			if (typeof(loginID)!=="undefined"){
				delID.push(loginID);
			}
		}
		

		if (delID.length===0){//没有选择旧帐号
			for(var i=0; i<rows.length; i++){
			rows[i].remove();
			}

			$("#all_check").attr("checked", false);
		}else{//选择了旧帐号
			$.ajax({
				url: "./removeLogin.do", 
				type: "post", 
				data: JSON.stringify({loginID:delID}), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						for(var i=0; i<rows.length; i++){
							rows[i].remove();
						}
						$("#all_check").attr("checked", false);

						showTips("删除成功");
					}
				}, 
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
	});
	
	$("#btn_move_right").click(function(){
		var list2 = $("#list2");
		
		$("#list1 option:selected").each(function(){
			$("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>").appendTo(list2);
			$(this).remove();
		});
	});
	
	$("#btn_move_left").click(function(){
		var list1 = $("#list1");
		
		$("#list2 option:selected").each(function(){
			$("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>").appendTo(list1);
			$(this).remove();
		});
	});
	
	$("#btn_save_role").click(function(){
		if (selectedRow){
			var inputs = $(selectedRow).find("input");
			var id = $(inputs[1]).attr("data");
			var roles = [];
			
			$("#list2 option").each(function(){
				roles.push($(this).val());
			});
			
			if (roles.length>0){
				$.ajax({
					url:"./modifyPartMember.do", 
					type: "post", 
					data:JSON.stringify({loginID:parseInt(id), PartID:roles}), 
					success:function(rsp){
						if (rsp.errorCode!=0){
							showTips(rsp.msg);
						}else{
							showTips("保存成功");
						}
					}, 
					error: function(){
						showTips("请求出错了");
					}, 
					dataType:"json"
				});
			}
		}else{
			showTips("当前还没选择任何账号");
		}
	});
	
	$("#btn_save").click(function(){
		var param = getModifiedData();
		if (param===false) return;
		else if (param.customers.length===0){
			showTips("没有修改过数据，不需要保存！");
			return;
		}
		
		disableGrid();//先禁止表格的编辑
		
		$.ajax({
			url: "./saveCustomer.do", 
			type: "post", 
			data: JSON.stringify(param), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					afterSaveGrid(rsp.data);//保存成功后，按提交请求时的顺序返回所有新的loginID，旧的不需要
					showTips("保存数据成功");
				}

				enableGrid();//开放表格的编辑
			}, 
			error: function(){
				showTips("请求出错了");
				enableGrid();
			},
			dataType: "json"
		});
	});
	
	$("#btn_cancel").click(function(){
		if (!confirm("是否确定取消所做的修改？")){
			return;
		}
		
		restoreGrid();
	});
	
	$("table").on("click","tr:gt(0):not(:last)",function(){
		if (selectedRow!==this){
			if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
			
			$(this).addClass("tr_high_light");
			selectedRow = this;
			
			var inputs = $(this).find("input");
			var id = $(inputs[1]).attr("data");
			
			if (typeof(id)!=="undefined"){
				$.ajax({
					url: "./qryPartMember.do", 
					type: "post", 
					data: JSON.stringify({loginID:parseInt(id)}), 
					success: function(rsp){
						if (rsp.errorCode!=0){
							showTips(rsp.msg);
						}else{
							moveAllRightRolesToLeft();
							moveLeftRolesToRight(rsp.data);
						}
					}, 
					error: function(){
						showTips("请求出错了");
					}, 
					dataType: "json"
				});
			}else{
				moveAllRightRolesToLeft();
			}
		}
	});
	function initPartData(tr){
		var ln = allData.customerGroup.length;
		var ln1 = allData.deliveryGroup.length;
		var t = tr.find('select');
		var groupInf= $(t[0]);
		var deliveryInfo = $(t[1]);
		for(var i=0;i<ln;i++){
			$("<option value='"+allData.customerGroup[i].ID+"'>"+allData.customerGroup[i].Name+"</option>").appendTo(groupInf);
		}
		for(var i=0;i<ln1;i++){
			$("<option value='"+allData.deliveryGroup[i].ID+"'>"+allData.deliveryGroup[i].Name+"</option>").appendTo(deliveryInfo);
		}
	}

	
	
});
