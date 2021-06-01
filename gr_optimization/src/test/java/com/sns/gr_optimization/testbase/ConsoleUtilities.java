package com.sns.gr_optimization.testbase;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

public class ConsoleUtilities {

	public StringBuilder analyzeLog(WebDriver driver, String page, StringBuilder str, String consolecheck) {
		if(consolecheck.equalsIgnoreCase("Yes")) {
			 LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		     SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		     String message = null;
		     for (LogEntry entry : logEntries) {
		        if((entry.getLevel().equals(Level.SEVERE)) || (entry.getMessage().contains("404"))) {
		        	message = "Console Error in "+ page + " - " + format.format(new Date(entry.getTimestamp())) + " " + entry.getLevel() + " " + entry.getMessage();
		        	System.out.println(message);
		        	str.append("\n");
		            str.append(message);
		        }      	
		    }
		}       
        return str;
    }
}
