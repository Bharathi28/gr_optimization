package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PromotionUtilities {
	
	BuyflowUtilities bf_obj = new BuyflowUtilities();
	
	public double addProductsAsPerLimit(WebDriver driver, double upperLimit, double lowerLimit, String check) throws InterruptedException {
		double cartValue = 0;
		double subtotalValue = 0;
		
		if(check.equalsIgnoreCase("above")) {
			while(subtotalValue < upperLimit) {
				String RandomProductprice = addRandomProductToCart(driver);
				RandomProductprice = RandomProductprice.replace("$", "");
				double currentProductPrice = Double.parseDouble(RandomProductprice);	
				double price_roundOff = Math.floor(currentProductPrice * 100.0) / 100.0;
				
				cartValue = cartValue + price_roundOff;
				
				String subtotal = getProductSubTotal(driver);
				subtotal = subtotal.replace("$", "");			
				subtotalValue = Double.parseDouble(subtotal);
				
				if(subtotalValue < upperLimit) {
					driver.findElement(By.xpath("//a[@id='Shop']")).click();
					Thread.sleep(3000);
				}	
			}
		}
		else if(check.equalsIgnoreCase("between")) {
			while(subtotalValue < lowerLimit) {
				String RandomProductprice = addRandomProductToCart(driver);
				RandomProductprice = RandomProductprice.replace("$", "");
				double currentProductPrice = Double.parseDouble(RandomProductprice);	
				double price_roundOff = Math.floor(currentProductPrice * 100.0) / 100.0;
				
				cartValue = cartValue + price_roundOff;
				
				String subtotal = getProductSubTotal(driver);
				subtotal = subtotal.replace("$", "");			
				subtotalValue = Double.parseDouble(subtotal);
				
				if(subtotalValue < lowerLimit) {
					driver.findElement(By.xpath("//a[@id='Shop']")).click();
					Thread.sleep(3000);
				}
			}
			if(subtotalValue < upperLimit) {
				return cartValue;
			}
			else {
				removeAllProductsFromCart(driver);
				addProductsAsPerLimit(driver, upperLimit, lowerLimit, check);
			}
		}		
		return cartValue;
	}
	
	public String addRandomProductToCart(WebDriver driver) throws InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		List<WebElement> allProducts = driver.findElements(By.xpath("//a[@class='name-link']"));
		int allProducts_size = allProducts.size();
		
		Random rand = new Random();
		int randProduct_index = 0;
		
		while(true) {
			randProduct_index = rand.nextInt(allProducts_size+1);
		    if(randProduct_index !=0) 
		    	break;
		}
			
		while(driver.findElements(By.xpath("(//a[@class='name-link'])[" + randProduct_index + "]")).size() == 0) {			
			jse.executeScript("window.scrollBy(0,400)", 0);			
			if(driver.findElements(By.xpath("(//a[@class='name-link'])[" + randProduct_index + "]")).size() != 0) {
				break;
			}			
		}
		WebElement randomProduct = driver.findElement(By.xpath("(//a[@class='name-link'])[" + randProduct_index + "]"));				
	    randomProduct.click();
	    
	    if(driver.findElements(By.xpath("//li[contains(@class,'one-time ')]//label//span[@class='pdp-radio pixels_pdp-radio']//input")).size() != 0) {
	    	driver.findElement(By.xpath("//li[contains(@class,'one-time ')]//label//span[@class='pdp-radio pixels_pdp-radio']//input")).click();
	    }
	    
	    String randomProductPrice = "";
	    
	    if(driver.findElements(By.xpath("//strong[@class='single-price current-price ']")).size() != 0) {
	    	randomProductPrice = driver.findElement(By.xpath("//strong[@class='single-price current-price ']")).getText();
	    }
	    else {
	    	randomProductPrice = driver.findElement(By.xpath("//strong[@class='pdp-price current-price oneShotSingle-price']")).getText();
	    }	    		
	    
	    Thread.sleep(2000);
	    driver.findElement(By.xpath("//button[@id='add-to-cart']")).click();
	    Thread.sleep(2000);
	    driver.findElement(By.xpath("//a[@class='button mini-cart-link-checkout small-12']")).click();	
	    
	    return randomProductPrice;
	}
	
	public void removeAllProductsFromCart(WebDriver driver) throws InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		List<WebElement> allProducts = driver.findElements(By.xpath("//a[@class='removeproduct']"));
		System.out.println(allProducts.size());
		for(int i=allProducts.size()-1 ; i>=0; i--) {
			allProducts.get(i).click();
			
			Thread.sleep(2000);
			jse.executeScript("window.scrollBy(0,400)", 0);
			Thread.sleep(5000);
			driver.findElement(By.xpath("//div[@id='removeConfirmationModal']//div//div//button[2]")).click();
			Thread.sleep(2000);
			jse.executeScript("window.scrollBy(0,-200)", 0);
			Thread.sleep(2000);
			
			allProducts = driver.findElements(By.xpath("//a[@class='removeproduct']"));
			i = allProducts.size();
		}
	}
	
	public String getProductSubTotal(WebDriver driver) {
		String productTotal = driver.findElement(By.xpath("//div[@class='order-subtotal clearfix']//span[3]")).getText();
		return productTotal;
	}
	
	public String getShipping(WebDriver driver) {
		String productTotal = driver.findElement(By.xpath("//span[contains(@class,'discount')]")).getText();
		return productTotal;
	}
	
	public String getSalesTax(WebDriver driver) {
		String productTotal = driver.findElement(By.xpath("//div[@class='order-sales-tax clearfix']//span[3]")).getText();
		return productTotal;
	}
	
	public String getTotal(WebDriver driver) {
		String productTotal = driver.findElement(By.xpath("//div[@class='order-total clearfix']//span[3]")).getText();
		return productTotal;
	}
	
	public List<HashMap<String, String>> readPromotions(String brand, String promotions, String campaign) throws ClassNotFoundException, SQLException {
		
		List<HashMap<String, String>> promotionList = new ArrayList<HashMap<String, String>>();
			
		if(campaign.toLowerCase().contains("savemore")) {				
			String[] promotionArr = promotions.split("\n");
				
			for(int itr = promotionArr.length-1 ; itr>=0 ; itr--) {
//			for(String item : promotionArr) {
				HashMap<String, String> promotionMap = new HashMap<String, String>();
				
				String[] itemArr = promotionArr[itr].split(" - ");
				String offer = itemArr[0];
				String limit = itemArr[1];
				
				if(offer.contains("$")) {		
					offer = offer.replace("$", "");
					limit = limit.replace("$", "");
					offer = offer.replace(" off", "");
					if(limit.contains("or more")) {
						limit = limit.replace(" or more", "");
					}					
					promotionMap.put("Calc", "$ off");
					promotionMap.put("Value", offer);
					promotionMap.put("Limit", limit);
				}
				promotionList.add(promotionMap);
			}
		}
		else {
			campaign = campaign.replace("_CXT", "");
			HashMap<String, String> promotionMap = new HashMap<String, String>();
			
			String offer = "";
			String limit = "";
			if(campaign.contains("-")) {
				String[] campaignArr = campaign.split("-");
				offer = campaignArr[0];
				limit = campaignArr[1];
			}			
			else {
				offer = campaign;
			}
			
			if((offer.contains("FSH")) || (limit.contains("FSH"))) {
				promotionMap.put("FSH", "Yes");
				offer = offer.replace("FSH", "");
				limit = limit.replace("FSH", "");
			}
			if(((offer.contains("Poff")) || (offer.contains("P"))) && (!(offer.contains("Promo")))) {
				offer = offer.replace("Poff", "");
				offer = offer.replace("P", "");
							
				promotionMap.put("Calc", "% off");
				promotionMap.put("Value", offer);				
			}
			if(offer.contains("off")) {
				offer = offer.replace("off", "");
				
				promotionMap.put("Calc", "$ off");
				promotionMap.put("Value", offer);
			}
			if(offer.contains("FG")) {
				String FreeGiftPPID =  bf_obj.getPPIDfromString(brand, promotions).get(0);
				
				promotionMap.put("Free Gift PPID", FreeGiftPPID);
			}			
			if(limit.contains("over")) {
				limit = limit.replace("over", "");
				promotionMap.put("Limit", limit);
			}
			promotionList.add(promotionMap);
		}			
		return promotionList;
	}
}