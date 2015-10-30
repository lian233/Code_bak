/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$(function(){
	var draggingObj = null;
	var x, y;
	
	$(".dialog_body").mousedown(function(e){
		draggingObj = this;
		
		x = e.clientX-this.offsetLeft;
		y = e.clientY-this.offsetTop;
		if (y>=35)
		{
			return true;
		}
		if (this.setCapture) this.setCapture();
		
		return false;
	});
	
	$(document).mousemove(function(e){
		if (draggingObj){
			var ox = e.clientX-x;
			var oy = e.clientY-y;
			
			$(draggingObj).css({"left":ox+"px", "top":oy+"px"});
			return false;
		}
	});
	
	$(document).mouseup(function(e){
		if (draggingObj){
			if (draggingObj.releaseCapture) draggingObj.releaseCapture();
			
			draggingObj = null;
			e.cancelBubble = true;
		}
	});
});
