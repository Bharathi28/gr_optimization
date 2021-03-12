package com.sns.gr_optimization.testbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class ContentUtilities {

	CommonUtilities comm_obj = new CommonUtilities();
	BuyflowUtilities bf_obj = new BuyflowUtilities();

	static final String pass = "PASS";
	static final String fail = "FAIL";
	String Spanish = "Spanish";
	String English = "English";

//	public void select_subscribe(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
//			throws ClassNotFoundException, SQLException, InterruptedException, IOException {
	public String Compare_text_file(File path_src, String brand, String lang, String T_C, String filename,
			String format) throws ClassNotFoundException, SQLException, InterruptedException, IOException {

		File path_des = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\" + T_C
				+ "\\" + lang + "\\des\\" + brand + "\\" + filename + format);

		BufferedReader reader1 = new BufferedReader(new FileReader(path_src));
		BufferedReader reader2 = new BufferedReader(new FileReader(path_des));
		String line1 = reader1.readLine();
		String line2 = reader2.readLine();
		boolean areEqual = true;
		String result = null;

		int lineNum = 1;

		while (line1 != null || line2 != null) {
			if (line1 == null || line2 == null) {
				areEqual = false;

				break;
			} else if (!line1.equalsIgnoreCase(line2)) {
				areEqual = false;

				break;
			}

			line1 = reader1.readLine();

			line2 = reader2.readLine();

			lineNum++;
		}

		if (areEqual) {
			// System.out.println("Two files have same content ");
			// result = "Two files have same content ";

		} else {
			System.out.println("Two files have different content. They differ at line " + lineNum);
			result = "Two files have different content. Difference at line " + lineNum + " Expected Results : " + line1
					+ " and Actual Result : " + line2 + " at line ";

			// System.out.println("File1 has " + line1 + " and File2 has " + line2 + " at
			// line " + lineNum);
			// result = "File1 has " + line1 + " and File2 has " + line2 + " at line " +
			// lineNum;
		}

		reader1.close();

		reader2.close();
		return result;
	}

	public void write_text(String W, String brand, String lang, String T_C) {
		try {
			File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\"
					+ T_C + "\\" + lang + "\\src\\" + brand + "\\" + "Content_src.txt"); // src
			FileWriter myWriter = new FileWriter(newDirectory);// "filename.txt"
			myWriter.write(W);
			myWriter.close();
			System.out.println("Successfully wrote Content to the file : " + newDirectory);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	public void write_textfile(String W, String brand, String lang, String T_C, String filename, String format) {
		try {
			File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\"
					+ T_C + "\\" + lang + "\\src\\" + brand + "\\" + filename + format); // "Robotx_src.txt"
			FileWriter myWriter = new FileWriter(newDirectory);// "filename.txt"
			myWriter.write(W);
			myWriter.close();
			System.out.println("Successfully wrote Content to the file : " + newDirectory);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	public List<Map<String, Object>> get_terms_conditions(String brand, String campaign, String step, String offer)
			throws ClassNotFoundException, SQLException {

		String query = "select * from content_locators where ";
		String include_brand = "brand='" + brand + "'";
		String include_campaign = "campaign='" + campaign + "'";
		String include_step = "step='" + step + "'";
		String include_offer = "offer='" + offer + "'";

		if (brand != null) {
			query = query + include_brand;
			if ((campaign != null) || (step != null) || (offer != null)) {
				query = query + " and ";
			}
		}
		if (campaign != null) {
			query = query + include_campaign;
			if ((step != null) || (offer != null)) {
				query = query + " and ";
			}
		}
		if (step != null) {
			query = query + include_step;
			if (offer != null) {
				query = query + " and ";
			}
		}
		if (offer != null) {
			query = query + include_offer;
		}
//		query = query + ";";
		System.out.println("Locator query: " + query);

		List<Map<String, Object>> locator = DBLibrary.dbAction("fetch", query);

		return locator;
	}

	public void upsell_terms_conditions(WebDriver driver, String brand, String campaign, String upsell)
			throws InterruptedException, ClassNotFoundException, SQLException {
		Thread.sleep(4000);
		JavascriptExecutor jse = (JavascriptExecutor) driver;

		List<Map<String, Object>> locator = null;

		locator = get_terms_conditions(brand, campaign, "PostPU", upsell);

		if (locator.size() == 0) {
			locator = get_terms_conditions(brand, null, "PostPU", upsell);
		}

		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();
		jse.executeScript("window.scrollBy(0,350)", 0);
		Thread.sleep(4000);

		comm_obj.find_webelement(driver, elementlocator, elementvalue).click();
	}

	public List<String> donotsell(WebDriver driver, String ev, String bnd, String cpg, String Query_Name)
			throws Exception {
		// Check Do Not Sell My Info
		List<String> output_row = new ArrayList<String>();
		output_row = new ArrayList<String>();

		output_row.add(ev);
		output_row.add(bnd);
		output_row.add(cpg);
		output_row.add(Query_Name + " : Validate Do Not Sell My Info link");
		List<Map<String, Object>> Privacy_Policy_Content_S = null;
		Privacy_Policy_Content_S = get_terms_conditions(bnd, cpg, Query_Name, null);
		String elementvalue_Content_PP_S = Privacy_Policy_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Content_PP_S = Privacy_Policy_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_Content_PP_S = comm_obj.find_webelement(driver, elementlocator_Content_PP_S,
				elementvalue_Content_PP_S);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Content_PP_S);

		try {
			kit_elmt_Content_PP_S.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Content_PP_S)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException("Do Not Sell My Info link not available");
		}

		return output_row;
	}

	public List<String> Check_Available(WebDriver driver, String ev, String bnd, String cpg, String Query_Name,
			String Check) throws Exception {
		List<String> output_row = new ArrayList<String>();
		output_row = new ArrayList<String>();

		output_row.add(ev);
		output_row.add(bnd);
		output_row.add(cpg);
		output_row.add(Check);
		List<Map<String, Object>> Privacy_Policy_Content_S = null;
		Privacy_Policy_Content_S = get_terms_conditions(bnd, cpg, Query_Name, null);
		String elementvalue_Content_PP_S = Privacy_Policy_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Content_PP_S = Privacy_Policy_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_Content_PP_S = comm_obj.find_webelement(driver, elementlocator_Content_PP_S,
				elementvalue_Content_PP_S);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Content_PP_S);

		try {
			kit_elmt_Content_PP_S.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Content_PP_S)).size() != 0) {
				output_row.add(pass);
			} else {
				output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException(Check + " not available");
		}

		return output_row;
	}

	public void clickaelement(WebDriver driver, String ev, String bnd, String cpg, String Query_Name)
			throws Exception, SQLException {
		List<Map<String, Object>> Customer_Service_Content_S = null;
		Customer_Service_Content_S = get_terms_conditions(bnd, cpg, Query_Name, null);
		String elementvalue_Customer_Service = Customer_Service_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Customer_Service = Customer_Service_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_Customer_Service = comm_obj.find_webelement(driver, elementlocator_Customer_Service,
				elementvalue_Customer_Service);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Customer_Service);
		Thread.sleep(1000);

		try {
			kit_elmt_Customer_Service.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Customer_Service)).size() != 0) {
				System.out.println(pass);
				// output_row.add(pass);
			} else {
				System.out.println(fail);
				// output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException(Query_Name + " link not available");
		}
		Thread.sleep(2000);
		WebElement Element = driver.findElement(By.xpath(elementvalue_Customer_Service));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// js.executeScript("arguments[0].scrollIntoView();", Element);
		js.executeScript("window.scrollBy(0,0)", Element);
		Thread.sleep(2000);
		kit_elmt_Customer_Service.click();
	}

	public void check_element(WebDriver driver, String ev, String bnd, String cpg, String Query_Name)
			throws Exception, SQLException {
		List<Map<String, Object>> Customer_Service_Content_S = null;
		Customer_Service_Content_S = get_terms_conditions(bnd, cpg, Query_Name, null);
		String elementvalue_Customer_Service = Customer_Service_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Customer_Service = Customer_Service_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_Customer_Service = comm_obj.find_webelement(driver, elementlocator_Customer_Service,
				elementvalue_Customer_Service);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Customer_Service);
		Thread.sleep(1000);

		try {
			kit_elmt_Customer_Service.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Customer_Service)).size() != 0) {
				System.out.println(pass);
				// output_row.add(pass);
			} else {
				System.out.println(fail);
				// output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException(Query_Name + " link not available");
		}
		Thread.sleep(2000);
		WebElement Element = driver.findElement(By.xpath(elementvalue_Customer_Service));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// js.executeScript("arguments[0].scrollIntoView();", Element);
		js.executeScript("window.scrollBy(0,0)", Element);
		Thread.sleep(2000);

	}

	public void clickaelement_top(WebDriver driver, String ev, String bnd, String cpg, String Query_Name)
			throws Exception, SQLException {
		List<Map<String, Object>> Customer_Service_Content_S = null;
		Customer_Service_Content_S = get_terms_conditions(bnd, cpg, Query_Name, null);
		String elementvalue_Customer_Service = Customer_Service_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Customer_Service = Customer_Service_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_Customer_Service = comm_obj.find_webelement(driver, elementlocator_Customer_Service,
				elementvalue_Customer_Service);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Customer_Service);
		Thread.sleep(1000);

		try {
			kit_elmt_Customer_Service.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Customer_Service)).size() != 0) {
				System.out.println(pass);
				// output_row.add(pass);
			} else {
				System.out.println(fail);
				// output_row.add(fail);
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException(Query_Name + " link not available");
		}
		Thread.sleep(2000);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		// js.executeScript("arguments[0].scrollIntoView();", Element);
		js.executeScript("window.scrollBy(0,0)", 0);
		// System.out.println(kit_elmt_Customer_Service);
		Thread.sleep(2000);
		kit_elmt_Customer_Service.click();
	}

	public String get_String(WebDriver driver, String ev, String bnd, String cpg, String Query_Name) throws Exception {
		String output_row = null;
		List<Map<String, Object>> Privacy_Policy_Content_S = null;
		Privacy_Policy_Content_S = get_terms_conditions(bnd, cpg, Query_Name, null);
		String elementvalue_Content_PP_S = Privacy_Policy_Content_S.get(0).get("ELEMENTVALUE").toString();
		String elementlocator_Content_PP_S = Privacy_Policy_Content_S.get(0).get("ELEMENTLOCATOR").toString();

		WebElement kit_elmt_robot = comm_obj.find_webelement(driver, elementlocator_Content_PP_S,
				elementvalue_Content_PP_S);
		comm_obj.waitUntilElementAppears(driver, elementvalue_Content_PP_S);

		try {
			kit_elmt_robot.isDisplayed();
			if (driver.findElements(By.xpath(elementvalue_Content_PP_S)).size() != 0) {
				output_row = kit_elmt_robot.getText();
			} else {
				output_row = "No Data found";
			}

		} catch (NoSuchElementException e) {
			throw new RuntimeException(" not available");
		}

		return output_row;
	}

	// public HashMap<String, String> getProdRowfromCatalog(String[][] catalogData,
	// String ppid) {
	public HashMap<String, String> getProdRowfromCatalog(String[][] catalogData, String ppid) {
		LinkedHashMap<String, String> productdata = new LinkedHashMap<String, String>();

		int ppidcolumn = 0; // --->> row

		for (int i = 0; i < catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			String colName2 = colName.trim();
			if ((colName2 != null) && (colName2.equalsIgnoreCase("Page Name") && (colName2.contains("Page Name")))) {
				ppidcolumn = i;
			}
		}
		// System.out.println(ppidcolumn);

		for (int i = 0; i < catalogData.length; i++) {
			String ppidinrow = "";
			if (ppidinrow != null) {
				ppidinrow = catalogData[i][ppidcolumn];
				// System.out.println("ppidinrow :" + ppidinrow);
				// ppidinrow = catalogData[i][ppidcolumn].replaceAll("\\s+", "");
			}

			// System.out.println("Test : " + ppidinrow + " " + ppid);//
			// System.out.println("catalogData 1 : " + catalogData[0][1] + " " +
			// catalogData[i][1]);
			if (ppidinrow != null) {
				if (ppidinrow.trim().equalsIgnoreCase(ppid.trim())) {
					for (int j = 0; j < catalogData[0].length; j++) {
						// if (catalogData[0][j] != null) {
						productdata.put(catalogData[0][j].trim(), catalogData[i][j]);
						// System.out.println("catalogData 2 : " + catalogData[0][j] + " " +
						// catalogData[i][j]);
						// }
					}
					break;
				}

			}

			// }
		}
		// System.out.println("productdata : " + productdata);
		return productdata;

	}

	public List<String> add_result(String EV, String BD, String CG, String Validate_Off, String Result) {
		// Bind all result together
		List<String> output_row = new ArrayList<String>();
		output_row = new ArrayList<String>();
		output_row.add(EV);
		output_row.add(BD);
		output_row.add(CG);
		output_row.add(Validate_Off);
		output_row.add(Result);
		return output_row;
	}

	public void select_offer(WebDriver driver, HashMap<String, String> offerdata, String category,
			HashMap<String, String> pre_upsell_page, String[][] seoData)
			throws ClassNotFoundException, SQLException, InterruptedException {
//		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,0)", 0);

		String brand = offerdata.get("Brand");
		String campaign = offerdata.get("Campaign");

		String[] patternarr = offerdata.get("PagePattern").split("-");
		for (String pattern : patternarr) {
			switch (pattern) {
			case "kit":
				select_kit(driver, brand, campaign, offerdata);
				break;
			case "gift":
				select_gift(driver, brand, campaign, offerdata);
				break;
			case "prepu":
				select_prepu(driver, brand, campaign, offerdata, pre_upsell_page, seoData);
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

	public void select_offer2(WebDriver driver, HashMap<String, String> offerdata, String category)
			throws ClassNotFoundException, SQLException, InterruptedException {
//		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,0)", 0);

		String brand = offerdata.get("Brand");
		String campaign = offerdata.get("Campaign");

		String[] patternarr = offerdata.get("PagePattern").split("-");
		for (String pattern : patternarr) {
			switch (pattern) {
			case "kit":
				select_kit(driver, brand, campaign, offerdata);
				break;
			case "gift":
				select_gift(driver, brand, campaign, offerdata);
				break;
			case "prepu":
				select_prepu2(driver, brand, campaign, offerdata);
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

	public void add_product_to_cart(WebDriver driver, String brand, String campaign, String category)
			throws InterruptedException {
		Thread.sleep(2000);
		System.out.println("Adding product to cart");
		if ((category.equalsIgnoreCase("Product")) || (category.equalsIgnoreCase("ShopKit"))
				|| (category.equalsIgnoreCase("SubscribeandSave"))) {
//			if(brand.equalsIgnoreCase("JLoBeauty")) {
//				if(driver.findElements(By.xpath("//a[@class='button mini-cart-link-checkout small-12']")).size() == 0) {
			Thread.sleep(3000);
			driver.findElement(By.xpath("//button[@id='add-to-cart']")).click();
//				}
//			}
		}
	}

	public void select_kit(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String kitname = offerdata.get("Kit Name");

		List<Map<String, Object>> locator = null;

		locator = bf_obj.get_element_locator(brand, campaign, "Kit", kitname);
		if (locator.size() == 0) {
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

	public void select_gift(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		moveto_gift(driver, brand, campaign);

		JavascriptExecutor jse = (JavascriptExecutor) driver;

		String giftppid = offerdata.get("Gift PPID");

		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign
				+ "' and step='Gift' and offer like '%" + giftppid + "%'";
		List<Map<String, Object>> giftloc = DBLibrary.dbAction("fetch", query);
		WebElement gift_elmt = comm_obj.find_webelement(driver, giftloc.get(0).get("ELEMENTLOCATOR").toString(),
				giftloc.get(0).get("ELEMENTVALUE").toString());
		comm_obj.waitUntilElementAppears(driver, giftloc.get(0).get("ELEMENTVALUE").toString());
		gift_elmt.click();
		Thread.sleep(1000);
	}

	public void moveto_gift(WebDriver driver, String brand, String campaign) throws InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;

		if (brand.equalsIgnoreCase("CrepeErase")) {
			if (campaign.equalsIgnoreCase("Core")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			} else if (campaign.equalsIgnoreCase("cscb1")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			}
		} else if (brand.equalsIgnoreCase("MeaningfulBeauty")) {
			if (campaign.equalsIgnoreCase("deluxe20off")) {
				jse.executeScript("window.scrollBy(0,800)", 0);
			} else if (campaign.equalsIgnoreCase("Core")) {
				jse.executeScript("window.scrollBy(0,800)", 0);
			} else if (campaign.equalsIgnoreCase("friend50poff")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			} else if (campaign.equalsIgnoreCase("trymbnow")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			} else if (campaign.equalsIgnoreCase("special-offer-panelc")) {
				jse.executeScript("window.scrollBy(0,700)", 0);
			}
		}
		Thread.sleep(4000);
	}

	public Boolean select_prepu(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata,
			HashMap<String, String> pre_upsell_page, String[][] seoData)
			throws ClassNotFoundException, SQLException, InterruptedException {

		Boolean Title_Tag_result = null;
		moveto_prepu(driver, brand, campaign);
		Thread.sleep(1000);

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,200)", 0);

		String prepu = offerdata.get("Offer Pre-Purchase");

		List<Map<String, Object>> locator = null;

		locator = bf_obj.get_element_locator(brand, campaign, "PrePU", prepu);
		if (locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, "PrePU", prepu);
		}

		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();

		Thread.sleep(5000);
		String pageSource = driver.getPageSource();
		// Check Pre-upsell page &

		pre_upsell_page = getProdRowfromCatalog(seoData, "Pre-upsell page");
		String pre_upsell_Tag = pre_upsell_page.get("Title Tag");
		System.out.println("pre_upsell_Tag : " + pre_upsell_Tag);
		if (pre_upsell_Tag != null) {
			Title_Tag_result = pageSource.contains(pre_upsell_Tag);
		} else {
			Title_Tag_result = false;
		}

		// System.out.println(prepu);
		// System.out.println(pre_upsell_Tag);
		// System.out.println("URL name : " + driver.getCurrentUrl() + " Title Name is :
		// " + driver.getTitle());

		Thread.sleep(5000);

		WebElement prepu_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		comm_obj.waitUntilElementAppears(driver, elementvalue);

		prepu_elmt.click();
		Thread.sleep(1000);

		return Title_Tag_result;
	}

	public void select_prepu2(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		moveto_prepu(driver, brand, campaign);
		Thread.sleep(1000);

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,200)", 0);

		String prepu = offerdata.get("Offer Pre-Purchase");

		List<Map<String, Object>> locator = null;

		locator = bf_obj.get_element_locator(brand, campaign, "PrePU", prepu);
		if (locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, "PrePU", prepu);
		}

		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();

		WebElement prepu_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		comm_obj.waitUntilElementAppears(driver, elementvalue);
		prepu_elmt.click();
		Thread.sleep(1000);
	}

	public void moveto_prepu(WebDriver driver, String brand, String campaign)
			throws InterruptedException, ClassNotFoundException, SQLException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,200)", 0);

		List<Map<String, Object>> locator = null;

		locator = bf_obj.get_element_locator(brand, campaign, "MoveToPrePU", null);
		if (locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, "MoveToPrePU", null);
		}

		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();

		if (!(elementvalue.equalsIgnoreCase("n/a"))) {
			WebElement prepu_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
			comm_obj.waitUntilElementAppears(driver, elementvalue);
			prepu_elmt.click();
			Thread.sleep(4000);
		}
	}

	public void select_fragrance(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String fragrance = offerdata.get("Fragrance");

		List<Map<String, Object>> locator = null;

		locator = bf_obj.get_element_locator(brand, campaign, "Fragrance", fragrance);
		if (locator.size() == 0) {
			locator = bf_obj.get_element_locator(brand, null, "Fragrance", fragrance);
		}

		String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
		String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();

		if ((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("order30fshadvanced"))) {
			jse.executeScript("window.scrollBy(0,-300)", 0);
			Thread.sleep(2000);
		}

		WebElement frag_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
		comm_obj.waitUntilElementAppears(driver, elementvalue);

		if (locator.size() != 0) {
			frag_elmt.click();
		}
		Thread.sleep(1000);
	}

	public void select_kitshade(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,100)", 0);

		String kitname = offerdata.get("Kit Name");
		String kitshade = offerdata.get("KitShade");

		if (!(kitshade.equalsIgnoreCase("No"))) {
			List<Map<String, Object>> locator = null;

			locator = bf_obj.get_element_locator(brand, campaign, "KitShade", kitshade + " " + kitname);
			if (locator.size() == 0) {
				locator = bf_obj.get_element_locator(brand, null, "KitShade", kitshade + " " + kitname);
			}

			String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
			String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();

			if (!(elementvalue.equalsIgnoreCase("n/a"))) {
				WebElement shade_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
				comm_obj.waitUntilElementAppears(driver, elementvalue);
				Thread.sleep(2000);
				shade_elmt.click();
				Thread.sleep(1000);
			}
		}
	}

	public void select_giftshade(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);

		String giftshade = offerdata.get("GiftShade");

		if (!(giftshade.equalsIgnoreCase("No"))) {
			List<Map<String, Object>> locator = null;

			locator = bf_obj.get_element_locator(brand, campaign, "GiftShade", giftshade);
			if (locator.size() == 0) {
				locator = bf_obj.get_element_locator(brand, null, "GiftShade", giftshade);
			}

			String elementlocator = locator.get(0).get("ELEMENTLOCATOR").toString();
			String elementvalue = locator.get(0).get("ELEMENTVALUE").toString();

			if (!(elementvalue.equalsIgnoreCase("n/a"))) {
				WebElement shade_elmt = comm_obj.find_webelement(driver, elementlocator, elementvalue);
				comm_obj.waitUntilElementAppears(driver, elementvalue);
				Thread.sleep(3000);
				shade_elmt.click();
				Thread.sleep(1000);
			}
		}
	}

	public void select_product(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);

		String ppid = offerdata.get("Product PPID");
		String name = offerdata.get("Product Name").trim();
		String pagepattern = offerdata.get("PagePattern");

		if (ppid.equalsIgnoreCase("JL2A0136")) {
			driver.findElement(By.xpath("(//a[@class='button-text sd-cta'])[2]")).click();
			Thread.sleep(1000);
		} else {
			String xpath = "";
			if (brand.equalsIgnoreCase("JLoBeauty")) {
				xpath = "(//h3[contains(@class,'product-name')]//a[contains(text(),'" + name + "')])[1]";
			} else if (brand.equalsIgnoreCase("CrepeErase")) {
				xpath = "//h4[contains(@class,'product-name')]//a[contains(text(),'" + name + "')]";
			} else {
				xpath = "//h3[contains(@class,'product-name')]//a[contains(text(),'" + name + "')]";
			}

			while (driver.findElements(By.xpath(xpath)).size() == 0) {
				jse.executeScript("window.scrollBy(0,400)", 0);

				if (driver.findElements(By.xpath(xpath)).size() != 0) {
					break;
				}
			}

			WebElement product_elmt = driver.findElement(By.xpath(xpath));
			comm_obj.waitUntilElementAppears(driver, xpath);
			Thread.sleep(2000);
			product_elmt.click();
		}

		if (brand.equalsIgnoreCase("JLoBeauty")) {
			if (!(pagepattern.contains("shade"))) {
				Thread.sleep(4000);
				driver.findElement(By.xpath("//button[@id='add-cart-modal']")).click();
			}
		}
		Thread.sleep(1000);
	}

	public void select_shade(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("window.scrollBy(0,300)", 0);

		String ppid = offerdata.get("Product PPID");

		if (brand.equalsIgnoreCase("CrepeErase")) {
			Select sel_element = new Select(
					driver.findElement(By.xpath("//ul[@class='variations-section clearfix']//li//div[3]//select")));
			sel_element.selectByVisibleText(offerdata.get("Shade").toString());
		} else {
			String xpath = "";
			if ((brand.equalsIgnoreCase("Smileactives")) || (brand.equalsIgnoreCase("WestmoreBeauty"))
					|| (brand.equalsIgnoreCase("MallyBeauty"))) {
				xpath = "//li[@data-variantid='" + ppid + "']";
			} else {
				xpath = "(//li[@data-variantid='" + ppid + "'])[3]";
			}
			WebElement shade_elmt = driver.findElement(By.xpath(xpath));
			comm_obj.waitUntilElementAppears(driver, xpath);
			Thread.sleep(2000);
			if (!(shade_elmt.getAttribute("class").contains("selected"))) {
				shade_elmt.click();
			}
			Thread.sleep(1000);
		}

		if (brand.equalsIgnoreCase("JLoBeauty")) {
			Thread.sleep(4000);
			driver.findElement(By.xpath("//button[@id='add-cart-modal']")).click();
		}
	}

	public void select_onetime(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);

		String ppid = offerdata.get("Product PPID");

		Thread.sleep(1000);
		if (!(driver.findElement(By.xpath("//input[@name='dwopt_" + ppid + "_entryKit']/..")).getAttribute("class")
				.contains("hide"))) {
			driver.findElement(By.xpath(
					"//input[@name='dwopt_" + ppid + "_entryKit']/../../..//label[contains(@for,'entryKit-one-pay')]"))
					.click();
			Thread.sleep(1000);
		}
	}

	public void select_subscribe(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
			throws ClassNotFoundException, SQLException, InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,100)", 0);

		String ppid = offerdata.get("Product PPID");

		Thread.sleep(1000);
		driver.findElement(By.xpath(
				"//input[@name='dwopt_" + ppid + "_entryKit']/../../..//label[contains(@for,'entryKit-auto-renew')]"))
				.click();
		Thread.sleep(1000);
	}

	public HashMap<String, String> getProdRowfromCatalog_buy(String[][] catalogData, String ppid) {
		LinkedHashMap<String, String> productdata = new LinkedHashMap<String, String>();

		int ppidcolumn = 0;

		System.out.println("catalogData for content data : " + catalogData[0].length);
		for (int i = 0; i < catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			if ((colName != null) && (colName.equalsIgnoreCase("PPID"))) {
				ppidcolumn = i;
			}
		}

		for (int i = 0; i < catalogData.length; i++) {
			String ppidinrow = catalogData[i][ppidcolumn].replaceAll("\\s+", "");

			if (ppidinrow.trim().equalsIgnoreCase(ppid.trim())) {
				for (int j = 0; j < catalogData[0].length; j++) {
					if (catalogData[0][j] != null) {
						if (catalogData[0][j].contains("Acq One Time price")) {
							productdata.put("Acq One Time price", catalogData[i][j]);
						} else if (catalogData[0][j].contains("Subscribe and Save price")) {
							productdata.put("Subscribe and Save price", catalogData[i][j]);
						} else if (catalogData[0][j].contains("Entry-Continuity Pricebook")) {
							productdata.put("Entry-Continuity Pricebook", catalogData[i][j]);
						} else {
							productdata.put(catalogData[0][j].trim(), catalogData[i][j]);
						}
					}
				}
				break;
			}
		}
		return productdata;
	}

	public HashMap<String, String> getProdRowfromCatalog(String[][] catalogData, String ppid, String category) {
		LinkedHashMap<String, String> productdata = new LinkedHashMap<String, String>();

		int ppidcolumn = 0;
		int categorycolumn = 0;
		int contppidcolumn = 0;

		for (int i = 0; i < catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			if (colName != null) {
				if (colName.equalsIgnoreCase("PPID")) {
					ppidcolumn = i;
				}
				if (colName.equalsIgnoreCase("Kit or Single")) {
					categorycolumn = i;
				}
				if (colName.equalsIgnoreCase("Post Purchase PPID")) {
					contppidcolumn = i;
				}
			}
		}

		for (int i = 0; i < catalogData.length; i++) {
			String ppidinrow = catalogData[i][ppidcolumn].replaceAll("\\s+", "");
			String categoryinrow = catalogData[i][categorycolumn].replaceAll("\\s+", "");
			String contppidinrow = null;
			if (catalogData[i][contppidcolumn] != null) {
				contppidinrow = catalogData[i][contppidcolumn].replaceAll("\\s+", "");
			}

			if (category.equalsIgnoreCase("ShopKit")) {
				if (categoryinrow.equalsIgnoreCase("Kit")) {
					if (((ppidinrow != null) && (ppidinrow.trim().equalsIgnoreCase(ppid.trim())))
							|| ((contppidinrow != null) && (contppidinrow.trim().equalsIgnoreCase(ppid.trim())))) {
						for (int j = 0; j < catalogData[0].length; j++) {
							if (catalogData[0][j] != null) {
								if (catalogData[0][j].contains("Entry-Continuity Pricebook")) {
									productdata.put("Entry-Continuity Pricebook", catalogData[i][j]);
								} else {
									productdata.put(catalogData[0][j].trim(), catalogData[i][j]);
								}
							}
						}
						break;
					}
				}
			} else {
				if (categoryinrow.equalsIgnoreCase("Single")) {
					if (((ppidinrow != null) && (ppidinrow.trim().equalsIgnoreCase(ppid.trim())))
							|| ((contppidinrow != null) && (contppidinrow.trim().equalsIgnoreCase(ppid.trim())))) {
						for (int j = 0; j < catalogData[0].length; j++) {
							if (catalogData[0][j] != null) {
								if (catalogData[0][j].contains("Acq One Time price")) {
									productdata.put("Acq One Time price", catalogData[i][j]);
								} else if (catalogData[0][j].contains("Subscribe and Save price")) {
									productdata.put("Subscribe and Save price", catalogData[i][j]);
								} else {
									productdata.put(catalogData[0][j].trim(), catalogData[i][j]);
								}
							}
						}
						break;
					}
				}
			}

//			if(ppidinrow.trim().equalsIgnoreCase(ppid.trim())){
//				for(int j=0; j<catalogData[0].length; j++) {
//					if(catalogData[0][j] != null) {
//						if(catalogData[0][j].contains("Acq One Time price")) {
//							productdata.put("Acq One Time price", catalogData[i][j]);
//						}
//						else if(catalogData[0][j].contains("Subscribe and Save price")) {
//							productdata.put("Subscribe and Save price", catalogData[i][j]);
//						}
//						else if(catalogData[0][j].contains("Entry-Continuity Pricebook")) {
//							productdata.put("Entry-Continuity Pricebook", catalogData[i][j]);
//						}
//						else {
//							productdata.put(catalogData[0][j].trim(), catalogData[i][j]);
//						}							
//					}
//				}
//				break;
//			}
		}
		return productdata;
	}

	public String check_seo_content(HashMap<String, String> kit_offerdata_d, String Tag, String pageSource) {
		Boolean Title_Tag_result = null;
		String Title_Tag = kit_offerdata_d.get(Tag);// "Title Tag");
		String Result = null;

		if (Title_Tag != null) {
			if (Title_Tag != "-") {
				if (pageSource.contains("&amp;")) {
					pageSource = pageSource.replaceAll("&amp;", "&");
					// check_amp(pageSource);
				}
				Title_Tag_result = pageSource.contains(Title_Tag);
				if (Title_Tag_result == true) {
					System.out.println(Tag + " " + pass + " " + Title_Tag_result);
					// content_obj.add_result(env, brand, campaign, "Homepage : Validate Title Tag
					// ", pass);
					Result = pass;
				} else if (Title_Tag_result == false) {

					System.out.println(Tag + " " + fail + Title_Tag + " result : " + Title_Tag_result);
					// content_obj.add_result(env, brand, campaign, "Homepage : Validate Title Tag
					// ", fail);
					Result = fail;

				}

			} else {
				Result = "Not Present";
			}
		} else {
			System.out.println(Tag + fail);
			// content_obj.add_result(env, brand, campaign, "Homepage : Validate Title Tag
			// ", null);
			Result = "Not Present";
		}
		return Result;
	}

	public String check_amp(String str) {
		str = str.replaceAll("&amp;", "&");
		return str;
	}
}
