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
import java.util.List;
import java.util.Map;

import response.Response;
import url.URLInfo;
import util.DefaultURLStringUtil;

public class HttpRequest extends Request{
	
	public HttpRequest(String url) {
		super(url);
	}
	
	@Override
	protected String createURLString(final URLInfo urlInfo) {
		return DefaultURLStringUtil.toString(urlInfo);
	}
	
	@Override
	protected Response getResponse(String urlStr, String method, List<HeaderField> rqProperties,
			List<QueryAttribute> rqParams, String body) {
		HttpURLConnection connection = null;
		if(method == null) {
			method = "GET";
		}
	    if(method.equals("GET")){
	    	urlStr = addRequestParameter(urlStr, rqParams);
	    }
	    
	    Response response = new Response();
	    try {
	        URL url = new URL(urlStr);
	        connection = (HttpURLConnection) url.openConnection();
	        
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
}
