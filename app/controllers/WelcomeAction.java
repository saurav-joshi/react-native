package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;

import play.Play;
import play.cache.Cache;
import play.data.DynamicForm;
import play.mvc.Result;

import com.iaasimov.dao.service.GenericController;
import com.iaasimov.entity.Conversation;
import com.iaasimov.entity.User;
import com.iaasimov.utils.StringConstants;
import com.iaasimov.utils.StringUtils;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class WelcomeAction extends CommonAction {

    public Result welcome() {
    	return ok(views.html.landing.render());
    }
    
    public Result input() {
    	if (getUser() != null) {
    		return redirect(routes.ChatbotAction.input());
    	}
    	return ok(views.html.pages.login.render());
    }
    
    public Result login() {
    	DynamicForm form = form().bindFromRequest();
    	String name = form.get("name");
		Map<String, String> map = new HashMap<String, String>();
		String isNewUser = "";
		if (!StringUtils.isEmpty(name)) {
			name = name.trim().toLowerCase();
			GenericController controller = GenericController.getInstance();
			List<Criterion> restrictions = new ArrayList<Criterion>();
			restrictions.add(Restrictions.eq("userName",  name));
			List<User> lstUser = controller.findByCriteria(User.class, restrictions);
			User user = null;
			// checking user existing or not
			if (lstUser != null && !lstUser.isEmpty()) {
				user = lstUser.get(0);
		    	restrictions = new ArrayList<Criterion>();
				restrictions.add(Restrictions.eq("userId",  user.getId()));
				List<Order> order = new ArrayList<Order>();
				order.add(Order.desc("id"));
				List<Conversation> lstMessage = controller.findByCriteria(Conversation.class, restrictions, order);
				boolean needAskUser = true;
		    	if (lstMessage != null && !lstMessage.isEmpty()) {
		    		if (StringUtils.isEmpty(lstMessage.get(0).getQuestion())) {
		    			needAskUser = false;
		    		}
		    	}
				
		    	if (needAskUser) {
		    		try {
		    			// query API to get begin greeting message
		    			JSONObject postBody = new JSONObject();
		    	    	postBody.put("message", "");
		    	    	postBody.put("type", StringConstants.CONVERSATION_TYPE.BEGIN.toString());
		    	    	postBody.put("token", user.getToken());
		    	    	JSONObject res = postData("/query", postBody);
		    			
		    	    	// store user begin message to db
						Conversation con = new Conversation();
						con.setUserId(user.getId());
						con.setQuestion("");
						con.setAnswer(res.toString());
						con.setPostedDate(new Date());
						controller.saveObject(con);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
		    	}
			} else {
				isNewUser = "en";
				// query api to create new user and get token
				JSONObject postBody = new JSONObject();
				postBody.put("userName", name);
				postBody.put("applicationId", Play.application().configuration().getString("maya.bot.app_id"));
    	    	JSONObject res = postData("/create", postBody);

				user = new User();
				user.setUserName(name);
				user.setEmail("");
				user.setToken(res.getString("token"));
				
				try {
					Long userId = controller.saveObject(user);
					
					// query api to get new user greeting message
					postBody = new JSONObject();
	    	    	postBody.put("message", "");
	    	    	postBody.put("type", StringConstants.CONVERSATION_TYPE.NEW.toString());
	    	    	postBody.put("token", user.getToken());
	    	    	res = postData("/query", postBody);
	    	    	JSONObject suggestion = null;
	    	    	if (res.has("suggestion") && !res.isNull("suggestion")) {
	    	    		suggestion = res.getJSONObject("suggestion");
	    	    		res.remove("suggestion");
	    	    	}
	    	    	String msg[] = res.getString("message").split("\\.");
	    	    	for (int i = 0; i < msg.length; i++) {
	    	    		res.put("message", msg[i].trim()+".");
	    	    		if (i == (msg.length-1)) {
	    	    			res.put("suggestion", suggestion);
	    	    		}
	    	    		// store user new message into db
						Conversation con = new Conversation();
						con.setUserId(userId);
						con.setQuestion("");
						con.setAnswer(res.toString());
						con.setPostedDate(new Date());
						controller.saveObject(con);
	    	    	}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String uuid = UUID.randomUUID().toString();
    		session().put(StringConstants.USER_UUID, uuid);
    		Cache.set(uuid+"-user", user, StringConstants.DEFAULT_CACHE_TTL);
		}
		map.put("en", isNewUser);
		return toJson(map);
    }
    
    public Result logout() {
    	Cache.remove(session().get(StringConstants.USER_UUID)+"-user");
    	session().clear();
    	return ok(views.html.pages.login.render());
    }
}