package request;

import url.XmlV2URLConfigManager;

public class ThreadRequestManager 
	implements RequestManager{
	
	@Override
	public void execute(Request request) {
		new Thread(request).start();
	}

	@Override
	public void finish() {
		System.out.println("RequestManager finish");
	}

}
