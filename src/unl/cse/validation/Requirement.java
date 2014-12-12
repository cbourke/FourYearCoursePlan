package unl.cse.validation;

import java.util.List;

import unl.cse.Schedule;

public interface Requirement {
	public List<String> validate(Schedule s);	
}
