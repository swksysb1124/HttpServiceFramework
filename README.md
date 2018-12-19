# HttpServiceFramework

設計由 Java 實現基於 HTTP/HTTPS 協議的傳輸回調框架。
適用於各種 Web Service API 格式，能夠快速的生成對應的遠端服務。

## 使用方式

### 格式化 Web Service API 的 URL資訊

將 Web Service API 的 URL資訊 存成 XML格式，格式如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<service
	name="HTTP Bin API Test Service">

	<server
		scheme="http://"
		host="httpbin.org" />

	<apis>
		<api
			key="testGET"
			path="/get"
			expires="300"
			method="GET" />
		
		<api
			key="testPOST"
			path="/post"
			expires="300"
			method="POST" />
		
		<api
			key="testPUT"
			path="/put"
			expires="300"
			method="PUT" />
			
		<api
			key="testDELETE"
			path="/delete"
			expires="300"
			method="DELETE" />
	</apis>
	
</service>
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
1. 繼承 `BaseRemoteService` 類別 並 實作 `WebServiceAPI` 介面。透過 `WebServiceAPI` 介面輸入 HTTP請求需要的參數，最後呼叫`invoke(key, headerFields, queryAttributes, body)`。

```java
public class ExampleRemoteService 
	extends BaseRemoteService 
	implements ExampleWebServiceAPI{
	
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

	...
}
```

2. 實作 *單模模式 (Singleton Pattern)*
```java
public class ExampleRemoteService {

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
}
```
3. 注入 RequestManager，此框架提供 ExecutorRequestManager 及 ThreadRequestManager

```java
public class ExampleRemoteService {
	
	// 注入 RequestManager
	@Override
	protected RequestManager injectRequestManager() {
		return new ExecutorRequestManager();
	}
}
```

4. 注入 URLConfigManager，此框架提供 XmlV2URLConfigManager

```java
public class ExampleRemoteService {

	// 注入 URLConfigManager
	@Override
	protected URLConfigManager injectURLConfigManager() {
		return new XmlV2URLConfigManager("httpbin_test_service_url.xml");
	}

}
```

5. 如果URL有特別規則，可以透過覆寫 `interceptURLString(urlInfo)` 方法 來攔截URL字串

```java
public class ExampleRemoteService {
	
	// 攔截URL字串
	@Override
	protected String interceptURLString(URLInfo urlInfo) {
		return null;
	}
}
```

6. HTTP請求 結果會透過 `onSuccess(key, response, data)` 跟 `onFail(key, response, errorType, errorMessage)`回調至 實作的`RemoteService`。
   實作的`RemoteService`物件再透過 `OnDataReceivedListener().onSuccess(key, content)` 跟 `OnDataReceivedListener().onFail(key, errorType, errorMessage)` 回傳至主程式。 
   
   **注意**：`OnDataReceivedListener().onSuccess(key, content)` 跟 `OnDataReceivedListener().onFail(key, errorType, errorMessage)`基本上還是跑在sub-thread上面的，這要特別這注意的。
  
```java
public class ExampleRemoteService {
	
	...

	// HTTP請求結果透過 onSuccess方法 跟 onFail方法 回傳
	@Override
	public void onSuccess(String key, Response response, String content) {
		
		// 預處理資料
		
		// 記得要加super.onSuccess() 不然不會回調到 OnDataReceivedListener().onSuccess(key, content)
		super.onSuccess(key, response, content); 
		

	}

	@Override
	public void onFail(String key, Response response, int errorType, String errorMessage) {
		
		// 預處理錯誤
		
		// 記得要加super.onFail() 不然不會回調到 OnDataReceivedListener().onFail(key, errorType, errorMessage)
		super.onFail(key, response, errorType, errorMessage); 
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
