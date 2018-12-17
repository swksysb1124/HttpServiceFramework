package util;

import url.URLInfo;

public class DefaultURLStringUtil {
	
	public static String toString(URLInfo urlData) {
		StringBuilder builder = new StringBuilder();
		builder.append(urlData.getScheme());
		builder.append(urlData.getHost());
		builder.append(urlData.getPath());
		return builder.toString();
	}
}
