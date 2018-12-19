package request;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import response.Response;
import url.URLInfo;
import util.DefaultURLStringUtil;

public class HttpsRequest extends Request{

	public HttpsRequest(String url) {
		super(url);
	}

	@Override
	protected String createURLString(URLInfo urlInfo) {
		return DefaultURLStringUtil.toString(urlInfo);
	}

	@Override
	protected Response getResponse(String urlStr, String method, List<HeaderField> rqProperties,
			List<QueryAttribute> rqParams, String body) {
		HttpsURLConnection connection = null;
		if(method == null) {
			method = "GET";
		}
	    if(method.equals("GET")){
	    	urlStr = addRequestParameter(urlStr, rqParams);
	    }
	    
	    Response response = new Response();
	    try {
	        
	        URL url= new URL(null, urlStr, new sun.net.www.protocol.https.Handler());
	        
	        connection = (HttpsURLConnection) url.openConnection();
//	        setCertificate(connection);
	        
	        connection.setRequestMethod(method);
	        
	        addRequestProperty(connection, rqProperties);
	        
	        setTimeout(connection);
	        
	        if(method.equals("POST") || method.equals("PUT")){
	        	connection.setDoOutput(true); // 由 connecction 輸出
	        	writeBodyToConnection(getBody(), connection);
	        }
	        
	        int statusCode = connection.getResponseCode();
	        response.setStatusCode(statusCode);
	        
	        if(statusCode == HttpURLConnection.HTTP_OK) {
	        	String cookie = extractSetCookie(connection);
	        	response.setCookie(cookie);
	            InputStream inputStream = connection.getInputStream();
	            String result = getResponseBody(inputStream);
	            setSuccessResponse(result, response);
	        }else {
	            InputStream inputStream = connection.getErrorStream();
	            String errorMessage = getResponseBody(inputStream);
	            setErrorResponse(statusCode, errorMessage, response);
	        }
	        
	    }catch (SocketTimeoutException se) {
	        setErrorResponse(Response.SOCKET_TIMEOUT_EXCEPTION_ERROR, 
	        		se.toString(), response);
	    }catch (SocketException se) {
	        setErrorResponse(Response.SOCKET_EXCEPTION_ERROR, 
	        		se.toString(), response);
	    }catch (IOException e) {
	        setErrorResponse(Response.IO_EXCEPTION_ERROR, 
	        		e.toString(), response);
	    }finally {
	        if (connection != null) {
	            connection.disconnect();
	        }
	    }
	    return response;
	}
	
	private String extractSetCookie(HttpURLConnection connection) {
        String firstCookie = "";
        Map<String, List<String>> map = connection.getHeaderFields();
        for (String key: map.keySet()) {
            if(key == null) continue;
            if(key.equals("Set-Cookie")) {
                List<String> list = map.get(key);
                StringBuilder builder = new StringBuilder();
                for (String str : list) {
                    builder.append(str);
                }
                firstCookie = builder.toString();
                System.out.println("第一次得到的cookie: "+firstCookie);
            }
        }
        return firstCookie;
    }
	
	private void writeBodyToConnection(final String requestBody, final HttpURLConnection connection) 
			throws UnsupportedEncodingException, IOException {
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        dos.write(requestBody.getBytes("UTF-8"));
        dos.flush();
        dos.close(); 
    }
	
	private void addRequestProperty(final HttpURLConnection connection, List<HeaderField> rqProperties) {
		if(rqProperties == null) return;
		for(HeaderField property: rqProperties) {
			connection.setRequestProperty(property.key, property.value);
		}
	}
	
	private String addRequestParameter(String urlStr, List<QueryAttribute> rqParams) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(urlStr);
		if(rqParams != null && !rqParams.isEmpty()) {
    		urlBuilder.append("?");
    		for(int i = 0; i < rqParams.size(); i++) {
    			QueryAttribute param = rqParams.get(i);
    			urlBuilder.append(param.key);
    			urlBuilder.append("=");
    			urlBuilder.append(param.value);
    			if(i != rqParams.size()-1) {
    				urlBuilder.append("&");
    			}
    		}
    	}
		return urlBuilder.toString();
	}
	
	private void setTimeout(final HttpURLConnection connection) {
	    connection.setConnectTimeout(30000);
	    connection.setReadTimeout(30000);
	}
	
	private String getResponseBody(InputStream inputStream) throws IOException {
	    byte[] buffer = new byte[2048];
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    int length;
	    while ((length = inputStream.read(buffer)) != -1) {
	        out.write(buffer, 0, length);
	    }
	    byte[] result = out.toByteArray();
	    out.close();
	    return new String(result);
	}
	
	private void setSuccessResponse(String result, final Response response) {
	    response.setError(false);
	    response.setErrorType(0);
	    response.setErrorMessage("");
	    response.setResult(result);
	}
	
	private void setErrorResponse(int type, String message, final Response response) {
	    response.setError(true);
	    response.setErrorType(type);
	    response.setErrorMessage(message);
	    response.setResult("");
	}

	private void setCertificate(final HttpsURLConnection connection) {
		SSLSocketFactory oldSSLSocketFactory = null;
		HostnameVerifier oldHostnameVerifier = null;
		
		HttpsURLConnection https = (HttpsURLConnection) connection;
		oldSSLSocketFactory = trustAllHosts(https);
		oldHostnameVerifier = https.getHostnameVerifier();
		https.setHostnameVerifier(DO_NOT_VERIFY);
	}
	
	private  SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
		SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLSocketFactory newFactory = sc.getSocketFactory();
			connection.setSSLSocketFactory(newFactory);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return oldFactory;
	}
	
	private final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

		@Override
		public boolean verify(String paramString, SSLSession paramSSLSession) {
			return true;
		}
		
	};
	
	private TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return new X509Certificate[]{};
				}
				
			}
	};
}
