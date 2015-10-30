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
var idxDeliveryGroup = 1;
var idxdelivery = 2;
var idxState = 3;
var idxCity = 4;
var idxDistrict = 5;
var idxStatus = 6;
var idxReceivedZone = 7;
var idxNoReceivedZone = 8;
var idxPartReceivedZone = 9;
var idxNoReceivedZoneEx = 10;

//每页行数设定
var pageSizeSetting = 11;

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
                url: "./qryDecItem.do",
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
                setActiveMenu("客户管理", "商品资料管理");
            }



            //读取店铺信息
            if ("shop" in allData){
                delivery = allData.shop;
                var selShop = $("#selShop");
                var ln = delivery.length;
                for(var i=0; i<ln; i++){
                    $("<option value='"+delivery[i].ID+"'>"+$.trim(delivery[i].Name)+"</option>").appendTo(selShop);
                }
            }




            //设置分页
            if (typeof(resetPageCtrl)!=="undefined"){
                resetPageCtrl({rowCnt:0, page:0, psize:pageSizeSetting});
            }

			$.ajax({
				url: "./getCustomerSKUReplace.do",
				type: "post",
				data: {},
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						$("#edtValue").val(rsp.data);
					}
				},
				error: function(){
					showTips("请求出错了");
				},
				dataType: "json"
			});

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
    }

    //添加新行(ok)
    function addGridRow(i,data){
        var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        $("#Goods_List tbody").append(txt);
        var tr = $("#Goods_List tr:last");
        var cells = tr.find("td");
        var GoodsList = data[i];
        //商品编码、商品名称、基本价格、进货价、供应商、备注
        //店铺
        $(cells[0]).html("<input type=\"text\" style=\"background-color:transparent\" readonly=\"readonly\" class=\"text\" >");
        $(cells[0]).find("input").val(GoodsList.ShopName);
        $(cells[0]).find("input").attr("skuid",GoodsList.sku_id);
        $(cells[0]).find("input").attr("shopid",GoodsList.ShopID);
        $(cells[0]).find("input").attr("customord",GoodsList.CustomBC);
        $(cells[0]).find("input").attr("nameord",GoodsList.Name);
        $(cells[0]).find("input").attr("venord",GoodsList.Vender);
        $(cells[0]).find("input").attr("baseord",GoodsList.BasePrice);
        $(cells[0]).find("input").attr("costord",GoodsList.Cost);
        $(cells[0]).find("input").attr("noteord",GoodsList.Note);
        //SKU
        $(cells[1]).html("<input type=\"text\"style=\"background-color:transparent\" readonly=\"readonly\" class=\"text\" >");
        $(cells[1]).find("input").val($.trim(GoodsList.OuterSkuID));
        //商品编码
        $(cells[2]).html("<input type=\"text\"  class=\"text\"  placeholder=\"请输入商品编码\">");

        $(cells[2]).find("input").val($.trim(GoodsList.CustomBC));
        //商品标题
        $(cells[3]).html("<label>"+GoodsList.Title+"</label>");
        //商品名称
        $(cells[4]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入商品名称\">");
        $(cells[4]).find("input").val($.trim(GoodsList.Name));
        //商品图片
        $(cells[5]).html("<img src=\"\" width=\"80\" height=\"80\" alt=\"商品图片\" />");
        $(cells[5]).find("img").attr("src",GoodsList.pic_url);
        //属性
        $(cells[6]).html("<label>"+GoodsList.props_name+"</label>");
        //供应商
        $(cells[7]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入供应商\">");
        $(cells[7]).find("input").val($.trim(GoodsList.Vender));
        //基本价格
        $(cells[8]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入基本价格\">");
        $(cells[8]).find("input").val($.trim(GoodsList.BasePrice));
        //进货价
        $(cells[9]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入进货价\">");
        $(cells[9]).find("input").val($.trim(GoodsList.Cost));
        //销售价
        $(cells[10]).html("<input type=\"text\"style=\"background-color:transparent\"  readonly=\"readonly\" class=\"text\" >");
        $(cells[10]).find("input").val($.trim(GoodsList.price));
        //备注
        $(cells[11]).html("<input type=\"text\" class=\"text\" placeholder=\"请输入备注\">");
        $(cells[11]).find("input").val($.trim(GoodsList.Note));
        return tr;
    }

    //填充数据到表格(ok)
    function fillGrid(data){
        var ln = data.length;
        for(var i=0; i<ln; i++)
        {
            var tr = addGridRow(i,data);
//            var GoodsList = data[i];
//            var inputs = tr.find("input");
//            //店铺   商品编码、商品名称、基本价格、进货价、供应商、备注可编辑。
//            var field = $(inputs[0]);
//            field.attr("skuid",GoodsList.sku_id);
//            field.attr("shopid",GoodsList.ShopID);
//            field.val(GoodsList.ShopName);
//            //SKU
//            field = $(inputs[1]);
//            field.val($.trim(GoodsList.OuterSkuID));
//            //商品编码
//            field = $(inputs[2]);
//            field.attr("old",GoodsList.CustomBC);
//            field.val($.trim(GoodsList.CustomBC));
            //商品标题
//            field = $(span[3]);
//            field.text($.trim(GoodsList.Title));
//            //商品名称
//            field = $(inputs[3]);
//            field.attr("old",GoodsList.Name);
//            field.val($.trim(GoodsList.Name));
            //属性
//            field = $(inputs[4]);
//            field.val($.trim(GoodsList.props_name));
//            //供应商
//            field = $(inputs[5]);
//            field.attr("old",GoodsList.Vender);
//            field.val($.trim(GoodsList.Vender));
//            //基本价格
//            field = $(inputs[6]);
//            field.attr("old",GoodsList.BasePrice);
//            field.val($.trim(GoodsList.BasePrice));
//            //进货价
//            field = $(inputs[7]);
//            field.attr("old",GoodsList.Cost);
//            field.val($.trim(GoodsList.Cost));
//            //销售价
//            field = $(inputs[8]);
//            field.val(GoodsList.price);
//            //备注
//            field = $(inputs[9]);
//            field.attr("old",GoodsList.Note);
//            field.val(GoodsList.Note);
        }
    }

    //对比并获取不同的数据
    function getModifiedData(){
        var data = {DecItems:[]};

        $("#Goods_List tr:gt(0)").each(function(){
            var tr = $(this);
            var inputs = tr.find("input");
            var one = {};
            var field = $(inputs[0]);
            var skuId = field.attr("skuid");
            var shopId = field.attr("shopid");
            var custOrd = field.attr("customord");
            var nameOrd = field.attr("nameord");
            var venOrd = field.attr("venord");
            var baseOrd = field.attr("baseord");
            var costOrd = field.attr("costord");
            var noteOrd = field.attr("noteord");
            var val = $.trim(field.val());
            one.ShopID = parseInt(shopId);
            one.sku_id = skuId;
            //商品编码
            field = $(inputs[2]);
            val = $.trim(field.val());
            if (typeof(custOrd)!=="undefined"){
                if (custOrd!==val) one.CustomBC = val;
            }
            else if(val.length>0){
                one.CustomBC = val;
            }

            //商品名称
            field = $(inputs[3]);
            val = $.trim(field.val());
            if (typeof(nameOrd)!=="undefined"){
                if ($.trim(nameOrd)!==val)
                  one.Name = val;
            }
            else if(val.length>0){
                one.Name = val;
            }
            //供应商
            field = $(inputs[4]);
            val = $.trim(field.val());
            if (typeof(venOrd)!=="undefined"){
                if (venOrd!==val) one.Vender = val;
            }
            else if(val.length>0){
                one.Vender = val;
            }
            //基本价格
            field = $(inputs[5]);
            val = $.trim(field.val());
            if (typeof(baseOrd)!=="undefined"){
                if (baseOrd!==val) one.BasePrice = parseFloat(val);
            }
            else if(val.length>0){
                one.BasePrice = parseFloat(val);
            }
            //进货价
            field = $(inputs[6]);
            val = $.trim(field.val());
            if (typeof(costOrd)!=="undefined"){
                if (costOrd!==val) one.Cost = parseFloat(val);
            }
            else if(val.length>0){
                one.Cost = parseFloat(val);
            }
            //备注
            field = $(inputs[8]);
            val = $.trim(field.val());
            if (typeof(noteOrd)!=="undefined"){
                if (noteOrd!==val) one.Note = val;
            }
            else if(val.length>0){
                one.Note = val;
            }
            if ( "CustomBC" in one || "Name" in one || "Vender" in one || "BasePrice" in one || "Cost" in one || "Note" in one){
                data.DecItems.push(one);

            }
        });
        return data;
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
        var selS = $("#selShop").attr("shopid");//商品ID
        var selV = $("#selShop").val();//判断是否有选择
        var sku = $("#sku").val();//suk
        var comC = $("#comCode").val();//商品编码
        var comT = $("#comTitle").val();//商品标题
        var comN = $("#comName").val();//商品名称
        var pro = $("#property").val();//属性
        var stall = $("#stall").val();//档口
        lastSearchParam = {};
        //填充参数
        //页数
        lastSearchParam.pageSize = parseInt($("#num").val());
        //商品ID
        if(selV != -1){
            lastSearchParam.ShopID = parseInt(selV);
        }
        //suk
        if(sku.length > 0)
            lastSearchParam.OuterSkuID = sku;
        //商品编码
        if(comC.length > 0)
            lastSearchParam.CustomBC = comC;
        //商品标题
        if(comT.length > 0)
            lastSearchParam.Title = comT;
        //商品名称
        if(comN.length > 0)
            lastSearchParam.Name = comN;
        //属性
        if(pro.length > 0)
            lastSearchParam.Props = pro;
        //档口
        if(stall.length > 0)
            lastSearchParam.Vender = stall;


        //刷新列表
        refList();
    });

    //执行查询,刷新列表(ok)
    function refList(){
        $.ajax({
            url: "./qryDecItem.do",
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

    //保存
    $("#btnSave").click(function(){
		if(!confirm("确认保存资料吗?")){
			return ;
		}

        var param = getModifiedData();
        disableGrid();//先禁止表格的编辑
        $.ajax({
            url: "./saveDecItem.do",
            type: "post",
            data: JSON.stringify(param),
            success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    refList();
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

    //资料过滤保存
    $("#btnSaveRepace").click(function(){
		if(!confirm("确认保存资料过滤数据吗?")){
			return ;
		}

		var data = {};
		data.Value =  $("#edtValue").val();
        $.ajax({
            url: "./setCustomerSKUReplace.do",
            type: "post",
            data: JSON.stringify(data),
            success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    showTips("保存数据成功");
                }
            },
            error: function(){
                showTips("请求出错了");
            },
            dataType: "json"
        });
    });


	$("#btnImport").click(function(){
		var dlg = $("#import_dlg");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "30px");
	});

	$("#btnExport").click(function(){
		$.ajax({
				type : "POST",
				url : "exportDecItem.do",
				dataType:'json',
				contentType:"application/json;charset=UTF-8",
				data:JSON.stringify(lastSearchParam),
				async : false,
				success : function(data) {
					if (data.errorCode==0)
					{
						window.location.href=data.data;
					}else{
						showTips(data.msg);
					}										
				},error:function(data){
					showTips("导出错误" );
				}
			});

	});

	$("#btnImportUp").click(function(){
		var _this = this;
		w = $("#imgWait2");
		w.show();

		var file = $("#file_for_import").val();
		if (file.length>0){
			$("#uploadForm").ajaxSubmit({
				url : "importDecItem.do",
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
					showTips(msg);
				}
			});
		}else{
			showTips("请选择一个文件");
		}

		w.hide();
	});


    //关闭窗口(ok)
    $(".dialog_close").click(function(){
        var dialogbody = $(this).parent("div");
        $(dialogbody).parent("div").hide();
    });
    $(".dlg_Close_btn").click(function(){
        $(this).parents(".dialog").hide();
    });

    //开始初始化
    init();
});
