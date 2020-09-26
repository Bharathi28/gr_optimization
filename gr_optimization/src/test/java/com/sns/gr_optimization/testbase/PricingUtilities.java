package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.sns.gr_optimization.testbase.CommonUtilities;
import com.sns.gr_optimization.testbase.DBLibrary;
import com.sns.gr_optimization.testbase.DBUtilities;

public class PricingUtilities {

	BuyflowUtilities bf_obj = new BuyflowUtilities();
	CommonUtilities comm_obj = new CommonUtilities();	
	DBUtilities db_obj = new DBUtilities();	

	public List<Map<String, Object>> get_pricing_locator(String realm, String brand, String campaign, String step, String offer) throws ClassNotFoundException, SQLException {
		String query = "select * from pricing_locators where ";
		String include_realm = "realm='" + realm + "'";
		String include_brand = "brand='" + brand + "'";
		String include_campaign = "campaign='" + campaign + "'";
		String include_step = "step='" + step + "'";
		String include_offer = "offer='" + offer + "'";
			
		if(realm != null) {
			query = query + include_realm;
			if((brand != null) || (campaign != null) || (step != null) || (offer != null)) {
				query = query + " and ";
			}
		}
		
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
		if(brand != null) {
			query = query + include_brand;
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
		List<Map<String, Object>> locator = DBLibrary.dbAction("fetch", query);
		return locator;		
	}
	
	public String fetchSASPrice(WebDriver driver, String brand, String campaign, String kitname) throws ClassNotFoundException, SQLException, InterruptedException {
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='SASKitPrice' and offer='" + kitname + "'";
		List<Map<String, Object>> priceloc = DBLibrary.dbAction("fetch", query);
		
		WebElement priceelmt = comm_obj.find_webelement(driver, priceloc.get(0).get("ELEMENTLOCATOR").toString(), priceloc.get(0).get("ELEMENTVALUE").toString());
		Thread.sleep(1000);
		String sasprice = priceelmt.getText();
		return sasprice;
	}
	
	public String getCheckoutEntryKitPrice(WebDriver driver) throws ClassNotFoundException, SQLException, InterruptedException {
		String entrykitprice = driver.findElement(By.xpath("(//div[contains(@class,'product-card')]//div//div[2]//ul//li[3]//span[2])[1]")).getText();	
		entrykitprice = entrykitprice.replace("$", "");
		return entrykitprice;
	}
	
	public String fetch_pricing (WebDriver driver, String env, String brand, String campaign, String pricing) throws ClassNotFoundException, SQLException {
		String realm = DBUtilities.get_realm(brand);	
		List<Map<String, Object>> locator = null;
		
		locator = get_pricing_locator(realm, brand, campaign, pricing, null);
		
		if(locator.size() == 0) {
			if(pricing.contains("Checkout")){
				locator = get_pricing_locator(realm, null, null, pricing, null);		
			}
			else if((pricing.contains("Confirmation")) || (pricing.contains("Paypal"))){
				if(realm.equalsIgnoreCase("R4")) {
					locator = get_pricing_locator(realm, null, null, pricing, null);
				}
				else if(realm.equalsIgnoreCase("R2")) {
					locator = get_pricing_locator(realm, brand, null, pricing, null);
				}
			}
		}
				
		String text = "";
		for(Map<String,Object> loc : locator) {
			String elementvalue = loc.get("ELEMENTVALUE").toString();
											
			System.out.println("pricing: " + elementvalue);
			if(driver.findElements(By.xpath(elementvalue)).size() != 0) {
				text = driver.findElement(By.xpath(elementvalue)).getText();
 				break;
			}
		}
		if(text.contains("$")) {
			text = text.replace("$", "");
		}
		return text;
	}
}
