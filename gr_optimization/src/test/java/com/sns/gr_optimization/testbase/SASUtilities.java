package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class SASUtilities {
	
	CommonUtilities comm_obj = new CommonUtilities();
	BuyflowUtilities bf_obj = new BuyflowUtilities();
	
	public void select_offer(WebDriver driver, HashMap<String, String> offerdata, String category) throws ClassNotFoundException, SQLException, InterruptedException {
//		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,0)", 0);
		
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
	    	case "kitshade":
	    		select_kitshade(driver, brand, campaign, offerdata);
	    		break; 
	    	case "giftshade":
	    		select_giftshade(driver, brand, campaign, offerdata);
	    		break; 
	    	case "product":
	    		select_product(driver, brand, campaign, offerdata);
	    		break;
	    	case "shade":
	    		select_shade(driver, brand, campaign, offerdata);
	    		break;
	    	case "onetime":
	    		select_onetime(driver, brand, campaign, offerdata);
	    		break;
	    	case "subscribe":
	    		select_subscribe(driver, brand, campaign, offerdata);
	    		break;
			}
		}	
		add_product_to_cart(driver, brand, campaign, category);
	}
	
	public void add_product_to_cart(WebDriver driver, String brand, String campaign, String category) throws InterruptedException {
		Thread.sleep(2000);
		System.out.println("Adding product to cart");
		if((category.equalsIgnoreCase("Product")) || (category.equalsIgnoreCase("ShopKit")) || (category.equalsIgnoreCase("SubscribeandSave"))) {
//			if(brand.equalsIgnoreCase("JLoBeauty")) {
//				if(driver.findElements(By.xpath("//a[@class='button mini-cart-link-checkout small-12']")).size() == 0) {
					Thread.sleep(3000);
					driver.findElement(By.xpath("//button[@id='add-to-cart']")).click();
//				}
//			}
		}
	}
	
	public void select_kit(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String kitname = offerdata.get("Kit Name");
		
		List<Map<String, Object>> locator = null;
		
		locator = bf_obj.get_element_locator(brand, campaign, "Kit", kitname);		
		if(locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, "Kit", kitname);
		}
		
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();

		WebElement kit_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);

		comm_obj.waitUntilElementAppears(driver, elementvalue);

		Thread.sleep(1000);	
		kit_elmt.click();
		Thread.sleep(1000);
	}
	
	public void moveto_gift(WebDriver driver, String brand, String campaign) throws InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		if(brand.equalsIgnoreCase("CrepeErase")){
			if(campaign.equalsIgnoreCase("Core")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			}
			else if(campaign.equalsIgnoreCase("cscb1")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			}
		}
		else if(brand.equalsIgnoreCase("MeaningfulBeauty")){
			if(campaign.equalsIgnoreCase("deluxe20off")) {
				jse.executeScript("window.scrollBy(0,800)", 0);
			}
			else if(campaign.equalsIgnoreCase("Core")) {
				jse.executeScript("window.scrollBy(0,800)", 0);
			}
			else if(campaign.equalsIgnoreCase("friend50poff")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			}
			else if(campaign.equalsIgnoreCase("trymbnow")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			}
			else if(campaign.equalsIgnoreCase("special-offer-panelc")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			}
		}
		Thread.sleep(4000);
	}
	
	public void select_gift(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		moveto_gift(driver, brand, campaign);
		
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		String giftppid = offerdata.get("Gift PPID");
		
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='Gift' and offer like '%" + giftppid + "%'";
		List<Map<String, Object>> giftloc = DBLibrary.dbAction("fetch", query);		
//		System.out.println(query);
		WebElement gift_elmt = comm_obj.find_webelement(driver, giftloc.get(0).get("ELEMENTLOCATOR").toString(), giftloc.get(0).get("ELEMENTVALUE").toString());
		comm_obj.waitUntilElementAppears(driver, giftloc.get(0).get("ELEMENTVALUE").toString());
		gift_elmt.click();
		Thread.sleep(1000);
	}
	
	public void moveto_prepu(WebDriver driver, String brand, String campaign) throws InterruptedException, ClassNotFoundException, SQLException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,200)", 0);
		
		List<Map<String, Object>> locator = null;
		
		locator = bf_obj.get_element_locator(brand, campaign, "MoveToPrePU", null);		
		if(locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, "MoveToPrePU", null);
		}
		
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
			
		if(!(elementvalue.equalsIgnoreCase("n/a"))) {
			WebElement prepu_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
			comm_obj.waitUntilElementAppears(driver, elementvalue);
			prepu_elmt.click();
			Thread.sleep(4000);
		}		
	}

	public void select_prepu(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		moveto_prepu(driver, brand, campaign);
		Thread.sleep(1000);
		
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,200)", 0);
		
		String prepu = offerdata.get("Offer Pre-Purchase");
		
		List<Map<String, Object>> locator = null;
		
		locator = bf_obj.get_element_locator(brand, campaign, "PrePU", prepu);		
		if(locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, "PrePU", prepu);
		}
		
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
			
		WebElement prepu_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		comm_obj.waitUntilElementAppears(driver, elementvalue);
		prepu_elmt.click();
		Thread.sleep(1000);
	}
	
	public void select_fragrance(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String fragrance = offerdata.get("Fragrance");
		
		List<Map<String, Object>> locator = null;
		
		locator = bf_obj.get_element_locator(brand, campaign, "Fragrance", fragrance);		
		if(locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, "Fragrance", fragrance);
		}
		
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
		
		if((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fshadvanced"))) {
			jse.executeScript("window.scrollBy(0,-300)", 0);
			Thread.sleep(2000);
		}		
		
		WebElement frag_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		comm_obj.waitUntilElementAppears(driver, elementvalue);
		frag_elmt.click();
		Thread.sleep(1000);
	}
	
	public void select_kitshade(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,100)", 0);
		
		String kitname = offerdata.get("Kit Name");
		String kitshade = offerdata.get("KitShade");
		
		if(!(kitshade.equalsIgnoreCase("No"))) {
			List<Map<String, Object>> locator = null;
			
			locator = bf_obj.get_element_locator(brand, campaign, "KitShade", kitshade + " " + kitname);		
			if(locator.size() == 0) {
				locator = bf_obj.get_element_locator(brand, null, "KitShade", kitshade + " " + kitname);
			}
			
			String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
			String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
			
			if(!(elementvalue.equalsIgnoreCase("n/a"))) {
				WebElement shade_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
				comm_obj.waitUntilElementAppears(driver, elementvalue);
				Thread.sleep(2000);
				shade_elmt.click();
				Thread.sleep(1000);
			}			
		}
	}
	
	public void select_giftshade(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);
		
		String giftshade = offerdata.get("GiftShade");
		
		if(!(giftshade.equalsIgnoreCase("No"))) {
			List<Map<String, Object>> locator = null;
			
			locator = bf_obj.get_element_locator(brand, campaign, "GiftShade", giftshade);		
			if(locator.size() == 0) {
				locator = bf_obj.get_element_locator(brand, null, "GiftShade", giftshade);
			}
			
			String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
			String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
			
			if(!(elementvalue.equalsIgnoreCase("n/a"))) {
				WebElement shade_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
				comm_obj.waitUntilElementAppears(driver, elementvalue);
				Thread.sleep(3000);
				shade_elmt.click();
				Thread.sleep(1000);
			}			
		}
	}
	
	public void select_product(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);
	
		String ppid = offerdata.get("Product PPID");
		String pagepattern = offerdata.get("PagePattern");
		String masterppid = ppid;
		if(ppid.equalsIgnoreCase("JL2A0136")) {
			driver.findElement(By.xpath("(//a[@class='button-text sd-cta'])[2]")).click();
			Thread.sleep(1000);
		}
		else {
			if(offerdata.containsKey("Master PPID")) {
				masterppid = offerdata.get("Master PPID");
			}
			
			String master_xpath = "";
			String ppid_xpath = "";
			if(brand.equalsIgnoreCase("JLoBeauty")) {
				master_xpath = "//div[@data-itemid='" + masterppid + "']//div[4]//h3//a";
				ppid_xpath = "//div[@data-itemid='" + ppid + "']//div[4]//h3//a";
			}
			else {
				master_xpath = "//div[@data-itemid='" + masterppid + "']//div//div//h4//a";
				ppid_xpath = "//div[@data-itemid='" + ppid + "']//div//div//h4//a";
			}			
			
			while(driver.findElements(By.xpath(master_xpath)).size() == 0){
				jse.executeScript("window.scrollBy(0,400)", 0);
				
				if(driver.findElements(By.xpath(ppid_xpath)).size() != 0) {
					masterppid = ppid;
					break;
				}
			}
			
			if(brand.equalsIgnoreCase("JLoBeauty")) {
				master_xpath = "//div[@data-itemid='" + masterppid + "']//div[4]//h3//a";
			}
			else {
				master_xpath = "//div[@data-itemid='" + masterppid + "']//div//div//h4//a";
			}
			
			WebElement product_elmt = driver.findElement(By.xpath(master_xpath));
			comm_obj.waitUntilElementAppears(driver, master_xpath);
			Thread.sleep(2000);
			product_elmt.click();
		}		
		
		if(brand.equalsIgnoreCase("JLoBeauty")) {
			if(!(pagepattern.contains("shade"))) {
				Thread.sleep(4000);
				driver.findElement(By.xpath("//button[@id='add-cart-modal']")).click();
			}
		}
		Thread.sleep(1000);
	}
	
	public void select_shade(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,300)", 0);
	
		String ppid = offerdata.get("Product PPID");
		
		if(brand.equalsIgnoreCase("CrepeErase")) {
			Select sel_element = new Select(driver.findElement(By.xpath("//ul[@class='variations-section clearfix']//li//div[3]//select")));
			sel_element.selectByVisibleText(offerdata.get("Shade").toString());
		}
		else {
			WebElement shade_elmt = driver.findElement(By.xpath("(//li[@data-variantid='" + ppid + "'])[3]"));
			comm_obj.waitUntilElementAppears(driver, "(//li[@data-variantid='" + ppid + "'])[3]");
			Thread.sleep(2000);
			if(!(shade_elmt.getAttribute("class").contains("selected"))) {
				shade_elmt.click();
			}		
			Thread.sleep(1000);	
		}
		
		
		
		if(brand.equalsIgnoreCase("JLoBeauty")) {
			Thread.sleep(4000);
			driver.findElement(By.xpath("//button[@id='add-cart-modal']")).click();
		}
	}
	
	public void select_onetime(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);
	
		String ppid = offerdata.get("Product PPID");
		
		Thread.sleep(1000);
		if(!(driver.findElement(By.xpath("//input[@name='dwopt_" + ppid + "_entryKit']/..")).getAttribute("class").contains("hide"))){
			driver.findElement(By.xpath("//input[@name='dwopt_" + ppid + "_entryKit']/../../..//label[contains(@for,'entryKit-one-pay')]")).click();
			Thread.sleep(1000);
		}		
	}
	
	public void select_subscribe(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);
	
		String ppid = offerdata.get("Product PPID");
		
//		if(brand.equalsIgnoreCase("JLoBeauty")) {
//			if((ppid.equalsIgnoreCase("JL1A0036")) || (ppid.equalsIgnoreCase("JL1A0037")) || (ppid.equalsIgnoreCase("JL1A0034"))) {
//				ppid = "JL1A0035";
//			}
//		}		
		//input[@name='dwopt_JL1A0034_entryKit']/../../..//label[contains(@for,'entryKit-auto-renew')]
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='dwopt_" + ppid + "_entryKit']/../../..//label[contains(@for,'entryKit-auto-renew')]")).click();
		Thread.sleep(1000);
	}
}
