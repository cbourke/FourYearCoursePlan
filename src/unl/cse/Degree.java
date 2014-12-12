package unl.cse;

public enum Degree {

	COMPUTER_SCIENCE("Computer Science", 1), COMPUTER_ENGINEERING("Computer Engineering", 2);
	
	private final String name;
	private final Integer id;
	
	private Degree(String name, Integer id) {
		this.name = name;
		this.id = id;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static Degree getDegreeById(Integer id) {
		if(id == 1) {
			return COMPUTER_SCIENCE;
		} else if(id == 2) {
			return COMPUTER_ENGINEERING;
		} else {
			return null;
		}
	}
	
}
