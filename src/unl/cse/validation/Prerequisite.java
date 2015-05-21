package unl.cse.validation;

import java.util.ArrayList;
import java.util.List;

import unl.cse.Course;
import unl.cse.CourseUtils;
import unl.cse.Schedule;
import unl.cse.schedule.SemesterUtils;

public class Prerequisite implements Requirement {

	private Course c;
	private Course prereq;
	
	public Prerequisite(Course c, Course prereq) {
		this.c = c; 
		this.prereq = prereq;
	}

	@Override
	public List<String> validate(Schedule s) {
		List<String> messages = new ArrayList<String>();
		int currentTerm = SemesterUtils.getCurrentTermCode();

		//if the schedule contains this course...
		if(s.getCourseToTermMap().keySet().contains(this.c)) {
			//and the course is being taken in a current or future term...
			if(s.getCourseToTermMap().get(this.c) >= currentTerm) {
				//and it does not contain the prerequisite OR the prerequisite comes AFTER this course...
				if(!s.getCourseToTermMap().keySet().contains(this.prereq) || 
				   s.getCourseToTermMap().get(this.prereq) > s.getCourseToTermMap().get(this.c)) 
				{
					Course honorsVersionPreq = CourseUtils.getHonorsVersion(this.prereq);
					//if there is an honors version 
					if(honorsVersionPreq != null) {
						if(!s.getCourseToTermMap().keySet().contains(honorsVersionPreq) ||
							s.getCourseToTermMap().get(honorsVersionPreq) > s.getCourseToTermMap().get(this.c)
						  )
						{
							messages.add(this.prereq.getSubject() + " " + this.prereq.getNumber() + " is a prerequisite for " +
							     this.c.getSubject() + " " + this.c.getNumber() + " and must be taken first.");
						}
					} else {
						//no honors version, so 
						messages.add(this.prereq.getSubject() + " " + this.prereq.getNumber() + " is a prerequisite for " +
							     this.c.getSubject() + " " + this.c.getNumber() + " and must be taken first.");
					}
				}
			}
		}
		return messages;
	}
	
	
}
