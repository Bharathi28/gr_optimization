package com.sns.gr_optimization.testbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CommonUtilities {
	
	public String[][] getExcelData(String fileName, String sheetName, int startrow) {
		String[][] arrayExcelData = null;
		try {			
			File input_file = new File(fileName);
			
			FileInputStream inputstream = new FileInputStream(input_file);
			Workbook testData = new XSSFWorkbook(inputstream);
			Sheet dataSheet = testData.getSheet(sheetName);
			
			int totalNoOfCols = dataSheet.getRow(0).getLastCellNum();
			int end = 0;		
			
			//////////////////////////////////////
			int k = startrow;			
			String rowdata = dataSheet.getRow(k).getCell(0).getStringCellValue();
			while(!(rowdata.equalsIgnoreCase("End"))) {
				k++;
				if(dataSheet.getRow(k) == null) {
					continue;
				}
				else {
					if(dataSheet.getRow(k).getCell(0) == null) {
						continue;
					}
				}
				String cellType = dataSheet.getRow(k).getCell(0).getCellTypeEnum().toString();
				if(cellType.equalsIgnoreCase("STRING")) {
					rowdata =  dataSheet.getRow(k).getCell(0).getStringCellValue();
				}
				else if(cellType.equalsIgnoreCase("NUMERIC")) {
					Double value = dataSheet.getRow(k).getCell(0).getNumericCellValue();
					rowdata = Double.toString(value);
				}
				
//				rowdata =  dataSheet.getRow(k).getCell(0).getStringCellValue();
			}
			int totalNoOfRows = k;
			
			if(startrow == 0) {
				totalNoOfRows = totalNoOfRows+1;
			}
			else if(startrow == 1){
				totalNoOfRows = totalNoOfRows-1;
			}
			
			//////////////////////////////////////
						
			arrayExcelData = new String[totalNoOfRows][totalNoOfCols];
			
			int startarray = 0;
			
			for (int i= startrow ; i <= totalNoOfRows; i++) {
				for (int j=0; j < totalNoOfCols; j++) {
					if(dataSheet.getRow(i) == null) {
						continue;
					}
					else {
						if(dataSheet.getRow(i).getCell(j) == null) {
							continue;
						}
					}
					String cellType = dataSheet.getRow(i).getCell(j).getCellTypeEnum().toString();
					
					if(cellType.equalsIgnoreCase("STRING")) {
						arrayExcelData[startarray][j] = dataSheet.getRow(i).getCell(j).toString();
						if(arrayExcelData[startarray][j].equalsIgnoreCase("End")) {
							end = 1;
							break;
						}
					}
					else if(cellType.equalsIgnoreCase("NUMERIC")) {
						Double value = dataSheet.getRow(i).getCell(j).getNumericCellValue();
						arrayExcelData[startarray][j] = Double.toString(value);
					}
//					System.out.println(arrayExcelData[startarray][j]);
				}
				startarray++;
				if(end == 1) {
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return arrayExcelData;
	}
	
	public String populateOutputExcel(List<List<String>> output, String fileName, String filePath) throws IOException {
		
		File file = new File(filePath + generateFileName(fileName) + ".xlsx");
		XSSFWorkbook workbook = null;
		// Check file existence 
	    if (file.exists() == false) {
	        // Create new file if it does not exist
	        workbook = new XSSFWorkbook();
	    } 
	    else {
	        FileInputStream inputStream = new FileInputStream(new File(filePath + generateFileName(fileName) + ".xlsx"));
	        workbook = new XSSFWorkbook(inputStream);
	    }
	    
		XSSFSheet resultSheet = null;
		
		for(List<String> row : output) {
			String brand = row.get(1);
			String campaign = row.get(2);
			
			// Check if the workbook is empty or not
		    if (workbook.getNumberOfSheets() != 0) {
		    	String sheetExists = checkSheetExists(workbook, brand);
		    	if(sheetExists.equalsIgnoreCase("true")) {
		    		resultSheet = workbook.getSheet(brand);
		    	}
		    	else {
		    		resultSheet = workbook.createSheet(brand);
		    		resultSheet = setHeader(resultSheet, fileName);
		    	}
		    }
		    else {
		    	resultSheet = workbook.createSheet(brand);
		    	resultSheet = setHeader(resultSheet, fileName);
		    }
		    
		    int lastRowNum = resultSheet.getLastRowNum();
		    int newRowNum = lastRowNum + 1;
			XSSFRow newRow = resultSheet.createRow(newRowNum);
			for(int j=0; j<row.size(); j++) {
				Cell cell = newRow.createCell(j);
				cell.setCellValue(row.get(j));
			}
			
			int col_count = output.get(0).size();
			for(int columnIndex = 0; columnIndex < col_count; columnIndex++) {
				resultSheet.autoSizeColumn(columnIndex);
			}
		}
		FileOutputStream outputStream = new FileOutputStream(new File(filePath + generateFileName(fileName) + ".xlsx"));
	    workbook.write(outputStream);
	    workbook.close();
	    outputStream.close();
	    System.out.println(generateFileName(fileName) + ".xlsx written successfully");
	    
	    return filePath + generateFileName(fileName) + ".xlsx";
	}
	
	public XSSFSheet setHeader(XSSFSheet resultSheet, String header){
		List<String> header_list = new ArrayList<String>();
		
		if(header.toLowerCase().contains("buyflow")) {		
			header_list.add("Environment");
			header_list.add("Brand");
			header_list.add("Campaign");
			header_list.add("Category");
			header_list.add("e-mail");
			header_list.add("PPID");			
			header_list.add("Confirmation Number");
			header_list.add("Entry Pricing");	
			header_list.add("Continuity Pricing");
			header_list.add("Renewal Plan");	
			header_list.add("Installment Plan");
			header_list.add("Cart Language");
			header_list.add("Supplemental Cart Language");
			header_list.add("Media Id");	
			header_list.add("Creative Id");
			header_list.add("Venue Id");
			header_list.add("Price Book Id");
			header_list.add("Shipping Billing");
			header_list.add("Payment Method");
			header_list.add("Browser");
			header_list.add("Remarks");
		}	
				
		XSSFRow firstRow = resultSheet.createRow(0);
		for(int j=0; j<header_list.size(); j++) {
			Cell cell = firstRow.createCell(j);
			cell.setCellValue(header_list.get(j));
		}
		return resultSheet;
	}
	
	public String checkSheetExists(XSSFWorkbook workbook, String sheetName) {
		String sheetExists = "false";
		int noOfSheets = workbook.getNumberOfSheets();
		for(int i=0; i<noOfSheets; i++) {
			if (workbook.getSheetName(i).equalsIgnoreCase(sheetName)) {
				sheetExists = "true";
				break;
			}
		}
		return sheetExists;
	}
	
	public String generateFileName(String fileName) {
		Calendar now = Calendar.getInstance();		
		String monthStr = Integer.toString(now.get(Calendar.MONTH) + 1); // Note: zero based!
		String dayStr = Integer.toString(now.get(Calendar.DAY_OF_MONTH));  
		String yearStr = Integer.toString(now.get(Calendar.YEAR));
		
		String filename = fileName + "_" + monthStr + dayStr + yearStr;
		return filename;
	}
	
	public WebElement find_webelement(WebDriver driver, String elementlocator, String elementvalue) {
		WebElement element = null;
		switch(elementlocator){  
	    	case "id":
	    		element = driver.findElement(By.id(elementvalue));
	    		break;  
	    	case "name":
	    		element = driver.findElement(By.name(elementvalue));
	    		break;  
	    	case "xpath":
	    		element = driver.findElement(By.xpath(elementvalue));
	    		break;
	    	case "classname":
	    		element = driver.findElement(By.className(elementvalue));
	    		break;
	    	case "cssselector":
	    		element = driver.findElement(By.cssSelector(elementvalue));
	    		break;
	    }
		return element;  
	}
	
	public List<WebElement> find_mulwebelement(WebDriver driver, String elementlocator, String elementvalue) {
		List<WebElement> element = new ArrayList<WebElement>();
		switch(elementlocator){  
	    	case "id":
	    		element = driver.findElements(By.id(elementvalue));
	    		break;  
	    	case "name":
	    		element = driver.findElements(By.name(elementvalue));
	    		break;  
	    	case "xpath":
	    		element = driver.findElements(By.xpath(elementvalue));
	    		break;
	    	case "classname":
	    		element = driver.findElements(By.className(elementvalue));
	    		break;
	    	case "cssselector":
	    		element = driver.findElements(By.cssSelector(elementvalue));
	    		break;
	    }
		return element;  
	}
	
	public String getFromVariableMap(WebDriver driver, String variablename) throws InterruptedException {
		Thread.sleep(2000);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		String datavalue = "";
		if((variablename.equalsIgnoreCase("paymentPlanId")) || (variablename.equalsIgnoreCase("renewalPlanId")) || (variablename.equalsIgnoreCase("pricebookId"))) {
			List<String> value = (List<String>) jse.executeScript("return app.variableMap." + variablename);
			for(String data : value) {
				datavalue = datavalue + data + ",";
			}
		}
		else {
			datavalue = (String) jse.executeScript("return app.variableMap." + variablename);
		}		
		if(datavalue == null) {
			datavalue = "null";
		}
		return datavalue;
	}
	
	public String getPagePattern(String brand, String campaign) throws IOException {
		String pagepattern = "";
		File input_file = new File(System.getProperty("user.dir")+"/Input_Output/BuyflowValidation/Merchandising Input/" + brand + ".xlsx");
		
		FileInputStream inputstream = new FileInputStream(input_file);
		Workbook testData = new XSSFWorkbook(inputstream);
		Sheet dataSheet = testData.getSheet("PagePattern");
		
		int end = 0;		
		
		//////////////////////////////////////
		int k = 1;
		String rowdata = dataSheet.getRow(k).getCell(0).getStringCellValue();
		while(!(rowdata.equalsIgnoreCase("End"))) {
			if(rowdata.equalsIgnoreCase(campaign)) {
				pagepattern = dataSheet.getRow(k).getCell(1).getStringCellValue();
				break;
			}
			k++;
			rowdata =  dataSheet.getRow(k).getCell(0).getStringCellValue();
		}
		return pagepattern;
	}
	
	public void waitUntilElementAppears(WebDriver driver, String xpath) {
		while(driver.findElements(By.xpath(xpath)).size() == 0) {
			// Do Nothing
		}
	}
	
	public void checkPageIsReady(WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor)driver;
		//Initially below given if condition will check ready state of page.
		if (js.executeScript("return document.readyState").toString().equals("complete")){ 
			return; 
		} 
		
		//Check every 1 second (for 25 seconds) if the page has fully loaded
		for (int i=0; i<25; i++){
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {				
			}
			// Try after 1 second
			if (js.executeScript("return document.readyState").toString().equals("complete")){ 
				break;
			}
		}
	}
}
