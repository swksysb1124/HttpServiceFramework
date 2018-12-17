package remoteservice;


public interface OnDataReceivedListener {
	public void onSuccess(String key, String content);
	public void onFail(String key, int errorType, String errorMessage);
}
