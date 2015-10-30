/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
 //allData变量声明
 //用于存储返回的初始化数据
 if (typeof allData === "undefined") allData = {};
 
 //渠道列表
var channel = {};
 
 //客户列表
var customerlist = {};
 
var idx = 0;
 //字段索引定义
var idxName = 0;
var idxCustomerID = 1;
var idxChannelID = 2;
var idxCode = 3;
var idxNetAddr = 4;
var idxLinkMan = 5;
var idxTele = 6;
var idxCanMerge = 7;
var idxCanSeparate = 8;
var idxSynFlag = 9;
var idxStatus = 10;
var idxNote = 11;


var selChannelID = 0;
var selCanMerge = 1;
var selCanSeparate = 2;
var selSynFlag = 3;
var selStatus = 4;

var inpName = 0;
var inpCustomerID=1;
var inpCode = 2;
var inpNetAddr = 3;
var inpLinkMan = 4;
var inpTele = 5;
var inpNote = 6;


//每页行数设定
var pageSizeSetting = 10;

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
	
	//翻页功能回调函数
	page_ctrl_to_callback = function(pn){
		if (lastSearchParam){
			lastSearchParam.pn = pn;

			$.ajax({
				url: "./qryShop.do", 
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
	
	//"客户"下拉菜单(ok)
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
	$("#customers").on("dblclick",function(){
		if(this.value.length == 0)
		{
			var input_selectlist = $("#customers").parent("div").parent("div").find(".input_select_list_wraper");
			var ln = customerlist.length;
			$(input_selectlist).find("span").remove();
			for(var i=0; i<ln; i++){
				$("<span data=\""+customerlist[i].ID+"\">"+$.trim(customerlist[i].Name)+"</span>").appendTo(input_selectlist);
			}
			getCustomerlist();
		}
	})
	
		$.ajaxSetup({async: false});
	
	//获取所有客户列表(ok)
	function getCustomerlist(){
			$.ajax({
				url: "./qryCustomerList.do", 
				type: "post", 
				data: "{}", 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						customerlist = rsp.data;
					}
				}, 
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
	}
	
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
				if (allData.curLogin.SystemType == "1"){
					setActiveMenu("资料管理", "店铺管理");
				}
				else{
					setActiveMenu("客户管理", "店铺管理");
				}				
			}

			
			//读取网店类型信息
			if ("channel" in allData){
				channel = allData.channel;
			}
			
			//读取客户列表
			getCustomerlist();
			//if(customerlist)
			
			//设置分页
			if (typeof(resetPageCtrl)!=="undefined"){
				resetPageCtrl({rowCnt:0, page:0, psize:pageSizeSetting});
			}
			
			//按情况隐藏客户列
			hideCustomer();
		}

	};
	

	
	//填充数据到表格(ok)
	function fillGrid(data){
		var ln = data.length;
		for(var i=0; i<ln; i++)
		{
			var tr = addGridRow();
			var inputs = tr.find("input");
			var ShopList = data[i];
			var sel = tr.find("select");
			
			//网店名称
			var field = $(inputs[inpName]);

			field.attr("data",ShopList.id);
			field.attr("old",$.trim(ShopList.name));
			field.val($.trim(ShopList.name));

			//客户
			field = $(inputs[inpCustomerID]);
			field.attr("data", ShopList.customerID);
			field.attr("old", ShopList.customerID);
			field.attr("oldText", $.trim(ShopList.CustomerName));
			field.val($.trim(ShopList.CustomerName));

			//店铺类型
			$(sel[selChannelID]).attr("old", parseInt(ShopList.channelID) - 1);
			sel[selChannelID].selectedIndex = parseInt(ShopList.channelID) - 1;

			//店铺编码
			field = $(inputs[inpCode]);
			field.attr("old",$.trim(ShopList.code));
			field.val($.trim(ShopList.code));

			//网址
			field = $(inputs[inpNetAddr]);
			field.attr("old",$.trim(ShopList.netAddr));
			field.val($.trim(ShopList.netAddr));

			//是否能合单
			$(sel[selCanMerge]).attr("old", parseInt(ShopList.canMerge));
			sel[selCanMerge].selectedIndex = parseInt(ShopList.canMerge);

			//是否能拆单
			$(sel[selCanSeparate]).attr("old", parseInt(ShopList.canSeparate));
			sel[selCanSeparate].selectedIndex = parseInt(ShopList.canSeparate);

			//同步标记
			$(sel[selSynFlag]).attr("old", parseInt(ShopList.synFlag));
			sel[selSynFlag].selectedIndex = parseInt(ShopList.synFlag);

			//状态
			$(sel[selStatus]).attr("old", parseInt(ShopList.status));
			sel[selStatus].selectedIndex = parseInt(ShopList.status);

			//联系人
			field = $(inputs[inpLinkMan]);
			field.attr("old",$.trim(ShopList.linkMan));
			field.val($.trim(ShopList.linkMan));

			//联系电话
			field = $(inputs[inpTele]);
			field.attr("old",$.trim(ShopList.tele));
			field.val($.trim(ShopList.tele));
			
			//备注
			field = $(inputs[inpNote]);
			field.attr("old",$.trim(ShopList.note));
			field.val($.trim(ShopList.note));
			
			//主帐号
			$(tr).attr("nick",$.trim(ShopList.nick));
			
			//网店Key
			$(tr).attr("appKey",$.trim(ShopList.appKey));
			
			//网店Token
			$(tr).attr("token",$.trim(ShopList.token));
			
			//网店Session
			$(tr).attr("session",$.trim(ShopList.session));

			//channelid
			$(tr).attr("channelid",parseInt(ShopList.channelID));
			//shopid
			$(tr).attr("shopid",parseInt(ShopList.id));
			//ordertime
			$(tr).attr("ordertime",ShopList.lastOrderTime);
		}
	}
	
	//清除表格所有数据(ok)
	function clearGrid(){
		selectedRow = null;
		$("#ShopList tr:gt(0)").each(function(){
			$(this).remove();
		});
	}
	
	//还原修改前的数据(ok)
	function restoreGrid(){
		selectedRow = null;
		$("#ShopList tr:gt(0)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			var sel = tr.find("select");
			//取消高亮显示
			tr.removeClass("tr_high_light");
			
			var field = $(inputs[inpName]);
			var id = field.attr("data");
			if (typeof(id)==="undefined"){//没有ID，是新的数据，则删除
				tr.remove();
				return true;//继续each.
			}
			//网店名称
			field.val(field.attr("old"));
			
			//客户
			field = $(inputs[inpCustomerID]);
			field.attr("data", field.attr("old"));//客户ID
			field.val(field.attr("oldText"));
			
			//店铺类型
			sel[selChannelID].selectedIndex = $(sel[selChannelID]).attr("old");
			
			//店铺编码
			field = $(inputs[inpCode]);
			field.val(field.attr("old"));
			
			//网址
			field = $(inputs[inpNetAddr]);
			field.val(field.attr("old"));

			field = $(inputs[inpLinkMan]);
			field.val(field.attr("old"));

			field = $(inputs[inpTele]);
			field.val(field.attr("old"));
			
			//是否能合单
			sel[selCanMerge].selectedIndex = $(sel[selCanMerge]).attr("old");
			
			//是否能拆单
			sel[selCanSeparate].selectedIndex = $(sel[selCanSeparate]).attr("old");
			
			//同步标记
			sel[selSynFlag].selectedIndex = $(sel[selSynFlag]).attr("old");
			
			//状态
			sel[selStatus].selectedIndex = $(sel[selStatus]).attr("old");
			
			//备注
			field = $(inputs[inpNote]);
			field.val(field.attr("old"));
		});
	}
	
	//添加新行(ok)
	function addGridRow(){
		var txt = "<tr><td></td><td class='customerCol'></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		$("#ShopList tbody").append(txt);
		var tr = $("#ShopList tr:last");
		var cells = tr.find("td");
		
		idx++;
		//$(cells[0]).addClass("");
		//店铺名称
		$(cells[idxName]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入店铺名称\">");
		//客户
		$(cells[idxCustomerID]).html("<div class=\"input_select_wraper remove_left_margin\" style=\"width:100px\"><div class=\"input_select_down_btn\"><div class=\"input_select_down_arrow\"></div></div><div class=\"input_select_text_wraper\"><input id=\"customers" + idx + "\" type=\"text\" class=\"input_select_text_field\"/></div><div class=\"input_select_list_wraper\"></div></div>");
			//当客户没写内容时,自动显示全部客户列表
			if(customerlist)
			{
				$(cells[idxCustomerID]).on("dblclick","input",function(){
					if(this.value.length == 0)
					{
						var input_selectlist = $(cells[idxCustomerID]).find(".input_select_list_wraper");
						var ln = customerlist.length;
						$(input_selectlist).find("span").remove();
						for(var i=0; i<ln; i++){
							$("<span data=\""+customerlist[i].ID+"\">"+$.trim(customerlist[i].Name)+"</span>").appendTo(input_selectlist);
						}
						getCustomerlist();
					}
				})
			}
			//每个客户下拉菜单框都加入"事件",实现自动查询
			registInputSelect({
				id:"customers" + idx, 
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
		//店铺类型
		$(cells[idxChannelID]).html("<div class=\"select_wraper remove_left_margin\" style=\"width:100px\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"></select></div>");
		//填充店铺类型数据到select
		if(channel)
		{
			var selectlist = $(cells[idxChannelID]).find("select");
			var ln = channel.length;
			for(var i=0; i<ln; i++){
				$("<option value='"+channel[i].ID+"'>"+channel[i].Name+"</option>").appendTo(selectlist);
			}
		}
		//店铺编码
		$(cells[idxCode]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入店铺编码\">");
		//网址
		$(cells[idxNetAddr]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入店铺网址\">");
		$(cells[idxLinkMan]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入联系人\">");
		$(cells[idxTele]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入联系电话\">");
		//是否能合单
		$(cells[idxCanMerge]).html("<div class=\"select_wraper remove_left_margin\" style=\"width:88px\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value=\"0\">不能合并(仅在线支付,其它能合并)</option><option value=\"1\">能合并(仅在线支付)</option></select></div>");
		//是否能拆单
		$(cells[idxCanSeparate]).html("<div class=\"select_wraper remove_left_margin\" style=\"width:88px\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value=\"0\">不能拆单(仅在线支付,其它能拆单)</option><option value=\"1\">能拆单(仅在线支付)</option></select></div>");
		//同步标志
		$(cells[idxSynFlag]).html("<div class=\"select_wraper remove_left_margin\" style=\"width:80px\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value=\"0\">不同步</option><option value=\"1\">同步</option></select></div>");
		//状态
		$(cells[idxStatus]).html("<div class=\"select_wraper remove_left_margin\" style=\"width:70px\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value=\"0\">停用</option><option value=\"1\">正常</option></select></div>");
		//备注
		$(cells[idxNote]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入备注\"><input type=\"hidden\" id=\"isAddParam\">");
		
		var sel = tr.find("select");
		sel[selCanMerge].selectedIndex = 1;
		sel[selCanSeparate].selectedIndex = 1;
		sel[selSynFlag].selectedIndex = 1;
		sel[selStatus].selectedIndex = 1;
		
		hideCustomer();
		
		return tr;
	}
	
	//获取修改过的列表数据(ok)
	function getModifiedData(){
		var data = {shops:[]};
		
		$("#ShopList tr:gt(0)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			var sel = tr.find("select");
			var one = {};
			var Modified = false;
			
			//*网店ID及Name
			var field = $(inputs[inpName]);
			var sid = field.attr("data");	//店铺ID
			var sname = field.attr("old");	//之前店铺名称
			var val = $.trim(field.val());	//当前店铺名称
				//检查店铺名称是否为空
				if (val.length===0){
					showTips("网店名称不能为空");
					data = false;
					return false;
				}
				//此字段data未定义则为新数据
				if (typeof(sid)!=="undefined"){
					//修改
					one.ID = parseInt(sid);
				}else{
					//添加
					one.ID = -1;
				}
				//检查店铺名称是否需要被保存
				if (typeof(sname)!=="undefined"){
					//已经有old数据但是与当前val不一样就代表被修改过
					if (sname!==val)	Modified = true;
				}
				//必填字段,保存时都需要提交
				one.Name = val;
			//*客户CustomerID
			if (allData.curLogin.CustomerID<=0){
				field = $(inputs[inpCustomerID]);
				cid = field.attr("data");	//当前客户ID
				old = field.attr("old");	//之前的客户ID
				//val = $.trim(field.val);	//客户名称
					if (typeof(cid)!=="undefined" && parseInt(cid) != null){
						if (typeof(old)!=="undefined"){
							//旧数据被修改
							if (old!=cid)	Modified = true;
						}
					}
					else
					{
						showTips("请选择客户!<br/>提示:输入客户后需点击下方列表确认才生效!");
						data = false;
						return false;
					}
					//必填字段,保存时都需要提交
					one.CustomerID = parseInt(cid);
			}
			//*店铺类型ChannelID
			old = $(sel[inpName]).attr("old");			//之前店铺类型ID
			val = parseInt(sel[selChannelID].selectedIndex);	//当前店铺类型ID
				//未选择
				if (val < 0){
					showTips("请选择店铺类型!");
					data = false;
					return false;
				}
				//旧数据被修改
				if (typeof(old)!=="undefined"){
					if (old!=val)	Modified = true;
				}
				//必填字段,保存时都需要提交
				one.ChannelID = val + 1;
			//店铺编码Code
			field = $(inputs[inpCode]);
			old = field.attr("old");
			val = $.trim(field.val());
				//检查
				// if (val.length===0){
					// showTips("店铺编码不能为空");
					// data = false;
					// return false;
				// }
				//判断
				if (typeof(old)!=="undefined"){
					//旧数据被修改
					if (old!=val){Modified = true; one.Code = val;}
				}
				else
				{
					one.Code = val;
				}
			//网址NetAddr
			field = $(inputs[inpNetAddr]);
			old = field.attr("old");
			val = $.trim(field.val());
				if (typeof(old)!=="undefined"){
					if (old!=val){Modified = true; one.NetAddr = val;}
				}else{
					one.NetAddr = val;
				}

			field = $(inputs[inpLinkMan]);
			old = field.attr("old");
			val = $.trim(field.val());
				if (typeof(old)!=="undefined"){
					if (old!=val){Modified = true; one.LinkMan = val;}
				}else{
					one.LinkMan = val;
				}

			field = $(inputs[inpTele]);
			old = field.attr("old");
			val = $.trim(field.val());
				if (typeof(old)!=="undefined"){
					if (old!=val){Modified = true; one.Tele = val;}
				}else{
					one.Tele = val;
				}


			//是否能合单CanMerge
			old = $(sel[selCanMerge]).attr("old");
			val = parseInt(sel[selCanMerge].selectedIndex);
				if (typeof(old)!=="undefined"){
					if (old!=val){Modified = true; one.CanMerge = val;}
				}else{
					one.CanMerge = val;
				}
			//是否能拆单CanSeparate
			old = $(sel[selCanSeparate]).attr("old");
			val = parseInt(sel[selCanSeparate].selectedIndex);
				if (typeof(old)!=="undefined"){
					if (old!=val){Modified = true; one.CanSeparate = val;}
				}else{
					one.CanSeparate = val;
				}
			//同步标记SynFlag
			old = $(sel[selSynFlag]).attr("old");
			val = parseInt(sel[selSynFlag].selectedIndex);
				if (typeof(old)!=="undefined"){
					if (old!=val){Modified = true; one.SynFlag = val;}
				}else{
					one.SynFlag = val;
				}
			//Nick,AppKey,Session,Token
			//*状态Status
			old = $(sel[selStatus]).attr("old");
			val = parseInt(sel[selStatus].selectedIndex);
				if (typeof(old)!=="undefined"){
					if (old!=val) Modified = true;
				}
				one.Status = val;
			//备注Note
			field = $(inputs[inpNote]);
			old = field.attr("old");
			val = $.trim(field.val());
				if (typeof(old)!=="undefined"){
					if (old!=val){Modified = true; one.Note = val;}
				}else{
					one.Note = val;
				}
			//最终检查
			if (one.ID==-1 || Modified)
			{
				//alert(JSON.stringify(one));
				data.shops.push(one);
			}
		});
		
		return data;
	}
	
	//保存成功后的后续处理(ok)
	function afterSaveGrid(newLoginID){
		selectedRow = null;
		var i = 0;
		$("#ShopList tr:gt(0)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			var sel = tr.find("select");
			
			//取消高亮显示
			tr.removeClass("tr_high_light");
			
			//网店ID&名称
			var field = $(inputs[inpName]);
			var id = field.attr("data");
			if (typeof(id)==="undefined"){//没有ID，是新的数据，则把返回的newid填回去
				field.attr("data", newLoginID[i++]);
			}
			field.attr("old", $.trim(field.val()));
			
			//客户
			field = $(inputs[inpCustomerID]);
			field.attr("old", field.attr("data"));//客户ID
			field.attr("oldText", $.trim(field.val()));
			
			//店铺类型
			$(sel[selChannelID]).attr("old", sel[selChannelID].selectedIndex);
			
			//店铺编码
			field = $(inputs[inpCode]);
			field.attr("old", $.trim(field.val()));
			
			//网址
			field = $(inputs[inpNetAddr]);
			field.attr("old", $.trim(field.val()));
			
			//是否能合单
			$(sel[selCanMerge]).attr("old", sel[selCanMerge].selectedIndex);
			
			//是否能拆单
			$(sel[selCanSeparate]).attr("old", sel[selCanSeparate].selectedIndex);
			
			//同步标记
			$(sel[selSynFlag]).attr("old", sel[selSynFlag].selectedIndex);
			
			//状态
			$(sel[selStatus]).attr("old", sel[selStatus].selectedIndex);
			
			//备注
			field = $(inputs[inpNote]);
			field.attr("old", $.trim(field.val()));
		});
	}
	
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
	
	//选择的行高亮显示(ok)
	$("table").on("click","tr:gt(0)",function(){
		if (selectedRow!==this){
			if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
			
			$(this).addClass("tr_high_light");
			selectedRow = this;
		}
	});
	
	//查询按钮(ok)
	$("#search").click(function(){
		//取参数
		var cid = $("#customers").attr("data");
		var cname = $.trim($("#customers").val());
		var shopname = $.trim($("#shop_name").val());
		lastSearchParam = {};
		
		//填充参数
		lastSearchParam.pn = 0;
		lastSearchParam.pageSize = pageSizeSetting;
		//客户
		if(typeof(cid) !== "undefined" && $.trim(cname) !== "")
		{
			if($.trim(cid) == "")
			{
				showTips("请选择客户!<br/>提示:输入客户后需点击下方列表确认才生效!");
				return;
			}
			else
			{
				lastSearchParam.customerID = parseInt(cid);
			}
		}
		//店铺名称
		if(shopname.length > 0)
		{
			lastSearchParam.name = shopname;
		}
		
		//执行查询
		$.ajax({
			url: "./qryShop.do", 
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
	});
	
	//添加按钮(ok)
	$("#btn_Add").click(function(){
		addGridRow();
	});
	
	//保存按钮(ok)
	$("#btn_Save").click(function(){
		var param = getModifiedData();
		if (param===false) return;
		else if (param.shops.length===0){
			showTips("没有修改过数据，不需要保存！");
			return;
		}
		
		disableGrid();//先禁止表格的编辑
		
		$.ajax({
			url: "./saveShop.do", 
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
	
	//取消按钮(ok)
	$("#btn_Cancel").click(function(){
		if (!confirm("是否确定取消所做的修改？")){
			return;
		}
		restoreGrid();
	});
	
	//网店参数窗口(ok)
	$("#shop_setting").click(function(){
		var dlg = $("#setting_dialog");
		var dbody = $(dlg.find(".dialog_body"));
		var frame = $("#frmShopSetting");
		var framedoc = $(window.frames[0].document);
		var objEvt;
		var rowobj = {};
		
		//例行检查,并准备必要数据
		if (selectedRow==null)
		{
			showTips("请选择一个店铺进行设置！");
			return;
		}
		else
		{
			//判断当前店铺资料是否已经保存
			var inputs = $(selectedRow).find("input");
			var field = $(inputs[inpName]);
			var sid = field.attr("data");	//店铺ID
				//此字段data未定义则为新数据
				if (typeof(sid)=="undefined"){
					showTips("请您先保存店铺信息后再进行网店参数设置！");
					return;
				}
		}
		
		//设置显示位置
		dbody.css("width","auto");
		dbody.css("height","auto");
		dbody.css("left", ($(document).width()- parseInt(frame.css("width").replace("px","")))/2+"px");
		dbody.css("top",  "30px");
		
		//读取数据到文本框中
		//主帐号
		framedoc.find("#txtNick").val($.trim($(selectedRow).attr("nick")));
		//网店Key
		framedoc.find("#txtAppKey").val($.trim($(selectedRow).attr("appKey")));
		//网店Token
		framedoc.find("#txtSession").val($.trim($(selectedRow).attr("token")));
		//网店Session
		framedoc.find("#txtToken").val($.trim($(selectedRow).attr("session")));
		
		//渠道id
		framedoc.find("#channelid").val($(selectedRow).attr("channelid"));
		//店id
		framedoc.find("#shopid").val($(selectedRow).attr("shopid"));
		//订单下载时间

		if (typeof $(selectedRow).attr("ordertime")=== "undefined")
		{
			var t = new Date();
			var m = ""+(t.getMonth()+1);
			if (m.length==1){
				m='0'+m;
			}
			var d = ""+(t.getDate());
			if (d.length==1){
				d='0'+d;
			}

			framedoc.find("#ordertime").val(""+t.getFullYear()+"-"+m+"-"+d + " 00:00:00");
			//framedoc.find("#ordertime").val(t.toLocaleString());
		}
		else{
			framedoc.find("#ordertime").val($(selectedRow).attr("ordertime"));
		}
		

		var sel = $(selectedRow).find("select");

		framedoc.find("#appurl").attr("href",channel[sel[selChannelID].selectedIndex].AppUrl);
		
		//绑定取消按钮事件
		var btnCancel = framedoc.find("#btn_Cancel");
		objEvt = $._data($(btnCancel)[0], "events")
		if(!objEvt || !objEvt["click"])
		{//判断是否已经绑定,确保不会重复绑定
			$(btnCancel).click(function(){
				$(window.parent.document).find("#setting_dialog").hide();
			});
		}
		
		//绑定保存按钮事件
		var btnSave = framedoc.find("#btn_Save_params");
		objEvt = $._data($(btnSave)[0], "events")
		if(!objEvt || !objEvt["click"])
		{
			$(btnSave).click(function(){
				//准备要发送的数据
				var rowobj = {};
				var inputs = $(selectedRow).find("input");
				var sel = $(selectedRow).find("select");
				var channelid = parseInt($(sel[selChannelID]).val());
				//店铺ID&名称
				var field = $(inputs[inpName]);
				var sid = field.attr("data");	//店铺ID
				rowobj.ID = parseInt(sid);
				rowobj.channelid = channelid;
				rowobj.Nick = $.trim(framedoc.find("#txtNick").val());
				rowobj.AppKey = $.trim(framedoc.find("#txtAppKey").val());
				rowobj.Token = $.trim(framedoc.find("#txtSession").val());
				rowobj.Session = $.trim(framedoc.find("#txtToken").val());
				rowobj.lastordertime = $.trim(framedoc.find("#ordertime").val());
				if(rowobj.Nick.length==0 || rowobj.AppKey.length==0 || rowobj.Token.length==0 || rowobj.Session.length==0 || rowobj.lastordertime.length==0){
					showTips('主帐号 appkey appsession token 订单下载时间不能为空');
					return ;
				}
				//alert(JSON.stringify(data));
				//发送请求
				$.ajax({
					url: "./updateShopParams.do", 
					type: "post", 
					data: JSON.stringify(rowobj), 
					success: function(rsp){
						if (rsp.errorCode!=0){
							showTips(rsp.msg);
						}else{
							//保存主帐号
							$(selectedRow).attr("nick",rowobj.Nick);
							//保存网店Key
							$(selectedRow).attr("appKey",rowobj.AppKey);
							//保存网店Token
							$(selectedRow).attr("token",rowobj.Token);
							//保存网店Session
							$(selectedRow).attr("session",rowobj.Session);
							//关闭窗口
							$(window.parent.document).find("#setting_dialog").hide();
							//提示
							showTips("设置保存成功");
						}
					}, 
					error: function(){
						showTips("请求出错了");
					},
					dataType: "json"
				});
			});
		}

		//绑定获取token按钮事件
		var btn_gettoken = framedoc.find("#btn_gettoken");
		objEvt = $._data($(btn_gettoken)[0], "events")
		if(!objEvt || !objEvt["click"])
		{
			$(btn_gettoken).click(function(){
				//准备要发送的数据
				var data = {};
				var inputs = $(selectedRow).find("input");
				data.channelid = parseInt(framedoc.find("#channelid").val());
				data.AppKey = $.trim(framedoc.find("#txtAppKey").val());
				data.Session = $.trim(framedoc.find("#txtToken").val());
				data.gettoken = $.trim(framedoc.find("#getToken").val());
				data.id = parseInt(framedoc.find("#shopid").val());
				//alert(JSON.stringify(data));
				//发送请求
				if(data.gettoken.length==0 || data.Session.length==0 || data.AppKey.length==0)
				{
					showTips("请输入appkey,appsecret,地址参数");
					return;
				}
				$.ajax({
					url: "./getToken.do", 
					type: "post", 
					data: JSON.stringify(data), 
					success: function(rsp){
						if (rsp.errorCode!=0){
							showTips(rsp.msg);
						}else{
							//保存主帐号
							//$(selectedRow).attr("nick",rowobj.Nick);
							//保存网店Key
							//$(selectedRow).attr("appKey",rowobj.AppKey);
							//保存网店Token
							$(selectedRow).attr("token",rsp.data.token);
							framedoc.find("#txtSession").val(rsp.data.token);
							framedoc.find("#getToken").val("");
							//保存网店Session
							//$(selectedRow).attr("session",rowobj.Session);
							//关闭窗口
							//$(window.parent.document).find("#setting_dialog").hide();
							//提示
							showTips("获取token成功");
						}
					}, 
					error: function(){
						showTips("请求出错了");
					},
					dataType: "json"
				});
			});
		}
		
		//显示窗口
		dlg.show();
	});
	
	//隐藏客户字段(ok)
	function hideCustomer(){
		if (allData.curLogin.CustomerID>0)
		{
			var c = $("#customerLabel");
			c.hide();
			c = $(".input_select_wraper");
			c.hide();
			$(".customerCol").hide();
		}
	}
	
	//关闭窗口(ok)
	$(".dialog_close").click(function(){
		var dialogbody = $(this).parent("div");
		$(dialogbody).parent("div").hide();
	});

	//关闭提示框(ok)
	$("#dlg_btn").click(function(){
		$("#tips_dlg").hide();
	});
	
	//开始初始化
	init();
});
