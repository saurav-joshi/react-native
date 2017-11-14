package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.iaasimov.entity.Customer;
import com.iaasimov.utils.EmailTemplate;
import com.iaasimov.utils.StringUtils;

import play.Configuration;
import play.Play;
import play.data.DynamicForm;
import play.mvc.Result;
import play.mvc.Security;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
@Security.Authenticated(CustomerSecuredAuthenticate.class)
public class MemberAction extends CommonAction {
  
    public Result input() {
    	return ok(views.html.pages.manage_member.render());
    }
    
    public Result loadMembers() {
    	Customer customer = getCustomer();
    	List<Customer> lstCustomer = new ArrayList<Customer>();
    	if (customer != null) {
    		JSONArray arrCustomer = (JSONArray) getData("/customer/parent/"+customer.getId(), false);
        	if (arrCustomer != null && arrCustomer.length() > 0) {
        		for (int i = 0; i < arrCustomer.length(); i++) {
        			Customer cus = new Customer();
        			JSONObject obj = (JSONObject)arrCustomer.get(i);
        			cus.setCompanyName(obj.getString("companyName"));
        			cus.setContractEndedString(StringUtils.dateToString(new Date(obj.getLong("contractEnded"))));
        			cus.setContractStartedString(StringUtils.dateToString(new Date(obj.getLong("contractStarted"))));
        			cus.setContractEnded(obj.getLong("contractEnded"));
        			cus.setContractStarted(obj.getLong("contractStarted"));
        			cus.setEmail(obj.getString("email"));
        			cus.setId(obj.getLong("id"));
        			cus.setIsAdmin(obj.getBoolean("isAdmin"));
        			cus.setIsSystemAdmin(obj.getBoolean("isSystemAdmin"));
        			cus.setName(obj.getString("name"));
        			cus.setParentId(obj.getLong("parentId"));
        			cus.setPhone(obj.getString("phone"));
        			cus.setRequestLimited(obj.getLong("requestLimited"));
        			lstCustomer.add(cus);
        		}
        	}
    	}
    	return toJson(lstCustomer);
    }
    
    public Result addMember() {
    	DynamicForm form = form().bindFromRequest();
    	String name = form.get("name");
    	String email = form.get("email");
    	String limited = form.get("limited");
    	Map<String, String> map = new HashMap<String, String>();
    	try {
	    	if (!StringUtils.isEmpty(limited) && StringUtils.isAllDegit(limited) && !StringUtils.isEmpty(email)) {
	    		Customer customer = getCustomer();
	    		if (getCustomer().getRequestLimited() < 0 || isValidNoRequest(map, null, Long.valueOf(limited))) {
	    			Configuration configuration  = Play.application().configuration();
					String password = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
					JSONObject postBody = new JSONObject();
			    	postBody.put("companyName", customer.getCompanyName());
			    	postBody.put("contractStarted", customer.getContractStarted());
			    	postBody.put("contractEnded", customer.getContractEnded());
			    	postBody.put("email", email);
			    	postBody.put("name", name);
			    	postBody.put("parentId", customer.getId());
			    	postBody.put("password", password);
			    	postBody.put("phone", "");
			    	postBody.put("isAdmin", false);
			    	postBody.put("isSystemAdmin", false);
			    	postBody.put("requestLimited", limited);
			    	JSONObject res = postData("/customer", postBody);
					
			    	if (res.has("error")) {
			    		map.put("err", "Email already existed!");
					} else {
						// send email to user
						try {
							EmailTemplate.sendWelcomeMail(name, email, password);
							map.put("msg", "Member added successful!");
						} catch (Exception e) {
							map.put("err", "Please try again later!");
						}
					}
	    		}
	    	}
    	} catch (Exception ex) {
    		map.put("err", "Error happening. Please try later.");
    	}
    	
    	return toJson(map);
    }
    
    private boolean isValidNoRequest(Map<String, String> msg, Long editCustId, Long limit) {
    	Customer customer = getCustomer();
    	JSONArray arrCustomer = (JSONArray) getData("/customer/parent/"+customer.getId(), false);
    	Long l = 0l;
    	if (arrCustomer != null && arrCustomer.length() > 0) {
    		for (int i = 0; i < arrCustomer.length(); i++) {
    			JSONObject obj = (JSONObject)arrCustomer.get(i);
    			if (editCustId != null && editCustId.longValue() == obj.getLong("id")) {
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
    
    public Result updateMember() {
    	DynamicForm form = form().bindFromRequest();
    	Map<String, String> msg = new HashMap<String, String>();
    	msg.put("err", "Error happening. Please try again later.");
    	JSONObject obj = (JSONObject) getData("/customer/"+form.get("customerId"), true);
    	if (obj != null && obj.has("id") && obj.getLong("id") > 0) {
    		if (getCustomer().getRequestLimited() < 0 || isValidNoRequest(msg, obj.getLong("id"), Long.valueOf(form.get("limit")))) {
    			JSONObject postBody = new JSONObject();
        		postBody.put("id", obj.getLong("id"));
        		postBody.put("companyName", obj.getString("companyName"));
    	    	postBody.put("contractStarted", obj.getLong("contractStarted"));
    	    	postBody.put("contractEnded", obj.getLong("contractEnded"));
    	    	postBody.put("name", form.get("name"));
    	    	postBody.put("phone", obj.getString("phone"));
    	    	postBody.put("isAdmin", obj.getBoolean("isAdmin"));
    	    	postBody.put("isSystemAdmin", obj.getBoolean("isSystemAdmin"));
    	    	postBody.put("requestLimited", Long.valueOf(form.get("limit")));
    	    	postBody.put("password", "");
    	    	JSONObject res = putData("/customer", postBody);
    	    	msg.put("msg", "Updated successful!");
    		}
    	}
    	
    	return toJson(msg);
    }
    
    public Result deleteMember(Long id) {
    	Map<String, String> msg = new HashMap<String, String>();
    	msg.put("err", "Something wrong happened. Please try again later!");
    	if (id != null && id.longValue() > 0) {
	    	deleteData("/customer/"+id);
    		msg.put("msg", "Deleted successful!");
    	}
    	return toJson(msg);
    }
}