# HttpServiceFramework

設計由 Java 實現基於 HTTP/HTTPS 協議的傳輸回調框架。
適用於各種 Web Service API 格式，能夠快速的生成對應的遠端服務。

## 使用方式

### 格式化 Web Service API 的 URL資訊

將 Web Service API 的 URL資訊 存成 XML格式，格式如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<config>

	<url
		key="testGET" 
		expires="300"
		method="GET"
		scheme="http://"
		host="httpbin.org"
		path="/get" />
		
	<url
		key="testPOST"
		expires="300"
		method="POST"
		scheme="http://"
		host="httpbin.org"
		path="/post" />
		
</config>
```

### 介面化 Web Service API

```java
public interface ExampleWebServiceAPI {
	void testGET(String attrValue1, String attrValue2);
	void testPOST(String email, String password);
}
```

### 實作 RemoteService 

**建議實作方式**
1. 繼承 `BaseRemoteService` 類別。 
2. 實作 `WebServiceAPI` 介面。透過 `WebServiceAPI` 介面輸入 HTTP請求需要的參數，最後呼叫`invoke(key, rqProperties, List<QueryAttribute> rqParams, body)`。
3. 實作 *單模模式 (Singleton Pattern)*
4. 如果URL有特別規則，可以透過覆寫 `interceptURLString(urlInfo)` 方法 來符合其規則。
5. HTTP請求 結果會透過 `onSuccess(key, response, data)` 跟 `onFail(key, response, errorType, errorMessage)`回調至 實作的`RemoteService`。
   實作的`RemoteService`物件可透過 `OnDataReceivedListener().onSuccess(key, content)` 跟 `OnDataReceivedListener().onFail(key, errorType, errorMessage)` 回傳至主程式。
  
```java
public class ExampleRemoteService 
	extends BaseRemoteService 
	implements ExampleWebServiceAPI{

	// 建議使用單模模式
	private ExampleRemoteService() {}
	
	private static ExampleRemoteService instance;
	
	public static ExampleRemoteService getInstance() {
		if(instance == null) {
			synchronized(ExampleRemoteService.class) {
				if(instance == null) {
					instance = new ExampleRemoteService();
				}
			}
		}
		return instance;
	}
	
	// 實作 WebServiceAPI
	@Override
	public void testGET(String value1, String value2) {
		List<QueryAttribute> rqParams = new ArrayList<>();
		rqParams.add(new QueryAttribute("key1", value1));
		rqParams.add(new QueryAttribute("key2", value2));
		
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		// 將Request所需參數透過 invoke 方法 傳入底層的 RequestManager 完成 HTTP請求動作
		invoke("testGET", rqProperties, rqParams, null);
	}

	@Override
	public void testPOST(String email, String password) {
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		String body = JSONParserUtil.toJson(new UserAccount(email, password));
		
		invoke("testPOST", rqProperties, null, body);
	}

	// HTTP請求結果透過 onSuccess方法 跟 onFail方法 回傳
	@Override
	public void onSuccess(String key, Response response, String content) {
		// 透過 OnDataReceivedListener 回傳至 主程式
		if(getOnDataReceivedListener() != null) {
			getOnDataReceivedListener().onSuccess(key, content);
		}
	}

	@Override
	public void onFail(String key, Response response, int errorType, String errorMessage) {
		if(getOnDataReceivedListener() != null) {
			getOnDataReceivedListener().onFail(key, errorType, errorMessage);
		}
	}
}
```
   
6. 可以換不同的 `RequestManager` 實作，方法是覆寫 `getRequesManager()` 方法。
```java
public class ExampleRemoteService {
	...
	
	// 注入 RequestManager 的實體。預設使用 Thread 實現的 ThreadRequestManager 物件
	@Override
	public RequestManager getRequesManager() {
		return new ThreadRequestManager();
	}
}
```

### 調用方式

```java
public class MainTest {

	public static void main(String[] args) {
		setDataListener();
		testGET();	
	}
	
	private static void setDataListener() {
		// 設定 OnDataReceivedListener
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
		// 調用 API
		ExampleRemoteService.getInstance()
			.testGET("value1", "value2");
	}

}
```
