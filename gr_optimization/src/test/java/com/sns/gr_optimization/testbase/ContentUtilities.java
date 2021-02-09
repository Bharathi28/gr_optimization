package com.sns.gr_optimization.testbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ContentUtilities {

	CommonUtilities comm_obj = new CommonUtilities();
	static final String pass = "PASS";
	static final String fail = "FAIL";
	String Spanish = "Spanish";
	String English = "English";

//	public void select_subscribe(WebDriver driver, String brand, String campaign, HashMap<String, String> offerdata)
//			throws ClassNotFoundException, SQLException, InterruptedException, IOException {
	public String Compare_text_file(File path_src, String brand, String lang, String T_C)
			throws ClassNotFoundException, SQLException, InterruptedException, IOException {

		File path_des = new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Content\\" + T_C
				+ "\\" + lang + "\\des\\" + brand + "\\" + "Content.txt");

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
		// System.out.println("Locator : " + query);
		List<Map<String, Object>> locator = DBLibrary.dbAction("fetch", query);
		return locator;
	}

	public List<String> donotsell(WebDriver driver, String ev, String bnd, String cpg, String Query_Name)
			throws Exception {
		// Check Do Not Sell My Info
		List<String> output_row = new ArrayList<String>();
		output_row = new ArrayList<String>();

		output_row.add(ev);
		output_row.add(bnd);
		output_row.add(cpg);
		output_row.add("Validate Do Not Sell My Info link in " + Query_Name);
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
		js.executeScript("arguments[0].scrollIntoView();", Element);

		kit_elmt_Customer_Service.click();
	}

}
