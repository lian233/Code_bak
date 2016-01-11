 //allData变量声明
 //用于存储返回的初始化数据
	if (typeof allData === "undefined") allData = {};
	
	var loginCustomerID = null;
	var loginCustomerLevel = null;
	var loginCustomerName = "";
	var productLineList = null;
	var reNumber = /^[0-9]+.?[0-9]*$/; 
	var dirpath = "./datapackage/";

//字段索引定义
	var idxDPTitle = 1;
	var idxPLine = 2;
	var idxDPType = 3;
	var idxDPMemo = 4;
	var idxFileName = 5;
	var idxUpTime = 6;
	var idxOperator = 7;
	var idxDownLoad = 8;
	
	var inpDPTitle = 1;
	var selPLine = 0;
	var selDPType = 1;
	var inpDPMemo = 2;
	var lbFileName = 0;
	var lbUpTime = 1;
	var lbOperator = 2;
	var btnDownLoad = 0;

//弹出提示框(ok)
	function showTips(text){
		var dlg = $("#tips_dlg");
		dlg.fadeIn(100);
		windowResize();
		$("#tips_dlg .tips").html(text);
	}

//控制等待对话框(ok)
	function CallWaitdlg(Display,text){
		if(typeof(text) != "undefined" && text != "")
			$("#Wait_dlg .dialog_caption").html(text);
		else
			$("#Wait_dlg .dialog_caption").html("正在处理请求,请稍候");
		
		if(Display == true)
		{
			$("#Wait_dlg").fadeIn(50);
			windowResize();
		}
		else
			$("#Wait_dlg").fadeOut(50);
	}
	
//界面初始化(ok)
	$(document).ready(function(){
		//自动调整页面
		$(window).resize(windowResize);
		windowResize();
	});
		
//调整界面(ok)
	function windowResize(){ 
		var contentW = $("#Content").width();
		var contentH = $("#Content").height();
		var queryBarH = $("#query_bar").outerHeight(true);		//目前55px

		//对话框自动居中
		var dbody = $("#tips_dlg .dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", ($(document).height()-dbody.height())/2+"px");
		
		dbody = $("#Wait_dlg .dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", ($(document).height()-dbody.height())/2+"px");
		
		dbody = $("#uploadDataPackage_dlg .dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", ($(document).height()-dbody.height())/2+"px");
	}
	
//定义方法
	$(function(){
	//当前选中的行
		var selectedRow = null;
	//最后搜索的条件参数
		var lastSearchParam = null;
	//开启ajax异步
		$.ajaxSetup({async: true});
		
	//翻页功能回调函数(ok)
		page_ctrl_to_callback = function(pn){
			if (lastSearchParam){
				lastSearchParam.pn = pn;
				CallWaitdlg(true);
				$.ajax({
					url: "./qryDataPackageList.do", 
					type: "post", 
					data: JSON.stringify(lastSearchParam), 
					success: function(rsp){
						CallWaitdlg(false);
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
						CallWaitdlg(false);
						showTips("请求出错了");
					}, 
					dataType: "json"
				});
			}	
		};
		
	//初始化数据(ok)
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
					loginCustomerLevel = parseInt(allData.curLogin.CustomerLevel);
					loginCustomerName = allData.curLogin.CustomerName;
				}
				
				//初始化菜单栏
				if ("menu" in allData){
					setActiveMenu("资料管理", "数据包管理");		
				}
				
				//载入产品线列表
				if ("productLine" in allData){
					productLineList = allData.productLine;
					var qrybarselectlist = $("#selProductLine");
					var uploadselectlist = $("#packageProductLineID");
					var ln = productLineList.length;
					for(var i=0; i<ln; i++){
						$("<option value='"+productLineList[i].ID+"'>"+$.trim(productLineList[i].Name)+"</option>").appendTo(qrybarselectlist);
						$("<option value='"+productLineList[i].ID+"'>"+$.trim(productLineList[i].Name)+"</option>").appendTo(uploadselectlist);
					}
				}
				
				//设置分页
				if (typeof(resetPageCtrl)!=="undefined")
					resetPageCtrl({rowCnt:0, page:0, psize:parseInt($("#pageSize").val())});
				
				//按情况隐藏操作栏
				hideCustomer();
			}
		};
		
	//根据当前登录用户ID隐藏操作栏(ok)
		function hideCustomer(){
			if("curLogin" in allData)
				if (allData.curLogin.CustomerID > 0)
				{
					var foot = $(".foot");
					foot.remove();
					disableGrid();
				}
		}
	
	//数据包列表
		//填充数据到表格(ok)
		function fillGrid(data){
			var ln = data.length;
			for(var i=0; i<ln; i++)
			{
				var tr = addGridRow();
				var ListItem = data[i];
				var inputs = tr.find("input");
				var sel = tr.find("select");
				var lbs = tr.find("label");
				var downloadbtn = tr.find(".button");
				
				//ID
				$(tr).attr("data",ListItem.ID);
				
				//数据包标题
				var field = $(inputs[inpDPTitle]);
				field.attr("old",$.trim(ListItem.title));
				field.val($.trim(ListItem.title));
				
				//产品线ID
				var plid = parseInt(ListItem.productLineID)
				if(plid <= 0) plid = -1;
				$(sel[selPLine]).attr("old", plid);
				$(sel[selPLine]).get(0).value = plid;
				
				//类型
				$(sel[selDPType]).attr("old", ListItem.dataType);
				$(sel[selDPType]).get(0).value = parseInt(ListItem.dataType);
				
				//备注
				field = $(inputs[inpDPMemo]);
				field.attr("old",$.trim(ListItem.note));
				field.val($.trim(ListItem.note));
				
				//文件名
				field = $(lbs[lbFileName]);
				field.html($.trim(ListItem.fileName));
				
				//上传时间
				field = $(lbs[lbUpTime]);
				field.html((1900+ListItem.uploadTime.year)+"-"+(1+ListItem.uploadTime.month)+"-"+(ListItem.uploadTime.date) + " " + (ListItem.uploadTime.hours) + ":" + (ListItem.uploadTime.minutes) + ":" + (ListItem.uploadTime.seconds));
				
				//操作员
				field = $(lbs[lbOperator]);
				field.html($.trim(ListItem.operator));
				
				//下载
				$(downloadbtn[0]).attr("data",dirpath + ListItem.fileName);
				$(downloadbtn[0]).click(function(){window.open($(this).attr("data"));});
				
				
			}
		}
		
		//清除表格所有数据(ok)
		function clearGrid(){
			selectedRow = null;
			$("#DataPackage_List tr:gt(0)").each(function(){
				$(this).remove();
			});
		}
		
		//还原修改前的数据(ok)
		function restoreGrid(){
			selectedRow = null;
			$("#DataPackage_List tr:gt(0)").each(function(){
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
				
				//数据包标题
				var field = $(inputs[inpDPTitle]);
				field.val(field.attr("old"));
				
				//产品线ID
				$(sel[selPLine]).get(0).value = parseInt($(sel[selPLine]).attr("old"));
				
				//类型
				$(sel[selDPType]).get(0).value = parseInt($(sel[selDPType]).attr("old"));
				
				//备注
				field = $(inputs[inpDPMemo]);
				field.val(field.attr("old"));
			});
		}
	
		//添加新行(ok)
		function addGridRow(){
			var txt = "<tr style=\"height:25px\"><td><input type=\"checkbox\" class=\"checkbox\"></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
			$("#DataPackage_List tbody").append(txt);
			var tr = $("#DataPackage_List tr:last");
			var cells = tr.find("td");
			
			//数据包标题
			$(cells[idxDPTitle]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入数据包标题\" style=\"width:100%\">");
			//产品线ID
			$(cells[idxPLine]).html("<div class=\"select_wraper select_short\" style=\"width:100%\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value=\"-1\">全部产品线</option></select></div>");
			//类型
			$(cells[idxDPType]).html("<div class=\"select_wraper select_short\" style=\"width:100%\"><div class=\"select_offset_right\"></div><div class=\"select_arrow\"></div><select class=\"select_body\"><option value=\"-1\">请选择</option><option value=\"0\">商品</option><option value=\"1\">推广</option></select></div>");
			//备注
			$(cells[idxDPMemo]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入备注\" style=\"width:100%\">");
			//文件名
			$(cells[idxFileName]).html("<label></label>");
			//上传时间
			$(cells[idxUpTime]).html("<label></label>");
			//操作员
			$(cells[idxOperator]).html("<label></label>");
			//下载
			$(cells[idxDownLoad]).html("<div class=\"button\">下载文件</div>");
			
			//载入产品线列表
			if ("productLine" in allData){
				productLineList = allData.productLine;
				var selectlist = $($(cells[idxPLine]).find("select"));
				var ln = productLineList.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+productLineList[i].ID+"'>"+$.trim(productLineList[i].Name)+"</option>").appendTo(selectlist);
				}
			}
			
			//下拉菜单当前选中第一个选项
			var sel = tr.find("select");
			$(sel[selPLine]).selectedIndex = 0;
			$(sel[selDPType]).selectedIndex = 0;
			
			hideCustomer();
			
			return tr;
		}
		
		//获取修改过的列表数据(ok)
		function getModifiedData(){
			var data = {DataPackages:[]};
			
			$("#DataPackage_List tr:gt(0)").each(function(){
				var tr = $(this);
				var inputs = tr.find("input");
				var sel = tr.find("select");
				var item = {};
				var Modified = false;
				
				//ID
				var PackageID = tr.attr("data");
				//此行data未定义则为新数据
				if (typeof(PackageID)!=="undefined"){
					//修改
					item.ID = parseInt(PackageID);
				}else{
					//未定义则忽略
					return true;//继续each(下一个).
				}
				
				//数据包标题
				var field;
				field = $(inputs[inpDPTitle]);
				val = $.trim(field.val());
				old = field.attr("old");
				//标题不能不填
				if($.trim(val) == "")
				{
					showTips("请填写数据包标题!");
					data = false;
					return false;
				}
					//确认数据被修改
					if (typeof(old)!=="undefined"){
						if (old!=val)
						{
							item.Title = val;
							Modified = true;
						}
					}
				
				//产品线ID
				var old = $(sel[selPLine]).attr("old");
				var val = parseInt($(sel[selPLine]).get(0).value);
					//确认数据被修改
					if (typeof(old)!=="undefined"){
						if (old!=val)
						{
							item.ProductLineID = val;
							Modified = true;
						}
					}
				
				//类型
				old = $(sel[selDPType]).attr("old");
				val = parseInt($(sel[selDPType]).get(0).value);
				//未选择
				if (val < 0){
					showTips("请选择数据包类型!");
					data = false;
					return false;
				}
					//数据被修改
					if (typeof(old)!=="undefined"){
						if (old!=val)
						{
							item.DataType = val;
							Modified = true;
						}
					}
				
				//备注
				field = $(inputs[inpDPMemo]);
				val = $.trim(field.val());
				old = field.attr("old");
					//数据被修改
					if (typeof(old)!=="undefined"){
						if (old!=val)
						{
							item.Note = val;
							Modified = true;
						}
					}

				//最终检查
				if (Modified)
				{
					data.DataPackages.push(item);
				}
			});
			
			return data;
		}
				
		//保存成功后的后续处理(ok)
		function afterSaveGrid(){
			selectedRow = null;
			var i = 0;
			$("#DataPackage_List tr:gt(0)").each(function(){
				var tr = $(this);
				var inputs = tr.find("input");
				var sel = tr.find("select");
				
				//取消高亮显示
				tr.removeClass("tr_high_light");
				
				//数据包标题
				field = $(inputs[inpDPTitle]);
				field.attr("old", $.trim(field.val()));
				
				//产品线ID
				$(sel[selPLine]).attr("old", parseInt($(sel[selPLine]).get(0).value));
				
				//类型
				$(sel[selDPType]).attr("old", parseInt($(sel[selDPType]).get(0).value));
				
				//备注
				field = $(inputs[inpDPMemo]);
				field.attr("old", $.trim(field.val()));
			});
		}
		
		//全选(ok)
		$("#all_check").click(function(){
			var _this = this;
			var v = this.checked;
			
			$("#DataPackage_List :checkbox").each(function(){
				if (this!==_this) this.checked = v;
			});
		});
			
		//禁止编辑表格(ok)
		function disableGrid(){
			$("#DataPackage_List input").attr("disabled", "disabled");
			$("#DataPackage_List select").attr("disabled", "disabled");
		}
		
		//允许编辑表格(ok)
		function enableGrid(){
			$("#DataPackage_List input").removeAttr("disabled");
			$("#DataPackage_List select").removeAttr("disabled");
		}
			
		//选择的行高亮显示(ok)
		$("#DataPackage_List").on("click","tr:gt(0)",function(){
			if (selectedRow!==this){
				if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
				
				$(this).addClass("tr_high_light");
				selectedRow = this;
			}
		});
			
		//查询按钮(ok)
		$("#search").click(LoadList);
		
		//查询数据包(ok)
		function LoadList(){
			//取参数
			var DPTitle = $.trim($("#txtDPTitle").val());
			var PLine = parseInt($("#selProductLine").get(0).value);
			var DPType = parseInt($("#selDPType").get(0).value);
			var beginTime = $.trim($("#beginTime").val());
			var endTime = $.trim($("#endTime").val());
			
			lastSearchParam = {};
			
			//填充参数
			lastSearchParam.pn = 0;
			if(!reNumber.test($("#pageSize").val()))
				$("#pageSize").val("100");
			lastSearchParam.pageSize = parseInt($("#pageSize").val());
			//时间
			if(beginTime != "")
			{
				lastSearchParam.StartDateTime = beginTime.replace(/T/, " ");
			}
			if(endTime != "")
			{
				lastSearchParam.EndDateTime = endTime.replace(/T/, " ");
			}
			//产品线
			if(PLine >= 0)
			{
				lastSearchParam.ProductLineID = PLine;
			}
			//类型
			if(DPType >= 0)
			{
				lastSearchParam.DataTypeID = DPType;
			}
			//标题
			if(DPTitle.length > 0)
			{
				lastSearchParam.Title = DPTitle;
			}
			
			//执行查询
			CallWaitdlg(true);
			$.ajax({
				url: "./qryDataPackageList.do", 
				type: "post", 
				data: JSON.stringify(lastSearchParam), 
				success: function(rsp){
					CallWaitdlg(false);
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
					CallWaitdlg(false);
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
		
		//弹出上传窗口(ok)
		$("#btnAdd").click(function(){
			//复位
			$("#packageTitle").val("");
			$("#packageNote").val("");
			$("#packageProductLineID").get(0).value = -1;
			$("#packageDataTypeID").get(0).value = -1;
			$("#packageFile").val("");
			$("#uploadDataPackage_dlg").fadeIn(100);
		});
		
		//上传按钮(ok)
		$("#btnUpload").click(function(){
			//检查
			if($("#packageTitle").val().length <=0)
			{
				showTips("请填写数据包标题");
				return;
			}
			if($("#packageDataTypeID").get(0).value <0)
			{
				showTips("请选择数据包类型");
				return;
			}
			if($("#packageFile").val().length <=0)
			{
				showTips("请选择一个文件");
				return;
			}
			
			var _this = this;
			var file = $("#packageFile").val();
			if (file.length>0){
				CallWaitdlg(true,"正在上传文件,请稍候...");
				$("#uploadForm").ajaxSubmit({
					url : "./uploadDataPackage.do",
					secureuri : false,
					fileElementId : 'packageFile',
					dataType : 'json',
					async : true,
					success : function(data) {
						CallWaitdlg(false);
						if(data.errorCode == 0){
							$(_this).parents(".dialog").hide();		
							LoadList();
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
				showTips("请选择一个文件");
			}
		});
		
		//保存按钮(ok)
		$("#btnSave").click(function(){
			var param = getModifiedData();
			if (param===false) return;
			else if (param.DataPackages.length===0){
				showTips("没有修改过数据，不需要保存！");
				return;
			}
			
			disableGrid();//先禁止表格的编辑
			CallWaitdlg(true);
			$.ajax({
				url: "./editDataPackage.do", 
				type: "post", 
				data: JSON.stringify(param), 
				success: function(rsp){
					CallWaitdlg(false);
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						afterSaveGrid();
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
		
		//批量删除(ok)
		$("#btnDel").click(function(){
			//准备数据
			var counter = 0;
			var data = {DataPackages:[]}
			$("#DataPackage_List tr:gt(0)").each(function(){
				var chkbox = $(this).find(".checkbox");
				if(chkbox[0].checked)
				{
					var idnum = $(this).attr("data");
					if(typeof(idnum) !== "undefined")
					{
						var param = {ID:parseInt(idnum)};
						data.DataPackages.push(param);
					}
					else
					{
						$(this).remove();
					}
					counter++;
				}
			});
			
			if(data.DataPackages.length > 0)
			{
				if (!confirm("是否确定删除所选的项？"))
					return;
				CallWaitdlg(true);
				$.ajax({
					url: "./editDataPackage.do", 
					type: "post", 
					data: JSON.stringify(data), 
					success: function(rsp){
						CallWaitdlg(false);
						if (rsp.errorCode!=0){
							showTips(rsp.msg);

						}else{
							showTips("操作成功!");
							LoadList();
						}
					}, 
					error: function(){
						CallWaitdlg(false);
						showTips("请求出错了");
					}, 
					dataType: "json"
				});
			}
			else
			{
				showTips("请勾选至少一条要删除的项!");
				return;
			}
		});
		
		//取消按钮(ok)
		$("#btnCancel").click(function(){
			if (!confirm("是否确定取消所做的修改？")){
				return;
			}
			restoreGrid();
		});
	
	//关闭窗口(ok)
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