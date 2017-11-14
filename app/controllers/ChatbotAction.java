package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;

import play.Play;
import play.Configuration;
import play.cache.Cache;
import play.data.DynamicForm;
import play.mvc.Result;
import play.mvc.Security;

import com.iaasimov.dao.service.GenericController;
import com.iaasimov.entity.Conversation;
import com.iaasimov.entity.Liked;
import com.iaasimov.entity.User;
import com.iaasimov.utils.StringConstants;
import com.iaasimov.utils.StringUtils;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
@Security.Authenticated(SecuredAuthenticate.class)
public class ChatbotAction extends CommonAction {
  
    public Result input() {
    	boolean hasGEO = false;
    	User user = getUser();
    	if (user != null && user.getCurrentLatitude() != null && user.getCurrentLongitude() != null) {
    		hasGEO = true;
    	}
    	Configuration configuration = Play.application().configuration();
    	String apiUrl = "";
    	if (configuration.getBoolean("booking.external_allow").booleanValue() == true) {
    		apiUrl = configuration.getString("booking.external_api_url");
    	}
    	return ok(views.html.pages.chatbot.render(hasGEO, apiUrl));
    }
    
    public Result storeLatLon() {
    	DynamicForm form = form().bindFromRequest();
    	String lat = form.get("lat");
    	String lon = form.get("lon");
    	User user = getUser();  
    	if (user != null) {
    		user.setCurrentLatitude(lat);
    		user.setCurrentLongitude(lon);
    		try {
				GenericController.getInstance().saveOrUpdateObjects(user);
				Cache.set(session().get(StringConstants.USER_UUID)+"-user", user, StringConstants.DEFAULT_CACHE_TTL);
				
				if ("true".equalsIgnoreCase(form.get("isClickShare"))) {
					return sendQuestion();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	return toJson(new ArrayList<String>());
    }
    
    public Result loadMessages() {
    	GenericController controller = GenericController.getInstance();
    	User user = getUser();
    	List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("userId",  user.getId()));
		List<Order> order = new ArrayList<Order>();
		order.add(Order.desc("id"));
		List<Conversation> lstMessage = controller.findByCriteria(Conversation.class, restrictions, order);
		if (lstMessage == null) {
    		lstMessage = new ArrayList<Conversation>();
    	}
		Collections.sort(lstMessage, new Comparator<Conversation>() {
		    @Override
		    public int compare(Conversation c1, Conversation c2) {
		    	if (c1.getId() > c2.getId()) {
		            return 1;
		    	} else if (c1.getId() < c2.getId()) {
		            return -1;
		    	}
		    	return 0;
		    }
		});
    	restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("userId",  user.getId()));
    	List<Liked> lstLike = controller.findByCriteria(Liked.class, restrictions);
    	String restIds = "";
    	if (lstLike != null) {
    		for (Liked like: lstLike) {
    			restIds += like.getRestId() + ",";
    		}
    	}
    	Map<String, Object> res = new HashMap<String, Object>();
    	res.put("conversation", lstMessage);
    	res.put("liked", restIds);
    	return toJson(res);
    }
    
    public Result sendQuestion() {
    	DynamicForm form = form().bindFromRequest();
    	Map<String, String> ret = new HashMap<String, String>();
    	ret.put("pid", form.get("pid"));
    	String question = form.get("question");
    	ret.put("value", "");
    	ret.put("cid", "");
    	int bookOpt = -1;
    	if (form.get("bookOpt") != null) {
    		bookOpt = Integer.parseInt(form.get("bookOpt"));
    	}
    	if (!StringUtils.isEmpty(question) || bookOpt > 1 || ("true".equalsIgnoreCase(form.get("isClickShare")))) {
    		GenericController controller = GenericController.getInstance();
    		User user = getUser();
    		try {
    			// send message to server to get the analysis
    			JSONObject res = null;
    			JSONObject postBody = new JSONObject();
    			postBody.put("token", user.getToken());
    			if (bookOpt >= 1) {
    				String type = "";
    				postBody.put("restId", form.get("rid"));
    				if (bookOpt == 1) {
    					type = StringConstants.BOOKING_TYPE.ACTIVE.toString();
    				} else if (bookOpt == 2) {
    					type = StringConstants.BOOKING_TYPE.CONFIRM.toString();
    				} else if (bookOpt == 3) {
    					type = StringConstants.BOOKING_TYPE.CANCEL.toString();
    				}
        	    	postBody.put("type", type);
        	    	res = postData("/booking", postBody);
    			} else {
        	    	postBody.put("question", question);
	    	    	postBody.put("type", StringConstants.CONVERSATION_TYPE.QUERY);
	    	    	postBody.put("latitude", user.getCurrentLatitude());
	    	    	postBody.put("longitude", user.getCurrentLongitude());
	    	    	res = postData("/query", postBody);
    			}
    			
    	    	// store into DB
    			Conversation mess = new Conversation();
        		mess.setAnswer(res.toString());
        		mess.setQuestion(question);
        		mess.setPostedDate(new Date());
        		mess.setUserId(user.getId());
				Long cid = controller.saveObject(mess);
				
				ret.put("value", res.toString());
				ret.put("cid", String.valueOf(cid));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	return toJson(ret);
    }
    
    public Result resInput(Long cid, String rid) {
    	GenericController controller = GenericController.getInstance();
    	List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("id",  cid));
		restrictions.add(Restrictions.eq("userId",  getUser().getId()));
    	List<Conversation> lstCon = controller.findByCriteria(Conversation.class, restrictions);
    	JSONObject rest = null;
    	
    	if (lstCon != null && !lstCon.isEmpty()) {
    		Conversation con = lstCon.get(0);
    		JSONObject answer = new JSONObject(con.getAnswer());
    		JSONArray arr = answer.getJSONArray("resultRestaurants");
    		if (arr != null) {
    			for (int i = 0; i < arr.length(); i++) {
    				JSONObject obj = arr.getJSONObject(i);
    				if (obj != null && rid.equalsIgnoreCase(obj.getString("id"))) {
    					rest = obj;
    					break;
    				}
    			}
    		}
    	}
    	
    	if (rest == null) {
    		rest = new JSONObject();
    	} else {
    		int rate = 0;
    		if (!rest.isNull("rating")) {
    			String rating = rest.getString("rating");
        		if (rating != null && rating != "") {
        			rate = Math.round(Integer.parseInt(rating)*5/100);
        		}
    		}
    		rest.put("rating", rate);
    		
    		String website = "";
    		if (!rest.isNull("website")) {
    			website = rest.getString("website");
    		}
    		rest.put("website", website);
    		
    		String cuisine = "";
    		if (!rest.isNull("cuisine")) {
    			cuisine = rest.getString("cuisine").replaceAll(",", ", ");
    		}
    		rest.put("cuisine", cuisine);
    		
    		String phone = "";
    		if (!rest.isNull("telephone")) {
    			phone = rest.getString("telephone");
    		}
    		rest.put("phone", phone);
    		
    		String options = "";
    		if (!rest.isNull("options")) {
    			options = rest.getString("options").replaceAll(",", ", ");
    		}
    		rest.put("options", options);
    	}
    	
    	return ok(views.html.pages.restaurant.render(rest));
    }
    
    public Result like() {
    	DynamicForm form = form().bindFromRequest();
    	String cid = form.get("cid");
    	String rid = form.get("rid");
    	String isLike = form.get("isLike");
    	Map<String, String> ret = new HashMap<String, String>();
    	ret.put("pid", form.get("pid"));
    	try {
    		if (isLike != null) {
            	JSONObject res = null;
    			GenericController controller = GenericController.getInstance();
    			User user = getUser();
    			JSONObject postBody = new JSONObject();
				JSONArray restIds = new JSONArray();
				restIds.put(rid);
				postBody.put("token", user.getToken());
    	    	postBody.put("restIds", restIds);
    			if (isLike.equalsIgnoreCase("1")) {
        	    	res = postData("/like", postBody);
    				
        	    	if (res != null && !res.isNull("success")) {
        	    		Liked like = new Liked();
        	        	like.setConversationId(Long.valueOf(cid));
        	        	like.setRestId(rid);
        	        	like.setUserId(user.getId());
        				controller.saveObject(like);
    				}
        		} else {
        	    	res = postData("/dislike", postBody);
    				
        	    	if (res != null && !res.isNull("success")) {
        	    		List<Criterion> restrictions = new ArrayList<Criterion>();
            			restrictions.add(Restrictions.eq("userId",  user.getId()));
            			restrictions.add(Restrictions.eq("restId",  rid));
            	    	List<Liked> lstLike = controller.findByCriteria(Liked.class, restrictions);
            	    	if (lstLike != null && !lstLike.isEmpty()) {
            	    		controller.deleteObject(lstLike.get(0));
            	    	}
        	    	}
        		}
    			ret.put("value", res.toString());
				ret.put("cid", String.valueOf(cid));
    		}
    		
		} catch (Exception e) {
    		JSONObject err = new JSONObject();
    		err.put("error", e.toString());
    		ret.put("value", err.toString());
		}
    	
    	return toJson(ret);
    }
}