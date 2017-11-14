package controllers;

import play.Configuration;
import play.Play;
import play.mvc.Result;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class FBMayabotAction extends FBBotAction {
	public Result mayaProcessGetRequest() {
		Configuration configuration  = Play.application().configuration();
		fbVerifyToken = configuration.getString("maya.bot.fb.verify_token");
		return processGetRequest();
	}
	
	public Result mayaProcessPostRequest() {
		Configuration configuration  = Play.application().configuration();
		appId = configuration.getString("maya.bot.app_id");
		fbPageToken = configuration.getString("maya.bot.fb.page_token");
		return processPostRequest();
	}
}