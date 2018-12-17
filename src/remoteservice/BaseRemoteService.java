package remoteservice;

import java.util.List;

import request.BaseRequest;
import request.HeaderField;
import request.QueryAttribute;
import request.Request;
import request.RequestCallback;
import request.RequestManager;
import request.ThreadRequestManager;
import url.XmlURLConfigManager;
import url.URLConfigManager;
import url.URLInfo;

public abstract class BaseRemoteService
	implements RemoteService {

	protected OnDataReceivedListener dataListenrer;
	
	public void setOnDataReceivedListener(OnDataReceivedListener dataListenrer) {
		this.dataListenrer = dataListenrer;
	}
	
	protected OnDataReceivedListener getOnDataReceivedListener() {
		return dataListenrer;
	}
	
	@Override
	public RequestManager getRequestManager() {
		return new ThreadRequestManager();
	}
	
	@Override
	public URLConfigManager getURLConfigManager() {
		return new XmlURLConfigManager();
	}
	
	protected void invoke(final String key, List<HeaderField> rqProperties, List<QueryAttribute> rqParams, 
			final String requestBody) {
		invoke(key, rqProperties, rqParams, requestBody, this);
	}
	
	@Override
	public void invoke(final String key, List<HeaderField> rqProperties, List<QueryAttribute> rqParams, 
			final String requestBody, RequestCallback callback) {
		
		final URLInfo urlInfo = getURLConfigManager().findURL(key);
		if(urlInfo == null) {
			callback.onFail(key, null, 5, "Cannot find the URLInfo for key: " + key);
			return;
		}
		String url = createURLString(urlInfo);
		if(interceptURLString(urlInfo)!=null){
			url = interceptURLString(urlInfo);
		}
		Request request = new BaseRequest(url);
		request.setKey(key);
		request.setMethod(urlInfo.getMethod());
		request.setRqProperties(rqProperties);
		request.setRqParams(rqParams);
		request.setBody(requestBody);
		request.setCallback(callback);
		getRequestManager().execute(request);
	} 
	
	private String createURLString(URLInfo urlInfo) {
		StringBuilder builder = new StringBuilder();
		if(urlInfo == null) {
			System.out.println("urlInfo == null");
		}
		builder.append(urlInfo.getScheme());
		builder.append(urlInfo.getHost());
		builder.append(urlInfo.getPath());
		return builder.toString();
	} 

	@Override
	public String interceptURLString(URLInfo urlInfo) {
		return null;
	}
	
	@Override
	public void finish() {
		if(getRequestManager() != null){
			getRequestManager().finish();
		}
	}
}
