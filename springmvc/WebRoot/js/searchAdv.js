
$(function(){
	//var now=new Date(); 
	//alert($("#beginTime").attr("value"));
	//alert("");

	$("#search").click(function(){
		var param = {};
		param.refsheetid = $.trim($("#refsheetid").val());
		
		param.sellerFlag = $("#sellerFlag").val();
		param.timeType = $("#timeType").val();
		param.buyerNick = $.trim($("#buyerNick").val()); 
		param.linkman = $.trim($("#linkman").val()); 
		param.phone = $.trim($("#phone").val());
		param.mobile = $.trim($("#mobile").val());
		param.state = $.trim($("#state").val());
		param.address = $.trim($("#address").val());
		param.deliverySheetID = $.trim($("#deliverySheetID").val());
		param.goodsName = $.trim($("#goodsName").val());
		param.title = $.trim($("#title").val());
		param.outerSkuID = $.trim($("#outerSkuID").val());
		param.note = $.trim($("#note").val()); 
		param.buyerMemo = $.trim($("#buyerMemo").val()); 
		param.sellerMemo = $.trim($("#sellerMemo").val()); 
		param.buyerMessage = $.trim($("#buyerMessage").val()); 
		param.tradeMemo = $.trim($("#tradeMemo").val()); 
		param.color = $.trim($("#color").val());
		param.size = $.trim($("#size").val());
		
		if ($("#itemCount").val()!=""){param.itemCount = parseInt($.trim($("#itemCount").val()));}
		if ($("#itemQty").val()!=""){param.itemQty = parseInt($.trim($("#itemQty").val()));}
		if ($("#postFee").val()!=""){param.postFee = parseFloat($.trim($("#postFee").val()));}
		if ($("#totalAmount").val()!=""){param.totalAmount = parseFloat($.trim($("#totalAmount").val()));}

		param.tradeFrom = $.trim($("#tradeFrom").val()); 
		param.invoiceFlag = $("#invoiceFlag").val();
		param.payMode = $.trim($("#payMode").val());
		
		//param.payMode = $.trim($("#payMode").val());
		param.sheetID = $.trim($("#sheetID").val());
		param.beginTime = $.trim($("#beginTime").val());
		param.endTime = $.trim($("#endTime").val());
		

		var cnt = 0;
		for(var n in param){
			if (param[n].length===0){
				delete param[n];
			}else{
				cnt++;
				if (n=="beginTime"){
					param.beginTime = param.beginTime.replace(/T/, " ");
				}else if (n=="endTime"){
					param.endTime = param.endTime.replace(/T/, " ");
				}
			}
		}

		if (cnt>0){
			param.sellerFlag = parseInt(param.sellerFlag);
			param.timeType = parseInt(param.timeType);
			param.payMode = parseInt(param.payMode);
			param.invoiceFlag = parseInt(param.invoiceFlag);
			
			if(param.sellerFlag===-1) delete param["sellerFlag"];
			if(param.timeType===-1) delete param["timeType"];
			if(param.payMode===-1) delete param["payMode"];
			if(param.invoiceFlag===-1) delete param["invoiceFlag"];

			parent.advSearch(param);
		}
	});

	$("#clear").click(function(){
		$("input").each(function(){
			$(this).val("");
		});
		$("select").each(function(){
			$(this)[0].selectedIndex = 0;
		});
	});

	$("#close").click(function(){
		parent.closeSearchDlg();
	});
});