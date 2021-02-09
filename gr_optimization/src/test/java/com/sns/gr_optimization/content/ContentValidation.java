package com.sns.gr_optimization.content;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sns.gr_optimization.testbase.BuyflowUtilities;
import com.sns.gr_optimization.testbase.CartLanguageUtilities;
import com.sns.gr_optimization.testbase.CommonUtilities;
import com.sns.gr_optimization.testbase.ContentUtilities;
import com.sns.gr_optimization.testbase.DBUtilities;
import com.sns.gr_optimization.testbase.MailUtilities;
import com.sns.gr_optimization.testbase.MerchandisingUtilities;
import com.sns.gr_optimization.testbase.PixelUtilities;
import com.sns.gr_optimization.testbase.PricingUtilities;
import com.sns.gr_optimization.testbase.SASUtilities;

public class ContentValidation {
	CommonUtilities comm_obj = new CommonUtilities();
	DBUtilities db_obj = new DBUtilities();

	BuyflowUtilities bf_obj = new BuyflowUtilities();
	PricingUtilities pr_obj = new PricingUtilities();
	CartLanguageUtilities lang_obj = new CartLanguageUtilities();
	MailUtilities mailObj = new MailUtilities();
	SASUtilities sas_obj = new SASUtilities();
	MerchandisingUtilities merch_obj = new MerchandisingUtilities();
	PixelUtilities pixel_obj = new PixelUtilities();
	ContentUtilities content_obj = new ContentUtilities();
	// BaseTest base_obj = new BaseTest();
	Scanner in = new Scanner(System.in);
	static List<String> attachmentList = new ArrayList<String>();

	List<List<String>> output = new ArrayList<List<String>>();
	String sendReportTo = "aaqil@searchnscore.com";

	static final String pass = "PASS";
	static final String fail = "FAIL";
	String Spanish = "Spanish";
	String English = "English";

	// String env = "";
	// String env = System.getProperty("Environment");

	// @Parameters({ "environment" })
	@BeforeSuite
	public void getEmailId() {
		// public void getEmailId(String environment) {
		// env = environment;
		// System.out.println("Enter Email id : ");
		// sendReportTo = in.next();
		mkdir();
		// newDirectory = new File(System.getProperty("user.dir") +
		// "\\Input_Output\\BuyflowValidation\\Screenshots", brand);
		// newDirectory.mkdir();
	}

	@DataProvider(name = "buyflowInput", parallel = true)
	public Object[][] testData() {
		Object[][] arrayObject = null;
		arrayObject = comm_obj.getExcelData(
				System.getProperty("user.dir") + "/Input_Output/ContentValidation/run_input.xlsx", "Content", 1);
		return arrayObject;
	}

	@Test(dataProvider = "buyflowInput")
	public void buyflow(String env, String brand, String campaign) throws Exception {

		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/Drivers/chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		// options.setProxy(ClientUtil.createSeleniumProxy(new
		// InetSocketAddress("192.168.0.15", 0)));
		options.setAcceptInsecureCerts(true);
		options.addArguments("--ignore-certificate-errors");
		options.addArguments("--disable-backgrounding-occluded-windows");
		// Get Source Code Information - Campaign Category
		String campaigncategory = db_obj.checkcampaigncategory(brand, campaign);
		if (campaigncategory.equalsIgnoreCase("n/a")) {
			campaigncategory = campaign;
		}
		///////////////////////////////////////////////////////////////
		// Read all Merchandising Input
		String[][] catalogData = null;
		String[][] merchData = null;

		System.out.println(env);
		List<String> category_list = Arrays.asList("Kit".split(","));
		System.out.println(category_list);

		// Read Merchandising Input

		merchData = comm_obj.getExcelData(System.getProperty("user.dir")
				+ "/Input_Output/BuyflowValidation/Merchandising Input/" + brand + "/" + campaigncategory + ".xlsx",
				"Active Campaign", 0);

		// PPID List<String> single_offers = merch_obj.fetch_random_singles(catalogData,
		// 1);

		// Intialize result variables
		String remarks = "";
		String giftResult = "";

		String ppid = merch_obj.getrandomPPID(merchData);

		// Launch Browser
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		String pixelStr = "-";
		String giftppid = "-";

		String url = db_obj.getUrl(brand, campaign, env);
		url = pixel_obj.generateURL(url, pixelStr, brand);

		// Output initialize
		List<String> output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);

		// HomePage

		System.out.println(url);
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);

		HashMap<String, String> sourcecodedata = null;
		HashMap<String, String> expectedsourcecodedata = null;
		// Read Source code details from Merchandising template for the campaign
		sourcecodedata = merch_obj.getSourceCodeInfo(merchData, campaign);
		// Collect Source code details for the campaign
		expectedsourcecodedata = merch_obj.generateExpectedSourceCodeData(sourcecodedata);
		// System.out.println(expectedsourcecodedata);

		// HashMap variable to collect Kit related details from Merchandising Template
		HashMap<String, String> expectedofferdata_kit = null;
		HashMap<String, String> expectedofferdata_product = null;

		List<String> subtotal_list = new ArrayList<String>();
		List<String> subtotal_list_forshippingcalc = new ArrayList<String>();
		List<String> shipping_list = new ArrayList<String>();
		List<String> renewal_plan_list = new ArrayList<String>();
		List<String> pricebook_id_list = new ArrayList<String>();
		List<List<String>> expected_lineitems = new ArrayList<List<String>>();

		List<String> campaignpages = new ArrayList<String>();

		String postpu = "No";
		String prepu = "No";
		String T_C = "Terms & Conditions";
		String P_P = "Privacy Policy";

		// Go to Terms Conditions
		// output_row = new ArrayList<String>();
		// output_row.add(env);
		// output_row.add(brand);
		// output_row.add(campaign);
		// output_row.add("Terms & Conditions : ");
		List<Map<String, Object>> Terms_Conditions_locator = null;
		Terms_Conditions_locator = content_obj.get_terms_conditions(brand, campaign, "Terms & Conditions", null);
		String elementvalue = Terms_Conditions_locator.get(0).get("ELEMENTVALUE").toString();
		String elementlocator = Terms_Conditions_locator.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		comm_obj.waitUntilElementAppears(driver, elementvalue);
		Thread.sleep(1000);
		try {
			kit_elmt.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue)).size() != 0) {
				// output_row.add(pass);
			} else {
				// output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Terms & Conditions link not available");
		}
		kit_elmt.click();
		// output.add(output_row);

		// Check Content T & C - English
		List<Map<String, Object>> Terms_Conditions_Content = null;
		// Go to Terms Conditions
		output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Terms & Conditions: Validate English Content ");
		Terms_Conditions_Content = content_obj.get_terms_conditions(brand, campaign,
				"Terms & Conditions Content English", null);
		String elementvalue_Content = Terms_Conditions_Content.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Content = Terms_Conditions_Content.get(0).get("ELEMENTLOCATOR").toString();
		File path_src = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\" + T_C
				+ "\\" + English + "\\src\\" + brand + "\\" + "Content_src.txt");

		WebElement kit_elmt_Content = comm_obj.find_webelement(driver, elementlocator_Content, elementvalue_Content);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Content);
		Thread.sleep(1000);
		String Content = kit_elmt_Content.getText();
		content_obj.write_text(Content, brand, English, T_C);
		try {
			kit_elmt_Content.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Content)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Terms & Conditions link not available");
		}
		String result = content_obj.Compare_text_file(path_src, brand, English, T_C);
		output_row.add(result);
		output.add(output_row);

		// Sales Tax
		output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Terms & Conditions: Check Sales Tax link");
		List<Map<String, Object>> Sales_Tax_locator = null;
		Sales_Tax_locator = content_obj.get_terms_conditions(brand, campaign, "Sales Tax", null);
		String saleselementvalue = Sales_Tax_locator.get(0).get("ELEMENTVALUE").toString();
		String saleselementlocator = Sales_Tax_locator.get(0).get("ELEMENTLOCATOR").toString();

		WebElement sales_elmt = comm_obj.find_webelement(driver, saleselementlocator, saleselementvalue);
		comm_obj.waitUntilElementAppears(driver, saleselementvalue);
		Thread.sleep(1000);
		try {
			sales_elmt.isDisplayed();
			if (driver.findElements(By.xpath(saleselementvalue)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);

			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Sales Tax link not available");
		}
		output.add(output_row);

		// Click T & C Spanish
		content_obj.clickaelement(driver, env, brand, campaign, "Terms & Conditions Español");

		// Terms Conditions Español
		List<Map<String, Object>> Terms_Conditions_locator_Es = null;
		output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Terms & Conditions: Validate En Español link");
		Terms_Conditions_locator_Es = content_obj.get_terms_conditions(brand, campaign, "Terms & Conditions Español",
				null);
		String elementvalue_Es = Terms_Conditions_locator_Es.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Es = Terms_Conditions_locator_Es.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_Es = comm_obj.find_webelement(driver, elementlocator_Es, elementvalue_Es);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Es);
		Thread.sleep(1000);

		try {
			kit_elmt_Es.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Es)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Terms & Conditions link not available");
		}

		output.add(output_row);

		// Check Content T & C - Spanish
		List<Map<String, Object>> Terms_Conditions_Content_S = null;
		// Go to Terms Conditions
		output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Terms & Conditions: Validate Spanish Content ");
		Terms_Conditions_Content_S = content_obj.get_terms_conditions(brand, campaign,
				"Terms & Conditions Content Spanish", null);
		String elementvalue_Content_S = Terms_Conditions_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Content_S = Terms_Conditions_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		File path_src_S = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\" + T_C
				+ "\\" + Spanish + "\\src\\" + brand + "\\" + "Content_src.txt");

		WebElement kit_elmt_Content_S = comm_obj.find_webelement(driver, elementlocator_Content_S,
				elementvalue_Content_S);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Content_S);
		Thread.sleep(1000);
		String Content_S = kit_elmt_Content_S.getText();
		content_obj.write_text(Content_S, brand, Spanish, T_C);
		try {
			kit_elmt_Content_S.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Content_S)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Terms & Conditions link not available");
		}
		String result_S = content_obj.Compare_text_file(path_src_S, brand, Spanish, T_C);
		output_row.add(result_S);
		output.add(output_row);

		// Click English Version

		content_obj.clickaelement(driver, env, brand, campaign, "Terms & Conditions English");

		// Go to Privacy Policy

		content_obj.clickaelement(driver, env, brand, campaign, "Privacy Policy");

		// String winHandleBefore = driver.getWindowHandle();
		for (String winHandle : driver.getWindowHandles()) {
			driver.switchTo().window(winHandle);
			// driver.manage().window().maximize();
			Thread.sleep(2000);
			// ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			// System.out.println("No. of tabs: " + tabs.size());
		}

		// Privacy Policy - English

		output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Privacy Policy: Validate English content ");
		List<Map<String, Object>> Terms_Conditions_Content_PP = null;
		Terms_Conditions_Content_PP = content_obj.get_terms_conditions(brand, campaign,
				"Privacy Policy Content English", null);
		String elementvalue_Content_PP = Terms_Conditions_Content_PP.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Content_PP = Terms_Conditions_Content_PP.get(0).get("ELEMENTLOCATOR").toString();
		File path_src_PP = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\"
				+ P_P + "\\" + English + "\\src\\" + brand + "\\" + "Content_src.txt");

		WebElement kit_elmt_Content_PP = comm_obj.find_webelement(driver, elementlocator_Content_PP,
				elementvalue_Content_PP);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Content_PP);
		Thread.sleep(1000);
		String Content_PP = kit_elmt_Content_PP.getText();
		content_obj.write_text(Content_PP, brand, English, P_P);
		try {
			kit_elmt_Content_PP.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Content_PP)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Privacy Policy link not available");
		}
		String result_PP = content_obj.Compare_text_file(path_src_PP, brand, English, P_P);
		output_row.add(result_PP);
		output.add(output_row);

		// California Privacy Rights
		output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Privacy Policy: Check Your California Privacy Rights link ");
		List<Map<String, Object>> Sales_Tax_locator_PPR = null;
		Sales_Tax_locator_PPR = content_obj.get_terms_conditions(brand, campaign, "California Privacy Rights", null);
		String saleselementvalue_PPR = Sales_Tax_locator_PPR.get(0).get("ELEMENTVALUE").toString();
		String saleselementlocator_PPR = Sales_Tax_locator_PPR.get(0).get("ELEMENTLOCATOR").toString();

		WebElement sales_elmt_PPR = comm_obj.find_webelement(driver, saleselementlocator_PPR, saleselementvalue_PPR);
		comm_obj.waitUntilElementAppears(driver, saleselementvalue_PPR);
		Thread.sleep(1000);
		try {
			sales_elmt_PPR.isDisplayed();
			if (driver.findElements(By.xpath(saleselementvalue_PPR)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);

			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Your California Privacy Rights link not available");
		}
		output.add(output_row);

		// Go To Privacy Policy Español
		List<Map<String, Object>> Privacy_Policy_locator_Es = null;
		output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Privacy Policy : Validate En Español link ");
		Privacy_Policy_locator_Es = content_obj.get_terms_conditions(brand, campaign, "Privacy Policy Español", null);
		String elementvalue_PP_Es = Privacy_Policy_locator_Es.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_PP_Es = Privacy_Policy_locator_Es.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_PP_Es = comm_obj.find_webelement(driver, elementlocator_PP_Es, elementvalue_PP_Es);
		comm_obj.waitUntilElementAppears(driver, elementvalue_PP_Es);
		Thread.sleep(1000);

		try {
			kit_elmt_PP_Es.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_PP_Es)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Privacy Policy link not available");
		}
		Thread.sleep(2000);
		kit_elmt_PP_Es.click();
		output.add(output_row);

		// Check Content Privacy Policy - Spanish
		output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add("Privacy Policy: Validate Spanish content ");
		List<Map<String, Object>> Privacy_Policy_Content_S = null;
		Privacy_Policy_Content_S = content_obj.get_terms_conditions(brand, campaign, "Privacy Policy Content Spanish",
				null);
		String elementvalue_Content_PP_S = Privacy_Policy_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Content_PP_S = Privacy_Policy_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		File path_src_PP_S = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\"
				+ P_P + "\\" + Spanish + "\\src\\" + brand + "\\" + "Content_src.txt");

		WebElement kit_elmt_Content_PP_S = comm_obj.find_webelement(driver, elementlocator_Content_PP_S,
				elementvalue_Content_PP_S);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Content_PP_S);
		Thread.sleep(1000);
		String Content_PP_S = kit_elmt_Content_PP_S.getText();
		content_obj.write_text(Content_PP_S, brand, Spanish, P_P);
		try {
			kit_elmt_Content_PP_S.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Content_PP_S)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Privacy Policy link not available");
		}
		String result_PP_S = content_obj.Compare_text_file(path_src_PP_S, brand, Spanish, P_P);
		output_row.add(result_PP_S);
		output.add(output_row);

		// Check DND on Home Page
		output.add(content_obj.donotsell(driver, env, brand, campaign, "HomePage"));

		// Check Customer_Service
		// output_row = new ArrayList<String>();
		// output_row.add(env);
		// output_row.add(brand);
		// output_row.add(campaign);
		// output_row.add("Check Customer Service : ");
		List<Map<String, Object>> Customer_Service_Content_S = null;
		Customer_Service_Content_S = content_obj.get_terms_conditions(brand, campaign, "Customer Service", null);
		String elementvalue_Customer_Service = Customer_Service_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Customer_Service = Customer_Service_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_Customer_Service = comm_obj.find_webelement(driver, elementlocator_Customer_Service,
				elementvalue_Customer_Service);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Customer_Service);
		Thread.sleep(1000);

		try {
			kit_elmt_Customer_Service.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Customer_Service)).size() != 0) {
				// output_row.add(pass);
			} else {
				// output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Customer Service link not available");
		}
		Thread.sleep(2000);
		kit_elmt_Customer_Service.click();
		// output.add(output_row);

		output.add(content_obj.Check_Available(driver, env, brand, campaign, "Phone Number",
				"Validate Phone Number in Customer Service "));
		output.add(content_obj.Check_Available(driver, env, brand, campaign, "live chat",
				"Validate chat link in Customer Service "));
		output.add(content_obj.Check_Available(driver, env, brand, campaign, "Accessibility Interface",
				"Validate AccessiBe tool "));

		content_obj.clickaelement(driver, env, brand, campaign, "Accessibility Interface");
		//

		driver.get(url);

		// Get column in which the PPID is present and check if the PPID belongs to
		// Pre-Purchase Entry Kit
		int PPIDcolumn = merch_obj.getPPIDColumn(merchData, ppid);
		String PPUSection = merch_obj.IsPrePurchase(merchData, ppid);

		if (((brand.equalsIgnoreCase("MeaningfulBeauty")) && (campaign.equalsIgnoreCase("os")))
				|| ((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("advanced-one")))
				|| ((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fsh2b")))) {
			if (giftppid.equalsIgnoreCase("No")) {
				PPUSection = "No";
			} else {
				PPUSection = "Yes";
			}
		}

		// System.out.println(PPIDcolumn + PPUSection);

		// Check if the PPID is present in the campaign
		if (PPIDcolumn == 0) {
			remarks = remarks + ppid + " doesn't exist in " + brand + " - " + campaigncategory;
			// continue;
		}

		// Read the entire column data
		HashMap<String, String> kit_offerdata = merch_obj.getColumnData(merchData, PPIDcolumn, PPUSection);
		// System.out.println(kit_offerdata);
		// Check Post-purchase Upsell for the campaign
		postpu = merch_obj.checkPostPU(kit_offerdata, brand);
		if (((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fsh2b")))
				&& (PPUSection.equalsIgnoreCase("No"))) {
			postpu = "No";
		}

		// PagePattern for the Current PPID
		String pagepattern = kit_offerdata.get("PagePattern").trim();

		// Check Pre-purchase Upsell for the campaign
		if (pagepattern.contains("prepu")) {
			prepu = "Yes";
		} else {
			prepu = "No";
		}

		// Collect current Offer related details from Merchandising Input file
		if (!(brand.equalsIgnoreCase("JloBeauty"))) {
			expectedofferdata_kit = merch_obj.generateExpectedOfferDataForKit(kit_offerdata, PPUSection, postpu, ppid,
					giftppid, brand, campaigncategory);
			// System.out.println("Expected Offerdata - Kit : " + expectedofferdata_kit);
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
		if (!(giftppidarr[0].contains("No Gift"))) {
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
		if (expectedofferdata_kit.get("Offer Pre-Purchase").equalsIgnoreCase("Yes")) {
			if (!(expectedofferdata_kit.get("PrePU Product").equalsIgnoreCase("No PrePU Product"))) {
				String prepu_ppid = String.join(",",
						bf_obj.getPPIDfromString(brand, expectedofferdata_kit.get("PrePU Product")));
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
		if (expectedofferdata_kit.get("Offer Post-Purchase").equalsIgnoreCase("Yes")) {
			if (!(expectedofferdata_kit.get("PostPU Product").equalsIgnoreCase("No PostPU Product"))) {
				String postpu_ppid = String.join(",",
						bf_obj.getPPIDfromString(brand, expectedofferdata_kit.get("PostPU Product")));
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
		if (expectedsourcecodedata.get("Price Book ID") == null) {
			pricebook_id_list.add("No expected PriceBookID");
		} else {
			pricebook_id_list.add(expectedsourcecodedata.get("Price Book ID"));
		}

		// Move to SAS

		bf_obj.click_cta(driver, brand, campaign, "Ordernow");

		// Gift Validation
		// if(!(expectedofferdata_kit.get("Gift Name").equalsIgnoreCase("No Gift"))) {
		// if(!(expectedofferdata_kit.get("Campaign Gifts").equalsIgnoreCase("-"))) {
		String expectedcampaigngifts = expectedofferdata_kit.get("Campaign Gifts");

		// }
		if ((expectedcampaigngifts != null) && (!(expectedcampaigngifts.equals("-")))
				&& (!(expectedcampaigngifts.equals("")))) {
			giftResult = bf_obj.checkGifts(driver, brand, campaigncategory, expectedcampaigngifts);
			remarks = remarks + giftResult;
		}
		// }
		String Gift_PPID = expectedofferdata_kit.get("Gift PPID");
		String[] arrOfStr = Gift_PPID.split(",", 2);
		expectedofferdata_kit.put("Gift PPID", arrOfStr[1]);
		System.out.println("Selected Gift : " + arrOfStr[1]);

		// Select offer

		sas_obj.select_offer(driver, expectedofferdata_kit, "Kit");

		// Check DND on SAS Page
		output.add(content_obj.donotsell(driver, env, brand, campaign, "SASPage"));

		// Move to Checkout

		bf_obj.move_to_checkout(driver, brand, campaigncategory, "Kit");

		// Check DND on Checkout Page
		output.add(content_obj.donotsell(driver, env, brand, campaign, "CheckoutPage"));

		output.add(content_obj.Check_Available(driver, env, brand, campaign, "Terms & Conditions Checkout",
				"Click open Terms & Conditions popup in Checkout page"));

		content_obj.clickaelement(driver, env, brand, campaign, "Terms & Conditions Checkout");

		output_row.add(remarks);
		// System.out.println("Output row : " + output_row);
		// output.add(output_row);

		// Move - sitemap.xml
		String site = "sitemap.xml";
		String Append_xml = url + "/" + site;
		driver.get(Append_xml);

		output.add(content_obj.Check_Available(driver, env, brand, campaign, site, "Validate sitemap.xml "));

		// Move - robots.txt
		String Append_rb = null;
		String txt = "robots.txt";
		// if (campaign.equalsIgnoreCase("Core") == false) {
		// String[] up_url = url.split("/", 4);
		// Append_rb = up_url[2] + "/" + txt;
		// }
		Append_rb = url + "/" + txt;
		System.out.println("URL : " + Append_rb);
		driver.get(Append_rb);

		output.add(content_obj.Check_Available(driver, env, brand, campaign, txt, "Validate Robots.txt "));

		driver.quit();

	}

	@AfterSuite
	public void populateExcel() throws Exception {

		// take_result();

		String file = comm_obj.populateOutputExcel(output, "ContentValidation",
				System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Run Output\\");

		// List<String> attachmentList = new ArrayList<String>();
		attachmentList.add(file);

		mailObj.sendEmail("Content Validation Results", sendReportTo, attachmentList);
	}

	public void mkdir() {
		// Create Required Directories
		File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation",
				"Run Output");
		if (newDirectory.exists() != true) {
			newDirectory.mkdir();
		}
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation", "Screenshots");
		if (newDirectory.exists() != true) {
			newDirectory.mkdir();
		}
		newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation", "Run Output");
		if (newDirectory.exists() != true) {
			newDirectory.mkdir();
		}

	}

	public void take_result() throws Exception {
		File old_file = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Run Output\\",
				comm_obj.generateFileName("ContentValidation") + ".xlsx");
		System.out.println(" task result old : " + old_file);
		if (old_file.exists() == true) {
			// old_file.move(old_file, newName);
			Calendar now = Calendar.getInstance();
			String minute = Integer.toString(now.get(Calendar.MINUTE));
			String hour = Integer.toString(now.get(Calendar.HOUR_OF_DAY));

			String new_file = (System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Run Output\\"
					+ "ContentValidation" + minute + hour);
			old_file.renameTo(new File(comm_obj.generateFileName(new_file)));
			System.out.println(old_file + "replaced with  :" + new_file);
		}
	}

}
