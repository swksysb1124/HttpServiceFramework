package request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExecutorRequestManager implements RequestManager {
	
	private ExecutorService executor;
	
	public ExecutorRequestManager() {
		executor = Executors.newSingleThreadExecutor();
	}
	
	@Override
	public void execute(Request request) {
		executor.execute(request);
	}

	@Override
	public void finish() {
		executor.shutdown();
	}

}
