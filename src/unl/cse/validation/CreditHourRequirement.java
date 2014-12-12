package unl.cse.validation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import unl.cse.Course;
import unl.cse.Schedule;

public class CreditHourRequirement implements Requirement {

	private final Integer totalHrs;
	
	public CreditHourRequirement(Integer totalHrs) {
		this.totalHrs = totalHrs;
	}
	
	@Override
	public List<String> validate(Schedule s) {
		int total = 0;
		for(Course c : s.getCourseToTermMap().keySet()) {
			if(c.getCreditHours() != null) {
				total += c.getCreditHours();
			}
		}
		if(total < totalHrs) {
			return Arrays.asList("Total credit hours must be at least " +this.totalHrs + ", you only have "+total+", schedule at least " + (totalHrs - total) + " more.");
		} else {
			return Collections.emptyList();
		}
	}

}
