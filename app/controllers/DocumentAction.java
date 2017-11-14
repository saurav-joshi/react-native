package controllers;

import static play.data.Form.form;

import play.mvc.Result;
import play.mvc.Security;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
@Security.Authenticated(CustomerSecuredAuthenticate.class)
public class DocumentAction extends CommonAction {
	
    public Result input() {
    	return ok(views.html.dev.api.render());
    }
}