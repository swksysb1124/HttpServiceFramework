package request;

import java.util.List;

public interface RequestManager {
	void execute(Request request);
	void finish();
}
