import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http.Request;

import java.lang.reflect.Method;

import com.iaasimov.utils.HibernateUtil;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application app) {
		Logger.debug("Start contextInitialized");
		try{
			HibernateUtil.getSessionFactory();
		} catch(Exception e){
			Logger.error("Have error when innitiating context :", e);			
		}
		Logger.debug("End contextInitialized");
	}
	
	@Override
	public void onStop(Application app) {
		HibernateUtil.shutdown();
	}
	
	@Override
	public Action onRequest(Request request, Method actionMethod) {
		return super.onRequest(request, actionMethod);
	}
	
/*	@Override
	public Result onError(RequestHeader request, Throwable t) {
		return Results.internalServerError(
			views.html.error.error_page.render()
		);
	}*/
	
/*	@Override
	public Result onHandlerNotFound(RequestHeader request) {
		return Results.notFound(
			views.html.error.page_not_found.render()
		);
	}*/
	
/*	@Override
	public Result onBadRequest(RequestHeader request, String error) {
		return Results.badRequest(
			views.html.error.page_not_found.render()
		);
	}*/
}