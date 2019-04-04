/**
 * 
 */
package com.gorilla.devconnect.endpoint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.gorilla.devconnect.util.RequestUtil;

/**
 * Class that invokes the devConnect SubmissionEntryEndpoint.
 * @author ihuaylupo - Gorilla Logic
 * @version 1.0
 * @since Apr 3, 2019
 */
public class SubmissionEntryEndpoint {


	public static void main(String[] args) throws ClientProtocolException, HttpException, IOException, ParseException {
		Map<String,String> responseData = null;
		Map<String,File> JSONParameters = null;
		List<File> fileParameters = null;
		JSONObject result = null;
		String responseBody = null;
		String token = null;

		JSONParameters = new HashMap<String,File>();
		JSONParameters.put("submissionEntryRequest", generateJsonFile());
		
		fileParameters = new ArrayList<File>();
		fileParameters.add(generateFile("submissionTest1.txt", "test"));
		fileParameters.add(generateFile("submission129Test2.txt", "LoremIpsum"));

		token = LoginEndpoint.login();
		responseData = RequestUtil.multiPartFormRequest("https://be-prod.kinsaleins.com/submission/entry", JSONParameters, "files", fileParameters, token);
		//Response body with JSON String
		responseBody = responseData.get(RequestUtil.BODY_KEY);
		//JSON Object with the response
		result = RequestUtil.convertToJSONObject(responseBody);
		System.out.println(result.toString());

	}


	/**
	 * Generates a submission entry dummy JSONFile
	 * @return file - the created jsonFile
	 * @user ihuaylupo
	 * @since 2019-04-03 
	 */
	@SuppressWarnings("unchecked")
	private static File generateJsonFile() {
		FileWriter fileWriter = null;

		//Submission Entry
		JSONObject submissionEntry = new JSONObject();
		submissionEntry.put("name_insured_primary","bigSubmission");
		submissionEntry.put("name_insured_secondary","a");
		submissionEntry.put("name_insured_address_line1","Address");
		submissionEntry.put("name_insured_address_line2","a");
		submissionEntry.put("name_insured_address_city","Orlando");
		submissionEntry.put("name_insured_address_state","FL");
		submissionEntry.put("name_insured_address_zip","21231");
		submissionEntry.put("name_insured_phone","+01234567890");
		submissionEntry.put("name_insured_email","test@test.com");
		submissionEntry.put("name_insured_taxid","#%^#$30382");
		submissionEntry.put("broker_email","test@mail.com");
		submissionEntry.put("brokerage_id","test");
		submissionEntry.put("business_unit","undertest");
		submissionEntry.put("rating_state","FL");
		submissionEntry.put("needby_date","08-23-2018");
		submissionEntry.put("effective_date","08-23-2018");
		submissionEntry.put("expiry_date","08-23-2018");
		submissionEntry.put("policy_effective_date","08-23-2018");
		submissionEntry.put("policy_expiry_date","08-23-2018");
		submissionEntry.put("new_renew_ind","true");
		submissionEntry.put("text_subject","subject11");
		submissionEntry.put("text_message","placeholder message");
		submissionEntry.put("policy_unit_number","12");
		submissionEntry.put("policy_base","Months");
		submissionEntry.put("underwriter_uid","10001");
		submissionEntry.put("underwriter_name","Charlotte");

		//Write JSON file
		try {
			//file = new File("submissionEntry.json");
			fileWriter = new FileWriter("submissionEntry.json");
			fileWriter.write(submissionEntry.toJSONString());
			fileWriter.flush();
			
			//File file = 

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new File("submissionEntry.json");
	}

	/**
	 * Generic method to create files.
	 *@param name
	 *@param content
	 *@return file - The file just created
	 * @user ihuaylupo
	 * @since 2019-04-03 
	 */
	private static File generateFile(String name, String content) {
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(name);
			fileWriter.write(content);
			fileWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new File(name);
	}

}

