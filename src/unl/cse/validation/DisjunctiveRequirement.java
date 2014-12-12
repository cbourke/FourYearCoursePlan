package unl.cse.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unl.cse.Schedule;

public class DisjunctiveRequirement implements Requirement{

	private Set<Requirement> requirements;
	private String customMessage;

	public DisjunctiveRequirement() {
		this.requirements = new HashSet<Requirement>();
	}
	
	public DisjunctiveRequirement(String customMessage) {
		this.customMessage = customMessage;
		this.requirements = new HashSet<Requirement>();
	}

	public DisjunctiveRequirement(Set<Requirement> requirements) {
		this.requirements = new HashSet<Requirement>(requirements);
	}
	
	public void addRequirement(Requirement r) {
		this.requirements.add(r);
	}

	@Override
	public List<String> validate(Schedule s) {
		List<String> messages = new ArrayList<String>();
		for(Requirement r : this.requirements) {
			List<String> tmp = r.validate(s);
			if(tmp == null || tmp.isEmpty()) {
				return Collections.emptyList();
			} else {
				messages.addAll(tmp);
			}			
		}
		if(this.customMessage != null) {
			return Arrays.asList(this.customMessage);
		} else {
			return messages;
		}
	}

}
