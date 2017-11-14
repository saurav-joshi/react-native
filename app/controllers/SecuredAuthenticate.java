package controllers;

import play.cache.Cache;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import com.iaasimov.entity.User;
import com.iaasimov.utils.StringConstants;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class SecuredAuthenticate extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
    	User user = (User)Cache.get(ctx.session().get(StringConstants.USER_UUID)+"-user");
    	if (user == null) return null;
        return user.getUserName();
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.WelcomeAction.input().url() + "?targetURL="+ctx.request().path());
    }
}