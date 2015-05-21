package unl.cse.validation;

import java.util.ArrayList;
import java.util.List;

import unl.cse.Course;
import unl.cse.CourseUtils;
import unl.cse.Schedule;
import unl.cse.schedule.SemesterUtils;

/**
 * Validator to ensure that courses are scheduled in a valid
 * semester (fall/spring; even/odd year)
 * @author cbourke
 *
 */
public class SchedulingValidator implements Requirement {

	public List<String> validate(Schedule s) {
		List<String> messages = new ArrayList<String>();
		
		int currentTerm = SemesterUtils.getCurrentTermCode();
		//for each termCode in the schedule...
		for(Integer termCode : s.getTermToCoursesMap().keySet()) {
			//if termCode is current or future (implicitly excluding transfer term)
			if(termCode >= currentTerm) {
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
