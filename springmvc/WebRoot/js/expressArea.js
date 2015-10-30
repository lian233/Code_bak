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
                url: "./queryDecDeliveryZone.do",
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
                setActiveMenu("业务管理", "快递区域");
            }



            //读取快递套餐方案
            if ("deliveryGroup" in allData){
                delivery = allData.deliveryGroup;
                var Name = $("#selDeliveryGroup");
                var ln = delivery.length;
                for(var i=0; i<ln; i++){
                    $("<option value='"+delivery[i].ID+"'>"+$.trim(delivery[i].Name)+"</option>").appendTo(selDeliveryGroup);
                }
            }

            //读取快递套餐方案2
            if ("deliveryGroup" in allData){
                delivery = allData.deliveryGroup;
                var Name = $("#selDeliveryGroup2");
                var ln = delivery.length;
                for(var i=0; i<ln; i++){
                    $("<option value='"+delivery[i].Name+"'>"+$.trim(delivery[i].Name)+"</option>").appendTo(selDeliveryGroup2);
                }
            }

            //读取快递
            if ("delivery" in allData){
                delivery = allData.delivery;
                var selectlist = $("#selDelivery");
				 var modifyDelivery = $("#modifyDelivery");
                //var selectlistB = $("#selAddProductLine");
                var ln = delivery.length;
                for(var i=0; i<ln; i++){
                    $("<option value='"+delivery[i].id+"'>"+$.trim(delivery[i].name)+"</option>").appendTo(selDelivery);
					$("<option value='"+delivery[i].id+"'>"+$.trim(delivery[i].name)+"</option>").appendTo(modifyDelivery);
                   //$("<option value='"+productLineList[i].ID+"'>"+$.trim(productLineList[i].Name)+"</option>").appendTo(selectlistB);

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
        var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        $("#Goods_List tbody").append(txt);
        var tr = $("#Goods_List tr:last");
        var cells = tr.find("td");
        //$(cells[0]).addClass("");

        //复选框
        $(cells[0]).html("<input type=\"checkbox\" class=\"checkbox\"/>");
        //快递区域,label 1
        $(cells[idxDeliveryGroup]).html("<label></label>");
        //快递,label 2
        $(cells[idxdelivery]).html("<label></label>");
        //省份    3
        $(cells[idxState]).html("<label></label>");
        //城市,label 4
        $(cells[idxCity]).html("<label></label>");
        //区,label 5
        $(cells[idxDistrict]).html("<label></label>");
        //状态,label 6
        $(cells[idxStatus]).html("<label></label>");
        //接受范围,label 7
        $(cells[idxReceivedZone]).html("<label></label>");
        //不接受范围 8
        $(cells[idxNoReceivedZone]).html("<label></label>");
        //部分接受,label 9
        $(cells[idxPartReceivedZone]).html("<label></label>");
        //不接收范围扩展 10
        $(cells[idxNoReceivedZoneEx]).html("<label></label>");

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
            var labels = tr.find("label");

            //快递套餐和快递ID
            var field = $(labels[0]);
            field.attr("DeliveryID",GoodsList.DeliveryID);//前面一个是自定义一个.GoodsID是post得到的数据，赋值给goodsID
            field.attr("DeliveryGroupID",GoodsList.DeliveryGroupID);
            field.html(GoodsList.DeliveryGroupName);
            //快递
            field = $(labels[1]);
            field.html($.trim(GoodsList.DeliveryName));

            // 省份
            field = $(labels[2]);
            field.attr("State",GoodsList.State);
            field.html($.trim(GoodsList.State));
            // 城市
            field = $(labels[3]);
            field.attr("City",GoodsList.City);
            field.html($.trim(GoodsList.City));
            // 区
            field = $(labels[4]);
            field.attr("District",GoodsList.District);
            field.html($.trim(GoodsList.District));
            //状态
            field = $(labels[5]); //8
            if(GoodsList.Status == 1)
                field.html("正常");
            else
                field.html("作废");
            // 接受范围
            field = $(labels[6]);
            field.html($.trim(GoodsList.ReceivedZone));
            //不接收范围
            field = $(labels[7]);
            field.html($.trim(GoodsList.NoReceivedZone));
            //部分接收
            field = $(labels[8]);
            field.html($.trim(GoodsList.PartReceivedZone));
            //field.
            //不接收范围扩展
            field = $(labels[9]);
            field.html($.trim(GoodsList.NoReceivedZoneEx));

        }
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
        var group = $("#selDeliveryGroup").val();
        var del = $("#selDelivery").val();
        var sta = $("#state").val();
        var city = $("#city").val();
        var dis = $("#district").val();
        var status = $("#selStatus").val();
        lastSearchParam = {};
        //填充参数
        lastSearchParam.pn = 0;
        lastSearchParam.pageSize = parseInt($("#pageSize").val());
        //快递套餐
        if(parseInt(group) >= 0)
            lastSearchParam.DeliveryGroupID = parseInt(group);
        //快递
        if(parseInt(del) >= 0)
            lastSearchParam.DeliveryID = parseInt(del);
        //省份
        if(sta.length > 0)
            lastSearchParam.State = sta;
        //城市
        if(city.length > 0)
            lastSearchParam.City = city;
        //区
        if(dis.length > 0)
            lastSearchParam.District = dis;
        //状态
        if(parseInt(status) >=0 )
            lastSearchParam.Status = parseInt(status);

        //刷新列表
        refList();
    });

    //执行查询,刷新列表(ok)
    function refList(){
        $.ajax({
            url: "./queryDecDeliveryZone.do",
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




	//修改快递按钮
    $("#btnModifyDelivery").click(function(){
        var del = $("#modifyDelivery").val();

		var deliveryID = parseInt(del);
        if(deliveryID <= 0){
            showTips("请选择快递!");
            return;
		}

        var selbool = false;
        $("#Goods_List tbody :checkbox").each(function(){
            if(this.checked)
            {
                selbool = true;
            }
        });
        if(!selbool)
        {
            showTips("请先勾选记录!");
            return;
        }

		if(!confirm("确认要修改勾选的快递吗?")){
			return ;
		}

        var data = {DecDeliveryZones:[],DeliveryID:deliveryID};
        $("#Goods_List tr:gt(0)").each(function(){
            var chkbox = $(this).find(".checkbox");
            if(chkbox[0].checked)
            {
                var labels = $(this).find("label");
                var DelG = $(labels[0]).attr("DeliveryGroupID");
                var Del = $(labels[0]).attr("DeliveryID");
                var State = $(labels[2]).attr("state");
                var City = $(labels[3]).attr("city");
                var District = $(labels[4]).attr("district");
                var param = {};


                if(typeof(DelG) !== "undefined" && typeof(Del) !== "undefined")
                {
                    param.DeliveryGroupID	= parseInt(DelG);
                    param.DeliveryID = parseInt(Del);
                    param.State = State;
                    param.City = City;
                    param.District = District;
                    data.DecDeliveryZones.push(param);
                }
            }
        });

		$.ajax({
			url: "./modifyDecDeliveryZoneDelivery.do",
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
            showTips("请先勾选记录!");
            return;
        }

		if(!confirm("确认作废勾选的数据吗?")){
			return ;
		}

        var data = {DecDeliveryZones:[],Status:0}
        $("#Goods_List tr:gt(0)").each(function(){
            var chkbox = $(this).find(".checkbox");
            if(chkbox[0].checked)
            {
                var labels = $(this).find("label");
                var DelG = $(labels[0]).attr("DeliveryGroupID");
                var Del = $(labels[0]).attr("DeliveryID");
                var State = $(labels[2]).attr("state");
                var City = $(labels[3]).attr("city");
                var District = $(labels[4]).attr("district");
                var param = {};


                if(typeof(DelG) !== "undefined" && typeof(Del) !== "undefined")
                {
                    param.DeliveryGroupID	= parseInt(DelG);
                    param.DeliveryID = parseInt(Del);
                    param.State = State;
                    param.City = City;
                    param.District = District;
                    data.DecDeliveryZones.push(param);
                }
            }
        });
            $.ajax({
                url: "./setDecDeliveryZone.do",
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
            showTips("请先勾选记录!");
            return;
        }

		if(!confirm("确认恢复勾选的数据吗?")){
			return ;
		}

        var data = {DecDeliveryZones:[],Status:1}
        $("#Goods_List tr:gt(0)").each(function(){
            var chkbox = $(this).find(".checkbox");
            if(chkbox[0].checked)
            {
                var labels = $(this).find("label");
                var DelG = $(labels[0]).attr("DeliveryGroupID");
                var Del = $(labels[0]).attr("DeliveryID");
                var State = $(labels[2]).attr("state");
                var City = $(labels[3]).attr("city");
                var District = $(labels[4]).attr("district");
                var param = {};

                if(typeof(DelG) !== "undefined" && typeof(Del) !== "undefined")
                {
                    param.DeliveryGroupID	= parseInt(DelG);
                    param.DeliveryID = parseInt(Del);
                    param.State = State;
                    param.City = City;
                    param.District = District;
                    data.DecDeliveryZones.push(param);
                }
            }
        });


            $.ajax({
                url: "./setDecDeliveryZone.do",
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

    $("#upload").click(function(){
        var datasent = $("#uploadForm").serializeObject();
        params = JSON.stringify(datasent);
        $("#uploadForm").ajaxSubmit({
            url : "importDecDeliveryZone.do",
            secureuri:false,
            fileElementId:'file',
            dataType:'json',
            success : function(data){
                var ss=eval(data);
                if(data.errorCode == 0){   //登录成功
                    alert("上传文件成功");
                }else{
                    alert("上传文件失败: "+data.msg);
                }

            },error:function(data){
                var ss=eval(data);
                //alert(data.toString());
                alert(JSON.stringify(ss));
                //alert(data.flag);
            }
        });
    });

    $("#out").click(function(){
        $.ajax({
            url: "exportDecDeliveryZone.do",
            type: "post",
			dataType:'json',
			contentType:"application/json;charset=UTF-8",
			async : false,
            data: JSON.stringify(lastSearchParam),
            success: function(rsp){
				if (rsp.errorCode==0)
				{
					window.location.href=rsp.data;
				}else{
					showTips(rsp.msg);
				}										
            },
            error: function(){
                showTips("导出错误");
            }
        });
    });



/*
    $("#out").click(function(){
        $.ajax({
            url: "exportDecDeliveryZone.do",
            type: "post",
            data: JSON.stringify(lastSearchParam),
            success: function(rsp){
                window.location = "http://192.168.1.20:8003/springmvc/temp/快递区域.xls"
            },
            error: function(){
                showTips("请求出错了");
            },
            dataType: "json"
        });
    });

*/

    //开始初始化
    init();
});
