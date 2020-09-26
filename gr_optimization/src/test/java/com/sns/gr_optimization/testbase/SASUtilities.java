package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SASUtilities {
	
	CommonUtilities comm_obj = new CommonUtilities();
	
	public void select_offer(WebDriver driver, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		
		String brand =  offerdata.get("Brand");
		String campaign =  offerdata.get("Campaign");
		
		String[] patternarr = offerdata.get("PagePattern").split("-");
		for(String pattern : patternarr) {
			switch(pattern){  
	    	case "kit":
	    		select_kit(driver, brand, campaign, offerdata);
	    		break;  
	    	case "gift":
	    		select_gift(driver, brand, campaign, offerdata);
	    		break;  	    	
	    	case "prepu":
	    		select_prepu(driver, brand, campaign, offerdata);
	    		break;
	    	case "fragrance":
	    		select_fragrance(driver, brand, campaign, offerdata);
	    		break;  
			}
		}		
	}
	
	public void select_kit(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		String kitname = offerdata.get("Kit Name");
		
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='Kit' and offer='" + kitname + "'";
		List<Map<String, Object>> kitloc = DBLibrary.dbAction("fetch", query);
		
		WebElement kit_elmt = comm_obj.find_webelement(driver, kitloc.get(0).get("ELEMENTLOCATOR").toString(), kitloc.get(0).get("ELEMENTVALUE").toString());
		Thread.sleep(2000);
		kit_elmt.click();
		Thread.sleep(1000);
	}
	
	public void select_gift(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		String giftppid = offerdata.get("Gift PPID");
		
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='Gift' and offer like '%" + giftppid + "%'";
		List<Map<String, Object>> giftloc = DBLibrary.dbAction("fetch", query);
		
		if((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("Core"))){
			jse.executeScript("window.scrollBy(0,700)", 0);
		}
		else if((brand.equalsIgnoreCase("MeaningfulBeauty")) && (campaign.equalsIgnoreCase("deluxe20off"))){
			jse.executeScript("window.scrollBy(0,700)", 0);
		}
		
		WebElement gift_elmt = comm_obj.find_webelement(driver, giftloc.get(0).get("ELEMENTLOCATOR").toString(), giftloc.get(0).get("ELEMENTVALUE").toString());
		Thread.sleep(2000);
		gift_elmt.click();
		Thread.sleep(1000);
	}
	
	public void moveto_prepu(WebDriver driver, String brand, String campaign) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,200)", 0);
		
		if((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("Core"))) {
			driver.findElement(By.xpath("//div[@class = 'sas-sticky-footer']//a[contains(text(),'Proceed to Checkout')]")).click();
		}
		else if((brand.equalsIgnoreCase("MeaningfulBeauty")) && (campaign.equalsIgnoreCase("Core"))) {
			driver.findElement(By.xpath("//button[@class='button checkout-special-offer']")).click();
		}
	}

	public void select_prepu(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		moveto_prepu(driver, brand, campaign);
		Thread.sleep(1000);
		
		String prepu = offerdata.get("Offer Pre-Purchase");
		
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='PrePU' and offer='" + prepu + "'";
		List<Map<String, Object>> prepuloc = DBLibrary.dbAction("fetch", query);
		
		WebElement prepu_elmt = comm_obj.find_webelement(driver, prepuloc.get(0).get("ELEMENTLOCATOR").toString(), prepuloc.get(0).get("ELEMENTVALUE").toString());
		Thread.sleep(2000);
		prepu_elmt.click();
		Thread.sleep(1000);
	}
	
	public void select_fragrance(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		
		String fragrance = offerdata.get("Fragrance");
		
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='Fragrance' and offer='" + fragrance + "'";
		List<Map<String, Object>> fragloc = DBLibrary.dbAction("fetch", query);
		
		WebElement frag_elmt = comm_obj.find_webelement(driver, fragloc.get(0).get("ELEMENTLOCATOR").toString(), fragloc.get(0).get("ELEMENTVALUE").toString());
		Thread.sleep(2000);
		frag_elmt.click();
		Thread.sleep(1000);
	}

}
