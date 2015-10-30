/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
 //allData变量声明
 //用于存储返回的初始化数据
 if (typeof allData === "undefined") allData = {};
 
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
		var chk_all = $("#all_check")[0];
		chk_all.checked = false;
	}
	
	//添加新行(ok)
	function addGridRow(){
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		$("#Goods_List tbody").append(txt);
		var tr = $("#Goods_List tr:last");
		var cells = tr.find("td");
		//$(cells[0]).addClass("");
		
		//状态
		$(cells[0]).html("<label></label>");
		//单号
		$(cells[1]).html("<label></label>"); 
		//快递
		$(cells[2]).html("<label></label>");
		//时间
		$(cells[3]).html("<label></label>");
		//数量
		$(cells[4]).html("<label></label>");
		//金额
		$(cells[5]).html("<label></label>");
		
		return tr;
	}
	
	//填充数据到表格(ok)
	function fillGrid(data){
		var ln = data.length;
		for(var i=0; i<ln; i++)
		{
			var tr = addGridRow();
			var GoodsList = data[i];
			var labels = tr.find("label");
			//状态
			var field = $(labels[0]);
			field.text(GoodsList.Flag);
			//单号
			var field = $(labels[1]);
			field.text(GoodsList.SheetID);
			//快递
			var field = $(labels[2]);
			field.text(GoodsList.DeliverySheetID);
			//时间
			var field = $(labels[3]);
			field.text(GoodsList.EndTime);
			//数量
			var field = $(labels[4]);
			field.text(GoodsList.TotalQty);			
			//金额
			var field = $(labels[5]);
			field.text(GoodsList.TotalRefundAmount);				
		}
	}
	
	//获取修改过的列表数据(ok)
	function getModifiedData(){
		var data = {goods:[]};
		$("#Goods_List tr:gt(0)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			var labels = tr.find("label");
			var one = {};
			var Modified = false;
			
			//产品线ID,商品ID
			var field = $(labels[0]);
			var GID = field.attr("goodsid");
			var PID = field.attr("productLineID");
			if (typeof(PID)!=="undefined" && typeof(GID)!=="undefined")
			{
				one.ProductLineID = parseInt(PID);
				one.GoodsID = parseInt(GID);

				//标题
				var field = $(inputs[1]);
				var oldval = field.attr("old");	//旧标题
				var newval = $.trim(field.val());	//标题
				if (typeof(oldval)!=="undefined"){
					//已经有old数据但是与当前val不一样就代表被修改过
					if (oldval!==newval)
						Modified = true;
				}
				one.Title = newval;
				//网络牌价
				field = $(inputs[2]);
				var oldval = parseInt(field.attr("old"));	//旧牌价
				var newval = parseInt($.trim(field.val()));	//牌价
				if (typeof(oldval)!=="undefined")
				{
					if (oldval!==newval)
						Modified = true;
				}
				one.BasePrice = newval;
				
				//网站地址
				field = $(inputs[3]);
				oldval = field.attr("old");	//旧地址
				newval = $.trim(field.val());	//地址
				if (typeof(oldval)!=="undefined"){
					if (oldval!==newval)
						Modified = true;
				}
				one.GoodsUrl = newval;
				
				
				
				//备注
				field = $(inputs[4]);
				oldval = field.attr("old");	//旧备注
				newval = $.trim(field.val());	//备注
				if (typeof(oldval)!=="undefined"){
					if (oldval!==newval)
						Modified = true;
				}
				one.Note = newval;
			}
			//最终检查
			if (Modified && one !== JSON.stringify({}))
			{
				data.goods.push(one);
			}
		});
		
		return data;
	}
	
	//保存成功后的后续处理(ok)
	function afterSaveGrid(){
		selectedRow = null;
		$("#Goods_List tr:gt(0)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			var labels = tr.find("label");
			tr.removeClass("tr_high_light");
			
			//产品线ID,商品ID
			var field = $(labels[0]);
			var GID = field.attr("goodsid");
			var PID = field.attr("productLineID");
			//仅限于已经加入分销的商品
			if (typeof(PID)!=="undefined" && typeof(GID)!=="undefined")
			{
				//标题
				var field = $(inputs[1]);
				field.attr("old",$.trim(field.val()));

				//网站地址
				var field = $(inputs[2]);

				field.attr("old",$.trim(field.val()));
				
				//备注
				var field = $(inputs[3]);
				field.attr("old",$.trim(field.val()));
			}
		});
	}
	
	//还原修改前的数据(ok)
	function restoreGrid(){
		selectedRow = null;
		$("#Goods_List tr:gt(0)").each(function(){
			var tr = $(this);
			var inputs = tr.find("input");
			var labels = tr.find("label");
			tr.removeClass("tr_high_light");

			//标题
			var field = $(inputs[1]);
			field.val(field.attr("old"));

			//网站地址
			var field = $(inputs[2]);
			field.val(field.attr("old"));
			
			//备注
			var field = $(inputs[3]);
			field.val(field.attr("old"));
		});
	}
	
	//Goods_List选择的行高亮显示(ok)
	$("#Goods_List").on("click","tr:gt(0)",function(){
		if (selectedRow!==this){
			if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
			
			$(this).addClass("tr_high_light");
			selectedRow = this;
		}
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
		lastSearchParam = {};
		
		lastSearchParam.pn = 0;
		lastSearchParam.pageSize = pageSizeSetting;
		//快递公司
		if(expCorp.length > 0) {lastSearchParam.delivery = expCorp;}
		//快递单号
		if(expNo.length > 0) {lastSearchParam.deliverySheetID = expNo;}
		//状态
		lastSearchParam.flag = parseInt(status);
		//时间类型
		lastSearchParam.timeType = parseInt(makeTime);
		//开始时间
		if(beginTime.length > 0) 
		{
			alert($("#beginTime").val());
			lastSearchParam.beginTime = ($.trim($("#beginTime").val())).replace(/T/, " ");
			
		} //暂时出错，取了空值出来
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
	
	//加入分销(ok)
	$("#btnAdd").click(function(){
		//例行检查
		var selbool = false;
		$("#Goods_List tbody :checkbox").each(function(){
			if(this.checked)
			{
				selbool = true;
			}
		});
		if(!selbool)
		{
			showTips("请先勾选至少一个商品!");
			return;
		}
		
		//得到对话框对象
		var dlg = $("#AddGodds_dialog");
		var dbody = $(dlg.find(".dialog_body"));

		//设置显示位置
		dbody.css("left", ($(document).width()- parseInt(dbody.css("width").replace("px","")))/2+"px");
		dbody.css("top",  "30px");
		
		//显示窗口
		dlg.show();
	});
	
	//取消加入分销操作(ok)
	$("#btnAddCancel").click(function(){
		var dlg = $("#AddGodds_dialog");
		dlg.hide();
	});
	
	//确定加入分销操作(ok)
	$("#btnAddConfirm").click(function(){
		var dlg = $("#AddGodds_dialog");
		var sel = $("#selAddProductLine");
		if(sel.val() == -1)
		{
			showTips("请选择要加入的产品线!");
			return;
		}
		
		var data = {goods:[]}
		$("#Goods_List tr:gt(0)").each(function(){
			var chkbox = $(this).find(".checkbox");
			if(chkbox[0].checked)
			{
				var labels = $(this).find("label");
				var PID = $(labels[0]).attr("productLineID");
				var goodsID = $(labels[0]).attr("goodsID");
				var param = {};

				if(typeof(goodsID) !== "undefined" && typeof(PID) == "undefined")
				{
					param.ProductLineID	= parseInt(sel.val());
					param.GoodsID = parseInt(goodsID);
					data.goods.push(param);
				}
			}
		});
		
		if(JSON.stringify(data) == JSON.stringify({goods:[]}))
		{
			showTips("请选择未加入分销的商品!");
			dlg.hide();
		}
		else
		{
			$.ajax({
				url: "./addDistributeGoods.do", 
				type: "post", 
				data: JSON.stringify(data), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);

					}else{
						showTips("操作成功!");
						dlg.hide();
						refList();
					}
				}, 
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
	});
	
	//保存修改按钮(ok)
	$("#btnSave").click(function(){
		var param = getModifiedData();
		if (param===false) return;
		else if (param.goods.length===0){
			showTips("已加入分销的商品中,没有修改过数据，不需要保存！");
			return;
		}

		disableGrid();//先禁止表格的编辑
		
		$.ajax({
			url: "./saveDistributeGoods.do", 
			type: "post", 
			data: JSON.stringify(param), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					afterSaveGrid();
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
	
	//撤销修改按钮(ok)
	$("#btnCancel").click(function(){
		if (!confirm("是否确定取消所做的修改？")){
			return;
		}
		restoreGrid();
	});
	
	//作废按钮(ok)
	$("#btnInvalid").click(function(){
		//例行检查
		var selbool = false;
		$("#Goods_List tbody :checkbox").each(function(){
			if(this.checked)
			{
				selbool = true;
			}
		});
		if(!selbool)
		{
			showTips("请先勾选至少一个商品!");
			return;
		}
		
		var data = {goods:[],Status:0}
		$("#Goods_List tr:gt(0)").each(function(){
			var chkbox = $(this).find(".checkbox");
			if(chkbox[0].checked)
			{
				var labels = $(this).find("label");
				var PID = $(labels[0]).attr("productLineID");
				var goodsID = $(labels[0]).attr("goodsID");
				var param = {};

				if(typeof(goodsID) !== "undefined" && typeof(PID) !== "undefined")
				{
					param.ProductLineID	= parseInt(PID);
					param.GoodsID = parseInt(goodsID);
					data.goods.push(param);
				}
			}
		});
		if(JSON.stringify(data) == JSON.stringify({goods:[],Status:0}))
		{
			showTips("请选择未加入分销的商品!");
			dlg.hide();
		}
		else
		{
			$.ajax({
				url: "./setDistributeGoodsStatus.do", 
				type: "post", 
				data: JSON.stringify(data), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						refList();
						showTips("操作成功!");
					}
				}, 
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
	});
	
	//恢复按钮(ok)
	$("#btnRecovery").click(function(){
		//例行检查
		var selbool = false;
		$("#Goods_List tbody :checkbox").each(function(){
			if(this.checked)
			{
				selbool = true;
			}
		});
		if(!selbool)
		{
			showTips("请先勾选至少一个商品!");
			return;
		}
		
		var data = {goods:[],Status:1}
		$("#Goods_List tr:gt(0)").each(function(){
			var chkbox = $(this).find(".checkbox");
			if(chkbox[0].checked)
			{
				var labels = $(this).find("label");
				var PID = $(labels[0]).attr("productLineID");
				var goodsID = $(labels[0]).attr("goodsID");
				var param = {};

				if(typeof(goodsID) !== "undefined" && typeof(PID) !== "undefined")
				{
					param.ProductLineID	= parseInt(PID);
					param.GoodsID = parseInt(goodsID);
					data.goods.push(param);
				}
			}
		});
		if(JSON.stringify(data) == JSON.stringify({goods:[],Status:1}))
		{
			showTips("请选择未加入分销的商品!");
			dlg.hide();
		}
		else
		{
			$.ajax({
				url: "./setDistributeGoodsStatus.do", 
				type: "post", 
				data: JSON.stringify(data), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						refList();
						showTips("操作成功!");
					}
				}, 
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
		}
	});
	
	//打开上传图片对话框(ok)
	$("#btnUploadIMG").click(function(){
		//例行检查,并准备必要数据
		if (selectedRow==null)
		{
			showTips("请选择一个商品！");
			return;
		}
		//得到对话框对象
		var dlg = $("#uploadIMG_dlg");
		var dbody = $(dlg.find(".dialog_body"));

		//设置显示位置
		dbody.css("left", ($(document).width()- parseInt(dbody.css("width").replace("px","")))/2+"px");
		dbody.css("top",  "30px");
		
		//显示窗口
		dlg.show();
	});
	
	//上传图片(ok)
	$("#btnUpload").click(function(){
		//例行检查,并准备必要数据
		if (selectedRow==null)
		{
			showTips("请选择一个商品！");
			return;
		}
		
		var _this = this;
		var labels = $(selectedRow).find("label");
		var GID = $(labels[0]).attr("goodsID");
		
		if(typeof(GID) !== "undefined")
		{
			var file = $("#imgfile_upload").val();
			if (file.length>0){
				$("#uploadForm").ajaxSubmit({
					url : "setDistributeGoodsImg.do",
					secureuri : false,
					data: {'GoodsID':GID}, 
					dataType : 'json',
					success : function(data) {
						if(data.errorCode == 0){
							$(_this).parents(".dialog").hide();
				
							showTips("上传商品图片成功");
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
		}
		else
			showTips("请选择一个商品！");
	});
	
	//按情况隐藏操作栏(ok)
	function hideCustomer(){
		if("curLogin" in allData)
			if (allData.curLogin.CustomerID > 0)
			{
				var foot = $(".foot");
				foot.hide();
				disableGrid();
			}
	}
	
	//关闭窗口(ok)
	$(".dialog_close").click(function(){
		var dialogbody = $(this).parent("div");
		$(dialogbody).parent("div").hide();
	});
	$(".dlg_Close_btn").click(function(){
		$(this).parents(".dialog").hide();
	});
	
	//弹出导入提示框
	$("#import").click(function(){
		var dlg = $("#import_dlg"); //弹出对话框
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
				url : "importDistributeGoods.do",
				secureuri : false,
				fileElementId : 'file_for_import',
				dataType : 'json',
				success : function(data) {
					if(data.errorCode == 0){
						$(_this).parents(".dialog").hide();
										
						var result = data.data;
						var ln = result.length;
						if (ln<=0)
						{
							showTips("导入文件成功");
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
								showTips("导入文件成功");
							}else{
								showTips(msg);
							}
						}
						
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
	
	//开始初始化
	init();
});
