package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;

import com.iaasimov.dao.service.GenericController;
import com.iaasimov.entity.Customer;
import com.iaasimov.entity.CustomerApp;
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
public class AdminAction extends CommonAction {
	
    public Result input() {
    	if (!getCustomer().getIsAdmin()) {
    		return ok(views.html.error.unauthorize.render());
    	}
    	return ok(views.html.admin.manage_user.render());
    }
    
    public Result getCustomers() {
    	JSONArray arrCustomer = (JSONArray) getData("/customer", false);
    	List<Customer> lstCustomer = new ArrayList<Customer>();
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
    	
    	return toJson(lstCustomer);
    }
    
    public Result getCustomerApps(Long cid) {
    	List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("customerId",  cid));
		List<CustomerApp> lstCustomerApp = GenericController.getInstance().findByCriteria(CustomerApp.class, restrictions);
		List<Map<String, String>> lstRet = new ArrayList<Map<String, String>>();
		if (lstCustomerApp != null && !lstCustomerApp.isEmpty()) {
			for (CustomerApp cApp: lstCustomerApp) {
				Map<String, String> map = new HashMap<>();
				map.put("appName", cApp.getAppName());
				map.put("appId", cApp.getAppId());
    			map.put("request_limited", String.valueOf(cApp.getRequestLimited()));
				lstRet.add(map);
			}
		}
    	return toJson(lstRet);
    }
    
    public Result createCustomer() {
    	DynamicForm form = form().bindFromRequest();
    	String customerName = form.get("customerName");
    	String customerEmail = form.get("customerEmail");
    	String customerCompany = form.get("customerCompany");
    	Map<String, String> map = new HashMap<String, String>();
    	try {
	    	if (!StringUtils.isEmpty(customerName) && !StringUtils.isEmpty(customerEmail)) {
	    		Configuration configuration  = Play.application().configuration();
				String password = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
				JSONObject postBody = new JSONObject();
		    	postBody.put("companyName", customerCompany);
		    	Calendar cal = Calendar.getInstance();
		    	postBody.put("contractStarted", cal.getTime().getTime());
		    	cal.set(Calendar.DATE, cal.get(Calendar.DATE) + configuration.getInt("system.default_contract_ended"));
		    	postBody.put("contractEnded", cal.getTime().getTime());
		    	postBody.put("email", customerEmail);
		    	postBody.put("name", customerName);
		    	postBody.put("parent_id", 0);
		    	postBody.put("password", password);
		    	postBody.put("phone", "");
		    	postBody.put("isAdmin", true);
		    	postBody.put("isSystemAdmin", false);
		    	postBody.put("requestLimited", configuration.getLong("system.default_no_request"));
		    	JSONObject res = postData("/customer", postBody);
				
		    	if (res.has("error")) {
		    		map.put("err", "Email already existed!");
				} else {
					// send email to user
					try {
						EmailTemplate.sendWelcomeMail(customerName, customerEmail, password);
						map.put("msg", "Created customer successful!");
					} catch (Exception e) {
						map.put("err", "Please try again later!");
					}
				}
	    	}
    	} catch (Exception ex) {
    		map.put("err", "Error happening. Please try later.");
    	}
    	
    	return toJson(map);
    }
    
    public Result updateCustomer() {
    	DynamicForm form = form().bindFromRequest();
    	Map<String, String> msg = new HashMap<String, String>();
    	msg.put("msg", "Error happening. Please try again later.");
    	JSONObject obj = (JSONObject) getData("/customer/"+form.get("customerId"), true);
    	if (obj != null && obj.has("id") && obj.getLong("id") > 0) {
    		JSONObject postBody = new JSONObject();
    		postBody.put("id", obj.getLong("id"));
    		postBody.put("companyName", obj.getString("companyName"));
	    	postBody.put("contractStarted", obj.getLong("contractStarted"));
	    	postBody.put("contractEnded", (StringUtils.stringToDate(form.get("contractDate")).getTime()));
	    	postBody.put("name", obj.getString("name"));
	    	postBody.put("phone", obj.getString("phone"));
	    	postBody.put("isAdmin", obj.getBoolean("isAdmin"));
	    	postBody.put("isSystemAdmin", obj.getBoolean("isSystemAdmin"));
	    	postBody.put("requestLimited", Long.valueOf(form.get("limit")));
	    	postBody.put("password", "");
	    	JSONObject res = putData("/customer", postBody);
	    	msg.put("msg", "Updated successful!");
    	}
    	
    	return toJson(msg);
    }
    
    public Result getNoQueryUser(Long parentId) {
    	JSONArray arrCustomerQuery = (JSONArray) getData("/customer/admin/member/query/"+parentId, false);
    	return toJson(arrCustomerQuery.toString());
    }
    
    public Result deleteCustomer(Long id) {
    	Map<String, String> msg = new HashMap<String, String>();
    	msg.put("err", "Something wrong happened. Please try again later!");
    	if (id != null && id.longValue() > 0) {
	    	deleteData("/customer/"+id);
    		msg.put("msg", "Deleted successful!");
    	}
    	return toJson(msg);
    }
}