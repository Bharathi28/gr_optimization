package com.sns.gr_optimization.content;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

//import com.sns.gr_optimization.setup.Authentication;
import com.sns.gr_optimization.testbase.BuyflowUtilities;
import com.sns.gr_optimization.testbase.CommonUtilities;
import com.sns.gr_optimization.testbase.ContentVerfUtilities;
import com.sns.gr_optimization.testbase.DBUtilities;
import com.sns.gr_optimization.testbase.MailUtilities;
import com.sns.gr_optimization.testbase.MerchandisingUtilities;
import com.sns.gr_optimization.testbase.PixelUtilities;
import com.sns.gr_optimization.testbase.SASUtilities;

import de.sstoehr.harreader.HarReaderException;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;

public class ContentVerification {
	
	CommonUtilities comm_obj = new CommonUtilities();
	DBUtilities db_obj = new DBUtilities();
	MailUtilities mailObj = new MailUtilities();
	PixelUtilities pixel_obj = new PixelUtilities();
	ContentVerfUtilities contentObj = new ContentVerfUtilities();
	MerchandisingUtilities merch_obj = new MerchandisingUtilities();
	BuyflowUtilities bf_obj = new BuyflowUtilities();
	SASUtilities sas_obj = new SASUtilities();
//	Authentication auth_obj = new Authentication();
	
	List<List<String>> output = new ArrayList<List<String>>();
	
	String sendReportTo = "manibharathi@searchnscore.com";
	
	// Sharepoint Authentication
//	String access_token = auth_obj.SharepointAuthentication();	
	
	@DataProvider(name="contentInput", parallel=true)
	public Object[][] testData() throws Exception {		
		Object[][] arrayObject = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/ContentValidation/run_input.xlsx", "Content", 1);
		return arrayObject;
	}	
	
	@Test(dataProvider = "contentInput")
	public void content(String env, String brand, String campaign) throws ClassNotFoundException, SQLException, IOException, InterruptedException, HarReaderException {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/Drivers/chromedriver.exe");
		
		File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation", "SEO Templates");
		newDirectory.mkdir();		
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\SEO Templates", brand);
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation", "Merchandising Input");
		newDirectory.mkdir();		
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Merchandising Input", brand);
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation", "Run_Output");
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation", "Harfiles");
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Harfiles", brand);
		newDirectory.mkdir();
		
		String brandcode = db_obj.get_sourceproductlinecode(brand);
		
//		// Download Merchandising data from Sharepoint
//		String download_foldername = "Merchandising%20Data%20Repo/" + brand;
//		String download_filename = "";
//		String download_path = System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Merchandising Input\\" + brand + "\\";
		
//		//Download Merchandising Data
//		if(brand.equalsIgnoreCase("JLoBeauty")) {
//			download_filename =  brandcode + " Web Catalog.xlsx";
//			auth_obj.sharepointFileDownload(access_token, download_foldername, download_filename, download_path);
//		}		
//		download_filename =  campaign + ".xlsx";
//		auth_obj.sharepointFileDownload(access_token, download_foldername, download_filename, download_path);
//		
//		// Download SEO data from Sharepoint
//		download_foldername = "SEO%20Templates/" + brand;
//		download_path = System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\SEO Templates\\" + brand + "\\";
//		
//		auth_obj.sharepointFileDownload(access_token, download_foldername, "Robots.txt", download_path);
//		auth_obj.sharepointFileDownload(access_token, download_foldername, "Sitemap.xml", download_path);
//		auth_obj.sharepointFileDownload(access_token, download_foldername, "SEO Data.xlsx", download_path);
//		auth_obj.sharepointFileDownload(access_token, download_foldername, "Privacy Policy English.txt", download_path);
//		auth_obj.sharepointFileDownload(access_token, download_foldername, "Privacy Policy Spanish.txt", download_path);
//		auth_obj.sharepointFileDownload(access_token, download_foldername, "Terms and Conditions English.txt", download_path);
//		auth_obj.sharepointFileDownload(access_token, download_foldername, "Terms and Conditions Spanish.txt", download_path);
		
		// start the proxy
		BrowserMobProxy proxy = new BrowserMobProxyServer();
		proxy.setTrustAllServers(true);
		proxy.start(0);
		System.out.println("Started proxy server at: " + proxy.getPort());
		
		// get the Selenium proxy object
		Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);	
		
		ChromeOptions options = new ChromeOptions();
		options.setProxy(seleniumProxy);
		options.setAcceptInsecureCerts(true);	   
		options.addArguments("--ignore-certificate-errors");
		options.addArguments("--disable-backgrounding-occluded-windows");
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);	    
		capabilities.setCapability("os", "Windows");
		capabilities.setCapability("os_version", "10");
		capabilities.setCapability("browser_version", "80");	    
		capabilities.setCapability("browser", "Chrome");
		
		WebDriver driver = new ChromeDriver(capabilities);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);
		
		proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
		
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		WebDriverWait wait = new WebDriverWait(driver,50);
		
		String url = db_obj.getUrl(brand, campaign, env);		
		
		String robots_url = "";
		String sitemap_url = "";
		String lastChar = url.substring(url.length() - 1);
		if(lastChar.equalsIgnoreCase("/")) {
			robots_url = url + "robots.txt";
			sitemap_url = url + "sitemap.xml";
		}
		else {
			robots_url = url + "/robots.txt";
			sitemap_url = url + "/sitemap.xml";
		}
		String result = "";
		String remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Validate Robots.txt		
		String robots_file = System.getProperty("user.dir")+"/Input_Output/ContentValidation/SEO Templates/" + brand + "/Robots.txt";
		String expected_robotstxt = comm_obj.readTextFile(robots_file);		
				
		driver.get(robots_url);		
		String actual_robotstxt = driver.findElement(By.xpath("//pre")).getText();		
		
		System.out.println(actual_robotstxt);
		System.out.println(expected_robotstxt);
		
		if(actual_robotstxt.equalsIgnoreCase(expected_robotstxt)) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Robots.txt mismatch";
		}
		System.out.println(result);
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Robots.txt", result, remarks);
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Validate sitemap.xml		
		String sitemap_file = System.getProperty("user.dir")+"/Input_Output/ContentValidation/SEO Templates/" + brand + "/Sitemap.xml";
		String expected_sitemapxml = comm_obj.readTextFile(sitemap_file);
		
		driver.get(sitemap_url);		
		String actual_sitemapxml = driver.getPageSource();
		
		actual_sitemapxml = actual_sitemapxml.replaceAll("\\s+", "");
		actual_sitemapxml = actual_sitemapxml.replaceAll(" ", "");		
		expected_sitemapxml = expected_sitemapxml.replaceAll("\\s+", "");
		expected_sitemapxml = expected_sitemapxml.replaceAll(" ", "");
		
		if(actual_sitemapxml.contains(expected_sitemapxml)) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Sitemap.xml mismatch";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate sitemap.xml", result, remarks);
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Validate Accessibe tool		
		driver.get(url);
		jse.executeScript("window.scrollBy(0,800)", 0);
		Thread.sleep(5000);
		
		if(driver.findElements(By.xpath("//div[@aria-label='Open accessibility options, statement and help']")).size() != 0){
			WebElement accessibe = driver.findElement(By.xpath("//div[@aria-label='Open accessibility options, statement and help']"));
			accessibe.click();
			Thread.sleep(2000);
			if(driver.findElements(By.xpath("//div[@aria-label='Accessibility Adjustments']")).size() != 0) {
				Thread.sleep(2000);
				driver.findElement(By.xpath("//a[@aria-label='Close Accessibility Interface']")).click();
				result = "PASS";
			}
			else {
				result = "FAIL";
				remarks = "Accessibe Icon is present but widget did not open";
			}
		}
		else {
			result = "FAIL";
			remarks = "Accessibe Icon is Missing";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Accessibe", result, remarks);		
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Customer Service Page
		jse.executeScript("window.scrollTo(0, document.body.scrollHeight)", 0);
		Thread.sleep(2000);
		WebElement customerService_link;
		if(brand.equalsIgnoreCase("Smileactives")) {
			customerService_link = driver.findElement(By.xpath("(//a[text()='CUSTOMER SERVICE'])[1]"));
		}
		else {
			customerService_link = driver.findElement(By.xpath("(//a[text()='Customer Service'])[1]"));
		}
		customerService_link.click();
		Thread.sleep(1000);
		jse.executeScript("window.scrollTo(0, document.body.scrollHeight)", 0);
		Thread.sleep(3000);
		
		// Chat Validation			
		String chat_xpath;
		if(brand.equalsIgnoreCase("WestmoreBeauty")) {
			chat_xpath = "(//span[contains(@class,'zendesk-chat')])[4]";
		}
		else if(brand.equalsIgnoreCase("MallyBeauty")) {
			chat_xpath = "(//span[contains(@class,'zendesk-chat')])[3]";
		}
		else {
			chat_xpath = "(//span[contains(@class,'zendesk-chat')])[last()]";
		}
		
		WebElement chat = driver.findElement(By.xpath(chat_xpath));
		if(driver.findElements(By.xpath(chat_xpath)).size() != 0) {
			Thread.sleep(2000);
			chat.click();
			if(driver.findElements(By.xpath("//iframe[@name='ada-embed-iframe']")).size() != 0) {
				result = "PASS";				
				contentObj.closeChat(driver);			
			}
			else {
				result = "FAIL";
				remarks = "Chat link is present but window did not open";
			}
		}
		else {
			result = "FAIL";
			remarks = "Chat link is missing in Customer Service";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate chat functionality", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
				
		// Phone number validation
		WebElement phonenumber_link;
		if(brand.equalsIgnoreCase("WestmoreBeauty")) {
			phonenumber_link = driver.findElement(By.xpath("(//a[contains(@href,'tel')])[6]"));
		}
		else if(brand.equalsIgnoreCase("MeaningfulBeauty")) {
			phonenumber_link = driver.findElement(By.xpath("(//a[contains(@href,'tel')])[9]"));
		}
		else if(brand.equalsIgnoreCase("JLoBeauty")) {
			phonenumber_link = driver.findElement(By.xpath("(//a[contains(@href,'tel')])[4]"));
		}		
		else {
			phonenumber_link = driver.findElement(By.xpath("(//a[contains(@href,'tel')])[last()]"));
		}
		Thread.sleep(2000);
		String phone_number = phonenumber_link.getText();
		if((brand.equalsIgnoreCase("MeaningfulBeauty")) && (phone_number.contains("800"))) {
			result = "PASS";
		}
		else if(phone_number.contains("888")) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Phone number is missing in Customer Service";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Phone Number in Customer Service page", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Terms & Conditions
		WebElement Terms_Conditions;
		if(brand.equalsIgnoreCase("Smileactives")) {
			Terms_Conditions = driver.findElement(By.xpath("(//a[text()='TERMS AND CONDITIONS'])[1]"));
		}
		else if(brand.equalsIgnoreCase("JLoBeauty")) {
			Terms_Conditions = driver.findElement(By.xpath("(//a[text()='Terms & Conditions'])[2]"));
		}
		else {
			Terms_Conditions = driver.findElement(By.xpath("(//a[text()='Terms & Conditions'])[1]"));
		}
		Terms_Conditions.click();
		
		///////////////////////////////////////////////////////////////////////////
		
		// Validate Terms and Conditions - English content
		String tc_english = System.getProperty("user.dir")+"/Input_Output/ContentValidation/SEO Templates/" + brand + "/Terms and Conditions English.txt";
		String expected_tc_english = comm_obj.readTextFile(tc_english);	
		
		String actual_tc_english = driver.findElement(By.xpath("//div[@id='content']")).getText();		
		
		actual_tc_english = actual_tc_english.replaceAll("\\s+", "");
		expected_tc_english = expected_tc_english.replaceAll("\\s+", "");
		
		if(actual_tc_english.equalsIgnoreCase(expected_tc_english)) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Terms and Conditions - English - Content mismatch";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Terms and Conditions - English Content", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		Thread.sleep(2000);
		contentObj.closeChat(driver);			
		
		// Validate Terms and Conditions - Spanish link			
		WebElement spanish_link = driver.findElement(By.xpath("//*[text()='En Español']"));
		spanish_link.click();
		
		WebElement english_link = driver.findElement(By.xpath("//*[text()='In English']"));
		if(english_link.isDisplayed()) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Terms and Conditions - Spanish link is not working";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Terms and Conditions - Spanish link", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Validate Terms and Conditions - Spanish content
		String tc_spanish = System.getProperty("user.dir")+"/Input_Output/ContentValidation/SEO Templates/" + brand + "/Terms and Conditions Spanish.txt";
		String expected_tc_spanish = comm_obj.readTextFile(tc_spanish);	
		
		String actual_tc_spanish = driver.findElement(By.xpath("//div[@id='content']")).getText();	
		
		actual_tc_spanish = actual_tc_spanish.replaceAll("\\s+", "");
		expected_tc_spanish = expected_tc_spanish.replaceAll("\\s+", "");
		
		if(actual_tc_spanish.equalsIgnoreCase(expected_tc_spanish)) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Terms and Conditions - Spanish - Content mismatch";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Terms and Conditions - Spanish Content", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Validate Terms and Conditions - English link		
		driver.findElement(By.xpath("//*[text()='In English']")).click();
		if(driver.findElement(By.xpath("//*[text()='En Español']")).isDisplayed()) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Terms and Conditions - English link is not working";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Terms and Conditions - English link", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Check all links
		List<WebElement> links = driver.findElements(By.xpath("//ol[@class='link-list']//a"));
		String tc_link_results = contentObj.checkBrokenLinks(links, url);		
		
		if(tc_link_results.equalsIgnoreCase("")) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = tc_link_results;
		}		
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Terms and Conditions - TOC - All links", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		Thread.sleep(2000);
		driver.get(url);
		Thread.sleep(3000);
		jse.executeScript("window.scrollTo(0, document.body.scrollHeight)", 0);
		Thread.sleep(3000);
				
		// Privacy Policy
		WebElement Privacy_Policy;
		if(brand.equalsIgnoreCase("Smileactives")) {
			Privacy_Policy = driver.findElement(By.xpath("(//a[text()='PRIVACY POLICY'])[1]"));
		}
		else if(brand.equalsIgnoreCase("JLoBeauty")) {
			Privacy_Policy = driver.findElement(By.xpath("(//a[text()='Privacy Policy'])[2]"));
		}
		else if(brand.equalsIgnoreCase("MallyBeauty")) {
			Privacy_Policy = driver.findElement(By.xpath("//a[@href='/privacy-policy']"));			
		}
		else if(brand.equalsIgnoreCase("WestmoreBeauty")) {
			Privacy_Policy = driver.findElement(By.xpath("//a[@href='/privacy-policy']"));			
		}
		else {
			Privacy_Policy = driver.findElement(By.xpath("(//a[text()='Privacy Policy'])[1]"));
		}
		Privacy_Policy.click();
		
		///////////////////////////////////////////////////////////////////////////
		
		// Validate Privacy Policy - English content
		String pp_english = System.getProperty("user.dir")+"/Input_Output/ContentValidation/SEO Templates/" + brand + "/Privacy Policy English.txt";
		String expected_pp_english = comm_obj.readTextFile(pp_english);	
		
		String actual_pp_english = driver.findElement(By.xpath("(//div[contains(@class,'privacy-container')])[last()]")).getText();		
		
		actual_pp_english = actual_pp_english.replaceAll("\\s+", "");
		expected_pp_english = expected_pp_english.replaceAll("\\s+", "");
		
		if(actual_pp_english.equalsIgnoreCase(expected_pp_english)) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Privacy Policy - English - Content mismatch";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Privacy Policy - English Content", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Validate Privacy Policy - Spanish link		
		WebElement pp_spanish_link;
		if((brand.equalsIgnoreCase("MallyBeauty")) || (brand.equalsIgnoreCase("Smileactives")) || (brand.equalsIgnoreCase("WestmoreBeauty"))) {
			pp_spanish_link = driver.findElement(By.xpath("(//*[text()='En Español'])[last()]"));
		}
		else {
			pp_spanish_link = driver.findElement(By.xpath("//*[text()='En Español']"));
		}
		pp_spanish_link.click();
		
		WebElement pp_english_link = null;
		if((brand.equalsIgnoreCase("MallyBeauty")) || (brand.equalsIgnoreCase("Smileactives")) || (brand.equalsIgnoreCase("WestmoreBeauty"))) {
			pp_english_link = driver.findElement(By.xpath("(//*[text()='In English'])[last()]"));
		}
		else {
			pp_english_link = driver.findElement(By.xpath("//*[text()='In English']"));
		}
		if(pp_english_link.isDisplayed()) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Privacy Policy - Spanish link is not working";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Privacy Policy - Spanish link", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Validate Terms and Conditions - Spanish content
		String pp_spanish = System.getProperty("user.dir")+"/Input_Output/ContentValidation/SEO Templates/" + brand + "/Privacy Policy Spanish.txt";
		String expected_pp_spanish = comm_obj.readTextFile(pp_spanish);	
		
		String actual_pp_spanish = driver.findElement(By.xpath("(//div[contains(@class,'privacy-container')])[last()]")).getText();	
		
		actual_pp_spanish = actual_pp_spanish.replaceAll("\\s+", "");
		expected_pp_spanish = expected_pp_spanish.replaceAll("\\s+", "");		
		actual_pp_spanish = actual_pp_spanish.replaceAll("[^a-zA-Z0-9$]+", "");
		expected_pp_spanish = expected_pp_spanish.replaceAll("[^a-zA-Z0-9$]+", "");
		
		if(actual_pp_spanish.equalsIgnoreCase(expected_pp_spanish)) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Privacy Policy - Spanish - Content mismatch";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Privacy Policy - Spanish Content", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		if((brand.equalsIgnoreCase("MallyBeauty")) || (brand.equalsIgnoreCase("Smileactives")) || (brand.equalsIgnoreCase("WestmoreBeauty"))) {
			pp_spanish_link = driver.findElement(By.xpath("(//*[text()='En Español'])[last()]"));
		}
		else {
			pp_spanish_link = driver.findElement(By.xpath("//*[text()='En Español']"));
		}
		
		if((brand.equalsIgnoreCase("MallyBeauty")) || (brand.equalsIgnoreCase("Smileactives")) || (brand.equalsIgnoreCase("WestmoreBeauty"))) {
			pp_english_link = driver.findElement(By.xpath("(//*[text()='In English'])[last()]"));
		}
		else {
			pp_english_link = driver.findElement(By.xpath("//*[text()='In English']"));
		}		
		
		// Validate Privacy Policy - English link		
		pp_english_link.click();
		if(pp_spanish_link.isDisplayed()) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Privacy Policy - English link is not working";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Privacy Policy - English link", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Check all links
		List<WebElement> pp_links = driver.findElements(By.xpath("//ol[@class='link-list']//a"));
		String pp_link_results = contentObj.checkBrokenLinks(pp_links, url);		
		
		if(pp_link_results.equalsIgnoreCase("")) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = pp_link_results;
		}		
		output = contentObj.generateOutputList(output, env, brand, campaign, "Validate Privacy Policy - TOC - All links", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		Thread.sleep(3000);
		jse.executeScript("window.scrollTo(0, document.body.scrollHeight)", 0);
		Thread.sleep(3000);
		
		// LDU Validation		
		WebElement Do_Not_Sell_My_Info;
		if(brand.equalsIgnoreCase("Smileactives")) {
			Do_Not_Sell_My_Info = driver.findElement(By.xpath("(//a[text()='DO NOT SELL MY INFO'])[1]"));
		}
		else if(brand.equalsIgnoreCase("MallyBeauty")) {
			Do_Not_Sell_My_Info = driver.findElement(By.xpath("//a[@id='donot-sell']"));
		}
		else {
			Do_Not_Sell_My_Info = driver.findElement(By.xpath("(//a[text()='Do Not Sell My Info'])[1]"));
		}
		Do_Not_Sell_My_Info.click();
		contentObj.closeChildWindows(driver);
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		Thread.sleep(2000);
		
		// Home Page Validation
		pixel_obj.defineNewHar(proxy, brand + "HomePage");		
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);	
		pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Harfiles\\" + brand + "\\" + brand + "_homepage.har", driver, "Facebook");
		
		String home_PageSource = driver.getPageSource();
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// HomePage Validations
		
		// Check Do Not Sell My Info link in HomePage		
		result = contentObj.check_donotsellmyinfo(driver, brand, "Home");
		if(result.equalsIgnoreCase("FAIL")) {
			remarks = "Do Not Sell My Info is missing in Homepage";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "HomePage : Validate Do Not Sell My Info", result, remarks);
		remarks = "";
		
		// LDU Validation in HomePage		
		result = contentObj.LDU_validation(brand, "homepage");
		if(result.equalsIgnoreCase("FAIL")) {
			remarks = "LDU String is missing in Homepage";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "HomePage : Validate LDU String", result, remarks);	
		remarks = "";
		
		// SEO Validation in HomePage
		String home_seo = contentObj.seoValidation(brand, "HomePage", home_PageSource);		
		if(home_seo.equalsIgnoreCase("")) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "HomePage - " + home_seo;
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "HomePage : SEO Validation", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		
		// Read all Merchandising Input
		String[][] catalogData = null;
		String[][] merchData = null;
		
		// Read Web Catalog
		if(brand.equalsIgnoreCase("JLoBeauty")) {
			catalogData = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/ContentValidation/Merchandising Input/" + brand + "/" + brandcode + " Web Catalog.xlsx", "Acq", 0);
		}		
		// Read Merchandising Input
		merchData = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/ContentValidation/Merchandising Input/" + brand + "/" + campaign + ".xlsx", "Active Campaign", 0);
		
		String ppid = "";
		String category = "";
		// HashMap variable to collect Kit related details from Merchandising Template
		HashMap<String, String> expectedofferdata_kit = null;
		HashMap<String, String> expectedofferdata_product = null;
		
		if(brand.equalsIgnoreCase("JLoBeauty")) {
			List<String> ppidList = merch_obj.fetch_random_singles(catalogData, 1);
			ppid = ppidList.get(0);
			category = "Product";
			
			// Get the product data from Web Catalog
			HashMap<String, String> product_offerdata = merch_obj.getProdRowfromCatalog(catalogData, ppid, category);
			// Get Price Book IDs
			LinkedHashMap<String, String> catalogPriceBookIDs = merch_obj.getCatalogPriceBookIDs(catalogData, ppid, category);
			expectedofferdata_product = merch_obj.generateExpectedOfferDataForProduct(product_offerdata, null, "", ppid, "-", "No", brand, campaign, category, catalogPriceBookIDs);
		
			pixel_obj.defineNewHar(proxy, brand + "ShopPage");	  
			bf_obj.click_cta(driver, brand, campaign, "Shop");
			pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Harfiles\\" + brand + "\\" + brand + "_shoppage.har", driver, "Facebook");
		
			String shop_PageSource = driver.getPageSource();
			
			///////////////////////////////////////////////////////////////////////////
						
			// Shop Page Validations
			
			// Check Do Not Sell My Info link in ShopPage			
			result = contentObj.check_donotsellmyinfo(driver, brand, "Shop");
			if(result.equalsIgnoreCase("FAIL")) {
				remarks = "Do Not Sell My Info is missing in ShopPage";
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "ShopPage : Validate Do Not Sell My Info", result, remarks);	
			remarks = "";
			
			// LDU Validation in ShopPage
			result = contentObj.LDU_validation(brand, "shoppage");
			if(result.equalsIgnoreCase("FAIL")) {
				remarks = "LDU String is missing in Shoppage";
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "ShopPage : Validate LDU String", result, remarks);	
			remarks = "";	
			// SEO Validation in ShopPage
			String shop_seo = contentObj.seoValidation(brand, "ShopPage", shop_PageSource);			
			if(shop_seo.equalsIgnoreCase("")) {
				result = "PASS";
			}
			else {
				result = "FAIL";
				remarks = "ShopPage - " + shop_seo;
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "ShopPage : SEO Validation", result, remarks);	
			remarks = "";	
			///////////////////////////////////////////////////////////////////////////
			
			// Select offer			
			pixel_obj.defineNewHar(proxy, brand + "PDPage");	
			sas_obj.select_offer(driver, expectedofferdata_product, category);
			pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Harfiles\\" + brand + "\\" + brand + "_pdpage.har", driver, "Facebook");
			
			String pdp_PageSource = driver.getPageSource();
			
			///////////////////////////////////////////////////////////////////////////
			
			// PDP Page Validations
				
			// Check Do Not Sell My Info link in PDPPage
			result = contentObj.check_donotsellmyinfo(driver, brand, "PDP");
			if(result.equalsIgnoreCase("FAIL")) {
				remarks = "Do Not Sell My Info is missing in PDPPage";
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "PDPPage : Validate Do Not Sell My Info", result, remarks);	
			remarks = "";
			
			// LDU Validation in PDPPage
			result = contentObj.LDU_validation(brand, "pdpage");
			if(result.equalsIgnoreCase("FAIL")) {
				remarks = "LDU String is missing in PDPpage";
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "PDPPage : Validate LDU String", result, remarks);	
			remarks = "";
			
			// SEO Validation in PDPPage
			String productname = expectedofferdata_product.get("Product Name");
			if(productname.contains("Star Power Duo")) {
				productname = "Star Power Duo";
			}
			String pdp_seo = contentObj.seoValidation(brand, productname, pdp_PageSource);
			if(shop_seo.equalsIgnoreCase("")) {
				result = "PASS";
			}
			else {
				result = "FAIL";
				remarks = "PDPPage - " + pdp_seo;
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "PDPPage : SEO Validation", result, remarks);	
			remarks = "";
			///////////////////////////////////////////////////////////////////////////
			
			// Move to Checkout
			pixel_obj.defineNewHar(proxy, brand + "CheckoutPage");
			bf_obj.move_to_checkout(driver, brand, campaign, category);
			
			if((brand.equalsIgnoreCase("JLoBeauty")) && (expectedofferdata_product.get("Product Name").contains("Star Power Duo"))) {
				sas_obj.select_prepu(driver, brand, campaign, expectedofferdata_product);
			}			
			pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Harfiles\\" + brand + "\\" + brand + "_checkoutpage.har", driver, "Facebook");
		}
		else {
			ppid = merch_obj.getrandomPPID(merchData);
			category = "Kit";
			
			int PPIDcolumn = merch_obj.getPPIDColumn(merchData, ppid);
			String PPUSection = merch_obj.IsPrePurchase(merchData, ppid);
			// Read the entire column data
			HashMap<String, String> kit_offerdata = merch_obj.getColumnData(merchData, PPIDcolumn, PPUSection);
			String giftppid = "";
			if(brand.equalsIgnoreCase("CrepeErase")) {
				giftppid = "CS2A0807";
			}
			else {
				giftppid = "-";
			}
			expectedofferdata_kit = merch_obj.generateExpectedOfferDataForKit(kit_offerdata, null, PPUSection, "No", ppid, giftppid, brand, campaign, "Kit");
		
			pixel_obj.defineNewHar(proxy, brand + "SASPage");	  
			bf_obj.click_cta(driver, brand, campaign, "Ordernow");		
			pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Harfiles\\" + brand + "\\" + brand + "_saspage.har", driver, "Facebook");
		
			String sas_PageSource = driver.getPageSource();
			
			///////////////////////////////////////////////////////////////////////////
						
			// Check Do Not Sell My Info link in SASPage
			result = contentObj.check_donotsellmyinfo(driver, brand, "SAS");
			if(result.equalsIgnoreCase("FAIL")) {
				remarks = "Do Not Sell My Info is missing in SASPage";
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "SASPage : Validate Do Not Sell My Info", result, remarks);	
			remarks = "";
			
			// LDU Validation in SASPage
			result = contentObj.LDU_validation(brand, "saspage");
			if(result.equalsIgnoreCase("FAIL")) {
				remarks = "LDU String is missing in SASpage";
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "SASPage : Validate LDU String", result, remarks);	
			remarks = "";
			
			// SEO Validation in SASPage
			String sas_seo = contentObj.seoValidation(brand, "SASPage", sas_PageSource);
			if(sas_seo.equalsIgnoreCase("")) {
				result = "PASS";
			}
			else {
				result = "FAIL";
				remarks = "SASPage - " + sas_seo;
			}
			output = contentObj.generateOutputList(output, env, brand, campaign, "SASPage : SEO Validation", result, remarks);	
			remarks = "";
			///////////////////////////////////////////////////////////////////////////
			
			//Checkout Validation
			pixel_obj.defineNewHar(proxy, brand + "CheckoutPage");				
			sas_obj.select_offer(driver, expectedofferdata_kit, category);			
			// Move to Checkout
			bf_obj.move_to_checkout(driver, brand, campaign, category);
			pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Harfiles\\" + brand + "\\" + brand + "_checkoutpage.har", driver, "Facebook");
		}		
		String checkout_PageSource = driver.getPageSource();
		///////////////////////////////////////////////////////////////////////////
				
		// Checkout Page Validations
		
		// Check Do Not Sell My Info link in CheckoutPage
		result = contentObj.check_donotsellmyinfo(driver, brand, "Checkout");
		if(result.equalsIgnoreCase("FAIL")) {
			remarks = "Do Not Sell My Info is missing in CheckoutPage";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "CheckoutPage : Validate Do Not Sell My Info", result, remarks);	
		remarks = "";
		
		// LDU Validation in CheckoutPage
		result = contentObj.LDU_validation(brand, "checkoutpage");
		if(result.equalsIgnoreCase("FAIL")) {
			remarks = "LDU String is missing in Checkoutpage";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "CheckoutPage : Validate LDU String", result, remarks);	
		remarks = "";
		
		// SEO Validation in CheckoutPage
		String checkout_seo = contentObj.seoValidation(brand, "CheckoutPage", checkout_PageSource);		
		if(checkout_seo.equalsIgnoreCase("")) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "CheckoutPage - " + checkout_seo;
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "CheckoutPage : SEO Validation", result, remarks);	
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
				
		Thread.sleep(2000);
		jse.executeScript("window.scrollBy(0,750)", 0);
		Thread.sleep(2000);
	
		// Checkout Page - Terms and Conditions Popup link
		WebElement tc_popup;
		if(brand.equalsIgnoreCase("JLoBeauty")) {
			tc_popup = driver.findElement(By.xpath("//div[@id='tncOneshotOrSingles']//a"));
		}
		else {
			tc_popup = driver.findElement(By.xpath("//div[@id='tncEntryKit']//a"));
		}
		tc_popup.click();
		
		if(driver.findElements(By.xpath("//div[@id='popupRevealModal']")).size() != 0) {
			result = "PASS";
		}
		else {
			result = "FAIL";
			remarks = "Checkout Page - Terms and Conditions Popup link is not working";
		}
		output = contentObj.generateOutputList(output, env, brand, campaign, "CheckoutPage : Terms and Conditions Popup Validation", result, remarks);		
		remarks = "";
		///////////////////////////////////////////////////////////////////////////
		driver.quit();
	}
	
	@AfterSuite
	public void populateExcel() throws IOException {
		
		comm_obj.deleteDirectory(new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Merchandising Input"));
		comm_obj.deleteDirectory(new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\SEO Templates"));
		
		String file = comm_obj.populateOutputExcel(output, "ContentVerificationResults", System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Run_Output\\");
		
		List<String> attachmentList = new ArrayList<String>();
		attachmentList.add(file);		
		
		mailObj.sendEmail("Content Verification Results", sendReportTo, attachmentList);
	}
}
