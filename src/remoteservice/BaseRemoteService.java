package remoteservice;

import java.util.List;

import request.HttpRequest;
import request.HeaderField;
import request.HttpsRequest;
import request.QueryAttribute;
import request.Request;
import request.RequestCallback;
import request.RequestManager;
import response.Response;
import url.URLConfigManager;
import url.URLInfo;

public abstract class BaseRemoteService
	implements RemoteService {
	
	private RequestManager mRequestManager;
	private URLConfigManager mURLConfigManager;

	protected OnDataReceivedListener dataListenrer;
	
	public void setOnDataReceivedListener(OnDataReceivedListener dataListenrer) {
		this.dataListenrer = dataListenrer;
	}
	
	protected OnDataReceivedListener getOnDataReceivedListener() {
		return dataListenrer;
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
		String url = generateDefaultURLString(urlInfo);
		if(interceptURLString(urlInfo)!=null){
			url = interceptURLString(urlInfo);
		}
		Request request;
		if(url.startsWith("https")){
			request = new HttpsRequest(url);
		}else {
			request = new HttpRequest(url);
		}
		request.setKey(key);
		request.setMethod(urlInfo.getMethod());
		request.setRqProperties(rqProperties);
		request.setRqParams(rqParams);
		request.setBody(requestBody);
		request.setCallback(callback);
		getRequestManager().execute(request);
	} 
	
	private String generateDefaultURLString(URLInfo urlInfo) {
		StringBuilder builder = new StringBuilder();
		if(urlInfo == null) {
			System.out.println("urlInfo == null");
		}
		builder.append(urlInfo.getScheme());
		builder.append(urlInfo.getHost());
		builder.append(urlInfo.getPath());
		return builder.toString();
	} 
	
	protected abstract RequestManager injectRequestManager();
	protected abstract URLConfigManager injectURLConfigManager();
	protected abstract String interceptURLString(URLInfo urlInfo);
	
	
	@Override
	public void onSuccess(String key, Response response, String content) {
		if(getOnDataReceivedListener() != null) {
			getOnDataReceivedListener().onSuccess(key, content);
		}
	}

	@Override
	public void onFail(String key, Response response, int errorType, String errorMessage) {
		if(getOnDataReceivedListener() != null) {
			getOnDataReceivedListener().onFail(key, errorType, errorMessage);
		}
	}
	
	protected RequestManager getRequestManager() {
		if(mRequestManager == null) {
			mRequestManager = injectRequestManager();
		}
		return mRequestManager;
	}
	
	protected URLConfigManager getURLConfigManager(){
		if(mURLConfigManager == null) {
			mURLConfigManager = injectURLConfigManager();
		}
		return mURLConfigManager;
	}
	
	@Override
	public void finish() {
		if(mRequestManager != null){
			mRequestManager.finish();
		}
	}
}
