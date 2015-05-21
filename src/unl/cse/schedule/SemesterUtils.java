package unl.cse.schedule;

import org.joda.time.DateTime;

public class SemesterUtils {

	/**
	 * Returns the 4 digit code used by UNL to designate a
	 * term.  This method is based on the current month
	 * according to the system time and so the final digit
	 * will be a value 1--9.
	 * @return
	 */
	public static int getCurrentMonthTermCode() {
		DateTime dt = new DateTime();
		int month = dt.getMonthOfYear(); //1 - 12
		int year  = dt.getYear();
		int termCode = 1000 + ((year % 100) * 10) + (month % 10);
		return termCode;
	}
	
	/**
	 * Returns the 4 digit code used by UNL to designate a
	 * term.  This method is based on the current semester, 
	 * so only the following values will be returned based
	 * on the current month:
	 * <ul>
	 *   <li>1yy1 (Spring)</li>
	 *   <li>1yy6 (1st five week summer session or 8 week summer session)</li>
	 *   <li>1yy7 (2nd five week summer session)</li>
	 *   <li>1yy8 (Fall)</li>
	 * </ul>
	 * where yy is the two digit year code
	 * @return
	 */
	public static int getCurrentTermCode() {
		DateTime dt = new DateTime();
		int month = dt.getMonthOfYear(); //1 - 12
		int year  = dt.getYear();
		int termMonth = 0;
		if(month <= 5) {
			termMonth = 1; //spring
		} else if(month == 6) {
			termMonth = 6; //1st five week session, 8 week session
		} else if(month == 7) {
			termMonth = 7; //2nd five week session
		} else {
			termMonth = 8; //fall
		}
		int termCode = 1000 + ((year % 100) * 10) + termMonth;
		return termCode;
	}
	
	public static void main(String args[]) {
		int x = getCurrentTermCode();
		System.out.println(x);
	}
}
