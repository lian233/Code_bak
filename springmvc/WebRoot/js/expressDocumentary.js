 //客户列表
var customerlist = {};

//初始化后得到的数据
if (typeof allData === "undefined") allData = {};

//分页设定
var pageSizeSettingA = 5;
var pageSizeSettingB = 20;
 var num = 12;
//提示框
function showTips(text){
	var dlg = $("#tips_dlg");
	dlg.show();

	var dbody = dlg.find(".dialog_body");
	dbody.css("left", ($(document).width()-dbody.width())/2+"px");
	dbody.css("top", "100px");

	$(".tips").html(text);
}

$(function(){
	//alert(allData.curLogin.CustomerID);
	if(allData.curLogin.CustomerID > 0) 
	{
		$("#cu_id_div").empty();
		$("#cu_id_div1").empty();
		$("#cu_id_div1").attr("class","");
		$("#cu_id_div2").empty();
		$("#cu_id_div2").attr("class","");
		$("#cu_id_div3").empty();
		$("#cu_id_div3").attr("class","");
		$("#cu_id_div4").empty();
		$("#cu_id_div4").attr("class","");
		$("#qty_add").empty();
		$("#qty_add").attr("class","");
		$("#qty_add").attr("hidden","");
		$("#btn_save").empty();
		$("#btn_save").attr("class","");	
	}
	//当前选中的行
	var selectedRow = null;
	//当前查询条件
	var lastSearchParamA = null;	//客户表格
	var lastSearchParamB = null;	//快递记录
	//客户记录的页面控制
	page_ctrl_to_callback = function(pn){
		if (lastSearchParamA){
			lastSearchParamA.pn = pn;

			$.ajax({
                url: "./qryDeliveryTrace.do",
				type: "post", 
				data: JSON.stringify(lastSearchParamA), 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
                    next(rsp);
					}
				}, 
				error: function(){
					showTips("请求出错了");
				},
				dataType: "json"
			});
		}
	};

    function next(rsp){
        $.ajax({
            url: "./qryDeliveryTraceSta.do",
            type: "post",
            data: JSON.stringify(lastSearchParamA),
            success: function(next){
                if (next.errorCode!=0){
                    showTips(next.msg);
                }else{
                    clearGrid();	//清除客户表
                    fillGrid(next.data,rsp.data);		//填充新数据

                    //翻页后的相应操作调用pageCtrl.js
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
    };


	$.ajaxSetup({async: false});
	
	//"客户"下拉菜单(调用inputSelect.js)
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
	
	//获取所有客户列表
	function getCustomerlist(){
			$.ajax({
				url: "./qryCustomerList.do", 
				type: "post", 
				data: "{}", 
				success: function(rsp){
					if (rsp.errorCode!=0){
						showTips(rsp.msg);
					}else{
						customerlist = rsp.data;	//获取到的客户列表放到全局变量中
					}
				}, 
				error: function(){
					showTips("请求出错了");
				}, 
				dataType: "json"
			});
	}
	
	//初始化函数
	function init(){
		if (allData)
		{
			//登录用户信息
			if ("curLogin" in allData)
			{
				var info = $(".acount_info");
				info.html(allData.curLogin.Name+"["+allData.curLogin.CName+"]");
				info.attr("data", allData.curLogin.ID);

				var logo = $("#systemLogo");
				logo.attr("src",allData.curLogin.SystemLogo );
				logo = $("#systemName");
				logo.text(allData.curLogin.SystemName);
			}
			
			//读取快递代号列表
			if ("delivery" in allData)
			{
				var selectlist = $("#deliverycodeSelect");
				var selectlistB = $("#deliverycodeSelectB");
				var ln = allData.delivery.length;
				$("<option value='-1'>请选择</option>").appendTo(selectlist);
				for(var i=0; i<ln; i++){
					$("<option value='"+allData.delivery[i].id+"'>"+allData.delivery[i].name+"</option>").appendTo(selectlist);
				}
			}

			if ("menu" in allData){
				setActiveMenu("业务管理", "快递跟踪");
			}

			
			//初始化分页器
			if (typeof(resetPageCtrl)!=="undefined")
			{
				resetPageCtrl({rowCnt:0, page:0, psize:10});	//客户表
				resetPageCtrl_detail({rowCnt:0, page:0, psize:10});		//记录表
			}
		}
		//初始化日期输入框：
		var myDate = new Date();
		$("#date_time_start").val(myDate.getFullYear() + "-" + (myDate.getMonth()+1) + "-" + (myDate.getDate()-1)); 
		$("#date_time_end").val(myDate.getFullYear() + "-" + (myDate.getMonth()+1) + "-" + myDate.getDate()); 
	};
	

	
	//清除客户表
	function clearGrid(){
		selectedRow = null;
		$("#account_list tr:gt(0)").each(function(){
			$(this).remove();
		});
	}

	
	//添加行数据(客户表)
	function addGridRow(){
		var txt = "<tr> <td hidden></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td> </tr>";
		$("#account_list tbody").append(txt);
		var tr = $("#account_list tr:last");
		var cells = tr.find("td");
		$(cells[0]).addClass("tb_fix_width_short");
		$(cells[0]).html("<label hidden></label>");	//客户ID
		$(cells[1]).html("<label></label>");	//状态
		$(cells[2]).html("<label></label>");	//数量
		$(cells[3]).html("<label></label>");	//快递单号
		$(cells[4]).html("<label></label>");	//地址
        $(cells[5]).html("<label></label>");	//收货人
        $(cells[6]).html("<label></label>");	//时效
        $(cells[7]).html("<label></label>");	//历时
        $(cells[8]).html("<label></label>");	//寄件日期
        $(cells[9]).html("<label></label>");	//当前站点
        $(cells[10]).html("<label></label>");	//状态
        $(cells[11]).html("<label></label>");	//状态时间
        $(cells[12]).html("<label></label>");	//签收时间
        $(cells[13]).html("<label></label>");	//问题

		return tr;
	}

	//填充数据(客户表)
	function fillGrid(data,det){
        var ln = data.length;
        var la = det.length;

        for(var i=0; i<ln ; i++){
            if(data[i].RouteFlag == 0){
                var num0 = data[i].Qty;
            }

            if(data[i].RouteFlag == 1){
                var num1 =  data[i].Qty;
            }

            if(data[i].RouteFlag == 2){
                var num2 = data[i].Qty;
            }

            if(data[i].RouteFlag == 3){
                var num3 = data[i].Qty;
            }

            if(data[i].RouteFlag == 4){
                var num4 = data[i].Qty;
            }

        }

        for(var i=0; i<la; i++)
        {

            var tr = addGridRow();
            var label = tr.find("label");
            var record = det[i];
            var sum = data[i];
            //客户ID
            var field = $(label[0]);
//			field.attr("cid_data",record.CustomerID);	//存储客户ID
//			field.attr("dID_data",record.DeliveryID);	//存储快递ID
//			field.text(record.CustomerID);
//            if(record.Qty >= 0){
//            num = record.Qty;
//            }
//            alert(num);

            //状态
            if(record.RouteFlag==0) {
                field = $(label[1]);
                field.text("占用");
            }
            if(record.RouteFlag==1) {
                field = $(label[1]);
                field.text("流转");
            }
            if(record.RouteFlag==2) {
                field = $(label[1]);
                field.text("签收");
            }
            if(record.RouteFlag==3) {
                field = $(label[1]);
                field.text("可回收");
            }
            if(record.RouteFlag==4) {
                field = $(label[1]);
                field.text("已回收");
            }

            //数量
            if(record.RouteFlag==0) {
                field = $(label[2]);
                field.text(num0);
            }
            if(record.RouteFlag==1) {
                field = $(label[2]);
                field.text(num1);
            }
            if(record.RouteFlag==2) {
                field = $(label[2]);
                field.text(num2);
            }
            if(record.RouteFlag==3) {
                field = $(label[2]);
                field.text(num3);
            }
            if(record.RouteFlag==4) {
                field = $(label[2]);
                field.text(num4);
            }
            //快递单号
            field = $(label[3]);
            field.text(record.DeliverySheetID);

            //地址
            if(record.State != undefined && record.City != undefined && record.District != undefined) {
                field = $(label[4]);
                field.text(record.State + "" + record.City + "" + record.District + "" + record.Address);
            }

            //收货人
                if(record.LinkMan != undefined) {
                    field = $(label[5]);
                    field.text(record.LinkMan + "" + record.Mobile);
                }

            //时效
            field = $(label[6]);
            field.text(record.Span);

            //是否超时，历时减去时效
            if((record.SpanDays - record.Span)> 0){
                tr.css("font-style","italic");
            }
            //是否是问题件
            if(record.Problem != undefined){
            if(record.Problem.length>0){
                tr.css("color","red");
            }
            }
            //历时
            field = $(label[7]);
            field.text(record.SpanDays);

            //寄件日期
            var dt = new Date();
            var d  = record.BegingRouteTime;
            if(record.BegingRouteTime != null){
            dt.setTime(record.BegingRouteTime.time);
            field = $(label[8]);
            field.text((dt.getFullYear()-2000)+"-"+(dt.getMonth()+1)+"-"+d.date+" "+d.hours+":"+d.minutes+":"+d.seconds);
            }

            //当前站点
            field = $(label[9]);
            field.text(record.Position);

            //状态
            if(record.RouteFlag==0) {
                field = $(label[10]);
                field.text("占用");
            }
            if(record.RouteFlag==1) {
                field = $(label[10]);
                field.text("流转");
            }
            if(record.RouteFlag==2) {
                field = $(label[10]);
                field.text("已签收");
                tr.css("font-weight","bold");
            }
            if(record.RouteFlag==3) {
                field = $(label[10]);
                field.text("可回收");
            }
            if(record.RouteFlag==4) {
                field = $(label[10]);
                field.text("已回收");
            }

            //状态时间
            field = $(label[11]);
            var d2  = record.EndRouteTime;
            if(record.EndRouteTime != null){
                dt.setTime(record.EndRouteTime.time);
                field = $(label[11]);
                field.text((dt.getFullYear()-2000)+"-"+(dt.getMonth()+1)+"-"+d2.date+" "+d2.hours+":"+d2.minutes+":"+d2.seconds);
            }

            //签收时间
            field = $(label[12]);
            var d3  = record.FinishTime;
            if(record.FinishTime != null) {
                dt.setTime(record.FinishTime.time);
                field = $(label[12]);
                field.text((dt.getFullYear() - 2000) + "-" + (dt.getMonth() + 1) + "-" + d3.date + " " + d3.hours + ":" + d3.minutes + ":" + d3.seconds);

            }
            //问题
            field = $(label[13]);
            field.text(record.Problem);


		}


	}




    //选择的行高亮显示(ok)
	$("table").on("click","tr:gt(0)",function(){
		if (selectedRow!==this){
			if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
			
			$(this).addClass("tr_high_light");
			selectedRow = this;
		}
	});
	
	//关闭提示窗口
	$(".dialog_close").click(function(){
		$("#tips_dlg").hide();
	});
	$("#dlg_btn").click(function(){
		$("#tips_dlg").hide();
	});
	
	//快递查询
	$("#search").click(
        function(){

//        var cId = $("#customers").attr("data");      //客户ID
//        var dID = $("#deliverycodeSelect").val();      //快递ID
		//要提交的参数
		lastSearchParamA = {};
//        var cName = $.trim($("#customers").val());
//        var clength = cName.length;
//        if(clength > 0)
//		{
//			lastSearchParamA.CustomerID = parseInt(cId);
//		}
//		//快递ID
//		if(dID !== "-1")
//		{
//			lastSearchParamA.DeliveryID = parseInt(dID);
//		}
         //时间
        var label = $(selectedRow).find("label");
        var date_start = $("#date_time_start").val();		//*开始时间
        var date_end = $("#date_time_end").val();		//*结束时间
        var express = $("#deliverycodeSelect").val();  //快递
        var exNum = $("#expressNum").val();   //快递单号
        var site = $("#site").val();       //站点
        var address = $("#address").val();//地址
        var con = $("#consignee").val();//收货人
        var sel1 = $("#sel1").val();//是否签收
        var sel2 = $("#sel2").val();//是否超时
        lastSearchParamA.BeginTime = date_start;
        lastSearchParamA.EndTime = date_end;
            //快递
            if(parseInt(express) >= 0)
                lastSearchParamA.DeliveryID = parseInt(express);


            //快递单号
            if(exNum.length > 0)
                lastSearchParamA.DeliverySheetID = exNum;

            //站点
            if(site.length > 0)
                lastSearchParamA.Position = site;

            //地址
            if(address.length > 0)
                lastSearchParamA.Address = address;

            //收货人
            if(con.length > 0)
                lastSearchParamA.LinkMan = con;

            //是否签收
            if(parseInt(sel1) >= 0)
                lastSearchParamA.IsFinish = parseInt(sel1);

            //是否超时
            if(parseInt(sel2) >= 0)
                lastSearchParamA.IsOverTime = parseInt(sel2);

        var cName = $.trim($("#customers").val());
		//发送请求
		$.ajax({
            url: "./qryDeliveryTrace.do",
                type: "post",
                dataType: "json",
                data: JSON.stringify(lastSearchParamA),
                success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    add(rsp);
                    //alert(rsp.data)
                }
            },
            error: function(){
                showTips("请求出错了");
            }
        }


        );
            function add(det){
            $.ajax({
                    url: "./qryDeliveryTraceSta.do",
                    type: "post",
                    dataType: "json",
                    data: JSON.stringify(lastSearchParamA),
                    success: function(add){
                        if (add.errorCode!=0){
                            showTips(add.msg);
                        }else{
                            clearGrid();
                            fillGrid(add.data,det.data);
                            //alert(rsp.data)
                            if (typeof(resetPageCtrl)!=="undefined"){
                                resetPageCtrl(det.pageInfo);
                            }
                        }
                    },
                    error: function(){
                        showTips("请求出错了");
                    }
                }

            );
            }


	});




 //      	//判断时间是否有选择
//      	if($("#date_time_start").val() === "" || $("#date_time_end").val() === "")
//      	{
//      		showTips("请选择时间!");
//      		return;
//      	}

	
	//初始化
	init();
});

