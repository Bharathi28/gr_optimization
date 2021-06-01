package com.sns.gr_optimization.testbase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.collect.MapDifference.ValueDifference;

import de.sstoehr.harreader.HarReader;
import de.sstoehr.harreader.HarReaderException;

public class ContentVerfUtilities {
	
	CommonUtilities comm_obj = new CommonUtilities();
	
	public List<List<String>> generateOutputList(List<List<String>> overallOutput, String env, String brand, String campaign, String testcase, String result, String remarks) {
		
		List<String> output_row = new ArrayList<String>();
		output_row.add(env);
		output_row.add(brand);
		output_row.add(campaign);
		output_row.add(testcase);
		output_row.add(result);
		output_row.add(remarks);
		
		overallOutput.add(output_row);
		return overallOutput;		
	}
	
	public String checkBrokenLinks(List<WebElement> links, String brandURL) {
		
		Iterator<WebElement> it = links.iterator();		
		HttpURLConnection huc = null;
		String url = "";
		int respCode = 200;
		
		String remarks = "";
		
		while(it.hasNext()){            
			url = it.next().getAttribute("href");            
            if(url == null || url.isEmpty()){
            	remarks = remarks + "\"" + url + "\" - URL is either not configured for anchor tag or it is empty ; ";
                continue;
            }            
            if(!url.startsWith(brandURL)){
            	remarks = remarks + "\"" + url + "\" - URL belongs to another domain ; ";
                continue;
            }            
            try {
                huc = (HttpURLConnection)(new URL(url).openConnection());                
                huc.setRequestMethod("HEAD");                
                huc.connect();                
                respCode = huc.getResponseCode();                
                if(respCode >= 400){
                	remarks = remarks + "\"" + url + "\" - Broken link ; ";
                }                    
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return remarks;
	}
	
	public String check_donotsellmyinfo(WebDriver driver, String brand, String page) throws InterruptedException {
		String result;
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		
		Thread.sleep(10000);
		jse.executeScript("window.scrollTo(0, document.body.scrollHeight)", 0);
		Thread.sleep(3000);
		
		WebElement Do_Not_Sell_My_Info;
		if(brand.equalsIgnoreCase("Smileactives")) {
			Do_Not_Sell_My_Info = driver.findElement(By.xpath("(//a[text()='DO NOT SELL MY INFO'])[1]"));
		}
		else if(brand.equalsIgnoreCase("MallyBeauty")) {
			Do_Not_Sell_My_Info = driver.findElement(By.xpath("//a[@id='donot-sell']"));
		}
		else if((brand.equalsIgnoreCase("JLoBeauty")) && (page.equalsIgnoreCase("PDP"))){
			Do_Not_Sell_My_Info = driver.findElement(By.xpath("(//a[text()='Do Not Sell My Info'])[1]"));
		}
		else if((brand.equalsIgnoreCase("JLoBeauty")) && (page.equalsIgnoreCase("Checkout"))){
			Do_Not_Sell_My_Info = driver.findElement(By.xpath("(//a[text()='Do Not Sell My Info'])[last()]"));
		}
		else {
			Do_Not_Sell_My_Info = driver.findElement(By.xpath("(//a[text()='Do Not Sell My Info'])[1]"));
		}
		
		if(Do_Not_Sell_My_Info.isDisplayed()) {
			result = "PASS";
		}
		else {
			result = "FAIL";
		}
		
		Thread.sleep(3000);
		jse.executeScript("window.scrollTo(0, 0)", 0);
		Thread.sleep(3000);
		
		return result;
	}
	
	public String LDU_validation(String brand, String page) throws HarReaderException {
		String result = "FAIL";
		
		HarReader harReader = new HarReader();
        de.sstoehr.harreader.model.Har harq = harReader.readFromFile(new File(System.getProperty("user.dir") + "\\Input_Output\\ContentValidation\\Harfiles\\" + brand + "\\" + brand + "_" + page + ".har"));

        List<de.sstoehr.harreader.model.HarEntry> entries = harq.getLog().getEntries();
        for (de.sstoehr.harreader.model.HarEntry entry : entries) {
        	String dataurl = entry.getRequest().getUrl();        	
        	if((dataurl.contains("ev=PageView")) && (dataurl.contains("dpo=LDU&dpoco=0&dpost=0"))) {
        		result = "PASS";
        	}
        }		
		return result;
	}
	
	public void closeChildWindows(WebDriver driver) {
		String winHandleBefore = driver.getWindowHandle();
		
		// window handles
	    Set all_handles = driver.getWindowHandles();
	    Iterator handle_itr = all_handles.iterator();
	    
	    while(handle_itr.hasNext()) {
	    	String newhandle = (String) handle_itr.next();
	    	if(!(winHandleBefore.equalsIgnoreCase(newhandle))) {
	    		driver.switchTo().window(newhandle);
	    		driver.close();
	    	}
	    }			
		driver.switchTo().window(winHandleBefore);
	}
	
	public void closeChat(WebDriver driver) throws InterruptedException {
		if(driver.findElements(By.xpath("//iframe[@name='ada-embed-iframe']")).size() != 0) {
			Thread.sleep(5000);
			driver.switchTo().frame("ada-embed-iframe");
			Thread.sleep(3000);			
			while(driver.findElements(By.xpath("//div[@id='page-loader']")).size() != 0) {
			}
			WebElement chat_close_button = driver.findElement(By.xpath("//button[@id='ada-close-button']"));				
			Thread.sleep(5000);
			chat_close_button.click();
			driver.switchTo().defaultContent();				
		}
	}
	
	public String seoValidation(String brand, String page, String pagesource) throws IOException {
		String remarks = "";
//		FileWriter myWriter1 = new FileWriter("E://seo//" + page + "_pagesource.txt");
//	      myWriter1.write(pagesource);
//	      myWriter1.close();
	      
		String[][] seoData = comm_obj.getExcelData(System.getProperty("user.dir")+"/Input_Output/ContentValidation/SEO Templates/" + brand + "/SEO Data.xlsx", brand, 0);
		LinkedHashMap<String, String> pagedata = getSEOPageData(page, seoData);
		
		pagesource = pagesource.replaceAll("\\s+", "");		
		pagesource = pagesource.replaceAll("[^a-zA-Z0-9$]+", "");
		
		for(Entry<String, String> entry : pagedata.entrySet()) {
						
			String value = entry.getValue();
			value = value.replaceAll("\\s+", "");		
			value = value.replaceAll("[^a-zA-Z0-9$]+", "");
			
			if(!(pagesource.contains(value))) {
				remarks = remarks + entry.getKey() + " mismatch : Expected - " + entry.getValue() + " ; ";
			}
		}
		return remarks;
	}
	
	public LinkedHashMap<String, String> getSEOPageData(String page, String[][] seoData) {
		LinkedHashMap<String, String> pagedata = new LinkedHashMap<String, String>();		
		int pagenamecolumn = 0;
		
		for(int i=0; i<seoData[0].length; i++) {
			String colName = seoData[0][i];
			if(colName != null) {
				if(colName.equalsIgnoreCase("Page Name")) {
					pagenamecolumn = i;
				}
			}
		}
		for(int i=1; i<seoData.length; i++) {
			String pageinrow = seoData[i][pagenamecolumn].replaceAll("\\s+", "");
			page = page.replaceAll("\\s+", "");
			
			pageinrow = pageinrow.replaceAll("[^a-zA-Z0-9$]+", "");
			page = page.replaceAll("[^a-zA-Z0-9$]+", "");
			
			if((pageinrow != null) && (pageinrow.toLowerCase().contains(page.toLowerCase()))){
				for(int j=1; j<seoData[0].length; j++) {
					if((seoData[0][j] != null) && (!(seoData[0][j].equalsIgnoreCase("-")))) {
						pagedata.put(seoData[0][j].trim(), seoData[i][j]);
					}					
				}
				break;
			}
		}		
		return pagedata;		
	}
}
