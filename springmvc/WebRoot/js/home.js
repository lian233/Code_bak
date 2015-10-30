/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

if (typeof allData === "undefined") allData = {};//{curLogin:{ID:1,Name:"admin",CName:"超级管理员",CustomerID:1},part:[{ID:1,Name:'role1'},{ID:2,Name:'role2'},{ID:3,Name:'role3'},{ID:4,Name:'role4'}]};



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


	function init(){
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

			if ("menu" in allData){
				setActiveMenu("首页");
			}
			
		}

		$.ajax({
			url: "./getCustomerBaseSta.do",
			type: "post",
			data: {},
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					
					if (parent.allData.curLogin.SystemType != "1"){
						$("#lblAccount").hide();
						$("#lblUseAccount").hide();
						$("#lblCreditAccount").hide();
					}

					$("#account").html(rsp.data[0].Account);
					$("#useAccount").html(rsp.data[0].UseAccount);
					$("#creditAccount").html(rsp.data[0].CreditAccount);
					$("#decCount").html(rsp.data[0].DecCount);
					$("#orderCount").html(rsp.data[0].OrderCount);
					$("#checkCount").html(rsp.data[0].CheckCount);

				}
			},
			error: function(){
				showTips("请求出错了");
			},
			dataType: "json"
		});


	};
	
	$.ajaxSetup({async: false});//设置AJAX请求为同步请求，以防操作顺序混乱
	
	init();
	

});
