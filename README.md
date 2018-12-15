# HttpServiceFramework

## 使用方式

### 格式化 MobileAPI 的 URL資訊

將 MobileAPI 的 URL資訊 存成 XML 格式，格式如下：

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

### 介面化 MobileAPI

```java
public interface ExampleMobileAPI {
	void testGET(String attrValue1, String attrValue2, RequestCallback callback);
	void testPOST(String email, String password, RequestCallback callback);
}
```

### 實作 RemoteService 

繼承 `BaseRemoteService` 跟實作 `MobileAPI` 介面

```java
public class ExampleRemoteService 
	extends BaseRemoteService 
	implements ExampleMobileAPI{

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
		
		Response response = getResponse("testGET", rqProperties, rqParams, null);
		callback(response, callback);
	}

	@Override
	public void testPOST(String email, String password, final RequestCallback callback) {
		List<HeaderField> rqProperties = new ArrayList<>();
		rqProperties.add(new HeaderField("Content-Type","application/json"));
		rqProperties.add(new HeaderField("Accept","application/json"));
		rqProperties.add(new HeaderField("User-Agent","json.app"));
		
		String body = JSONParserUtil.toJson(new UserAccount(email, password));
		
		Response response = getResponse("testPOST", rqProperties, null, body);
		callback(response, callback);
	}
}
```

特別說明`BaseRemoteService`內容
```java
public class BaseRemoteService 
	implements RemoteService{
	
	@Override
	public String getURLString(final URLInfo urlData) {
		return DefaultURLStringUtil.toString(urlData);
	}
	
	@Override
	public RequestManager getRequesManager() {
		return new DefaultRequestManager();
	}
	
	@Override
	public URLConfigManager getURLConfigManager() {
		return DefaultURLConfigManager.getInstance();
	}
	
	@Override
	public Response getResponse(final String key, List<HeaderField> rqProperties, List<QueryAttribute> rqParams, final String requestBody) {
	    final URLInfo urlData = getURLConfigManager().findURL(key);
	    String urlStr = getURLString(urlData);
	    return getRequesManager().getResponse(urlStr, urlData.getMethod(), rqProperties, rqParams, requestBody);
	} 
	
	@Override
	public void callback(Response response, RequestCallback callback) {
		DefaultCallbackUtil.callback(response, callback);
	}
}
```


```java
public String getURLString(final URLInfo urlData) {...}
```
定義生成URL字串的方法，這會用在呼叫Http Request前被使用


```java
public RequestManager getRequesManager() {...}
```
定義`RequestManager`的實體，預設使用`HttpURLConnection`實現的`DefaultRequestManager`


```java
public URLConfigManager getURLConfigManager() {...}
```

定義`URLConfigManager`的實體，預設使用`DefaultURLConfigManager`

```java
public void callback(Response response, RequestCallback callback) {...}
```
定義依據`Response`結果回調的動作，預設使用 `DefaultCallbackUtil.callback(response, callback)`


### 實際調用

```java
private static void testGET() {
	ExampleRemoteService.getInstance()
		.testGET("value1", "value2", new RequestCallback() {

		@Override
		public void onSuccess(String content) {
			System.out.println("SUCCESS");
			System.out.println(content);
			// 可以將content內容，在轉換成Entity物件
			// 並依據Entitiy內容做處理
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
