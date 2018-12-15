package http;

import java.util.List;

public interface RemoteService {
	String getURLString(final URLInfo urlInfo);
	RequestManager getRequesManager();
	URLConfigManager getURLConfigManager();
	Response getResponse(final String key, List<HeaderField> headers, List<QueryAttribute> queryAtts, final String requestBody);
	void callback(Response response, RequestCallback callback);
}
