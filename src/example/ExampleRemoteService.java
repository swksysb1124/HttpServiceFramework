package example;

import java.util.ArrayList;
import java.util.List;

import http.AbstractRemoteService;
import http.SimpleRequestManager;
import http.HeaderField;
import http.RequestCallback;
import http.RequestManager;
import http.QueryAttribute;
import http.Response;
import http.URLInfo;
import util.JSONParserUtil;

public class ExampleRemoteService 
	extends AbstractRemoteService 
	implements ExampleMobileApi{

	private ExampleRemoteService() {}
	
	private static ExampleRemoteService instance;
	
	public static ExampleRemoteService getInstance() {
		if(instance == null) {
			synchronized(ExampleRemoteService.class) {
				if(instance == null) {
					instance = new ExampleRemoteService();
				}
			}
		}
		return instance;
	}
	
	@Override
	public String getURLString(URLInfo urlData) {
		StringBuilder builder = new StringBuilder();
		builder.append(urlData.getScheme());
		builder.append(urlData.getHost());
		builder.append(urlData.getPath());
		return builder.toString();
	}
	
	@Override
	public void testGET(String value1, String value2, final RequestCallback callback) {
		List<QueryAttribute> rqParams = new ArrayList<>();
		rqParams.add(new QueryAttribute("key1", value1));
		rqParams.add(new QueryAttribute("key2", value2));
		
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		Response response = getResponse("testGET", rqProperties, rqParams, null);
		callback(response, callback);
	}

	@Override
	public void testPUT(String email, String password, final RequestCallback callback) {
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		String body = JSONParserUtil.toJson(new UserAccount(email, password));
		
		Response response = getResponse("testPUT", rqProperties, null, body);
		callback(response, callback);
	}

	@Override
	public void testPOST(String email, String password, final RequestCallback callback) {
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		String body = JSONParserUtil.toJson(new UserAccount(email, password));
		
		Response response = getResponse("testPOST", rqProperties, null, body);
		callback(response, callback);
	}

	@Override
	public void testDELETE(final RequestCallback callback) {
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		Response response = getResponse("testDELETE", rqProperties, null, null);
		callback(response, callback);
	}

	@Override
	public RequestManager getRequesManager() {
		return new SimpleRequestManager();
	}
}
