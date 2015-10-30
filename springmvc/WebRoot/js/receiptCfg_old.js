/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
if (typeof allData === "undefined") allData = {};//{delivery:[{"LocalCode":"HTKY","Name":"汇","ID":1,"Code":"HTKY"}, {"LocalCode":"YTO","Name":"圆","ID":2,"Code":"YTO"}]};

$(function(){
	
	function init(){
		if (allData){
			if ("curLogin" in allData){
				var info = $(".acount_info");
				info.html(allData.curLogin.Name+"["+allData.curLogin.CName+"]");
				info.attr("data", allData.curLogin.ID);
			}

			if ("delivery" in allData){
				var list = allData.delivery;
				var ln = list.length;
				
				for(var i=0; i<ln; i++){
					addGridRow(list[i], i+1);
				}
			}
		}
	}
	
	function addGridRow(data, ind){
		var txt = "<tr><td></td><td></td><td></td><td></td><td></td><!--td></td--><td></td></tr>";
		$("#receipt_list tr:last").before(txt);
		
		var tr = $("#receipt_list tr:last").prev();
		var cells = tr.find("td");
		
		$(cells[0]).addClass("tb_fix_width_small");
		$(cells[1]).html(ind);
		$(cells[2]).html(data.Name);
		$(cells[3]).html(data.LocalCode);
		$(cells[4]).addClass("tb_fix_width");
		$(cells[4]).html("<img class='logist_pic' src='./deliveryImg/"+data.LocalCode+".jpg'/>");
		//$(cells[5]).addClass("tb_fix_width_short");
		//$(cells[5]).html("<img class='move_icon' src='./images/go-up.png'/><img class='move_icon' src='./images/go-down.png'/>");
		$(cells[5]).addClass("tb_fix_width");
		$(cells[5]).html("<div class='button edit_btn' data='"+data.LocalCode+"'>编辑</div>");
		
		return tr;
	}
	
	function showTips(text){
		var dlg = $("#tips_dlg");
		dlg.show();

		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "100px");

		$(".tips").html(text);
	}
	
	$.ajaxSetup({async: false});//设置AJAX请求为同步请求，以防操作顺序混乱
	
	init();
	
	$(".menu").find("span").click(function(e){
		switch(e.target.id){
			case "mi_0":
				window.location.href = "home.html";
				break;
			case "mi_1":
				window.location.href = "logins.html";
				break;
			case "mi_2":
				window.location.href = "customerManage.html";
				break;
			case "mi_3":
				window.location.href = "shopManager.html";
				break;
			case "mi_4":
				window.location.href = "expressManager.html";
				break;
			case "mi_5":
				window.location.href = "order.html";
				break;
			case "mi_6":
				window.location.href = "receiptConfig.html";
				break;
			case "mi_7":
				window.location.href = "default.html";
				break;
		}
	});				
	
	$("table").on("click", ".edit_btn", function(){
		var dlg = $("#dialog");
		var dbody = dlg.find(".dialog_body_editor");
		
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "10px");
		
		dlg.show();
		
		$("#designer").attr("src", "./receiptsPrintDesigner.html?code="+encodeURIComponent($(this).attr("data")));
	});

	$(".dialog_close").click(function(){
		$(this).parents(".dialog").hide();
		
		if ($(this).parents(".dialog").attr("id")==="dialog"){
			$("#designer").attr("src", "about:blank");
		}
	});

});
