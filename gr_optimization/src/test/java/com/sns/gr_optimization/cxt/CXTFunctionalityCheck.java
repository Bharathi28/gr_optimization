package com.sns.gr_optimization.cxt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sns.gr_optimization.setup.BaseTest;
import com.sns.gr_optimization.testbase.CXTUtilities;
import com.sns.gr_optimization.testbase.CommonUtilities;
import com.sns.gr_optimization.testbase.DBUtilities;
import com.sns.gr_optimization.testbase.MailUtilities;

public class CXTFunctionalityCheck {

	CommonUtilities comm_obj = new CommonUtilities();
	BaseTest base_obj = new BaseTest();
	DBUtilities db_obj = new DBUtilities();
	CXTUtilities cxt_obj = new CXTUtilities();
	MailUtilities mailObj = new MailUtilities();
	Scanner in = new Scanner(System.in);	
	
	List<List<String>> output = new ArrayList<List<String>>();
	String sendReportTo = "aaqil@searchnscore.com,manibharathi@searchnscore.com";
	
	@BeforeSuite
	public void getEmailId() {
		
//		System.out.println("Enter Email id : ");
//		sendReportTo = in.next();
//		sendReportTo = System.getProperty("email");
	}
	
	@DataProvider(name="cxtInput", parallel=true)
	public Object[][] testData() {
		Object[][] arrayObject = null;
		arrayObject = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/CXTValidation/cxt_runinput.xlsx", "rundata",1);
		return arrayObject;
	}
	
	@Test(dataProvider="cxtInput")
	public void cxtvalidation(String env, String brand, String campaign, String postponedays, String browser) throws IOException, ClassNotFoundException, SQLException, InterruptedException, ParseException {		
									
		BaseTest base_obj = new BaseTest();			
		WebDriver driver = base_obj.setUp(browser, "Local");
		
		String url = db_obj.getPageUrl(brand, campaign, "SignIn", env);
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);		
		
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		String realm = db_obj.get_realm(brand);
		String actual = "";
		String expected = "";
		String remarks = "";
				
		//Step 1
		int loginSuccess = 0;
		List<String> output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Login");
		cxt_obj.LoginintoCXT(driver, brand, campaign, env);		
		if (realm.equals("R4")) {
			actual = cxt_obj.getPageTitle(driver);
			expected = "Shop";
		}
		else if(realm.equalsIgnoreCase("R2")) {
			if(driver.findElements(By.xpath("//h2[@class='kit-section-header']")).size() != 0) {
				actual = driver.findElement(By.xpath("//h2[@class='kit-section-header']")).getText();
			}
			else {
				if(driver.findElements(By.xpath("//div[@class='form-error errorCode errorContainer']")).size() != 0){
					remarks = "Login Failed - " + driver.findElement(By.xpath("//div[@class='form-error errorCode errorContainer']")).getText();
				}
			}			
			expected = "My Next Kit";
		}
		
		if(actual.equals(expected)) {
			loginSuccess = 1;
			System.out.println(brand + "- Step 1 - Login Successful");		
			output_row.add("PASS");
			cxt_obj.takeScreenshot(driver, brand, "login", "Success", "visiblepart");
		}
		else {
			System.out.println(brand + "- Step 1 - Login Unsuccessful");			
			output_row.add("FAIL");
			output_row.add(remarks);
			cxt_obj.takeScreenshot(driver, brand, "login", "Failure", "visiblepart");
		}
		output.add(output_row);
		Thread.sleep(1000);
		
		if(loginSuccess == 1) {
			//Step 2
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Navigate away - To Google");
			driver.get("https://www.google.com/");
			actual = cxt_obj.getPageTitle(driver);
			expected = "Google";
			if(actual.equals(expected)) {
				System.out.println(brand + "- Step 2 - Navigating away is Successful");	
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "googlenavigation", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 2 - Navigating away is Unsuccessful");		
				output_row.add("FAIL");
				output_row.add("Navigating away is Unsuccessful");
				cxt_obj.takeScreenshot(driver, brand, "googlenavigation", "Failure", "visiblepart");
			}
			output.add(output_row);
					
			//Step 3
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Soft Login");	
			url = db_obj.getUrl(brand, campaign, env);
			driver.get(url);
			String message = "";
			if (realm.equals("R4")) {
				actual = cxt_obj.getPageTitle(driver);
				expected = "Shop";
				message = "Softlogin";
			}
			else if(realm.equalsIgnoreCase("R2")) {
				actual = driver.findElement(By.xpath("//h2[@class='kit-section-header']")).getText();
				expected = "My Next Kit";
				message = "Softlogin/Navigation to My Next Kit";
			}
			if(actual.equals(expected)) {
				System.out.println(brand + "- Step 3 - "+ message +" Successful");	
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "softlogin", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 3 - "+ message +" Unsuccessful");	
				output_row.add("FAIL");
				output_row.add(message + " Unsuccessful");
				cxt_obj.takeScreenshot(driver, brand, "softlogin", "Failure", "visiblepart");
			}
			output.add(output_row);
					
			if(realm.equals("R4")) {
				//Step 4
				output_row = new ArrayList<String>();
				output_row.add(env);
				output_row.add(brand);
				output_row.add(campaign);
				output_row.add("Navigation to My Next kit Page");
				cxt_obj.moveToMyNextKit(driver, brand, campaign);
				actual = driver.findElement(By.xpath("//h1[@class='text-center']")).getText();
				expected = "My Next Kit";
				if((brand.equalsIgnoreCase("SeaCalmSkin")) || (brand.equalsIgnoreCase("WestmoreBeauty"))){
					expected = expected.toUpperCase();
				}
				
				if(actual.equals(expected)) {
					System.out.println(brand + "- Step 4 - Navigation To MyNextKit is Successful");		
					output_row.add("PASS");
					cxt_obj.takeScreenshot(driver, brand, "mynextkit", "Success", "fullpage");
				}
				else {
					System.out.println(brand + "- Step 4 - Navigation To MyNextKit is Unsuccessful");		
					output_row.add("FAIL");
					output_row.add("Navigation To MyNextKit is Unsuccessful");
					cxt_obj.takeScreenshot(driver, brand, "mynextkit", "Failure", "fullpage");
				}
				output.add(output_row);
			}		
			jse.executeScript("window.scrollTo(0, 0)", 0);
			Thread.sleep(2000);
			
			//Step 5 - Reschedule Shipment
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Reschedule Shipment");
			if(postponedays.equalsIgnoreCase("0.0")) {		
				System.out.println(brand + "- Step 5 - Reschedule Shipment - Could not verify");		
				output_row.add("FAIL");
				output_row.add("Could not validate Reschedule Shipment");
			}		
			else {				
				String format = "";
				if(realm.equals("R4")) {
					format = "MMM dd, yyyy";
				}
				else {
					format = "E MMM dd yyyy";
				}
				
				Calendar now = Calendar.getInstance();		
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				
				double ddays = Double.parseDouble(postponedays);
				int days = (int) ddays;
				now.add(Calendar.DAY_OF_MONTH, days); 
				String expecteddate = sdf.format(now.getTime()); 	
				
				String actualdate = cxt_obj.rescheduleShipment(driver, brand, expecteddate, now);		
				if(actualdate.contains("FAIL")) {
					System.out.println(brand + "- Step 5 - Reschedule Shipment Unsuccessful");		
					output_row.add("FAIL");
					output_row.add(actualdate);
					cxt_obj.takeScreenshot(driver, brand, "postponeshipment", "Failure", "visiblepart");
				}
				else {
					if (realm.equals("R4")) {
						actual = driver.findElement(By.xpath("//div[@class='success clearfix']")).getText();
					}		
					else {
						actual = driver.findElement(By.xpath("//div[@class='message box-sucess']")).getText();
					}
					expected = "Success! Your next shipment has been rescheduled.";			
							System.out.println(brand + " --"+actualdate+"--");
							System.out.println(brand + " --"+expecteddate+"--");
					if((actual.equals(expected)) && (actualdate.equalsIgnoreCase(expecteddate))){
						System.out.println(brand + "- Step 5 - Reschedule Shipment Successful");	
						output_row.add("PASS");
						cxt_obj.takeScreenshot(driver, brand, "postponeshipment", "Success", "visiblepart");
					}
					else {
						System.out.println(brand + "- Step 5 - Reschedule Shipment Unsuccessful");		
						output_row.add("FAIL");
						output_row.add("Reschedule Shipment Unsuccessful");
						cxt_obj.takeScreenshot(driver, brand, "postponeshipment", "Failure", "visiblepart");
					}
				}		
				output.add(output_row);
			}		
					
			//Step 6
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Navigation to Order History Page");
			cxt_obj.ShiftTabsCXT(driver, brand, campaign, "OrderHistory");		
			
			if (realm.equals("R4")) {
				actual = driver.findElement(By.xpath("//h1[@class='page-title text-center']")).getText();
			}
			else if(realm.equalsIgnoreCase("R2")) {
				actual = driver.findElement(By.xpath("//h1[contains(text(),'Order History')]")).getText();
			}
			expected = "Order History";
			if((brand.equalsIgnoreCase("SeaCalmSkin")) || (brand.equalsIgnoreCase("WestmoreBeauty"))) {
				expected = expected.toUpperCase();
			}
			if(actual.equals(expected)) {
				System.out.println(brand + "- Step 6 - Navigation To OrderHistory is Successful");
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "orderhistory", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 6 - Navigation To OrderHistory is Unsuccessful");
				output_row.add("FAIL");
				output_row.add("Navigation To OrderHistory is Unsuccessful");
				cxt_obj.takeScreenshot(driver, brand, "orderhistory", "Failure", "visiblepart");
			}
			output.add(output_row);
					
			//Step 7
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Navigation to My Profile Page");
			cxt_obj.ShiftTabsCXT(driver, brand, campaign, "MyProfile");		
			
			if (realm.equals("R4")) {
				actual = driver.findElement(By.xpath("//h1[@class='text-center']")).getText();
			}
			else if(realm.equalsIgnoreCase("R2")) {
				actual = driver.findElement(By.xpath("//h1[contains(text(),'My Profile')]")).getText();
			}
			expected = "My Profile";
			if((brand.equalsIgnoreCase("SeaCalmSkin")) || (brand.equalsIgnoreCase("WestmoreBeauty"))) {
				expected = expected.toUpperCase();
			}
			if(actual.equals(expected)) {
				System.out.println(brand + "- Step 7 - Navigation To MyProfile is Successful");
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "myprofile", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 7 - Navigation To MyProfile is Unsuccessful");	
				output_row.add("FAIL");
				output_row.add("Navigation To MyProfile is Unsuccessful");
				cxt_obj.takeScreenshot(driver, brand, "myprofile", "Failure", "visiblepart");
			}
			output.add(output_row);
			
			//Step 8
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Navigation to Shop Page");
			cxt_obj.ShiftTabsCXT(driver, brand, campaign, "Shop");	
			
			if (realm.equals("R4")) {
				if((brand.equalsIgnoreCase("SeaCalmSkin")) || (brand.equalsIgnoreCase("WestmoreBeauty")) || (brand.equalsIgnoreCase("Mally"))) {
					actual = cxt_obj.getPageTitle(driver);
				}
				else {
					actual = driver.findElement(By.xpath("//h1[@class='page-title text-center']")).getText();
				}			
			}
			else if(realm.equalsIgnoreCase("R2")) {
				actual = driver.findElement(By.xpath("//h2[contains(text(),'Shop')]")).getText();
			}
			expected = "Shop";		
			if(actual.equals(expected)) {
				System.out.println(brand + "- Step 8 - Navigation To Shop is Successful");
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "shop", "Success", "fullpage");
			}
			else {
				System.out.println(brand + "- Step 8 - Navigation To Shop is Unsuccessful");
				output_row.add("FAIL");
				output_row.add("Navigation To Shop is Unsuccessful");
				cxt_obj.takeScreenshot(driver, brand, "shop", "Failure", "fullpage");
			}
			jse.executeScript("window.scrollTo(0, 0)", 0);
			Thread.sleep(2000);
			output.add(output_row);
					
			//Step 9
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Add Product to KC");	
			cxt_obj.addProductToKC(driver, brand, campaign);
			if(realm.equals("R4")) {					
				if(driver.findElements(By.xpath("//p[@class='error']")).size() != 0){
					String errormsg = driver.findElement(By.xpath("//p[@class='error']")).getText().trim();
					actual = errormsg;
				}
				else if(driver.findElements(By.xpath("//span[@class='sucess-msg']")).size() != 0) {
					String successmsg = driver.findElement(By.xpath("//span[@class='sucess-msg']")).getText().trim();
					actual = successmsg.replace(" ", "");
				}
					
				expected = "Thankyouforloving" + brand;
				if(brand.equals("Mally")) {
					expected = expected+"Beauty";
				}
				
				System.out.println(actual);
				System.out.println(expected);
			}
			else {
				if(driver.findElements(By.xpath("//div[@class='message error']")).size() != 0){
					String errormsg = driver.findElement(By.xpath("//div[@class='message error']")).getText().trim();
					actual = errormsg;
				}
				else if(driver.findElements(By.xpath("//span[@class='hide-for-small-only sucess-msg']")).size() != 0) {
					String successmsg = driver.findElement(By.xpath("//span[@class='hide-for-small-only sucess-msg']")).getText().trim();
					actual = successmsg;
				}			
				
				expected = "Success! Your kit has been updated.";
			}
			if(actual.equals(expected)) {
				System.out.println(brand + "- Step 9 - Adding Product to KC Successful");
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "addtokc", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 9 - Adding Product to KC Unsuccessful");		
				output_row.add("FAIL");
				output_row.add(actual);
				cxt_obj.takeScreenshot(driver, brand, "addtokc", "Failure", "visiblepart");
			}
			output.add(output_row);
					
			//Step 10
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Remove Product from KC");
			int number_before = cxt_obj.getNumberofProductsinKC(driver, realm, brand);
			Thread.sleep(2000);
			cxt_obj.removeProductFromKC(driver, brand);
			if(realm.equals("R4")) {			
				
				if(driver.findElements(By.xpath("//p[@class='error']")).size() != 0){
					String errormsg = driver.findElement(By.xpath("//p[@class='error']")).getText().trim();
					actual = errormsg;
				}
				else {
					int number_after = cxt_obj.getNumberofProductsinKC(driver, realm, brand);
					if(number_before == (number_after + 1)) {
						actual = "PASS";
					}
				}			
				expected = "PASS";
				
				System.out.println("Remove from KC");
				System.out.println(actual);
				System.out.println(expected);
			}
			else {
				if(driver.findElements(By.xpath("//div[@class='message error']")).size() != 0){
					String errormsg = driver.findElement(By.xpath("//div[@class='message error']")).getText().trim();
					actual = errormsg;
				}
				else if(driver.findElements(By.xpath("//span[@class='hide-for-small-only sucess-msg']")).size() != 0) {
					String successmsg = driver.findElement(By.xpath("//span[@class='hide-for-small-only sucess-msg']")).getText().trim();
					actual = successmsg;
				}	
				
				expected = "Success! Your kit has been updated.";
			}
			if(actual.equals(expected)) {
				System.out.println(brand + "- Step 10 - Removing Product from KC Successful");	
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "removefromkc", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 10 - Removing Product from KC Unsuccessful");		
				output_row.add("FAIL");
				output_row.add(actual);
				cxt_obj.takeScreenshot(driver, brand, "removefromkc", "Failure", "visiblepart");
			}
			output.add(output_row);
			
			//Step 11
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Add Product to Cart");
			cxt_obj.removeAllProductsfromCart(driver, brand, campaign);
			Thread.sleep(2000);
			String addtocartresult = cxt_obj.addProductToCart(driver, brand, campaign);
			if(addtocartresult.equals("PASS")) {
				System.out.println(brand + "- Step 11 - Adding product to Cart Successful");	
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "addtocart", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 11 - Adding product to Cart Unsuccessful");		
				output_row.add("FAIL");
				output_row.add("Adding product to Cart Unsuccessful");
				cxt_obj.takeScreenshot(driver, brand, "addtocart", "Failure", "visiblepart");
			}
			output.add(output_row);
			
			//Step 12
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Remove Product from Cart");
			String rmcartresult = cxt_obj.removeProductfromCart(driver, brand, campaign);
			if(rmcartresult.equals("PASS")) {
				System.out.println(brand + "- Step 12 - Removing product from Cart Successful");	
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "removefromcart", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 12 - Removing product from Cart Unsuccessful");		
				output_row.add("FAIL");
				output_row.add("Removing product from Cart Unsuccessful");
				cxt_obj.takeScreenshot(driver, brand, "removefromcart", "Failure", "visiblepart");
			}
			output.add(output_row);						
					
			//Step 13
			output_row = new ArrayList<String>();
			output_row.add(env);
			output_row.add(brand);
			output_row.add(campaign);
			output_row.add("Logout");
			cxt_obj.LogoutCXT(driver, brand, campaign);
			if((brand.equalsIgnoreCase("ITCosmetics")) || (brand.equalsIgnoreCase("Smileactives")) || (brand.equalsIgnoreCase("CrepeErase"))){
				actual = driver.getCurrentUrl();
				expected = "login";
			}
			else {
				actual = cxt_obj.getPageTitle(driver);
				expected = "Login";
			}
			
			if(actual.contains(expected)) {
				System.out.println(brand + "- Step 13 - Logout Successful");
				output_row.add("PASS");
				cxt_obj.takeScreenshot(driver, brand, "logout", "Success", "visiblepart");
			}
			else {
				System.out.println(brand + "- Step 13 - Logout Unsuccessful");	
				output_row.add("FAIL");
				output_row.add("Logout Unsuccessful");
				cxt_obj.takeScreenshot(driver, brand, "logout", "Failure", "visiblepart");
			}
			output.add(output_row);
		}
		driver.quit();
	}		
	
	@AfterSuite
	public void populateExcel() throws IOException {
		File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\CXTValidation", "Run_Output");
		newDirectory.mkdir();
		String file = comm_obj.populateOutputExcel(output, "CXTValidationResults", System.getProperty("user.dir") + "\\Input_Output\\CXTValidation\\Run_Output\\");
		
		List<String> attachmentList = new ArrayList<String>();
		attachmentList.add(file);
		
		Path testoutput_path = Paths.get(System.getProperty("user.dir") + "\\test-output\\emailable-report.html");
		Path target_path = Paths.get(System.getProperty("user.dir") + "\\target\\surefire-reports\\emailable-report.html");
		if (Files.exists(testoutput_path)) {
			attachmentList.add(System.getProperty("user.dir") + "\\test-output\\emailable-report.html");
		}
		else if (Files.exists(target_path)){
			attachmentList.add(System.getProperty("user.dir") + "\\target\\surefire-reports\\emailable-report.html");
		}		
		
		mailObj.sendEmail("CXT Validation Results", sendReportTo, attachmentList);
	}
}
