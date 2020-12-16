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
//		System.out.println(url);
		if(env.equalsIgnoreCase("qa")) {
			if(brand.equalsIgnoreCase("JLoBeauty")) {
				url = url.replace("www.", "storefront:Jloqa123@");
			}
			else {
				url = url.replace("www.", "storefront:eComweb123@");
			}			
			url = url.replace("com", "grdev.com");
		}
		else if(env.equalsIgnoreCase("stg")) {
			if(brand.equalsIgnoreCase("JLoBeauty")) {
				url = url.replace("www.", "storefront:Jlostg123@");
			}
			else {
				url = url.replace("www.", "storefront:eComweb123@www.");
			}			
			url = url.replace("com", "stg.dw4.grdev.com");
		}
		else if(env.equalsIgnoreCase("prod")) {
		}
		else {
			url = url.replace("www.", "storefront:eComweb123@www.");
			url = url.replace("com", "stg.dw4.grdev.com");
			url = url.replace(".stg.", "."+ env.toLowerCase() +".");
		}
//		System.out.println(url);
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
	
	// Pixel Validation
		public List<String> getAllEvents(String pixel) throws ClassNotFoundException, SQLException {
			String query = "select * from pixels where pixelname='" + pixel + "'";
			List<Map<String, Object>> pixeldata = DBLibrary.dbAction("fetch",query);	
			
			List<String> events = new ArrayList<String>();
			for(Map<String, Object> entry :pixeldata) {
				String name = entry.get("EVENTNAME").toString();
				events.add(name);
			}
			return events;
		}
		
		public int checkBrandPixelCompatibility(String brand, String event) throws ClassNotFoundException, SQLException {
					
			String joinquery = "select * from brand_pixel where brand='" + brand + "' and event='" + event + "'";
			List<Map<String, Object>> joinlist = DBLibrary.dbAction("fetch",joinquery);		
			return joinlist.size();
		}
		
		public List<String> getFiringPages(String brand, String campaign, String flow, String pixel, String event, List<String> campaignPageList) throws ClassNotFoundException, SQLException {
									
			String pixelQuery = "select * from pixels where pixelname='" + pixel + "' and eventname='" + event + "'";
			List<Map<String, Object>> pixellist = DBLibrary.dbAction("fetch", pixelQuery);
			String pages = pixellist.get(0).get("FIRINGPAGES").toString();
			
			String[] pageArr = pages.split(",");
			List<String> pageList = new ArrayList<String>();
			
			for(String value : pageArr) {
				if(value.equalsIgnoreCase("All")) {
					pageList.addAll(campaignPageList);
					pageList.remove("PrePurchaseUpsell");
					if(flow.equalsIgnoreCase("ccflow")) {
						pageList.remove("paypalreviewpage");
					}
				}
				if(value.equalsIgnoreCase("Home")) {
					if(campaignPageList.contains("HomePage")) {
						pageList.add("HomePage");
					}
				}
				if(value.equalsIgnoreCase("SAS")) {
					if(campaignPageList.contains("SASPage")) {
						pageList.add("SASPage");
					}
				}
				if(value.equalsIgnoreCase("Checkout")) {
					if(campaignPageList.contains("CheckoutPage")) {
						pageList.add("CheckoutPage");
					}
				}		
				if(value.equalsIgnoreCase("Checkout/PaypalReview")) {
					if(flow.equalsIgnoreCase("paypalflow")) {
						if(campaignPageList.contains("paypalreviewpage")) {
							pageList.add("paypalreviewpage");
						}
					}	
					else {
						if(campaignPageList.contains("CheckoutPage")) {
							pageList.add("CheckoutPage");
						}
					}
				}					
				if(value.equalsIgnoreCase("Confirmation")) {
					if(campaignPageList.contains("ConfirmationPage")) {
						pageList.add("ConfirmationPage");
					}
				}	
				if(value.equalsIgnoreCase("Upsell/Confirmation")) {
					if(campaignPageList.contains("PostPurchaseUpsell")) {
						pageList.add("PostPurchaseUpsell");
					}
					else {
						pageList.add("ConfirmationPage");
					}
				}
			}
			return pageList;
		}	
		
		public String getSearchPattern(String brand, String event) throws ClassNotFoundException, SQLException {
			String joinquery = "select * from brand_pixel where brand='" + brand + "' and event='" + event + "'";
			List<Map<String, Object>> joinlist = DBLibrary.dbAction("fetch",joinquery);
			String pattern = joinlist.get(0).get("SEARCHPATTERN").toString();
			return pattern;
		}
		
		public String getPixelBrandId(String brand, String event) throws ClassNotFoundException, SQLException {
			String joinquery = "select * from brand_pixel where brand='" + brand + "' and event='" + event + "'";
			List<Map<String, Object>> joinlist = DBLibrary.dbAction("fetch",joinquery);
			String id = joinlist.get(0).get("PIXELBRANDID").toString();
			return id;
		}
}
