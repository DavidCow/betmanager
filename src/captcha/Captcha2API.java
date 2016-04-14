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

public class Captcha2API {
	private static final String appKey = "3daa8e8a39f8a5a48065ce00c1e20234";
	private static final String url_2Captcha = "http://2captcha.com/in.php";
	
	private static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	public static List<Integer> breakCaptcha(String captchaImagePath) throws ClientProtocolException, IOException{
		List<Integer> res = new ArrayList<Integer>();
		
		// Upload captcha
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(url_2Captcha);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("key", appKey, ContentType.TEXT_PLAIN);
		builder.addTextBody("recaptcha", "1", ContentType.TEXT_PLAIN);
		builder.addBinaryBody("file", new File(captchaImagePath), ContentType.APPLICATION_OCTET_STREAM, "file");
		HttpEntity multipart = builder.build();

		uploadFile.setEntity(multipart);

		// Handle response
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		HttpEntity responseEntity = response.getEntity();
		String uploadStatusString = convertStreamToString(responseEntity.getContent());
		String idString = "INVALIID";
		if(uploadStatusString.indexOf("OK") == 0){
			int startId = uploadStatusString.indexOf("|") + 1;
			if(startId == 3){
				idString = uploadStatusString.substring(startId);
			}
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		if(!idString.equals("INVALID")){
			String requestString = "http://2captcha.com/res.php?key=" + appKey + "&action=get&id=" + idString;
			URL obj = new URL(requestString);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");

			// optional default is GET
			con.setRequestMethod("GET");
			String captchaResultString = convertStreamToString(con.getInputStream());
			if(captchaResultString.equals("CAPCHA_NOT_READY")){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}	
				URL obj2 = new URL(requestString);
				HttpURLConnection con2 = (HttpURLConnection) obj2.openConnection();
				con2.setRequestMethod("GET");

				// optional default is GET
				con2.setRequestMethod("GET");
				captchaResultString = convertStreamToString(con2.getInputStream());
			}
			if(captchaResultString.equals("CAPCHA_NOT_READY")){
				return res;
			}
			else{
				if(captchaResultString.indexOf("OK") == 0){
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
				else{
					return res;
				}
			}
		}		
		return res;
	}
	
	public static void main(String[] args) throws Exception{
		List<Integer> clickPoints = breakCaptcha("payload.jpg");
		System.out.println(clickPoints);
	}
}
