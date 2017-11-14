/**
 * static util function;
 */
function beginWait() {
	try {
		
		var top=0;
		var left=0;
		
		var width=document.body.clientWidth;
		if (getViewportWidth()>width) {
			width=getViewportWidth();
		}
		
		var height=document.body.clientHeight;
		if (getViewportHeight()>height) {
			height=getViewportHeight();
		}
		
		var div= "<div id='divWaitOverlay' style='display:none;align: center;vertical-align: middle;border: 0px none ; margin: 0px; padding: 0px; background-color: rgb(0,0, 0); background-image: none; position: fixed; z-index: 11000;opacity: 0.5;filter: alpha(opacity=50);";
		div = div + "top: "+ top +"px ;" ;
		div = div + "left: " + left + "px;";
		div = div +" width: "+width+"px; ";
		div = div + "height: "+ height+"px;'/>";
		
		height=getViewportHeight();
		width=getViewportWidth();
		
		var imgTop =  Math.round((height - 16) /2);
		var imgLeft=  Math.round((width -66) /2);
	
		var img ="<img id='imgWaitOverlay' src='/assets/images/loading.gif' style='z-index: 11001;position:fixed;left: " + imgLeft + "px; top:" +imgTop  +"px;' />";
		
		$('body').prepend(div);
		$('body').prepend(img);
		$('#divWaitOverlay').css('display','block');
		
	} catch (e) {
		
	}
}

function endWait() {
	try {
		$('body').find('#divWaitOverlay').remove();
		$('body').find('#imgWaitOverlay').remove();
	}  catch (e) {
		
	}
}

function getViewportHeight() {

	if (window.innerHeight!=window.undefined) return window.innerHeight;
	if (document.compatMode=='CSS1Compat') return document.documentElement.clientHeight;
	if (document.body) return document.body.clientHeight; 
	return window.undefined; 
}
function getViewportWidth() {
	if (window.innerWidth!=window.undefined) return window.innerWidth; 
	if (document.compatMode=='CSS1Compat') return document.documentElement.clientWidth; 
	if (document.body) return document.body.clientWidth; 
	return window.undefined; 
}

function handleErr() {
	alert("Something wrong. Please try agian later. We sorry for this inconvenience.");
	window.location.href = "/"
}

function replaceBreakLine(txt) {
	if (txt == null || txt == undefined) return "";
	return txt.replace(/(?:\r\n|\r|\n)/g, '<br />');
}