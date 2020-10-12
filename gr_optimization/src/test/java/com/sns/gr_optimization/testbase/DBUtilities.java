package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sns.gr_optimization.testbase.DBLibrary;

public class DBUtilities {
	
	static BuyflowUtilities bf_obj = new BuyflowUtilities();

	public static String get_realm(String brand) throws ClassNotFoundException, SQLException {
		String realmQuery = "select * from brand where brandname ='" + brand + "'";
		List<Map<String, Object>> realmResult = DBLibrary.dbAction("fetch", realmQuery);
		String realm = realmResult.get(0).get("REALM").toString();
		return realm;
	}
	
	public String getUrl(String brand, String campaign, String env) throws ClassNotFoundException, SQLException {
		String query = "select * from campaign_urls where brand='" + brand + "' and campaign='" + campaign + "'";
//		System.out.println(query);
		List<Map<String, Object>> campaigndata = DBLibrary.dbAction("fetch", query);		
		String url = campaigndata.get(0).get("PRODURL").toString();
		if(url.equalsIgnoreCase("qa")) {
			url = url.replace("www.", "storefront:eComweb123@");
			url = url.replace("com", "grdev.com");
		}
		else if(url.equalsIgnoreCase("stg")) {
			url = url.replace("www.", "storefront:eComweb123@www.");
			url = url.replace("com", "stg.dw4.grdev.com");
		}
		else if(url.equalsIgnoreCase("prod")) {
		}
		else {
			url = url.replace("www.", "storefront:eComweb123@www.");
			url = url.replace("com", "stg.dw4.grdev.com");
			url = url.replace(".stg.", "."+ env.toLowerCase() +".");
		}
		return url;
	}
	
	public static String get_sourceproductlinecode(String brand) throws ClassNotFoundException, SQLException {
		String abbrQuery = "select * from brand where brandname ='" + brand + "'";
		List<Map<String, Object>> abbrResult = DBLibrary.dbAction("fetch", abbrQuery);
		String abbr = abbrResult.get(0).get("SOURCEPRODUCTLINECODE").toString();
		return abbr;
	}
	
	public static String checkcampaigncategory(String brand, String campaign) throws ClassNotFoundException, SQLException {
		String query = "select * from campaign_urls where brand ='" + brand + "' and campaign='" + campaign + "'";
		List<Map<String, Object>> result = DBLibrary.dbAction("fetch", query);
		String category = result.get(0).get("DWCAMPAIGNCATEGORY").toString();
		return category;
	}
	
	public static String checkgiftlineitem(String brand, String campaign) throws ClassNotFoundException, SQLException {
		String query = "select * from campaign_urls where brand ='" + brand + "' and campaign='" + campaign + "'";
		List<Map<String, Object>> result = DBLibrary.dbAction("fetch", query);
		String lineitem = result.get(0).get("GIFTLINEITEM").toString();
		return lineitem;
	}
	
	public String getGiftname(String brand, String campaign, String ppid) throws ClassNotFoundException, SQLException {
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='Gift' and offer like '%" + ppid + "%'";
		List<Map<String, Object>> giftloc = DBLibrary.dbAction("fetch", query);
//		System.out.println(query);
		String[] giftarr = giftloc.get(0).get("OFFER").toString().split("_");
		String giftname = giftarr[0];
		return giftname;		
	}
	
	public String getSalesTaxPercentage(String state) throws ClassNotFoundException, SQLException {
		String query = "select * from sales_tax where state ='" + state + "'";
		List<Map<String, Object>> result = DBLibrary.dbAction("fetch", query);
		String percentage = result.get(0).get("PERCENTAGE").toString();
		return percentage;
	}
}
