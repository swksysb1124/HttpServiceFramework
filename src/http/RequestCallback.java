package http;

public interface RequestCallback {
	public void onSuccess(String content);
	public void onFail(int errorType, String errorMessage);
}
