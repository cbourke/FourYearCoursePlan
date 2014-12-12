package unl.cse.validation;

import java.util.ArrayList;
import java.util.List;

import unl.cse.Course;
import unl.cse.Schedule;

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
		if(s.getCourseToTermMap().keySet().contains(this.c)) {
			if(!s.getCourseToTermMap().keySet().contains(this.prereq) || 
			   s.getCourseToTermMap().get(this.prereq) > s.getCourseToTermMap().get(this.c)) 
			{
				messages.add(this.prereq.getSubject() + " " + this.prereq.getNumber() + " is a prerequisite for " +
						     this.c.getSubject() + " " + this.c.getNumber() + " and must be taken first.");
			}
		}
		return messages;
	}
	
	
}
