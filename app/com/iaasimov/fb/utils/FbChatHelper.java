package com.iaasimov.fb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;

import com.iaasimov.dao.service.GenericController;
import com.iaasimov.entity.Conversation;
import com.iaasimov.entity.Liked;
import com.iaasimov.entity.User;
import com.iaasimov.fb.contract.Action;
import com.iaasimov.fb.contract.Attachment;
import com.iaasimov.fb.contract.Button;
import com.iaasimov.fb.contract.Element;
import com.iaasimov.fb.contract.Message;
import com.iaasimov.fb.contract.Messaging;
import com.iaasimov.fb.contract.Payload;
import com.iaasimov.fb.contract.QuickReply;
import com.iaasimov.fb.contract.Recipient;
import com.iaasimov.fb.profile.FbProfile;
import com.iaasimov.utils.ImageUtils;
import com.iaasimov.utils.StringConstants;
import com.iaasimov.utils.StringUtils;
import com.google.gson.Gson;

import controllers.CommonAction;
import play.Configuration;
import play.Play;
import controllers.routes;

public class FbChatHelper extends CommonAction {

	private static final HttpClient client = HttpClientBuilder.create().build();
	private static HttpPost httppost = null;
	private static String profileLink = "";
	private static String botAppId = "";

	public FbChatHelper(String appId, String fbPageToken) {
		Configuration configuration  = Play.application().configuration();
		httppost = new HttpPost(configuration.getString("fb.msg_url") + fbPageToken);
		profileLink = configuration.getString("fb.profile_link") + fbPageToken;
		botAppId = appId;
	}

	/**
	 * methods which analyze the postbacks ie. the button clicks sent by
	 * senderId and replies according to it.
	 * 
	 * @param senderId
	 * @param text
	 * @return
	 */
	public List<String> getPostBackReplies(String senderId, String text) {
		String msg = "";
		String arr[] = null;
		if (StringConstants.FB.GREETING_DEFINED_PAYLOAD.equalsIgnoreCase(text)) {
			msg = getGreetingMessage(senderId);
			arr = msg.split("\\.");
		} else if (text.indexOf(StringConstants.FB.LIKE_DEFINED_PAYLOAD) >= 0) {
			text = text.replaceAll(StringConstants.FB.LIKE_DEFINED_PAYLOAD, "");
			return getLikeMessage(senderId, text.substring(0, text.indexOf("_")), text.substring(text.indexOf("_")+1, text.length()));
		} else if (text.indexOf(StringConstants.FB.DISLIKE_DEFINED_PAYLOAD) >= 0) {
			text = text.replaceAll(StringConstants.FB.DISLIKE_DEFINED_PAYLOAD, "");
			msg = getDislikeMessage(senderId, text.substring(0, text.indexOf("_")), text.substring(text.indexOf("_")+1, text.length()));
		} else if (text.indexOf(StringConstants.FB.BOOK_IT_DEFINED_PAYLOAD) >= 0) {
			text = text.replaceAll(StringConstants.FB.BOOK_IT_DEFINED_PAYLOAD, "");
			msg = getBookItMessage(senderId, text.substring(0, text.length()));
		} else if (text.indexOf(StringConstants.FB.CONFIRM_BOOK_DEFINED_PAYLOAD) >= 0) {
			text = text.replaceAll(StringConstants.FB.CONFIRM_BOOK_DEFINED_PAYLOAD, "");
			msg = getConfirmBookMessage(senderId, text.substring(0, text.length()));
		} else if (text.indexOf(StringConstants.FB.CANCEL_BOOK_DEFINED_PAYLOAD) >= 0) {
			text = text.replaceAll(StringConstants.FB.CANCEL_BOOK_DEFINED_PAYLOAD, "");
			msg = getCancelBookMessage(senderId, text.substring(0, text.length()));
		} else if (text.indexOf(StringConstants.FB.SUGGEST_DEFINED_PAYLOAD) >= 0) {
			text = text.replaceAll(StringConstants.FB.SUGGEST_DEFINED_PAYLOAD, "");
//			msg = getSuggestMessage(senderId, text.substring(0, text.length()));
			return getReplies(senderId, text.substring(0, text.length()).replaceAll("_", " "));
		} else {
			msg = "received postback msg: " + text;
		}
		
		List<String> postbackReplies = new ArrayList<String>();
		
		if (msg != "") {
			// only split for the greeting message
			if (arr != null) {
				for (int i = 0; i < arr.length; i++) {
					Message fbMsg = getMsg(arr[i].trim()+".");
					String fbReply = getJsonReply(senderId, fbMsg);
					postbackReplies.add(fbReply);
				}
			} else {
				Message fbMsg = getMsg(msg);
				String fbReply = getJsonReply(senderId, fbMsg);
				postbackReplies.add(fbReply);
			}
			
		}

		return postbackReplies;
	}
	
	/**
	 * Sending greeting message and create new user in our database
	 * @return greeting message
	 */
	private String getGreetingMessage(String senderId) {
		String amsg = "";
    	GenericController controller = GenericController.getInstance();
		try {
			User user = checkAndCreateUser(senderId);
			if (user != null) {
				// query api to get new user greeting message
				JSONObject postBody = new JSONObject();
		    	postBody.put("message", "");
		    	postBody.put("type", StringConstants.CONVERSATION_TYPE.NEW.toString());
		    	postBody.put("token", user.getToken());
		    	JSONObject res = postData("/query", postBody);
				
		    	amsg = res.getString("message");
		    	
		    	String msg[] = res.getString("message").split("\\.");
    	    	for (int i = 0; i < msg.length; i++) {
    	    		// store user new message into db
    				Conversation con = new Conversation();
    				con.setUserId(user.getId());
    				con.setQuestion("");
    				con.setAnswer(res.put("message", msg[i]).toString());
    				con.setPostedDate(new Date());
    				controller.saveObject(con);
    	    	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return amsg;
	}
	
	private List<String> getLikeMessage(String senderId, String cid, String rid) {
		GenericController controller = GenericController.getInstance();
		String msg = "";
		List<String> postbackReplies = new ArrayList<String>();
		try {
			User user = checkAndCreateUser(senderId);
			if (user != null) {
				// query api to get new user greeting message
				JSONObject postBody = new JSONObject();
				JSONArray restIds = new JSONArray();
				restIds.put(rid);
				postBody.put("token", user.getToken());
    	    	postBody.put("restIds", restIds);
				
		    	// store user new message into db
    	    	JSONObject res = postData("/like", postBody);
				
    	    	if (res != null && !res.isNull("message")) {
    	    		msg = res.getString("message");
    	    		Liked like = new Liked();
    	        	like.setConversationId(Long.valueOf(cid));
    	        	like.setRestId(rid);
    	        	like.setUserId(user.getId());
    				controller.saveObject(like);
				}
    	    	
    	    	if (msg != "") {
        	    	Message fbMsg = getMsg(msg);
    				String fbReply = getJsonReply(senderId, fbMsg);
    				postbackReplies.add(fbReply);
    				
    				if (!res.isNull("suggestion")) {
    					getSuggestion(postbackReplies, res, senderId);
    				}
    	    	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return postbackReplies;
	}
	
	private void getSuggestion(List<String> postbackReplies, JSONObject res, String senderId) {
		Message msgSuggest = getMsg(res.getJSONObject("suggestion").getString("message"));
		if (!res.getJSONObject("suggestion").isNull("options")) {
			List<QuickReply> quickReplies = new ArrayList<QuickReply>();
			JSONArray opts = res.getJSONObject("suggestion").getJSONArray("options");
			for (int i = 0; i < opts.length(); i++) {
				QuickReply qr = new QuickReply();
				if ("SHARE_LOCATION".equalsIgnoreCase(opts.getString(i))) {
					qr.setType("location");
				} else {
					qr.setType("text");
					qr.setTitle(opts.getString(i));
					qr.setPayload(StringConstants.FB.SUGGEST_DEFINED_PAYLOAD+(opts.getString(i).replaceAll(" ", "_")));
				}
				quickReplies.add(qr);
			}
			msgSuggest.setQuickReplies(quickReplies);
		}
		String fbReply = getJsonReply(senderId, msgSuggest);
		postbackReplies.add(fbReply);
	}
	
	private String getDislikeMessage(String senderId, String cid, String rid) {
		GenericController controller = GenericController.getInstance();
		String msg = "";
		try {
			User user = checkAndCreateUser(senderId);
			if (user != null) {
				// query api to get new user greeting message
				JSONObject postBody = new JSONObject();
				JSONArray restIds = new JSONArray();
				restIds.put(rid);
				postBody.put("token", user.getToken());
    	    	postBody.put("restIds", restIds);
				
		    	// store user new message into db
    	    	JSONObject res = postData("/dislike", postBody);
				
    	    	if (res != null && !res.isNull("message")) {
    	    		msg = res.getString("message");
    	    		List<Criterion> restrictions = new ArrayList<Criterion>();
        			restrictions.add(Restrictions.eq("userId",  user.getId()));
        			restrictions.add(Restrictions.eq("restId",  rid));
        	    	List<Liked> lstLike = controller.findByCriteria(Liked.class, restrictions);
        	    	if (lstLike != null && !lstLike.isEmpty()) {
        	    		controller.deleteObject(lstLike.get(0));
        	    	}
    	    	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	private String getBookItMessage(String senderId, String rid) {
		String msg = "";
		try {
			User user = checkAndCreateUser(senderId);
			if (user != null) {
				msg = bookAction(user, rid, "booking", StringConstants.BOOKING_TYPE.ACTIVE.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	private String getConfirmBookMessage(String senderId, String rid) {
		String msg = "";
		try {
			User user = checkAndCreateUser(senderId);
			if (user != null) {
				msg = bookAction(user, rid, "", StringConstants.BOOKING_TYPE.CONFIRM.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	private String getCancelBookMessage(String senderId, String rid) {
		String msg = "";
		try {
			User user = checkAndCreateUser(senderId);
			if (user != null) {
				msg = bookAction(user, rid, "", StringConstants.BOOKING_TYPE.CANCEL.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	private String bookAction(User user, String rid, String msg, String type) throws Exception {
		GenericController controller = GenericController.getInstance();
		JSONObject postBody = new JSONObject();
		postBody.put("token", user.getToken());
		postBody.put("restId", rid);
		postBody.put("type", type);
		JSONObject res = postData("/booking", postBody);
		
		// store into DB
		Conversation mess = new Conversation();
		mess.setAnswer(res.toString());
		mess.setQuestion(msg);
		mess.setPostedDate(new Date());
		mess.setUserId(user.getId());
		controller.saveObject(mess);
		return res.getString("message");
	}
	
	private String getSuggestMessage(String senderId, String sgMsg) {
		String msg = "";
		try {
			User user = checkAndCreateUser(senderId);
			if (user != null) {
				JSONObject res = queryDataAndStore(user, sgMsg);
				msg = res.getString("message");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	private User checkAndCreateUser(String senderId) throws Exception {
		FbProfile profile = getFbProfile(senderId);
		if (StringUtils.isEmpty(profile.getFirstName()) && StringUtils.isEmpty(profile.getLastName())) {
			return null;
		}
		String name = profile.getFirstName() + " " + profile.getLastName() + " " + senderId;
		GenericController controller = GenericController.getInstance();
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("fbId",  senderId));
		List<User> lstUser = controller.findByCriteria(User.class, restrictions);
		User user = null;
		if (lstUser != null && !lstUser.isEmpty()) {
			user = lstUser.get(0);
		} else {
			JSONObject putBody = new JSONObject();
			putBody.put("userName", name);
			putBody.put("applicationId", botAppId);
	    	JSONObject res = postData("/create", putBody);
			
			user = new User();
			user.setUserName(name);
			user.setEmail("");
			user.setToken(res.getString("token"));
			user.setFbId(senderId);
			Long userId = controller.saveObject(user);
			user.setId(userId);
		}
		return user;
	}
	
	private FbProfile getFbProfile(String senderId) {
		String link = profileLink.replaceAll("SENDER_ID", senderId);
		return getObjectFromUrl(link, FbProfile.class);
	}

	/**
	 * methos which analyze the simple texts sent by senderId and replies
	 * according to it.
	 * 
	 * @param senderId
	 * @param text
	 * @return
	 */
	public List<String> getReplies(String senderId, String text) {
		List<String> replies = new ArrayList<String>();
		try {
			User user = checkAndCreateUser(senderId);
			if (user != null) {
				JSONObject res = queryDataAndStore(user, text);
				
				if (res.has("warningMessage") && !res.isNull("warningMessage")) {
					Message wMsg = getMsg(res.getString("warningMessage"));
					String wReply = getJsonReply(senderId, wMsg);
					replies.add(wReply);
				}
				
				Message fbMsg = getMsg(res.getString("message"));
				String fbReply = getJsonReply(senderId, fbMsg);
				replies.add(fbReply);
				
				if (!res.isNull("resultRestaurants")) {
					JSONArray arrRest = res.getJSONArray("resultRestaurants");
					Payload payload = new Payload();
					payload.setTemplateType("generic");
					
					List<Element> lstElement = new ArrayList<Element>();
					for (int i = 0; i < arrRest.length(); i++) {
						JSONObject rest = arrRest.getJSONObject(i);
						Element elm = new Element();
						elm.setTitle(rest.getString("name"));
						elm.setSubtitle(rest.getString("address"));
						boolean hasOffer = (!rest.isNull("offers") && rest.getJSONArray("offers").length() > 0);
						String strOffer = "";
						if (hasOffer) {
							JSONObject offer = (rest.getJSONArray("offers")).getJSONObject(0);
							strOffer = offer.getString("short_desc")+" (Exp: "+ offer.getString("end_date")+")";
						}
						elm.setImageUrl(generateImage(hasOffer, "https://dpimages.crayondata.com/high-res-image/" + rest.getString("image"), strOffer));
						Action defaultAction = new Action();
						defaultAction.setType("web_url");
						String restUrl = routes.RestaurantDetailAction.input().absoluteURL(request())+"/"+res.getString("cid")+"/"+rest.getString("id");
						if (restUrl.startsWith("http:")) {
							restUrl = restUrl.replaceFirst("http:", "https:");
						}
						defaultAction.setWebviewHeightRatio("tall");
						defaultAction.setUrl(restUrl);
						elm.setDefaultAction(defaultAction);
						
						List<Button> lstButton = new ArrayList<Button>();
						
						// add button like
						lstButton.add(addButton("postback", "", "Like", StringConstants.FB.LIKE_DEFINED_PAYLOAD+res.getString("cid")+"_"+rest.getString("id")));
						// add button dislike
						lstButton.add(addButton("postback", "", "Don't show this again", StringConstants.FB.DISLIKE_DEFINED_PAYLOAD+res.getString("cid")+"_"+rest.getString("id")));
						
						// check allow booking
						if (rest.optBoolean("allowBooking") || !rest.isNull("chope_link")) {
							//if (configuration.getBoolean("booking.external_allow").booleanValue() == true) {
							if (!rest.isNull("chope_link")) {
								lstButton.add(addButton("web_url", rest.getString("chope_link"), "Book it!", ""));
							} else {
								lstButton.add(addButton("postback", "", "Book it!", StringConstants.FB.BOOK_IT_DEFINED_PAYLOAD+rest.getString("id")));
							}
						}
						
						// check has offers
						if (!rest.isNull("offers")) {
							// TODO for the offer display
						}
						
						// set buttons
						elm.setButtons(lstButton);
						lstElement.add(elm);
					}
					
					payload.setElements(lstElement);
					
					Attachment attachment = new Attachment();
					attachment.setType("template");
					attachment.setPayload(payload);
					
					Message msgRests = new Message();
					msgRests.setAttachment(attachment);
					
					fbReply = getJsonReply(senderId, msgRests);
					replies.add(fbReply);
				}

				// check confirm book and show booking info
				if (res.optBoolean("confirmBooking") && !res.isNull("bookingInfo")) {
					String msg = "\n";
					msg += "Date: "+res.getJSONObject("bookingInfo").getString("date")+"\n";
					msg += "Time: "+res.getJSONObject("bookingInfo").getString("time")+"\n";
					msg += "Pax: "+res.getJSONObject("bookingInfo").getString("pax")+"\n";
					msg += "Phone: "+res.getJSONObject("bookingInfo").getString("phone")+"\n";
					msg += "Email: "+res.getJSONObject("bookingInfo").getString("email")+"\n";
					msg += "Special Request: "+res.getJSONObject("bookingInfo").getString("specialRequest")+"\n";
					Message fbMsgBookInfo = getMsg(msg);
					
					// add confirm and cancel option
					List<QuickReply> quickReplies = new ArrayList<QuickReply>();
					QuickReply qrCancel = new QuickReply();
					qrCancel.setType("text");
					qrCancel.setTitle("Cancel");
					qrCancel.setPayload(StringConstants.FB.CANCEL_BOOK_DEFINED_PAYLOAD+res.getJSONObject("bookingInfo").getString("restaurantId"));
					quickReplies.add(qrCancel);
					
					QuickReply qrConfirm = new QuickReply();
					qrConfirm.setType("text");
					qrConfirm.setTitle("Confirm");
					qrConfirm.setPayload(StringConstants.FB.CONFIRM_BOOK_DEFINED_PAYLOAD+res.getJSONObject("bookingInfo").getString("restaurantId"));
					quickReplies.add(qrConfirm);
					
					fbMsgBookInfo.setQuickReplies(quickReplies);
					fbReply = getJsonReply(senderId, fbMsgBookInfo);
					replies.add(fbReply);
				}
				
				// check show suggest
				if (!res.isNull("suggestion")) {
					getSuggestion(replies, res, senderId);
				}
			}
		} catch (Exception ex) {ex.printStackTrace();}
		return replies;
	}
	
	private String generateImage(boolean hasOffer, String imgUrl, String strOffer) {
		if(!hasOffer) {
			return imgUrl;
		}
		return ImageUtils.generateOfferImage(routes.WelcomeAction.welcome().absoluteURL(request()), imgUrl, strOffer);
	}
	
	private Button addButton(String type, String url, String title, String payload) {
		Button btn = new Button();
		btn.setType(type);
		if (url != null && url != "") {
			btn.setUrl(url);
		}
		btn.setTitle(title);		
		if (payload != null && payload != "") {
			btn.setPayload(payload);
		}
		return btn;
	}
	
	public String getActionTypeOnReply(String senderId) {
		Recipient recipient = new Recipient();
		recipient.setId(senderId);
		Messaging reply = new Messaging();
		reply.setRecipient(recipient);
		reply.setSenderAction("typing_on");

		String jsonReply = new Gson().toJson(reply);
		return jsonReply;
	}
	
	private JSONObject queryDataAndStore(User user, String question) throws Exception {
		JSONObject postBody = new JSONObject();
		postBody.put("token", user.getToken());
		postBody.put("question", question);
    	postBody.put("type", StringConstants.CONVERSATION_TYPE.QUERY);
    	postBody.put("latitude", user.getCurrentLatitude());
    	postBody.put("longitude", user.getCurrentLongitude());
    	JSONObject res = postData("/query", postBody);
		
    	// store into DB
		Conversation mess = new Conversation();
		mess.setAnswer(res.toString());
		mess.setQuestion(question);
		mess.setPostedDate(new Date());
		mess.setUserId(user.getId());
		Long cid = GenericController.getInstance().saveObject(mess);
		res.put("cid", String.valueOf(cid));
		return res;
	}

	private Message getMsg(String msg) {
		Message message = new Message();
		message.setText(msg);
		return message;
	}

	/**
	 * final body which will be sent to fb messenger api through a post call
	 * 
	 * @see WebHookServlet#FB_MSG_URL
	 * @param senderId
	 * @param message
	 * @return
	 */
	private String getJsonReply(String senderId, Message message) {
		Recipient recipient = new Recipient();
		recipient.setId(senderId);
		Messaging reply = new Messaging();
		reply.setRecipient(recipient);
		reply.setMessage(message);

		String jsonReply = new Gson().toJson(reply);
		return jsonReply;
	}

	/**
	 * Returns object of type clazz from an json api link
	 * 
	 * @param link
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private <T> T getObjectFromUrl(String link, Class<T> clazz) {
		T t = null;
		URL url;
		String jsonString = "";
		try {
			url = new URL(link);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				jsonString = jsonString + inputLine;
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!StringUtils.isEmpty(jsonString)) {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, clazz);
		}
		return t;
	}
	
	public String submitToFBMessenger(String jsonReply) {
		try {
			HttpEntity entity = new ByteArrayEntity(jsonReply.getBytes("UTF-8"));
			httppost.setEntity(entity);
			httppost.setHeader("Content-type", "application/json");
			HttpResponse response = client.execute(httppost);
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void updateUserLocation(String senderId, Double lat, Double lon) {
		try{ 
			User user = checkAndCreateUser(senderId);
			user.setCurrentLatitude(String.valueOf(lat));
			user.setCurrentLongitude(String.valueOf(lon));
			GenericController.getInstance().saveOrUpdateObjects(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
