/* 
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
var selFieldId = null;
var fieldsData = {};
var movingField = null;
var currFieldsIndex = 0;
var isModifyed = false;
var localCode = null;
var fieldGroups = null;

(function(){
	var uri = window.location.search;
	if (uri.indexOf("?")===0){
		uri = uri.substr(1);
		var params = uri.split('&');
		if (params.length>0){
			for(var i=0; i<params.length; i++){
				var str = params[i];
				var p = str.indexOf("=");
				if (p>0){
					if (str.substr(0, p)==="code"){
						localCode = str.substr(p+1);
						break;
					}
				}
			}
		}
	}
})();

function showTips(text){
	var dlg = $("#tips_dlg");
	dlg.show();

	var dbody = dlg.find(".dialog_body");
	dbody.css("left", ($(document).width()-dbody.width())/2+"px");
	dbody.css("top", "100px");

	$(".tips").html(text);
}

function save(){
	if (isModifyed){
		var img = document.getElementById("bgImage");
		var data = {};
		data.name = localCode;
		data.img = img.src;
		data.width = img.offsetWidth;
		data.height = img.offsetHeight;
		data.fields = [];

		var wksp = document.getElementById("workspace");

		var len = wksp.childNodes.length;
		for(var i=0; i<len; i++){
			var node = wksp.childNodes[i];
			if (node.id && node.id.indexOf("field_")===0){
				var field = {};
				field.name = node.id.substr(6);
				field.x = node.offsetLeft;
				field.y = node.offsetTop;
				field.w = node.offsetWidth-2;//减去边框
				field.h = node.offsetHeight-2;//减去边框
				field.fs = node.style.fontSize;
				field.fw = node.style.fontWeight;
				field.text = node.innerText;

				data.fields.push(field);
			}
		}

		$.ajax({
			url:"./savePrintDelivery.do", 
			type:"post",
			dataType: "json",
			data:JSON.stringify(data), 
			success:onSaveRsp, 
			error:function(xhr, msg){
				showTips(msg);
			}
		});
	}else{
		showTips("没有修改，不需保存", true);
	}
}

function onSaveRsp(rsp){
	if (rsp.errorCode!==0) showTips(rsp.msg);
	else{
		isModifyed = false;
		showTips("保存成功！");
	}
}

function clearFields(){
	var wksp = document.getElementById("workspace");
	var img = document.getElementById("bgImage");
	img.style.visibility = "hidden";
	img.src = "";
	//删除所有子节点
	while(wksp.firstChild) wksp.removeChild(wksp.firstChild);
	//还原底图节点
	wksp.appendChild(img);

	selFieldId = null;
	//清除所有复选框的选择状态
	var wksp1 = document.getElementById("workspace1");
	var checks = wksp1.getElementsByTagName("input");
	for(var i=0; i<checks.length; i++){
		var chk = checks[i];
		if (chk.type==="checkbox"){
			chk.checked = false;
		}
	}
}

function resetDesigner(rsp){
	clearFields();

	var img = document.getElementById("bgImage");
	if (!img.onload){
		img.onload = function(){
			var maxWidth = 800, maxHeight = 540;
			
			if (this.naturalWidth < maxWidth){
				this.parentNode.style.width = this.naturalWidth+"px";
			}
			
			var h = Math.max(maxHeight, this.offsetHeight);
			var h1 = h-26*fieldGroups.length;
			
			document.getElementById("panel").style.height = (h+41)+"px";
			for(var i=0; i<fieldGroups.length; i++){
				document.getElementById("fields_"+i).style.height = h1+"px";
			}
		};
	}
	img.style.visibility = "visible";
	img.src = rsp.img;

	var len = rsp.fields.length;
	for(var i=0; i<len; i++){
		var field = rsp.fields[i];
		if (field){
			var node = createField("field_"+field.name, field.text, field.x, field.y, field.w, field.h, parseInt(field.fs), parseInt(field.fw));

			var check = document.getElementById(field.name);
			if (check) check.checked = true;
		}
	}
}

function setSelectValue(selObj, value){
	var len = selObj.options.length;
	for(var i=0; i<len; i++){
		if (selObj.options[i].value==value){
			selObj.options[i].selected = true;
			break;
		}
	}
}

function unSelField(){
	if (selFieldId){
		var field = document.getElementById(selFieldId);
		field.style.background = "white";
	}
	selFieldId = null;
}

function onSelField(){
	if (selFieldId){
		var field = document.getElementById(selFieldId);
		field.style.background = "#b1dcfb";

		var fs = parseInt(field.style.fontSize);
		setSelectValue(document.getElementById("fieldFontSize"), fs);

		if (parseInt(field.style.fontWeight)<800){
			document.getElementById("fieldFontWeight").checked = false;
		}else{
			document.getElementById("fieldFontWeight").checked = true;
		}
	}
}

function createField(id, text, x, y, w, h, fs, fw){
	var field = document.createElement("div");
	field.id = id;
	field.style.position = "absolute";
	field.style.left = x+"px";
	field.style.top = y+"px";
	field.style.width = w+"px";
	field.style.height = h+"px";
	field.style.lineHeight = (parseInt(fs)+4)+"px";
	field.style.border = "1px dashed darkred";
	field.style.background = "white";
	field.style.color = "darkred";
	field.style.fontSize = fs+"px";
	field.style.fontWeight = fw;
	field.style.overflow = "hidden";
	field.innerHTML = text;

	field.onmousedown = function(event){
		if (!event) event = window.event;

		if (selFieldId){
			document.getElementById(selFieldId).style.background = "white";
		}
		selFieldId = this.id;

		var left = this.offsetLeft+this.parentNode.offsetLeft;
		var top = this.offsetTop+this.parentNode.offsetTop;

		fieldsData[this.id] = [0,0];
		movingField = this.id;

		if (event.pageX){
			fieldsData[this.id][0] = event.pageX-left;
			fieldsData[this.id][1] = event.pageY-top;
		}else{
			fieldsData[this.id][0] = event.clientX-left;
			fieldsData[this.id][1] = event.clientY-top;
		}

		if (event.stopPropagation) event.stopPropagation();
		else event.cancelBubble = true;

		onSelField();
	};

	document.getElementById("workspace").appendChild(field);

	var drager = document.createElement("div");
	drager.id = id+"_drager";
	drager.style.position = "absolute";
	drager.style.left = (w-12)+"px";
	drager.style.top = (h-12)+"px";
	drager.style.width = "12px";
	drager.style.height = "12px";
	drager.style.background = "url(./images/drager.png) no-repeat";

	drager.onmousedown = function(event){
		if (!event) event = window.event;

		fieldsData[this.id] = [0,0,0,0];
		movingField = this.id;

		if (event.pageX){
			fieldsData[this.id][0] = event.pageX;
			fieldsData[this.id][1] = event.pageY;
		}else{
			fieldsData[this.id][0] = event.clientX;
			fieldsData[this.id][1] = event.clientY;
		}

		fieldsData[this.id][2] = this.parentNode.offsetWidth;
		fieldsData[this.id][3] = this.parentNode.offsetHeight;

		if (event.stopPropagation) event.stopPropagation();
		else event.cancelBubble = true;
	};

	field.appendChild(drager);

	return field;
}
function pickField(fname, text, checkbox){
	var checked = checkbox.checked;

	fname = "field_"+fname;
	var field = document.getElementById(fname);
	if (!field && checked){
		createField(fname, text, 150, 150, 200, 20, 12, 400);

		isModifyed = true;
	}else if (field && checked===false){
		document.getElementById("workspace").removeChild(field);
		if (selFieldId==fname){
			selFieldId = null;
		}

		isModifyed = true;
	}
}
function moveField(event){
	if (movingField){
		if (!event) event = window.event;
		//阻止默认行为
		if (event.preventDefault) event.preventDefault();
		else event.returnValue = false;

		var x = 0, y = 0;
		if (event.pageX){
			x = event.pageX;
			y = event.pageY;
		}else{
			x = event.clientX;
			y = event.clientY;
		}

		var p = movingField.indexOf("_drager");
		if (p>0 && p===movingField.length-7){//是缩放
			var drager = document.getElementById(movingField);
			var field = document.getElementById(movingField.substr(0, p));

			var w = fieldsData[movingField][2]+x-fieldsData[movingField][0];
			var h = fieldsData[movingField][3]+y-fieldsData[movingField][1];

			field.style.width = (w<20?20:w)+"px";
			field.style.height = (h<20?20:h)+"px";

			drager.style.left = (field.offsetWidth-14)+"px";
			drager.style.top = (field.offsetHeight-14)+"px";
		}else{//是移动
			var field = document.getElementById(movingField);
			field.style.left = (x-fieldsData[movingField][0]-field.parentNode.offsetLeft)+"px";
			field.style.top = (y-fieldsData[movingField][1]-field.parentNode.offsetTop)+"px";
		}

		isModifyed = true;
	}
}
function endMoveField(event){
	if (movingField){
		delete fieldsData[movingField];
		movingField = null;
	}
}

function microMoveField(event){
	if (selFieldId){
		if (!event) event = window.event;
		
		var key = 0;
		
		if (typeof(event.keyCode)!=="undefined"){
			key = event.keyCode;
		}else if (typeof(event.which)!=="undefined"){
			key = event.which;
		}
		
		var x = 0, y = 0;
		
		if (key==37) x = -1;//left
		else if (key==38) y = -1;//up
		else if (key==39) x = 1;//right
		else if (key==40) y = 1;//down
		
		if (x!==0 || y!==0){
			if (event.preventDefault) event.preventDefault();
			else event.returnValue = false;
			
			var field = document.getElementById(selFieldId);
			field.style.left = (parseInt(field.style.left)+x)+"px";
			field.style.top = (parseInt(field.style.top)+y)+"px";
			
			isModifyed = true;
		}
	}
}

function showFields(index){
	/*if (index!==currFieldsIndex){
		if (currFieldsIndex!==-1){
			$("#fields_"+currFieldsIndex).hide();
			$("#fp_"+currFieldsIndex).find("span").html("+&nbsp;&nbsp;");
		}*/
				
		if ($("#fp_"+index).find("span").html()=="-&nbsp;&nbsp;")
		{
			$("#fields_"+index).hide();
			$("#fp_"+index).find("span").html("+&nbsp;&nbsp;");
		}
		else{
			$("#fields_"+index).show();
			$("#fp_"+index).find("span").html("-&nbsp;&nbsp;");
		}
		
		currFieldsIndex = index;
	/*}
	else{
		$("#fields_"+index).hide();
		$("#fp_"+index).find("span").html("+&nbsp;&nbsp;");
	}*/
}

function changeFont(){
	if (selFieldId){
		var fs = document.getElementById("fieldFontSize").value;
		if (fs>0){
			var field = document.getElementById(selFieldId);
			field.style.fontSize = fs+"px";
			field.style.lineHeight = (parseInt(fs)+4)+"px";

			isModifyed = true;
		}
	}
}


function changeFontWeight(){
	if (selFieldId){
		var field = document.getElementById(selFieldId);

		var checked = document.getElementById("fieldFontWeight").checked;
		if (checked){
			field.style.fontWeight = "800";
		}else{
			field.style.fontWeight = "400";
		}

		isModifyed = true;
	}
}

function setFieldGroups(groups){
	fieldGroups = groups;
	
	var ws = $("#workspace1");

	for(var i=0; i<groups.length; i++){
		var gp = groups[i];
		$("<div id='fp_"+i+"' class=\"fieldgroup\">&nbsp;&nbsp;"+gp.text+"<span style='float:right'>"+(i>0?"+":"-")+"&nbsp;&nbsp;</span></div><div id=\"fields_"+i+"\" class=\"field_group_fields\"><div class=\"field_group_fieldwraper\"></div></div>").appendTo(ws);
		
		var fwraper = $("#fields_"+i+" .field_group_fieldwraper");
		if (fwraper.length>0){
			for(var n=0; n<gp.fields.length; n++){
				$("<div class=\"forcheck\"><input id=\""+gp.fields[n]+"\" class=\"mycheck\" type=\"checkbox\" data=\""+gp.fieldsText[n]+"\"/>&nbsp;"+gp.fieldsText[n]+"</div>").appendTo(fwraper);
			}
		}
		
		if (i>0){
			$("#fields_"+i).hide();
		}
	}
}

$(function(){
	
	if (localCode){
		$.ajax({
			url:"getPrintDeliveryProp.do",
			type:"post",
			dataType:"json",
			data:JSON.stringify({name:localCode}),
			success:function(rsp){
				if (rsp.errorCode!==0){
					showTips(rsp.msg);
				}else{
					setFieldGroups(rsp.data.fieldGroup);
					resetDesigner(rsp.data.prop);
				}
			},
			error:function(xhr, msg){
				showTips(msg);
			}
		});
	}else{
		showTips("没有指定具体模板！");
	}
	
	$(document).mousemove(moveField);
	$(document).mouseup(endMoveField);
	$(document).keydown(microMoveField);
	
	$(".dialog_close").click(function(){
		$(this).parents(".dialog").hide();
	});
	$("#dlg_btn").click(function(){
		$(this).parents(".dialog").hide();
	});
	
	$(".add_com_btn,.label_btn_short").click(function(){
		var dlg = $("#edit_com_dlg");
		dlg.show();
		
		var dbody = dlg.find(".dialog_body");
		dbody.css("left", ($(document).width()-dbody.width())/2+"px");
		dbody.css("top", "90px");
	});
	
	$("#workspace1").on("click",".fieldgroup", function(){
		showFields(parseInt($(this).attr("id").substr(3)));
	});
	
	$("#workspace1").on("click", ".mycheck", function(){
		pickField($(this).attr("id"),$(this).attr("data"), this);
	});
});



function changeAllFont(addSize){
	var wksp = document.getElementById("workspace");
	var len = wksp.childNodes.length;

	for(var i=0; i<len; i++){
		var node = wksp.childNodes[i];
		if (node.id && node.id.indexOf("field_")===0){
			var fs = parseInt(node.style.fontSize.replace("px","")) ;
			fs = fs + addSize;			
			
			node.style.fontSize = ""+fs+"px";
			node.style.lineHeight = ""+(fs+4)+"px";
		}
	}

	isModifyed = true;
}

function btnAddFontSize(){
	changeAllFont(1);
}

function btnDecFontSize(){
	changeAllFont(-1);
}

//打开自定义打印内容对话框
function btnPrintContent(){
	var data = {};

	//取得数据
	$.ajax({
		url:"./qryCustomerPrintContent.do", 
		type:"post",
		dataType: "json",
		data:JSON.stringify(data), 
		success: function(rsp){
			if (rsp.errorCode!=0){
				showTips(rsp.msg);
				return ;
			}else{
				if (rsp.data.length>=1)
				{
					$("#txtPrintContent1").val(rsp.data[0].PrintContent1);
					$("#txtPrintContent2").val(rsp.data[0].PrintContent2);
					$("#txtPrintContent3").val(rsp.data[0].PrintContent3);
					$("#txtPrintContent4").val(rsp.data[0].PrintContent4);
					$("#txtPrintContent5").val(rsp.data[0].PrintContent5);
					$("#txtPrintContent6").val(rsp.data[0].PrintContent6);
					$("#txtPrintContent7").val(rsp.data[0].PrintContent7);
					$("#txtPrintContent8").val(rsp.data[0].PrintContent8);
					$("#txtPrintContent9").val(rsp.data[0].PrintContent9);
					$("#txtPrintContent10").val(rsp.data[0].PrintContent10);
				}
			}
		}, 
		error: function(){
			showTips("请求出错!");
			return ;
		}
	});

	//得到对话框对象
	var dlg = $("#PrintContent_dialog");
	var dbody = $(dlg.find(".dialog_body"));

	//设置显示位置
	dbody.css("left", ($(document).width()- parseInt(dbody.css("width").replace("px","")))/2+"px");
	dbody.css("top",  "30px");
	
	//显示窗口
	dlg.show();
	
}

//保存自定义内容
function saveCustomerPrintContent(){
	if (!confirm("是否确定保存自定义内容？")){
		return;
	}

	var data = {};
	var c = $("#txtPrintContent1").val();
	data.PrintContent1 = c;
	c = $("#txtPrintContent2").val();
	data.PrintContent2 = c;
	c = $("#txtPrintContent3").val();
	data.PrintContent3 = c;
	c = $("#txtPrintContent4").val();
	data.PrintContent4 = c;
	c = $("#txtPrintContent5").val();
	data.PrintContent5 = c;
	c = $("#txtPrintContent6").val();
	data.PrintContent6 = c;
	c = $("#txtPrintContent7").val();
	data.PrintContent7 = c;
	c = $("#txtPrintContent8").val();
	data.PrintContent8 = c;
	c = $("#txtPrintContent9").val();
	data.PrintContent9 = c;
	c = $("#txtPrintContent10").val();
	data.PrintContent10 = c;

	$.ajax({
		url:"./saveCustomerPrintContent.do", 
		type:"post",
		dataType: "json",
		data:JSON.stringify(data), 
		success: function(rsp){
			if (rsp.errorCode!=0){
				showTips(rsp.msg);
			}else{
				alert("保存成功!");
			}
		}, 
		error: function(){
			alert("请求出错!");
		}
	});

	
}


