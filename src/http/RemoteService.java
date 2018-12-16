package http;

import java.util.List;

public interface RemoteService {
	String getURLString(final URLInfo urlInfo);
	RequestManager getRequesManager();
	URLConfigManager getURLConfigManager();
	void invoke(final String key, List<HeaderField> headers, List<QueryAttribute> queryAtts, final String requestBody, RequestCallback callback);
	void callback(final String key, Response response, RequestCallback callback);
}
