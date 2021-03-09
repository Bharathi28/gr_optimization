package com.sns.gr_optimization.content;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
	String pagepattern;
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
		String[][] JLoshipfreq = null;
		String brandcode = db_obj.get_sourceproductlinecode(brand);
		String currentCategory = "Product";

		// Read All SEO Page
		String[][] seoData = null;
		HashMap<String, String> kit_offerdata_d = null;
		HashMap<String, String> SAS_offerdata = null;
		HashMap<String, String> Checkout_offerdata = null;
		HashMap<String, String> pre_upsell_page = null;
		HashMap<String, String> product_offerdat = null;

		System.out.println(env);
		List<String> category_list = Arrays.asList("Kit".split(","));
		System.out.println(category_list);

		// Read Merchandising Input

		merchData = comm_obj.getExcelData(System.getProperty("user.dir")
				+ "/Input_Output/BuyflowValidation/Merchandising Input/" + brand + "/" + campaigncategory + ".xlsx",
				"Active Campaign", 0);

		if (brand.equalsIgnoreCase("JLoBeauty")) {
			// Read Web Catalog

			// if ((category_list.contains("Product")) ||
			// (category_list.contains("SubscribeandSave"))
			// || (category_list.contains("ShopKit"))) {
			catalogData = comm_obj.getExcelData(
					System.getProperty("user.dir") + "/Input_Output/BuyflowValidation/Merchandising Input/" + brand
							+ "/" + brandcode + " Web Catalog.xlsx",
					"Acq", 0);
			if (brand.equalsIgnoreCase("JLoBeauty")) {
				JLoshipfreq = comm_obj.getExcelData(
						System.getProperty("user.dir") + "/Input_Output/BuyflowValidation/Merchandising Input/" + brand
								+ "/" + brandcode + " Web Catalog.xlsx",
						"Shipping Frequencies", 0);
				// }
			}
		}
		if (brand.equalsIgnoreCase("JLoBeauty") != true) {// if (brand.equalsIgnoreCase("CrepeErase")) {
			// Read SEO Page

			seoData = comm_obj
					.getExcelData(System.getProperty("user.dir") + "/Input_Output/ContentValidation/Content/SEO/"
							+ English + "/src/" + brand + "/" + brand + ".xlsx", brand, 0);

			kit_offerdata_d = content_obj.getProdRowfromCatalog(seoData, "Home page");

			System.out.println("Page Source Data : " + kit_offerdata_d);

		}
		if (brand.equalsIgnoreCase("JLoBeauty")) {
			JLoshipfreq = comm_obj.getExcelData(
					System.getProperty("user.dir") + "/Input_Output/BuyflowValidation/Merchandising Input/" + brand
							+ "/" + brandcode + " Web Catalog.xlsx",
					"Shipping Frequencies", 0);
		}
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

		String postpu = "No";
		String prepu = "No";
		String T_C = "Terms & Conditions";
		String P_P = "Privacy Policy";
		String seo = "SEO";

		// Get Page Source
		if (brand.equalsIgnoreCase("JLoBeauty") != true) { // JLoBeauty CrepeErase
			String pageSource = driver.getPageSource();

			content_obj.write_textfile(pageSource, brand, English, seo, "pageSource", ".txt");

			// System.out.println("Title Tag Data : " + Title_Tag);
			// System.out.println("Title Tag Data : " + Meta_description);
			// System.out.println("Title Tag Data : " + Meta_Name);
			// System.out.println("Title Tag Data : " + Meta_verification);

			// Result_Tag = content_obj.check_seo_content(kit_offerdata_d, "Title Tag",
			// pageSource);
			String Title_Tag_Rs = content_obj.check_seo_content(kit_offerdata_d, "Title Tag", pageSource);
			String Meta_description_Tag_Rs = content_obj.check_seo_content(kit_offerdata_d, "Meta description",
					pageSource);
			String Meta_Name_Tag_Rs = content_obj.check_seo_content(kit_offerdata_d, "Meta Name", pageSource);
			String Meta_Google_Tag_Rs = content_obj.check_seo_content(kit_offerdata_d, "Meta Google-site-verification",
					pageSource);

			output.add(content_obj.add_result(env, brand, campaign, "Homepage : Validate Title Tag ", Title_Tag_Rs));

			output.add(content_obj.add_result(env, brand, campaign, "Homepage : Validate Meta description ",
					Meta_description_Tag_Rs));

			output.add(
					content_obj.add_result(env, brand, campaign, "Homepage : Validate Meta Name ", Meta_Name_Tag_Rs));

			output.add(content_obj.add_result(env, brand, campaign,
					"Homepage : Validate Meta Google-site-verification ", Meta_Google_Tag_Rs));

		}

		// Go to Terms Conditions

		content_obj.clickaelement(driver, env, brand, campaign, "Terms & Conditions");

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
		String result = content_obj.Compare_text_file(path_src, brand, English, T_C, "Content", ".txt");
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

		if (brand.equalsIgnoreCase("MallyBeauty") || brand.equalsIgnoreCase("DrDenese") == true) {
			content_obj.clickaelement_top(driver, env, brand, campaign, "Terms & Conditions Español");

		} else {

			content_obj.clickaelement(driver, env, brand, campaign, "Terms & Conditions Español");
		}

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
		String result_S = content_obj.Compare_text_file(path_src_S, brand, Spanish, T_C, "Content", ".txt");
		output_row.add(result_S);
		output.add(output_row);

		// Click English Version of T & C
		if (brand.equalsIgnoreCase("MallyBeauty") || brand.equalsIgnoreCase("DrDenese") == true) {
			content_obj.clickaelement_top(driver, env, brand, campaign, "Terms & Conditions English");
		} else {
			content_obj.clickaelement(driver, env, brand, campaign, "Terms & Conditions English");
		}
		if (brand.equalsIgnoreCase("JLoBeauty") == true) {

			content_obj.clickaelement(driver, env, brand, campaign, "CloseTermsConditions");
		}

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
		String result_PP = content_obj.Compare_text_file(path_src_PP, brand, English, P_P, "Content", ".txt");
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
		String result_PP_S = content_obj.Compare_text_file(path_src_PP_S, brand, Spanish, P_P, "Content", ".txt");
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

		if (brand.equalsIgnoreCase("DrDenese") != true) {
			output.add(content_obj.Check_Available(driver, env, brand, campaign, "Accessibility Interface",
					"Validate AccessiBe tool "));
			content_obj.clickaelement(driver, env, brand, campaign, "Accessibility Interface");
		}

		//
		if (brand.equalsIgnoreCase("JLoBeauty") != true) {
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

			if (brand.equalsIgnoreCase("JloBeauty") == true) {
				System.out.println(" catalog Data : " + currentCategory);
				// Get the product data from Web Catalog
				HashMap<String, String> product_offerdata = merch_obj.getProdRowfromCatalog(catalogData, ppid,
						currentCategory);

				HashMap<String, String> product_offerdata2 = content_obj.getProdRowfromCatalog(seoData, "SAS page");
				System.out.println(product_offerdata);
				System.out.println(product_offerdata2);

				// Get Shipping Frequency
				HashMap<String, String> product_shippingfrequency = null;

				if (brand.equalsIgnoreCase("JLoBeauty")) {
					product_shippingfrequency = merch_obj.getProdShippingFrequency(JLoshipfreq, ppid);
				}

			}

			// PagePattern for the Current PPID
			pagepattern = kit_offerdata.get("PagePattern").trim();

			// Check Pre-purchase Upsell for the campaign
			if (pagepattern.contains("prepu")) {
				prepu = "Yes";
			} else {
				prepu = "No";
			}

			// Collect current Offer related details from Merchandising Input file
			if (!(brand.equalsIgnoreCase("JloBeauty"))) {
				expectedofferdata_kit = merch_obj.generateExpectedOfferDataForKit(kit_offerdata, PPUSection, postpu,
						ppid, giftppid, brand, campaigncategory);
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
			expectedofferdata_kit.put("Gift PPID", arrOfStr[0]);
			System.out.println("Selected Gift : " + arrOfStr[0]);
			Boolean Title_Tag_result = null;
			if (brand.equalsIgnoreCase("JLoBeauty") != true && prepu.equalsIgnoreCase("Yes")) {
				// Select offer

				// content_obj.select_offer(driver, expectedofferdata_kit, "Kit",
				// pre_upsell_page, seoData);

				if (brand.equalsIgnoreCase("CrepeErase") == true) {
					content_obj.select_fragrance(driver, brand, campaign, expectedofferdata_kit);
				}
				content_obj.select_kit(driver, brand, campaign, expectedofferdata_kit);

				if (pagepattern.contains("gift") == true) {
					System.out.println("pagepattern : " + pagepattern);
//					if ((brand.equalsIgnoreCase("WestmoreBeauty") != true)
//						&& (brand.equalsIgnoreCase("Smileactives") != true)) {
					content_obj.select_gift(driver, brand, campaign, expectedofferdata_kit);
				}

				Title_Tag_result = content_obj.select_prepu(driver, brand, campaign, expectedofferdata_kit,
						pre_upsell_page, seoData);

				if (Title_Tag_result == null) {
					System.out.println("Title Tag Exp : " + null);
					output.add(content_obj.add_result(env, brand, campaign, "Homepage : Validate Title Tag ", null));
				}
				if (Title_Tag_result != null) {
					if (Title_Tag_result == true) {
						System.out.println(" Pre-upsell page : " + pass);
						output.add(content_obj.add_result(env, brand, campaign, "Pre-upsell page : Validate Title Tag ",
								pass));
					}
//					else if (Title_Tag_result == false) {
//						System.out.println("Title Tag Exp : " + fail);
//						output.add(
//								content_obj.add_result(env, brand, campaign, "Homepage : Validate Title Tag ", fail));
//					}
					else {
						System.out.println(" Pre-upsell page : " + null);
						output.add(content_obj.add_result(env, brand, campaign, "Pre-upsell page : Validate Title Tag ",
								null));
					}
				}

//			content_obj.select_kitshade(driver, brand, campaign, expectedofferdata_kit);
//			content_obj.select_giftshade(driver, brand, campaign, expectedofferdata_kit);
//			content_obj.select_product(driver, brand, campaign, expectedofferdata_kit);
//			content_obj.select_shade(driver, brand, campaign, expectedofferdata_kit);
//			content_obj.select_onetime(driver, brand, campaign, expectedofferdata_kit);
//			content_obj.select_subscribe(driver, brand, campaign, expectedofferdata_kit);

				content_obj.add_product_to_cart(driver, brand, campaign, "Kit");
			}

			else

			{
				content_obj.select_offer2(driver, expectedofferdata_kit, "Kit");

			}
			// Check Pre-upsell page &
//		if (brand.equalsIgnoreCase("CrepeErase") && prepu.equalsIgnoreCase("Yes")) {
//			pre_upsell_page = content_obj.getProdRowfromCatalog(seoData, "Pre-upsell page");
//			String pre_upsell_Tag = pre_upsell_page.get("Title Tag");
//
//			System.out.println(prepu);
//			System.out.println(pre_upsell_Tag);
//			System.out.println("URL name : " + driver.getCurrentUrl());
//		}

			// Check DND on SAS Page
			output.add(content_obj.donotsell(driver, env, brand, campaign, "SASPage"));

			if (brand.equalsIgnoreCase("JLoBeauty") != true) {// if (brand.equalsIgnoreCase("CrepeErase")) {

				SAS_offerdata = content_obj.getProdRowfromCatalog(seoData, "SAS page");
				String Title_Tag = null;
				String Meta_description = null;
				// System.out.println("Page Source Data : " + SAS_offerdata);

				String pageSource = driver.getPageSource();

				content_obj.write_textfile(pageSource, brand, English, seo, "pageSource_SAS_Page", ".txt");

				Title_Tag = SAS_offerdata.get("Title Tag");
				Meta_description = SAS_offerdata.get("Meta description");

				System.out.println(" SAS Page Data : " + Title_Tag);
				System.out.println(" SAS Page Data : " + Meta_description);

				if (Title_Tag != null) {
					Boolean Title_Tag_result1 = pageSource.contains(Title_Tag);

					// if (homepage.trim().contains(pageSource.trim())) {
					if (Title_Tag != null) {
						if (Title_Tag_result1 == true) {
							System.out.println(" SAS Page Exp : " + pass);
							output.add(content_obj.add_result(env, brand, campaign, "SAS Page : Validate Title Tag ",
									pass));
						} else {

							System.out.println(" SAS Page Exp : " + fail);
							output.add(content_obj.add_result(env, brand, campaign, "SAS Page : Validate Title Tag ",
									fail));
						}
					} else {
						System.out.println(" SAS Page : Validate Title Tag  : " + null);
						output.add(content_obj.add_result(env, brand, campaign, "SAS Page : Validate Title Tag ",
								"Not Present"));
					}
					if (Meta_description != null) {
						Boolean Meta_description_result = pageSource.contains(Meta_description);
						if (Meta_description_result == true) {
							System.out.println(" SAS Page Exp : " + pass);
							output.add(content_obj.add_result(env, brand, campaign,
									"SAS Page : Validate Meta description ", pass));
						} else {

							System.out.println(" SAS Page Exp : " + fail);
							output.add(content_obj.add_result(env, brand, campaign,
									"SAS Page : Validate Meta description ", fail));
						}
					} else {
						System.out.println(" SAS Page : Validate Title Tag  : " + null);
						output.add(content_obj.add_result(env, brand, campaign, "SAS Page : Validate Meta description ",
								"Not Present"));
					}
				}
				String Title_Tag_Rs = content_obj.check_seo_content(SAS_offerdata, "Title Tag", pageSource);
				output.add(
						content_obj.add_result(env, brand, campaign, "SAS Page : Validate Title Tag ", Title_Tag_Rs));

			}

			// Move to Checkout

			bf_obj.move_to_checkout(driver, brand, campaigncategory, "Kit");

			if (brand.equalsIgnoreCase("JLoBeauty") != true) { // if (brand.equalsIgnoreCase("CrepeErase")) {

				Checkout_offerdata = content_obj.getProdRowfromCatalog(seoData, "Checkout Page");
				String Checkout_Tag = Checkout_offerdata.get("Title Tag");
				String Checkout_MDTag1 = Checkout_offerdata.get("Meta description");
				Thread.sleep(4000);
				String pageSource_Co = driver.getPageSource();

				System.out.println("Checkout_Tag : " + Checkout_MDTag1);

				if (Checkout_Tag != null && Checkout_MDTag1 != null) {
					Boolean Checkout_result = pageSource_Co.contains(Checkout_Tag);
					Boolean CheckoutMD_result = pageSource_Co.contains(Checkout_MDTag1);
					// if (homepage.trim().contains(pageSource.trim())) {
					if (Checkout_result == true) {
						System.out.println(" Checkout Page Exp : " + pass);
						output.add(content_obj.add_result(env, brand, campaign, "Checkout Page : Validate Title Tag ",
								pass));
					} else {

						System.out.println(" Checkout Page Exp : " + fail);
						output.add(content_obj.add_result(env, brand, campaign, "Checkout Page : Validate Title Tag ",
								fail));
					}

					if (CheckoutMD_result == true) {
						System.out.println(" Checkout Page Exp : " + pass);
						output.add(content_obj.add_result(env, brand, campaign,
								"Checkout Page : Validate Meta description ", pass));
					} else {

						System.out.println(" Checkout Page Exp : " + fail);
						output.add(content_obj.add_result(env, brand, campaign,
								"Checkout Page : Validate Meta description ", fail));
					}
				} else {
					output.add(
							content_obj.add_result(env, brand, campaign, "Checkout Page : Validate Title Tag ", null));
					output.add(content_obj.add_result(env, brand, campaign,
							"Checkout Page : Validate Meta description ", null));
				}

			}
			// Check DND on Checkout Page
			output.add(content_obj.donotsell(driver, env, brand, campaign, "CheckoutPage"));

			output.add(content_obj.Check_Available(driver, env, brand, campaign, "Terms & Conditions Checkout",
					"Checkout page : Click open Terms & Conditions popup"));

			content_obj.clickaelement(driver, env, brand, campaign, "Terms & Conditions Checkout");

			output_row.add(remarks);
			// System.out.println("Output row : " + output_row);
			// output.add(output_row);

			// Move - sitemap.xml
			String site = "sitemap.xml";
			String Append_xml = url + "/" + site;
			driver.get(Append_xml);

			output.add(content_obj.Check_Available(driver, env, brand, campaign, site, "Validate sitemap.xml "));

			if (brand.equalsIgnoreCase("JLoBeauty") != true) {// if (brand.equalsIgnoreCase("CrepeErase")) {
				// Compare sitemap.xml Text
				output_row = new ArrayList<String>();
				output_row.add(env);
				output_row.add(brand);
				output_row.add(campaign);
				output_row.add("Validate sitemap.xml Data ");
				String sitemap = content_obj.get_String(driver, env, brand, campaign, "sitemap.xml data");
				content_obj.write_textfile(sitemap, brand, English, seo, "Sitemap", ".xml");
				File path_sitemap = new File(
						System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\" + seo + "\\"
								+ English + "\\src\\" + brand + "\\" + "Sitemap.xml");
				String result_sitemap = content_obj.Compare_text_file(path_sitemap, brand, English, seo, "Sitemap",
						".xml");

				if (result_sitemap == null) {

					output_row.add(pass);
				} else {
					output_row.add(fail);
				}
				output_row.add(result_sitemap);
				output.add(output_row);
			}

//		HashMap<String, String> robots = content_obj.getSourceCodeInfo(seoData, brand);
//		System.out.println(robots);

			// Move - robots.txt
			String Append_rb = null;
			String txt = "robots.txt";
			if (campaign.equalsIgnoreCase("Core") == false) {
				String[] up_url = url.split("/", 4);
				String up = up_url[0] + "/" + up_url[1] + up_url[2];
				Append_rb = up + "/" + txt;
			} else {
				Append_rb = url + "/" + txt;
			}
			System.out.println("URL : " + Append_rb);
			driver.get(Append_rb);

			output.add(content_obj.Check_Available(driver, env, brand, campaign, txt, "Validate Robots.txt "));

			if (brand.equalsIgnoreCase("JLoBeauty") != true) {// if (brand.equalsIgnoreCase("CrepeErase")) {
				// Compare Robots Text
				output_row = new ArrayList<String>();
				output_row.add(env);
				output_row.add(brand);
				output_row.add(campaign);
				output_row.add("Validate Robots.txt Data ");
				String robots = content_obj.get_String(driver, env, brand, campaign, txt);
				content_obj.write_textfile(robots, brand, English, seo, "Robotx_src", ".txt");
				File path_robots = new File(
						System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\" + seo + "\\"
								+ English + "\\src\\" + brand + "\\" + "Robotx_src.txt");
				String result_robots = content_obj.Compare_text_file(path_robots, brand, English, seo, "Robotx",
						".txt");
				if (result_robots == null) {

					output_row.add(pass);
				} else {
					output_row.add(fail);
				}
				output_row.add(result_robots);
				output.add(output_row);
			}
		}

		// JLO

		else {

			System.out.println(url);
			driver.get(url);

			String category = "SubscribeandSave";
			ppid = "JL2A0136";
			giftppid = "30 Day";

			// Get the product data from Web Catalog
			HashMap<String, String> product_offerdata = merch_obj.getProdRowfromCatalog(catalogData, ppid,
					currentCategory);
			System.out.println(product_offerdata);

			// Get Shipping Frequency
			HashMap<String, String> product_shippingfrequency = null;

			if (brand.equalsIgnoreCase("JLoBeauty")) {
				product_shippingfrequency = merch_obj.getProdShippingFrequency(JLoshipfreq, ppid);
			}

			// Get Price Book IDs
			LinkedHashMap<String, String> catalogPriceBookIDs = merch_obj.getCatalogPriceBookIDs(catalogData);

			// Check if the PPID is present in the campaign
			if (product_offerdata.size() == 0) {
				remarks = remarks + ppid + " doesn't exist in the Shop page of " + brand + " - " + campaigncategory;
				// continue;
			}

			// Check if Post Purchase Upsell Page exists
			if (currentCategory.equalsIgnoreCase("SubscribeandSave")) {
				postpu = merch_obj.checkShopPostPU(product_offerdata, brand);
			}

			// Collect current Offer related details from Merchandising Input file
			expectedofferdata_product = merch_obj.generateExpectedOfferDataForProduct(product_offerdata,
					product_shippingfrequency, ppid, giftppid, postpu, brand, campaigncategory, currentCategory,
					catalogPriceBookIDs);
			System.out.println(expectedofferdata_product);

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
			List<String> supplysize_list = new ArrayList<String>();
			List<String> offer_postpurchase_list = new ArrayList<String>();
			String jloShippingSelect = "";

			supplysize_list.add(expectedofferdata_product.get("SupplySize"));
			offer_postpurchase_list.add(expectedofferdata_product.get("Offer Post-Purchase"));
//			if(currentCategory.equalsIgnoreCase("Product")) {
			subtotal_list_forshippingcalc.add(expectedofferdata_product.get("Price"));
//			}								 

			String[] jloShippingOptions = { "Free Shipping", "Two Day Shipping" };
			int rnd = new Random().nextInt(jloShippingOptions.length);
			jloShippingSelect = jloShippingOptions[rnd];

			String shipping_calc = merch_obj.calculateShippingforProduct(brand, subtotal_list_forshippingcalc,
					jloShippingSelect, currentCategory, category_list);
			shipping_list.add(shipping_calc);

			if ((currentCategory.equalsIgnoreCase("SubscribeandSave"))
					|| (currentCategory.equalsIgnoreCase("ShopKit"))) {
				renewal_plan_list.add(expectedofferdata_product.get("Renewal Plan Id"));
			}

			pricebook_id_list.add(expectedofferdata_product.get("Price Book Id"));

			// Move to Shop

			bf_obj.click_cta(driver, brand, campaign, "Shop");

			// Select offer

			sas_obj.select_offer(driver, expectedofferdata_product, currentCategory);

			// Move to Checkout

			bf_obj.move_to_checkout(driver, brand, campaigncategory, currentCategory);
		}
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
