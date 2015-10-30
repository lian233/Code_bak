/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var input_select_map = {};
//全局方法，给使用者注册一个控件的相关参数
function registInputSelect(param){//param = {id:"",url:"",onload:function(){}}
	input_select_map[param.id] = param;
}
//全局方法，给使用者在控件的onload事件中调用来获得列表子项的描述
function input_select_list_item_html(data, text){
	return "<span data=\""+data+"\">"+text+"</span>";
}

$(function(){
	var timer = -1;
	
	$.ajaxSetup({async:false});//设置AJAX请求为同步请求，以防操作顺序混乱

	$("body").on("click", ".input_select_down_btn", function(){
		var list = $(this).parent().find(".input_select_list_wraper");
		list.show();
		
		$(this).parent().find(".input_select_text_field").focus();
	});

	$("body").on("focus", ".input_select_text_field", function(){
		var list = $(this).parent().next(".input_select_list_wraper");
		list.show();
	});

	$("body").on("click", ".input_select_text_field", function(){
		var list = $(this).parent().next(".input_select_list_wraper");
		list.show();
	});

	$(document).mouseup(function(e){
		if ($(e.target).attr("class")!=="input_select_list_wraper" && 
				$(e.target).parent().attr("class")!=="input_select_list_wraper"){
			$(".input_select_list_wraper").hide();
		}
	});

	$("body").on("keydown", ".input_select_text_field", function(){
		var _this = this;
		
		var id = $(this).attr("id");
		if (typeof id !== "undefined"){
			var param = input_select_map[id];
			if (param){
				if (timer!==-1) clearTimeout(timer);
				
				timer = setTimeout(function(){
					var list = $(_this).parent().next(".input_select_list_wraper");
					list.html("");

					$(_this).attr("data", "");

					var kw = $.trim($(_this).val());
					if (kw.length>0){
						$.ajax({
							url: param.url, 
							type: "post",
							data: JSON.stringify({key:kw}), 
							success: function(rsp){
								if (rsp.errorCode!=0){
									showTips(rsp.msg);
								}else{
									var itemHtmls = [];
									param.onload(rsp.data, itemHtmls);

									for(var i=0; i<itemHtmls.length; i++){
										$(itemHtmls[i]).appendTo(list);
									}
								}
							}, 
							error: function(){
								showTips("请求出错了");
							}, 
							dataType: "json"
						});
					}
				}, 1000);
			}
		}
	});

	$("body").on("click", ".input_select_list_wraper", function(e){
		var _this = this;
		var target = e.target;

		$(this).find("span").each(function(index, ele){
			if (target===ele){
				var data = $(this).attr("data");
				var text = $(this).text();

				var input = $(_this).parent().find(".input_select_text_field");
				input.val(text);
				input.attr("data", data);

				$(_this).hide();
				return false;//stop each.
			}
		});
	});
});
