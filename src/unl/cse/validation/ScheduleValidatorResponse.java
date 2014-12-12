package unl.cse.validation;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ScheduleValidatorResponse {

	private String status = "noerror";
	private List<String> errorMessages = new ArrayList<String>();
	private List<String> warningMessages = new ArrayList<String>();
	
	public ScheduleValidatorResponse(List<String> errorMessages,
			List<String> warnMessages) {
		this.errorMessages = errorMessages;
		this.warningMessages = warnMessages;
		if(errorMessages.size() == 0) {
			if(warnMessages.size() == 0) {
				this.status = "noerror";
			}
			else {
				this.status = "warning";
			}
		} else {
			this.status = "error";
		}
	}

	
}
