package com.sns.gr_optimization.setup;

import java.net.MalformedURLException;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Optional;

import com.sns.gr_optimization.setup.BrowserDriverFactory;

public class BaseTest {

WebDriver driver;
	
	public WebDriver setUp(@Optional("chrome") String browser, @Optional("grid") String environment) throws MalformedURLException {
//		System.out.println(browser);
//		System.out.println(environment);
		// Create Driver
		BrowserDriverFactory factory = new BrowserDriverFactory(browser);
		if (environment.equalsIgnoreCase("grid")) {
			driver = factory.createDriverGrid();
		} else {
			driver = factory.createDriver();
		}
		return driver;
	}

	public void tearDown() {
		// Closing driver
		driver.quit();
	}
}
