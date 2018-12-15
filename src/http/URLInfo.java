package http;

public class URLInfo {
	
	private String key;
    private long expires; // cache使用
    private String method;
    private String scheme;
    private String host;
    private String path;
    
    public URLInfo(String key, long expires, String method, String scheme, String host, String path) {
		this.key = key;
		this.expires = expires;
		this.method = method;
		this.scheme = scheme;
		this.host = host;
		this.path = path;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
}
