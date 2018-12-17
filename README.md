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
	void testGET(String attrValue1, String attrValue2, RequestCallback callback);
	void testPOST(String email, String password, RequestCallback callback);
}
```

### 實作 RemoteService 

**建議實作方式**
1. 繼承 `BaseRemoteService` 類別。 
2. 實作 `WebServiceAPI` 介面。
3. 實作 *單模模式 (Singleton Pattern)*
4. 如果URL有特別規則，可以透過覆寫 `getURLString(urlData)` 方法 來符合其規則。
5. 取得 Response 會透過 `callback(key, response, requestCallback)`回調。
   `BaseRemoteService` 有提供預設的回調方式 `DefaultCallbackUtil.callback(response, callback)`。
   如果預設回調不適用，可以覆寫 `callback(key, response, requestCallback)` 方法。

```java
public class ExampleRemoteService 
	extends BaseRemoteService 
	implements ExampleWebServiceAPI{

	private ExampleRemoteService() {}
	
	private static ExampleRemoteService instance;
	
	// 建議使用單模模式
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
	
	@Override
	public void testGET(String attrValue1, String attrValue2, final RequestCallback callback) {
		List<QueryAttribute> rqParams = new ArrayList<>();
		rqParams.add(new QueryAttribute("key1", attrValue1));
		rqParams.add(new QueryAttribute("key2", attrValue1));
		
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		invoke("testGET", rqProperties, rqParams, null, callback);
	}

	@Override
	public void testPOST(String email, String password, final RequestCallback callback) {
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		String body = JSONParserUtil.toJson(new UserAccount(email, password));
		
		invoke("testPOST", rqProperties, null, body, callback);
	}
}
```

> 特別說明`BaseRemoteService`內容

```java
public class BaseRemoteService 
	implements RemoteService{
	
	// 定義生成 URL字串 的方法，這會用在發送 Http Request 前被使用。預設使用 DefaultURLStringUtil.toString(urlData) 方法
	@Override
	public String getURLString(final URLInfo urlData) {
		return DefaultURLStringUtil.toString(urlData);
	}
	
	// 注入 RequestManager 的實體。預設使用 HttpURLConnection 實現的 DefaultRequestManager 物件
	@Override
	public RequestManager getRequesManager() {
		return new DefaultRequestManager();
	}
	
	// 注入 URLConfigManager 的實體。預設使用 DefaultURLConfigManager 物件
	@Override
	public URLConfigManager getURLConfigManager() {
		return DefaultURLConfigManager.getInstance();
	}
	
	
	@Override
	public void invoke(final String key, List<HeaderField> rqProperties, List<QueryAttribute> rqParams, final String requestBody, RequestCallback callback) {
	    final URLInfo urlData = getURLConfigManager().findURL(key);
	    String urlStr = getURLString(urlData);
	    Response response = getRequesManager().getResponse(urlStr, urlData.getMethod(), rqProperties, rqParams, requestBody);
	    callback(key, response, callback);
	} 
	
	// 定義取得Response後，回調的行為，可以針對 key 做訂製。預設使用 DefaultCallbackUtil.callback(response, callback) 方法
	@Override
	public void callback(String key, Response response, RequestCallback callback) {
		DefaultCallbackUtil.callback(response, callback);
	}
}
```

### 調用方式

```java
private static void testGET() {
	ExampleRemoteService.getInstance()
		.testGET("value1", "value2", new RequestCallback() {

		@Override
		public void onSuccess(String content) {
			System.out.println("SUCCESS");
			System.out.println(content);
			
			// 可以將content內容，轉換成Entity物件
			// 再依據Entitiy內容做處理
			//
			// ex:
			// Entity ent = JSONParserUtil.fromJson(content, Entity.class);
			// processTestGetSuccess(ent);
		}
	
		@Override
		public void onFail(int errorType, String errorMessage) {
			System.out.println("ERROR");
			System.out.println("[error:"+errorType+"] "+errorMessage);
			
			// 可以依據回傳的錯誤訊息做處理
			//
			// ex:
			// processTestGetFail(errorType, errorMessage);

		}
			
	});
}
```
