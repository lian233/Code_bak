
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
				setActiveMenu("修改密码");
			}
		}

	};
	
	init();
	$("#ok_btn").click(function(){
		var oriPassword = $.trim($("#txtOriPassword").val());
		if (oriPassword=="")
		{
			showTips("请输入原密码！");
			return ;
		}

		var newPassword = $.trim($("#txtNewPassword").val());
		if (newPassword=="")
		{
			showTips("请输入新密码！");
			return;
		}

		var confirmPassword = $.trim($("#txtConfirmPassword").val());
		if (newPassword != confirmPassword)
		{
			showTips("新密码和确认密码不一致！");
			return;
		}


		$.ajax({
			url: "./modifyPasswd.do", 
			type: "post", 
			data: JSON.stringify({password:newPassword,oriPassword:oriPassword}), 
			success: function(rsp){
				if (rsp.errorCode!=0){
					showTips(rsp.msg);
				}else{
					showTips("修改成功！");
				}
			}, 
			error: function(){
				showTips("请求出错了");
			}, 
			dataType: "json"
		});
	});
	
	$(".dialog_close").click(function(){
		$("#tips_dlg").hide();
	});
	$("#dlg_btn").click(function(){
		$("#tips_dlg").hide();
	});
	
	$(".dlg_Close_btn").click(function(){
		$(this).parents(".dialog").hide();
	});


	
});
