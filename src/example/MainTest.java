package example;

import remoteservice.OnDataReceivedListener;

public class MainTest {
	
	public static boolean stop = false;

	public static void main(String[] args) {
		setDataListener();
		testGET();
		testPOST();
		testPUT();
		testDELETE();		
	}
	
	private static void setDataListener() {
		ExampleRemoteService.getInstance()
			.setOnDataReceivedListener(new OnDataReceivedListener() {

			@Override
			public void onSuccess(String key, String content) {
				System.out.println(key+": SUCCESS");
				System.out.println(content);
			}

			@Override
			public void onFail(String key, int errorType, String errorMessage) {
				System.out.println(key+": ERROR");
				System.out.println("[error:"+errorType+"] "+errorMessage);
			}
		});
	}
	
	private static void testGET() {
		ExampleRemoteService.getInstance()
			.testGET("value1", "value2");
	}
	
	private static void testPOST() {
		ExampleRemoteService.getInstance()
			.testPOST("swksysb1124@gmail.com","QWEasd123");
	}
	
	private static void testPUT() {
		ExampleRemoteService.getInstance()
			.testPUT("swksysb1124@gmail.com","QWEasd123");
	}
	
	private static void testDELETE() {
		ExampleRemoteService.getInstance()
			.testDELETE();
	}

}
