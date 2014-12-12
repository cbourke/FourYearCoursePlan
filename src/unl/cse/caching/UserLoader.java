package unl.cse.caching;

import org.apache.log4j.Logger;

import unl.cse.servlet.StudentSearchServlet;

public class UserLoader implements Runnable {

	private static org.apache.log4j.Logger log = Logger.getLogger(UserLoader.class);

	@Override
	public void run() {
		log.info("reloading users from database...");
		StudentSearchServlet.loadUsers();
	}

}
