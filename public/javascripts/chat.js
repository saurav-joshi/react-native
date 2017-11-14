$(document).ready(function() {
   loadSwipe();
   $('#message-text').focus();

   $('#message-text').keyup(function(e){
       if(e.keyCode == 13) {
       	sendQuestion(-1,-1);
       }
   });
   
   loadConversations();
});
var itemswait = ["Just a sec...", "I'm on it!", "Wait lah"];
function gotoPage(page) {
	window.location.href=page;
}
function loadSwipe(){
	$("div[id^='restaurant-suggests-']").swiperight(function() {  
		$(this).carousel('prev');  
	});  
	$("div[id^='restaurant-suggests-']").swipeleft(function() {  
		$(this).carousel('next');  
	});
}

function sendQuestion() {
	sendQuestion(-1, -1);
}
function sendQuestion(rid, bookOpt) {
	i++;
	if ($("#message-text").val() == "" && bookOpt <= 1) {
		$('#message-text').focus();
		return;
	}
	$('#message-text').prop('disabled', true);
	$('#btn-send').prop('disabled', true);
	disableSuggestionBtn();
	
	var html = "";
	if (bookOpt <= 1) {
		html += '<div class="row msg_container base_sent">';
		html += '<div class="messages_wrapper">';
		html += '<div class="messages msg_sent">';
		html += '<p>'+emoji.replace_colons($("#message-text").val())+'</p>';
		html += '</div>';
		html += '<div class="avatar avatar_sender">';
		html += '<img src="/assets/images/img/user.png" class="img-responsive">';
		html += '</div>';
		html += '</div>';
		html += '</div>';
	}
	html += '<div class="row msg_container base_receive">';
	html += '<div class="messages_wrapper">';
	html += '<div class="avatar avatar_receive">';
	html += '<img src="/assets/images/img/iaasimov-4.png" class="img-responsive">';
	html += '</div>';
	html += '<div class="messages msg_receive">';
	html += '<p id="analysing-id-'+i+'">'+(itemswait[Math.floor(Math.random()*itemswait.length)])+'&nbsp;<img src="../assets/images/loading.gif"></p>';
	html += '</div>';
	html += '</div>';
	html += '</div>';
	$('#container_bubble').append(html);
	gotoBottom();
	
	$.ajax({
	    url: "/chatbot/sendQuestion",
	    data: { question: $("#message-text").val(), pid: i , rid: rid, bookOpt: bookOpt},
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	if (data) {
	    		generateResponseData(data);
	    	}
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	handleErr();
	    }
	});
}

function generateResponseData(data) {
	var html = "";
	var resp = jQuery.parseJSON(data.value);
	if (resp.error && resp.error != null) {
		$("#analysing-id-"+data.pid).html(resp.error);
		enableSendText();
		return;
	}
	var msgtxt = resp.message;
	if (msgtxt == null) {
		msgtxt = "";
	}
	if (resp.warningMessage && resp.warningMessage != null) {
		html = "";
		html += '<div class="row msg_container base_receive">';
		html += '<div class="messages_wrapper">';
		html += '<div class="avatar avatar_receive">';
		html += '<img src="/assets/images/img/iaasimov-4.png" class="img-responsive">';
		html += '</div>';
		html += '<div class="messages msg_receive">';
		html += '<p>'+replaceBreakLine(resp.warningMessage)+'</p>';
		html += '</div>';
		html += '</div>';
		html += '</div>';
//		$('#container_bubble').append(html);
		$("#container_bubble .base_receive:last").before(html);
	}
	
	$("#analysing-id-"+data.pid).html(emoji.replace_colons(replaceBreakLine(msgtxt)));
	
	if (resp.confirmBooking && resp.bookingInfo != null) {
		$("#analysing-id-"+data.pid).after('<p style="text-align: center;"><a class="btn btn-lg btn-primary" href="#" onclick="booking(\'\', \''+resp.bookingInfo.restaurantId+'\', 2)" role="button" style="padding: 5px; font-size: 14px;">Confirm</a><a class="btn btn-info" href="#" onclick="booking(\'\', \''+resp.bookingInfo.restaurantId+'\', 3)" role="button" style="padding: 5px; font-size: 14px;margin-left: 20px;">Cancel</a></p>');
		$("#analysing-id-"+data.pid).after('<p><b>Special Request: </b>'+resp.bookingInfo.specialRequest+'</p>');
		$("#analysing-id-"+data.pid).after('<p><b>Email: </b>'+resp.bookingInfo.email+'</p>');
		$("#analysing-id-"+data.pid).after('<p><b>Phone: </b>'+resp.bookingInfo.phone+'</p>');
		$("#analysing-id-"+data.pid).after('<p><b>Pax: </b>'+resp.bookingInfo.pax+'</p>');
		$("#analysing-id-"+data.pid).after('<p><b>Time: </b>'+resp.bookingInfo.time+""+'</p>');
		$("#analysing-id-"+data.pid).after('<p><b>Date: </b>'+resp.bookingInfo.date+""+'</p>');
	}
	
	if (resp.resultRestaurants != null) {
		html = "";
		html += '<div id="restaurant-suggests-'+data.cid+'" class="carousel slide" data-ride="carousel" style="display: none;">';
		html += '<ol class="carousel-indicators" style="margin-left: -20%; width: 40%;">';
		var j = 0;
		for (var o in resp.resultRestaurants) {
			html += '<li data-target="#restaurant-suggests-'+data.cid+'" data-slide-to="'+j+'" class="'+(j==0?"active":"")+'"></li>';
			j++;
		}
		html += '</ol>';
		html += '<div class="carousel-inner" role="listbox">';
		var isFirst = true;
		for (var o in resp.resultRestaurants) {
    		html += '<div class="item '+(isFirst?"active":"")+'">';
    		html += '<div class="container">';
    		html += '<div class="carousel-caption" style="cursor: pointer;">';
    		html += '<h3 style="color: #009AFF; max-height: 30px; overflow: hidden;" onclick="gotoPage(\'/chatbot/restaurant/'+data.cid+'/'+resp.resultRestaurants[o].id+'\')">'+resp.resultRestaurants[o].name+'</h3>';
    		html += '<p style="color: black; max-height: 20px; overflow: hidden;">'+resp.resultRestaurants[o].address+'</p>';
    		if (resp.resultRestaurants[o].offers != null && resp.resultRestaurants[o].offers.length > 0) {
    			html += '<p style="color: red; max-height: 20px; overflow: hidden; margin-bottom: 0;">'+resp.resultRestaurants[o].offers[0].short_desc+'&nbsp;(Exp: '+resp.resultRestaurants[o].offers[0].end_date+')</p>';
    		}
    		if (resp.resultRestaurants[o].image != null) {
    			html += '<p style="overflow: hidden;" onclick="gotoPage(\'/chatbot/restaurant/'+data.cid+'/'+resp.resultRestaurants[o].id+'\')"><img alt="Loading..." style="max-height: 125px;" src="https://dpimages.crayondata.com/high-res-image/'+resp.resultRestaurants[o].image+'"></p>';
    		} else {
    			html += '<p style="overflow: hidden;" onclick="gotoPage(\'/chatbot/restaurant/'+data.cid+'/'+resp.resultRestaurants[o].id+'\')"><img alt="Loading..." style="max-height: 125px;" src="/assets/images/img/no-image-available.jpg"></p>';
    		}
    		html += '<p><a class="btn btn-lg btn-primary" href="#" onclick="gotoPage(\'/chatbot/restaurant/'+data.cid+'/'+resp.resultRestaurants[o].id+'\')" role="button" style="padding: 5px; font-size: 14px;">Locate it!</a>';
    		if (resp.resultRestaurants[o].allowBooking || resp.resultRestaurants[o].chope_link != null) {
    			//if (apiUrl != "") {
				if (resp.resultRestaurants[o].chope_link != undefined && resp.resultRestaurants[o].chope_link != null) {
    				html += '&nbsp;<a class="btn btn-lg btn-success" href="'+resp.resultRestaurants[o].chope_link+'" target="_blank" role="button" style="padding: 5px; font-size: 14px;">Book it!</a>';
    			} else {
    				html += '&nbsp;<a class="btn btn-lg btn-success" href="#" onclick="booking(\'booking restaurant: '+resp.resultRestaurants[o].name+'\', \''+resp.resultRestaurants[o].id+'\', 1)" role="button" style="padding: 5px; font-size: 14px;">Book it!</a>';
    			}
    		}
    		html += "</p>";
    		var mstar = resp.resultRestaurants[o].mstar;
    		if (mstar == null || mstar == "") {
    			mstar = "blank";
    		}
    		var likeImg = "like-gray.png";
    		if (restIds.indexOf(resp.resultRestaurants[o].id+",") >= 0) {
    			likeImg = "like-green.png";
    		}
    		html += '<p><table width="100%"><tr><td style="text-align: left; width: 50%; padding-left: 10px;"><img src="/assets/images/img/'+likeImg+'" height="30px" onclick="changeLikeBtn(this,'+data.cid+','+resp.resultRestaurants[o].id+')"></td><td style="text-align: right; padding-right: 10px;"><img src="/assets/images/img/'+mstar+'.png" height="30px"></td></tr></table></p>';
    		if (resp.resultRestaurants[o].offers != null && resp.resultRestaurants[o].offers.length > 0) {
    			html += '<p style="position: absolute; right: 0; top: 0;"><img src="/assets/images/img/specialdeals.png" width="80px"></p>';
    		}
    		html += '</div>';
    		html += '</div>';
    		html += '</div>';
    		isFirst = false;
		}
		html += '</div>';
		html += '<a class="left carousel-control" href="#restaurant-suggests-'+data.cid+'" role="button" data-slide="prev">';
		html += '<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>';
		html += '<span class="sr-only">Previous</span>';
		html += '</a>';
		html += '<a class="right carousel-control" href="#restaurant-suggests-'+data.cid+'" role="button" data-slide="next">';
		html += '<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>';
		html += '<span class="sr-only">Next</span>';
		html += '</a>';
		html += '</div>';
	    $('#container_bubble').append(html);
	    $('#restaurant-suggests-'+data.cid).fadeIn();
	    loadSwipe();
	}
	if (resp.suggestion && resp.suggestion != null) {
		html = "";
		html += '<div class="row msg_container base_receive">';
		html += '<div class="messages_wrapper">';
		html += '<div class="avatar avatar_receive">';
		html += '<img src="/assets/images/img/iaasimov-4.png" class="img-responsive">';
		html += '</div>';
		html += '<div class="messages msg_receive">';
		html += '<p>'+resp.suggestion.message+'</p>';
		for (var o in resp.suggestion.options) {
			if (resp.suggestion.options[o] == "SHARE_LOCATION") {
				html += '<p><button type="button" class="btn btn-info suggestion-btn" onclick="shareLocation()">Share</button></p>';
			} else {
				html += '<p><button type="button" class="btn btn-info suggestion-btn" onclick="useSuggest(\''+resp.suggestion.options[o]+'\')">'+resp.suggestion.options[o]+'</button></p>';
			}
		}
		html += '</div>';
		html += '</div>';
		html += '</div>';
		 $('#container_bubble').append(html);
	}
	gotoBottom();
	enableSendText();
}

function shareLocation() {
	isClickShareLocation = true;
	navigator.geolocation.getCurrentPosition(successGEO, errorGEO);
	//getLatLon();
}

function enableSendText() {
	$('#message-text').prop('disabled', false);
	$('#btn-send').prop('disabled', false);
	$("#message-text").val("");
	$('#message-text').focus();
}

var restIds = "";
function loadConversations() {
	$.ajax({
	    url: "/chatbot/loadMessages",
	    data: {  },
	    dataType: "json",
	    type: "GET",
	    success: function(data) {
	    	if (data) {
	    		restIds = data.liked;
	    		$('#container_bubble').html("");
	    		for (var o in data.conversation) {
	    			var html = "";
	    			if (data.conversation[o].question != "") {
	    				html += '<div class="row msg_container base_sent">';
		    			html += '<div class="messages_wrapper">';
		    			html += '<div class="messages msg_sent">';
		    			html += '<p>'+emoji.replace_colons(data.conversation[o].question)+'</p>';
		    			html += '</div>';
		    			html += '<div class="avatar avatar_sender">';
		    			html += '<img src="/assets/images/img/user.png" class="img-responsive">';
		    			html += '</div>';
		    			html += '</div>';
		    			html += '</div>';
	    			}
	    			var resp = jQuery.parseJSON(data.conversation[o].answer);
	    			if (resp.error && resp.error != null) continue;
	    			html += '<div class="row msg_container base_receive">';
	    			html += '<div class="messages_wrapper">';
	    			html += '<div class="avatar avatar_receive">';
	    			html += '<img src="/assets/images/img/iaasimov-4.png" class="img-responsive">';
	    			html += '</div>';
	    			html += '<div class="messages msg_receive">';
	    			var msgtxt = resp.message;
		    		if (msgtxt == null) {
		    			msgtxt = "";
		    		}
	    			html += '<p>'+emoji.replace_colons(replaceBreakLine(msgtxt))+'</p>';
	    			if (resp.confirmBooking && resp.bookingInfo != null) {
	    				html += '<p><b>Date: </b>'+resp.bookingInfo.date+'</p>';
	    				html += '<p><b>Time: </b>'+resp.bookingInfo.time+'</p>';
	    				html += '<p><b>Pax: </b>'+resp.bookingInfo.pax+'</p>';
	    				html += '<p><b>Phone: </b>'+resp.bookingInfo.phone+'</p>';
	    				html += '<p><b>Email: </b>'+resp.bookingInfo.email+'</p>';
	    				html += '<p><b>Special Request: </b>'+resp.bookingInfo.specialRequest+'</p>';
	    			}
	    			html += '</div>';
	    			html += '</div>';
	    			html += '</div>';
	    			if (resp.resultRestaurants != null) {
	    				html += '<div id="restaurant-suggests-'+data.conversation[o].id+'" class="carousel slide" data-ride="carousel">';
			    		html += '<ol class="carousel-indicators" style="margin-left: -20%; width: 40%;">';
			    		var j = 0;
			    		for (var o1 in resp.resultRestaurants) {
			    			html += '<li data-target="#restaurant-suggests-'+data.conversation[o].id+'" data-slide-to="'+j+'" class="'+(j==0?"active":"")+'"></li>';
			    			j++;
			    		}
			    		html += '</ol>';
			    		html += '<div class="carousel-inner" role="listbox">';
			    		var isFirst = true;
		    			for (var o2 in resp.resultRestaurants) {
				    		html += '<div class="item '+(isFirst?"active":"")+'">';
				    		html += '<div class="container">';
				    		html += '<div class="carousel-caption" style="cursor: pointer;">';
				    		html += '<h3 style="color: #009AFF; max-height: 30px; overflow: hidden;" onclick="gotoPage(\'/chatbot/restaurant/'+data.conversation[o].id+'/'+resp.resultRestaurants[o2].id+'\')">'+resp.resultRestaurants[o2].name+'</h3>';
				    		html += '<p style="color: black; max-height: 20px; overflow: hidden; margin-bottom: 5px;">'+resp.resultRestaurants[o2].address+'</p>';
				    		if (resp.resultRestaurants[o2].offers != null && resp.resultRestaurants[o2].offers.length > 0) {
				    			html += '<p style="color: red; max-height: 20px; overflow: hidden; margin-bottom: 0;">'+resp.resultRestaurants[o2].offers[0].short_desc+'&nbsp;(Exp: '+resp.resultRestaurants[o2].offers[0].end_date+')</p>';
				    		}
				    		if (resp.resultRestaurants[o2].image != null) {
				    			html += '<p style="overflow: hidden;" onclick="gotoPage(\'/chatbot/restaurant/'+data.conversation[o].id+'/'+resp.resultRestaurants[o2].id+'\')"><img alt="Loading..." style="max-height: 125px;" src="https://dpimages.crayondata.com/high-res-image/'+resp.resultRestaurants[o2].image+'"></p>';
				    		} else {
				    			html += '<p style="overflow: hidden;" onclick="gotoPage(\'/chatbot/restaurant/'+data.conversation[o].id+'/'+resp.resultRestaurants[o2].id+'\')"><img alt="Loading..." style="max-height: 125px;" src="/assets/images/img/no-image-available.jpg"></p>';
				    		}
				    		
				    		html += '<p><a class="btn btn-lg btn-primary" href="#" onclick="gotoPage(\'/chatbot/restaurant/'+data.conversation[o].id+'/'+resp.resultRestaurants[o2].id+'\')" role="button" style="padding: 5px; font-size: 14px;">Locate it!</a></p>';
				    		var mstar = resp.resultRestaurants[o2].mstar;
				    		if (mstar == null || mstar == "") {
				    			mstar = "blank";
				    		}
				    		var likeImg = "like-gray.png";
				    		if (restIds.indexOf(resp.resultRestaurants[o2].id+",") >= 0) {
				    			likeImg = "like-green.png";
				    		}
				    		html += '<p><table width="100%"><tr><td style="text-align: left; width: 50%; padding-left: 10px;"><img src="/assets/images/img/'+likeImg+'" onclick="changeLikeBtn(this,'+data.conversation[o].id+','+resp.resultRestaurants[o2].id+')" height="30px"></td><td style="text-align: right; padding-right: 10px;"><img src="/assets/images/img/'+mstar+'.png" height="30px"></td></tr></table></p>';
				    		if (resp.resultRestaurants[o2].offers != null && resp.resultRestaurants[o2].offers.length > 0) {
				    			html += '<p style="position: absolute; right: 0; top: 0;"><img src="/assets/images/img/specialdeals.png" width="80px"></p>';
				    		}
				    		html += '</div>';
				    		html += '</div>';
				    		html += '</div>';
				    		isFirst = false;
		    			}
		    			html += '</div>';
			    		html += '<a class="left carousel-control" href="#restaurant-suggests-'+data.conversation[o].id+'" role="button" data-slide="prev">';
			    		html += '<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>';
			    		html += '<span class="sr-only">Previous</span>';
			    		html += '</a>';
			    		html += '<a class="right carousel-control" href="#restaurant-suggests-'+data.conversation[o].id+'" role="button" data-slide="next">';
			    		html += '<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>';
			    		html += '<span class="sr-only">Next</span>';
			    		html += '</a>';
			    		html += '</div>';
	    			}
	    			if (resp.suggestion && resp.suggestion != null) {
	    				html += '<div class="row msg_container base_receive">';
		    			html += '<div class="messages_wrapper">';
		    			html += '<div class="avatar avatar_receive">';
		    			html += '<img src="/assets/images/img/iaasimov-4.png" class="img-responsive">';
		    			html += '</div>';
		    			html += '<div class="messages msg_receive">';
		    			html += '<p>'+resp.suggestion.message+'</p>';
		    			for (var o in resp.suggestion.options) {
		    				if (resp.suggestion.options[o] == "SHARE_LOCATION") {
		    					html += '<p><button type="button" class="btn btn-info suggestion-btn" onclick="shareLocation()">Share</button></p>';
		    				} else {
		    					html += '<p><button type="button" class="btn btn-info suggestion-btn" onclick="useSuggest(\''+resp.suggestion.options[o]+'\')">'+resp.suggestion.options[o]+'</button></p>';
		    				}
		    			}
		    			
		    			html += '</div>';
		    			html += '</div>';
		    			html += '</div>';
	    			}
	    			$('#container_bubble').append(html);
	    		}
	    		loadSwipe();
	    		gotoBottom();
	    		disableSuggestionBtn();
	    	}
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	handleErr();
	    }
	});
}

function disableSuggestionBtn() {
	if(!window.location.hash) {
		$(".suggestion-btn").prop('disabled', true);
		$(".suggestion-btn").removeAttr('onclick');
	}
}

function useSuggest(val) {
	$("#message-text").val(val);
	sendQuestion(-1, -1);
}
function changeLikeBtn(obj, cid, rid) {
	var isLike = 1;
	if (obj.src.indexOf("like-gray.png") >= 0) {
		isLike = 1;
		restIds += rid + ",";
	} else {
		isLike = 0;
		restIds = restIds.replace(rid+",", "");
	}
	
	i++;
	var html = "";
	html += '<div class="row msg_container base_receive">';
	html += '<div class="messages_wrapper">';
	html += '<div class="avatar avatar_receive">';
	html += '<img src="/assets/images/img/iaasimov-4.png" class="img-responsive">';
	html += '</div>';
	html += '<div class="messages msg_receive">';
	html += '<p id="analysing-id-'+i+'">'+(itemswait[Math.floor(Math.random()*itemswait.length)])+'&nbsp;<img src="../assets/images/loading.gif"></p>';
	html += '</div>';
	html += '</div>';
	html += '</div>';
	$('#container_bubble').append(html);
	gotoBottom();
	
	$.ajax({
	    url: "/chatbot/restaurant/like",
	    data: { cid: cid, rid: rid, isLike: isLike, pid: i },
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	if (isLike == 1) {
	    		obj.src = "/assets/images/img/like-green.png";
	    	} else {
	    		obj.src = "/assets/images/img/like-gray.png";
	    	}
	    	if (data) {
	    		generateResponseData(data);
	    	}
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	handleErr();
	    }
	});
}

function booking(message, rid, bookOpt) {
	$("#message-text").val(message);
	sendQuestion(rid, bookOpt);
}

function gotoBottom() {
	$("html, body").animate({ scrollTop: $(document).height() }, 1000);
}