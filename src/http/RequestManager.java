package http;

import java.util.List;

public interface RequestManager {
	Response getResponse(String urlStr, String method, List<HeaderField> rqProperties, List<QueryAttribute> rqParams, String requestBody);
	void printRequestInfo(String url, String method, String body);
}
