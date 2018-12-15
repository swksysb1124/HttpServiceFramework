package example;

import http.RequestCallback;

public interface ExampleMobileApi {
	void testGET(String value1, String value2, RequestCallback callback);
	void testPUT(String email, String password, RequestCallback callback);
	void testPOST(String email, String password, RequestCallback callback);
	void testDELETE(RequestCallback callback) ;
}
