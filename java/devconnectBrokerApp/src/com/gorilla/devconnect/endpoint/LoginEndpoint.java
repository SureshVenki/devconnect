/**
 * 
 */
package com.gorilla.devconnect.endpoint;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.gorilla.devconnect.util.RequestUtil;

/**
 * Class in charge of making the login endpoint
 * @author ihuaylupo - Gorilla Logic
 * @version 1.0
 * @since Apr 3, 2019
 */
public class LoginEndpoint {

	/**
	 * 
	 *@param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws ClientProtocolException 
	 * @throws ParseException 
	 * @user ihuaylupo
	 * @since 2019-04-03 
	 */
	public static void main(String[] args) throws ClientProtocolException, HttpException, IOException, ParseException {
		login();
	}

	/**
	 * 
	 *@throws ClientProtocolException
	 *@throws IOException
	 *@throws HttpException
	 *@throws ParseException
	 * @user ihuaylupo
	 * @since 2019-04-03 
	 */
	@SuppressWarnings("unchecked")
	public static String login() throws ClientProtocolException, IOException, HttpException, ParseException {
		Map<String,String> responseData = null;
		JSONObject result = null;
		String responseBody = null;
		String token = null;
		
		
		JSONObject json = new JSONObject();
		//TODO: Brokers are required to update the username and password values introduced in the JSON with their valid account values 
		json.put("username", "Broker_Username_Goes_Here");
		json.put("password", "Broker_Password_Goes_Here");
		
		responseData = RequestUtil.requestWithJSONParameters("https://be-prod.kinsaleins.com/auth", json.toJSONString(), null);
		//Response body with JSON String
		responseBody = responseData.get(RequestUtil.BODY_KEY);
		//JSON Object with the response
		result = RequestUtil.convertToJSONObject(responseBody);
		System.out.println(result.toString());
		token = (String) result.get("token");
		
		return token;
	}

}
