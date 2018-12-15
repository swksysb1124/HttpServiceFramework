package http;

import java.util.List;

import util.DefaultCallbackUtil;
import util.DefaultURLStringUtil;

public class BaseRemoteService 
	implements RemoteService{
	
	@Override
	public String getURLString(final URLInfo urlData) {
		return DefaultURLStringUtil.toString(urlData);
	}
	
	@Override
	public RequestManager getRequesManager() {
		return new DefaultRequestManager();
	}
	
	@Override
	public URLConfigManager getURLConfigManager() {
		return DefaultURLConfigManager.getInstance();
	}
	
	@Override
	public Response getResponse(final String key, List<HeaderField> rqProperties, List<QueryAttribute> rqParams, final String requestBody) {
	    final URLInfo urlData = getURLConfigManager().findURL(key);
	    String urlStr = getURLString(urlData);
	    return getRequesManager().getResponse(urlStr, urlData.getMethod(), rqProperties, rqParams, requestBody);
	} 
	
	@Override
	public void callback(Response response, RequestCallback callback) {
		DefaultCallbackUtil.callback(response, callback);
	}
}
