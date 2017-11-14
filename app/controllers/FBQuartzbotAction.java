package controllers;

import play.Configuration;
import play.Play;
import play.mvc.Result;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class FBQuartzbotAction extends FBBotAction {
	public Result quartzProcessGetRequest() {
		Configuration configuration  = Play.application().configuration();
		fbVerifyToken = configuration.getString("quartz.bot.fb.verify_token");
		return processGetRequest();
	}
	
	public Result quartzProcessPostRequest() {
		Configuration configuration  = Play.application().configuration();
		appId = configuration.getString("quartz.bot.app_id");
		fbPageToken = configuration.getString("quartz.bot.fb.page_token");
		return processPostRequest();
	}
}