package unl.cse.validation;

import java.util.ArrayList;
import java.util.List;

import unl.cse.Course;
import unl.cse.CourseUtils;
import unl.cse.Schedule;

public class SchedulingValidator implements Requirement {

	public List<String> validate(Schedule s) {
		List<String> messages = new ArrayList<String>();
		
		for(Integer termCode : s.getTermToCoursesMap().keySet()) {
			if(termCode != Schedule.TRANSFER_CREDIT_TERM_CODE) {
				String sem = (termCode % 10 == 8) ? "fall" : "spring";
				String year = ((termCode / 10) % 2 == 0) ? "even" : "odd";
				for(Course c : s.getTermToCoursesMap().get(termCode)) {
					if(c.getSchedule() == null || (!(c.getSchedule().contains(sem) && c.getSchedule().contains(year)))) {
						messages.add("Course "+c.getSubject() + " " + c.getNumber()+" is not offered during "+CourseUtils.termCodeToString(termCode));
					}
				}
			}
		}
		
		return messages;
	}
}
