package controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iaasimov.entity.Customer;
import com.iaasimov.entity.User;
import com.iaasimov.utils.StringConstants;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import play.Play;
import play.cache.Cache;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.4 $ 
 */
public class CommonAction extends Controller {
	
	public static Result toJson(Object obj) {
		return ok(Json.toJson(obj));
	}
	
	public Object getData(String restPath, boolean isById) throws JSONException {
		ClientConfig config = new DefaultClientConfig();
    	Client client = Client.create(config);
        String restUrl=Play.application().configuration().getString("api.rest_url");
    	WebResource webResource = client.resource(UriBuilder.fromUri(restUrl).build());
    	ClientResponse response = webResource.path(restPath).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
    	if (isById) {
    		return new JSONObject(response.getEntity(String.class)); 
    	}
    	return new JSONArray(response.getEntity(String.class));
	}
	
	public static JSONObject getDataByURL(String restUrl) throws UniformInterfaceException, ClientHandlerException, JSONException {
    	ClientConfig config = new DefaultClientConfig();
    	Client client = Client.create(config);
    	WebResource webResource = client.resource(UriBuilder.fromUri(restUrl).build());
    	return new JSONObject(webResource.accept(MediaType.APPLICATION_JSON).get(String.class)); 
	}
	
	public  JSONObject postData(String restPath, JSONObject postBody) throws ClientHandlerException, UniformInterfaceException, JSONException {
		ClientConfig config = new DefaultClientConfig();
    	Client client = Client.create(config);
        String restUrl=Play.application().configuration().getString("api.rest_url");
    	WebResource webResource = client.resource(UriBuilder.fromUri(restUrl).build());
    	ClientResponse response = webResource.path(restPath).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, postBody.toString());
    	return new JSONObject(response.getEntity(String.class));
	}
		
	public  JSONObject putData(String restPath, JSONObject postBody) throws ClientHandlerException, UniformInterfaceException, JSONException {
		ClientConfig config = new DefaultClientConfig();
    	Client client = Client.create(config);
        String restUrl=Play.application().configuration().getString("api.rest_url");
    	WebResource webResource = client.resource(UriBuilder.fromUri(restUrl).build());
    	ClientResponse response = webResource.path(restPath).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, postBody.toString());
    	return new JSONObject(response.getEntity(String.class));
	}
	
	public void deleteData(String restPath) throws ClientHandlerException, UniformInterfaceException, JSONException {
		ClientConfig config = new DefaultClientConfig();
    	Client client = Client.create(config);
        String restUrl=Play.application().configuration().getString("api.rest_url");
    	WebResource webResource = client.resource(UriBuilder.fromUri(restUrl).build());
    	webResource.path(restPath).delete();
	}
	
	public static JSONObject getDataObject(JSONObject object, String type) throws JSONException {
		return object.getJSONObject("_links").getJSONObject(type);
	}
	
	public static User getUser() {
		return (User)Cache.get(session().get(StringConstants.USER_UUID)+"-user");
	}
	
	public static Customer getCustomer() {
		return (Customer)Cache.get(session().get(StringConstants.CUSTOMER_UUID)+"-customer");
	}
}
