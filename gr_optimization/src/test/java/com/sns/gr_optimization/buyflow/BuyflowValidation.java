package com.sns.gr_optimization.buyflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.sns.gr_optimization.setup.BaseTest;
import com.sns.gr_optimization.testbase.BuyflowUtilities;
import com.sns.gr_optimization.testbase.CartLanguageUtilities;
import com.sns.gr_optimization.testbase.CommonUtilities;
import com.sns.gr_optimization.testbase.DBLibrary;
import com.sns.gr_optimization.testbase.DBUtilities;
import com.sns.gr_optimization.testbase.MailUtilities;
import com.sns.gr_optimization.testbase.MerchandisingUtilities;
import com.sns.gr_optimization.testbase.PixelUtilities;
import com.sns.gr_optimization.testbase.PricingUtilities;
import com.sns.gr_optimization.testbase.SASUtilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

public class BuyflowValidation {

	CommonUtilities comm_obj = new CommonUtilities();
	DBUtilities db_obj = new DBUtilities();
	BuyflowUtilities bf_obj = new BuyflowUtilities();
	PricingUtilities pr_obj = new PricingUtilities();
	CartLanguageUtilities lang_obj = new CartLanguageUtilities();
	MailUtilities mailObj = new MailUtilities();
	SASUtilities sas_obj = new SASUtilities();
	MerchandisingUtilities merch_obj = new MerchandisingUtilities();
	PixelUtilities pixel_obj = new PixelUtilities();
//	BaseTest base_obj = new BaseTest();
	Scanner in = new Scanner(System.in);
	static List<String> attachmentList = new ArrayList<String>();
	
	List<List<String>> output = new ArrayList<List<String>>();
	String sendReportTo = "manibharathi@searchnscore.com";
//	String env = "";
//	String env = System.getProperty("Environment");
	
//	@Parameters({ "environment" })
	@BeforeSuite
	public void getEmailId() {
//	public void getEmailId(String environment) {
//		env = environment;
//		System.out.println("Enter Email id : ");
//		sendReportTo = in.next();
	}
	
	@DataProvider(name="buyflowInput", parallel=true)
	public Object[][] testData() {
		Object[][] arrayObject = null;
		arrayObject = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/BuyflowValidation/new_run_input.xlsx", "rundata", 1);
		return arrayObject;
	}	
	
	@Test(dataProvider="buyflowInput")
	public void buyflow(String env, String brand, String campaign, String category, String kitppid, String giftppid, String shipbill, String cc, String browser, String pixelStr) throws IOException, ClassNotFoundException, SQLException, InterruptedException {	
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/Drivers/chromedriver.exe");
		
		// start the proxy
	    BrowserMobProxy proxy = new BrowserMobProxyServer();
	    proxy.setTrustAllServers(true);
	    proxy.start(0);

	    // get the Selenium proxy object
	    Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);	    
	    
	    ChromeOptions options = new ChromeOptions();
	    options.addArguments("--ignore-certificate-errors");
	    options.addArguments("--disable-backgrounding-occluded-windows");
//	    options.addArguments("--no-sandbox");
//	    options.addArguments("--disable-dev-shm-usage");
	    
	    // configure it as a desired capability
	    DesiredCapabilities capabilities = new DesiredCapabilities();
	    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
	    capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
	    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	    capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);		
		
		// Get Source Code Information - Campaign Category
		String campaigncategory = db_obj.checkcampaigncategory(brand, campaign);
		if(campaigncategory.equalsIgnoreCase("n/a")) {
			campaigncategory = campaign;
		}
		
		///////////////////////////////////////////////////////////////		
		// Read all Merchandising Input
		String[][] catalogData = null;
		String[][] merchData = null;		
		
		List<String> category_list = Arrays.asList(category.split(","));
		System.out.println(category_list);
		
		// Read Web Catalog
		if((category_list.contains("Product")) || (category_list.contains("SubscribeandSave"))) {
			catalogData = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/BuyflowValidation/Merchandising Input/" + brand + "/Web Catalog.xlsx", "Acq", 0);
//			merch_obj.getProdRowfromCatalog(catalogData, kitppid);
//			System.out.println(catalogData);
		}
		
		// Read Merchandising Input
		if(category_list.contains("Kit")) {
			merchData = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/BuyflowValidation/Merchandising Input/" + brand + "/" + campaigncategory + ".xlsx", "Active Campaign", 0);
		}
		///////////////////////////////////////////////////////////////
		
		// Given offer and category
		String[] offer_array = kitppid.split(",");
		String[] category_array = category.split(",");
		
		// Generate Final Offerlist and categorylist
		List<String> offerlist = new ArrayList<String>();
		List<String> categorylist = new ArrayList<String>();
		
		for(int i=0 ; i<offer_array.length ; i++) {
			if(offer_array[i].contains("single")) {
				String[] single_array = offer_array[i].split(" ");
				String no_of_singles_str = single_array[0];
				int no_of_singles = Integer.parseInt(no_of_singles_str);
				
				List<String> single_offers = merch_obj.fetch_random_singles(catalogData, no_of_singles);
				
				for(String single_offer : single_offers) {
					offerlist.add(single_offer);
					categorylist.add(category_array[i]);
				}
			}
			else {
				offerlist.add(offer_array[i]);
				categorylist.add(category_array[i]);
			}
		}
		System.out.println(offerlist);
		System.out.println(categorylist);
		
		// Intialize result variables
		String remarks = "";
		String giftResult = "";
		String ppidResult = "";
		String SASPriceResult = "";
		String EntryPriceResult = "";
		String ContinuityPriceResult = "";
		String CartLanguageResult = "";
		String SuppCartLanguageResult = "";
		String RenewalPlanResult = "";
		String InstallmentPlanResult = "";
		String MediaIdResult = "";
		String CreativeIdResult = "";
		String VenueIdResult = "";
		String PriceBookIdResult = "";
		
		ListIterator<String> offerIterator = offerlist.listIterator();
		ListIterator<String> categoryIterator = categorylist.listIterator();
		
		// Launch Browser
		WebDriver driver = new ChromeDriver(capabilities);
		driver.manage().window().maximize();
		
		// enable more detailed HAR capture, if desired (see CaptureType for the complete list)
	    proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
		
		String url = db_obj.getUrl(brand, campaign, env);
		url = pixel_obj.generateURL(url, pixelStr, brand);

		// Har file pattern based on Pixel
		String pattern = pixel_obj.getPattern(url, pixelStr);
		
		// HomePage
		pixel_obj.defineNewHar(proxy, brand + "HomePage");				
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);	
		pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_homepage_" + pattern +".har", driver);
				
		while(offerIterator.hasNext() && categoryIterator.hasNext()) {
			String ppid = offerIterator.next();
			
			HashMap<String, String> sourcecodedata = merch_obj.getSourceCodeInfo(merchData, campaign);			
			
			if(categoryIterator.next().equalsIgnoreCase("Kit")) {
				// Get column in which the PPID is present and check if the PPID belongs to Pre-Purchase Entry Kit
				int PPIDcolumn = merch_obj.getPPIDColumn(merchData, ppid);
				String PPUSection = merch_obj.IsPrePurchase(merchData, ppid);
				
				if(((brand.equalsIgnoreCase("MeaningfulBeauty")) && (campaign.equalsIgnoreCase("os"))) || ((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("advanced-one"))) || ((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fsh2b")))){
					if(giftppid.equalsIgnoreCase("No")) {
						PPUSection = "No";
					}
					else {
						PPUSection = "Yes";
					}
				}
				System.out.println(PPIDcolumn + PPUSection);
				
				// Check if the PPID is present in the campaign
				if(PPIDcolumn == 0) {
					remarks = remarks + kitppid + " doesn't exist in " + brand + " - " + campaigncategory;
					continue;
				}
	
				// Read the entire column data
				HashMap<String, String> kit_offerdata = merch_obj.getColumnData(merchData, PPIDcolumn, PPUSection);
					
				// Check Post-purchase Upsell
				String postpu = merch_obj.checkPostPU(kit_offerdata);
				if(((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fsh2b"))) && (PPUSection.equalsIgnoreCase("No"))) {
					postpu = "No";
				}
				
				String pagepattern = kit_offerdata.get("PagePattern").trim();
				
				String prepu = "";
				if(pagepattern.contains("prepu")) {
					prepu = "Yes";
				}
				else {
					prepu = "No";
				}
				
				// List pages in this campaign
				List<String> campaignpages = new ArrayList<String>();
				campaignpages.add("HomePage");
				campaignpages.add("SASPage");
				if(prepu.equalsIgnoreCase("Yes")) {
					campaignpages.add("PrePurchaseUpsell");
				}
				campaignpages.add("CheckoutPage");
				if(postpu.equalsIgnoreCase("Yes")) {
					campaignpages.add("PostPurchaseUpsell");
				}
				campaignpages.add("ConfirmationPage");	
				
				// Collect current Offer related details from Merchandising Input file
				HashMap<String, String> expectedofferdata_kit = merch_obj.generateExpectedOfferDataForKit(kit_offerdata, sourcecodedata, PPUSection, postpu, kitppid, giftppid, brand, campaigncategory);
				System.out.println("Expected Offerdata - Kit : " + expectedofferdata_kit);
				
				// Move to SAS
				pixel_obj.defineNewHar(proxy, brand + "SASPage");	  
				bf_obj.click_cta(driver, brand, campaign, "Ordernow");
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_saspage_" + pattern +".har", driver);
			
				// Gift Validation
				if(!(expectedofferdata_kit.get("Gift Name").equalsIgnoreCase("No Gift"))) {
					if(!(expectedofferdata_kit.get("Campaign Gifts").equalsIgnoreCase("-"))) {
						giftResult = bf_obj.checkGifts(driver, brand, campaigncategory, expectedofferdata_kit.get("Campaign Gifts"));
						remarks = remarks + giftResult;
					}
				}								
				
				// Select offer				
				sas_obj.select_offer(driver, expectedofferdata);
				
				// Move to Checkout
				pixel_obj.defineNewHar(proxy, brand + "CheckoutPage");
				bf_obj.move_to_checkout(driver, brand, campaigncategory, category);
			}
			else if(categoryIterator.next().equalsIgnoreCase("Product")) {
				HashMap<String, String> product_offerdata = merch_obj.getProdRowfromCatalog(catalogData, ppid);
				String pagepattern = product_offerdata.get("PagePattern").trim();
			}
			else if(categoryIterator.next().equalsIgnoreCase("SubscribeandSave")) {
				HashMap<String, String> product_offerdata = merch_obj.getProdRowfromCatalog(catalogData, ppid);
				String pagepattern = product_offerdata.get("PagePattern").trim();
			}			
			
			if(offerIterator.hasNext() && categoryIterator.hasNext()) {
				bf_obj.click_logo(driver, brand, campaigncategory);
			}		
		}
		
		// Checkout Page Validation
		// Validate Line Items
		List<String> lineitems = bf_obj.getLineItems(driver);
		
		// Validate Added Kit
		if(lineitems.contains(expectedofferdata.get("30 day PPID"))) {
			ppidResult = "PASS";
		}
		else {
			ppidResult = "FAIL";
			remarks = remarks + "Wrong Kit added to cart, Expected - " + expectedofferdata.get("30 day PPID") + " , Actual - " + lineitems.get(0);
		}
						
		// Validate Added Gift
		if(!(expectedofferdata.get("Gift Name").equalsIgnoreCase("No Gift"))) {
			if(expectedofferdata.get("GiftSeperateLineItem").equalsIgnoreCase("Yes")) {
				if(!(expectedofferdata.get("Campaign Gifts").equalsIgnoreCase("-"))) {
					// When there is no gift choice
					if(giftppid.equalsIgnoreCase("-")) {
						giftppid = bf_obj.getPPIDfromString(brand, expectedofferdata.get("Campaign Gifts")).get(0);
					}
					
					// In case of gift combo - multiple gift ppids
					if(giftppid.contains(",")) {								
						String[] giftarr = giftppid.split(",");
						if((lineitems.contains(giftarr[0])) &&  (lineitems.contains(giftarr[1]))){
							ppidResult = "PASS";
						}
						else {
							ppidResult = "FAIL";
							remarks = remarks + "Wrong Gift added to cart, Expected - " + giftppid + " , Actual - " + lineitems;
						}
					}
					else {								
						if(lineitems.contains(giftppid)) {
							ppidResult = "PASS";
						}
						else {
							ppidResult = "FAIL";
							remarks = remarks + "Wrong Gift added to cart, Expected - " + giftppid + " , Actual - " + lineitems;
						}
					}
				}
			}
		}	
		
		// Validate entry kit price
		String checkoutentrykitprice = pr_obj.getCheckoutEntryKitPrice(driver, brand, campaign);
		if(expectedofferdata.get("Entry Pricing").contains(checkoutentrykitprice)) {
			EntryPriceResult = "PASS";
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Entry Kit Price on Checkout Page is wrong, Expected - " + expectedofferdata.get("Entry Pricing") + " , Actual - " + checkoutentrykitprice;
		}				
		
		// Fill out form
		String email = "";
		
		// Fall-back scenario
		// Paypal - Cannot fill-in invalid address-zipcode
		if(cc.equalsIgnoreCase("Paypal")) {
			email = bf_obj.fill_out_form(driver, brand, campaigncategory, cc, shipbill, "30");
			System.out.println("Email : " + email);
			if(!(email.contains("testbuyer"))) {
				remarks = remarks + "Paypal Down - Credit card order placed";
				cc = "Visa";
			}
		}
		else {
			if(expectedofferdata.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {
				email = bf_obj.fill_out_form(driver, brand, campaigncategory, "VISA", "same", "90");
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_checkoutpage_" + pattern +".har", driver);
				
				pixel_obj.defineNewHar(proxy, brand + "PostPurchaseUpsell");	  				
				bf_obj.complete_order(driver, brand, "VISA");
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_postpurchaseupsell_" + pattern +".har", driver);
				
				bf_obj.upsell_confirmation(driver, brand, campaigncategory, expectedofferdata.get("Offer Post-Purchase"));
			}
			else {
				email = bf_obj.fill_out_form(driver, brand, campaigncategory, cc, shipbill, "30");
				System.out.println("Email : " + email);
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_checkoutpage_" + pattern +".har", driver);
			}	
		}						
		
		String cartlang_pricing = "";
		if(!(expectedofferdata.get("Continuity Pricing").equalsIgnoreCase("No Continuity"))) {
			// Scenario - 90-day order + Paypal - could not validate 90-day cart language and supplemental language because invalid zipcode could not be fill-in for Paypal
			if(!((cc.equalsIgnoreCase("Paypal")) && (expectedofferdata.get("Offer Post-Purchase").equalsIgnoreCase("Yes")))) {
				// Validate Cart Language
				String full_cart_lang = lang_obj.getfullcartlanguage(driver);
				
				//Remove whitespace
				String expcartlang = expectedofferdata.get("Cart Language").replaceAll("\\s+", "");
				String actcartlang = full_cart_lang.replaceAll(" ", "");
				
				// Remove special characters
				expcartlang = expcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
				actcartlang = actcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
//				System.out.println("Actual Edited  : " + actcartlang);
//				System.out.println("Expected Edited: " + expcartlang);
				
				if(actcartlang.equalsIgnoreCase(expcartlang)) {
					CartLanguageResult = "PASS";
				}
				else {
//					System.out.println("Actual : " + full_cart_lang);
//					System.out.println("Expected : " + expectedofferdata.get("Cart Language"));
					CartLanguageResult = "FAIL";
					remarks = remarks + "Cart Language is wrong, Expected - " + expectedofferdata.get("Cart Language") + " , Actual - " + full_cart_lang;
				}
				
				// Validate Continuity pricing
				String cart_lang = lang_obj.getfullcartlanguage(driver);						
				String[] lang_price_arr = lang_obj.parse_cart_language(cart_lang);		
//				String cart_lang_price = "$" + lang_price_arr[1];
//				String cart_lang_shipping = "$" + lang_price_arr[2];	
				
				String cart_lang_price = lang_price_arr[1];
				String cart_lang_shipping = lang_price_arr[2];	
				cartlang_pricing = cart_lang_price + "," + cart_lang_shipping;
								
				if(expectedofferdata.get("Continuity Pricing").equalsIgnoreCase(cart_lang_price)) {
					ContinuityPriceResult = "PASS";
				}
				else {
					ContinuityPriceResult = "FAIL";
					remarks = remarks + "Continuity Price is wrong, Expected - " + expectedofferdata.get("Continuity Pricing") + " , Actual - " + cart_lang_price;
				}
				
				if(expectedofferdata.get("Continuity Shipping").equalsIgnoreCase(cart_lang_shipping)) {
					
					// If Result is already fail, then the overall Continuity result is fail
					if(ContinuityPriceResult.equalsIgnoreCase("FAIL")) {
						ContinuityPriceResult = "FAIL";
					}
					else {
						ContinuityPriceResult = "PASS";
					}						
				}
				else {
					ContinuityPriceResult = "FAIL";
					remarks = remarks + "Continuity Shipping is wrong, Expected - " + expectedofferdata.get("Continuity Shipping") + " , Actual - " + cart_lang_shipping;
				}	
				
				// Validate supplemental cart language
				String supp_cart_lang = lang_obj.getsupplementalcartlanguage(driver);
								
				//Remove whitespace
				String expsuppcartlang = expectedofferdata.get("Supplemental Cart Language").replaceAll("\\s+", "");
				String actsuppcartlang = supp_cart_lang.replaceAll(" ", "");
				
				// Remove special characters
				expsuppcartlang = expsuppcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
				actsuppcartlang = actsuppcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
//				System.out.println("Actual Edited  : " + expsuppcartlang);
//				System.out.println("Expected Edited: " + actsuppcartlang);
								
				if(actsuppcartlang.equalsIgnoreCase(expsuppcartlang)) {
					SuppCartLanguageResult = "PASS";
				}
				else {
//					System.out.println("Actual : " + supp_cart_lang);
//					System.out.println("Expected : " + expectedofferdata.get("Supplemental Cart Language"));
					SuppCartLanguageResult = "FAIL";
					remarks = remarks + "Supplemental Cart Language is wrong, Expected - " + expectedofferdata.get("Supplemental Cart Language") + " , Actual - " + supp_cart_lang;
				}
			}							
//		}
		
		// Validate Checkout pricing
		String checkout_subtotal = "";
		String checkout_shipping = "";
		String checkout_salestax = "";
		String checkout_total = "";
		
		String realm = DBUtilities.get_realm(brand);
		
		if((cc.equalsIgnoreCase("Paypal")) && (realm.equalsIgnoreCase("R2"))) {
			checkout_subtotal = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Paypal Review Subtotal");
			checkout_shipping = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Paypal Review Shipping");
			checkout_salestax = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Paypal Review Salestax");
			checkout_total = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Paypal Review Total");
		}
		else {
			checkout_subtotal = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Checkout Subtotal");
			checkout_shipping = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Checkout Shipping");
			checkout_salestax = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Checkout Salestax");
			checkout_total = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Checkout Total");
		}			
//		System.out.println("Checkout Pricing expected : " + expectedofferdata.get("Final Pricing") + "," + expectedofferdata.get("Final Shipping"));
//		System.out.println("Checkout Pricing fetched : " + checkout_subtotal + "," + checkout_shipping + "," + checkout_salestax + "," + checkout_total);
					
		if(expectedofferdata.get("Final Pricing").contains(checkout_subtotal)) {
			EntryPriceResult = "PASS";
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Checkout Subtotal does not match with the expected price, Expected - " + expectedofferdata.get("Final Pricing") + " , Actual - " + checkout_subtotal;
		}
		
		if((expectedofferdata.get("Final Shipping").contains("0.0")) || (expectedofferdata.get("Final Shipping").equalsIgnoreCase("FREE")) || (expectedofferdata.get("Final Shipping").equalsIgnoreCase("0"))) {
			if((checkout_shipping.contains("0.0")) || (checkout_shipping.equalsIgnoreCase("FREE"))) {
				// If Result is already fail, then the overall EntryPrice result is fail
				if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
					EntryPriceResult = "FAIL";
				}
				else {
					EntryPriceResult = "PASS";
				}	
			}
		}				
		else if(expectedofferdata.get("Final Shipping").equalsIgnoreCase(checkout_shipping)) {
			// If Result is already fail, then the overall EntryPrice result is fail
			if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
				EntryPriceResult = "FAIL";
			}
			else {
				EntryPriceResult = "PASS";
			}	
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Checkout Shipping does not match with the expected shipping price, Expected - " + expectedofferdata.get("Final Shipping") + " , Actual - " + checkout_shipping;
		}
		
		// Checkout SalesTax Validation
		String salestax = bf_obj.getSalesTax(driver, expectedofferdata.get("Final Pricing"), expectedofferdata.get("Final Shipping"));
		
		Double expected_salestax = Double.valueOf(salestax);
		Double actual_salestax = Double.valueOf(checkout_salestax);
		Double diff = Math.abs(expected_salestax-actual_salestax);				
		double roundOff = Math.floor(diff * 100.0) / 100.0;
		int diff_value = (int)roundOff;

		if(diff_value == 0) {
			EntryPriceResult = "PASS";
		}				
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Checkout Salestax does not match with the expected salestax, Expected - " + salestax + " , Actual - " + checkout_salestax;
		}
		
//		System.out.println("Expected SalesTax : " + salestax);
//		System.out.println("Actual SalesTax : " + checkout_salestax);
		
		// Checkout Total Validation
		String total = bf_obj.getTotal(expectedofferdata.get("Final Pricing"), expectedofferdata.get("Final Shipping"), salestax);
		
		Double expected_total = Double.valueOf(total);
		Double actual_total = Double.valueOf(checkout_total);	
		diff = Math.abs(expected_total-actual_total);				
		roundOff = Math.floor(diff * 100.0) / 100.0;
		diff_value = (int)roundOff;

		if(diff_value == 0) {
			EntryPriceResult = "PASS";
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Checkout Total does not match with the expected total, Expected - " + total + " , Actual - " + checkout_total;
		}
		
//		System.out.println("Expected Total : " + total);
//		System.out.println("Actual Total : " + checkout_total);
		
		
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		if(!(cc.equalsIgnoreCase("Paypal"))) {
			if(expectedofferdata.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {
				jse.executeScript("window.scrollBy(0,600)", 0);
				bf_obj.clear_form_field(driver, realm, "Zip");
				bf_obj.fill_form_field(driver, realm, "Zip", "90245");
				Thread.sleep(2000);
				bf_obj.fill_form_field(driver, realm, "CVV", "349");	
			}
		}							
		
		// No Post Purchase Upsell page
		if(postpu.equalsIgnoreCase("No")) {
			pixel_obj.defineNewHar(proxy, brand + "ConfirmationPage");
        	// Navigate to Confirmation Page	        
        	bf_obj.complete_order(driver, brand, cc);          
            pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_confirmationpage_" + pattern + ".har", driver);
		}
		// Post Purchase Upsell page is present
		else {
//			System.out.println("Else block - Post Purchase Upsell page is present");
			// supplysize - 30
			// No Fall back scenario
			if(expectedofferdata.get("SupplySize").equalsIgnoreCase("30")) {
//				System.out.println("Else block - supplysize 30");
				pixel_obj.defineNewHar(proxy, brand + "PostPurchaseUpsell");	  				
				bf_obj.complete_order(driver, brand, cc);
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_postpurchaseupsell_" + pattern + ".har", driver);
			}
			// supplysize - 90
			// After Fall back scenario - control navigates to Confirmation page
			else if(expectedofferdata.get("SupplySize").equalsIgnoreCase("90")) {
//				System.out.println("Else block - else if block - supplysize 90");
				pixel_obj.defineNewHar(proxy, brand + "ConfirmationPage");	        
	        	bf_obj.complete_order(driver, brand, cc);          
	            pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_confirmationpage_" + pattern + ".har", driver);
			}					
		}				
		
		Thread.sleep(3000);
		
//		// Upsell page validations
//		driver.findElement(By.xpath("//i[@class='fa fa-plus']")).click();				
		
		// No Upsell Confirmation if fall-back has happened previously(90-day - CC Order)
		// For 30-day order or Paypal order - Select Upsell Confirmation
		if(postpu.equalsIgnoreCase("Yes")) {
			if((expectedofferdata.get("SupplySize").equalsIgnoreCase("30")) || (cc.equalsIgnoreCase("Paypal"))) {
				pixel_obj.defineNewHar(proxy, brand + "ConfirmationPage");
				bf_obj.upsell_confirmation(driver, brand, campaigncategory, expectedofferdata.get("Offer Post-Purchase"));
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_confirmationpage_" + pattern + ".har", driver);
			}
		}								
		
		// Confirmation page validations
		String conf_offercode = bf_obj.fetch_confoffercode(driver, brand);
		System.out.println("Confirmation PPIDs : " + conf_offercode);
		if(conf_offercode.contains(kitppid)){
			// If Result is already fail, then the overall ppid result is fail
			if(ppidResult.equalsIgnoreCase("FAIL")) {
				ppidResult = "FAIL";
			}
			else {
				ppidResult = "PASS";
			}	
		}
		else {
			ppidResult = "FAIL";
			remarks = remarks + "Confirmation page - Wrong Kit added, Expected - " + kitppid + " , Actual - " + conf_offercode;
		}
		
		if(!(expectedofferdata.get("Gift Name").equalsIgnoreCase("No Gift"))) {
			if(expectedofferdata.get("GiftSeperateLineItem").equalsIgnoreCase("Yes")) {
				if(!(expectedofferdata.get("Campaign Gifts").equalsIgnoreCase("-"))) {
					if(conf_offercode.contains(giftppid)){
						// If Result is already fail, then the overall ppid result is fail
						if(ppidResult.equalsIgnoreCase("FAIL")) {
							ppidResult = "FAIL";
						}
						else {
							ppidResult = "PASS";
						}	
					}
					else {
						ppidResult = "FAIL";
						remarks = remarks + "Confirmation page - Wrong Gift added, Expected - " + giftppid + " , Actual - " + conf_offercode;
					}
				}	
			}
		}		
		
		// Validate PrepU Product
		if(!(expectedofferdata.get("PrePU Product").equalsIgnoreCase("No PrePU Product"))) {
			String expectedprepuppid = bf_obj.getPPIDfromString(brand, expectedofferdata.get("PrePU Product")).get(0);
			if(conf_offercode.contains(expectedprepuppid)){
				// If Result is already fail, then the overall ppid result is fail
				if(ppidResult.equalsIgnoreCase("FAIL")) {
					ppidResult = "FAIL";
				}
				else {
					ppidResult = "PASS";
				}	
			}
			else {
				ppidResult = "FAIL";
				remarks = remarks + "Confirmation page - PrePU Lineitem missing, Expected - " + expectedprepuppid + " , Actual - " + conf_offercode;
			}
		}
		
		// Validate PostpU Product
		if(!(expectedofferdata.get("PostPU Product").equalsIgnoreCase("No PostPU Product"))) {
			String expectedpostpuppid = bf_obj.getPPIDfromString(brand, expectedofferdata.get("PostPU Product")).get(0);
			if(conf_offercode.contains(expectedpostpuppid)){
				// If Result is already fail, then the overall ppid result is fail
				if(ppidResult.equalsIgnoreCase("FAIL")) {
					ppidResult = "FAIL";
				}
				else {
					ppidResult = "PASS";
				}	
			}
			else {
				ppidResult = "FAIL";
				remarks = remarks + "Confirmation page - PostPU Lineitem missing, Expected - " + expectedpostpuppid + " , Actual - " + conf_offercode;
			}
		}
		
		Screenshot confpage = new AShot().takeScreenshot(driver);
		ImageIO.write(confpage.getImage(),"PNG",new File(System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Screenshots\\" + brand + "\\" + campaign + "_" + kitppid +".png"));
		
		String conf_num = bf_obj.fetch_conf_num(driver, brand);
		System.out.println("Confirmation Number : " + conf_num);				
						
		// Confirmation Price Validation
		String conf_subtotal = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Confirmation Subtotal");
		String conf_shipping = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Confirmation Shipping");
		String conf_salestax = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Confirmation Salestax");
		String conf_total = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Confirmation Total");
		
		String conf_pricing = conf_subtotal + " ; " + conf_shipping + " ; " + conf_salestax + " ; " + conf_total;	
//		System.out.println("Confirmation Pricing fetched : " + conf_pricing);
		
		// Subtotal validation
		if(expectedofferdata.get("Final Pricing").contains(conf_subtotal)) {
			// If Result is already fail, then the overall EntryPrice result is fail
			if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
				EntryPriceResult = "FAIL";
			}
			else {
				EntryPriceResult = "PASS";
			}	
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Confirmation Subtotal is wrong, Expected - " + expectedofferdata.get("Final Pricing") + " , Actual - " + conf_subtotal + ",";
		}
		
		// Shipping validation
		if((expectedofferdata.get("Final Shipping").contains("0.0")) || (expectedofferdata.get("Final Shipping").equalsIgnoreCase("FREE")) || (expectedofferdata.get("Final Shipping").equalsIgnoreCase("0"))) {
			if((conf_shipping.contains("0.0")) || (conf_shipping.equalsIgnoreCase("FREE"))) {
				// If Result is already fail, then the overall EntryPrice result is fail
				if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
					EntryPriceResult = "FAIL";
				}
				else {
					EntryPriceResult = "PASS";
				}	
			}
		}				
		else if(expectedofferdata.get("Final Shipping").equalsIgnoreCase(conf_shipping)) {
			// If Result is already fail, then the overall EntryPrice result is fail
			if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
				EntryPriceResult = "FAIL";
			}
			else {
				EntryPriceResult = "PASS";
			}	
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Shipping price on confirmation page is wrong, Expected - " + expectedofferdata.get("Final Shipping") + " , Actual - " + checkout_shipping;
		}
				
		// Sales Tax Validation
		expected_salestax = Double.valueOf(salestax);
		actual_salestax = Double.valueOf(conf_salestax);				
		diff = Math.abs(expected_salestax-actual_salestax);				
		roundOff = Math.floor(diff * 100.0) / 100.0;
		diff_value = (int)roundOff;

		if(diff_value == 0) {
			// If Result is already fail, then the overall EntryPrice result is fail
			if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
				EntryPriceResult = "FAIL";
			}
			else {
				EntryPriceResult = "PASS";
			}	
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "SalesTax on Confirmation page does not match with that of checkout page, Expected - " + salestax + " , Actual - " + conf_salestax + ",";
		}
		
		// Total Price Validation
		expected_total = Double.valueOf(total);
		actual_total = Double.valueOf(conf_total);
		diff = Math.abs(expected_total-actual_total);						
		roundOff = Math.floor(diff * 100.0) / 100.0;				
		diff_value = (int)roundOff;

		if(diff_value == 0) {
			// If Result is already fail, then the overall EntryPrice result is fail
			if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
				EntryPriceResult = "FAIL";
			}
			else {
				EntryPriceResult = "PASS";
			}	
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Total Price on Confirmation page is wrong, Expected - " + total + " , Actual - " + conf_total + ",";
		}
		
		// Renewal Plan Validation
		String actualrenewalplanid = "";
		if(!(expectedofferdata.get("Renewal Plan Id").equalsIgnoreCase("No Renewal Plan"))) {
			actualrenewalplanid = comm_obj.getFromVariableMap(driver, "renewalPlanId");				
			if(!(actualrenewalplanid.contains(expectedofferdata.get("Renewal Plan Id")))) {
				RenewalPlanResult = "FAIL";
				remarks = remarks + "Renewal Plan Id does not match, Expected - " + expectedofferdata.get("Renewal Plan Id") + " , Actual - " + actualrenewalplanid + ",";
			}
			else {
				RenewalPlanResult = "PASS";
				
				// To remove null and commas
				actualrenewalplanid = expectedofferdata.get("Renewal Plan Id");
			}
			
//			System.out.println("Expected Renewal Plan Id : " + expectedofferdata.get("Renewal Plan Id"));	
//			System.out.println("Actual Renewal Plan Id : " + actualrenewalplanid);
		}					
		
		// Installment Plan Validation
		String actualinstallmentplanid = "";
		if(!(expectedofferdata.get("Installment Plan Id").equalsIgnoreCase("No Installment Plan"))) {
			actualinstallmentplanid = comm_obj.getFromVariableMap(driver, "paymentPlanId");
			if(expectedofferdata.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {					
				if(!(actualinstallmentplanid.contains(expectedofferdata.get("Installment Plan Id")))) {
					InstallmentPlanResult = "FAIL";
					remarks = remarks + "Installment Plan Id does not match, Expected - " + expectedofferdata.get("Installment Plan Id") + " , Actual - " + actualinstallmentplanid + ",";
				}
				else {
					InstallmentPlanResult = "PASS";
					
					// To remove null and commas
					actualinstallmentplanid = expectedofferdata.get("Installment Plan Id");
				}
				
//				System.out.println("Expected Installment Plan Id : " + expectedofferdata.get("Installment Plan Id"));	
//				System.out.println("Actual Installment Plan Id : " + actualinstallmentplanid);
			}				
			else {
				actualinstallmentplanid = "";
				InstallmentPlanResult = "";
			}
		}				
		
		// Media ID Validation
		String actualmediaid = comm_obj.getFromVariableMap(driver, "mediaId");				
		if(!(expectedofferdata.get("Media ID").contains(actualmediaid))) {
			MediaIdResult = "FAIL";
			remarks = remarks + "Media Id does not match, Expected - " + expectedofferdata.get("Media ID") + " , Actual - " + actualmediaid + ",";
		}
		else {
			MediaIdResult = "PASS";
		}
		
//		System.out.println("Expected Media Id : " + expectedofferdata.get("Media ID"));	
//		System.out.println("Actual Media Id : " + actualmediaid);
		
		// Creative ID Validation
		String actualcreativeid = comm_obj.getFromVariableMap(driver, "creativeId");				
		if(!(expectedofferdata.get("Creative ID").contains(actualcreativeid))) {
			CreativeIdResult = "FAIL";
			remarks = remarks + "Creative Id does not match, Expected - " + expectedofferdata.get("Creative ID") + " , Actual - " + actualcreativeid + ",";
		}
		else {
			CreativeIdResult = "PASS";
		}
		
//		System.out.println("Expected Creative Id : " + expectedofferdata.get("Creative ID"));	
//		System.out.println("Actual Creative Id : " + actualcreativeid);
		
		// Venue ID Validation
		String actualvenueid = comm_obj.getFromVariableMap(driver, "venueId");				
		if(!(expectedofferdata.get("Venue ID").contains(actualvenueid))) {
			VenueIdResult = "FAIL";
			remarks = remarks + "Venue Id does not match, Expected - " + expectedofferdata.get("Venue ID") + " , Actual - " + actualvenueid + ",";
		}
		else {
			VenueIdResult = "PASS";
		}
		
//		System.out.println("Expected Venue Id : " + expectedofferdata.get("Venue ID"));	
//		System.out.println("Actual Venue Id : " + actualvenueid);
		
//		// Price Book Id Validation
//		String actualpricebookid = comm_obj.getFromVariableMap(driver, "pricebookId");				
//		if(!(expectedofferdata.get("Price Book ID").contains(actualpricebookid))) {
//			PriceBookIdResult = "FAIL";
//			remarks = remarks + "Price Book Id does not match, Expected - " + expectedofferdata.get("Price Book ID") + " , Actual - " + actualpricebookid + ",";
//		}
//		else {
//			PriceBookIdResult = "PASS";
//		}
		
		List<String> output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add(category);
		output_row.add(email);
		output_row.add(conf_offercode + " - " + ppidResult);
		output_row.add(conf_num);
		output_row.add(conf_pricing + " - " + EntryPriceResult);
		output_row.add(cartlang_pricing + " - " + ContinuityPriceResult);
		output_row.add(actualrenewalplanid + " - " + RenewalPlanResult);
		output_row.add(actualinstallmentplanid + " - " + InstallmentPlanResult);
		output_row.add(CartLanguageResult);
		output_row.add(SuppCartLanguageResult);
		output_row.add(actualmediaid + " - " + MediaIdResult);
		output_row.add(actualcreativeid + " - " + CreativeIdResult);
		output_row.add(actualvenueid + " - " + VenueIdResult);
//		output_row.add(actualpricebookid + " - " + PriceBookIdResult);
		output_row.add(shipbill);	
		output_row.add(cc);	
		output_row.add(browser);	
		output_row.add(remarks);
		output.add(output_row);
		
		driver.close();
//		
	if(!(pixelStr.equalsIgnoreCase("-"))) {
		HashMap<Integer, HashMap> overallOutput = pixel_obj.validatePixels(pixelStr, pattern, brand, campaign, env, campaignpages);
		attachmentList = pixel_obj.writePixelOutput(overallOutput, brand, campaign, attachmentList);
	}
		}
		
//		offer_array = kitppid.split(",");	
//		
//		for(int i = 0; i < offer_array.length; i++) {	
//			
//			String current_category = category_list.get(i);
//			System.out.println(current_category);
//			
//			if(current_category.equalsIgnoreCase("Kit")) {
//				HashMap<String, String> sourcecodedata = merch_obj.getSourceCodeInfo(merchData, campaign);
//				
//				// Get column in which the PPID is present and check if the PPID belongs to Pre-Purchase Entry Kit
//				int PPIDcolumn = merch_obj.getPPIDColumn(merchData, kitppid);
//				String PPUSection = merch_obj.IsPrePurchase(merchData, kitppid);
//				
//				if(((brand.equalsIgnoreCase("MeaningfulBeauty")) && (campaign.equalsIgnoreCase("os"))) || ((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("advanced-one"))) || ((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fsh2b")))){
//					if(giftppid.equalsIgnoreCase("No")) {
//						PPUSection = "No";
//					}
//					else {
//						PPUSection = "Yes";
//					}
//				}
//				System.out.println(PPIDcolumn + PPUSection);
//				
//				// Intialize result variables
//				String remarks = "";
//				String giftResult = "";
//				String ppidResult = "";
//				String SASPriceResult = "";
//				String EntryPriceResult = "";
//				String ContinuityPriceResult = "";
//				String CartLanguageResult = "";
//				String SuppCartLanguageResult = "";
//				String RenewalPlanResult = "";
//				String InstallmentPlanResult = "";
//				String MediaIdResult = "";
//				String CreativeIdResult = "";
//				String VenueIdResult = "";
//				String PriceBookIdResult = "";
				
//				// Check if the PPID is present in the campaign
//				if(PPIDcolumn == 0) {
//					remarks = remarks + kitppid + " doesn't exist in " + brand + " - " + campaigncategory;
//				}
//				else {	
//					// Read the entire column data
//					HashMap<String, String> offerdata = merch_obj.getColumnData(merchData, PPIDcolumn, PPUSection);
//					
//					// Iterate through the offerdata hashmap and print each key-value pair
//					Iterator itr = offerdata.entrySet().iterator();
//					while (itr.hasNext()) {
//						Map.Entry mapElement = (Map.Entry)itr.next(); 
//			            System.out.println(mapElement.getKey() + " : " + mapElement.getValue()); 
//			        } 
////					System.out.println("End of offerdata");
////					System.out.println("--------------------------------------------------------------------------");
//					
//					// Check Post-purchase Upsell
//					String postpu = merch_obj.checkPostPU(offerdata);
//					if(((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fsh2b"))) && (PPUSection.equalsIgnoreCase("No"))) {
//						postpu = "No";
//					}
//					
//					// Collect current Offer related details from Merchandising Input file
//					HashMap<String, String> expectedofferdata = merch_obj.generateExpectedOfferData(offerdata, sourcecodedata, PPUSection, postpu, kitppid, giftppid, brand, campaigncategory);
//					System.out.println("Expected Offerdata : " + expectedofferdata);
					
					// Collect current campaign related data
					// Pagepattern, Pre-purchase upsell - Yes or No, Post-purchase upsell - Yes or No
//					String pagepattern = comm_obj.getPagePattern(brand,campaigncategory);
//					String pagepattern = expectedofferdata.get("PagePattern");
//					if(pagepattern.equalsIgnoreCase("")) {
//						System.out.println(brand + "-" + campaign + "has no pattern");
//					}
//					else {
						// Check Pre-purchase Upsell
//						String prepu = "";
//						if(pagepattern.contains("prepu")) {
//							prepu = "Yes";
////							System.out.println("Pre Purchase Upsell exists for " + brand + " - " + campaign);
//						}
//						else {
//							prepu = "No";
////							System.out.println("No Pre Purchase Upsell for " + brand + " - " + campaign);
//						}
						
						
//						if(postpu.equalsIgnoreCase("Yes")) {
////							System.out.println("Post Purchase Upsell exists for " + brand + " - " + campaign);
//						}
//						else {
////							System.out.println("No Post Purchase Upsell for " + brand + " - " + campaign);
//						}
						
//						// List pages in this campaign
//						List<String> campaignpages = new ArrayList<String>();
//						campaignpages.add("HomePage");
//						campaignpages.add("SASPage");
//						if(prepu.equalsIgnoreCase("Yes")) {
//							campaignpages.add("PrePurchaseUpsell");
//						}
//						campaignpages.add("CheckoutPage");
//						if(postpu.equalsIgnoreCase("Yes")) {
//							campaignpages.add("PostPurchaseUpsell");
//						}
//						campaignpages.add("ConfirmationPage");
//						System.out.println(campaignpages);				
						
//						System.out.println();
//						System.out.println();
						
						//***********************************************************************
						// Launch Browser
//						BaseTest base_obj = new BaseTest();			
//						WebDriver driver = base_obj.setUp(browser, "Local");
						
//						WebDriverManager.chromedriver().setup();
//						WebDriver driver = new ChromeDriver(capabilities);
//						driver.manage().window().maximize();
//						
//						// enable more detailed HAR capture, if desired (see CaptureType for the complete list)
//					    proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
//						
////						System.out.println(env);
//						String url = db_obj.getUrl(brand, campaign, env);
//						url = pixel_obj.generateURL(url, pixelStr, brand);
////						System.out.println(url);
//						
//						String pattern = pixel_obj.getPattern(url, pixelStr);
//						
//						// HomePage
//						pixel_obj.defineNewHar(proxy, brand + "HomePage");				
//						driver.get(url);
//						driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);	
//						pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_homepage_" + pattern +".har", driver);
//						
//						// Move to SAS
//						pixel_obj.defineNewHar(proxy, brand + "SASPage");	  
//						bf_obj.click_cta(driver, brand, campaign, "Ordernow");
//						pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_saspage_" + pattern +".har", driver);
						
//						// SAS Page Validations
//						// Price Validation							
//						String actualSASPrice = pr_obj.fetchSASPrice(driver, brand, campaign, kitname);
//									
//						System.out.println(kitppid + "Expected entry price: " + expectedEntryPrice);
//						System.out.println(kitppid + "Actual entry price: " + actualSASPrice);
//						
//						if(expectedEntryPrice.equals(actualSASPrice)) {
//							SASPriceResult = "PASS";
//						}
//						else {
//							SASPriceResult = "FAIL";
//							remarks = remarks + "Entry Kit Price on SAS Page is wrong, Expected - " + expectedEntryPrice + " , Actual - " + actualSASPrice;
//						}
						
//						// Gift Validation
//						if(!(expectedofferdata.get("Gift Name").equalsIgnoreCase("No Gift"))) {
//							if(!(expectedofferdata.get("Campaign Gifts").equalsIgnoreCase("-"))) {
//								giftResult = bf_obj.checkGifts(driver, brand, campaigncategory, expectedofferdata.get("Campaign Gifts"));
//								remarks = remarks + giftResult;
//							}
//						}								
//						
//						// Select offer				
//						sas_obj.select_offer(driver, expectedofferdata);
//						
//						// Move to Checkout
////						pixel_obj.defineNewHar(proxy, brand + "CheckoutPage");
//						bf_obj.move_to_checkout(driver, brand, campaigncategory, category);
//						
//						// Checkout Page Validation
//						// Validate Line Items
//						List<String> lineitems = bf_obj.getLineItems(driver);
//						
//						// Validate Added Kit
//						if(lineitems.contains(expectedofferdata.get("30 day PPID"))) {
//							ppidResult = "PASS";
//						}
//						else {
//							ppidResult = "FAIL";
//							remarks = remarks + "Wrong Kit added to cart, Expected - " + expectedofferdata.get("30 day PPID") + " , Actual - " + lineitems.get(0);
//						}
//										
//						// Validate Added Gift
//						if(!(expectedofferdata.get("Gift Name").equalsIgnoreCase("No Gift"))) {
//							if(expectedofferdata.get("GiftSeperateLineItem").equalsIgnoreCase("Yes")) {
//								if(!(expectedofferdata.get("Campaign Gifts").equalsIgnoreCase("-"))) {
//									// When there is no gift choice
//									if(giftppid.equalsIgnoreCase("-")) {
//										giftppid = bf_obj.getPPIDfromString(brand, expectedofferdata.get("Campaign Gifts")).get(0);
//									}
//									
//									// In case of gift combo - multiple gift ppids
//									if(giftppid.contains(",")) {								
//										String[] giftarr = giftppid.split(",");
//										if((lineitems.contains(giftarr[0])) &&  (lineitems.contains(giftarr[1]))){
//											ppidResult = "PASS";
//										}
//										else {
//											ppidResult = "FAIL";
//											remarks = remarks + "Wrong Gift added to cart, Expected - " + giftppid + " , Actual - " + lineitems;
//										}
//									}
//									else {								
//										if(lineitems.contains(giftppid)) {
//											ppidResult = "PASS";
//										}
//										else {
//											ppidResult = "FAIL";
//											remarks = remarks + "Wrong Gift added to cart, Expected - " + giftppid + " , Actual - " + lineitems;
//										}
//									}
//								}
//							}
//						}	
//						
//						// Validate entry kit price
//						String checkoutentrykitprice = pr_obj.getCheckoutEntryKitPrice(driver, brand, campaign);
//						if(expectedofferdata.get("Entry Pricing").contains(checkoutentrykitprice)) {
//							EntryPriceResult = "PASS";
//						}
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "Entry Kit Price on Checkout Page is wrong, Expected - " + expectedofferdata.get("Entry Pricing") + " , Actual - " + checkoutentrykitprice;
//						}				
//						
//						// Fill out form
//						String email = "";
//						
//						// Fall-back scenario
//						// Paypal - Cannot fill-in invalid address-zipcode
//						if(cc.equalsIgnoreCase("Paypal")) {
//							email = bf_obj.fill_out_form(driver, brand, campaigncategory, cc, shipbill, "30");
//							System.out.println("Email : " + email);
//							if(!(email.contains("testbuyer"))) {
//								remarks = remarks + "Paypal Down - Credit card order placed";
//								cc = "Visa";
//							}
//						}
//						else {
//							if(expectedofferdata.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {
//								email = bf_obj.fill_out_form(driver, brand, campaigncategory, "VISA", "same", "90");
//								pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_checkoutpage_" + pattern +".har", driver);
//								
//								pixel_obj.defineNewHar(proxy, brand + "PostPurchaseUpsell");	  				
//								bf_obj.complete_order(driver, brand, "VISA");
//								pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_postpurchaseupsell_" + pattern +".har", driver);
//								
//								bf_obj.upsell_confirmation(driver, brand, campaigncategory, expectedofferdata.get("Offer Post-Purchase"));
//							}
//							else {
//								email = bf_obj.fill_out_form(driver, brand, campaigncategory, cc, shipbill, "30");
//								System.out.println("Email : " + email);
//								pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_checkoutpage_" + pattern +".har", driver);
//							}	
//						}						
//						
//						String cartlang_pricing = "";
//						if(!(expectedofferdata.get("Continuity Pricing").equalsIgnoreCase("No Continuity"))) {
//							// Scenario - 90-day order + Paypal - could not validate 90-day cart language and supplemental language because invalid zipcode could not be fill-in for Paypal
//							if(!((cc.equalsIgnoreCase("Paypal")) && (expectedofferdata.get("Offer Post-Purchase").equalsIgnoreCase("Yes")))) {
//								// Validate Cart Language
//								String full_cart_lang = lang_obj.getfullcartlanguage(driver);
//								
//								//Remove whitespace
//								String expcartlang = expectedofferdata.get("Cart Language").replaceAll("\\s+", "");
//								String actcartlang = full_cart_lang.replaceAll(" ", "");
//								
//								// Remove special characters
//								expcartlang = expcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
//								actcartlang = actcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
////								System.out.println("Actual Edited  : " + actcartlang);
////								System.out.println("Expected Edited: " + expcartlang);
//								
//								if(actcartlang.equalsIgnoreCase(expcartlang)) {
//									CartLanguageResult = "PASS";
//								}
//								else {
////									System.out.println("Actual : " + full_cart_lang);
////									System.out.println("Expected : " + expectedofferdata.get("Cart Language"));
//									CartLanguageResult = "FAIL";
//									remarks = remarks + "Cart Language is wrong, Expected - " + expectedofferdata.get("Cart Language") + " , Actual - " + full_cart_lang;
//								}
//								
//								// Validate Continuity pricing
//								String cart_lang = lang_obj.getfullcartlanguage(driver);						
//								String[] lang_price_arr = lang_obj.parse_cart_language(cart_lang);		
////								String cart_lang_price = "$" + lang_price_arr[1];
////								String cart_lang_shipping = "$" + lang_price_arr[2];	
//								
//								String cart_lang_price = lang_price_arr[1];
//								String cart_lang_shipping = lang_price_arr[2];	
//								cartlang_pricing = cart_lang_price + "," + cart_lang_shipping;
//												
//								if(expectedofferdata.get("Continuity Pricing").equalsIgnoreCase(cart_lang_price)) {
//									ContinuityPriceResult = "PASS";
//								}
//								else {
//									ContinuityPriceResult = "FAIL";
//									remarks = remarks + "Continuity Price is wrong, Expected - " + expectedofferdata.get("Continuity Pricing") + " , Actual - " + cart_lang_price;
//								}
//								
//								if(expectedofferdata.get("Continuity Shipping").equalsIgnoreCase(cart_lang_shipping)) {
//									
//									// If Result is already fail, then the overall Continuity result is fail
//									if(ContinuityPriceResult.equalsIgnoreCase("FAIL")) {
//										ContinuityPriceResult = "FAIL";
//									}
//									else {
//										ContinuityPriceResult = "PASS";
//									}						
//								}
//								else {
//									ContinuityPriceResult = "FAIL";
//									remarks = remarks + "Continuity Shipping is wrong, Expected - " + expectedofferdata.get("Continuity Shipping") + " , Actual - " + cart_lang_shipping;
//								}	
//								
//								// Validate supplemental cart language
//								String supp_cart_lang = lang_obj.getsupplementalcartlanguage(driver);
//												
//								//Remove whitespace
//								String expsuppcartlang = expectedofferdata.get("Supplemental Cart Language").replaceAll("\\s+", "");
//								String actsuppcartlang = supp_cart_lang.replaceAll(" ", "");
//								
//								// Remove special characters
//								expsuppcartlang = expsuppcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
//								actsuppcartlang = actsuppcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
////								System.out.println("Actual Edited  : " + expsuppcartlang);
////								System.out.println("Expected Edited: " + actsuppcartlang);
//												
//								if(actsuppcartlang.equalsIgnoreCase(expsuppcartlang)) {
//									SuppCartLanguageResult = "PASS";
//								}
//								else {
////									System.out.println("Actual : " + supp_cart_lang);
////									System.out.println("Expected : " + expectedofferdata.get("Supplemental Cart Language"));
//									SuppCartLanguageResult = "FAIL";
//									remarks = remarks + "Supplemental Cart Language is wrong, Expected - " + expectedofferdata.get("Supplemental Cart Language") + " , Actual - " + supp_cart_lang;
//								}
//							}							
//						}
//						
//						// Validate Checkout pricing
//						String checkout_subtotal = "";
//						String checkout_shipping = "";
//						String checkout_salestax = "";
//						String checkout_total = "";
//						
//						String realm = DBUtilities.get_realm(brand);
//						
//						if((cc.equalsIgnoreCase("Paypal")) && (realm.equalsIgnoreCase("R2"))) {
//							checkout_subtotal = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Paypal Review Subtotal");
//							checkout_shipping = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Paypal Review Shipping");
//							checkout_salestax = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Paypal Review Salestax");
//							checkout_total = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Paypal Review Total");
//						}
//						else {
//							checkout_subtotal = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Checkout Subtotal");
//							checkout_shipping = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Checkout Shipping");
//							checkout_salestax = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Checkout Salestax");
//							checkout_total = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Checkout Total");
//						}			
////						System.out.println("Checkout Pricing expected : " + expectedofferdata.get("Final Pricing") + "," + expectedofferdata.get("Final Shipping"));
////						System.out.println("Checkout Pricing fetched : " + checkout_subtotal + "," + checkout_shipping + "," + checkout_salestax + "," + checkout_total);
//									
//						if(expectedofferdata.get("Final Pricing").contains(checkout_subtotal)) {
//							EntryPriceResult = "PASS";
//						}
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "Checkout Subtotal does not match with the expected price, Expected - " + expectedofferdata.get("Final Pricing") + " , Actual - " + checkout_subtotal;
//						}
//						
//						if((expectedofferdata.get("Final Shipping").contains("0.0")) || (expectedofferdata.get("Final Shipping").equalsIgnoreCase("FREE")) || (expectedofferdata.get("Final Shipping").equalsIgnoreCase("0"))) {
//							if((checkout_shipping.contains("0.0")) || (checkout_shipping.equalsIgnoreCase("FREE"))) {
//								// If Result is already fail, then the overall EntryPrice result is fail
//								if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
//									EntryPriceResult = "FAIL";
//								}
//								else {
//									EntryPriceResult = "PASS";
//								}	
//							}
//						}				
//						else if(expectedofferdata.get("Final Shipping").equalsIgnoreCase(checkout_shipping)) {
//							// If Result is already fail, then the overall EntryPrice result is fail
//							if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
//								EntryPriceResult = "FAIL";
//							}
//							else {
//								EntryPriceResult = "PASS";
//							}	
//						}
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "Checkout Shipping does not match with the expected shipping price, Expected - " + expectedofferdata.get("Final Shipping") + " , Actual - " + checkout_shipping;
//						}
//						
//						// Checkout SalesTax Validation
//						String salestax = bf_obj.getSalesTax(driver, expectedofferdata.get("Final Pricing"), expectedofferdata.get("Final Shipping"));
//						
//						Double expected_salestax = Double.valueOf(salestax);
//						Double actual_salestax = Double.valueOf(checkout_salestax);
//						Double diff = Math.abs(expected_salestax-actual_salestax);				
//						double roundOff = Math.floor(diff * 100.0) / 100.0;
//						int diff_value = (int)roundOff;
//
//						if(diff_value == 0) {
//							EntryPriceResult = "PASS";
//						}				
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "Checkout Salestax does not match with the expected salestax, Expected - " + salestax + " , Actual - " + checkout_salestax;
//						}
//						
////						System.out.println("Expected SalesTax : " + salestax);
////						System.out.println("Actual SalesTax : " + checkout_salestax);
//						
//						// Checkout Total Validation
//						String total = bf_obj.getTotal(expectedofferdata.get("Final Pricing"), expectedofferdata.get("Final Shipping"), salestax);
//						
//						Double expected_total = Double.valueOf(total);
//						Double actual_total = Double.valueOf(checkout_total);	
//						diff = Math.abs(expected_total-actual_total);				
//						roundOff = Math.floor(diff * 100.0) / 100.0;
//						diff_value = (int)roundOff;
//
//						if(diff_value == 0) {
//							EntryPriceResult = "PASS";
//						}
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "Checkout Total does not match with the expected total, Expected - " + total + " , Actual - " + checkout_total;
//						}
//						
////						System.out.println("Expected Total : " + total);
////						System.out.println("Actual Total : " + checkout_total);
//						
//						
//						JavascriptExecutor jse = (JavascriptExecutor) driver;
//						if(!(cc.equalsIgnoreCase("Paypal"))) {
//							if(expectedofferdata.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {
//								jse.executeScript("window.scrollBy(0,600)", 0);
//								bf_obj.clear_form_field(driver, realm, "Zip");
//								bf_obj.fill_form_field(driver, realm, "Zip", "90245");
//								Thread.sleep(2000);
//								bf_obj.fill_form_field(driver, realm, "CVV", "349");	
//							}
//						}							
//						
//						// No Post Purchase Upsell page
//						if(postpu.equalsIgnoreCase("No")) {
//							pixel_obj.defineNewHar(proxy, brand + "ConfirmationPage");
//				        	// Navigate to Confirmation Page	        
//				        	bf_obj.complete_order(driver, brand, cc);          
//				            pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_confirmationpage_" + pattern + ".har", driver);
//						}
//						// Post Purchase Upsell page is present
//						else {
////							System.out.println("Else block - Post Purchase Upsell page is present");
//							// supplysize - 30
//							// No Fall back scenario
//							if(expectedofferdata.get("SupplySize").equalsIgnoreCase("30")) {
////								System.out.println("Else block - supplysize 30");
//								pixel_obj.defineNewHar(proxy, brand + "PostPurchaseUpsell");	  				
//								bf_obj.complete_order(driver, brand, cc);
//								pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_postpurchaseupsell_" + pattern + ".har", driver);
//							}
//							// supplysize - 90
//							// After Fall back scenario - control navigates to Confirmation page
//							else if(expectedofferdata.get("SupplySize").equalsIgnoreCase("90")) {
////								System.out.println("Else block - else if block - supplysize 90");
//								pixel_obj.defineNewHar(proxy, brand + "ConfirmationPage");	        
//					        	bf_obj.complete_order(driver, brand, cc);          
//					            pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_confirmationpage_" + pattern + ".har", driver);
//							}					
//						}				
//						
//						Thread.sleep(3000);
//						
////						// Upsell page validations
////						driver.findElement(By.xpath("//i[@class='fa fa-plus']")).click();				
//						
//						// No Upsell Confirmation if fall-back has happened previously(90-day - CC Order)
//						// For 30-day order or Paypal order - Select Upsell Confirmation
//						if(postpu.equalsIgnoreCase("Yes")) {
//							if((expectedofferdata.get("SupplySize").equalsIgnoreCase("30")) || (cc.equalsIgnoreCase("Paypal"))) {
//								pixel_obj.defineNewHar(proxy, brand + "ConfirmationPage");
//								bf_obj.upsell_confirmation(driver, brand, campaigncategory, expectedofferdata.get("Offer Post-Purchase"));
//								pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_confirmationpage_" + pattern + ".har", driver);
//							}
//						}								
//						
//						// Confirmation page validations
//						String conf_offercode = bf_obj.fetch_confoffercode(driver, brand);
//						System.out.println("Confirmation PPIDs : " + conf_offercode);
//						if(conf_offercode.contains(kitppid)){
//							// If Result is already fail, then the overall ppid result is fail
//							if(ppidResult.equalsIgnoreCase("FAIL")) {
//								ppidResult = "FAIL";
//							}
//							else {
//								ppidResult = "PASS";
//							}	
//						}
//						else {
//							ppidResult = "FAIL";
//							remarks = remarks + "Confirmation page - Wrong Kit added, Expected - " + kitppid + " , Actual - " + conf_offercode;
//						}
//						
//						if(!(expectedofferdata.get("Gift Name").equalsIgnoreCase("No Gift"))) {
//							if(expectedofferdata.get("GiftSeperateLineItem").equalsIgnoreCase("Yes")) {
//								if(!(expectedofferdata.get("Campaign Gifts").equalsIgnoreCase("-"))) {
//									if(conf_offercode.contains(giftppid)){
//										// If Result is already fail, then the overall ppid result is fail
//										if(ppidResult.equalsIgnoreCase("FAIL")) {
//											ppidResult = "FAIL";
//										}
//										else {
//											ppidResult = "PASS";
//										}	
//									}
//									else {
//										ppidResult = "FAIL";
//										remarks = remarks + "Confirmation page - Wrong Gift added, Expected - " + giftppid + " , Actual - " + conf_offercode;
//									}
//								}	
//							}
//						}		
//						
//						// Validate PrepU Product
//						if(!(expectedofferdata.get("PrePU Product").equalsIgnoreCase("No PrePU Product"))) {
//							String expectedprepuppid = bf_obj.getPPIDfromString(brand, expectedofferdata.get("PrePU Product")).get(0);
//							if(conf_offercode.contains(expectedprepuppid)){
//								// If Result is already fail, then the overall ppid result is fail
//								if(ppidResult.equalsIgnoreCase("FAIL")) {
//									ppidResult = "FAIL";
//								}
//								else {
//									ppidResult = "PASS";
//								}	
//							}
//							else {
//								ppidResult = "FAIL";
//								remarks = remarks + "Confirmation page - PrePU Lineitem missing, Expected - " + expectedprepuppid + " , Actual - " + conf_offercode;
//							}
//						}
//						
//						// Validate PostpU Product
//						if(!(expectedofferdata.get("PostPU Product").equalsIgnoreCase("No PostPU Product"))) {
//							String expectedpostpuppid = bf_obj.getPPIDfromString(brand, expectedofferdata.get("PostPU Product")).get(0);
//							if(conf_offercode.contains(expectedpostpuppid)){
//								// If Result is already fail, then the overall ppid result is fail
//								if(ppidResult.equalsIgnoreCase("FAIL")) {
//									ppidResult = "FAIL";
//								}
//								else {
//									ppidResult = "PASS";
//								}	
//							}
//							else {
//								ppidResult = "FAIL";
//								remarks = remarks + "Confirmation page - PostPU Lineitem missing, Expected - " + expectedpostpuppid + " , Actual - " + conf_offercode;
//							}
//						}
//						
//						Screenshot confpage = new AShot().takeScreenshot(driver);
//						ImageIO.write(confpage.getImage(),"PNG",new File(System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Screenshots\\" + brand + "\\" + campaign + "_" + kitppid +".png"));
//						
//						String conf_num = bf_obj.fetch_conf_num(driver, brand);
//						System.out.println("Confirmation Number : " + conf_num);				
//										
//						// Confirmation Price Validation
//						String conf_subtotal = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Confirmation Subtotal");
//						String conf_shipping = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Confirmation Shipping");
//						String conf_salestax = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Confirmation Salestax");
//						String conf_total = pr_obj.fetch_pricing (driver, brand, campaigncategory, "Confirmation Total");
//						
//						String conf_pricing = conf_subtotal + " ; " + conf_shipping + " ; " + conf_salestax + " ; " + conf_total;	
////						System.out.println("Confirmation Pricing fetched : " + conf_pricing);
//						
//						// Subtotal validation
//						if(expectedofferdata.get("Final Pricing").contains(conf_subtotal)) {
//							// If Result is already fail, then the overall EntryPrice result is fail
//							if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
//								EntryPriceResult = "FAIL";
//							}
//							else {
//								EntryPriceResult = "PASS";
//							}	
//						}
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "Confirmation Subtotal is wrong, Expected - " + expectedofferdata.get("Final Pricing") + " , Actual - " + conf_subtotal + ",";
//						}
//						
//						// Shipping validation
//						if((expectedofferdata.get("Final Shipping").contains("0.0")) || (expectedofferdata.get("Final Shipping").equalsIgnoreCase("FREE")) || (expectedofferdata.get("Final Shipping").equalsIgnoreCase("0"))) {
//							if((conf_shipping.contains("0.0")) || (conf_shipping.equalsIgnoreCase("FREE"))) {
//								// If Result is already fail, then the overall EntryPrice result is fail
//								if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
//									EntryPriceResult = "FAIL";
//								}
//								else {
//									EntryPriceResult = "PASS";
//								}	
//							}
//						}				
//						else if(expectedofferdata.get("Final Shipping").equalsIgnoreCase(conf_shipping)) {
//							// If Result is already fail, then the overall EntryPrice result is fail
//							if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
//								EntryPriceResult = "FAIL";
//							}
//							else {
//								EntryPriceResult = "PASS";
//							}	
//						}
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "Shipping price on confirmation page is wrong, Expected - " + expectedofferdata.get("Final Shipping") + " , Actual - " + checkout_shipping;
//						}
//								
//						// Sales Tax Validation
//						expected_salestax = Double.valueOf(salestax);
//						actual_salestax = Double.valueOf(conf_salestax);				
//						diff = Math.abs(expected_salestax-actual_salestax);				
//						roundOff = Math.floor(diff * 100.0) / 100.0;
//						diff_value = (int)roundOff;
//
//						if(diff_value == 0) {
//							// If Result is already fail, then the overall EntryPrice result is fail
//							if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
//								EntryPriceResult = "FAIL";
//							}
//							else {
//								EntryPriceResult = "PASS";
//							}	
//						}
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "SalesTax on Confirmation page does not match with that of checkout page, Expected - " + salestax + " , Actual - " + conf_salestax + ",";
//						}
//						
//						// Total Price Validation
//						expected_total = Double.valueOf(total);
//						actual_total = Double.valueOf(conf_total);
//						diff = Math.abs(expected_total-actual_total);						
//						roundOff = Math.floor(diff * 100.0) / 100.0;				
//						diff_value = (int)roundOff;
//
//						if(diff_value == 0) {
//							// If Result is already fail, then the overall EntryPrice result is fail
//							if(EntryPriceResult.equalsIgnoreCase("FAIL")) {
//								EntryPriceResult = "FAIL";
//							}
//							else {
//								EntryPriceResult = "PASS";
//							}	
//						}
//						else {
//							EntryPriceResult = "FAIL";
//							remarks = remarks + "Total Price on Confirmation page is wrong, Expected - " + total + " , Actual - " + conf_total + ",";
//						}
//						
//						// Renewal Plan Validation
//						String actualrenewalplanid = "";
//						if(!(expectedofferdata.get("Renewal Plan Id").equalsIgnoreCase("No Renewal Plan"))) {
//							actualrenewalplanid = comm_obj.getFromVariableMap(driver, "renewalPlanId");				
//							if(!(actualrenewalplanid.contains(expectedofferdata.get("Renewal Plan Id")))) {
//								RenewalPlanResult = "FAIL";
//								remarks = remarks + "Renewal Plan Id does not match, Expected - " + expectedofferdata.get("Renewal Plan Id") + " , Actual - " + actualrenewalplanid + ",";
//							}
//							else {
//								RenewalPlanResult = "PASS";
//								
//								// To remove null and commas
//								actualrenewalplanid = expectedofferdata.get("Renewal Plan Id");
//							}
//							
////							System.out.println("Expected Renewal Plan Id : " + expectedofferdata.get("Renewal Plan Id"));	
////							System.out.println("Actual Renewal Plan Id : " + actualrenewalplanid);
//						}					
//						
//						// Installment Plan Validation
//						String actualinstallmentplanid = "";
//						if(!(expectedofferdata.get("Installment Plan Id").equalsIgnoreCase("No Installment Plan"))) {
//							actualinstallmentplanid = comm_obj.getFromVariableMap(driver, "paymentPlanId");
//							if(expectedofferdata.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {					
//								if(!(actualinstallmentplanid.contains(expectedofferdata.get("Installment Plan Id")))) {
//									InstallmentPlanResult = "FAIL";
//									remarks = remarks + "Installment Plan Id does not match, Expected - " + expectedofferdata.get("Installment Plan Id") + " , Actual - " + actualinstallmentplanid + ",";
//								}
//								else {
//									InstallmentPlanResult = "PASS";
//									
//									// To remove null and commas
//									actualinstallmentplanid = expectedofferdata.get("Installment Plan Id");
//								}
//								
////								System.out.println("Expected Installment Plan Id : " + expectedofferdata.get("Installment Plan Id"));	
////								System.out.println("Actual Installment Plan Id : " + actualinstallmentplanid);
//							}				
//							else {
//								actualinstallmentplanid = "";
//								InstallmentPlanResult = "";
//							}
//						}				
//						
//						// Media ID Validation
//						String actualmediaid = comm_obj.getFromVariableMap(driver, "mediaId");				
//						if(!(expectedofferdata.get("Media ID").contains(actualmediaid))) {
//							MediaIdResult = "FAIL";
//							remarks = remarks + "Media Id does not match, Expected - " + expectedofferdata.get("Media ID") + " , Actual - " + actualmediaid + ",";
//						}
//						else {
//							MediaIdResult = "PASS";
//						}
//						
////						System.out.println("Expected Media Id : " + expectedofferdata.get("Media ID"));	
////						System.out.println("Actual Media Id : " + actualmediaid);
//						
//						// Creative ID Validation
//						String actualcreativeid = comm_obj.getFromVariableMap(driver, "creativeId");				
//						if(!(expectedofferdata.get("Creative ID").contains(actualcreativeid))) {
//							CreativeIdResult = "FAIL";
//							remarks = remarks + "Creative Id does not match, Expected - " + expectedofferdata.get("Creative ID") + " , Actual - " + actualcreativeid + ",";
//						}
//						else {
//							CreativeIdResult = "PASS";
//						}
//						
////						System.out.println("Expected Creative Id : " + expectedofferdata.get("Creative ID"));	
////						System.out.println("Actual Creative Id : " + actualcreativeid);
//						
//						// Venue ID Validation
//						String actualvenueid = comm_obj.getFromVariableMap(driver, "venueId");				
//						if(!(expectedofferdata.get("Venue ID").contains(actualvenueid))) {
//							VenueIdResult = "FAIL";
//							remarks = remarks + "Venue Id does not match, Expected - " + expectedofferdata.get("Venue ID") + " , Actual - " + actualvenueid + ",";
//						}
//						else {
//							VenueIdResult = "PASS";
//						}
//						
////						System.out.println("Expected Venue Id : " + expectedofferdata.get("Venue ID"));	
////						System.out.println("Actual Venue Id : " + actualvenueid);
//						
////						// Price Book Id Validation
////						String actualpricebookid = comm_obj.getFromVariableMap(driver, "pricebookId");				
////						if(!(expectedofferdata.get("Price Book ID").contains(actualpricebookid))) {
////							PriceBookIdResult = "FAIL";
////							remarks = remarks + "Price Book Id does not match, Expected - " + expectedofferdata.get("Price Book ID") + " , Actual - " + actualpricebookid + ",";
////						}
////						else {
////							PriceBookIdResult = "PASS";
////						}
//						
//						List<String> output_row = new ArrayList<String>();
//						output_row.add(env);
//						output_row.add(brand);
//						output_row.add(campaign);
//						output_row.add(category);
//						output_row.add(email);
//						output_row.add(conf_offercode + " - " + ppidResult);
//						output_row.add(conf_num);
//						output_row.add(conf_pricing + " - " + EntryPriceResult);
//						output_row.add(cartlang_pricing + " - " + ContinuityPriceResult);
//						output_row.add(actualrenewalplanid + " - " + RenewalPlanResult);
//						output_row.add(actualinstallmentplanid + " - " + InstallmentPlanResult);
//						output_row.add(CartLanguageResult);
//						output_row.add(SuppCartLanguageResult);
//						output_row.add(actualmediaid + " - " + MediaIdResult);
//						output_row.add(actualcreativeid + " - " + CreativeIdResult);
//						output_row.add(actualvenueid + " - " + VenueIdResult);
////						output_row.add(actualpricebookid + " - " + PriceBookIdResult);
//						output_row.add(shipbill);	
//						output_row.add(cc);	
//						output_row.add(browser);	
//						output_row.add(remarks);
//						output.add(output_row);
//						
//						driver.close();
////						
//					if(!(pixelStr.equalsIgnoreCase("-"))) {
//						HashMap<Integer, HashMap> overallOutput = pixel_obj.validatePixels(pixelStr, pattern, brand, campaign, env, campaignpages);
//						attachmentList = pixel_obj.writePixelOutput(overallOutput, brand, campaign, attachmentList);
//					}
//				}
//			}
//			else {
//				
//			}
//		}		
	}
	
	@AfterSuite
	public void populateExcel() throws IOException {
		String file = comm_obj.populateOutputExcel(output, "BuyflowResults", System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Run Output\\");
		
//		List<String> attachmentList = new ArrayList<String>();
		attachmentList.add(file);
		
		mailObj.sendEmail("Buyflow Results", sendReportTo, attachmentList);
	}	
}

