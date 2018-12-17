package request;
import java.util.List;

import response.Response;
import url.URLInfo;


public abstract class Request 
	implements Runnable{
	
	private String key;
	private String url;
	private String method;
	private List<HeaderField> rqProperties;
	private List<QueryAttribute> rqParams;
	private String body;
	private RequestCallback callback;
	
	public Request(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<HeaderField> getRqProperties() {
		return rqProperties;
	}

	public void setRqProperties(List<HeaderField> rqProperties) {
		this.rqProperties = rqProperties;
	}

	public List<QueryAttribute> getRqParams() {
		return rqParams;
	}

	public void setRqParams(List<QueryAttribute> rqParams) {
		this.rqParams = rqParams;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getURL() {
		return url;
	}

	public void setCallback(RequestCallback callback) {
		this.callback = callback;
	}
	

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RequestCallback getCallback() {
		return callback;
	}

	@Override
	public void run() {
		Response response = getResponse(url, method, rqProperties, rqParams, body);
		if(callback != null) {
			if(!response.hasError()) {
				callback.onSuccess(key, response, response.getResult());
			}else {
				callback.onFail(key, response, response.getErrorType(), response.getErrorMessage());
			}
		}
	}
	
	protected abstract String createURLString(final URLInfo urlInfo);
	protected abstract Response getResponse(String urlStr, String method, List<HeaderField> rqProperties, List<QueryAttribute> rqParams, String body);
	
}
