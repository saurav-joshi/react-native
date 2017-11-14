getids();

function getids() {
	$("#appid-body").html('<tr><td colspan="5"><img src="/assets/images/loading.gif"></td></tr>');
	$.ajax({
	    url: "/customer/app/appids",
	    data: { },
	    dataType: "json",
	    type: "GET",
	    success: function(data) {
	    	var html = "";
	    	if (data && data.length > 0) {
	    		for (var o in data) {
	    			var limited = parseInt(data[o].request_limited);
	    			if (limited < 0) {
	    				limited = "unlimited";
	    			}
	    			html += "<tr id='app-"+data[o].id+"'>";
		    		html += "<td><div contentEditable class='content-editable' id='app-name-"+data[o].id+"'>"+data[o].appName+"<div></td>";
		    		html += "<td>"+data[o].appId+"</td>";
		    		html += "<td>"+data[o].createdDateString+"</td>";
		    		var limited = data[o].requestLimited;
		    		if (data[o].requestLimited < 0) {
		    			limited = "Unlimited";
		    		}
		    		html += "<td><div contentEditable class='content-editable' title='Enter Number of Limit Request' id='limit-"+data[o].id+"'>"+limited+"</div></td>";
		    		html += "<td><span class='glyphicon glyphicon-floppy-disk' title='Save Your Changes' style='cursor: pointer;' onclick='updateCustomerApp("+data[o].id+")'></span>&nbsp;<span class='glyphicon glyphicon-th' title='View Detail' style='cursor: pointer;' class='clickable' data-toggle='collapse' onclick='loadAppInfo(\""+data[o].appId+"\")' data-target='.row-"+data[o].id+"'></span><span class='glyphicon glyphicon-trash' title='Delete' style='margin-left: 20px; color: red;cursor: pointer;' onclick='deleteAppRow("+data[o].id+")'></span></td>";
		    		html += "</tr>";
		    		html += "<tr class='collapse row-"+data[o].id+"'><td colspan='5' style='padding: 10px 50px 10px 50px;' id='expand-data-"+data[o].appId+"'></td></tr>";
	    		}
	    	}
	    	$("#appid-body").html(html);
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	handleErr();
	    }
	});
}

function createApp() {
	if ($("#app_name").val() == "" || $("#noLimited").val() == "") {
		$("#text-login-msg").html("Please enter full information.");
		$("#app_name").focus();
		return;
	}
	$("#text-login-msg").html("<img src='/assets/images/loading.gif' />");
	$.ajax({
	    url: "/customer/app/create",
	    data: { appName: $("#app_name").val(), limited: $("#noLimited").val() },
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	if (data.err) {
	    		$("#text-login-msg").html(data.err);
	    	} else {
		    	getids();
		    	$("#text-login-msg").html("Type your new application.");
		    	$('#login-modal').modal('toggle');
		    	$.notify(data.msg);
	    	}
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	handleErr();
	    }
	});
	return;
}

function updatePassword() {
	if ($("#new-password").val() == "") {
		$("#text-update-pass-msg").html("Please enter new password.");
		$("#new-password").focus();
		return;
	}
	if ($("#new-password").val() != $("#confirm-new-password").val()) {
		$("#text-update-pass-msg").html("Password is not similar.");
		$("#confirm-new-password").focus();
		return;
	}
	$("#text-update-pass-msg").html("<img src='/assets/images/loading.gif' />");
	$.ajax({
	    url: "/customer/updatepassword",
	    data: { pass: $("#new-password").val() },
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	if (data.err) {
	    		$("#text-update-pass-msg").html(data.err);
	    	} else {
		    	$('#change-pass-modal').modal('toggle');
		    	$('#text-update-pass-msg').html("Type your new password.");
		    	$.notify(data.msg);
	    	}
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	handleErr();
	    }
	});
	return;
}

function updateCustomerApp(id) {
	if (!$.isNumeric($("#limit-"+id).text())) {
		alert("Please enter a number value.");
		return;
	}
	$.notify("Updating...");
	$.ajax({
	    url: "/customer/app/update/"+id,
	    data: { appName: $("#app-name-"+id).text(), limited: $("#limit-"+id).text() },
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	if (data.msg != undefined) {
	    		$.notify(data.msg);
	    	} else {
	    		$.notify(data.err);
	    	}
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	handleErr();
	    }
	});
}

function loadAppInfo(appId) {
	if ($("#expand-data-"+appId).text() == "") {
		$("#expand-data-"+appId).html("<img src='/assets/images/loading.gif' />");
		$.ajax({
		    url: "/customer/app/query/"+appId,
		    data: { },
		    dataType: "json",
		    type: "GET",
		    success: function(data) {
		    	data = JSON.parse(data);
		    	$("#expand-data-"+appId).html("<div style='float: left;'><svg width='400' height='250' id='svg-id-"+appId+"'></svg></div><div style='float: left; padding-left: 30px;' id='status-info-"+appId+"'></div>");
		    	generateChart(appId, data[0].usaged);
		    	var tmp = "";
		    	for (var o in data[1].status) {
		    		if (data[1].status[o].is_success) {
		    			tmp += "No of Request Successed:&nbsp;<b style='color: green;'>"+data[1].status[o].total+"</b><br>";
		    		} else {
		    			tmp += "No of Request Failed:&nbsp;<b style='color: red;'>"+data[1].status[o].total+"</b><br>";
		    		}
		    	}
		    	$("#status-info-"+appId).html(tmp);
		    },
		    error: function (xhr, ajaxOptions, thrownError) {
		    	handleErr();
		    }
		});
	}
}

function generateChart(appId, data) {
	var svg = d3.select("#svg-id-"+appId);
    margin = {top: 20, right: 20, bottom: 30, left: 50},
    width = +svg.attr("width") - margin.left - margin.right,
    height = +svg.attr("height") - margin.top - margin.bottom,
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	var parseTime = d3.timeParse("%Y-%m-%d");

	var x = d3.scaleTime()
	    .rangeRound([0, width]);

	var y = d3.scaleLinear()
	    .rangeRound([height, 0]);

	var line = d3.line()
	    .x(function(d) { return x(d.date); })
	    .y(function(d) { return y(d.total); });

	data.forEach(function(d) {
	      d.date = parseTime(d.date);
	      d.total = +d.total;
	  });
	
	  x.domain(d3.extent(data, function(d) { return d.date; }));
	  y.domain(d3.extent(data, function(d) { return d.total; }));

	  g.append("g")
	      .attr("transform", "translate(0," + height + ")")
	      .call(d3.axisBottom(x))
	    .select(".domain")
	      .remove();

	  g.append("g")
	      .call(d3.axisLeft(y))
	      .append("text")
	      .attr("fill", "#000")
	      .attr("transform", "rotate(-90)")
	      .attr("y", 6)
	      .attr("dy", "0.71em")
	      .attr("text-anchor", "end")
	      .text("Requests");

	  g.append("path")
	      .datum(data)
	      .attr("fill", "none")
	      .attr("stroke", "steelblue")
	      .attr("stroke-linejoin", "round")
	      .attr("stroke-linecap", "round")
	      .attr("stroke-width", 1.5)
	      .attr("d", line);
}

function deleteAppRow(id) {
	if (confirm("Are you sure you want to delete?")) {
		$.notify("Deleting...");
		$.ajax({
		    url: "/customer/app/delete/"+id,
		    data: {  },
		    dataType: "json",
		    type: "POST",
		    success: function(data) {
		    	getids();
		    	if (data.msg != undefined) {
		    		$.notify(data.msg);
		    	} else {
		    		$.notify(data.err);
		    	}
		    },
		    error: function (xhr, ajaxOptions, thrownError) {
		    	handleErr();
		    }
		});
	}
}

function gotoPage(page) {
	window.location.href=page;
}