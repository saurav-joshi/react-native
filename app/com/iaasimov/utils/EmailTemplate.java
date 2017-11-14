package com.iaasimov.utils;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $
 */
public class EmailTemplate {
	
	private final static String DEFAULT_FROM_ADDRESS = "support@tastebots.com";
	
	public static void sendWelcomeMail(String customerName, String customerEmail, String customerPassword) throws Exception {
		EMail email = new EMail();
    	email.fromAddress = DEFAULT_FROM_ADDRESS;
    	email.toAddresses = customerName+"<"+customerEmail+">";
    	email.subject = "Welcome to #Maya Tastebot APIs.";
    	email.doNotRedirect = true;
    	StringBuffer body = new StringBuffer();
    	body.append("<div>Dear CUSTOMER_NAME,</div>");
    	body.append("<br>");
    	body.append("<div>Here is your login to Maya Tastebot APIs page.</div>");
    	body.append("<br>");
    	body.append("<div>Login Account: CUSTOMER_EMAIL</div>");
    	body.append("<div>Password: DEFAULT_PASSWORD</div>");
    	body.append("<div>Maya Tastebot APIs webstie: <a href=\"http://maya.tastebots.com\">http://maya.tastebots.com</a></div>");
    	body.append("<br>");
    	body.append("<div>Have an awesome time exploring our APIs and building your chatbot!</div>");
    	body.append("<br>");
    	body.append("<div>Cheers,</div>");
    	body.append("<div>Maya Tastbots</div>");
    	email.body = body.toString().replaceAll("CUSTOMER_NAME", customerName).replaceAll("CUSTOMER_EMAIL", customerEmail).replaceAll("DEFAULT_PASSWORD", customerPassword);
    	try {
			EmailUtils.sendMail(email);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}