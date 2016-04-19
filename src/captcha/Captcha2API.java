package captcha;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 
 * A Java wrapper around the 2Captcha API
 *
 */
public class Captcha2API {
	private static final String appKey = "3daa8e8a39f8a5a48065ce00c1e20234";
	private static final String url_2Captcha = "http://2captcha.com/in.php";
	private static final boolean accuracyFeature = true;
	
	private static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	public static List<Integer> breakCaptcha(String captchaImagePath, String captchaTask) throws ClientProtocolException, IOException{
		List<Integer> res = null;
		
		// Upload captcha
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(url_2Captcha);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("key", appKey, ContentType.TEXT_PLAIN);
		builder.addTextBody("recaptcha", "1", ContentType.TEXT_PLAIN);
		if(captchaTask != null && !captchaTask.isEmpty())
			builder.addTextBody("textinstructions", captchaTask, ContentType.TEXT_PLAIN);
		builder.addBinaryBody("file", new File(captchaImagePath), ContentType.APPLICATION_OCTET_STREAM, "file");
		HttpEntity multipart = builder.build();

		uploadFile.setEntity(multipart);

		// Handle response
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		HttpEntity responseEntity = response.getEntity();
		String uploadStatusString = convertStreamToString(responseEntity.getContent());
		String idString = "INVALID";
		if(uploadStatusString.indexOf("OK") == 0){
			int startId = uploadStatusString.indexOf("|") + 1;
			if(startId == 3){
				idString = uploadStatusString.substring(startId);
			}
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Get solved Captcha
		if(!idString.equals("INVALID")){
			String requestString = "http://2captcha.com/res.php?key=" + appKey + "&action=get&id=" + idString;		
			String captchaResultString = "CAPCHA_NOT_READY";
			
			while(captchaResultString.equals("CAPCHA_NOT_READY")){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}	
				URL obj = new URL(requestString);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");

				captchaResultString = convertStreamToString(con.getInputStream());
			}
			
			// Error, return null
			if(captchaResultString.indexOf("OK") != 0){
				System.out.println(captchaResultString);
				return null;
			}
			// Parse result into List
			else{
				res = new ArrayList<Integer>();
				// 100% accuracy Feature has a result with different syntax
				if(accuracyFeature){
					int startIndex = captchaResultString.indexOf("|");
					if(startIndex == -1){
						return res;
					}
					else{
						startIndex += 1;
						String clickString = captchaResultString.substring(startIndex);
						for(int i = 0; i < clickString.length(); i++){
							int index = Integer.parseInt(clickString.charAt(i) + "");
							if(index == 0){
								res.clear();
								return res;
							}
							res.add(index);
						}
					}			
				}
				// No 100% accuracy feature
				else{
					int startIndex = captchaResultString.indexOf("click:");
					if(startIndex == -1){
						return res;
					}
					else{
						startIndex += 6;
						String clickString = captchaResultString.substring(startIndex);
						String[] clickPoints = clickString.split("/");
						for(int i = 0; i < clickPoints.length; i++){
							int index = Integer.parseInt(clickPoints[i]);
							if(index == 0){
								res.clear();
								return res;
							}
							res.add(index);
						}
					}		
				}
			}
		}		
		return res;
	}
	
	public static void main(String[] args) throws Exception{
		List<Integer> clickPoints = breakCaptcha("payload.jpg", null);
		System.out.println(clickPoints);
	}
}
