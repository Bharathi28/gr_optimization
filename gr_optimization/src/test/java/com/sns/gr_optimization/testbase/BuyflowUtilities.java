package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.CharMatcher;
import com.sns.gr_optimization.testbase.CommonUtilities;
import com.sns.gr_optimization.testbase.DBLibrary;
import com.sns.gr_optimization.testbase.DBUtilities;

public class BuyflowUtilities {
	
	CommonUtilities comm_obj = new CommonUtilities();
	DBUtilities db_obj = new DBUtilities();
	CartLanguageUtilities lang_obj = new CartLanguageUtilities();
	
	public List<Map<String, Object>> get_element_locator(String brand, String campaign, String step, String offer) throws ClassNotFoundException, SQLException {
		
		String query = "select * from locators where ";
		String include_brand = "brand='" + brand + "'";
		String include_campaign = "campaign='" + campaign + "'";
		String include_step = "step='" + step + "'";
		String include_offer = "offer='" + offer + "'";
			
		if(brand != null) {
			query = query + include_brand;
			if((campaign != null) || (step != null) || (offer != null)) {
				query = query + " and ";
			}
		}
		if(campaign != null) {
			query = query + include_campaign;
			if((step != null) || (offer != null)) {
				query = query + " and ";
			}
		}
		if(step != null) {
			query = query + include_step;
			if(offer != null) {
				query = query + " and ";
			}
		}
		if(offer != null) {
			query = query + include_offer;
		}
//		query = query + ";";
//			System.out.println(query);
		List<Map<String, Object>> locator = DBLibrary.dbAction("fetch",query);
		return locator;		
	}
	
	public void click_cta(WebDriver driver, String brand, String campaign, String step) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
				
		String query = "select * from cta_locators where brand='" + brand + "' and campaign='" + campaign + "' and step='" + step + "'";
		List<Map<String, Object>> locator = DBLibrary.dbAction("fetch",query);
		String elementlocator = locator.get(0).get("LOCATOR").toString();
		String elementvalue = locator.get(0).get("VALUE").toString();
		
		if(!(elementvalue.equalsIgnoreCase("n/a"))) {
			Thread.sleep(5000);
			WebElement element = comm_obj.find_webelement(driver, elementlocator, elementvalue);	
			Thread.sleep(2000);
			//element.click();
			jse.executeScript("arguments[0].click();", element); 
		}
	}
	
	public void click_logo(WebDriver driver, String brand, String campaign) throws ClassNotFoundException, SQLException {
		
		List<Map<String, Object>> locator = null;
		
		locator = get_element_locator(brand, campaign, "Logo", null);		
		if(locator.size() == 0) {
			locator = get_element_locator(brand, null, "Logo", null);
		}
		for(Map<String,Object> logo : locator) {
			String elementvalue = logo.get("ELEMENTVALUE").toString();
			if(driver.findElements(By.xpath(elementvalue)).size() != 0) {
				driver.findElement(By.xpath(elementvalue)).click();
				break;
			}
		}
	}		

	public void move_to_checkout(WebDriver driver, String brand, String campaign, String category) throws InterruptedException, ClassNotFoundException, SQLException {
//		System.out.println("Moving to Checkout Page...");	
		JavascriptExecutor jse = (JavascriptExecutor) driver;
				
		Thread.sleep(2000);
		// Check if the page is already in checkout
		if(driver.findElements(By.id("dwfrm_personinf_contact_email")).size() != 0) {
			
		}
		else if(driver.findElements(By.id("dwfrm_cart_billing_billingAddress_email_emailAddress")).size() != 0) {
			
		}
		else {						
			if((category.equalsIgnoreCase("SubscribeandSave")) || (category.equalsIgnoreCase("ShopKit"))) {
				category = "Product";
			}
			
			List<Map<String, Object>> locator = null;
			
			locator = get_element_locator(brand, campaign, "ProceedToCheckout", category);		
			if(locator.size() == 0) {
				locator = get_element_locator(brand, null, "ProceedToCheckout", category);
			}
			
			String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
			String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();			

			if(!(elementvalue.equalsIgnoreCase("n/a"))) {
				WebElement element = comm_obj.find_webelement(driver, elementlocator, elementvalue);	
				Thread.sleep(2000);
				element.click();
				Thread.sleep(2000);
			}						
		}	
	}
	
	public void upsell_confirmation(WebDriver driver, String brand, String campaign, String upsell, String PostPUPage) throws InterruptedException, ClassNotFoundException, SQLException {
		Thread.sleep(4000);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		List<Map<String, Object>> locator = null;
		
		locator = get_element_locator(brand, campaign, PostPUPage, upsell);
		
		if(locator.size() == 0) {
			locator = get_element_locator(brand, null, PostPUPage, upsell);
		}		
			
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
		jse.executeScript("window.scrollBy(0,450)", 0);
		Thread.sleep(5000);
		
//		comm_obj.find_webelement(driver, elementlocator, elementvalue).click();
		jse.executeScript("arguments[0].click();", comm_obj.find_webelement(driver, elementlocator, elementvalue));	
	}
	
	// To get all Gift PPIDs of a campaign
	public List<String> getPPIDfromString(String brand, String gifts) throws ClassNotFoundException, SQLException {
		String productlinecode = db_obj.get_sourceproductlinecode(brand);
		
		String[] gift_str_arr = gifts.split("\\s+");
		List<String> gift_list = new ArrayList<String>();
		for(String str : gift_str_arr) {
			if(str.contains(productlinecode)) {
				if(str.contains(",")) {
					String[] arr = str.split(",");
					for(String a : arr) {
						a = a.replaceAll("[^a-zA-Z0-9$]+", "");
						gift_list.add(a);
					}
				}
				else {
					str = str.replaceAll("[^a-zA-Z0-9$]+", "");
					gift_list.add(str);
				}				
			}
		}	
		return gift_list;
	}	
	
	// To check if all the gifts are present in the campaign
	public String checkGifts(WebDriver driver, String brand, String campaign, String campaigngifts) throws InterruptedException, ClassNotFoundException, SQLException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		if((!(brand.equalsIgnoreCase("MallyBeauty")))  && (!(brand.equalsIgnoreCase("DrDenese")))){
//			jse.executeScript("window.scrollBy(0,1000)", 0);
		}
		Thread.sleep(2000);
		
		List<String> gift_list = getPPIDfromString(brand, campaigngifts);
		String giftResult = "";
		for(String gift : gift_list) {
			String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='Gift' and offer like '%" + gift + "%'";
			List<Map<String, Object>> giftloc = DBLibrary.dbAction("fetch", query);
			Thread.sleep(4000);
						
			if(!(giftloc.get(0).get("ELEMENTVALUE").toString().equalsIgnoreCase("n/a"))) {
				if(driver.findElements(By.xpath(giftloc.get(0).get("ELEMENTVALUE").toString())).size() != 0) {
					
				}
				else {
					giftResult = giftResult + giftloc.get(0).get("OFFER").toString() + " is not present on SAS";
				}
			}			
		}
		jse.executeScript("window.scrollBy(0,0)", 0);
		Thread.sleep(2000);
		return giftResult;
	}
	
	public List<List<String>> getLineItems(WebDriver driver, String cc, String offerpostpu, String brand) {
		
		List<List<String>> actual_lineitems = new ArrayList<List<String>>();
		
		List<WebElement> lineitems = driver.findElements(By.xpath("//div[@id='cartsummary']//div[contains(@class,'product-card')]"));
		for(int i=1; i<=lineitems.size(); i++) {
			List<String> item = new ArrayList<String>();
			
			String ppid = "";
			String price = null;
			if(brand.equalsIgnoreCase("JloBeauty")) {
				ppid = driver.findElement(By.xpath("(//div[@class='item-quantity'])[" + i + "]")).getAttribute("data-id");
			}
			else {
				ppid = driver.findElement(By.xpath("(//span[@class='PPID disclaimer-ppid'])[" + i + "]")).getText();
			}
						
			if(offerpostpu.equalsIgnoreCase("Yes")) {
				if(cc.equalsIgnoreCase("Paypal")) {
					price = driver.findElement(By.xpath("(//div[contains(@class,'product-card')]//ul//li[contains(@class,'item-total')]//span[last()])[" + i + "]")).getText();
				}
				else {
					if(driver.findElements(By.xpath("(//div[contains(@class,'product-card')]//ul//li[contains(@class,'first-payment-price')]//span[last()])[" + i + "]")).size() != 0) {
						price = driver.findElement(By.xpath("(//div[contains(@class,'product-card')]//ul//li[contains(@class,'first-payment-price')]//span[last()])[" + i + "]")).getText();
					}
					else {
						price = driver.findElement(By.xpath("(//div[contains(@class,'product-card')]//ul//li[contains(@class,'item-total')]//span[last()])[" + i + "]")).getText();
					}
				}
			}
			else {
				if(brand.equalsIgnoreCase("JloBeauty")) {
					if(driver.findElements(By.xpath("(//ul[@class='product-list-item']//li[contains(@class,'first-payment-price')]//span[last()])[" + i + "]")).size() != 0) {
						price = driver.findElement(By.xpath("(//ul[@class='product-list-item']//li[contains(@class,'first-payment-price')]//span[last()])[" + i + "]")).getText();
					}
					else {
						price = driver.findElement(By.xpath("(//ul[@class='product-list-item']//li[contains(@class,'item-total')]//span[last()])[" + i + "]")).getText();
					}					
				}
				else {
					price = driver.findElement(By.xpath("(//div[contains(@class,'product-card')]//ul//li[contains(@class,'item-total')]//span[last()])[" + i + "]")).getText();
				}
			}
			
			item.add(ppid);
			item.add(price);
			
			if(driver.findElements(By.xpath("(//div[contains(@class,'kitproduct')]//div[contains(@class,'continuity-summary')])[" + i + "]")).size() != 0) {
				String cart_language = driver.findElement(By.xpath("(//div[contains(@class,'kitproduct')]//div[contains(@class,'continuity-summary')])[" + i + "]")).getText();
				if((cart_language.contains("month")) || (cart_language.contains("day"))) {
					String[] lang_price_arr = lang_obj.parse_cart_language(cart_language);			
					String cart_lang_price = lang_price_arr[1];
					String cart_lang_shipping = lang_price_arr[2];	
					
					Double cont_price = Double.valueOf(cart_lang_price);	
					double cont_roundOff = Math.floor(cont_price * 100.0) / 100.0;		
					cart_lang_price = String.valueOf(cont_roundOff);	
					
					Double cont_shipping = Double.valueOf(cart_lang_shipping);	
					double cont_ship_roundOff = Math.floor(cont_shipping * 100.0) / 100.0;		
					cart_lang_shipping = String.valueOf(cont_ship_roundOff);
									
					item.add(cart_language);
					item.add(cart_lang_price);
					item.add(cart_lang_shipping);
				}
				else {
					item.add("No Cart Language");
					item.add("No Continuity Pricing");
					item.add("No Continuity Shipping");
				}				
			}			
			else {
				item.add("No Cart Language");
				item.add("No Continuity Pricing");
				item.add("No Continuity Shipping");
			}			
			actual_lineitems.add(item);
		}		
		return actual_lineitems;
	}
	
	public String checkAddedLineItem(WebDriver driver, String brand, String campaign, String offer, String name) throws ClassNotFoundException, SQLException, InterruptedException {
		String result = "FAIL";
	
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='CheckoutLineItem' and offer='" + offer + "'";
		List<Map<String, Object>> lineitemloc = DBLibrary.dbAction("fetch", query);
		
		WebElement lineitem_elmt = comm_obj.find_webelement(driver, lineitemloc.get(0).get("ELEMENTLOCATOR").toString(), lineitemloc.get(0).get("ELEMENTVALUE").toString());
		Thread.sleep(2000);
		
		String lineitemname = lineitem_elmt.getText();
		if(lineitemname.contains(name)) {
			result = "PASS";
		}
		else {
			result = lineitemname;
		}
		return result;
	}
//	
//	public String fetch_offercode(WebDriver driver, String brand) throws ClassNotFoundException, SQLException {
//		String offercode = "";
//		String realm = DBUtilities.get_realm(brand);
//		
//		List<WebElement> offercode_elmt;
//		if(realm.equalsIgnoreCase("R2")) {
//			offercode_elmt = driver.findElements(By.className("offerCodeID"));
//		}
//		else {
//			offercode_elmt = driver.findElements(By.xpath("//span[@class='hide PPID']"));
//		}
//		
//		int count = offercode_elmt.size();
//		for(WebElement elmt : offercode_elmt) {
//			offercode = offercode + elmt.getText();
//			if(count > 1) {
//				offercode = offercode + ",";
//			}
//		}
//		return offercode;
//	}
	
	public String fetch_confoffercode(WebDriver driver, String brand) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String realm = DBUtilities.get_realm(brand);
		String offercode = "";
		if(realm.equalsIgnoreCase("R2")) {
			// Also supplies to SB
			if(brand.equalsIgnoreCase("Sub-D")){
				Thread.sleep(3000);
				offercode = (String) jse.executeScript("return app.omniMap.MainOfferCode");
			}
			else {
				List<WebElement> offercode_elmt = null;
				if(driver.findElements(By.className("offerCode")).size() != 0) {
					offercode_elmt = driver.findElements(By.className("offerCode"));
				}
				else if(driver.findElements(By.xpath("//div[@class='ordersummary show-desktop']//div[contains(@id, 'orderSummary')]")).size() != 0){
					offercode_elmt = driver.findElements(By.xpath("//div[@class='ordersummary show-desktop']//div[contains(@id, 'orderSummary')]"));
				}
				int count = offercode_elmt.size();
				for(WebElement elmt : offercode_elmt) {
					if(elmt.getText().contains("-")) {
						String[] arr = elmt.getText().split("-");
						arr[0] = arr[0].replace(" ", "");
						offercode = offercode + arr[0];
					}
					else {
						offercode = offercode + elmt.getText();
					}
					if(count > 1) {
						offercode = offercode + ",";
					}
					count--;
				}
			}
		}
		else {
			Thread.sleep(5000);
			String products = (String) jse.executeScript("return app.variableMap.products");
			String[] arr = products.split(";");
			int arrSize = arr.length;
			for(int i=1; i<arrSize; i=i+3) {
				offercode = offercode + arr[i] + ",";
			}
			offercode = offercode.substring(0, offercode.length() - 1);
		}		
		return offercode;
	}
	
	public String fetch_conf_num(WebDriver driver, String brand) throws ClassNotFoundException, SQLException {
		String realm = DBUtilities.get_realm(brand);
		WebElement conf_num_elmt;
		if(realm.equalsIgnoreCase("R2")) {
			conf_num_elmt = driver.findElement(By.id("orderConfirmNum"));
		}
		else {
			conf_num_elmt = driver.findElement(By.xpath("//div[@class='orderReceived']//span"));
		}
		String conf_num = conf_num_elmt.getText();
		return conf_num;
	}
	
	public void complete_order(WebDriver driver, String brand, String cc) throws ClassNotFoundException, SQLException, InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver,50);
		String realm = DBUtilities.get_realm(brand);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		WebElement comp_order_element;
		if(realm.equalsIgnoreCase("R2")) {
			if(cc.toLowerCase().contains("paypal")) {
				comp_order_element = driver.findElement(By.xpath("//button[@class='cta-submit btn-primary']"));
			}
			else {
				comp_order_element = driver.findElement(By.id("contYourOrder"));
			}
		}
		else {
			if(cc.toLowerCase().contains("paypal")) {
				comp_order_element = driver.findElement(By.id("submitButton"));
			}
			else {
//				System.out.println(driver.findElement(By.xpath("//button[@id='trigerPlaceOrder']")).isDisplayed());
				comp_order_element = driver.findElement(By.xpath("//button[@id='trigerPlaceOrder']"));
			}			
		}
		wait.until(ExpectedConditions.visibilityOf(comp_order_element));
		wait.until(ExpectedConditions.elementToBeClickable(comp_order_element));
		jse.executeScript("arguments[0].click();", comp_order_element);	
//		Thread.sleep(2000);
//		comp_order_element.click();
	}
	
	public void clear_form_field(WebDriver driver, String realm, String field) throws ClassNotFoundException, SQLException {
		String query = "select * from form_locators where realm='" + realm + "' and form='Checkout' and field='" + field + "'";
		List<Map<String, Object>> locator = DBLibrary.dbAction("fetch", query);
		
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
		
		WebElement element = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		element.clear();
	}
	
	public void fill_form_field(WebDriver driver, String realm, String field, String value) throws ClassNotFoundException, SQLException {
		
		String query = "select * from form_locators where realm='" + realm + "' and form='Checkout' and field='" + field + "'";
		List<Map<String, Object>> locator = DBLibrary.dbAction("fetch", query);
		
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
		
		if((field.equalsIgnoreCase("State")) || (field.equalsIgnoreCase("Month")) || (field.equalsIgnoreCase("Year"))){
			Select sel_element = new Select(comm_obj.find_webelement(driver, elementlocator, elementvalue));
			sel_element.selectByValue(value);
		}
		if((field.equalsIgnoreCase("Agree")) || (field.equalsIgnoreCase("UseAsBilling")) || (field.equalsIgnoreCase("UseAsShipping"))) {
			WebElement element = comm_obj.find_webelement(driver, elementlocator, elementvalue);
			element.click();
		}
		else {
			WebElement element = comm_obj.find_webelement(driver, elementlocator, elementvalue);
			element.sendKeys(value);
		}
	}
	
	public String fill_out_form(WebDriver driver, String brand, String campaign, String cc, String shipbill, String supply, String address) throws InterruptedException, ClassNotFoundException, SQLException {
		WebDriverWait wait = new WebDriverWait(driver,50);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String realm = DBUtilities.get_realm(brand);
		String email = "";
		Thread.sleep(2000);
		if(cc.equalsIgnoreCase("paypal")) {
			if(realm.equalsIgnoreCase("R4")) {
				if(brand.equalsIgnoreCase("JLoBeauty")) {
					driver.findElement(By.xpath("(//div[@id='paypalSection']//div//div)[3]")).click();
				}
				else {
					driver.findElement(By.xpath("//div[@id='paypalSection']//div//div")).click();
				}					
			}
			else {
				driver.findElement(By.xpath("//button[@class='PayPalExpressButton']")).click();
			}
			
			String winHandleBefore = driver.getWindowHandle();
			for(String winHandle : driver.getWindowHandles()){
			   driver.switchTo().window(winHandle);
			   driver.manage().window().maximize();
			   Thread.sleep(2000);
			}						
			
			if(driver.findElements(By.xpath("//div[@id='loginSection']//div//div[2]//a")).size() != 0) {
				Thread.sleep(4000);
				driver.findElement(By.xpath("//div[@id='loginSection']//div//div[2]//a")).click();
				Thread.sleep(2000);
				email = paypalPayment(driver, wait, jse, winHandleBefore, realm);
			}

			
//			Thread.sleep(10000);
//			driver.switchTo().frame(0);
	
			if(driver.findElements(By.xpath("//div[@id='login_emaildiv']//div//input")).size() != 0) {
				Thread.sleep(4000);
				email = paypalPayment(driver, wait, jse, winHandleBefore, realm);
			}
			else {
				Thread.sleep(10000);
				while(driver.findElements(By.xpath("//div[@id='loginSection']//div//div[2]//a")).size() == 0) {
					
					if(driver.findElements(By.xpath("//section[@id='genericError']//div//div[2]")).size() != 0) {
						driver.close();
						driver.switchTo().window(winHandleBefore);
						Thread.sleep(2000);
						email = ccPayment(driver, jse, realm, brand, campaign, "Visa", shipbill, supply, address);
					}
					else if(driver.findElements(By.xpath("//div[contains(text(),\"Things don't appear to be working at the moment\")]")).size() != 0) {
						driver.close();
						driver.switchTo().window(winHandleBefore);
						Thread.sleep(2000);
						email = ccPayment(driver, jse, realm, brand, campaign, "Visa", shipbill, supply, address);
					}
					else if(driver.findElements(By.xpath("//div[@id='loginSection']//div//div[2]//a")).size() != 0) {
						driver.findElement(By.xpath("//div[@id='loginSection']//div//div[2]//a")).click();
						Thread.sleep(2000);
						email = paypalPayment(driver, wait, jse, winHandleBefore, realm);
					}
					else if(driver.findElements(By.xpath("//button[text()='Log In']")).size() != 0) {
						driver.findElement(By.xpath("//button[text()='Log In']")).click();
						Thread.sleep(2000);
						email = paypalPayment(driver, wait, jse, winHandleBefore, realm);
					}
//					else if(driver.findElements(By.xpath("//div[@id='login_emaildiv']//div//input")).size() != 0) {
//						Thread.sleep(2000);
//						email = paypalPayment(driver, wait, jse, winHandleBefore, realm);
//					}
					if(!(email.equalsIgnoreCase(""))) {
						break;
					}
				}
				if(driver.findElements(By.xpath("//div[@id='login_emaildiv']//div//input")).size() != 0) {
					Thread.sleep(2000);
					email = paypalPayment(driver, wait, jse, winHandleBefore, realm);
				}
				if(driver.findElements(By.xpath("//div[@id='loginSection']//div//div[2]//a")).size() != 0) {
					Thread.sleep(2000);
					driver.findElement(By.xpath("//div[@id='loginSection']//div//div[2]//a")).click();
					Thread.sleep(2000);
					email = paypalPayment(driver, wait, jse, winHandleBefore, realm);
				}
				else if(driver.findElements(By.xpath("//button[text()='Log In']")).size() != 0) {
					Thread.sleep(2000);
					driver.findElement(By.xpath("//button[text()='Log In']")).click();
					Thread.sleep(2000);
					email = paypalPayment(driver, wait, jse, winHandleBefore, realm);
				}
			}							
		}
		else {
			email = ccPayment(driver, jse, realm, brand, campaign, cc, shipbill, supply, address);
		}
		return email;
	}	
	
	public String ccPayment(WebDriver driver, JavascriptExecutor jse, String realm, String brand, String campaign, String cc, String shipbill, String supply, String address) throws ClassNotFoundException, SQLException, InterruptedException {
		String alpha = org.apache.commons.lang3.RandomStringUtils.randomAlphabetic(9);
		String num = org.apache.commons.lang3.RandomStringUtils.randomNumeric(4);
		String email = alpha + "-" + num + "@yopmail.com";
		
		fill_form_field(driver, realm, "Email", email.toLowerCase());
		if((brand.equalsIgnoreCase("CrepeErase")) || (brand.equalsIgnoreCase("MeaningfulBeauty")) || (brand.equalsIgnoreCase("JLoBeauty"))){
			driver.findElement(By.xpath("(//input[contains(@class,'input-text password')])[1]")).sendKeys("Grcweb123!");
		}
		
		if(address.equalsIgnoreCase("Real")) {
			fill_form_field(driver, realm, "PhoneNumber", "3103103100");	
		}
		else if(address.equalsIgnoreCase("Test")) {
			fill_form_field(driver, realm, "PhoneNumber", "8887878787");	
		}		
						
		fill_form_field(driver, realm, "FirstName", firstName());
		fill_form_field(driver, realm, "LastName", lastName());
		
		if(address.equalsIgnoreCase("Real")) {
			fill_form_field(driver, realm, "AddressLine1", "200 N Pacific Coast Hwy");
		}
		else if(address.equalsIgnoreCase("Test")) {
			fill_form_field(driver, realm, "AddressLine1", "123 QATest st");
		}

		if(campaign.equalsIgnoreCase("ca")) {
			fill_form_field(driver, realm, "City", "Anywhere");
			fill_form_field(driver, realm, "State", "NB");		
			fill_form_field(driver, realm, "Zip", "E3B7K6");
		}
		else {
			if(address.equalsIgnoreCase("Real")) {
				fill_form_field(driver, realm, "City", "Los Angeles");
			}
			else if(address.equalsIgnoreCase("Test")) {
				fill_form_field(driver, realm, "City", "El Segundo");
			}
			
////			fill_form_field(driver, realm, "City", "El Segundo");
//			fill_form_field(driver, realm, "City", "Los Angeles");
			fill_form_field(driver, realm, "State", "CA");					
			
			if(supply.equalsIgnoreCase("30")) {		
				fill_form_field(driver, realm, "Zip", "90245");	
			}
			else if(supply.equalsIgnoreCase("90")) {
				fill_form_field(driver, realm, "Zip", "81002");
			}
		}			
						
		Thread.sleep(2000);
		WebElement shipbill_elmt = null;
		if(driver.findElements(By.xpath("//input[@id='dwfrm_personinf_useAsBillingAddress']")).size() != 0) {
			shipbill_elmt = driver.findElement(By.xpath("//input[@id='dwfrm_personinf_useAsBillingAddress']"));
		}
		else if(driver.findElements(By.xpath("//input[@id='dwfrm_cart_billing_billingAddress_useAsShippingAddress']")).size() != 0) {
			shipbill_elmt = driver.findElement(By.xpath("//input[@id='dwfrm_cart_billing_billingAddress_useAsShippingAddress']"));
		}
		jse.executeScript("window.scrollBy(0,200)", 0);
		if(shipbill.equalsIgnoreCase("same")) {
			if(!(shipbill_elmt.isSelected())) {
				shipbill_elmt.click();
			}
		}
		else {
			if(shipbill_elmt.isSelected()) {
				shipbill_elmt.click();
			}
			
			fill_form_field(driver, realm, "ShippingFirstName", firstName());
			fill_form_field(driver, realm, "ShippingLastName", lastName());
			fill_form_field(driver, realm, "ShippingAddressLine1", "123 Anywhere st");
			fill_form_field(driver, realm, "ShippingCity", "Huntsville");
			fill_form_field(driver, realm, "ShippingState", "AL");
			fill_form_field(driver, realm, "ShippingZip", "35801");
		}		
		
		if((supply.equalsIgnoreCase("90")) && (brand.equalsIgnoreCase("Volaire"))){	
			fill_form_field(driver, realm, "CardNumber", "4111111111111111");
		}
		else {
			fill_form_field(driver, realm, "CardNumber", getCCNumber(cc));
		}		
		fill_form_field(driver, realm, "Month", "12");
		fill_form_field(driver, realm, "Year", "2024");	
		

//		if(realm.equalsIgnoreCase("R4")) {
//			fill_form_field(driver, realm, "CVV", "349");
//		}
		jse.executeScript("window.scrollBy(0,200)", 0);
		Thread.sleep(2000);
		fill_form_field(driver, realm, "Agree", "");
		Thread.sleep(2000);
		return (email.toLowerCase());
	}
	
	public String paypalPayment(WebDriver driver, WebDriverWait wait, JavascriptExecutor jse, String winHandleBefore, String realm) throws ClassNotFoundException, SQLException, InterruptedException {
		// Email Field
		Thread.sleep(5000);
		driver.findElement(By.xpath("//div[@id='login_emaildiv']//div//input")).sendKeys("testbuyer2@guthy-renker.com");
				
		if(driver.findElements(By.xpath("//button[@class='button actionContinue scTrack:unifiedlogin-login-click-next']")).size() != 0) {
			driver.findElement(By.xpath("//button[@class='button actionContinue scTrack:unifiedlogin-login-click-next']")).click();
		}
					
		// Password Field
		Thread.sleep(4000);
		driver.findElement(By.xpath("//div[@id='login_passworddiv']//div//input")).sendKeys("123456789");
						
		// Login button
		jse.executeScript("window.scrollBy(0,200)", 0);
		Thread.sleep(2000);
		driver.findElement(By.xpath("//button[@class='button actionContinue scTrack:unifiedlogin-login-submit']")).click();	
				
		Thread.sleep(5000);
		// Agree and Continue
		jse.executeScript("window.scrollBy(0,700)", 0);
		Thread.sleep(6000);
//		driver.findElement(By.xpath("//button[@class='btn full confirmButton continueButton']")).click();
//		Thread.sleep(2000);
//		driver.findElement(By.xpath("//input[@id='confirmButtonTop']")).click();
		driver.findElement(By.xpath("//button[@id='payment-submit-btn']")).click();
				
		wait.until(ExpectedConditions.numberOfWindowsToBe(1));
		driver.switchTo().window(winHandleBefore);
		Thread.sleep(2000);
		fill_form_field(driver, realm, "Agree", "");
		return "testbuyer2@guthy-renker.com";
	}
	
	public String getCCNumber(String cc) {
		String ccnumber = "";
		if(cc.equalsIgnoreCase("visa")) {
			ccnumber="4111111111111111";
		}
		else if(cc.equalsIgnoreCase("amex")) {
			ccnumber="378282246310005";
		}
		else if(cc.equalsIgnoreCase("mastercard")) {
			ccnumber="5555555555554444";
		}
		else if(cc.equalsIgnoreCase("discover")) {
			ccnumber="6011111111111117";
		}
		return ccnumber;
	}
	
	public String firstName() {
		String[] arr= {"Dina","Jessy","Kevin","Adam","Yuthan","Yuvan","Henry","Heera","Ishaan","Rachel","Phoebe","Joey"};
		int rnd = new Random().nextInt(arr.length);
	    return arr[rnd];
	}
	
	public String lastName() {
		String[] arr= {"William","Wilson","Abraham","Bush","Jones","Darlow","Shapiro","Weaver","Geller"};
		int rnd = new Random().nextInt(arr.length);
	    return arr[rnd];
	}
	
	public String getSalesTax(WebDriver driver, String subtotal, String shipping) throws ClassNotFoundException, SQLException {
		String state = "";
		if(driver.findElements(By.xpath("(//div[@class='order-city'])[2]")).size() != 0) {
			String[] citytext = driver.findElement(By.xpath("(//div[@class='order-city'])[2]")).getText().split(" ");
			for(String str : citytext) {
				if((str.length() == 2) && (CharMatcher.javaUpperCase().matchesAllOf(str))) {
					state = str;
					break;
				}
			}
		}
		else {
			String query = "select * from form_locators where realm='R4' and form='Checkout' and field='State'";
			List<Map<String, Object>> locator = DBLibrary.dbAction("fetch", query);
			
			String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
			String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
			
			Select select = new Select(comm_obj.find_webelement(driver, elementlocator, elementvalue));
			WebElement selectedoption = select.getFirstSelectedOption();
			state = selectedoption.getText();
		}
		
		// Remove $
		if(subtotal.contains("$")) {
			subtotal = subtotal.replace("$", "");
		}
		if(shipping.contains("$")) {
			shipping = shipping.replace("$", "");
		}	
		
		if(shipping.equalsIgnoreCase("FREE")) {
			shipping = "0.00";
		}
		
		Double subtotal_value = Double.valueOf(subtotal);
		Double shipping_value = Double.valueOf(shipping);
				
		String salestaxpercent = db_obj.getSalesTaxPercentage(state);
		salestaxpercent = salestaxpercent.replace("%", "");
		Double percent_value = Double.valueOf(salestaxpercent);
				
		Double salestax_value = ((subtotal_value + shipping_value)*percent_value)/100;
		double salextax_roundOff = Math.floor(salestax_value * 100.0) / 100.0;
				
		String salestax = String.valueOf(salextax_roundOff);		
		return salestax;
	}
	
	public String CalculateTotalPrice(List<String> price) {
		Double total_value = (double) 0;
		for(String value : price) {
			if((value.equalsIgnoreCase("FREE")) || (value.equalsIgnoreCase("0.0")) || (value.equalsIgnoreCase("0"))) {
				value="0";
			}
			
			if(value.contains("$")) {
				value = value.replace("$", "");
			}
			
			Double temp_value = Double.valueOf(value);
			
			total_value = total_value + temp_value;
		}
		double total_roundOff = Math.floor(total_value * 100.0) / 100.0;		
		String total = String.valueOf(total_roundOff);		
		return total;
	}
	
	public String assertPrice(String actual, String expected) {
		actual = actual.replace("$", "");
		expected = expected.replace("$", "");
		
		if(actual.equalsIgnoreCase("FREE")) {
			actual = "0.0";
		}
		if(expected.equalsIgnoreCase("FREE")) {
			expected = "0.0";
		}
		
		Double expected_price = Double.valueOf(expected);
		Double actual_price = Double.valueOf(actual);	
		Double diff = Math.abs(expected_price-actual_price);				
		double roundOff = Math.floor(diff * 100.0) / 100.0;
		int diff_value = (int)roundOff;
		
		String Result = "";
		if(diff_value == 0) {
			Result = "PASS";
		}				
		else {
			Result = "FAIL";
		}
		return Result;
	}
}
