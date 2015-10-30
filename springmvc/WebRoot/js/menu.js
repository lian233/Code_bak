/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function setActiveMenu(mName, smName){
	$(".menu").find("span").each(function(){
		if ($(this).text()===mName){
			$(this).attr("data", "do-not-open-url");//设置一个值让下面的click事件不打开url和不激活子项，否则会无效循环下去。
			$(this).click();

			var id = $(this).attr("id").substr(3);
			$("#sm_"+id).find("span").each(function(){
				if ($(this).text()===smName){
					$(this).attr("data", "do-not-open-url");//设置一个值让下面的click事件不打开url，否则会无效循环下去。
					$(this).click();
					return true;
				}
			});

			return true;
		}
	});
}

$(function(){
	
	if (allData){
		if ("menu" in allData){
			var topbar = $(".top_bar");
			var miOff = $("#mi_off");
			
			var activeMenu = -1;
			var activeSubMenu = -1;
			
			for(var i=0; i<allData.menu.length; i++){
				var m = allData.menu[i];
				
				miOff.before("<span id='mi_"+i+"'>"+m.caption+"</span>");//主菜单项
				
				if (typeof(m.childs)!=="undefined"){
					topbar.after("<div class='sub_menu' id='sm_"+i+"'></div>");//二级菜单栏
					
					var sm = $("#sm_"+i);
					for(var n=m.childs.length-1; n>=0; n--){
						var mi = m.childs[n];
						
						$("<span id='smi_"+n+"'>"+mi.caption+"</span>").appendTo(sm);//二级菜单项
					}
				}
			}
			
			$(".menu").find("span").click(function(){
				var id = $(this).attr("id");//mi_0
				
				var index = parseInt(id.substr(3));//主菜单项
				var m = allData.menu[index];
				
				if (activeMenu!==-1){
					$("#mi_"+activeMenu).removeClass("active_menu");
					if (typeof(allData.menu[activeMenu].childs)!=="undefined"){
						$("#sm_"+activeMenu).hide();
					}
				}
				$(this).addClass("active_menu");
				
				var hasChild = typeof(m.childs)!=="undefined";
				if (hasChild){
					$("#sm_"+index).show();
				}
				
				var w = $(this).outerWidth();
				var l = $(this).offset().left-$(this).parent().offset().left;
				$(".active_menu_arrow").css("left", (l+w/2-8)+"px");
				
				activeMenu = index;
				activeSubMenu = -1;
				
				if ($(this).attr("data")==="do-not-open-url"){//不打开url，不激活子项
					$(this).removeAttr("data");
				}else{
					if (hasChild){//激活第一个子菜单
						$("#sm_"+index+" #smi_0").click();
					}else if (typeof(m.url)!=="undefined"){
						window.location.href = m.url;
					}
				}
			});
			
			$(".sub_menu").find("span").click(function(){
				var mi = $(this).parent().attr("id");//sm_0
				var id = $(this).attr("id");//smi_0
				
				var mInd = parseInt(mi.substr(3));//子菜单栏
				var index = parseInt(id.substr(4));//子菜单项
				var m = allData.menu[mInd].childs[index];
				
				if (activeSubMenu!==-1){
					$("#smi_"+activeSubMenu).removeClass("active_menu");
				}
				$(this).addClass("active_menu");
				
				activeSubMenu = index;
				
				if ($(this).attr("data")==="do-not-open-url"){//不打开url
					$(this).removeAttr("data");
				}else{
					if (typeof(m.url)!=="undefined"){
						window.location.href = m.url;
					}
				}
			});
		}
	}
});

