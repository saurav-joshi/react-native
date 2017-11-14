getCustomers();

function getCustomers() {
	$("#appid-body").html('<tr><td colspan="5"><img src="assets/images/loading.gif"></td></tr>');
	$.ajax({
	    url: "/admin/customer/get",
	    data: { },
	    dataType: "json",
	    type: "GET",
	    success: function(data) {
	    	var html = "";
	    	if (data && data.length > 0) {
	    		for (var o in data) {
	    			html += "<tr id='raw-"+data[o].id+"'>";
		    		html += "<td>"+data[o].name+"</td>";
		    		html += "<td>"+data[o].email+"</td>";
		    		html += "<td>"+data[o].companyName+"</td>";
		    		html += "<td><div contentEditable class='content-editable' id='contractDate-"+data[o].id+"'>"+data[o].contractEndedString+"</div></td>";
		    		var limited = data[o].requestLimited;
		    		if (data[o].requestLimited < 0) {
		    			limited = "Unlimited";
		    		}
		    		html += "<td><div contentEditable class='content-editable' title='Enter Number of Limit Request' id='limit-"+data[o].id+"'>"+limited+"</div></td>";
		    		html += "<td><span class='glyphicon glyphicon-floppy-disk' title='Save Your Changes' style='cursor: pointer;' onclick='updateCustomer("+data[o].id+")'></span>&nbsp;<span class='glyphicon glyphicon-th' title='View Detail' style='cursor: pointer;' class='clickable' onclick='loadUserInfo(\""+data[o].id+"\")' data-toggle='collapse' data-target='.row-"+data[o].id+"'></span><span class='glyphicon glyphicon-trash' title='Delete' style='margin-left: 20px; color: red;cursor: pointer;' onclick='deleteAppRow("+data[o].id+")'></span></td>";
		    		html += "</tr>";
		    		html += "<tr class='collapse row-"+data[o].id+"'><td colspan='6' style='padding: 10px 50px 10px 50px;' id='expand-data-"+data[o].id+"'></td></tr>";
	    		}
	    	}
	    	$("#appid-body").html(html);
	    },
	    error: function (xhr, ajaxOptions, thrownError) {}
	});
}

function createCustomer() {
	if ($("#customerName").val() == "") {
		$("#text-login-msg").html("Please enter customer name.");
		$("#customerName").focus();
		return;
	}
	if ($("#customerEmail").val() == "") {
		$("#text-login-msg").html("Please enter customer email.");
		$("#customerEmail").focus();
		return;
	}
	$("#text-login-msg").html("<img src='/assets/images/loading.gif' />");
	$.ajax({
	    url: "/admin/customer/create",
	    data: { customerName: $("#customerName").val(), customerEmail: $("#customerEmail").val(), customerCompany: $("#customerCompany").val() },
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	if (data.err) {
	    		$("#text-login-msg").html(data.err);
	    	} else {
	    		getCustomers();
	    		$("#text-login-msg").html("Type your new customer.");
		    	$('#login-modal').modal('toggle');
		    	$.notify(data.msg);
	    	}
	    },
	    error: function (xhr, ajaxOptions, thrownError) {}
	});
	return;
}

function updateCustomer(cid) {
	if (!$.isNumeric($("#limit-"+cid).text())) {
		alert("Please enter a number value.");
		return;
	}
	$.notify("Updating...");
	$.ajax({
	    url: "/admin/customer/update",
	    data: { customerId: cid, limit: $("#limit-"+cid).text(), contractDate: $("#contractDate-"+cid).text() },
	    dataType: "json",
	    type: "POST",
	    success: function(data) {
	    	$.notify(data.msg);
	    },
	    error: function (xhr, ajaxOptions, thrownError) {}
	});
	return;
}

function loadUserInfo(parentId) {
	if ($("#expand-data-"+parentId).text() == "") {
		$("#expand-data-"+parentId).html("<img src='/assets/images/loading.gif' />");
		$.ajax({
		    url: "/admin/manage/member/query/"+parentId,
		    data: { },
		    dataType: "json",
		    type: "GET",
		    success: function(data) {
		    	data = JSON.parse(data);
		    	var html = "";
		    	html += '<table class="table table-hover table-responsive">';
		    	html += '<tr>';
		    	var i = 0;
		    	for (var o in data) {
		    		if (i != 0 && i%2==0) {
		    			html += '</tr><tr>';
		    		}
		    		html += '<td width="50%"><div><b>User:</b>&nbsp;'+data[o].email+'</div><div id="svg-id-'+data[o].customer_id+'"></div></td>';
		    		i++;
	    		}
		    	if (i%2==1) html += '<td>&nbsp;</td>';
		    	html += '</tr>';
		    	html += '</table>';
		    	$("#expand-data-"+parentId).html(html);
		    	
		    	for (var o in data) {
		    		if (data[o].data.length <= 0) {
			    		$("#svg-id-"+data[o].customer_id).html("No data found.");
			    	} else {
				    	generateChart(data[o].customer_id, data[o].data);
			    	}
		    	}
		    },
		    error: function (xhr, ajaxOptions, thrownError) {
		    	handleErr();
		    }
		});
	}
}

function generateChart(cId, data) {
	var width = 400,
    height = 130,
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
	    .attr("transform", "translate(" + width / 3 + "," + height / 2 + ")");
	
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
		    url: "/admin/customer/delete/"+id,
		    data: {  },
		    dataType: "json",
		    type: "POST",
		    success: function(data) {
		    	getCustomers();
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