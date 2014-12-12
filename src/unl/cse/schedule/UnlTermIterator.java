package unl.cse.schedule;

import java.util.Iterator;

public class UnlTermIterator implements Iterator<Integer> {

	private int currentTerm;
	private final boolean includeSummer;

	public UnlTermIterator(int beginTerm, boolean includeSummer) { 
		this.includeSummer = includeSummer;
		//validate the term:
		int month = beginTerm % 10;
		if(beginTerm < 1000 || !(month == 1 || (month == 6 && includeSummer) || month == 8)) {
			throw new RuntimeException("Invalid term code: " + beginTerm);
		}
		this.currentTerm = beginTerm;
	}

	public UnlTermIterator(int beginTerm) {
		this(beginTerm, true);
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Integer next() {
		Integer term = this.currentTerm;
		if(this.currentTerm % 10 == 1) { //Spring term
			if(this.includeSummer) {
				this.currentTerm += 5; //Summer term
			} else {
				this.currentTerm += 7;
			}
		} else if(this.currentTerm % 10 == 6) {
			this.currentTerm += 2;
		} else if(this.currentTerm % 10 == 8) {
			this.currentTerm += 3;
		} else {
			throw new IllegalStateException("invalid termcode: " + this.currentTerm);
		}
		return term;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported");
	}
	
}
