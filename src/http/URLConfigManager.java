package http;

public interface URLConfigManager {
	URLInfo findURL(String findKey);
	void fetchUrlDataFromXml();
}
