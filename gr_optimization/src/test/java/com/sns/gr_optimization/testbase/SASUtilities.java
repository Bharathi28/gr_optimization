package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class SASUtilities {
	
	CommonUtilities comm_obj = new CommonUtilities();
	BuyflowUtilities bf_obj = new BuyflowUtilities();
	
	public void select_offer(WebDriver driver, HashMap<String, String> offerdata, String category) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,0)", 0);
		Thread.sleep(2000);
		
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
	    	case "frequency":
	    		select_frequency(driver, brand, campaign, offerdata);
	    		break;	 
	    	case "size":
	    		select_size(driver, brand, campaign, offerdata);
	    		break;
	    	case "paymenttype":
	    		select_paymenttype(driver, brand, campaign, offerdata);
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
				if(driver.findElements(By.id("dwfrm_cart_billing_billingAddress_email_emailAddress")).size() != 0) {
				
				}
				else {
					Thread.sleep(3000);
					driver.findElement(By.xpath("//button[@id='add-to-cart']")).click();
				}
					
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

		Thread.sleep(3000);	
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
		WebElement gift_elmt = comm_obj.find_webelement(driver, giftloc.get(0).get("ELEMENTLOCATOR").toString(), giftloc.get(0).get("ELEMENTVALUE").toString());
		comm_obj.waitUntilElementAppears(driver, giftloc.get(0).get("ELEMENTVALUE").toString());
		gift_elmt.click();
		Thread.sleep(1000);
	}
	
	public void moveto_prepu(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws InterruptedException, ClassNotFoundException, SQLException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,500)", 0);
				
		String category = offerdata.get("Category");
		
		String preputype = "";
		if((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) {
			preputype = "MoveToShopPrePU";
		}
		else {
			preputype = "MoveToPrePU";
		}
		
		List<Map<String, Object>> locator = null;
		locator = bf_obj.get_element_locator(brand, campaign, preputype, null);		
		if(locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, preputype, null);
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
		moveto_prepu(driver, brand, campaign, offerdata);
		Thread.sleep(2000);
		
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		if((!((brand.equalsIgnoreCase("MallyBeauty")) && (campaign.equalsIgnoreCase("glow2a")))) && (!(brand.equalsIgnoreCase("JLoBeauty")))) {
			jse.executeScript("window.scrollBy(0,250)", 0);
			Thread.sleep(2000);
		}		
		
		String prepu = offerdata.get("Offer Pre-Purchase");
		String category = offerdata.get("Category");		
		
		String preputype = "";
		if((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) {
			preputype = "ShopPrePU";
		}
		else {
			preputype = "PrePU";
		}
		
		List<Map<String, Object>> locator = null;
		
		locator = bf_obj.get_element_locator(brand, campaign, preputype, prepu);		
		if(locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, preputype, prepu);
		}
		
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
		
		if(brand.equalsIgnoreCase("JLoBeauty")) {
			String paymenttype = offerdata.get("PaymentType");
			if(paymenttype.equalsIgnoreCase("TwoPay")) {
				elementvalue = "(" + elementvalue + ")[2]";
			}
		}
		
		WebElement prepu_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		comm_obj.waitUntilElementAppears(driver, elementvalue);
		Thread.sleep(4000);
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
//		if (!(brand.equalsIgnoreCase("MallyBeauty"))) {
//			jse.executeScript("window.scrollBy(0,400)", 0);
//		}
		
		if (brand.equalsIgnoreCase("WestmoreBeauty")) {
			jse.executeScript("window.scrollBy(0,500)", 0);
		}
		
		Thread.sleep(2000);
		String kitname = offerdata.get("Kit Name");
		String kitshade = offerdata.get("KitShade");
		String category = offerdata.get("Category");
		
		if((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) {
			driver.findElement(By.xpath("//img[@alt='" + kitshade + "']")).click();
		}
		else {
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
	}
	
	public void select_giftshade(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,800)", 0);
		Thread.sleep(4000);
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
				Thread.sleep(4000);
				shade_elmt.click();
				Thread.sleep(1000);
			}			
		}
	}
	
	public void select_product(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);
		
		String category = offerdata.get("Category");
		if(category.equalsIgnoreCase("BCP")) {
			driver.get("https://storefront:eComweb123@westmorebeauty.grdev.com/body-coverage-perfector-3.5oz-WYPG045.html");
		}
		else {
			String masterPPID = "";
			if((offerdata.get("Master PPID") != null) && (!(offerdata.get("Master PPID").equalsIgnoreCase("")))){
				masterPPID = offerdata.get("Master PPID");
			}
			 
			String ppid = offerdata.get("30 Day PPID");
			String name = offerdata.get("Product Name").trim();

			if(name.toLowerCase().contains("star power duo")) {
				driver.findElement(By.xpath("//div[@data-itemid='JL2A0196']//div[4]//div[5]//a")).click();
				Thread.sleep(1000);
			}
			else {
				String xpath = "";
				if(brand.equalsIgnoreCase("JLoBeauty")) {
//					xpath = "(//h3[contains(@class,'product-name')]//a[contains(text(),'" + name + "')])[1]";
					
					if(masterPPID.equalsIgnoreCase("")) {
						xpath = "//div[@data-itemid='" + ppid + "']//div[4]//div[5]//a";
					}
					else {
						xpath = "//div[@data-itemid='" + masterPPID + "']//div[4]//div[5]//a";
					}
				}
				else if(brand.equalsIgnoreCase("CrepeErase")){
					
					if(masterPPID.equalsIgnoreCase("")) {
						xpath = "//div[@data-itemid='" + ppid + "']//div//h4//a";
					}
					else {
						xpath = "//div[@data-itemid='" + masterPPID + "']//div//h4//a";
					}
				}	
				else if(brand.equalsIgnoreCase("MeaningfulBeauty")){
					if(category.equalsIgnoreCase("ShopKit")) {
						xpath = "//div[@data-itemid='" + ppid + "']//div//div//h3//a";
					}
					else {
						if(masterPPID.equalsIgnoreCase("")) {
							xpath = "//div[@data-itemid='" + ppid + "']//div//div//h3//a";
						}
						else {
							xpath = "//div[@data-itemid='" + masterPPID + "']//div//div//h3//a";
						}
					}								
				}
				else if((brand.equalsIgnoreCase("WestmoreBeauty")) || (brand.equalsIgnoreCase("MallyBeauty")) || (brand.equalsIgnoreCase("Smileactives"))){
					
					if(masterPPID.equalsIgnoreCase("")) {
						xpath = "//div[@data-itemid='" + ppid + "']//div//div[4]//h3//a";
					}
					else {					
						xpath = "//div[@data-itemid='" + masterPPID + "']//div//div[4]//h3//a";
					}
				}
				else {				
					xpath = "//h3[contains(@class,'product-name')]//a[contains(text(),'" + name + "')]";
				}
//				System.out.println(xpath);
				while(driver.findElements(By.xpath(xpath)).size() == 0){
					jse.executeScript("window.scrollBy(0,400)", 0);
					
					if(driver.findElements(By.xpath(xpath)).size() != 0) {
						break;
					}
				}
							
				
//				if(brand.equalsIgnoreCase("MeaningfulBeauty")) {
//					WebElement hover_elmt = driver.findElement(By.xpath("//div[@data-itemid='" + ppid + "']//div//div//h3//a"));
//					Actions act = new Actions(driver);
//					act.moveToElement(hover_elmt);
//					Thread.sleep(2000);
//				}
				WebElement product_elmt = driver.findElement(By.xpath(xpath));
				comm_obj.waitUntilElementAppears(driver, xpath);
				Thread.sleep(4000);
				product_elmt.click();
			}
		}		
		
		if((brand.equalsIgnoreCase("MallyBeauty")) && (offerdata.get("Product Name").contains("Kit"))){
			Thread.sleep(2000);
			driver.findElement(By.xpath("(//button[@class='button primary next-section'])[1]")).click();
			
//			if(offerdata.get("Product Name").contains("Kit")){
				if(offerdata.get("PagePattern").equalsIgnoreCase("product")) {
					Thread.sleep(4000);
					driver.findElement(By.xpath("//a[@id='no-upgrade']")).click();
				}
//			}
			
			
		}
		Thread.sleep(1000);
	}
	
	public void select_shade(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,300)", 0);
		Thread.sleep(2000);
		String ppid = offerdata.get("30 Day PPID");
		
		if(brand.equalsIgnoreCase("CrepeErase")) {
			Select sel_element = new Select(driver.findElement(By.xpath("//ul[@class='variations-section clearfix']//li//div[3]//select")));
			sel_element.selectByVisibleText(offerdata.get("Shade").toString());
		}
		else {
			String xpath = "";
			if(brand.equalsIgnoreCase("MallyBeauty")) {
				if(offerdata.get("Product Name").contains("Kit")){
					xpath = "//div[@data-valuepackid='" + ppid + "']//div//div[2]//a//img";
					if(offerdata.get("Shade").contains("Fair")) {
						xpath = "";
					}
				}
				else {
					xpath = "//li[@data-variantid='" + ppid + "']";
				}		
			}
			else if((brand.equalsIgnoreCase("Smileactives")) || (brand.equalsIgnoreCase("WestmoreBeauty"))){
				xpath = "//li[@data-variantid='" + ppid + "']";
			}
			else {
				xpath = "(//li[@data-variantid='" + ppid + "'])[3]";
			}
			if(!(xpath.equalsIgnoreCase(""))) {
				WebElement shade_elmt = driver.findElement(By.xpath(xpath));
				comm_obj.waitUntilElementAppears(driver, xpath);
				Thread.sleep(3000);
				if(!(shade_elmt.getAttribute("class").contains("selected"))) {
					shade_elmt.click();
				}		
				Thread.sleep(1000);
			}				
		}		
		
		if((brand.equalsIgnoreCase("MallyBeauty")) && (offerdata.get("Product Name").contains("Kit"))){
			Thread.sleep(2000);	
			driver.findElement(By.xpath("//a[@id='upgrade']")).click();
		}
		
//		if(brand.equalsIgnoreCase("JLoBeauty")) {
//			Thread.sleep(4000);
//			driver.findElement(By.xpath("//button[@id='add-cart-modal']")).click();
//		}
	}
	
	public void select_onetime(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);
	
//		String ppid = offerdata.get("Product PPID");
		String ppid = offerdata.get("30 Day PPID");		
		
		Thread.sleep(6000);
//		if(!(driver.findElement(By.xpath("//input[@name='dwopt_" + ppid + "_entryKit']/..")).getAttribute("class").contains("hide"))){
//			driver.findElement(By.xpath("//input[@name='dwopt_" + ppid + "_entryKit']/../../..//label[contains(@for,'entryKit-one-pay')]")).click();
//			Thread.sleep(1000);
//		}		
		
		if(!(driver.findElement(By.xpath("//input[contains(@id,'entryKit-one-pay')]/..")).getAttribute("class").contains("hide"))){
			driver.findElement(By.xpath("//input[contains(@id,'entryKit-one-pay')]")).click();
		}		
	}
	
	public void select_subscribe(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);
	
		String ppid = offerdata.get("30 Day PPID");
		String name = offerdata.get("Product Name").trim();
		
		if((brand.equalsIgnoreCase("JLoBeauty")) && (name.equalsIgnoreCase("Star Power Duo"))) {
			
		}
		else {
			Thread.sleep(4000);
//			driver.findElement(By.xpath("//input[@name='dwopt_" + ppid + "_entryKit']/../../..//label[contains(@for,'entryKit-auto-renew')]")).click();
			driver.findElement(By.xpath("//input[contains(@id,'entryKit-auto-renew')]")).click();
			
			
			
			Thread.sleep(4000);
		}		
	}
	
	public void select_frequency(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
	
		String shipfreq = offerdata.get("Shipping Frequency");
		String category = offerdata.get("Category");
		
		Thread.sleep(6000);
		Select sel_element = new Select(driver.findElement(By.xpath("//select[@id='shippingFrequencySelector']")));
		
		if((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) {
			if(shipfreq.equalsIgnoreCase("Onetime")) {
				select_onetime(driver, brand, campaign, offerdata);
			}
			else {
				select_subscribe(driver, brand, campaign, offerdata);
				Thread.sleep(10000);
				sel_element = new Select(driver.findElement(By.xpath("//select[@id='shippingFrequencySelector']")));
				sel_element.selectByVisibleText(shipfreq);
			}
			Thread.sleep(6000);
			driver.findElement(By.xpath("//button[@id='add-to-cart']")).click();
		}
		else {
			if(brand.equalsIgnoreCase("JLoBeauty")) {
				sel_element.selectByVisibleText(shipfreq.toLowerCase() + "s");
//				if(shipfreq.contains("30")) {
//					sel_element.selectByIndex(0);
//				}
//				else if(shipfreq.contains("60")) {
//					sel_element.selectByIndex(1);
//				}
//				else if(shipfreq.contains("90")) {
//					sel_element.selectByIndex(2);
//				}
			}
			else if((brand.equalsIgnoreCase("WestmoreBeauty")) || ((brand.equalsIgnoreCase("Smileactives")) && (campaign.equalsIgnoreCase("specialoffer2")))) {
				sel_element.selectByVisibleText(shipfreq);
			}	
		}				
			
		Thread.sleep(2000);
	}
	
	public void select_size(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		
		String ppid = offerdata.get("30 Day PPID");
		
		Thread.sleep(1000);
		if(brand.equalsIgnoreCase("MeaningfulBeauty")) {
			driver.findElement(By.xpath("//li[@data-variantid='" + ppid +"']")).click();
		}
		else {
			driver.findElement(By.xpath("(//li[@data-variantid='" + ppid +"'])[3]")).click();
		}		
		Thread.sleep(1000);
	}
	
	public void select_paymenttype(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata) throws ClassNotFoundException, SQLException, InterruptedException {
		String paymenttype = offerdata.get("PaymentType");
		
		List<Map<String, Object>> locator = null;
		
		locator = bf_obj.get_element_locator(brand, campaign, paymenttype, null);		
		if(locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, paymenttype, null);
		}
		
		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
			
		WebElement pay_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		comm_obj.waitUntilElementAppears(driver, elementvalue);
		pay_elmt.click();
		Thread.sleep(4000);		
	}
}
