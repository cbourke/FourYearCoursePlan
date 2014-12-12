package unl.cse.validation;

import java.util.ArrayList;
import java.util.List;

import unl.cse.Course;
import unl.cse.CourseUtils;
import unl.cse.Schedule;

public class SemesterCreditHourRequirements implements Requirement {

	@Override
	public List<String> validate(Schedule s) {

		List<String> errorMsgs = new ArrayList<String>();		
		List<Integer> codes = new ArrayList<Integer>(s.getTermToCoursesMap().keySet());
		codes.remove(new Integer(Schedule.TRANSFER_CREDIT_TERM_CODE));
		//remove summer terms:
		List<Integer> summerTerms = new ArrayList<Integer>();
		for(Integer i : codes) {
			if(CourseUtils.isSummerTerm(i)) {
				summerTerms.add(i);
			}
		}
		codes.removeAll(summerTerms);

		for(int i=0; i<codes.size(); i++) {
			int term = codes.get(i);
			int totalHrs = 0;
			for(Course c : s.getTermToCoursesMap().get(term)) {
				Integer hrs = c.getCreditHours();
				totalHrs += hrs != null ? hrs : 0;
			}
			if(totalHrs > 18) {
				errorMsgs.add(CourseUtils.termCodeToString(term)+" has " + totalHrs + " credit hours: you cannot take more than 18 credit hours in a single semester without permission.");
			}
			if(i != 0 && i != codes.size()-1 && totalHrs < 12) {
				errorMsgs.add(CourseUtils.termCodeToString(term)+" has only " + totalHrs + " credit hours: you must take a minimum of 12 hours to be considered a full time student and should take an average of 15.");
			}
		}

		return errorMsgs;
	}

}
