/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var indexObj = null;

function fillShopList(shops){
	var select = $("#all_shop");
	var ln = shops.length;
	for(var i=0; i<ln; i++){
		$("<option value='"+shops[i].id+"'>"+shops[i].name+"</option>").appendTo(select);
	}
}

function fillBillList(bills){
	var row = "<tr><td><input type='checkbox' class='checkbox item'/></td><td>%customer%</td><td>%name%</td><td>%name%</td><td>%name%</td><td>%name%</td></tr>";
	var ln = bills.length;
	
	for(var i=0; i<ln; i++){
		var b = bills[i];
		var str = row.replace(/%customer%/, b.customer);
		
		$("tbody tr:last").after(str);
	}
}

function getCheckedItems(){
	var rt = [];
	
	if ($("#allcheck").attr("checked")){
		$(".checkbox .item").each(function(index, ele){
			if (ele.attr("checked")){
				rt.push(index);
			}
		});
	}
	
	return rt;
}

$(function(){
	
	$(".menu").find("span").click(function(e){
		switch(e.target.id){
			case "mi_0"://55
				window.location.href = "home.html";
				break;
			case "mi_1"://145
				window.location.href = "index.html";
				break;
			case "mi_2"://267
				window.location.href = "receiptConfig.html";
				break;
			case "mi_3"://373
				window.location.href = "privilege.html";
				break;
			case "mi_4"://451
				break;
		}
	});
	
	$("#adv_search").click(function(){
		var dlg = $("#dialog");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "30px");
	});
	
	$(".dialog_close").click(function(){
		$("#dialog").hide();
	});
	
	$("#allcheck").click(function(){
		if ($(this).attr("checked")){
			$(".checkbox .item").each(function(){
				$(this).attr("checked", true);
			});
		}else{
			$(".checkbox .item").each(function(){
				$(this).attr("checked", false);
			});
		}
	});
	
	if (indexObj){//初始化页面数据
		if ("shops" in indexObj){
			fillShopList(indexObj.shops);
		}else if ("bills" in indexObj){
			fillBillList(indexObj.bills);
		}
	}
});
