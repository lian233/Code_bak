package com.wofu.base.job;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TestListener implements ServletContextListener {

	private Timer timer = null;

	public void contextDestroyed(ServletContextEvent sce) {
		timer.cancel();
		
	}

	public void contextInitialized(ServletContextEvent sce) {
		timer = new Timer(true);
		timer.schedule(new TestTimer(), 3000000, 60000);// milliseconds

		

	}

}
