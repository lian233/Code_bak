 //allData变量声明
 //用于存储返回的初始化数据
	if (typeof allData === "undefined") allData = {};

//当前登录的客户ID
	var loginCustomerID = null;

//当前登录的分销层级
	var loginCustomerLevel = null;
	
//当前登录的客户名称
	var loginCustomerName = "";
	
//当前选中的客户id
	var currentCustomerID = null;
	
//当前选中节点的名称
	var currentNodeName = "";
	
//分销商数据
	var DistributorData = [];
	
//目录树,菜单
	var zTree, rMenu;
	
//字段索引定义(价格表)
	//var idxCustomerID = 1;
	//var idxParentID = 2;
	var idxGoodsLevel = 1;
	var idxGoodsKey = 2;
	var idxSetType = 3;
	var idxValue = 4;
	
	var selGoodsLevel = 0;
	var selSetType = 1;
	
	//var inpCustomerID = 1;
	//var inpParentID = 2;
	var inpGoodsKey = 1;
	var inpValue = 2;
	
//纯数字正则
	var reNumber = /^[0-9]+.?[0-9]*$/; 
	
//搜索结果
	var resultNodes = null;
	var currkeytxt = "";
	var currResultIndex = 0;
	
//弹出提示框
	function showTips(text){
		var dlg = $("#tips_dlg");
		dlg.fadeIn(100);

		windowResize();
		
		$("#tips_dlg .tips").html(text);
	}

//控制等待对话框
	function CallWaitdlg(Display){
		if(Display == true)
		{
			$("#Wait_dlg").fadeIn(50);
			windowResize();
		}
		else
			$("#Wait_dlg").fadeOut(50);
	}
	
//界面初始化
	$(document).ready(function(){
		zTree = $.fn.zTree.getZTreeObj("dtree");
		rMenu = $("#rMenu");
		//目录树特效
		$("#rMenu li").hover(
		  function () {
			$(this).attr("style","background-color:#87CEFA;");
		  },
		  function () {
			$(this).removeAttr("style");
		  }
		);
		//隐藏右键菜单
		$("#rMenu").hide();
		//自动调整页面
		$(window).resize(windowResize);
		windowResize();
	});
		
//调整界面
	function windowResize(){ 
		var contentW = $("#Content").width();
		var contentH = $("#Content").height();
		var queryBarH = $("#query_bar").outerHeight(true);		//目前55px
		//debug
		//console.log(contentW + "," + contentH + "  " + queryBarH);
		
		//目录树
		$("#leftTree").height(contentH - 56);
		$("#dtree").height(contentH - 68);
		
		//分销商信息及价格表
		var treeW = $("#leftTree").outerWidth(true);
		$("#rightDetil").css("left",treeW);
		$("#rightDetil").width(contentW - treeW);
		$("#rightDetil").height(contentH - 56);
		
		//价格表
		if($("#rightDetil .info").css("display") == "none")
			$("#rightDetil .form").height($("#rightDetil").height());
		else
			$("#rightDetil .form").height($("#rightDetil").height() - $("#rightDetil .info").outerHeight());
		$("#gridPrice").height($("#rightDetil .form").height() - 160);
		
		//对话框自动居中
		var dbody = $("#tips_dlg .dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", ($(document).height()-dbody.height())/2+"px");
		
		dbody = $("#Wait_dlg .dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", ($(document).height()-dbody.height())/2+"px");
		
		dbody = $("#uploadPrice_dlg .dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", ($(document).height()-dbody.height())/2+"px");
		
		dbody = $("#PriceHistory_dialog .dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", ($(document).height()-dbody.height())/2+"px");
	}
	
//定义方法
	$(function(){
	//当前选中的行
		var selectedRow = null;
	//最后搜索的条件参数
		var lastSearchParamA = null;
		var lastSearchParamB = null;
		
	//翻页功能回调函数
		//价格表
		page_ctrl_to_callback = function(pn){
			if (lastSearchParamA){
				lastSearchParamA.pn = pn;
				CallWaitdlg(true);
				$.ajax({
					url: "./qryDistributorPrice.do", 
					type: "post", 
					data: JSON.stringify(lastSearchParamA), 
					success: function(rsp){
						CallWaitdlg(false);
						if (rsp.errorCode!=0){
							showTips(rsp.msg);
						}else{
							clearGrid("Price_list");
							fillGrid(rsp.data);
							
							if (typeof(afterChangedPage)!=="undefined"){
								afterChangedPage(rsp.pageInfo);
							}
						}
					}, 
					error: function(){
						CallWaitdlg(false);
						showTips("请求出错了");
					}, 
					dataType: "json"
				});
			}	
		};
		//历史价格表
		page_ctrl_to_callback_detail = function(pn){
			if (lastSearchParamB){
				lastSearchParamB.pn = pn;
				CallWaitdlg(true);
				$.ajax({
					url: "./qryDistributorPriceLog.do", 
					type: "post", 
					data: JSON.stringify(lastSearchParamB), 
					success: function(rsp){
						CallWaitdlg(false);
						if (rsp.errorCode!=0){
							showTips(rsp.msg);
						}else{
							clearGrid("PriceHistory_List");
							fillHistoryGrid(rsp.data);		//填充详细表
							
							//翻页后的相应操作调用pageCtrl.js
							if (typeof(afterChangedPage_detail)!=="undefined"){
								afterChangedPage_detail(rsp.pageInfo);
							}
						}
					}, 
					error: function(){
						CallWaitdlg(false);
						showTips("请求出错了");
					},
					dataType: "json"
				});
			}
		};
		
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
					
					loginCustomerID = parseInt(allData.curLogin.CustomerID);
					currentCustomerID = loginCustomerID;
					currentNodeName = "当前分销商:" + loginCustomerName;
					loginCustomerLevel = parseInt(allData.curLogin.CustomerLevel);
					loginCustomerName = allData.curLogin.CustomerName;
					// if(console)
					// {
						// console.log("CID:" + loginCustomerID);
						// console.log("Level:" + loginCustomerLevel);
					// }
				}
				
				//初始化菜单栏
				if ("menu" in allData){
					setActiveMenu("资料管理", "分销商管理");		
				}
				
				//设置分页
				if (typeof(resetPageCtrl)!=="undefined")
					resetPageCtrl({rowCnt:0, page:0, psize:parseInt($("#pageSize").val())});
				
				if (typeof(resetPageCtrl_detail)!=="undefined")
					resetPageCtrl_detail({rowCnt:0, page:0, psize:parseInt($("#pageSize").val())});
			}
			//禁用操作按钮
			disenableOperatBtn();

			//初始化目录树
			refreshDistributors();
			zTree = $.fn.zTree.getZTreeObj("dtree");
		};
		
	//关闭ajax异步
		//$.ajaxSetup({async: false});
		
	//分销价格表
		//填充数据到表格
		function fillGrid(data){
			var ln = data.length;
			for(var i=0; i<ln; i++)
			{
				var tr = addGridRow();
				var ListItem = data[i];
				var inputs = tr.find("input");
				var sel = tr.find("select");
				
				//ID
				$(tr).attr("data",ListItem.ID);
				$(tr).attr("CustomerID",ListItem.CustomerID);//客户ID
				$(tr).attr("ParentID",ListItem.ParentID);//上级客户ID
				
				//客户ID
				// var field = $(inputs[inpCustomerID]);
				// field.attr("old",$.trim(ListItem.CustomerID));
				// field.val($.trim(ListItem.CustomerID));
				
				//上级客户ID
				// field = $(inputs[inpParentID]);
				// field.attr("old",$.trim(ListItem.ParentID));
				// field.val($.trim(ListItem.ParentID));
				
				//商品层级
				$(sel[selGoodsLevel]).attr("old", ListItem.GoodsLevel);
				sel[selGoodsLevel].selectedIndex = parseInt(ListItem.GoodsLevel);
				
				//商品关键字
				field = $(inputs[inpGoodsKey]);
				field.attr("old",$.trim(ListItem.GoodsKey));
				field.val($.trim(ListItem.GoodsKey));
				
				//设置类型
				$(sel[selSetType]).attr("old", ListItem.SetType);
				sel[selSetType].selectedIndex = parseInt(ListItem.SetType) + 1;
				
				//设置值
				field = $(inputs[inpValue]);
				field.attr("old",$.trim(ListItem.Value));
				field.val($.trim(ListItem.Value));
			}
		}
		
		//填充数据到表格(历史价格表)
		function fillHistoryGrid(data){
			var ln = data.length;
			for(var i=0; i<ln; i++)
			{
				var tr = addHistoryGridRow();
				var ListItem = data[i];
				var lb = tr.find("label");
				
				//ID
				//$(tr).attr("data",ListItem.ID);
				
				//客户ID
				// var field = $(lb[0]);
				// field.text($.trim(ListItem.CustomerID));
				
				//上级客户ID
				// field = $(lb[1]);
				// field.text($.trim(ListItem.ParentID));
				
				//商品层级
				field = $(lb[0]);
				switch (parseInt(ListItem.GoodsLevel))
				{
					case 1:
					  field.text("按品牌");
					  break
					case 2:
					  field.text("按品类");
					  break
					case 3:
					  field.text("按款");
					  break
					case 4:
					  field.text("按SKU");
					  break
				}
				
				//商品关键字
				field = $(lb[1]);
				field.text($.trim(ListItem.GoodsKey));
				
				//设置类型
				field = $(lb[2]);
				switch (parseInt(ListItem.SetType))
				{
					case 0:
					  field.text("折扣（%）");
					  break
					case 1:
					  field.text("一口价（元）");
					  break
				}
				
				//设置值
				field = $(lb[3]);
				field.text($.trim(ListItem.Value));
				
				//操作人
				field = $(lb[4]);
				field.text($.trim(ListItem.Operator));
				
				//操作类型
				field = $(lb[5]);
				switch (parseInt(ListItem.OperType))
				{
					case 1:
					  field.text("增加");
					  break
					case 2:
					  field.text("修改");
					  break
					case 3:
					  field.text("删除");
					  break
				}
				
				//操作时间
				field = $(lb[6]);
				field.text((1900+ListItem.OperTime.year)+"-"+(1+ListItem.OperTime.month)+"-"+(ListItem.OperTime.date) + " " + (ListItem.OperTime.hours) + ":" + (ListItem.OperTime.minutes) + ":" + (ListItem.OperTime.seconds)) ;
			}
		}
		
		//清除表格所有数据
		function clearGrid(formName){
			selectedRow = null;
			$("#" + formName + " tr:gt(0)").each(function(){
				$(this).remove();
			});
		}
		
		//还原修改前的数据
		function restoreGrid(){
			selectedRow = null;
			$("#Price_list tr:gt(0)").each(function(){
				var tr = $(this);
				var inputs = tr.find("input");
				var sel = tr.find("select");
				//取消高亮显示
				tr.removeClass("tr_high_light");
				
				//获取ID
				var id = tr.attr("data");
				if (typeof(id)==="undefined"){//没有ID，是新的数据，则删除
					tr.remove();
					return true;//继续each(下一个).
				}
				
				//客户ID
				// var field = $(inputs[inpCustomerID]);
				// field.val(field.attr("old"));
				
				//上级客户ID
				// field = $(inputs[inpParentID]);
				// field.val(field.attr("old"));
				
				//商品层级
				sel[selGoodsLevel].selectedIndex = parseInt($(sel[selGoodsLevel]).attr("old"));
				
				//商品关键字
				field = $(inputs[inpGoodsKey]);
				field.val(field.attr("old"));
				
				//设置类型
				sel[selSetType].selectedIndex = parseInt($(sel[selSetType]).attr("old")) + 1;
				
				//设置值
				field = $(inputs[inpValue]);
				field.val(field.attr("old"));
			});
		}
	
		//添加新行
		function addGridRow(){
			var txt = "<tr style=\"height:25px\"><td><input type=\"checkbox\" class=\"checkbox\"></td><td></td><td></td><td></td><td></td></tr>";
			$("#Price_list tbody").append(txt);
			var tr = $("#Price_list tr:last");
			var cells = tr.find("td");
			
			//客户ID
			//$(cells[idxCustomerID]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入客户ID\" style=\"width:100%\">");
			//上级客户ID
			//$(cells[idxParentID]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入上级客户ID\" style=\"width:100%\">");
			//商品层级
			$(cells[idxGoodsLevel]).html("<div class=\"select_wraper select_short\" style=\"width:100%\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value=\"-1\">请选择层级</option><option value=\"1\">按品牌</option><option value=\"2\">按品类</option><option value=\"3\">按款</option><option value=\"4\">按SKU</option></select></div>");
			//商品关键字
			$(cells[idxGoodsKey]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入商品关键字\" style=\"width:100%\">");
			//设置类型
			$(cells[idxSetType]).html("<div class=\"select_wraper select_short\" style=\"width:100%\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value=\"-1\">请选择类型</option><option value=\"0\">折扣(%)</option><option value=\"1\">一口价(元)</option></select></div>");
			//设置值
			$(cells[idxValue]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入设置值\" style=\"width:100%\">");
			
			//下拉菜单当前选中第一个选项
			var sel = tr.find("select");
			sel[selGoodsLevel].selectedIndex = 3;
			sel[selSetType].selectedIndex = 1;
			
			return tr;
		}
		
		//添加新行(历史价格表)
		function addHistoryGridRow(){
			var txt = "<tr style=\"height:25px\"><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td><td><label></label></td></tr>";
			$("#PriceHistory_List tbody").append(txt);
			var tr = $("#PriceHistory_List tr:last");
			return tr;
		}
		
		//获取修改过的列表数据
		function getModifiedData(){
			var data = {distributorPrices:[]};
			
			$("#Price_list tr:gt(0)").each(function(){
				var tr = $(this);
				var inputs = tr.find("input");
				var sel = tr.find("select");
				var item = {};
				var Modified = false;
				
				//ID
				var RowID = tr.attr("data");
				//此行data未定义则为新数据
				if (typeof(RowID)!=="undefined"){
					//修改
					item.ID = parseInt(RowID);
					item.CustomerID = tr.attr("CustomerID");//客户ID
					item.ParentID = tr.attr("ParentID")//上级客户ID
				}else{
					//添加
					item.ID = -1;
					item.CustomerID = currentCustomerID;//客户ID(当目录树节点为所有时为-1)
				}
				
				//客户ID
				// var field = $(inputs[inpCustomerID]);
				// var val = $.trim(field.val());
				// var old = field.attr("old");
				// if (val.length != 0){
					// item.CustomerID = parseInt(val);
				// }
					//数据被修改
					// if (typeof(old)!=="undefined"){
						// if (old!=val)	Modified = true;
					// }
				
				//上级客户ID(可无)
				// field = $(inputs[inpParentID]);
				// val = $.trim(field.val());
				// old = field.attr("old");
				// if (val.length != 0){
					// item.ParentID = parseInt(val);
				// }
					//数据被修改
					// if (typeof(old)!=="undefined"){
						// if (old!=val)	Modified = true;
					// }

				//商品层级
				var field;
				var old = $(sel[selGoodsLevel]).attr("old");
				var val = parseInt(sel[selGoodsLevel].selectedIndex);
				item.GoodsLevel = val;
					//未选择
					if (val <= 0){
						showTips("请选择商品层级!");
						data = false;
						return false;
					}
					//数据被修改
					if (typeof(old)!=="undefined"){
						if (old!=val)	Modified = true;
					}
				
				//商品关键字
				field = $(inputs[inpGoodsKey]);
				val = $.trim(field.val());
				old = field.attr("old");
				item.GoodsKey = val;
					//数据被修改
					if (typeof(old)!=="undefined"){
						if (old!=val)	Modified = true;
					}
				
				//设置类型
				old = $(sel[selSetType]).attr("old");
				val = parseInt(sel[selSetType].selectedIndex) - 1;
				item.SetType = val;
					//未选择
					if (val < 0){
						showTips("请选择设置类型!");
						data = false;
						return false;
					}
					//数据被修改
					if (typeof(old)!=="undefined"){
						if (old!=val)	Modified = true;
					}
				
				//设置值
				field = $(inputs[inpValue]);
				val = $.trim(field.val());
				old = field.attr("old");
				item.Value = val;
					if(!reNumber.test(val))
					{
						showTips("设置值请输入数字!");
						data = false;
						return false;
					}
					//数据被修改
					if (typeof(old)!=="undefined"){
						if (old!=val)	Modified = true;
					}

				//最终检查
				if (item.ID==-1 || Modified)
				{
					// if(console)
						// console.log(JSON.stringify(item));
					data.distributorPrices.push(item);
				}
			});
			
			return data;
		}
				
		//保存成功后的后续处理
		function afterSaveGrid(newID){
			selectedRow = null;
			var i = 0;
			$("#Price_list tr:gt(0)").each(function(){
				var tr = $(this);
				var inputs = tr.find("input");
				var sel = tr.find("select");
				
				//取消高亮显示
				tr.removeClass("tr_high_light");
				
				//ID
				var ID = tr.attr("data");
				if (typeof(ID)==="undefined"){//没有ID，是新的数据，则把返回的newid填回去
					tr.attr("data", newID[i++]);
				}
				
				//客户ID
				// var field = $(inputs[inpCustomerID]);
				// field.attr("old", $.trim(field.val()));
				
				//上级客户ID
				// field = $(inputs[inpParentID]);
				// field.attr("old", $.trim(field.val()));
				
				//商品层级
				$(sel[selGoodsLevel]).attr("old", parseInt(sel[selGoodsLevel].selectedIndex));
				
				//商品关键字
				field = $(inputs[inpGoodsKey]);
				field.attr("old", $.trim(field.val()));
				
				//设置类型
				$(sel[selSetType]).attr("old", parseInt(sel[selSetType].selectedIndex) - 1);
				
				//设置值
				field = $(inputs[inpValue]);
				field.attr("old", $.trim(field.val()));
				
			});
		}
		
		//全选
		$("#all_check").click(function(){
			var _this = this;
			var v = this.checked;
			
			$("#Price_list :checkbox").each(function(){
				if (this!==_this) this.checked = v;
			});
		});
			
		//禁止编辑表格
		function disableGrid(){
			$("#Price_list input").attr("disabled", "disabled");
			$("#Price_list select").attr("disabled", "disabled");
		}
		
		//允许编辑表格
		function enableGrid(){
			$("#Price_list input").removeAttr("disabled");
			$("#Price_list select").removeAttr("disabled");
		}
			
		//选择的行高亮显示
		$("#Price_list").on("click","tr:gt(0)",function(){
			if (selectedRow!==this){
				if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
				
				$(this).addClass("tr_high_light");
				selectedRow = this;
			}
		});
			
		//分销价格查询按钮
		$("#searchPrice").click(LoadDistributorPrice);
		
		//查询分销价格
		function LoadDistributorPrice(){
			//保存分销商,保存表格,删除表格项,要检查是否当前选中的客户id为null
			if(currentCustomerID == null || currentNodeName == "")
			{
				showTips("请先点击左侧目录树的其中一个分销商节点后再操作！");
				return;
			}
			//取参数
			var Level = parseInt($("#selectGoodsLevel").get(0).selectedIndex);
			var Type = parseInt($("#selectSetType").get(0).selectedIndex) - 1;
			var Key = $.trim($("#txtGoodsKey").val());
			lastSearchParamA = {};
			
			//填充参数
			lastSearchParamA.pn = 0;
			if(!reNumber.test($("#pageSize").val()))
				$("#pageSize").val("100");
			lastSearchParamA.pageSize = parseInt($("#pageSize").val());

			//CustomerID
			lastSearchParamA.CustomerID = parseInt(currentCustomerID);
			
			//GoodsLevel
			if(Level > 0)
			{
				lastSearchParamA.GoodsLevel = Level;
			}
			
			//SetType
			if(Type >= 0)
			{
				lastSearchParamA.SetType = Type;
			}
			
			//GoodsKey
			if(Key.length > 0)
			{
				lastSearchParamA.GoodsKey = Key;
			}
			
			//执行查询
			CallWaitdlg(true);
			$.ajax({
				url: "./qryDistributorPrice.do", 
				type: "post", 
				data: JSON.stringify(lastSearchParamA), 
				success: function(rsp){
					CallWaitdlg(false);
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						clearGrid("Price_list");
						fillGrid(rsp.data);
						
						if (typeof(resetPageCtrl)!="undefined"){
							resetPageCtrl(rsp.pageInfo);
						}
					}
				}, 
				error: function(){
					CallWaitdlg(false);
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
		
		//添加按钮
		$("#btnAdd").click(function(){
			//保存分销商,保存表格,删除表格项,要检查是否当前选中的客户id为null
			if(currentCustomerID == null || currentNodeName == "")
			{
				showTips("请先点击左侧目录树的其中一个分销商节点后再操作！");
				return;
			}
			if(currentCustomerID == loginCustomerID)
			{
				showTips("当前登录的分销商不能维护自己的分销价格表！");
				return;
			}
			if(loginCustomerLevel >= 2)
			{
				showTips("第三级分销商只能进行查询操作！");
				return;
			}
			
			addGridRow();
		});
		
		//保存按钮
		$("#btnSave").click(function(){
			//保存分销商,保存表格,删除表格项,要检查是否当前选中的客户id为null
			if(currentCustomerID == null || currentNodeName == "")
			{
				showTips("请先点击左侧目录树的其中一个分销商节点后再操作！");
				return;
			}
			if(currentCustomerID == loginCustomerID)
			{
				showTips("当前登录的分销商不能维护自己的分销价格表！");
				return;
			}
			if(loginCustomerLevel >= 2)
			{
				showTips("第三级分销商只能进行查询操作！");
				return;
			}
		
			var param = getModifiedData();
			if (param===false) return;
			else if (param.distributorPrices.length===0){
				showTips("没有修改过数据，不需要保存！");
				return;
			}
			
			disableGrid();//先禁止表格的编辑
			CallWaitdlg(true);
			$.ajax({
				url: "./saveDistributorPrice.do", 
				type: "post", 
				data: JSON.stringify(param), 
				success: function(rsp){
					CallWaitdlg(false);
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						afterSaveGrid(rsp.data);//保存成功后，按提交请求时的顺序返回所有新的loginID，旧的不需要
						showTips("保存数据成功");
					}

					enableGrid();//开放表格的编辑
				}, 
				error: function(){
					CallWaitdlg(false);
					showTips("请求出错了");
					enableGrid();
				},
				dataType: "json"
			});
		});
		
		//批量删除
		$("#btnDel").click(function(){
			//保存分销商,保存表格,删除表格项,要检查是否当前选中的客户id为null
			if(currentCustomerID == null || currentNodeName == "")
			{
				showTips("请先点击左侧目录树的其中一个分销商节点后再操作！");
				return;
			}
			if(currentCustomerID == loginCustomerID)
			{
				showTips("当前登录的分销商不能维护自己的分销价格表！");
				return;
			}
			if(loginCustomerLevel >= 2)
			{
				showTips("第三级分销商只能进行查询操作！");
				return;
			}
			
			//统计要删除的项数
			var counter = 0;
			$("#Price_list tr:gt(0)").each(function(){
				var chkbox = $(this).find(".checkbox");
				if(chkbox[0].checked)
				{
					counter++;
				}
			});
			
			//若有勾选则询问
			if(counter > 0)
			{
				if (!confirm("是否确定删除所选的项？"))
					return;
			}
			else
			{//没则提示
				showTips("请勾选至少一条要删除的项!");
				return;
			}
			
			counter = 0;
			var data = {distributorPrices:[]}
			$("#Price_list tr:gt(0)").each(function(){
				var chkbox = $(this).find(".checkbox");
				if(chkbox[0].checked)
				{
					var idnum = $(this).attr("data");
					if(typeof(idnum) !== "undefined")
					{
						var param = {ID:parseInt(idnum)};
						data.distributorPrices.push(param);
					}
					else
					{
						$(this).remove();
						counter++;
					}
				}
			});
			
			if(data.distributorPrices.length > 0)
			{
				CallWaitdlg(true);
				$.ajax({
					url: "./saveDistributorPrice.do", 
					type: "post", 
					data: JSON.stringify(data), 
					success: function(rsp){
						CallWaitdlg(false);
						if (rsp.errorCode!=0){
							showTips(rsp.msg);

						}else{
							showTips("操作成功!");
							LoadDistributorPrice();
						}
					}, 
					error: function(){
						CallWaitdlg(false);
						showTips("请求出错了");
					}, 
					dataType: "json"
				});
			}
			else if(counter > 0)
			{
				showTips("操作成功!");
			}
			else
			{
				showTips("操作失败,请重试!");
			}
		});
		
		//取消按钮
		$("#btnCancel").click(function(){
			//保存分销商,保存表格,删除表格项,要检查是否当前选中的客户id为null
			if(currentCustomerID == null || currentNodeName == "")
			{
				showTips("请先点击左侧目录树的其中一个分销商节点后再操作！");
				return;
			}
			if(currentCustomerID == loginCustomerID)
			{
				showTips("当前登录的分销商不能维护自己的分销价格表！");
				return;
			}
			if(loginCustomerLevel >= 2)
			{
				showTips("第三级分销商只能进行查询操作！");
				return;
			}
			if (!confirm("是否确定取消所做的修改？")){
				return;
			}
			restoreGrid();
		});
		
	//分销价格历史表
		//查询按钮
		$("#SearchHistoryPrice").click(function(){
			//保存分销商,保存表格,删除表格项,要检查是否当前选中的客户id为null
			if(currentCustomerID == null || currentNodeName == "")
			{
				showTips("请先点击左侧目录树的其中一个分销商节点后再操作！");
				return;
			}
			//取参数
			var Level = parseInt($("#HistorySelectGoodsLevel").get(0).selectedIndex);
			var Type = parseInt($("#HistorySelectSetType").get(0).selectedIndex) - 1;
			var Key = $.trim($("#HistoryTxtGoodsKey").val());
			lastSearchParamB = {};
			
			//填充参数
			lastSearchParamB.pn = 0;
			if(!reNumber.test($("#HistoryPageSize").val()))
				$("#HistoryPageSize").val("100");
			lastSearchParamB.pageSize = parseInt($("#HistoryPageSize").val());
			
			//CustomerID
			lastSearchParamB.CustomerID = parseInt(currentCustomerID);
			
			//GoodsLevel
			if(Level > 0)
			{
				lastSearchParamB.GoodsLevel = Level;
			}
			
			//SetType
			if(Type >= 0)
			{
				lastSearchParamB.SetType = Type;
			}
			
			//GoodsKey
			if(Key.length > 0)
			{
				lastSearchParamB.GoodsKey = Key;
			}
			
			//执行查询
			CallWaitdlg(true);
			$.ajax({
				url: "./qryDistributorPriceLog.do", 
				type: "post", 
				data: JSON.stringify(lastSearchParamB), 
				success: function(rsp){
					CallWaitdlg(false);
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						//清空表格
						clearGrid("PriceHistory_List");
						//填充数据
						fillHistoryGrid(rsp.data);
						
						if (typeof(resetPageCtrl_detail)!="undefined"){
							resetPageCtrl_detail(rsp.pageInfo);
						}
					}
				}, 
				error: function(){
					CallWaitdlg(false);
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		});
		
		
		
	//分销商目录树
		//目录树初始数据
		var zNodes =[];

		//设置
		var setting = {
			view: {
				dblClickExpand: true
			},
			check: {
				enable: true
			},
			callback: {
				onRightClick: OnRightClick,
				onClick: onClick
			}
		};
		
		//单击节点显示相关信息
		function onClick(event,treeId, treeNode) {
			//var zTree = $.fn.zTree.getZTreeObj("dtree");
			//zTree.expandNode(treeNode, null, null, null, true);
			UpdateUI();
		}
		
		//目录树与详细信息(信息+表格)联动
		function UpdateUI(){
			zTree = $.fn.zTree.getZTreeObj("dtree");
			//获取当前选中的节点
			nodes = zTree.getSelectedNodes();
			
			//当前有选择节点
			if(nodes.length > 0)
			{
				//清空表格
				clearGrid("Price_list");
				clearGrid("PriceHistory_List");
				//重置分页
				if (typeof(resetPageCtrl)!=="undefined")
					resetPageCtrl({rowCnt:0, page:0, psize:parseInt($("#pageSize").val())});
				if (typeof(resetPageCtrl_detail)!=="undefined")
					resetPageCtrl_detail({rowCnt:0, page:0, psize:parseInt($("#pageSize").val())});
				//当前选中的客户ID
				currentCustomerID = nodes[0].CID;
				currentNodeName = nodes[0].name;
				
				//当前登录的分销商level>=2,不允许拥有,增加下级,不允许编辑下级和自己(即只能查询不能编辑)
				if(loginCustomerLevel >= 2)
				{
					disenableOperatBtn();
					$("#rightDetil .info").hide();
					//界面调整
					windowResize();
					return;
				}
				else
				{
					$("#rightDetil .info").show();
				}
				
				if(nodes[0].CID >0 && nodes[0].CID != loginCustomerID)
				{//下级分销商信息
					//与右边详细信息联动
					$("#txtName").val(nodes[0].name);
					$("#txtLinkMan").val(nodes[0].LinkMan);
					$("#txtLinkTele").val(nodes[0].LinkTele);
					$("#txtMobileNo").val(nodes[0].MobileNo);
					$("#txtState").val(nodes[0].State);
					$("#txtCity").val(nodes[0].City);
					$("#txtDistrict").val(nodes[0].District);
					$("#txtAddress").val(nodes[0].Address);
					$("#txtMemo").val(nodes[0].Note);
					$("#btn_Save").attr("type","save");
					$("#btn_Save").html("保存");
					//直属下级才可编辑
					if(nodes[0].ParentID == loginCustomerID)
						enableOperatBtn();
					else
						disenableOperatBtn();
				}
				else
				{
					//选择了所有分销商
					if(nodes[0].CID == -1)
					{
						clearInfo();
						$("#rightDetil .info").hide();
						$("#btn_Save").attr("type","null");		//所有分销商模式下不能增加分销商
						enableOperatBtn();
						$("#infotoolbar").hide();	//只可以编辑表格
					}
					else
					{//当前登录的分销商
						clearInfo();
						$("#btn_Save").attr("type","add");		//选择这个节点时可以增加,不可以编辑表格
						$("#btn_Save").html("增加");
						enableOperatBtn();
						$("#formtoolbar").hide();	//增加分销商时无法编辑下方表格
					}
				}
				//界面调整
				windowResize();
			}
		}
		
		//清空基本信息文本框
		function clearInfo(){
			$("#txtName").val("");
			$("#txtLinkMan").val("");
			$("#txtLinkTele").val("");
			$("#txtMobileNo").val("");
			$("#txtState").val("");
			$("#txtCity").val("");
			$("#txtDistrict").val("");
			$("#txtAddress").val("");
			$("#txtMemo").val("");
		}
		
		//允许操作(基本信息及表格)
		function enableOperatBtn(){
			$("#infotoolbar").show();
			$("#formtoolbar").show();
		}
		
		//禁用操作(基本信息及表格)
		function disenableOperatBtn(){
			$("#infotoolbar").hide();
			$("#formtoolbar").hide();
		}

		//右键菜单回调函数
		function OnRightClick(event, treeId, treeNode) {
			if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
				zTree.cancelSelectedNode();
				currentCustomerID = null;
				currentNodeName = "";
				disenableOperatBtn();
				clearGrid("Price_list");
				clearGrid("PriceHistory_List");
			} else if (treeNode && !treeNode.noR && (treeNode.ParentID == loginCustomerID || treeNode.CID == loginCustomerID)) {
				if(loginCustomerLevel >= 2)
				{//第三级分销商不显示菜单
					showRMenu("root", event.clientX, event.clientY);	//只显示刷新按钮的菜单
					return;
				}
				
				enableOperatBtn();
				zTree.selectNode(treeNode);
				if(treeNode.CID == loginCustomerID)
				{
					showRMenu("root", event.clientX, event.clientY);
				}
				else
				{
					if(treeNode.NodeEnable == 0)
						showRMenu("disablenode", event.clientX, event.clientY);
					else
						showRMenu("enablenode", event.clientX, event.clientY);
				}
			}
		}
		
		//显示菜单
		function showRMenu(type, x, y) {
			//根节点
			if (type=="root") {
				$("#m_disable").hide();
				$("#m_enable").hide();
				$("#m_refresh").show();
			}
			//下级分销商节点(不可用节点)
			if(type == "disablenode") {
				$("#m_disable").hide();
				$("#m_enable").show();
				$("#m_refresh").show();
			}
			//下级分销商节点(可用节点)
			if(type == "enablenode") {
				$("#m_disable").show();
				$("#m_enable").hide();
				$("#m_refresh").show();
			}
			
			//刷新ui
			UpdateUI();
			
			rMenu.css({"top":(y - $(".top_bar").height() - $(".sub_menu").height()) + "px", "left":x+"px"});		//显示的位置 , "visibility":"visible"
			
			$("#rMenu").fadeIn(100);
			
			$("body").bind("mousedown", onBodyMouseDown);
		}
		
		//隐藏菜单
		function hideRMenu() {
			if (rMenu) $("#rMenu").hide();
			//if (rMenu) rMenu.css({"visibility": "hidden"});
			$("body").unbind("mousedown", onBodyMouseDown);
		}
		
		//非目录树节点鼠标单击右键时不显示菜单
		function onBodyMouseDown(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				//rMenu.css({"visibility" : "hidden"});
				$("#rMenu").hide();
			}
		}
		
		//添加新节点
		function addTreeNode(parentNode,data) {
			hideRMenu();
			zTree.addNodes(parentNode, data);
		}
		
		//捆绑数据到目录树
		function bindTree(data) {
			hideRMenu();
			$.fn.zTree.init($("#dtree"), setting, data);
		}
		
	//分销商操作	
		//增加分销商
		function addDistributors() {
			//只能在当前登录的分销商节点增加分销商
			if(currentCustomerID != loginCustomerID)
			{
				showTips("当前节点不能增加分销商！");
				return;
			}
			if(loginCustomerLevel >= 2)
			{
				showTips("第三级分销商只能进行查询操作！");
				return;
			}
			
			//准备数据
			var param = {"distributors":[]};
			var item = {};
			item.CustomerID = -1;
			item.Name = $("#txtName").val();
			item.State = $("#txtState").val();
			item.City = $("#txtCity").val();
			item.District = $("#txtDistrict").val();
			item.Address = $("#txtAddress").val();
			item.LinkMan = $("#txtLinkMan").val();
			item.LinkTele = $("#txtLinkTele").val();
			item.MobileNo = $("#txtMobileNo").val();
			item.Note = $("#txtMemo").val();
			param.distributors.push(item);
			
			//提交数据
			CallWaitdlg(true);
			$.ajax({
				url: "./saveDistributor.do", 
				type: "post", 
				data: JSON.stringify(param), 
				success: function(rsp){
					CallWaitdlg(false);
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						//获取返回的ID
						var newid = rsp.data[0];
						//把当前定位信息改为新分销商节点
						currentNodeName = item.Name;
						currentCustomerID = parseInt(newid);
						//刷新目录树
						refreshDistributors();
						
						showTips("操作成功!");
					}
				}, 
				error: function(){
					CallWaitdlg(false);
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
		
		//保存&增加分销商
		$("#btn_Save").click(function(){
			if($("#btn_Save").attr("type") == "add")
				addDistributors();
			
			if($("#btn_Save").attr("type") == "save")
				editDistributors();
		});
		
		//取消按钮
		$("#btn_Cancel").click(function(){
			if($("#btn_Save").attr("type") == "add")
				clearInfo();
			
			if($("#btn_Save").attr("type") == "save")
				UpdateUI();
		});
		
		//修改分销商
		function editDistributors() {
			//保存分销商,保存表格,删除表格项,要检查是否当前选中的客户id为null
			if(currentCustomerID == null || currentNodeName == "")
			{
				showTips("请先点击左侧目录树的其中一个分销商节点后再操作！");
				return;
			}
			if(currentCustomerID == loginCustomerID)
			{
				showTips("当前登录的分销商不能修改自己的信息！");
				return;
			}
			if(loginCustomerLevel >= 2)
			{
				showTips("第三级分销商只能进行查询操作！");
				return;
			}
			
			//准备数据
			var param = {"distributors":[]};
			var item = {};
			item.CustomerID = currentCustomerID;
			item.Name = $("#txtName").val();
			item.State = $("#txtState").val();
			item.City = $("#txtCity").val();
			item.District = $("#txtDistrict").val();
			item.Address = $("#txtAddress").val();
			item.LinkMan = $("#txtLinkMan").val();
			item.LinkTele = $("#txtLinkTele").val();
			item.MobileNo = $("#txtMobileNo").val();
			item.Note = $("#txtMemo").val();
			param.distributors.push(item);
			
			//提交数据
			CallWaitdlg(true);
			$.ajax({
				url: "./saveDistributor.do", 
				type: "post", 
				data: JSON.stringify(param), 
				success: function(rsp){
					CallWaitdlg(false);
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						currentNodeName = item.Name;
						//刷新目录树
						refreshDistributors();
						showTips("操作成功!");
					}
				}, 
				error: function(){
					CallWaitdlg(false);
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
		
		//作废&恢复分销商
		function setDistributorEnable(setting){
			zTree = $.fn.zTree.getZTreeObj("dtree");
			//获取当前选中的节点
			nodes = zTree.getSelectedNodes();
			
			if(nodes.length > 0)
			{
				var param = {"distributors":[], "Enable":null};
				param.distributors.push(nodes[0].CID);
				param.Enable = setting;
				
				//提交数据
				CallWaitdlg(true);
				$.ajax({
					url: "./setDistributorEnable.do", 
					type: "post", 
					data: JSON.stringify(param), 
					success: function(rsp){
						CallWaitdlg(false);
						if (rsp.errorCode!=0){
							showTips(rsp.msg);
						}else{
							//showTips("操作成功!");
							alert("操作成功!");
							//刷新目录树
							refreshDistributors();
						}
					}, 
					error: function(){
						CallWaitdlg(false);
						showTips("请求出错了");
					}, 
					dataType: "json"
				});
			}
		}
		//作废分销商菜单按钮
		$("#m_disable").click(function(){setDistributorEnable(0);});
		//恢复分销商菜单按钮
		$("#m_enable").click(function(){setDistributorEnable(1);});
		
		//重新载入目录树
		function refreshDistributors(){
			var tmpdata = null;
			
			DistributorData = []
			//请求数据
			CallWaitdlg(true);
			$.ajax({
				url: "./qrySubDistributor.do", 
				type: "post", 
				data: "{}", 
				success: function(rsp){
					CallWaitdlg(false);
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						tmpdata = rsp.data;
					}
				}, 
				error: function(){
					CallWaitdlg(false);
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
			
			//生成目录树绑定数据
			//所有分销商
			DistributorData.push({name:"所有分销商", noR:true, CID:-1});
			//当前分销商
			DistributorData.push({name:"当前分销商:" + loginCustomerName, CID:loginCustomerID, LevelID:loginCustomerLevel, open:true, children:[]});
			
			if(tmpdata != null)
			{
				if(tmpdata.length > 0)
				{
					//下级分销商
					var currentData = DistributorData[1];
					
					for(var i=0;i<tmpdata.length;i++)
					{
						//等待被加入的节点数据
						var item = tmpdata[i];
						var tmpobj = {
								CID:item.CustomerID,
								ParentID:item.ParentID,
								name:item.Name,
								ParentName:item.ParentName,
								State:item.State,
								City:item.City,
								District:item.District,
								Address:item.Address,
								LinkMan:item.LinkMan,
								LinkTele:item.LinkTele,
								MobileNo:item.MobileNo,
								Note:item.Note,
								NodeEnable:item.Enable,
								LevelID:item.LevelID,
								open:false
							}
						
						if(item.Enable != 1)	//被作废时显示作废图标
							tmpobj.icon = "./css/zTreeStyle/img/tree_Disable.png";
						
						if(loginCustomerID == item.ParentID)
						{//当前节点直接属于当前分销商
							currentData.children.push(tmpobj);
						}
						else
						{
							//找到节点的父节点
							var Parentobj = GetCIDobj(currentData,item.ParentID);
							if(Parentobj != null)
							{
								if(typeof(Parentobj.children) == "undefined")
								{
									Parentobj.children = [];
								}
								Parentobj.children.push(tmpobj);
							}
						}
					}
					DistributorData[1] = currentData;
				}
			}
			
			//调试输出转化后的结果
			//console.log(JSON.stringify({DistributorData}));
			
			//开始绑定数据
			bindTree(DistributorData);
			
			//定位回原来的节点
			if(currentNodeName != "")
			{
				if(searchNode(currentNodeName,false) == false)
				{//原来的节点找不到时,默认选择二个节点(当前分销商节点)
					zTree = $.fn.zTree.getZTreeObj("dtree");
					var nodes = zTree.getNodes();
					zTree.selectNode(nodes[1]);
					UpdateUI();
				}
			}
		}
		
		//找到目录树数据对象中的指定客户ID的对象
		function GetCIDobj(obj,CID){
			var returnObj = null;
			var finsh = false;
			for ( var p in obj )
			{
				if(typeof(p) != "function")
				{
					if(obj[p] instanceof Array && p == "children")
					{//当前是否为children数组
						var arr = obj[p];	//获取这个数组
						for(var c=0;c<arr.length;c++)
						{
							var childrenobj = arr[c];
							var resultobj = GetCIDobj(childrenobj,CID);
							if(resultobj != null)
							{
								returnObj = resultobj;
								finsh = true;
								break;
							}
						}
					}
					else
					{//当前属性是否为cid且是否符合条件
						if(p == "CID")
						{//当前对象的cid是否符合条件
							if(parseInt(obj[p]) == CID)
							{
								returnObj = obj;
								break;
							}
						}
					}
					
				}
				if(finsh)
					break;
			}
			return returnObj;
		}
		
		//搜索定位分销商
		function searchNode(nodeName,msgswitch){
			zTree = $.fn.zTree.getZTreeObj("dtree");
			var nodes = zTree.getNodesByParam("name", nodeName, null)
			if(nodes.length > 0)
			{
				zTree.selectNode(nodes[0]);
				UpdateUI();
				if(msgswitch == true)
					alert("查找完毕!");
				return true;
			}
			else
			{
				if(msgswitch == true)
					alert("没有查找到该分销商");
				return false;
			}
		}
		
		//查询上一个分销商
		$("#pre_node").click(function(){
			zTree = $.fn.zTree.getZTreeObj("dtree");
			var strkey = $("#txtkeyword").val();
			if(strkey.length > 0)
			{
				if(currkeytxt != strkey || resultNodes == null)
				{
					resultNodes = zTree.getNodesByParamFuzzy("name", strkey, null);
					currkeytxt = strkey;
					if(resultNodes.length  > 0)
					{
						alert("已找到:" + resultNodes.length + "个结果,请按\"上一个\"或\"下一个\"检视搜索结果!");
						currResultIndex = 0;
						zTree.selectNode(resultNodes[currResultIndex]);
						UpdateUI();
					}
					else
					{
						currkeytxt = "";
						resultNodes = null;
						currResultIndex = 0;
						alert("找不到相关分销商!");
					}
				}
				else
				{
					if(currResultIndex - 1 < 0)
					{
						alert("搜索结果已到顶,请按下一个检视!");
					}
					else
					{
						currResultIndex--;
						zTree.selectNode(resultNodes[currResultIndex]);
						UpdateUI();
					}
				}
			}
			else
				showTips("请输入关键字!");
		});
		
		//查询下一个分销商
		$("#next_node").click(function(){
			zTree = $.fn.zTree.getZTreeObj("dtree");
			var strkey = $("#txtkeyword").val();
			if(strkey.length > 0)
			{
				if(currkeytxt != strkey || resultNodes == null)
				{
					resultNodes = zTree.getNodesByParamFuzzy("name", strkey, null);
					currkeytxt = strkey;
					if(resultNodes.length  > 0)
					{
						alert("已找到:" + resultNodes.length + "个结果,请按\"上一个\"或\"下一个\"检视搜索结果!");
						currResultIndex = 0;
						zTree.selectNode(resultNodes[currResultIndex]);
						UpdateUI();
					}
					else
					{
						currkeytxt = "";
						resultNodes = null;
						currResultIndex = 0;
						alert("找不到相关分销商!");
					}
				}
				else
				{
					if(currResultIndex + 1 > resultNodes.length - 1)
					{
						alert("搜索结果已到底,请按上一个检视!");
					}
					else
					{
						currResultIndex++;
						zTree.selectNode(resultNodes[currResultIndex]);
						UpdateUI();
					}
				}
			}
			else
				showTips("请输入关键字!");
		});
		
		//刷新目录树
		$("#m_refresh").click(function(){
			refreshDistributors();
		});
		
	//查看分销价格历史
		$("#btnHistroy").click(function(){
			//保存分销商,保存表格,删除表格项,要检查是否当前选中的客户id为null
			if(currentCustomerID == null || currentNodeName == "")
			{
				showTips("请先点击左侧目录树的其中一个分销商节点后再操作！");
				return;
			}
			$("#PriceHistory_dialog").fadeIn(100);
		});
		
	//导入分销价格
		//弹出上传窗口
		$("#btnUploadPrice").click(function(){
			$("#uploadPrice_dlg").fadeIn(100);
		});
	
		//上传
		$("#btnUpload").click(function(){
			CallWaitdlg(true);
			var _this = this;
			
			var file = $("#file_for_import").val();
			if (file.length>0){
				$("#uploadForm").ajaxSubmit({
					url : "./importDistributorPrice.do",
					secureuri : false,
					fileElementId : 'file_for_import',
					dataType : 'json',
					async : true,
					success : function(data) {
						CallWaitdlg(false);
						
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
						CallWaitdlg(false);
						showTips(msg);
					}
				});
			}else{
				CallWaitdlg(false);
				showTips("请选择一个文件");
			}
		});
	
	//导出分销商
		$("#btnDownloadDistributors").click(function(){
			CallWaitdlg(true);
			$.ajax({
					type : "POST",
					url : "./exportDistributor.do",
					dataType:'json',
					contentType:"application/json;charset=UTF-8",
					data:{},
					async : true,
					success : function(data) {
						if (data.errorCode==0)
						{
							CallWaitdlg(false);
							//window.location.href=data.data;
							window.open(data.data);
						}else{
							CallWaitdlg(false);
							showTips(data.msg);
						}										
					},error:function(data){
						CallWaitdlg(false);
						showTips("导出错误!" );
					}
				});
		});
	
	//导出分销价格
		$("#btnDownloadPrice").click(function(){
			CallWaitdlg(true);
			$.ajax({
					type : "POST",
					url : "./exportDistributorPrice.do",
					dataType:'json',
					contentType:"application/json;charset=UTF-8",
					data:{},
					async : true,
					success : function(data) {
						if (data.errorCode==0)
						{
							CallWaitdlg(false);
							//window.location.href=data.data;
							window.open(data.data);
						}else{
							CallWaitdlg(false);
							showTips(data.msg);
						}										
					},error:function(data){
						CallWaitdlg(false);
						showTips("导出错误!" );
					}
				});
		});
	
	//关闭窗口
		//关闭按钮
		$(".btn_dlgclose").click(function(){
			var dialog = $(this).parents(".dialog");
			$(dialog).fadeOut(100);
		});
		//右上角关闭按钮
		$(".dialog_close").click(function(){
			var dialog = $(this).parents(".dialog");
			$(dialog).fadeOut(100);
		});
		
	//执行初始化
		init();
	});