package com.sns.gr_optimization.cxt;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.MapDifference.ValueDifference;
//import com.sns.gr_optimization.setup.Authentication;
import com.sns.gr_optimization.testbase.CommonUtilities;
import com.sns.gr_optimization.testbase.MailUtilities;

import io.restassured.path.xml.XmlPath;

public class CDKValidation {
	
	CommonUtilities comm_obj = new CommonUtilities();
	MailUtilities mailObj = new MailUtilities();
//	Authentication auth_obj = new Authentication();
	
	List<List<String>> output = new ArrayList<List<String>>();
	
	String sendReportTo = "manibharathi@searchnscore.com";
	
	// Sharepoint Authentication
//	String access_token = auth_obj.SharepointAuthentication();	
	
	@DataProvider(name="brands")
	public Object[][] testData() throws Exception {		
	
		Object[][] arrayObject = {{"MeaningfulBeauty"}, {"CrepeErase"}};	
		return arrayObject;
	}	
	
	@Test(dataProvider="brands")
	public void CDK(String brand) throws IOException, InterruptedException {		
		
		File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\CDKValidation", "CDK_Input");
		newDirectory.mkdir();
		
		String brandcode = "";
		if(brand.equalsIgnoreCase("MeaningfulBeauty")) {
			brandcode = "MT";
		}
		else if(brand.equalsIgnoreCase("CrepeErase")) {
			brandcode = "CS";
		}	
		
		String download_path = System.getProperty("user.dir") + "\\Input_Output\\CDKValidation\\CDK_Input\\";
		
		//Download CDK Data	
//		auth_obj.sharepointFileDownload(access_token, "CDK%20Data%20Repo/", "Customer Data_All Brands.xlsx", download_path);
//		auth_obj.sharepointFileDownload(access_token, "CDK%20Data%20Repo/", brand + "_CDK_Pricing.xlsx", download_path);
		Thread.sleep(3000);
		
		String[][] CustomerData  = comm_obj.getExcelData(System.getProperty("user.dir") + "\\Input_Output\\CDKValidation\\CDK_Input\\Customer Data_All Brands.xlsx", brand, 0);
		int iterator = 0;
		int customercolumn = 0;
		
		for(int j=0; j<CustomerData[0].length; j++) {
			if(CustomerData[iterator][j].equalsIgnoreCase("CustomerNumber")) {
				customercolumn = j;
			}
		}
		
		for(int i=1; i<CustomerData.length-1; i++) {
			
			String customernumber = CustomerData[i][customercolumn];
			
			Double custnum_double = Double.valueOf(customernumber);	
			double roundOff_custnum = Math.floor(custnum_double * 10.0) / 10.0;
			int roundOff_value_custnum = (int)roundOff_custnum;
			
			customernumber = String.valueOf(roundOff_value_custnum);
			System.out.println();
			String requestURL = "https://api.oh.ocx-int.com/customer/info/account/" + customernumber + "/brand/" + brandcode + "/uid/dad23/trans/EWR23q/searchall/true/siteid/" + brandcode + "-US-LEN";
			System.out.println(requestURL);
			String response = given()
					.when()
						.get(requestURL)
					.then()
						.assertThat()
						.statusCode(200)
						.extract().response().asString();
			
			System.out.println(response);
				
			List<String> output_row = new ArrayList<String>();
			output_row.add("PROD");
			output_row.add(brand);
			output_row.add(customernumber);
				
			XmlPath xp = new XmlPath(response);
				
			String cdkname = xp.getString("customerResponse.responseData.customer.kitCustomization.offers.offer.customizationOfferCode");
			output_row.add(cdkname);
			System.out.println("CDK " + cdkname);
			if(cdkname.equalsIgnoreCase("")) {
				output.add(output_row);
				continue;
			}
				
			int size = xp.getInt("customerResponse.responseData.customer.kitCustomization.priceList.product.size()");
				
			HashMap<String, Double> Actual_productMap = new HashMap<String, Double>();
			for(int k=0; k<size; k++) {
					
				String ppid = xp.getString("customerResponse.responseData.customer.kitCustomization.priceList.product[" + k + "].ppid");
				String price =  xp.getString("customerResponse.responseData.customer.kitCustomization.priceList.product[" + k + "].price");
					
				Double actual_price = Double.valueOf(price);							
				Actual_productMap.put(ppid, actual_price);			
			}		
				
			String[][] CDKData  = comm_obj.getExcelData(System.getProperty("user.dir") + "\\Input_Output\\CDKValidation\\CDK_Input\\" + brand + "_CDK_Pricing.xlsx", cdkname, 0);
			int first_row_iterator = 0;
			int ppidcolumn = 0;
			int pricecolumn = 0;
			for(int j=0; j<CDKData[0].length; j++) {
				if((CDKData[first_row_iterator][j].equalsIgnoreCase("ItemCode")) || (CDKData[first_row_iterator][j].equalsIgnoreCase("Product Code"))) {
					ppidcolumn = j;
				}
				if(CDKData[first_row_iterator][j].equalsIgnoreCase("Price")) {
					pricecolumn = j;
				}
			}
				
			HashMap<String, Double> Expected_productMap = new HashMap<String, Double>();
			for(int k=1; k<CDKData.length-1; k++) {
							
				String ppid = CDKData[k][ppidcolumn];
				String price = CDKData[k][pricecolumn];
						
				Double expected_price = Double.valueOf(price);							
				Expected_productMap.put(ppid, expected_price);
			}		

			// Get extra elements in Expected - CDK Sheet
			HashSet<String> unionKeys1 = new HashSet<>(Actual_productMap.keySet());
			unionKeys1.addAll(Expected_productMap.keySet());		 
			unionKeys1.removeAll(Actual_productMap.keySet());		
			
			// Get extra elements in API
			HashSet<String> unionKeys2 = new HashSet<>(Expected_productMap.keySet());
			unionKeys2.addAll(Actual_productMap.keySet());				 
			unionKeys2.removeAll(Expected_productMap.keySet());		
			
			HashMap<String, Double> Filtered_Actual_productsMap = new HashMap<String, Double>();
			Filtered_Actual_productsMap = Actual_productMap;
			Filtered_Actual_productsMap.keySet().removeAll(unionKeys2);
					
			HashMap<String, Double> Filtered_Expected_productsMap = new HashMap<String, Double>();
			Filtered_Expected_productsMap = Expected_productMap;
			Filtered_Expected_productsMap.keySet().removeAll(unionKeys1);			
			
			String remarks = "";
				
			MapDifference<String, Double> mapDifference = Maps.difference(Filtered_Actual_productsMap, Filtered_Expected_productsMap);
			if((unionKeys1.size() == 0) && (unionKeys2.size() == 0) && (Filtered_Expected_productsMap.equals(Filtered_Actual_productsMap))) {
				System.out.println(cdkname + " : PASS");
				output_row.add("PASS");
			}
			else {
				System.out.println(cdkname + " : FAIL");
				output_row.add("FAIL");
				if(!(Filtered_Expected_productsMap.equals(Filtered_Actual_productsMap))) {						
					Map<String, ValueDifference<Double>> Difference_Map = mapDifference.entriesDiffering();
					
					for(Entry<String, ValueDifference<Double>> entry : Difference_Map.entrySet()) {
						remarks = remarks + "Price mismatch for " + entry.getKey() + ": Expected - " + entry.getValue().rightValue() + ", Actual -" + entry.getValue().leftValue() + " ; ";
					}
				}
				output_row.add(remarks);
				
				if(unionKeys1.size() != 0) {
					output_row.add(unionKeys1.toString());
				}
				if(unionKeys2.size() != 0) {
					output_row.add(unionKeys2.toString());
				}
			}			
			output.add(output_row);
		}		
	}
	
	@AfterSuite
	public void populateExcel() throws IOException {
		
		comm_obj.deleteDirectory(new File(System.getProperty("user.dir") + "\\Input_Output\\CDKValidation\\CDK_Input"));
		
		File newDirectory = new File(System.getProperty("user.dir") + "\\Input_Output\\CDKValidation", "Run_Output");
		newDirectory.mkdir();
		String file = comm_obj.populateOutputExcel(output, "CDKValidationResults", System.getProperty("user.dir") + "\\Input_Output\\CDKValidation\\Run_Output\\");
		
		List<String> attachmentList = new ArrayList<String>();
		attachmentList.add(file);		
		
		mailObj.sendEmail("CDK Validation Results", sendReportTo, attachmentList);
	}
}