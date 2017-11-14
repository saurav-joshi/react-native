getMembers();

function getMembers() {
	$("#mamber-body-id").html('<tr><td colspan="5"><img src="/assets/images/loading.gif"></td></tr>');
	$.ajax({
	    url: "/customer/manage/members",
	    data: { },
	    dataType: "json",
	    type: "GET",
	    success: function(data) {
	    	var html = "";
	    	if (data && data.length > 0) {
	    		for (var o in data) {
	    			html += "<tr id='raw-"+data[o].id+"'>";
		    		html += "<td><div contentEditable class='content-editable' id='name-"+data[o].id+"'>"+data[o].name+"</div></td>";
		    		html += "<td>"+data[o].email+"</td>";
		    		var limited = data[o].requestLimited;
		    		if (data[o].requestLimited < 0) {
		    			limited = "Unlimited";
		    		}
		    		html += "<td><div contentEditable class='content-editable' title='Enter Number of Limit Request' id='limit-"+data[o].id+"'>"+limited+"</div></td>";
		    		html += "<td><span class='glyphicon glyphicon-floppy-disk' title='Save Your Changes' style='cursor: pointer;' onclick='updateMember("+data[o].id+")'></span>&nbsp;<span class='glyphicon glyphicon-th' title='View Detail' style='cursor: pointer;' class='clickable' data-toggle='collapse' onclick='loadMemberInfo(\""+data[o].id+"\")' data-target='.row-"+data[o].id+"'></span><span class='glyphicon glyphicon-trash' title='Delete' style='margin-left: 20px; color: red;cursor: pointer;' onclick='deleteAppRow("+data[o].id+")'></span></td>";
		    		html += "</tr>";
		    		html += "<tr class='collapse row-"+data[o].id+"'><td colspan='4' style='padding: 10px 50px 10px 50px;' id='expand-data-"+data[o].id+"'></td></tr>";
	    		}
	    	}
	    	$("#mamber-body-id").html(html);
	    },
	    error: function (xhr, ajaxOptions, thrownError) {
	    	handleErr();
	    }
	});
}

function addMember() {
	if ($("#memberEmail").val() == "" || $("#memberName").val() == "" || $("#noLimited").val() == "") {
		$("#text-login-msg").html("Please enter full information.");
		$("#memberEmail").focus();
		return;
	}
	$("#text-login-msg").html("<img src='/assets/images/loading.gif' />");
	$.ajax({
	    url: "/customer/manage/member/add",
	    data: { email: $("#memberEmail").val(), name: $("#memberName").val(), limited: $("#noLimited").val() },
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	if (data.err) {
	    		$("#text-login-msg").html(data.err);
	    	} else {
	    		getMembers();
		    	$("#text-login-msg").html("Type your new member.");
		    	$('#add-member-modal').modal('toggle');
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

function updateMember(cid) {
	if (!$.isNumeric($("#limit-"+cid).text())) {
		alert("Please enter a number value.");
		return;
	}
	$.notify("Updating...");
	$.ajax({
	    url: "/customer/manage/member/update",
	    data: { customerId: cid, limit: $("#limit-"+cid).text(), name: $("#name-"+cid).text() },
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	if (data.msg != undefined) {
	    		$.notify(data.msg);
	    	} else {
	    		$.notify(data.err);
	    	}
	    },
	    error: function (xhr, ajaxOptions, thrownError) {}
	});
	return;
}

function loadMemberInfo(cId) {
	if ($("#expand-data-"+cId).text() == "") {
		$("#expand-data-"+cId).html("<img src='/assets/images/loading.gif' />");
		$.ajax({
		    url: "/customer/manage/member/query/"+cId,
		    data: { },
		    dataType: "json",
		    type: "GET",
		    success: function(data) {
		    	data = JSON.parse(data);
		    	if (data.length <= 0) {
		    		$("#expand-data-"+cId).html("<div id='svg-id-"+cId+"'>No data found.</div>");
		    	} else {
		    		$("#expand-data-"+cId).html("<div id='svg-id-"+cId+"'></div>");
			    	generateChart(cId, data);
		    	}
		    },
		    error: function (xhr, ajaxOptions, thrownError) {
		    	handleErr();
		    }
		});
	}
}

function generateChart(cId, data) {
	var width = 800,
    height = 250,
    radius = Math.min(width, height) / 2;

	var color = d3.scale.ordinal()
	    .range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);
	
	var arc = d3.svg.arc()
	    .outerRadius(radius - 10)
	    .innerRadius(0);
	
	var pie = d3.layout.pie()
	    .sort(null)
	    .value(function(d) { return d.total; });
	
	var svg = d3.select("#svg-id-"+cId).append("svg")
	    .attr("width", width)
	    .attr("height", height)
	    .append("g")
	    .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");
	
	  data.forEach(function(d) {
	    d.total = +d.total;
	  });
	
	  var g = svg.selectAll(".arc")
	      .data(pie(data))
	      .enter().append("g")
	      .attr("class", "arc");
	
	  g.append("path")
	      .attr("d", arc)
	      .attr("data-legend", function(d) { return d.data.application_name+" ("+d.data.total+")"; })
	      .attr("data-legend-pos", function(d, i) { return i; })
	      .style("fill", function(d) { return color(d.data.application_name+" ("+d.data.total+")"); });
	
	  g.append("text")
	      .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
	      .attr("dy", ".35em")
	      .style("text-anchor", "middle");
	
	  var padding = 20,
	    legx = radius + padding,
	    legend = svg.append("g")
	    .attr("class", "legend")
	    .attr("transform", "translate(" + legx + ", 0)")
	    .style("font-size", "12px")
	    .call(d3.legend);
}

function deleteAppRow(id) {
	if (confirm("Are you sure you want to delete?")) {
		$.notify("Deleting...");
		$.ajax({
		    url: "/customer/manage/member/delete/"+id,
		    data: {  },
		    dataType: "json",
		    type: "POST",
		    success: function(data) {
		    	getMembers();
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