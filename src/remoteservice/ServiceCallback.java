package remoteservice;

public interface ServiceCallback {
	void onDataReceivedSuccess(String data);
	void onDataReceivedError(int type, String error);
}
