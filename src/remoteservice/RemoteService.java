package remoteservice;

import java.util.List;

import request.HeaderField;
import request.QueryAttribute;
import request.RequestCallback;
import request.RequestManager;
import url.URLConfigManager;
import url.URLInfo;

public interface RemoteService extends RequestCallback{
	
	RequestManager getRequestManager();
	URLConfigManager getURLConfigManager();
	void finish();
	String interceptURLString(URLInfo urlInfo);
	void invoke(final String key, List<HeaderField> headers, List<QueryAttribute> queryAtts, final String requestBody, RequestCallback callback);
}
