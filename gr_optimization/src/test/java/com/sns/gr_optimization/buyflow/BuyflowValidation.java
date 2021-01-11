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

//import io.github.bonigarcia.wdm.WebDriverManager;
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
		
		// Create Required Directories
		File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation", "Harfiles");
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles", brand);
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation", "Pixel Output");
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation", "Run Output");
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation", "Screenshots");
		newDirectory.mkdir();
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Screenshots", brand);
		newDirectory.mkdir();
		
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
//		System.out.println(category_list);
		
		// Read Web Catalog
		if((category_list.contains("Product")) || (category_list.contains("SubscribeandSave")) || (category_list.contains("ShopKit"))) {
			catalogData = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/BuyflowValidation/Merchandising Input/" + brand + "/Web Catalog.xlsx", "Acq", 0);
		}
		
		// Read Merchandising Input
//		if(category_list.contains("Kit")) {
//		if(!(brand.equalsIgnoreCase("JLoBeauty"))) {
			merchData = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/BuyflowValidation/Merchandising Input/" + brand + "/" + campaigncategory + ".xlsx", "Active Campaign", 0);
//		}
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
//		System.out.println(offerlist);
//		System.out.println(categorylist);
		
		// Intialize result variables
		String remarks = "";
		String giftResult = "";
		String ppidResult = "PASS";
		String SASPriceResult = "";
		String EntryPriceResult = "";
		String ContinuityPriceResult = "";
		String CartLanguageResult = "";
		String SuppCartLanguageResult = "";
		String RenewalPlanResult = "PASS";
		String InstallmentPlanResult = "";
		String MediaIdResult = "";
		String CreativeIdResult = "";
		String VenueIdResult = "";
		String PriceBookIdResult = "PASS";
		
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
		System.out.println(url);
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);	
		pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_homepage_" + pattern +".har", driver);
						
		HashMap<String, String> sourcecodedata = null;
		HashMap<String, String> expectedsourcecodedata = null;

		// Read Source code details from Merchandising template for the campaign
		sourcecodedata = merch_obj.getSourceCodeInfo(merchData, campaign);	
		// Collect Source code details for the campaign
		expectedsourcecodedata = merch_obj.generateExpectedSourceCodeData(sourcecodedata);
//		System.out.println(expectedsourcecodedata);		
		
		// HashMap variable to collect Kit related details from Merchandising Template
		HashMap<String, String> expectedofferdata_kit = null;
		HashMap<String, String> expectedofferdata_product = null;
		
		List<String> subtotal_list = new ArrayList<String>();
		List<String> subtotal_list_forshippingcalc = new ArrayList<String>();
		List<String> shipping_list = new ArrayList<String>();
		List<String> renewal_plan_list = new ArrayList<String>();
		List<String> pricebook_id_list = new ArrayList<String>();
		List<List<String>> expected_lineitems =  new ArrayList<List<String>>();
		
		List<String> campaignpages = new ArrayList<String>();
		
		String postpu = "No";
		String prepu = "No";
		
		while(offerIterator.hasNext() && categoryIterator.hasNext()) {
			String ppid = offerIterator.next();			
			String currentCategory = categoryIterator.next();
			
			if(currentCategory.equalsIgnoreCase("Kit")) {
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
//				System.out.println(PPIDcolumn + PPUSection);
				
				// Check if the PPID is present in the campaign
				if(PPIDcolumn == 0) {
					remarks = remarks + ppid + " doesn't exist in " + brand + " - " + campaigncategory;
					continue;
				}
	
				// Read the entire column data
				HashMap<String, String> kit_offerdata = merch_obj.getColumnData(merchData, PPIDcolumn, PPUSection);
//				System.out.println(kit_offerdata);
				// Check Post-purchase Upsell for the campaign
				postpu = merch_obj.checkPostPU(kit_offerdata, brand);
				if(((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fsh2b"))) && (PPUSection.equalsIgnoreCase("No"))) {
					postpu = "No";
				}
				
				// PagePattern for the Current PPID
				String pagepattern = kit_offerdata.get("PagePattern").trim();
				
				// Check Pre-purchase Upsell for the campaign
				if(pagepattern.contains("prepu")) {
					prepu = "Yes";
				}
				else {
					prepu = "No";
				}					
				
				// Collect current Offer related details from Merchandising Input file
				if(!(brand.equalsIgnoreCase("JloBeauty"))) {
					expectedofferdata_kit = merch_obj.generateExpectedOfferDataForKit(kit_offerdata, PPUSection, postpu, ppid, giftppid, brand, campaigncategory);
//					System.out.println("Expected Offerdata - Kit : " + expectedofferdata_kit);
				}
								
				// Add Kit PPID to lineitem list
				List<String> kit_lineitem = new ArrayList<String>();
				kit_lineitem.add("Kit");
				kit_lineitem.add(expectedofferdata_kit.get("Kit PPID"));			
				kit_lineitem.add(expectedofferdata_kit.get("Entry Pricing"));
				kit_lineitem.add(expectedofferdata_kit.get("Cart Language"));
				kit_lineitem.add(expectedofferdata_kit.get("Continuity Pricing"));
				kit_lineitem.add(expectedofferdata_kit.get("Continuity Shipping"));
				kit_lineitem.add(expectedofferdata_kit.get("Supplemental Cart Language"));
				expected_lineitems.add(kit_lineitem);		
				
				// Add Gift PPIDs to lineitem list				
				String[] giftppidarr = expectedofferdata_kit.get("Gift PPID").split(",");
				if(!(giftppidarr[0].contains("No Gift"))) {
					for (String gift : giftppidarr) {
						List<String> gift_lineitem = new ArrayList<String>();
						gift_lineitem.add("Gift");
						gift_lineitem.add(gift);
						gift_lineitem.add("FREE");
						gift_lineitem.add("No Cart Language");
						gift_lineitem.add("No Continuity Pricing");
						gift_lineitem.add("No Continuity Shipping");
						gift_lineitem.add("No Supplemental Cart Language");
						expected_lineitems.add(gift_lineitem);
					}
				}		
				
				// Add PrePU Product to lineitem list
				if(expectedofferdata_kit.get("Offer Pre-Purchase").equalsIgnoreCase("Yes")) {
					if(!(expectedofferdata_kit.get("PrePU Product").equalsIgnoreCase("No PrePU Product"))) {
						String prepu_ppid = String.join(",", bf_obj.getPPIDfromString(brand, expectedofferdata_kit.get("PrePU Product")));
						String[] prepuppidarr = prepu_ppid.split(",");	
						for (String prepuprod : prepuppidarr) {
							List<String> prepu_lineitem = new ArrayList<String>();
							prepu_lineitem.add("PrePU");
							prepu_lineitem.add(prepuprod);
							prepu_lineitem.add("-");
							prepu_lineitem.add("No Cart Language");
							prepu_lineitem.add("No Continuity Pricing");
							prepu_lineitem.add("No Continuity Shipping");
							prepu_lineitem.add("No Supplemental Cart Language");
							expected_lineitems.add(prepu_lineitem);
						}
					}					
				}
				
				// Add PostPU Product to lineitem list
				if(expectedofferdata_kit.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {
					if(!(expectedofferdata_kit.get("PostPU Product").equalsIgnoreCase("No PostPU Product"))) {
						String postpu_ppid = String.join(",", bf_obj.getPPIDfromString(brand, expectedofferdata_kit.get("PostPU Product")));
						String[] postpuppidarr = postpu_ppid.split(",");	
						for (String postpuprod : postpuppidarr) {
							List<String> postpu_lineitem = new ArrayList<String>();
							postpu_lineitem.add("PostPU");
							postpu_lineitem.add(postpuprod);
							postpu_lineitem.add("-");
							postpu_lineitem.add("No Cart Language");
							postpu_lineitem.add("No Continuity Pricing");
							postpu_lineitem.add("No Continuity Shipping");
							postpu_lineitem.add("No Supplemental Cart Language");
							expected_lineitems.add(postpu_lineitem);
						}
					}					
				}
				
				subtotal_list.add(expectedofferdata_kit.get("Final Pricing"));
				shipping_list.add(expectedofferdata_kit.get("Final Shipping"));
				renewal_plan_list.add(expectedofferdata_kit.get("Renewal Plan Id"));
				if(expectedsourcecodedata.get("Price Book ID") == null) {
					pricebook_id_list.add("No expected PriceBookID");
				}
				else {
					pricebook_id_list.add(expectedsourcecodedata.get("Price Book ID"));
				}				
				
				// Move to SAS
				pixel_obj.defineNewHar(proxy, brand + "SASPage");	  
				bf_obj.click_cta(driver, brand, campaign, "Ordernow");
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_saspage_" + pattern +".har", driver);
			
				// Gift Validation
//				if(!(expectedofferdata_kit.get("Gift Name").equalsIgnoreCase("No Gift"))) {
//					if(!(expectedofferdata_kit.get("Campaign Gifts").equalsIgnoreCase("-"))) {
						String expectedcampaigngifts = expectedofferdata_kit.get("Campaign Gifts");
//					}
					if((expectedcampaigngifts != null) && (!(expectedcampaigngifts.equals("-"))) && (!(expectedcampaigngifts.equals("")))) {
						giftResult = bf_obj.checkGifts(driver, brand, campaigncategory, expectedcampaigngifts);
						remarks = remarks + giftResult;
					}
//				}								
				
				// Select offer				
				sas_obj.select_offer(driver, expectedofferdata_kit, currentCategory);
				
				// Move to Checkout
				pixel_obj.defineNewHar(proxy, brand + "CheckoutPage");
				bf_obj.move_to_checkout(driver, brand, campaigncategory, category);
			}
			else if((currentCategory.equalsIgnoreCase("Product")) || (currentCategory.equalsIgnoreCase("SubscribeandSave")) || (currentCategory.equalsIgnoreCase("ShopKit"))) {
				// Get the product data from Web Catalog
				HashMap<String, String> product_offerdata = merch_obj.getProdRowfromCatalog(catalogData, ppid);
//				System.out.println(product_offerdata);
				
				// Check if the PPID is present in the campaign
				if(product_offerdata.size() == 0) {
					remarks = remarks + ppid + " doesn't exist in the Shop page of " + brand + " - " + campaigncategory;
					continue;
				}
				
				// Collect current Offer related details from Merchandising Input file
				expectedofferdata_product = merch_obj.generateExpectedOfferDataForProduct(product_offerdata, ppid, brand, campaigncategory, currentCategory);
//				System.out.println("Expected Offerdata - Product : " + expectedofferdata_product);
				
				// Add Product PPID to lineitem list
				List<String> product_lineitem = new ArrayList<String>();
				product_lineitem.add(category);				
				product_lineitem.add(expectedofferdata_product.get("Product PPID"));				
				product_lineitem.add(expectedofferdata_product.get("Price"));
				product_lineitem.add(expectedofferdata_product.get("Cart Language"));
				product_lineitem.add(expectedofferdata_product.get("Continuity Pricing"));
				product_lineitem.add(expectedofferdata_product.get("Continuity Shipping"));
				product_lineitem.add(expectedofferdata_product.get("Supplemental Cart Language"));
				expected_lineitems.add(product_lineitem);
				
				subtotal_list.add(expectedofferdata_product.get("Price"));
				
				if(currentCategory.equalsIgnoreCase("Product")) {
					subtotal_list_forshippingcalc.add(expectedofferdata_product.get("Price"));
				}				
				
				String shipping_calc = "";
				if((category_list.contains("Kit")) || (currentCategory.equalsIgnoreCase("SubscribeandSave"))){
					shipping_calc = "FREE";
				}
				else {
					String subtotal_str = bf_obj.CalculateTotalPrice(subtotal_list_forshippingcalc);
					double subtotal_calc = Double.parseDouble(subtotal_str);  
					if(subtotal_calc > 49) {
						shipping_calc = "FREE";
					}
					else {
						if(brand.equalsIgnoreCase("JLoBeauty")) {
							shipping_calc = "$4.99";
						}
						else {
							shipping_calc = "$5.99";
						}						
					}  
				}
				
				shipping_list.add(shipping_calc);
				if(currentCategory.equalsIgnoreCase("SubscribeandSave")) {
					renewal_plan_list.add(expectedofferdata_product.get("Renewal Plan Id"));
				}						
				
				pricebook_id_list.add(expectedofferdata_product.get("Price Book Id"));
				
				// Move to Shop
				pixel_obj.defineNewHar(proxy, brand + "ShopPage");	  
				bf_obj.click_cta(driver, brand, campaign, "Shop");
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_shoppage_" + pattern +".har", driver);
			
				// Select offer			
				pixel_obj.defineNewHar(proxy, brand + "PDPage");	
				sas_obj.select_offer(driver, expectedofferdata_product, currentCategory);
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_pdpage_" + pattern +".har", driver);
				
				// Move to Checkout
				pixel_obj.defineNewHar(proxy, brand + "CheckoutPage");
				bf_obj.move_to_checkout(driver, brand, campaigncategory, currentCategory);
			}
						
			if(offerIterator.hasNext() && categoryIterator.hasNext()) {
				bf_obj.click_logo(driver, brand, campaigncategory);
			}		
		}		
		
		// List pages in this campaign
		campaignpages = new ArrayList<String>();
		campaignpages.add("HomePage");
		
		if(category_list.contains("Kit")) {
			campaignpages.add("SASPage");
			if(prepu.equalsIgnoreCase("Yes")) {
				campaignpages.add("PrePurchaseUpsell");
			}
		}
		
		if((category_list.contains("Product")) || (category_list.contains("SubscribeandSave"))) {
			campaignpages.add("ShopPage");
			campaignpages.add("PDPage");
		}
		
		campaignpages.add("CheckoutPage");
		
		if(category_list.contains("Kit")) {
			if(postpu.equalsIgnoreCase("Yes")) {
				campaignpages.add("PostPurchaseUpsell");
			}
		}
		
		campaignpages.add("ConfirmationPage");
//		System.out.println(campaignpages);
		
		// Fill out form
		String email = "";
				
		// Fall-back scenario
		// Paypal - Cannot fill-in invalid address-zipcode
		if(cc.equalsIgnoreCase("Paypal")) {
			email = bf_obj.fill_out_form(driver, brand, campaigncategory, cc, shipbill, "30");
			System.out.println("Email : " + email);
			if(!(email.contains("testbuyer"))) {
				remarks = remarks + "Paypal Down - Credit card order placed ; ";
				cc = "Visa";
			}
		}
		else {
			if((categorylist.contains("Kit")) && (expectedofferdata_kit.get("Offer Post-Purchase").equalsIgnoreCase("Yes"))) {
				email = bf_obj.fill_out_form(driver, brand, campaigncategory, "VISA", "same", "90");
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_checkoutpage_" + pattern +".har", driver);
						
				pixel_obj.defineNewHar(proxy, brand + "PostPurchaseUpsell");	  				
				bf_obj.complete_order(driver, brand, "VISA");
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_postpurchaseupsell_" + pattern +".har", driver);
						
				bf_obj.upsell_confirmation(driver, brand, campaigncategory, expectedofferdata_kit.get("Offer Post-Purchase"));
			}
			else {
				email = bf_obj.fill_out_form(driver, brand, campaigncategory, cc, shipbill, "30");
				System.out.println("Email : " + email);
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_checkoutpage_" + pattern +".har", driver);
			}	
		}	
		
		// Checkout Page Validation
		// Validate Line Items
		List<List<String>> actual_lineitems = new ArrayList<List<String>>();
		
		String offer_postpurchase  = "";
		String supplysize  = "";
		if(category_list.contains("Kit")) {
			offer_postpurchase = expectedofferdata_kit.get("Offer Post-Purchase");
			supplysize = expectedofferdata_kit.get("SupplySize");
		}
		// Product and Subscribe and Save - has no Post Purchase upsell
		else {
			offer_postpurchase = "No";
			supplysize = "30";
		}		
		
		actual_lineitems = bf_obj.getLineItems(driver, cc, offer_postpurchase, brand);		
		
		List<List<String>> temp_lineitemlist = new ArrayList<>();
		List<List<String>> diff_lineitemlist = new ArrayList<>();
		
		// Scenario - 90-day order + Paypal - could not validate 90-day cart language and supplemental language because invalid zipcode could not be fill-in for Paypal
//		if((!(cc.equalsIgnoreCase("Paypal"))) && (!(supplysize.equalsIgnoreCase("90"))) && (!(offer_postpurchase.equalsIgnoreCase("Yes")))) {
		if(!((cc.equalsIgnoreCase("Paypal")) && (supplysize.equalsIgnoreCase("90")) && (offer_postpurchase.equalsIgnoreCase("Yes")))){
			
//		}
			// When some more lineitems are expected
			temp_lineitemlist = new ArrayList<>();
			diff_lineitemlist = new ArrayList<>(expected_lineitems);
						
			for(List<String> actual_item : actual_lineitems) {
//				System.out.println("Actual item: " + actual_item);
				String actual_ppid = actual_item.get(0);
				String actual_price = actual_item.get(1);
				String actual_cart_language = actual_item.get(2);
				String actual_continuity_price = actual_item.get(3);
				String actual_continuity_shipping = actual_item.get(4);
					
				for(List<String> expected_item : expected_lineitems) {
//					System.out.println("Expected item: " + expected_item);
//					System.out.println("Actual PPID: " + actual_ppid);					
					
					if(expected_item.contains(actual_ppid)) {
						if(!(expected_item.get(2).equalsIgnoreCase("-"))) {
							actual_price = actual_price.replace("$", "");
							String price_result = bf_obj.assertPrice(actual_price, expected_item.get(2));
							if(price_result.equalsIgnoreCase("PASS")) {
								EntryPriceResult = "PASS";
							}
							else {
								EntryPriceResult = "FAIL";
								remarks = remarks + "Checkout Cart - " + expected_item.get(0) + " - " + expected_item.get(1) + " Price Mismatch. Expected - " + expected_item.get(2) + " , Actual - " + actual_price + " ; ";
							}
						}					
							
						if(!(expected_item.get(3).equalsIgnoreCase("No Cart Language"))) {
							//Remove whitespace
							String expcartlang = expected_item.get(3).replaceAll("\\s+", "");
							String actcartlang = actual_cart_language.replaceAll(" ", "");
									
							// Remove special characters
							expcartlang = expcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
							actcartlang = actcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
							if(expcartlang.equalsIgnoreCase(actcartlang)) {
								CartLanguageResult = "PASS";
							}
							else {
								CartLanguageResult = "FAIL";
								remarks = remarks + "Checkout Cart - " + expected_item.get(0) + " - " + expected_item.get(1) + " Cart Language Mismatch. Expected - " + expected_item.get(3) + " , Actual - " + actual_cart_language + " ; ";
							}
									
							// Continuity Pricing
							if(expected_item.get(4).equalsIgnoreCase(actual_continuity_price)) {
								ContinuityPriceResult = "PASS";
							}
							else {
								ContinuityPriceResult = "FAIL";
								remarks = remarks + "Checkout Cart - " + expected_item.get(0) + " - " + expected_item.get(1) + " Continuity Pricing Mismatch. Expected - " + expected_item.get(4) + " , Actual - " + actual_continuity_price + " ; ";
							}
									
							// Continuity Shipping
							if(expected_item.get(5).equalsIgnoreCase(actual_continuity_shipping)) {
								ContinuityPriceResult = "PASS";
							}
							else {
								ContinuityPriceResult = "FAIL";
								remarks = remarks + "Checkout Cart - " + expected_item.get(0) + " - " + expected_item.get(1) + " Continuity Shipping Mismatch. Expected - " + expected_item.get(5) + " , Actual - " + actual_continuity_shipping + " ; ";
							}						
						}					
						temp_lineitemlist.add(expected_item);
					}					
				}				
			}			
			diff_lineitemlist.removeAll(temp_lineitemlist);
			for(List<String> lineitem : diff_lineitemlist) {
				if((cc.equalsIgnoreCase("Paypal")) && (lineitem.get(0).equalsIgnoreCase("PostPU"))){
					continue;
				}
				ppidResult = "FAIL";
				remarks = remarks + "Missing " + lineitem.get(0) + " - " + lineitem.get(1) + " in Checkout Cart ; ";
			}
			
			// When Extra lineitems are present
//			System.out.println("Extra lineitems check");
			temp_lineitemlist.clear();
			temp_lineitemlist = new ArrayList<>();
			diff_lineitemlist = new ArrayList<>(actual_lineitems);
			
			for(List<String> expected_item : expected_lineitems) {
				String expected_ppid = expected_item.get(1);
					
				for(List<String> actual_item : actual_lineitems) {
					if(actual_item.contains(expected_ppid)) {
						temp_lineitemlist.add(actual_item);					
					}				
				}				
			}			
//			System.out.println(temp_lineitemlist);
//			System.out.println(diff_lineitemlist);
			diff_lineitemlist.removeAll(temp_lineitemlist);
//			System.out.println(diff_lineitemlist);
			for(List<String> lineitem : diff_lineitemlist) {
				ppidResult = "FAIL";
				remarks = remarks + lineitem.get(0) + " is present in Checkout Cart ; ";
			}		
					
				
		// Supplemental Cart Language Validation
		// Scenario - 90-day order + Paypal - could not validate 90-day cart language and supplemental language because invalid zipcode could not be fill-in for Paypal
		if((category_list.contains("Kit")) || (category_list.contains("SubscribeandSave"))) {
//			if((!(cc.equalsIgnoreCase("Paypal"))) && (!(supplysize.equalsIgnoreCase("90"))) && (!(offer_postpurchase.equalsIgnoreCase("Yes")))) {
				String actual_suppl_cart_lang = lang_obj.getsupplementalcartlanguage(driver);
//				System.out.println("Actual Supplemental cart language : " + actual_suppl_cart_lang);
				for(List<String> expected_item : expected_lineitems) {
					String exp_suppl_cart_lang = expected_item.get(6);
//					System.out.println("Expected Supplemental cart language : " + exp_suppl_cart_lang);
					
					if(exp_suppl_cart_lang.equalsIgnoreCase("No Supplemental Cart Language")) {
						continue;
					}
					
					//Remove whitespace
					String expsuppcartlang = exp_suppl_cart_lang.replaceAll("\\s+", "");
					String actsuppcartlang = actual_suppl_cart_lang.replaceAll("\\s+", "");
					
					// Remove special characters
					expsuppcartlang = expsuppcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
					actsuppcartlang = actsuppcartlang.replaceAll("[^a-zA-Z0-9$]+", "");
					
					if(actsuppcartlang.toLowerCase().contains(expsuppcartlang.toLowerCase())) {
						SuppCartLanguageResult = "PASS";
					}
					else {
						SuppCartLanguageResult = "FAIL";
						remarks = remarks + "Checkout - " + expected_item.get(0) + " - " + expected_item.get(1) + " Supplemental Cart Language Mismatch. Expected - " + exp_suppl_cart_lang + " ; ";
					}
				}	
//			}
		}	}	
				
		// Validate Checkout pricing		
//		System.out.println("Validating Checkout Pricing");
		// Get Actual Checkout Price
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
		System.out.println("Checkout Pricing fetched : " + checkout_subtotal + "," + checkout_shipping + "," + checkout_salestax + "," + checkout_total);
				
		// Calculate Expected Checkout Price
		String expected_subtotal = bf_obj.CalculateTotalPrice(subtotal_list);
		String subtotal_result = bf_obj.assertPrice(checkout_subtotal, expected_subtotal);
				
		if(subtotal_result.equalsIgnoreCase("PASS")) {
			EntryPriceResult = "PASS";
		}
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Checkout Subtotal does not match with the expected price, Expected - " + expected_subtotal + " , Actual - " + checkout_subtotal;
		}
		
		// Calculate Expected Checkout - Shipping Price
		String expected_shipping = bf_obj.CalculateTotalPrice(shipping_list);
		
		if(expected_shipping.contains("0.0")) {
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
		else if(expected_shipping.equalsIgnoreCase(checkout_shipping)) {
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
			remarks = remarks + "Shipping price on checkout page is wrong, Expected - " + expected_shipping + " , Actual - " + checkout_shipping;
		}
				
		// Checkout SalesTax Validation
		String salestax = bf_obj.getSalesTax(driver, expected_subtotal, expected_shipping);
		String salestax_result = bf_obj.assertPrice(checkout_salestax, salestax);

		if(salestax_result.equalsIgnoreCase("PASS")) {
			EntryPriceResult = "PASS";
		}				
		else {
			EntryPriceResult = "FAIL";
			remarks = remarks + "Checkout Salestax does not match with the expected salestax, Expected - " + salestax + " , Actual - " + checkout_salestax;
		}
		
//		System.out.println("Expected SalesTax : " + salestax);
//		System.out.println("Actual SalesTax : " + checkout_salestax);
		
		// Checkout Total Validation
		List<String> total_list = new ArrayList<String>();
		total_list.add(expected_subtotal);
		total_list.add(expected_shipping);
		total_list.add(salestax);
		
		String total = bf_obj.CalculateTotalPrice(total_list);
		String total_result = bf_obj.assertPrice(checkout_total, total);

		if(total_result.equalsIgnoreCase("PASS")) {
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
			if(offer_postpurchase.equalsIgnoreCase("Yes")) {
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
			// supplysize - 30
			// No Fall back scenario
			if(supplysize.equalsIgnoreCase("30")) {
//				System.out.println("Post Purchase - As per flow");
				pixel_obj.defineNewHar(proxy, brand + "PostPurchaseUpsell");	  				
				bf_obj.complete_order(driver, brand, cc);
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_postpurchaseupsell_" + pattern + ".har", driver);
			}
			// supplysize - 90
			// After Fall back scenario - control navigates to Confirmation page
			else if(supplysize.equalsIgnoreCase("90")) {
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
			if((supplysize.equalsIgnoreCase("30")) || (cc.equalsIgnoreCase("Paypal"))) {
				pixel_obj.defineNewHar(proxy, brand + "ConfirmationPage");
				bf_obj.upsell_confirmation(driver, brand, campaigncategory, expectedofferdata_kit.get("Offer Post-Purchase"));
				pixel_obj.getHarData(proxy, System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Harfiles\\" + brand + "\\" + brand + "_" + campaign + "_confirmationpage_" + pattern + ".har", driver);
			}
		}								
		
		// Confirmation page validations
		String conf_offercode = bf_obj.fetch_confoffercode(driver, brand);
		System.out.println("Confirmation PPIDs : " + conf_offercode);
		
		List<String> conf_offercode_list = Arrays.asList(conf_offercode.split("\\s*,\\s*"));
		
		// When some more lineitems are expected
		temp_lineitemlist = new ArrayList<>();
		diff_lineitemlist = new ArrayList<>(expected_lineitems);
							
		for(String offercode : conf_offercode_list) {									
			for(List<String> expected_item : expected_lineitems) {
				if(expected_item.contains(offercode)) {											
					temp_lineitemlist.add(expected_item);
				}
			}				
		}			
//		System.out.println("Diff lineitem :" + diff_lineitemlist);
//		System.out.println("Temp lineitem :" + temp_lineitemlist);
		diff_lineitemlist.removeAll(temp_lineitemlist);
//		System.out.println("Diff lineitem - After Removal :" + diff_lineitemlist);
		for(List<String> lineitem : diff_lineitemlist) {
			// If Result is already fail, then the overall ppid result is fail
			if(ppidResult.equalsIgnoreCase("FAIL")) {
				ppidResult = "FAIL";
			}
			else {
				ppidResult = "PASS";
			}
			remarks = remarks + "Missing " + lineitem.get(0) + " - " + lineitem.get(1) + " in Confirmation page ; ";
		}
				
		// When Extra lineitems are present
		List<String> temp_ppidlist = new ArrayList<>();
		List<String> diff_ppidlist = new ArrayList<>(conf_offercode_list);
				
		for(List<String> expected_item : expected_lineitems) {
			String expected_ppid = expected_item.get(1);
						
			for(String actual_item : conf_offercode_list) {
				if(actual_item.contains(expected_ppid)) {
					temp_ppidlist.add(actual_item);					
				}
			}				
		}		
		diff_ppidlist.removeAll(temp_ppidlist);
		for(String extrappid : diff_ppidlist) {
			// If Result is already fail, then the overall ppid result is fail
			if(ppidResult.equalsIgnoreCase("FAIL")) {
				ppidResult = "FAIL";
			}
			else {
				ppidResult = "PASS";
			}
			remarks = remarks + extrappid + " is present in Confirmation page ; ";
		}			
		
		if(category_list.contains("Kit")) {
			// Validate PrepU Product
			if(!(expectedofferdata_kit.get("PrePU Product").equalsIgnoreCase("No PrePU Product"))) {
				String expectedprepuppid = bf_obj.getPPIDfromString(brand, expectedofferdata_kit.get("PrePU Product")).get(0);
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
			if(!(expectedofferdata_kit.get("PostPU Product").equalsIgnoreCase("No PostPU Product"))) {
				String expectedpostpuppid = bf_obj.getPPIDfromString(brand, expectedofferdata_kit.get("PostPU Product")).get(0);
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
		System.out.println("Confirmation Pricing fetched : " + conf_pricing);
		
		// Subtotal validation
		String conf_subtotal_result = bf_obj.assertPrice(conf_subtotal, expected_subtotal);
		if(conf_subtotal_result.equalsIgnoreCase("PASS")) {
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
			remarks = remarks + "Confirmation Subtotal is wrong, Expected - " + expected_subtotal + " , Actual - " + conf_subtotal + ",";
		}
		
		// Shipping validation		
		if(expected_shipping.contains("0.0")) {
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
		else if(expected_shipping.equalsIgnoreCase(conf_shipping)) {
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
			remarks = remarks + "Shipping price on confirmation page is wrong, Expected - " + expected_shipping + " , Actual - " + conf_shipping;
		}		
				
		// Sales Tax Validation
		String conf_salestax_result = bf_obj.assertPrice(conf_salestax, salestax);

		if(conf_salestax_result.equalsIgnoreCase("PASS")) {
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
		String conf_total_result = bf_obj.assertPrice(conf_total, total);

		if(conf_total_result.equalsIgnoreCase("PASS")) {
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
		String actualrenewalplanid = comm_obj.getFromVariableMap(driver, "renewalPlanId");	
		
		List<String> actual_renewal_plan_list = Arrays.asList(actualrenewalplanid.split("\\s*,\\s*"));
		
		// When some more lineitems are expected
		List<String> temp_renewlist = new ArrayList<>();
		List<String> diff_renewlist = new ArrayList<>(renewal_plan_list);
							
		for(String plan : actual_renewal_plan_list) {									
			for(String expected_plan : renewal_plan_list) {
				if(expected_plan.contains(plan)) {											
					temp_renewlist.add(expected_plan);
				}
			}				
		}			
		diff_renewlist.removeAll(temp_renewlist);
		for(String plan : diff_renewlist) {
			RenewalPlanResult = "FAIL";			
			remarks = remarks + "Renewal Plan Id - " + plan + " is Missing ; ";
		}
				
		// When Extra lineitems are present
		temp_renewlist = new ArrayList<>();
		diff_renewlist = new ArrayList<>(actual_renewal_plan_list);
				
		for(String expected_plan : renewal_plan_list) {						
			for(String actual_plan : actual_renewal_plan_list) {
				if(actual_plan.contains(expected_plan)) {
					temp_renewlist.add(actual_plan);					
				}
			}				
		}			
		diff_renewlist.removeAll(temp_renewlist);
		for(String plan : diff_renewlist) {
			if(!(plan.equalsIgnoreCase("null"))) {
				RenewalPlanResult = "FAIL";
				remarks = remarks + "Additional Renewal Plan Id - " + plan + " is Present ; ";
			}			
		}	
			
//			System.out.println("Expected Renewal Plan Id : " + expectedofferdata.get("Renewal Plan Id"));	
//			System.out.println("Actual Renewal Plan Id : " + actualrenewalplanid);
				
		// Installment Plan Validation
		String actualinstallmentplanid = "";
		if(category_list.contains("Kit")) {
			if(!(expectedofferdata_kit.get("Installment Plan Id").equalsIgnoreCase("No Installment Plan"))) {
				actualinstallmentplanid = comm_obj.getFromVariableMap(driver, "paymentPlanId");
				if(expectedofferdata_kit.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {					
					if(!(actualinstallmentplanid.contains(expectedofferdata_kit.get("Installment Plan Id")))) {
						InstallmentPlanResult = "FAIL";
						remarks = remarks + "Installment Plan Id does not match, Expected - " + expectedofferdata_kit.get("Installment Plan Id") + " , Actual - " + actualinstallmentplanid + ",";
					}
					else {
						InstallmentPlanResult = "PASS";
						
						// To remove null and commas
						actualinstallmentplanid = expectedofferdata_kit.get("Installment Plan Id");
					}
					
//					System.out.println("Expected Installment Plan Id : " + expectedofferdata.get("Installment Plan Id"));	
//					System.out.println("Actual Installment Plan Id : " + actualinstallmentplanid);
				}				
				else {
					actualinstallmentplanid = "";
					InstallmentPlanResult = "";
				}
			}
		}		
		
		// Media ID Validation
		String actualmediaid = comm_obj.getFromVariableMap(driver, "mediaId");				
		if(!(expectedsourcecodedata.get("Media ID").contains(actualmediaid))) {
			MediaIdResult = "FAIL";
			remarks = remarks + "Media Id does not match, Expected - " + expectedsourcecodedata.get("Media ID") + " , Actual - " + actualmediaid + ",";
		}
		else {
			MediaIdResult = "PASS";
		}
		
		// Creative ID Validation
		String actualcreativeid = comm_obj.getFromVariableMap(driver, "creativeId");				
		if(!(expectedsourcecodedata.get("Creative ID").contains(actualcreativeid))) {
			CreativeIdResult = "FAIL";
			remarks = remarks + "Creative Id does not match, Expected - " + expectedsourcecodedata.get("Creative ID") + " , Actual - " + actualcreativeid + ",";
		}
		else {
			CreativeIdResult = "PASS";
		}
		
		// Venue ID Validation
		String actualvenueid = comm_obj.getFromVariableMap(driver, "venueId");				
		if(!(expectedsourcecodedata.get("Venue ID").contains(actualvenueid))) {
			VenueIdResult = "FAIL";
			remarks = remarks + "Venue Id does not match, Expected - " + expectedsourcecodedata.get("Venue ID") + " , Actual - " + actualvenueid + ",";
		}
		else {
			VenueIdResult = "PASS";
		}
		
//		// Price Book Id Validation
		String actualpricebookid = comm_obj.getFromVariableMap(driver, "pricebookId");
//		System.out.println("Actual price book id: " + actualpricebookid);
				
		List<String> actual_price_book_list = Arrays.asList(actualpricebookid.split("\\s*,\\s*"));
//		System.out.println("Expected price book list: " + pricebook_id_list);
//		System.out.println("Actual price book list: " + actual_price_book_list);
				
		if(pricebook_id_list.contains("No expected PriceBookID")) {
			PriceBookIdResult = "Could not validate";		
			remarks = remarks + "No Expected Price Book Id in the Merchandising template ; ";
		}
		else{
			// When some more Price Book Ids are expected
			List<String> temp_pricebooklist = new ArrayList<>();
			List<String> diff_pricebooklist = new ArrayList<>(pricebook_id_list);
										
			for(String id : actual_price_book_list) {									
				for(String expected_id : pricebook_id_list) {
					if(expected_id.contains(id)) {											
						temp_pricebooklist.add(expected_id);
					}
				}				
			}			
			diff_pricebooklist.removeAll(temp_pricebooklist);
			for(String id : diff_pricebooklist) {
				PriceBookIdResult = "FAIL";			
				remarks = remarks + "Price Book Id - " + id + " is Missing ; ";
			}
			
			temp_pricebooklist.clear();
			diff_pricebooklist.clear();
			
			// When Extra Price Book Ids are present
			temp_pricebooklist = new ArrayList<>();
			diff_pricebooklist = new ArrayList<>(actual_price_book_list);
							
			for(String expected_id : pricebook_id_list) {						
				for(String actual_id : actual_price_book_list) {
					if(actual_id.contains(expected_id)) {
						temp_pricebooklist.add(actual_id);					
					}
				}				
			}			
//			System.out.println("Diff price book id: " + diff_pricebooklist);
//			System.out.println("Temp price book id: " + temp_pricebooklist);
			diff_pricebooklist.removeAll(temp_pricebooklist);
//			System.out.println("Diff price book id - after removal: " + diff_pricebooklist);
			for(String id : diff_pricebooklist) {
				if(!(id.equalsIgnoreCase("null"))) {
					PriceBookIdResult = "FAIL";
					remarks = remarks + "Additional Price Book Id - " + id + " is Present ; ";
				}			
			}		
		}
			
				
		List<String> output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add(category);
		output_row.add(email);
		output_row.add(conf_offercode + " - " + ppidResult);
		output_row.add(conf_num);
		output_row.add(conf_pricing + " - " + EntryPriceResult);
		output_row.add(ContinuityPriceResult);
		output_row.add(actualrenewalplanid + " - " + RenewalPlanResult);
		output_row.add(actualinstallmentplanid + " - " + InstallmentPlanResult);
		output_row.add(CartLanguageResult);
		output_row.add(SuppCartLanguageResult);
		output_row.add(actualmediaid + " - " + MediaIdResult);
		output_row.add(actualcreativeid + " - " + CreativeIdResult);
		output_row.add(actualvenueid + " - " + VenueIdResult);
		output_row.add(actualpricebookid + " - " + PriceBookIdResult);
		output_row.add(shipbill);	
		output_row.add(cc);	
		output_row.add(browser);	
		output_row.add(remarks);
//		System.out.println("Output row : " + output_row);
		output.add(output_row);
		
		driver.close();
	
		if(!(pixelStr.equalsIgnoreCase("-"))) {
			HashMap<Integer, HashMap> overallOutput = pixel_obj.validatePixels(pixelStr, pattern, brand, campaign, env, campaignpages);
			attachmentList = pixel_obj.writePixelOutput(overallOutput, brand, campaign, attachmentList);
		}
	}
	
	@AfterSuite
	public void populateExcel() throws IOException {
		String file = comm_obj.populateOutputExcel(output, "BuyflowResults", System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Run Output\\");
		
//		List<String> attachmentList = new ArrayList<String>();
		attachmentList.add(file);
		
		mailObj.sendEmail("Buyflow Results", sendReportTo, attachmentList);
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
//	}
	
//	@AfterSuite
//	public void populateExcel() throws IOException {
//		String file = comm_obj.populateOutputExcel(output, "BuyflowResults", System.getProperty("user.dir") + "\\Input_Output\\BuyflowValidation\\Run Output\\");
//		
////		List<String> attachmentList = new ArrayList<String>();
//		attachmentList.add(file);
//		
//		mailObj.sendEmail("Buyflow Results", sendReportTo, attachmentList);
//	}	
//}

