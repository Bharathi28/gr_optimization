package com.sns.gr_optimization.cxt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sns.gr_optimization.setup.Authentication;
import com.sns.gr_optimization.testbase.CXTUtilities;
import com.sns.gr_optimization.testbase.CommonUtilities;
import com.sns.gr_optimization.testbase.MailUtilities;
import com.sns.gr_optimization.testbase.PromotionUtilities;

public class PromotionsCheck {
	
	PromotionUtilities promo_obj = new PromotionUtilities();
	CommonUtilities comm_obj = new CommonUtilities();
	CXTUtilities cxt_obj = new CXTUtilities();
	MailUtilities mailObj = new MailUtilities();
	Authentication auth_obj = new Authentication();
	
	List<List<String>> output = new ArrayList<List<String>>();
	String sendReportTo = "aaqil@searchnscore.com,manibharathi@searchnscore.com";
	
	@DataProvider(name="brands", parallel=true)
	public Object[][] testData() throws Exception {		
	
//		String[][] MBpromoData  = comm_obj.getExcelData(System.getProperty("user.dir") + "\\Input_Output\\CXTPromotionsValidation\\Promotions Data\\MeaningfulBeauty_Catalog_Promotions.xlsx", "Sheet1", 1);
//		String[][] SApromoData  = comm_obj.getExcelData(System.getProperty("user.dir") + "\\Input_Output\\CXTPromotionsValidation\\Promotions Data\\Smileactives_Catalog_Promotions.xlsx", "Sheet1", 1);
//		String[][] WYpromoData  = comm_obj.getExcelData(System.getProperty("user.dir") + "\\Input_Output\\CXTPromotionsValidation\\Promotions Data\\WestmoreBeauty_Catalog_Promotions.xlsx", "Sheet1", 1);
//		
//		Object[][] result = new Object[MBpromoData.length + SApromoData.length + WYpromoData.length][];
//
//		System.arraycopy(MBpromoData, 0, result, 0, MBpromoData.length);
//		System.arraycopy(SApromoData, 0, result, MBpromoData.length, SApromoData.length);
//		System.arraycopy(WYpromoData, 0, result, SApromoData.length, WYpromoData.length);
//		
//		
//		System.out.println(result);
//		Object[][] arrayObject = {{"MeaningfulBeauty"}, {"Smileactives"}, {"WestmoreBeauty"}};	
//		Object[][] arrayObject = {{"MeaningfulBeauty","Birthday"}};
//		Object[][] arrayObject = {{"CrepeErase","Birthday"}};
//		Object[][] arrayObject = {{"WestmoreBeauty","Birthday"}};
//		Object[][] arrayObject = {{"Smileactives"}};	
		Object[][] arrayObject = {{"WestmoreBeauty","Catalog"}};
		return arrayObject;
//		return result;
	}
	
	@Test(dataProvider="brands")
	public void PromotionsValidation(String brand, String promotionName) throws IOException, InterruptedException, ClassNotFoundException, SQLException {	
//	public void PromotionsValidation(String click_url_structure, String promotion, String associated_campaign, String associated_coupon, String associated_promotion, String old_keycode, String coupon_code_in_url, String vanity_url, String active_dates, String experience_requirements, String requester, String testing_notes) throws IOException, InterruptedException, ClassNotFoundException, SQLException {	
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/Drivers/chromedriver.exe");
		
		String[][] promoData = null;
		
		if(promotionName.equalsIgnoreCase("Catalog")) {
			promoData  = comm_obj.getExcelData(System.getProperty("user.dir") + "\\Input_Output\\CXTPromotionsValidation\\Promotions Data\\" + brand + "_Catalog_Promotions.xlsx", "Sheet1", 0);
			
		}
		else if(promotionName.equalsIgnoreCase("Birthday")) {
			promoData  = comm_obj.getExcelData(System.getProperty("user.dir") + "\\Input_Output\\CXTPromotionsValidation\\Promotions Data\\" + brand + "_Birthday_Promotions.xlsx", "Sheet1", 0);
		}
		
		
		int urlcolumn = 0;
		int promotioncolumn = 0;
		int promocampaigncolumn = 0;
		
		for(int j=0; j<promoData[0].length; j++) {
			if(promoData[0][j].equalsIgnoreCase("Click URL Structure")) {
				urlcolumn = j;
			}
			if(promoData[0][j].equalsIgnoreCase("Promotion")) {
				promotioncolumn = j;
			}
			if(promoData[0][j].equalsIgnoreCase("Associated Campaign")) {
				promocampaigncolumn = j;
			}
		}
		
		for(int i=1; i<promoData.length-1; i++) {
			
			String url = promoData[i][urlcolumn];
			String promotion = promoData[i][promotioncolumn];	
			String promocampaign = promoData[i][promocampaigncolumn];
		
//		String url = click_url_structure;
//		String promocampaign = associated_campaign;
//		String brand = "";
//		if(url.toLowerCase().contains("meaningfulbeauty")) {
//			brand = "MeaningfulBeauty";
//		}
//		else if(url.toLowerCase().contains("smileactives")) {
//			brand = "Smileactives";
//		}
//		else if(url.toLowerCase().contains("westmorebeauty")) {
//			brand = "WestmoreBeauty";
//		}
			
			url = url.replace("[UCI_code]", "ucistring");
			
			System.out.println(url);
			System.out.println(promotion);
			System.out.println(promocampaign);
			
			double previouslimit = 0;
			
			List<HashMap<String, String>> PromotionList = promo_obj.readPromotions(brand, promotion, promocampaign);
			
			for(HashMap<String, String> PromotionMap : PromotionList) {
				System.out.println(PromotionMap);
				System.out.println();
				
				// Launch Browser
				WebDriver driver = new ChromeDriver();
				driver.manage().window().maximize();
				driver.get(url);
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);	
				
				cxt_obj.LoginintoCXT(driver, brand, "Core", "PROD");	
				
				List<String> output_row = new ArrayList<String>();
				output_row.add("PROD");	
				output_row.add(brand);	
				output_row.add("CXT");	
				output_row.add(promocampaign);	
				
				if(driver.findElements(By.xpath("//h1[text()='Page Not Found']")).size() != 0) {
					output_row.add("Page Not Found");
					output.add(output_row);
					driver.quit();
					continue;
				}
				
				if(driver.findElements(By.xpath("//a[@class='mini-cart-link']//span")).size() != 0) {
					driver.findElement(By.xpath("//a[@class='mini-cart-link']//span")).click();
					Thread.sleep(2000);
					promo_obj.removeAllProductsFromCart(driver);
				}
				
				// Add products to Cart - Until limit
				double cartValue = 0;
				double subtotalValue = 0;
				
				if(brand.equalsIgnoreCase("Smileactives")) {
					String productPPID = PromotionMap.get("Offer_Product");
					
					if(productPPID.equalsIgnoreCase("Any")) {
						List<String> PPIDList = new ArrayList<String>();
						List<WebElement> tiles = driver.findElements(By.xpath("//div[contains(@class,'product-tile')]"));
						for(int itr=1; itr<=tiles.size(); itr++) {
							WebElement product = driver.findElement(By.xpath("(//div[contains(@class,'product-tile')])[" + itr + "]"));
							
							String ppid = product.getAttribute("data-itemid");
							
							if((!(ppid.equalsIgnoreCase("SA1A0028"))) && (!(ppid.equalsIgnoreCase("SA1A0118")))) {
								PPIDList.add(ppid);
							}							
						}
						
						String[] ppidArray = new String[PPIDList.size()];
						ppidArray = PPIDList.toArray(ppidArray);
						
						int rnd = new Random().nextInt(ppidArray.length);
						productPPID = ppidArray[rnd];
					}

					String xpath = "//div[@data-itemid='" + productPPID + "']//div//div[4]//h3//a";
					driver.findElement(By.xpath(xpath)).click();
					Thread.sleep(5000);
						
					if(driver.findElements(By.xpath("//li[contains(@class,'one-time ')]//label//span[@class='pdp-radio pixels_pdp-radio']//input")).size() != 0) {
					    driver.findElement(By.xpath("//li[contains(@class,'one-time ')]//label//span[@class='pdp-radio pixels_pdp-radio']//input")).click();
					}
					
					String randomProductPrice = "";
					
					if(driver.findElements(By.xpath("//strong[@class='pdp-price current-price oneShotSingle-price']")).size() != 0) {
						randomProductPrice = driver.findElement(By.xpath("//strong[@class='pdp-price current-price oneShotSingle-price']")).getText();
				    }
				    else {
				    	randomProductPrice = driver.findElement(By.xpath("//strong[@class='pdp-price current-price oneShotSingle-price']")).getText();
				    }	    	
					randomProductPrice = randomProductPrice.replace("$", "");
					double currentProductPrice = Double.parseDouble(randomProductPrice);	
					cartValue = Math.floor(currentProductPrice * 100.0) / 100.0;
				    
				    Thread.sleep(2000);
				    driver.findElement(By.xpath("//button[@id='add-to-cart']")).click();
				    Thread.sleep(2000);
				    driver.findElement(By.xpath("//a[@class='button mini-cart-link-checkout small-12']")).click();						
				}
				else {
					if(promocampaign.contains("savemore")) {
						double limitValue = Double.parseDouble(PromotionMap.get("Limit"));
						
						if(previouslimit == 0) {
							cartValue = promo_obj.addProductsAsPerLimit(driver, brand, limitValue, 0, "above");
						}
						else {
							cartValue = promo_obj.addProductsAsPerLimit(driver, brand, previouslimit, limitValue, "between");
						}					
						previouslimit = limitValue;					
					}
					else {
						if(PromotionMap.containsKey("Limit")) {				
							double limitValue = Double.parseDouble(PromotionMap.get("Limit"));	
//							double limit_roundOff = Math.floor(limitValue * 100.0) / 100.0;	
							
							cartValue = promo_obj.addProductsAsPerLimit(driver, brand, limitValue, 0, "above");				
						}		
						else {
							String RandomProductprice = promo_obj.addRandomProductToCart(driver, brand);
							RandomProductprice = RandomProductprice.replace("$", "");
							double currentProductPrice = Double.parseDouble(RandomProductprice);	
							double price_roundOff = Math.floor(currentProductPrice * 100.0) / 100.0;
							
							cartValue = cartValue + price_roundOff;
						}						
						System.out.println("Cart Value : " + cartValue);
					}
				}				
				
				String result = "";
				String remarks = "";
				
				// Check Free Shipping and Handling
				if(PromotionMap.containsKey("FSH")) {				
					String shipping = driver.findElement(By.xpath("//span[contains(@class,'discount')]")).getText();
					if(shipping.equalsIgnoreCase("FREE")) {
						result = result + "FSH - PASS , ";
					}
					else {
						result = result + "FSH - FAIL , ";
					}				
				}
								
				// Check % off/$ off
				double discount = 0;
				if(PromotionMap.containsKey("Calc")) {
					if(PromotionMap.get("Calc").equalsIgnoreCase("% off")) {
						
						double offerValue = Double.parseDouble(PromotionMap.get("Value"));	
//						double value_roundOff = Math.floor(offerValue * 100.0) / 100.0;	
						
						discount = (cartValue * offerValue)/100;
						System.out.println("Discount " + discount);			
					}
					else if(PromotionMap.get("Calc").equalsIgnoreCase("$ off")) {
						discount = Double.parseDouble(PromotionMap.get("Value"));
						System.out.println("Discount " + discount);
					}
					
					double offerAppliedPrice = cartValue - discount;
					System.out.println("Offer applied Price " + offerAppliedPrice);
					
					String subtotal = promo_obj.getProductSubTotal(driver, brand);
					subtotal = subtotal.replace("$", "");				
					subtotalValue = Double.parseDouble(subtotal);				
					System.out.println("subtotalValue " + subtotalValue);
					
					if(PromotionMap.get("Calc").equalsIgnoreCase("$ off")) {
						String shipping = promo_obj.getShipping(driver, brand);
						shipping = shipping.replace("$", "");
						double shippingValue = Double.parseDouble(shipping);
						System.out.println("shippingValue " + shippingValue);
						
						String salestax = promo_obj.getSalesTax(driver, brand);
						salestax = salestax.replace("$", "");
						double salestaxValue = Double.parseDouble(salestax);
						System.out.println("salestaxValue " + salestaxValue);
						
						double expectedTotal = offerAppliedPrice + shippingValue + salestaxValue;
						expectedTotal = Math.ceil(expectedTotal * 100.0) / 100.0;	
						System.out.println("expectedTotal " + expectedTotal);
						
						String total = promo_obj.getTotal(driver, brand);
						total = total.replace("$", "");
						double actualTotal = Double.parseDouble(total);
						System.out.println("actualTotal " + actualTotal);					
						
						if(Double.compare(expectedTotal, actualTotal) == 0) {
							result = result + PromotionMap.get("Value") + PromotionMap.get("Calc") + " - PASS , ";
						}
						else {
							result = result + PromotionMap.get("Value") + PromotionMap.get("Calc") + " - FAIL , ";
							remarks = remarks + "Expected total : " + expectedTotal + " , Actual total : " + actualTotal + " , ";
						}
					}
					else {
						if(Double.compare(offerAppliedPrice, subtotalValue) == 0) {
							result = result + PromotionMap.get("Value") + PromotionMap.get("Calc") + " - PASS , ";
						}
						else {
							result = result + PromotionMap.get("Value") + PromotionMap.get("Calc") + " - FAIL , ";
							remarks = remarks + "Expected subtotal : " + offerAppliedPrice + " , Actual subtotal : " + subtotalValue + " , ";
						}
					}							
				}
				
				// Check Free Gift
				if(PromotionMap.containsKey("Free Gift PPID")) {
					String actualGiftPPID = driver.findElement(By.xpath("(//span[@class='PPID disclaimer-ppid'])[last()]")).getText();
					String expectedGiftPPID = PromotionMap.get("Free Gift PPID");
					
					if(expectedGiftPPID.equalsIgnoreCase(actualGiftPPID)) {
						result = result + "Free Gift - " + expectedGiftPPID + " - PASS , ";
					}		
					else {
						result = result + "Free Gift - " + expectedGiftPPID + " - FAIL , ";
					}			
				}
				
				output_row.add(result);
				output_row.add(remarks);
				output.add(output_row);
				
				Thread.sleep(4000);
				promo_obj.removeAllProductsFromCart(driver);
				Thread.sleep(3000);
				cxt_obj.LogoutCXT(driver, brand, "Core");
				driver.quit();
			}
		}		
	}
	
	@AfterSuite
	public void populateExcel() throws IOException {
		File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\CXTPromotionsValidation", "Run_Output");
		newDirectory.mkdir();
		String file = comm_obj.populateOutputExcel(output, "CXTPromotionsValidationResults", System.getProperty("user.dir") + "\\Input_Output\\CXTPromotionsValidation\\Run_Output\\");
		
		List<String> attachmentList = new ArrayList<String>();
		attachmentList.add(file);	
		
		mailObj.sendEmail("CXT Promotions Validation Results", sendReportTo, attachmentList);
	}
}
