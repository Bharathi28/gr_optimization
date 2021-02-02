package com.sns.gr_optimization.testbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ContentUtilities {

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
			result = "Two files have different content. They differ at line " + lineNum + " File1 has : " + line1
					+ " and File2 has : " + line2 + " at line ";

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
		System.out.println("Locator : " + query);
		List<Map<String, Object>> locator = DBLibrary.dbAction("fetch", query);
		return locator;
	}

}
