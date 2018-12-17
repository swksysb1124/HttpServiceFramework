package util;

import request.RequestCallback;
import response.Response;

public class DefaultCallbackUtil {
	public static void callback(String key, Response response, RequestCallback callback) {
		if(callback != null) {
			if(!response.hasError()) {
				callback.onSuccess(key, response, response.getResult());
			}else {
				callback.onFail(key, response, response.getErrorType(), response.getErrorMessage());
			}
		}
	}
}
