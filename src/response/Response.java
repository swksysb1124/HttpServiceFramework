package response;

public class Response {
	
	public static final int INVALID_COOKIE_ERROR = 1;
    public static final int IO_EXCEPTION_ERROR = 2;
    public static final int SOCKET_EXCEPTION_ERROR = 3;
    public static final int SOCKET_TIMEOUT_EXCEPTION_ERROR = 4;
    public static final int OTHERS_ERROR = 5;

    private boolean isError;
    private int errorType;   // 1為Cookie失效
    private int statusCode;
    private String errorMessage;
    private String result;
    private String cookie;
    
    public boolean hasError(){
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    } 

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getErrorType(){
        return errorType;
    }

    public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public boolean isError() {
		return isError;
	}

	public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	
}
