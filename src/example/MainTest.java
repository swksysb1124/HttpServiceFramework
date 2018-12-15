package example;

import http.RequestCallback;

public class MainTest {

	public static void main(String[] args) {
		testGET();
		testPOST();
		testPUT();
		testDELETE();
	}
	
	private static void testGET() {
		System.out.println("test GET Method");
		ExampleRemoteService.getInstance()
			.testGET("value1", "value2", new RequestCallback() {

			@Override
			public void onSuccess(String content) {
				System.out.println("SUCCESS");
				System.out.println(content);
			}
	
			@Override
			public void onFail(int errorType, String errorMessage) {
				System.out.println("ERROR");
				System.out.println("[error:"+errorType+"] "+errorMessage);
			}
			
		});
	}
	
	private static void testPOST() {
		System.out.println("test POST Method");
		ExampleRemoteService.getInstance()
			.testPOST("swksysb1124@gmail.com","QWEasd123", new RequestCallback() {
	
			@Override
			public void onSuccess(String content) {
				System.out.println("SUCCESS");
				System.out.println(content);
			}
	
			@Override
			public void onFail(int errorType, String errorMessage) {
				System.out.println("ERROR");
				System.out.println("[error:"+errorType+"] "+errorMessage);
			}
		});
	}
	
	private static void testPUT() {
		System.out.println("test PUT Method");
		ExampleRemoteService.getInstance()
			.testPUT("swksysb1124@gmail.com","QWEasd123", new RequestCallback() {
	
			@Override
			public void onSuccess(String content) {
				System.out.println("SUCCESS");
				System.out.println(content);
			}
	
			@Override
			public void onFail(int errorType, String errorMessage) {
				System.out.println("ERROR");
				System.out.println("[error:"+errorType+"] "+errorMessage);
			}
		});
	}
	
	private static void testDELETE() {
		System.out.println("test DELETE Method");
		ExampleRemoteService.getInstance()
			.testDELETE(new RequestCallback() {
	
			@Override
			public void onSuccess(String content) {
				System.out.println("SUCCESS");
				System.out.println(content);
			}
	
			@Override
			public void onFail(int errorType, String errorMessage) {
				System.out.println("ERROR");
				System.out.println("[error:"+errorType+"] "+errorMessage);
			}
		});
	}

}
