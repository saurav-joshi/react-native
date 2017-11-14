package controllers;

import static play.data.Form.form;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import com.iaasimov.entity.Customer;
import com.iaasimov.utils.EmailTemplate;
import com.iaasimov.utils.StringConstants;
import com.iaasimov.utils.StringUtils;

import play.Configuration;
import play.Play;
import play.cache.Cache;
import play.data.DynamicForm;
import play.mvc.Result;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class CustomerLoginAction extends CommonAction {

    public Result login() {
    	DynamicForm form = form().bindFromRequest();
    	String email = form.get("email");
    	String password = form.get("password");
		Map<String, String> map = new HashMap<String, String>();
		if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) {
			JSONObject postBody = new JSONObject();
	    	postBody.put("email", email);
	    	postBody.put("password", password);
	    	JSONObject res = postData("/customer/login", postBody);
			if (res.has("error")) {
				map.put("err", "This account doesn't exist.");
			} else {
				Customer customer = new Customer();
				customer.setCompanyName(res.getString("companyName"));
				customer.setContractEnded(res.isNull("contractEnded")?0:res.getLong("contractEnded"));
				customer.setContractStarted(res.isNull("contractStarted")?0:res.getLong("contractStarted"));
				customer.setEmail(res.getString("email"));
				customer.setId(res.getLong("id"));
				customer.setIsAdmin(res.getBoolean("isAdmin"));
				customer.setIsSystemAdmin(res.getBoolean("isSystemAdmin"));
				customer.setName(res.getString("name"));
				customer.setParentId(res.getLong("parentId"));
				customer.setPhone(res.getString("phone"));
				customer.setRequestLimited(res.getLong("requestLimited"));
				customer.setStatus(res.getInt("status"));
				String uuid = UUID.randomUUID().toString();
	    		session().put(StringConstants.CUSTOMER_UUID, uuid);
	    		Cache.set(uuid+"-customer", customer, StringConstants.DEFAULT_CACHE_TTL);
			}
		}
		return toJson(map);
    }
    
    public Result requestAccount() {
    	DynamicForm form = form().bindFromRequest();
    	String company = form.get("company");
    	String username = form.get("username");
    	String email = form.get("email");
		Map<String, String> map = new HashMap<String, String>();
		if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(username) && !StringUtils.isEmpty(company)) {
			Configuration configuration  = Play.application().configuration();
			String password = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
			JSONObject postBody = new JSONObject();
	    	postBody.put("companyName", company);
	    	Calendar cal = Calendar.getInstance();
	    	postBody.put("contractStarted", cal.getTime().getTime());
	    	cal.set(Calendar.DATE, cal.get(Calendar.DATE) + configuration.getInt("system.default_contract_ended"));
	    	postBody.put("contractEnded", cal.getTime().getTime());
	    	postBody.put("email", email);
	    	postBody.put("name", username);
	    	postBody.put("parent_id", 0);
	    	postBody.put("password", password);
	    	postBody.put("phone", "");
	    	postBody.put("isAdmin", false);
	    	postBody.put("isSystemAdmin", false);
	    	postBody.put("requestLimited", configuration.getLong("system.default_no_request"));
	    	JSONObject res = postData("/customer", postBody);
			
	    	if (res.has("error")) {
	    		map.put("err", "You already requested!");
			} else {
				// send email to user
				try {
					EmailTemplate.sendWelcomeMail(username, email, password);
					map.put("msg", "Please check your email.");
				} catch (Exception e) {
					map.put("err", "Please try again later!");
				}
			}
		}
		return toJson(map);
    }
    
    public Result logout() {
    	Cache.remove(session().get(StringConstants.CUSTOMER_UUID)+"-customer");
    	session().clear();
    	return ok(views.html.landing.render());
    }
}