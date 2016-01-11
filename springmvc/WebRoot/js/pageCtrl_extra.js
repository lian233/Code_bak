/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var currPageInfo_detail = null;
var page_ctrl_to_callback_detail = null;

function resetPageCtrl_detail(pi){
	if (pi.page!==0) return;

	currPageInfo_detail = pi;

	var rc = parseInt(pi.rowCnt);
	var ps = parseInt(pi.psize);
	var pageCnt = Math.ceil(rc/ps);//总页数

	currPageInfo_detail.pageCnt = pageCnt;

	$(".page_num_detail span").each(function(index,ele){//先显示全部页码
		if (index<2){
			$(this).hide();
		}else if (index>=2 && index<=11){
			$(this).text(index-1);
			
			if (index===2 && pageCnt===0) $(this).show();
			else if (index-1>pageCnt) $(this).hide();
			else $(this).show();
		
			if (index===2) $(this).css("background", "rgb(220,220,220)");
			else $(this).css("background", "none");
		}else{
			if (index===13) $(this).text(pageCnt);
			
			if (pageCnt<=10) $(this).hide();
			else $(this).show();
		}
	});
}

function afterChangedPage_detail(pi){
	var lp = $(".page_num_detail span:contains('"+(currPageInfo_detail.page+1)+"')").map(function(){
		if ($(this).text()===""+(currPageInfo_detail.page+1)) return this;
	});
	lp.css("background", "none");
	
	var p = $(".page_num_detail span:contains('"+(pi.page+1)+"')").map(function(){
		if ($(this).text()===""+(pi.page+1)) return this;
	});
	p.css("background", "rgb(220,220,220)");
	
	currPageInfo_detail = pi;
	currPageInfo_detail.pageCnt = Math.ceil(pi.rowCnt/pi.psize);
}

$(function(){
	var beginPage = 0;
	
	function prevPageGroup(){
		if (beginPage-10>=0){
			beginPage -= 10;
			
			$(".page_num_detail span").each(function(index, ele){
				if (index<2){
					if (beginPage===0) $(this).hide();
				}else if (index>=2 && index<12){
					var pn = beginPage+index-1;
					$(this).text(pn);
					
					if (pn===currPageInfo_detail.page+1) $(this).css("background", "rgb(220,220,220)");
					else $(this).css("background", "none");
					
					$(this).show();
				}else{
					$(this).show();
				}
			});
		}
	}
	
	function nextPageGroup(){
		if (beginPage+10<currPageInfo_detail.pageCnt-1){
			beginPage += 10;
			
			$(".page_num_detail span").each(function(index, ele){
				if (index<2){
					$(this).show();
				}else if (index>=2 && index<12){
					var pn = beginPage+index-1;
					$(this).text(pn);
					if (pn>currPageInfo_detail.pageCnt) $(this).hide();
					
					if (pn===currPageInfo_detail.page+1) $(this).css("background", "rgb(220,220,220)");
					else $(this).css("background", "none");
				}else{
					if (beginPage+10>=currPageInfo_detail.pageCnt) $(this).hide();
				}
			});
		}
	}
	
	$(".page_num_detail span").click(function(){
		var pn = $(this).text();
		if (pn===".."){
			prevPageGroup();
		}else if (pn==="..."){
			nextPageGroup();
		}else if (page_ctrl_to_callback_detail){
			var pn = parseInt(pn)-1;
			if (pn!==currPageInfo_detail.page){
				page_ctrl_to_callback_detail(pn);
			}
		}
	});
});
