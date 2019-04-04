/**
 * 
 */
package com.gorilla.devconnect.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Request Util Class - Contains all the methods to make GET,POST,... requests.
 * @author ihuaylupo - GorillaLogic
 * @version 1.0
 * @since Apr 3, 2019
 */
public class RequestUtil {

	private static final String HTTPS_KEY = "https";
	private static final String CONTENT_TYPE_KEY = "Content-type";
	private static final String APPLICATION_JSON_CONTENT_TYPE_KEY = "application/json";
	private static final String ENCODING_KEY = "UTF-8";
	private static final String AUTHORIZATION_KEY = "Authorization";
	private static final String BEARER_TOKEN_KEY = "Bearer ";
	private static final String WAS_RETURNED_MESSAGE_KEY = " was returned.";
	private static final String REQUEST_NOT_WORKED_MESSAGE_KEY = " request failed ";
	public static final String RESPONSE_CODE_KEY = "responseCode";
	public static final String BODY_KEY = "body";



	/**
	 * Makes an http or https request.
	 * @param url
	 * @param methodType
	 * @param isHttps
	 * @return
	 * @throws IOException
	 * @throws HttpException 
	 */
	public static String requestWithoutParameters(String url,String methodType, String authenticationToken) throws IOException, HttpException {
		String objResponse = null;
		HttpURLConnection con = null;
		URL obj = new URL(url);
		if(url.toLowerCase().contains(HTTPS_KEY)) {
			con = (HttpsURLConnection) obj.openConnection();
		}else {
			con = (HttpURLConnection) obj.openConnection();
		}

		con.setRequestMethod(methodType);

		if (null != authenticationToken) {
			con.setRequestProperty(AUTHORIZATION_KEY, BEARER_TOKEN_KEY+authenticationToken);
		}

		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.flush();
		os.close();


		int responseCode = con.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) { 
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			objResponse = response.toString();
		} else {
			StringBuilder builder = getException(methodType, responseCode);
			throw new HttpException(builder.toString());
		} 

		return objResponse;
	}

	/**
	 * Request parameters without authorization
	 * @param url - URL string
	 * @param methodType - Http method type
	 * @return response - Http response
	 * @throws IOException
	 * @throws HttpException 
	 */
	public static String requestWithoutParameters(String url,String methodType) throws IOException, HttpException {
		return requestWithoutParameters(url, methodType, null);
	}

	/**
	 * @param methodType
	 * @param responseCode
	 * @return
	 */
	private static StringBuilder getException(String methodType, int responseCode) {
		StringBuilder builder = new StringBuilder();
		builder.append(methodType);
		builder.append(REQUEST_NOT_WORKED_MESSAGE_KEY);
		builder.append(responseCode);
		builder.append(WAS_RETURNED_MESSAGE_KEY);
		return builder;
	}
	
	/**
	 * Method that executes a multipartFormRequest using application/json parameters and multipartFormData parameters
	 *@param url
	 *@param jsonParameters
	 *@param fileParameters
	 *@param authenticationToken
	 *@return responseData - All the response data
	 *@throws ClientProtocolException
	 *@throws IOException
	 * @user ihuaylupo
	 * @since 2019-04-03 
	 */
	public static Map<String,String> multiPartFormRequest(String url, Map<String,File> jsonParameters, String fileParamsKey, List<File> fileParameters, String authenticationToken) throws ClientProtocolException, IOException {
		HttpResponse response = null;
		CloseableHttpClient httpClient = null;
		HttpPost post = null;
		Map<String,String> responseData = null;
		String responseString = null;
		int responseCode = -1;

		try {
			httpClient = HttpClientBuilder.create().build();
			post = new HttpPost(url);

			if (null != authenticationToken) {
				post.setHeader(AUTHORIZATION_KEY, BEARER_TOKEN_KEY+ authenticationToken);
			}
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			for (Entry<String, File> parameter : jsonParameters.entrySet())  {
				File parameterFile = parameter.getValue();
				FileBody fileBody = new FileBody(parameterFile,ContentType.APPLICATION_JSON, parameterFile.getName());
				builder.addPart(parameter.getKey(), fileBody);
			}

			for (File parameterFile : fileParameters)  {
				FileBody fileBody = new FileBody(parameterFile,ContentType.MULTIPART_FORM_DATA,parameterFile.getName());
				builder.addPart(fileParamsKey, fileBody);

			}

			HttpEntity entity = builder.build();
			post.setEntity(entity);

			response = httpClient.execute(post);
			responseCode = response.getStatusLine().getStatusCode();

			if(responseCode != HttpURLConnection.HTTP_OK){
				throw new HttpException(response.getStatusLine().toString());
			}

			entity = response.getEntity();
			responseString = EntityUtils.toString(entity, ENCODING_KEY);

		}finally {

			if(null != httpClient) {
				try {httpClient.close();} catch (IOException e) {}
			}
			if(null != post) {
				post.releaseConnection();
			}
			response = null;

		}

		responseData = new HashMap<>();
		responseData.put(RESPONSE_CODE_KEY, Integer.toString(responseCode));
		responseData.put(BODY_KEY, responseString);

		return responseData;

	}

	/**
	 * Makes an http or https request using a JSON as body parameters 
	 * @param url - URL String
	 * @param json - String with all the json data
	 * @param authenticationToken - String Bearer token
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws HttpException 
	 * @throws Exception 
	 */
	public static Map<String,String> requestWithJSONParameters(String url, String json, String authenticationToken) throws ClientProtocolException, IOException, HttpException {
		HttpResponse response = null;
		CloseableHttpClient httpClient = null;
		HttpPost post = null;
		String responseString = null;
		Map<String,String> responseData = null;
		int responseCode = -1;

		try {
			httpClient = HttpClientBuilder.create().build();
			post = new HttpPost(url);
			if (null != authenticationToken) {
				post.setHeader(AUTHORIZATION_KEY, BEARER_TOKEN_KEY+ authenticationToken);
			}
			post.setHeader(CONTENT_TYPE_KEY, APPLICATION_JSON_CONTENT_TYPE_KEY);

			post.setEntity(new StringEntity(json));
			response = httpClient.execute(post);

			responseCode = response.getStatusLine().getStatusCode();

			if(responseCode != HttpURLConnection.HTTP_OK){
				throw new HttpException(response.getStatusLine().toString());
			}

			HttpEntity entity = response.getEntity();

			responseString = EntityUtils.toString(entity, ENCODING_KEY);

		}finally {

			if(null != httpClient) {
				try {httpClient.close();} catch (IOException e) {}
			}
			if(null != post) {
				post.releaseConnection();
			}
			response = null;

		}

		responseData = new HashMap<>();
		responseData.put(RESPONSE_CODE_KEY, Integer.toString(responseCode));
		responseData.put(BODY_KEY, responseString);

		return responseData;

	}

	/**
	 * Method that converts a json string to a json object.
	 *@param body - Json String
	 *@return json - JSONObject
	 *@throws ParseException
	 * @user ihuaylupo
	 * @since 2019-04-03 
	 */
	public static JSONObject convertToJSONObject(String body) throws ParseException {
		JSONParser parser = new JSONParser(); 
		JSONObject json = (JSONObject) parser.parse(body);

		return json;
	}

}
