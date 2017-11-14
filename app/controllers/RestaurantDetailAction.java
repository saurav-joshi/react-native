package controllers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;

import play.mvc.Result;

import com.iaasimov.dao.service.GenericController;
import com.iaasimov.entity.Conversation;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class RestaurantDetailAction extends CommonAction {
   
	public Result input() {
    	return ok("Restaurant Detail");
    }
	
    public Result resInput(Long cid, String rid) {
    	GenericController controller = GenericController.getInstance();
    	List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("id",  cid));
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
}