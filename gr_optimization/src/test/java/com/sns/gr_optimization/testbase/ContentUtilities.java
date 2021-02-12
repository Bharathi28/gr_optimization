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
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
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
		System.out.println(ppidcolumn);

		for (int i = 0; i < catalogData.length; i++) {
			String ppidinrow = "";
			if (ppidinrow != null) {
				ppidinrow = catalogData[i][ppidcolumn];
				System.out.println("ppidinrow :" + ppidinrow);
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

}
