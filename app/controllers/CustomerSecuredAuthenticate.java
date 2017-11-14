package controllers;

import play.cache.Cache;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import com.iaasimov.entity.Customer;
import com.iaasimov.utils.StringConstants;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class CustomerSecuredAuthenticate extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
    	Customer customer = (Customer)Cache.get(ctx.session().get(StringConstants.CUSTOMER_UUID)+"-customer");
    	if (customer == null) return null;
        return customer.getEmail();
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.WelcomeAction.welcome().url() + "?targetURL="+ctx.request().path());
    }
}