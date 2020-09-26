package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sns.gr_optimization.testbase.DBLibrary;

public class DBUtilities {
	
	static BuyflowUtilities bf_obj = new BuyflowUtilities();

	public static String get_realm(String brand) throws ClassNotFoundException, SQLException {
//		if(brand.contains("BodyFirm")) {
//			brand = "BodyFirm";
//		}
		String realmQuery = "select * from brand where brandname ='" + brand + "'";
		List<Map<String, Object>> realmResult = DBLibrary.dbAction("fetch", realmQuery);
		String realm = realmResult.get(0).get("REALM").toString();
		return realm;
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
	
	public String getGiftname(String brand, String campaign, String ppid) throws ClassNotFoundException, SQLException {
		String query = "select * from locators where brand='" + brand + "' and campaign='" + campaign + "' and step='Gift' and offer like '%" + ppid + "%'";
		List<Map<String, Object>> giftloc = DBLibrary.dbAction("fetch", query);
		
		String[] giftarr = giftloc.get(0).get("OFFER").toString().split("-");
		String giftname = giftarr[0];
		return giftname;		
	}
//	
//	public String getUrl(String brand, String campaign, String env) throws ClassNotFoundException, SQLException {
//		String query = "select * from brand where brandname='" + brand + "' and campaign='" + campaign + "'";
////		System.out.println(query);
//		List<Map<String, Object>> branddata = DBLibrary.dbAction("fetch", query);		
//		String url = "";
//		if(env.toLowerCase().contains("dev")) {
//			url = branddata.get(0).get("STGURL").toString();
//			url = url.replace(".stg.", "."+ env.toLowerCase() +".");
//		}
//		else {
//			url = branddata.get(0).get(env.toUpperCase() + "URL").toString();
//		}		
//		return url;
//	}

}
