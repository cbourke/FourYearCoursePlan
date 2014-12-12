package unl.cse.caching;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CourseRefresher implements ServletContextListener {

    private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.scheduler.scheduleAtFixedRate(new CourseLoader(), 1, 1, TimeUnit.DAYS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		this.scheduler.shutdownNow();
	}
}
