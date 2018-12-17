package request;

import response.Response;

public interface RequestCallback {
	public void onSuccess(String key, Response response, String content);
	public void onFail(String key, Response response, int errorType, String errorMessage);
}
