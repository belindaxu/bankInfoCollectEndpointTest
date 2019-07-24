package xuxiaochan.test.airwallex;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import com.alibaba.fastjson.JSONObject;

public class BankInfoEndpointTest{
	String urlStr = null;
	String[] caseDiscriptions = null;
	int index = 0;
	String configStrings[] = null;
	PrintWriter out = null;
	
	private static CloseableHttpClient createAcceptSelfSignedCertificateClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
		
		HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
		
		SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
		
		return HttpClients.custom().setSSLSocketFactory(connectionSocketFactory).build();
	}
    
    @BeforeSuite(alwaysRun = true)
    private void readConfig( ) throws FileNotFoundException {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream("config/user.properties");
    	Properties properties = new Properties();
    	try {
			properties.load(inputStream);
			urlStr = properties.getProperty("requestUrl");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	out= new PrintWriter(new FileOutputStream("projectDebug.log"));
    }

	@DataProvider(name = "bankInfo")
	public String[][] getBankInfoRequestParameters(Method m) throws IOException {
		URL caseDocPath = ClassLoader.getSystemResource("testcaseParameters/testData.csv");
		//存储
		ArrayList<String[]> parametersArray = new ArrayList<String[]>();
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(
					caseDocPath.getPath()));
			String line = null;
			String split = ";";
			while ((line = bReader.readLine()) != null) {
				String[] parameters = line.split(split);
				parametersArray.add(parameters);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[][] parameters = new String[parametersArray.size()][parametersArray.get(0).length];
		caseDiscriptions = new String[parametersArray.size()];
		for(int i = 0; i < parametersArray.size(); i++) {
			parameters[i] = parametersArray.get(i);
			caseDiscriptions[i] = (parametersArray.get(i))[0];
		}
		return parameters;
	}

	@Test(dataProvider = "bankInfo")
	public void bankInfoCollectEndpointRequest(String description, String key_payment_method, String  payment_method, String  key_bank_country_code, String  bank_country_code, String  key_account_name, String  account_name, String  key_account_number, String  account_number, String  key_swift_code, String  swift_code, String  key_bsb, String bsb, String  key_aba, String aba, String expected_statusLine, String  expected_responseEntityString) throws ParseException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		System.out.println(description);
		out.println(description);
		CloseableHttpClient client = createAcceptSelfSignedCertificateClient();
		HttpPost post = new HttpPost(urlStr);
		post.addHeader("Content-Type", "application/json;charset=UTF-8");
		CloseableHttpResponse response = null;
		JSONObject requetJson = new JSONObject();
		requetJson.put(key_payment_method, payment_method);
		requetJson.put(key_bank_country_code, bank_country_code);
		requetJson.put(key_account_name, account_name);
		requetJson.put(key_account_number, account_number);
		requetJson.put(key_swift_code, swift_code);
		requetJson.put(key_bsb, bsb);
		requetJson.put(key_aba, aba);
		out.println(requetJson.toString());
		System.out.println(requetJson.toString());
		post.setEntity(new StringEntity(requetJson.toString(), "UTF-8"));
		try {			response = client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String statusLine = response.getStatusLine().toString();
		System.out.println(statusLine);
		out.println(statusLine);
		String responseEntityString = EntityUtils.toString(response.getEntity());
		System.out.println(responseEntityString);
		out.println(responseEntityString);
		Assert.assertEquals(responseEntityString, expected_responseEntityString);
		Assert.assertEquals(statusLine,expected_statusLine);
	}
	
	@AfterSuite(alwaysRun = true)
    private void tearDown( ) {
    	out.close();
    }
}
