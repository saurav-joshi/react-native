package controllers;

import play.Configuration;
import play.Play;
import play.mvc.Result;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class FBDevbotAction extends FBBotAction {
	public Result devProcessGetRequest() {
		Configuration configuration  = Play.application().configuration();
		fbVerifyToken = configuration.getString("dev.bot.fb.verify_token");
		return processGetRequest();
	}
	
	public Result devProcessPostRequest() {
		Configuration configuration  = Play.application().configuration();
		appId = configuration.getString("dev.bot.app_id");
		fbPageToken = configuration.getString("dev.bot.fb.page_token");
		return processPostRequest();
	}
}