package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.sns.gr_optimization.testbase.DBLibrary;
import com.sns.gr_optimization.testbase.DBUtilities;

public class CartLanguageUtilities {
	
	CommonUtilities comm_obj = new CommonUtilities();
	
	public String getfullcartlanguage(WebDriver driver) throws InterruptedException {
		comm_obj.waitUntilElementAppears(driver, "//div[contains(@class,'continuity-summary')]");
		Thread.sleep(4000);
		String language = driver.findElement(By.xpath("//div[contains(@class,'continuity-summary')]")).getText();
		return language;
	}
	
	public String getsupplementalcartlanguage(WebDriver driver) throws InterruptedException {
		comm_obj.waitUntilElementAppears(driver, "//div[@class='supplementcartlanguage']");
		Thread.sleep(1000);
		String language = driver.findElement(By.xpath("//div[@class='supplementcartlanguage']")).getText();
		return language;
	}	
	
	public String[] parse_cart_language(String language) {
//		System.out.println("1 " + language);
		language = language.replaceAll("[^0-9.$]+", "");
//		System.out.println("2 " + language);
//		if(language.contains("$")) {
			language = language.substring(language.indexOf("$"));
//		}
		
//		System.out.println("3 " + language);
		while(language.endsWith(".")) {
			language = language.substring(0, language.length() - 1);
		}		
		language = language.replace("$", " ");
		String[] price_arr = language.split(" ");		
		return price_arr;
	}
	
	public String ChecknoCartLanguage(WebDriver driver) throws InterruptedException {
		String expectedCartLang1 = "There is no commitment and no minimum to buy. Please note exact shipment times may vary.";
		String expectedCartLang2 = "Please note exact shipment times may vary.";
		
		if(driver.findElements(By.xpath("//div[@class='no-commitment-message clearfix']")).size() != 0) {
			comm_obj.waitUntilElementAppears(driver, "//div[@class='no-commitment-message clearfix']");
			Thread.sleep(4000);
			String actualCartlanguage = driver.findElement(By.xpath("//div[@class='no-commitment-message clearfix']")).getText();
			
			//Remove whitespace
			expectedCartLang1 = expectedCartLang1.replaceAll("\\s+", "");
			expectedCartLang2 = expectedCartLang2.replaceAll("\\s+", "");
			actualCartlanguage = actualCartlanguage.replaceAll(" ", "");
							
			// Remove special characters
			expectedCartLang1 = expectedCartLang1.replaceAll("[^a-zA-Z0-9$]+", "");
			expectedCartLang2 = expectedCartLang2.replaceAll("[^a-zA-Z0-9$]+", "");
			actualCartlanguage = actualCartlanguage.replaceAll("[^a-zA-Z0-9$]+", "");
			
			String Result = "";
			if((actualCartlanguage.contains(expectedCartLang1)) || (actualCartlanguage.contains(expectedCartLang2))){
				Result = "PASS";
			}
			else {
				Result = "FAIL";
			}			
			return Result;
		}
		return "FAIL";
	}
	
	public String ChecknoSupplCartLanguage(WebDriver driver) throws InterruptedException {
		String expectedSupplLang = "By checking this box, you are electronically signing your order, agreeing to the terms above and to our general ";
		
		if(driver.findElements(By.xpath("//div[@id='tncOneshotOrSingles']")).size() != 0) {
			comm_obj.waitUntilElementAppears(driver, "//div[@id='tncOneshotOrSingles']");
			Thread.sleep(4000);
			String actualSuppllanguage = driver.findElement(By.xpath("//div[@id='tncOneshotOrSingles']")).getText();
			
			//Remove whitespace
			expectedSupplLang = expectedSupplLang.replaceAll("\\s+", "");
			actualSuppllanguage = actualSuppllanguage.replaceAll(" ", "");
					
			// Remove special characters
			expectedSupplLang = expectedSupplLang.replaceAll("[^a-zA-Z0-9$]+", "");
			actualSuppllanguage = actualSuppllanguage.replaceAll("[^a-zA-Z0-9$]+", "");
			
			String Result = "";
			if(actualSuppllanguage.contains(expectedSupplLang)) {
				Result = "PASS";
			}
			else {
				Result = "FAIL";
			}
			
			return Result;
		}
		return "FAIL";		
	}
	
//	public String[] parse_installments_language(String language) {
//		
//		language = language.replace("1st Payment:", "");
//		language = language.replace("2nd Payment:", "");
//		language = language.replace("3rd Payment:", "");
//		language = language.replaceAll("[^0-9.$]+", "");
//		System.out.println(language);
//		language = language.substring(language.indexOf("$"));
//		
//		language = language.replace("$", " ");
//		language = language.substring(1);
//		String[] price_arr = language.split(" ");
//		return price_arr;
//	}
//	
//	public String get_installments_text(WebDriver driver) {
//		String ins_text = driver.findElement(By.xpath("//div[@class='disclouserText installment-summary small-12 float-left clearfix']")).getText();
//		return ins_text;
//	}
//	
//	public String get_ppid(WebDriver driver, String brand, String campaign, Map<String, Object> offer) throws ClassNotFoundException, SQLException {
//		String realm = DBUtilities.get_realm(brand);
//		String ppid = null;
//		if((brand.equalsIgnoreCase("WestmoreBeauty")) && (campaign.equalsIgnoreCase("eyeoffer"))){
//			ppid = offer.get("ppid").toString();
//		}
//		else if(realm.equalsIgnoreCase("R4")){
//			ppid = driver.findElement(By.xpath("(//span[@class='PPID disclaimer-ppid'])[1]")).getText();	
//		}	
//		else {
//			ppid = driver.findElement(By.xpath("//div[@class='offerCodeID']")).getText();
//		}
//		return ppid;
//	}
}
