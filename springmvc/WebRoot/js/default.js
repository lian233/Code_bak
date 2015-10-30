// JavaScript Document
$.fn.serializeObject = function() {     
    var o = {};     
    var a = this.serializeArray();     
    $.each(a, function() {     
      if (o[this.name]) {     
        if (!o[this.name].push) {     
          o[this.name] = [ o[this.name] ];     
        }     
        o[this.name].push(this.value || '');     
      } else {     
        o[this.name] = this.value || '';     
      }     
    });     
    return o;     
};

$(function(){
	$("#loginbtn").click(function(){
		//检查用户名与密码是否为空
		if($("#username").val() == "" || $("#password").val() == "")
		{
			alert("用户名和密码不能为空!");
			return;
		}
		
		var datasent = $("#loginForm").serializeObject();
		var params = JSON.stringify(datasent);
		//alert(params);
		$.ajax({
			type : "POST",
			url : "login.do",
			dataType:'json',
			contentType:"application/json;charset=UTF-8",
			data:params,
			async : false,
			success:function(data) {
				if(data.errorCode == 0){   //登录成功
					window.location.href = "home.html";
				}else{
					alert("登录失败："+ data.msg);
				}

			},error:function(data){
				var ss=eval(data);
				//alert(data.toString());
				alert(JSON.stringify(ss));
				//alert(data.flag);
			}
		});
	});
});

