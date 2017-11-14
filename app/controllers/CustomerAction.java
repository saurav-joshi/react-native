package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.iaasimov.entity.Customer;
import com.iaasimov.entity.CustomerApp;
import com.iaasimov.utils.EncryptionUtils;
import com.iaasimov.utils.StringUtils;

import play.data.DynamicForm;
import play.mvc.Result;
import play.mvc.Security;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
@Security.Authenticated(CustomerSecuredAuthenticate.class)
public class CustomerAction extends CommonAction {
  
    public Result input() {
    	return ok(views.html.pages.manage_app.render());
    }
    
    public Result getAppIDs() {
    	Customer customer = getCustomer();
    	List<CustomerApp> lstApp = new ArrayList<CustomerApp>();
    	if (customer != null) {
    		JSONArray arrCustomerApp = (JSONArray) getData("/customer/app/"+customer.getId(), false);
        	if (arrCustomerApp != null && arrCustomerApp.length() > 0) {
        		for (int i = 0; i < arrCustomerApp.length(); i++) {
        			CustomerApp cusApp = new CustomerApp();
        			JSONObject obj = (JSONObject)arrCustomerApp.get(i);
        			cusApp.setId(obj.getLong("id"));
        			cusApp.setAppId(obj.getString("applicationId"));
        			cusApp.setAppName(obj.getString("applicationName"));
        			cusApp.setCreatedDateString(StringUtils.dateToString(new Date(obj.getLong("createdDate"))));
        			cusApp.setCreatedDate(obj.getLong("createdDate"));
        			cusApp.setRequestLimited(obj.getLong("requestLimited"));
        			lstApp.add(cusApp);
        		}
        	}
    	}
    	
    	return toJson(lstApp);
    }
    
    public Result createApp() {
    	DynamicForm form = form().bindFromRequest();
    	String appName = form.get("appName");
    	String limited = form.get("limited");
    	Map<String, String> msg = new HashMap<String, String>();
    	try {
	    	if (!StringUtils.isEmpty(appName) && StringUtils.isAllDegit(limited)) {
	    		if (getCustomer().getRequestLimited() < 0 || isValidNoRequest(msg, null, Long.valueOf(limited))) {
	    			JSONObject postBody = new JSONObject();
			    	postBody.put("applicationId", EncryptionUtils.generateApplicationId());
			    	postBody.put("applicationName", appName);
			    	postBody.put("customerId", getCustomer().getId());
			    	postBody.put("requestLimited", limited);
			    	JSONObject res = postData("/customer/app", postBody);
		        	msg.put("msg", "Created successful!");
	    		}
	    	} else {
	    		msg.put("err", "Data is incorrect. Please try again.");
	    	}
    	} catch (Exception ex) {
    		msg.put("err", "Error happening. Please try later.");
    	}
    	
    	return toJson(msg);
    }
    
    private boolean isValidNoRequest(Map<String, String> msg, Long editCustAppId, Long limit) {
    	Customer customer = getCustomer();
    	JSONArray arrCustomerApp = (JSONArray) getData("/customer/app/"+customer.getId(), false);
    	Long l = 0l;
    	if (arrCustomerApp != null && arrCustomerApp.length() > 0) {
    		for (int i = 0; i < arrCustomerApp.length(); i++) {
    			JSONObject obj = (JSONObject)arrCustomerApp.get(i);
    			if (editCustAppId != null && editCustAppId.longValue() == obj.getLong("id")) {
    				continue;
    			}
    			l += obj.getLong("requestLimited");
    		}
    	}
    	if ((limit.longValue() + l.longValue()) > customer.getRequestLimited()) {
    		if (l.longValue() == customer.getRequestLimited()) {
    			msg.put("err", "You already reached limit No Request!");
    		} else {
    			msg.put("err", "No Request can't bigger than "+(customer.getRequestLimited() - l.longValue()));
    		}
			return false;
		}
    	return true;
    }
    
    public Result updatePassword() {
    	DynamicForm form = form().bindFromRequest();
    	String pass = form.get("pass");
    	Map<String, String> msg = new HashMap<String, String>();
    	msg.put("msg", "Error happening. Please try again later.");
    	JSONObject obj = (JSONObject) getData("/customer/"+getCustomer().getId(), true);
    	if (obj != null && obj.has("id") && obj.getLong("id") > 0) {
    		JSONObject postBody = new JSONObject();
    		postBody.put("id", obj.getLong("id"));
    		postBody.put("companyName", obj.getString("companyName"));
	    	postBody.put("contractStarted", obj.getLong("contractStarted"));
	    	postBody.put("contractEnded", obj.getLong("contractEnded"));
	    	postBody.put("name", obj.getString("name"));
	    	postBody.put("phone", obj.getString("phone"));
	    	postBody.put("isAdmin", obj.getBoolean("isAdmin"));
	    	postBody.put("isSystemAdmin", obj.getBoolean("isSystemAdmin"));
	    	postBody.put("requestLimited", obj.getLong("requestLimited"));
	    	postBody.put("password", pass);
	    	JSONObject res = putData("/customer", postBody);
	    	msg.put("msg", "Updated successful!");
    	}
    	
    	return toJson(msg);
    }
    
    public Result updateApp(Long id) {
    	DynamicForm form = form().bindFromRequest();
    	String appName = form.get("appName");
    	String limited = form.get("limited");
    	Map<String, String> msg = new HashMap<String, String>();
    	if (appName != "" && appName != null && StringUtils.isAllDegit(limited)) {
    		if (getCustomer().getRequestLimited() < 0 || isValidNoRequest(msg, id, Long.valueOf(limited))) {
	    		JSONObject putBody = new JSONObject();
	    		putBody.put("id", id);
	    		putBody.put("applicationName", appName);
	    		putBody.put("requestLimited", limited);
		    	JSONObject res = putData("/customer/app", putBody);
	    		msg.put("msg", "Updated successful!");
    		}
    	}
    	return toJson(msg);
    }
    
    public Result deleteApp(Long id) {
    	Map<String, String> msg = new HashMap<String, String>();
    	msg.put("err", "Something wrong happened. Please try again later!");
    	if (id != null && id.longValue() > 0) {
	    	deleteData("/customer/app/"+id);
    		msg.put("msg", "Deleted successful!");
    	}
    	return toJson(msg);
    }
    
    public Result getNoQueryApp(String appId) {
    	JSONArray arrCustomerQuery = (JSONArray) getData("/customer/app/query/"+appId, false);
    	return toJson(arrCustomerQuery.toString());
    }
    
    public Result getNoQueryMember(Long cId) {
    	JSONArray arrCustomerQuery = (JSONArray) getData("/customer/member/query/"+cId, false);
    	return toJson(arrCustomerQuery.toString());
    }
}