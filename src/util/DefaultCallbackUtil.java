package util;

import http.RequestCallback;
import http.Response;

public class DefaultCallbackUtil {
	public static void callback(Response response, RequestCallback callback) {
		if(callback != null) {
			if(!response.hasError()) {
				callback.onSuccess(response.getResult());
			}else {
				callback.onFail(response.getErrorType(), response.getErrorMessage());
			}
		}
	}
}
