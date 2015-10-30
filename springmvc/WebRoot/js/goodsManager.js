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
var idxProductLineID = 1;
var idxCustomNo = 2;
var idxTitle = 3;
var idxName = 4;
var idxBrandID = 5;
var idxDept = 6;
var idxBasePrice = 7;
var idxPrice = 8;
var idxDistributePrice = 9;
var idxImg = 10;
var idxGoodsUrl = 11;
var idxStockQty = 12;
var idxStatus = 13;
var idxNote = 14;


//label
var lblCustomNo = 1;
var lblName = 2;
var lblBrandID = 3;
var lblDept = 4;
var lblDistributePrice = 5;
var lblStockQty = 6;
var lblStatus = 7;

//input
var inpTitle = 1;
var inpBasePrice = 2;
var inpPrice = 3;
var inpGoodsUrl = 4;
var inpNote= 5;


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

//查看SKU库存
function skuStockQty(goodsID)
{
	//得到对话框对象
	var dlg = $("#SKU_dialog");
	var dbody = $(dlg.find(".dialog_body"));

	//设置显示位置
	dbody.css("left", ($(document).width()- parseInt(dbody.css("width").replace("px","")))/2+"px");
	dbody.css("top",  "30px");
	
	//显示窗口
	dlg.show();
	
	//取得数据
	$.ajax({
		url: "./querySkuInventory.do", 
		type: "post", 
		data: JSON.stringify({GoodsID:goodsID}), 
		success: function(rsp){
			if (rsp.errorCode!=0){
				showTips(rsp.msg);
			}else{
				//清除表数据
				$("#SkuInventory_List tr:gt(0)").each(function(){
					$(this).remove();
				});
				
				//填充表数据
				var ln = rsp.data.length;
				for(var i=0; i<ln; i++)
				{
					var ListRow = rsp.data[i];
					var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
					$("#SkuInventory_List tbody").append(txt);
					var tr = $("#SkuInventory_List tr:last");
					var cells = tr.find("td");
					$(cells[0]).html("<label>" + ListRow.CustomBC + "</label>");
					$(cells[1]).html("<label>" + ListRow.BarcodeID + "</label>");
					$(cells[2]).html("<label>" + ListRow.Color + "</label>");
					$(cells[3]).html("<label>" + ListRow.Size + "</label>");
					var qq=ListRow.Qty;
					if (""+qq=="undefined")
					{
						qq=0;
					}
					$(cells[4]).html("<label>" + qq + "</label>");
					$(cells[5]).html("<label>" + ListRow.UseQty + "</label>");
				}
			}
		}, 
		error: function(){
			showTips("请求出错了");
		}, 
		dataType: "json"
	});
}

//查看图片
function PicViewer(GoodsID)
{
	//alert(GoodsID);
	
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
				setActiveMenu("资料管理", "商品资料管理");
			}
			
			//读取品牌信息
			if ("brand" in allData){
				brandlist = allData.brand;
				var selectlist = $("#selBrand");
				var ln = brandlist.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+brandlist[i].ID+"'>"+$.trim(brandlist[i].Name)+"</option>").appendTo(selectlist);
				}
			}
			
			//读取线条列表
			if ("productLine" in allData){
				productLineList = allData.productLine;
				var selectlist = $("#selProductLine");





				var selectlistB = $("#selAddProductLine");
				var ln = productLineList.length;
				for(var i=0; i<ln; i++){
					$("<option value='"+productLineList[i].ID+"'>"+$.trim(productLineList[i].Name)+"</option>").appendTo(selectlist);
					$("<option value='"+productLineList[i].ID+"'>"+$.trim(productLineList[i].Name)+"</option>").appendTo(selectlistB);
					
				}
			}
			
			//设置分页
			if (typeof(resetPageCtrl)!=="undefined"){
				resetPageCtrl({rowCnt:0, page:0, psize:pageSizeSetting});
			}
			
			//按情况隐藏操作栏
			hideCustomer();
		}

	};
	
	//全选(ok)
	$("#all_check").click(function(){
		var _this = this;
		var v = this.checked;
		
		$("table :checkbox").each(function(){
			if (this!==_this) this.checked = v;
		});
	});
	
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
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td hidden></td><td></td><td></td><td></td></tr>";
		$("#Goods_List tbody").append(txt);
		var tr = $("#Goods_List tr:last");
		var cells = tr.find("td");
		//$(cells[0]).addClass("");
		
		//复选框
		$(cells[0]).html("<input type=\"checkbox\" class=\"checkbox\"/>");
		//线条&商品ID,label 0
		$(cells[idxProductLineID]).html("<label></label>"); 
		//货号,label 1
		$(cells[idxCustomNo]).html("<label></label>");
		//标题
		$(cells[idxTitle]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入标题\">");
		//名称,label 2
		$(cells[idxName]).html("<label></label>");
		//品牌,label 3
		$(cells[idxBrandID]).html("<label></label>");
		//品类,label 4
		$(cells[idxDept]).html("<label></label>");
		//网络牌价
		$(cells[idxBasePrice]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入网络牌价\">");
		//售价
		
		$(cells[idxPrice]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入售价\">");
		//分销价,label 5
		$(cells[idxDistributePrice]).html("<label></label>");
		//商品图片
		$(cells[idxImg]).html("<img src=\"\" width=\"80\" height=\"80\" alt=\"商品图片\" />");
		//网站链接
		$(cells[idxGoodsUrl]).html("<input hidden type=\"text\" class=\"text\" placeholder=\"请输入网站链接\"><a hidden href=\"#\" target='_blank'>链接</a>");
		//库存数&查看SKU库存,label 6
		$(cells[idxStockQty]).html("<center><label>库存数</label><br><a href=\"#\">查看SKU库存</a></center>");
		//状态,label 7
		$(cells[idxStatus]).html("<label></label>");
		//备注
		$(cells[idxNote]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入备注\">");
		//添加双击行事件
/* 		$(tr).on("dblclick",function(){
			// $(this).attr("style","{-moz-user-select:none}");
			// $(this).attr("onselectstart","return false");
			var chkbox = $(this).find(".checkbox");
			if(chkbox[0].checked)
				chkbox[0].checked = false;
			else
				chkbox[0].checked = true;
			// $(this).removeAttr("style");
			// $(this).removeAttr("onselectstart");
		}); */
		
		hideCustomer();
		
		return tr;
	}
	
	//填充数据到表格(ok)
	function fillGrid(data){
		var ln = data.length;
		for(var i=0; i<ln; i++)
		{			
			var tr = addGridRow();
			var GoodsList = data[i];
			var inputs = tr.find("input");
			var labels = tr.find("label");
			var img = tr.find("img");
			var links = tr.find("a");


			//线条&商品ID
			var field = $(labels[0]);
			field.attr("goodsID",GoodsList.GoodsID);
			field.attr("productLineID",GoodsList.ProductLineID);
			if(productLineList)
			{
				var count = productLineList.length;
				for(var idx=0; idx<count; idx++){
					if(productLineList[idx].ID == GoodsList.ProductLineID)
					{
						field.html($.trim(productLineList[idx].Name));
						break;
					}
				}
			}
			else
				field.html(GoodsList.ProductLineID);
			// 货号
			field = $(labels[lblCustomNo]);
			field.html($.trim(GoodsList.CustomNo));
			// 标题
			field = $(inputs[inpTitle]);
			field.attr("old",$.trim(GoodsList.Title));
			field.val($.trim(GoodsList.Title));
			// 名称
			field = $(labels[lblName]);
			field.html($.trim(GoodsList.GoodsName));
			// 品牌
			field = $(labels[lblBrandID]);
			if(brandlist)
			{
				var count = brandlist.length;
				for(var idx=0; idx<count; idx++){
					if(brandlist[idx].ID == GoodsList.BrandID)
					{
						field.html($.trim(brandlist[idx].Name));
						break;
					}
				}
			}
			else
				field.html(GoodsList.BrandID);
			//品类
			field = $(labels[lblDept]);
			field.html($.trim(GoodsList.Dept));
			//网络牌价
			field = $(inputs[inpBasePrice]);
			field.attr("old",$.trim(GoodsList.BasePrice));
			field.val($.trim(GoodsList.BasePrice));

			
			//售价
			field = $(inputs[inpPrice]);
			field.attr("old",$.trim(GoodsList.Price));
			field.val($.trim(GoodsList.Price));

			//field.
			//分销价
			field = $(labels[lblDistributePrice]);
			field.html($.trim(GoodsList.DistributePrice));
			//商品图片
			field = $(img[0]);
			// if(typeof(GoodsList.ImaUrl) !== "undefined" )
				// field.attr('src',GoodsList.ImaUrl);
			// else
			// {
				// $("<label>无图片</label>").insertAfter($(field));
				// field.remove();
			// }

			field.attr("onerror","this.onerror=null; this.src='./images/NoPic.jpg';");
			if (GoodsList.ImaUrl == null)
			{
				field.attr("src","./goodsImages/" + GoodsList.GoodsID + "/main.jpg");
			}
			else{
				field.attr("src",GoodsList.ImaUrl);
			}
			
			field.attr("ondblclick","PicViewer(" + GoodsList.GoodsID + ");");
			
			//网站链接
			field = $(inputs[inpGoodsUrl]);
			field.attr("old",$.trim(GoodsList.GoodsUrl));			
			var l = "<a href='"+$.trim(GoodsList.GoodsUrl)+"'></a>"
			field.val($.trim(GoodsList.GoodsUrl));
			
			field = $(links[0]);
			field.attr("href",$.trim(GoodsList.GoodsUrl));

			field.attr("link",$.trim(GoodsList.GoodsUrl));
			//库存数&查看SKU库存
			field = $(labels[lblStockQty]); //7
			if(typeof(GoodsList.StockQty) !== "undefined")
				field.html(GoodsList.StockQty);
			else
				field.html("0")
			field = $(links[1]);
			field.attr("onclick","skuStockQty(" + GoodsList.GoodsID + ");");
			//状态
			field = $(labels[lblStatus]); //8
			if(GoodsList.Status == 1)
				field.html("正常");
			else
				field.html("作废");
			//备注
			field = $(inputs[inpNote]);
			field.attr("old",$.trim(GoodsList.Note));
			field.val(GoodsList.Note);
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
				var field = $(inputs[inpTitle]);
				var oldval = field.attr("old");	//旧标题
				var newval = $.trim(field.val());	//标题
				if (typeof(oldval)!=="undefined"){
					//已经有old数据但是与当前val不一样就代表被修改过
					if (oldval!==newval)
						Modified = true;
				}
				one.Title = newval;
				//网络牌价
				field = $(inputs[inpBasePrice]);
				var oldval = parseFloat(field.attr("old"));	//旧牌价
				var newval = parseFloat($.trim(field.val()));	//牌价
				if (typeof(oldval)!=="undefined")
				{
					if (oldval!==newval)
						Modified = true;
				}
				one.BasePrice = newval;

				//售价
				field = $(inputs[inpPrice]);
				var oldval = parseFloat(field.attr("old"));	//
				var newval = parseFloat($.trim(field.val()));	//
				if (typeof(oldval)!=="undefined")
				{
					if (oldval!==newval)
						Modified = true;
				}
				one.Price = newval;
				
				//网站地址
				field = $(inputs[inpGoodsUrl]);
				oldval = field.attr("old");	//旧地址
				newval = $.trim(field.val());	//地址
				if (typeof(oldval)!=="undefined"){
					if (oldval!==newval)
						Modified = true;
				}
				one.GoodsUrl = newval;
				
				
				
				//备注
				field = $(inputs[inpNote]);
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
				var field = $(inputs[inpTitle]);
				field.attr("old",$.trim(field.val()));

				//网站地址
				var field = $(inputs[inpGoodsUrl]);
				field.attr("old",$.trim(field.val()));

				var field = $(inputs[inpBasePrice]);
				field.attr("old",$.trim(field.val()));

				var field = $(inputs[inpPrice]);
				field.attr("old",$.trim(field.val()));
				
				//备注
				var field = $(inputs[inpNote]);
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
			var field = $(inputs[inpTitle]);
			field.val(field.attr("old"));

			//网站地址
			var field = $(inputs[inpGoodsUrl]);
			field.val(field.attr("old"));

			var field = $(inputs[inpBasePrice]);
			field.val(field.attr("old"));
			var field = $(inputs[inpPrice]);
			field.val(field.attr("old"));
			
			//备注
			var field = $(inputs[inpNote]);
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
		var pid = $("#selProductLine").val();
		var brand = $("#selBrand").val();
		var dept = $("#txtDept").val();
		var customno = $("#txtCustomNo").val();
		var name = $("#txtName").val();
		var status = $("#selStatus").val();
		var distribution = document.getElementById("chkDistribution").checked;
		lastSearchParam = {};
		//填充参数
		lastSearchParam.pn = 0;
		lastSearchParam.pageSize = parseInt($("#pageSize").val());
		//产品线
		if(parseInt(pid) >= 0)
			lastSearchParam.ProductLineID = parseInt(pid);
		//品牌
		if(parseInt(brand) >= 0)
			lastSearchParam.BrandID = parseInt(brand);
		//品类
		if(dept.length > 0)
			lastSearchParam.Dept = dept;
		//货号
		if(customno.length > 0)
			lastSearchParam.CustomNo = customno;
		//名称
		if(name.length > 0)
			lastSearchParam.GoodsName = name;
		//状态
		if(parseInt(status) >=0 )
			lastSearchParam.Status = parseInt(status);
		//是否分销
		if(distribution)
			lastSearchParam.IsDistribute = 1;
		//刷新列表
		refList();
	});
	
	//执行查询,刷新列表(ok)
	function refList(){
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

		if (!confirm("是否确定作废所选的分销商品？")){
			return;
		}


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

		if (!confirm("是否确定恢复所选的分销商品？")){
			return;
		}

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

	//删除按钮
	$("#btnDelete").click(function(){
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
		
		var data = {goods:[]}
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

		if (!confirm("是否确定删除所选的分销商品？")){
			return;
		}

		if(JSON.stringify(data) == JSON.stringify({goods:[]}))
		{
			showTips("请选择未加入分销的商品!");
			dlg.hide();
		}
		else
		{
			$.ajax({
				url: "./deleteDistributeGoods.do", 
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
		

		var file = $("#importfile").val();
		if (file.length>0){
			$("#uploadForm2").ajaxSubmit({
				url : "importDistributeGoods.do",
				secureuri : false,
				fileElementId : 'importfile',
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
	
	//开始初始化
	init();
});
