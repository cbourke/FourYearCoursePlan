package unl.cse.caching;

import org.apache.log4j.Logger;

import unl.cse.servlet.CourseServlet;

public class CourseLoader implements Runnable {

	private static org.apache.log4j.Logger log = Logger.getLogger(CourseLoader.class);

	@Override
	public void run() {
		log.info("reloading courses from database...");
		CourseServlet.initialize();
	}

}
