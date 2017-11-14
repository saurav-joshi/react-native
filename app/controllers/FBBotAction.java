package controllers;

import static play.data.Form.form;

import java.util.List;

import com.iaasimov.fb.contract.Coordinates;
import com.iaasimov.fb.contract.FbMsgRequest;
import com.iaasimov.fb.contract.Messaging;
import com.iaasimov.fb.utils.FbChatHelper;
import com.iaasimov.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import play.data.DynamicForm;
import play.mvc.Result;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class FBBotAction extends CommonAction {
	protected static FbChatHelper helper = null;
	protected static String fbVerifyToken = "";
	protected static String appId = "";
	protected static String fbPageToken = "";
	
    public Result processGetRequest() {
    	DynamicForm request = form().bindFromRequest();
		String msg = "Error, wrong token";

		String verifyToken = request.get("hub.verify_token");
		String challenge = request.get("hub.challenge");
		// String mode = request.getParameter("hub.mode");

		if (fbVerifyToken.equalsIgnoreCase(verifyToken) && !StringUtils.isEmpty(challenge)) {
			msg = challenge;	
		} else {
			msg = "";
		}
		return ok(msg);
    }
    
    public Result processPostRequest() {
    	JsonNode json = request().body().asJson();
    	if (helper == null) {
    		helper = new FbChatHelper(appId, fbPageToken);
    	}

		/**
		 * convert the string request body in java object
		 */
		FbMsgRequest fbMsgRequest = new Gson().fromJson(json.toString(), FbMsgRequest.class);
		if (fbMsgRequest == null) {
			System.out.println("fbMsgRequest was null");
			return ok();
		}
		List<Messaging> messagings = fbMsgRequest.getEntry().get(0).getMessaging();
		for (Messaging event : messagings) {
			String sender = event.getSender().getId();
			if (!StringUtils.isEmpty(sender)) {
				if (event.getMessage() != null && event.getMessage().getText() != null) {
					if (event.getMessage().getQuickReply() != null) {
						String text = event.getMessage().getQuickReply().getPayload();
						sendTextMessage(sender, text, true);
					} else {
						String text = event.getMessage().getText();
						sendTextMessage(sender, text, false);
					}
				} else if (event.getPostback() != null) {
					String text = event.getPostback().getPayload();
					sendTextMessage(sender, text, true);
				} else if (event.getMessage() != null && event.getMessage().getAttachments() != null && 
						event.getMessage().getAttachments().size() > 0 && event.getMessage().getAttachments().get(0).getPayload() != null &&
						event.getMessage().getAttachments().get(0).getPayload().getCoordinates() != null) {
					Coordinates coord = event.getMessage().getAttachments().get(0).getPayload().getCoordinates();
					helper.updateUserLocation(sender, coord.getLatitude(), coord.getLongitude());
					sendTextMessage(sender, "", false);
				}
			}
		}
		return ok();
    }

    /**
	 * get the text given by senderId and check if it's a postback (button
	 * click) or a direct message by senderId and reply accordingly
	 * 
	 * @param senderId
	 * @param text
	 * @param isPostBack
	 */
	private void sendTextMessage(String senderId, String text, boolean isPostBack) {
		List<String> jsonReplies = null;
		if (isPostBack) {
			jsonReplies = helper.getPostBackReplies(senderId, text);
		} else {
			helper.submitToFBMessenger(helper.getActionTypeOnReply(senderId));
			jsonReplies = helper.getReplies(senderId, text);
		}

		for (String jsonReply : jsonReplies) {
			String result = helper.submitToFBMessenger(jsonReply);
			System.out.println(result);
		}
	}
}