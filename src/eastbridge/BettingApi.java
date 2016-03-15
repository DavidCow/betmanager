package eastbridge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class BettingApi {

	private static final String USER_NAME = "unity_group24";
	private static final String PASSWORD = "Bn06JqWl";
	private static final String API_URL = "http://biweb-unity-01.olesportsresearch.com";
	
	/**
	 * Creates an MD5 Hash from a String
	 * @param s The String to hash
	 * @return The hash
	 */
	private static String getMD5Hash(String s) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(s.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Executes a POST request and returns the response as a String
	 * @param targetURL The URL of the site that we send a request to
	 * @param urlParameters The parameters of the POST request
	 * @return
	 */
	private static String excutePost(String targetURL, String urlParameters) {
		HttpURLConnection connection = null;
		try {
			// Create connection
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	/**
	 * This method creates the MD5 hashed access token using getMD5Hash(String), the current date and the private static fields for the username and password
	 * 
	 * <username>_dd/MM/yyyy_<password>_HH/mm
	 * 
	 * @return the access token
	 */
	private static String getAccessToken(){
		// get date in GMT
		Date date = new Date();
		DateFormat dF = new SimpleDateFormat("dd/MM/yyyy-HH/mm"); 
		dF.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateString = dF.format(date);
		
		// get first half of date for the accessToken
		int firstSegmentEndIndex = dateString.indexOf("-");
		String firstDateSegment = dateString.substring(0, firstSegmentEndIndex);
		// get second segment of date
		String secondDateSegment = dateString.substring(firstSegmentEndIndex + 1);		
		
		// build cleartext String
		String res = USER_NAME + "_" + firstDateSegment + "_" + PASSWORD + "_" + secondDateSegment;
		// encrypt String
		res = getMD5Hash(res);
		return res;
	}
	
	/**
	 * This method queries the user credit from the API
	 * 
	 * @param reqId Id used for internal logging, use a negative number, if it should be ignored
	 * @return The JSON response String sent by the BettingApi
	 */
	public static String getUserCredit(int reqId){
		String url = API_URL + "/getusercredit";
		String accessToken = getAccessToken();
		String params = "username=" + USER_NAME + "&accessToken=" + accessToken;
		
		if(reqId > -1)
			params += "&reqId=" + reqId;
		
		return excutePost(url, params);
	}

	/**
	 * This method is a wrapper around getUserCredit which parses the json return and returns the result as a double
	 * 
	 * @param reqId Id used for internal logging, use a negative number, if it should be ignored
	 * @return The user credit or -1 in case of an exception
	 */
	public static double getUserCreditAsDouble(int reqId){
	
		String jsonReturn = getUserCredit(reqId);
		int creditStart = jsonReturn.indexOf("\"credit\":");
		if(creditStart != -1){
			creditStart += 9;
			int creditEnd = jsonReturn.indexOf(",", creditStart);
			if(creditEnd != -1){
				String creditString = jsonReturn.substring(creditStart, creditEnd);
				double res = -1;
				try {
					res = Double.parseDouble(creditString);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return res;
			}
		}

		return -1;
	}
	
	/**
	 * 
	 * @param company the company, get from record {sbo, crown, ibc, isn, pin, ed}}
	 * @param targetType the type of the target {give, take, over, under, one, two, draw}
	 * @param market the market, get from record {live, today, early}
	 * @param eventId Id of the event, get from record
	 * @param oddId Id of the odd, get from record 
	 * @param createdTime createdTime Id used for internal logging, use a negative number, if it should be ignored
	 * @param reqId reqId Id used for internal logging, use a negative number, if it should be ignored
	 * @return The JSON response String sent by the BettingApi
	 */
	public static String getBetTicket(String company, String targetType, String market, String eventId, int oddId, long createdTime, int reqId){
		String url = API_URL + "/getbetticket";
		String accessToken = getAccessToken();
		String params = "username=" + USER_NAME + "&accessToken=" + accessToken + "&company=" + company + "&targettype=" + targetType + "&market=" + market + "&eventid=" + eventId + "&oddid=" + oddId;
		
		if(createdTime > -1)
			params += "&createdTime=" + reqId;
		if(reqId > -1)
			params += "&reqId=" + reqId;
		
		return excutePost(url, params);		
	}
	
	/**
	 * 
	 * @param company the company, get from record {sbo, crown, ibc, isn, pin, ed}}
	 * @param targetType the type of the target {give, take, over, under, one, two, draw}
	 * @param market the market, get from record {live, today, early}
	 * @param eventId Id of the event, get from record
	 * @param oddId Id of the odd, get from record 
	 * @param targetOdd The odd you want to bet at
	 * @param gold The amount of money intended to stake for a bet
	 * @param acceptBetterOdd Wether we want to automatically accept better odds
	 * @param createdTime createdTime Id used for internal logging, use a negative number, if it should be ignored
	 * @param reqId reqId Id used for internal logging, use a negative number, if it should be ignored
	 * @return The JSON response String sent by the BettingApi
	 */
	public static String placeBet(String company, String targetType, String market, String eventId, int oddId, double targetOdd, double gold, boolean acceptBetterOdd, long createdTime, int reqId){
		String url = API_URL + "/placebet";
		String accessToken = getAccessToken();
		String params = "username=" + USER_NAME + "&accessToken=" + accessToken + "&company=" + company + "&targettype=" + targetType 
			   + "&market=" + market + "&eventid=" + eventId + "&oddid=" + oddId + "&targetodd=" + targetOdd + "&gold=" + gold + "&ecceptbetterodd=" + acceptBetterOdd;
		
		if(createdTime > -1)
			params += "&createdTime=" + reqId;
		if(reqId > -1)
			params += "&reqId=" + reqId;
		
		return excutePost(url, params);			
	}
	
	/**
	 * 
	 * @param id Id of the successful bet placed earlier
	 * @return The JSON response String sent by the BettingApi
	 */
	public static String getBetStatus(String id){
		String url = API_URL + "/getbetstatus";
		String accessToken = getAccessToken();
		String params = "username=" + USER_NAME + "&accessToken=" + accessToken + "&id=" + id;
		
		return excutePost(url, params);		
	}
	
	public static void main(String[] args) throws Exception {
		BettingApi api = new BettingApi();
		String s = api.getUserCredit(1);
		getUserCreditAsDouble(1);
		System.out.println(s);
	}
}
