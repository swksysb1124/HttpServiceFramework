package http;

import java.util.List;

public abstract class AbstractRemoteService 
	implements RemoteService{
	
	@Override
	public abstract String getURLString(final URLInfo urlData);
	
	@Override
	public abstract RequestManager getRequesManager();
	
	@Override
	public URLConfigManager getURLConfigManager() {
		return SimpleURLConfigManager.getInstance();
	}
	
	@Override
	public Response getResponse(final String key, List<HeaderField> rqProperties, List<QueryAttribute> rqParams, final String requestBody) {
	    final URLInfo urlData = getURLConfigManager().findURL(key);
	    String urlStr = getURLString(urlData);
	    return getRequesManager().getResponse(urlStr, urlData.getMethod(), rqProperties, rqParams, requestBody);
	} 
	
	@Override
	public void callback(Response response, RequestCallback callback) {
		if(callback != null) {
			if(!response.hasError()) {
				callback.onSuccess(response.getResult());
			}else {
				callback.onFail(response.getErrorType(), response.getErrorMessage());
			}
		}
	}
}
