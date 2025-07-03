package com.backend.tracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.backend.tracker.background.DatabaseCreation;

@SpringBootApplication
public class TrackerApplication {

	private static Logger logger=LogManager.getLogger(TrackerApplication.class);	

	public static void main(String[] args) {
		SpringApplication.run(TrackerApplication.class, args);

		// DatabaseCreation.checkTrackerDatabase();
		logger.info("<================   Tracker Application started successfully   ============>");
	}

}
