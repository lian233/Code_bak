/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

if (typeof allData === "undefined") allData = {};//{curLogin:{ID:1,Name:"admin",CName:"超级管理员",CustomerID:1},part:[{ID:1,Name:'role1'},{ID:2,Name:'role2'},{ID:3,Name:'role3'},{ID:4,Name:'role4'}]};

//列变量
var ci_name = 0;
var ci_cname = 1;
var ci_password = 2;
var ci_status = 3;
var ci_customer = 4;
var ci_note = 5;

//列输入变量
var cin_name = 0;
var cin_cname = 1;
var cin_password = 2;
var cin_customer = 3;
var cin_note = 4;

input_select_load_url = "./qryCustomerList.do";

input_select_load_callback = function(rsp, itemHtmls){
	if (rsp instanceof Array){
		for(var i=0; i<rsp.length; i++){
			itemHtmls.push(input_select_list_item_html(rsp[i].ID, rsp[i].Name));
		}
	}
};

function showTips(text){
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
				url: "./qryLogin.do", 
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
		$("#account_list tr:gt(0):not(:last)").each(function(){
			$(this).remove();
		});
	}
	
	function restoreGrid(){
		$("#account_list tr:gt(0):not(:last)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			
			var field = $(inputs[cin_name]);
			
			var id = field.attr("data");
			if (typeof(id)==="undefined"){//没有ID，是新的数据，则删除
				alert("删除");
				tr.remove();
				return true;//继续each.
			}
			
			field.val(field.attr("old"));

			field = $(inputs[cin_cname]);
			field.val(field.attr("old"));
			
			field = $(inputs[cin_password]);
			field.val(field.attr("old"));
			
			field = $(inputs[cin_customer]);
			field.attr("data", field.attr("old"));//客户ID
			field.val(field.attr("oldText"));
			
			field = $(inputs[cin_note]);
			field.val(field.attr("old"));
			
			var sel = tr.find("select");
			sel[0].selectedIndex = sel.attr("old");
		});
	}

	function init(){
		//初始化数据
		if (allData){
			if ("curLogin" in allData){
				var info = $(".acount_info");
				info.html(allData.curLogin.Name+"["+allData.curLogin.CName+"]");
				info.attr("data", allData.curLogin.ID);
			}

			if ("part" in allData){
				var list = $("#list1");
				var ln = allData.part.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.part[i].id+"'>"+allData.part[i].name+"</option>").appendTo(list);
				}
			}
			
			if (typeof(resetPageCtrl)!=="undefined"){
				resetPageCtrl({rowCnt:0, page:0, psize:10});
			}
		}

	};
	
	function addGridRow(){
		var txt = "<tr><td></td><td></td><td></td><!--td></td--><td></td><td></td><td></td></tr>";
		$("#account_list tr:last").before(txt);
		
		var tr = $("#account_list tr:last").prev();
		var cells = tr.find("td");
		
		$(cells[ci_name]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入账号\">");
		$(cells[ci_cname]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入中文名\">");
		$(cells[ci_password]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入密码\">");
		$(cells[ci_status]).addClass("tb_fix_width_short");
		$(cells[ci_status]).html("<div class=\"select_wraper remove_left_margin\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value='1'>正常</option><option value='0'>停用</option></select></div>");
		$(cells[ci_customer]).addClass("tb_fix_width_short");
		$(cells[ci_customer]).html("<div class=\"input_select_wraper remove_left_margin\"><div class=\"input_select_down_btn\"><div class=\"input_select_down_arrow\"></div></div><div class=\"input_select_text_wraper\"><input type=\"text\" class=\"input_select_text_field\"/></div><div class=\"input_select_list_wraper\"></div></div>");
		$(cells[ci_note]).addClass("tb_fix_width");
		$(cells[ci_note]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入备注\">");
		
		return tr;
	}
	
	function fillGrid(data){
		var ln = data.length;
		for(var i=0; i<ln; i++){
			var tr = addGridRow();
			var inputs = tr.find("input");

			var cstm = data[i];
			var field = $(inputs[cin_name]);
			field.attr("data",cstm.ID);
			field.attr("old",cstm.Name);
			field.val(cstm.Name);

			field = $(inputs[cin_cname]);
			field.attr("old",cstm.CName);
			field.val(cstm.CName);

			field = $(inputs[cin_password]);
			field.attr("old",cstm.Password);
			field.val(cstm.Password);

			field = $(inputs[cin_customer]);
			field.attr("data", cstm.CustomerID);
			field.attr("old", cstm.CustomerID);
			field.attr("oldText", cstm.CustomerName);
			field.val(cstm.CustomerName);

			field = $(inputs[cin_note]);
			field.attr("old", cstm.Note);
			field.val(cstm.Note);

			var sel = tr.find("select");
			sel.attr("old", cstm.Status);
			sel[0].selectedIndex = cstm.Status==1 ? 0 : 1;
		}
	}
	
	function getModifiedData(){
		var data = {logins:[]};
		
		$("#account_list tr:gt(0):not(:last)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			var one = {};
			
			var field = $(inputs[cin_name]);
			var cid = field.attr("data");
			var old = field.attr("old");
			var val = $.trim(field.val());
			
			if (val.length===0){
				showTips("帐号不能为空");
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
			}
			
			field = $(inputs[cin_cname]);
			old = field.attr("old");
			val = $.trim(field.val());
			
			if (val.length===0){
				showTips("中文名不能为空");
				data = false;
				return false;
			}
			
			if (typeof(old)!=="undefined"){
				if (old!==val) one.CName = val;
			}else{
				one.CName = val;
			}
			
			field = $(inputs[cin_password]);
			old = field.attr("old");
			val = $.trim(field.val());
			
			/*
			if (val.length===0){
				showTips("密码不能为空");
				data = false;
				return false;
			}*/
			
			if (typeof(old)!=="undefined"){
				if (old!==val) one.Password = val;
			}else{
				one.Password = val;
			}
			
			field = $(inputs[cin_customer]);
			cid = field.attr("data");
			old = field.attr("old");
			
			if (cid.length===0){
				showTips("客户不能为空"); 
				data = false;
				return false;
			}
			
			if (typeof(old)!=="undefined"){
				if (old!=cid) one.CustomerID = parseInt(cid);
			}else{
				one.CustomerID = parseInt(cid);
			}
			
			field = $(inputs[cin_note]);
			old = field.attr("old");
			val = $.trim(field.val());
			
			if (typeof(old)!=="undefined"){
				if (old!==val) one.Note = val;
			}else if (val.length>0){
				one.Note = val;
			}
			
			var sel = tr.find("select");
			old = sel.attr("old");
			val = parseInt(sel.val());
			
			if (typeof(old)!=="undefined"){
				if (old!=val) one.Status = val;
			}else{
				one.Status = val;
			}
			
			if (one.ID==-1 || "Name" in one || "CName" in one || "Password" in one || "CustomerID" in one || "Note" in one || "Status" in one){
				data.logins.push(one);
			}
		});
		
		return data;
	}
	
	function afterSaveGrid(newLoginID){
		var i = 0;
		$("#account_list tr:gt(0):not(:last)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			
			var field = $(inputs[cin_name]);
			if (typeof(field.attr("data"))==="undefined"){
				field.attr("data", newLoginID[i++]);
			}
			field.attr("old", $.trim(field.val()));

			field = $(inputs[cin_cname]);
			field.attr("old", $.trim(field.val()));
			
			field = $(inputs[cin_password]);
			field.attr("old", $.trim(field.val()));
			
			field = $(inputs[cin_customer]);
			field.attr("old", field.attr("data"));//客户ID
			field.attr("oldText", $.trim(field.val()));
			
			field = $(inputs[cin_note]);
			field.attr("old", $.trim(field.val()));
			
			var sel = tr.find("select");
			sel.attr("old", sel.val());
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
	
	$(".menu").find("span").click(function(e){
		switch(e.target.id){
			case "mi_0":
				window.location.href = "home.html";
				break;
			case "mi_1":
				window.location.href = "logins.html";
				break;
			case "mi_2":
				window.location.href = "customerManage.html";
				break;
			case "mi_3":
				window.location.href = "shopManager.html";
				break;
			case "mi_4":
				window.location.href = "expressManager.html";
				break;
			case "mi_5":
				window.location.href = "order.html";
				break;
			case "mi_6":
				window.location.href = "receiptConfig.html";
				break;
			case "mi_7":
				window.location.href = "default.html";
				break;
		}
	});

	$("#search").click(function(){
		var cid = $("#customers").attr("data");
		var name = $.trim($("#user_name").val());

		//if (cid.length>0 || name.length>0){
			lastSearchParam = {customerID:parseInt(cid),cName:name,pn:0,pageSize:12};
			
			$.ajax({
				url: "./qryLogin.do", 
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
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		//}else{
		//	showTips("请先输入查询条件再重试！");
		//}
	});
	
	$(".dialog_close").click(function(){
		$("#tips_dlg").hide();
	});
	$("#dlg_btn").click(function(){
		$("#tips_dlg").hide();
	});
	
	$("#btn_add").click(function(){
		addGridRow();
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
				rows.push($("#account_list tr:eq("+index+")"));
			}
		});
		
		var delID = [];
		
		for(var i=0; i<rows.length; i++){
			var inputs = rows[i].find("input");
			
			var loginID = $(inputs[cin_name]).attr("data");//取得loginID
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
			var id = $(inputs[cin_name]).attr("data");
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
		else if (param.logins.length===0){
			showTips("没有修改过数据，不需要保存！");
			return;
		}
		
		disableGrid();//先禁止表格的编辑
		
		$.ajax({
			url: "./saveLogin.do", 
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
			var id = $(inputs[cin_name]).attr("data");
			
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
	
});
