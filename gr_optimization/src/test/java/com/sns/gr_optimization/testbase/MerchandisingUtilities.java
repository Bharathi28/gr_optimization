package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

public class MerchandisingUtilities {
	
	DBUtilities db_obj = new DBUtilities();
	BuyflowUtilities bf_obj = new BuyflowUtilities();
	CartLanguageUtilities lang_obj = new CartLanguageUtilities();
	
	public HashMap<String, String> generateExpectedSourceCodeData(HashMap<String, String> sourcecodedata) throws ClassNotFoundException, SQLException {
		LinkedHashMap<String, String> expectedsourcecodedata = new LinkedHashMap<String, String>();
				
		expectedsourcecodedata.put("Media ID", sourcecodedata.get("Media ID"));
		expectedsourcecodedata.put("Creative ID", sourcecodedata.get("Creative ID"));
		expectedsourcecodedata.put("Venue ID", sourcecodedata.get("Venue ID"));
		expectedsourcecodedata.put("Price Book ID", sourcecodedata.get("Price Book ID"));
		return expectedsourcecodedata;
	}
	
	public HashMap<String, String> generateExpectedOfferDataForProduct(HashMap<String, String> offerdata, String kitppid, String PostPU, String brand, String campaign, String category, LinkedHashMap<String, String> CatalogPriceBookIDs) throws ClassNotFoundException, SQLException {
		LinkedHashMap<String, String> expectedofferdata = new LinkedHashMap<String, String>();
		
		expectedofferdata.put("Brand", brand);
		expectedofferdata.put("Campaign", campaign);
		
//		// Check PostPU for current offercode
//		if(PostPU.equalsIgnoreCase("Yes")) {
//			String offerpostpu = offerdata.get("90 Day PPID").trim();
//			offerpostpu = offerpostpu.replaceAll("\\s+", "");
//			if(offerpostpu.contains(kitppid)) {
//				expectedofferdata.put("Offer Post-Purchase", "Yes");	
//				expectedofferdata.put("SupplySize", "90");
//			}
//			else {
//				expectedofferdata.put("Offer Post-Purchase", "No");	
//				expectedofferdata.put("SupplySize", "30");
//			}					
//		}		
//		else {
//			expectedofferdata.put("Offer Post-Purchase", "No");
//			expectedofferdata.put("SupplySize", "30");
//		}		
		
		if(offerdata.get("Master Product") != null) {
			expectedofferdata.put("Master PPID", offerdata.get("Master Product").trim());
		}		
		
		expectedofferdata.put("Product PPID", offerdata.get("PPID").trim());
		if(offerdata.get("90 Day PPID") != null) {
			if(kitppid.equalsIgnoreCase(offerdata.get("90 Day PPID").trim())) {
				expectedofferdata.put("Product PPID", offerdata.get("90 Day PPID").trim());
			}
			else {
				expectedofferdata.put("Product PPID", offerdata.get("PPID").trim());
			}
		}		
		
		expectedofferdata.put("Product Name", offerdata.get("Product Name").trim());
		
		String shade = "No Shade";
		if((offerdata.get("Shade if any") != null) && (!(offerdata.get("Shade if any").equalsIgnoreCase(" ")))){
			shade = offerdata.get("Shade if any").trim();
			expectedofferdata.put("Shade", shade);
		}
		
		if(offerdata.get("Size") != null) {
			expectedofferdata.put("Size", offerdata.get("Size").trim());
		}			
		String onetime_subscribe_option = "";
		if((offerdata.get("Subscribe and Save price") == null) || (offerdata.get("Subscribe and Save price").trim().equalsIgnoreCase(" ")) ||
				(offerdata.get("Acq One Time price") == null) || (offerdata.get("Acq One Time price").trim().equalsIgnoreCase(" "))) {
			onetime_subscribe_option = "No";
		}
		else {
			onetime_subscribe_option = "Yes";
		}
		
		String pagepattern = generatePagePattern(category, shade, brand, onetime_subscribe_option);
		expectedofferdata.put("PagePattern", pagepattern);
		if(category.equalsIgnoreCase("Product")) {

			expectedofferdata.put("Price", offerdata.get("Acq One Time price").trim());
			expectedofferdata.put("Renewal Plan Id", "No Renewal Plan Id");
			expectedofferdata.put("Cart Language", "No Cart Language");
			expectedofferdata.put("Supplemental Cart Language", "No Supplemental Cart Language");
			expectedofferdata.put("Continuity Pricing", "No Continuity Pricing");
			expectedofferdata.put("Continuity Shipping", "No Continuity Shipping");
			expectedofferdata.put("Price Book Id", CatalogPriceBookIDs.get("Acq One Time price"));
		}
		else if(category.equalsIgnoreCase("SubscribeandSave")) {

			if(offerdata.get("Subscribe and Save price") != null) {
				expectedofferdata.put("Price", offerdata.get("Subscribe and Save price").trim());
			}			
					
			expectedofferdata.put("Renewal Plan Id", offerdata.get("Renewal Plan ID").trim());
			expectedofferdata.put("Cart Language", offerdata.get("Cart Language").trim());
			expectedofferdata.put("Supplemental Cart Language", offerdata.get("Supplementary Cart Language").trim());
			
			String[] lang_price_arr = lang_obj.parse_cart_language(offerdata.get("Cart Language").trim());			
			String cart_lang_price = lang_price_arr[1];
			String cart_lang_shipping = lang_price_arr[2];	
			expectedofferdata.put("Continuity Pricing", cart_lang_price);
			expectedofferdata.put("Continuity Shipping", cart_lang_shipping);
			
			expectedofferdata.put("Price Book Id", CatalogPriceBookIDs.get("Subscribe and Save price"));
		}		
		else if(category.equalsIgnoreCase("ShopKit")) {

			expectedofferdata.put("Price", offerdata.get("Entry-Continuity Pricebook").trim());
			expectedofferdata.put("Renewal Plan Id", offerdata.get("Renewal Plan ID").trim());
			expectedofferdata.put("Cart Language", offerdata.get("Cart Language").trim());
			expectedofferdata.put("Supplemental Cart Language", offerdata.get("Supplementary Cart Language").trim());

			String[] lang_price_arr = lang_obj.parse_cart_language(offerdata.get("Cart Language").trim());			
			String cart_lang_price = lang_price_arr[1];
			String cart_lang_shipping = lang_price_arr[2];	
			expectedofferdata.put("Continuity Pricing", cart_lang_price);
			expectedofferdata.put("Continuity Shipping", cart_lang_shipping);
			
			expectedofferdata.put("Price Book Id", CatalogPriceBookIDs.get("Entry-Continuity Pricebook"));
		}
		
		return expectedofferdata;
	}
	
	public String generatePagePattern(String category, String shade, String brand, String subscribe_option) {
		String pagepattern = "product-";
		
		if(!(shade.equalsIgnoreCase("No Shade"))) {
			pagepattern = pagepattern + "shade-";
		}
		
		if(subscribe_option.equalsIgnoreCase("Yes")) {
			if(category.equalsIgnoreCase("Product")) {
				pagepattern = pagepattern + "onetime";
			}
			else if(category.equalsIgnoreCase("SubscribeandSave")) {
				pagepattern = pagepattern + "subscribe";
			}
		}					
		
		String last_char = pagepattern.substring(pagepattern.length() - 1);
		if(last_char.equalsIgnoreCase("-")) {
			pagepattern = pagepattern.substring(0, pagepattern.length() - 1);
		}	
		return pagepattern; 
	}
	
	public String getValueFromCellIfExists(HashMap<String, String> offerdata, String key) {
		String value = null;
		if(StringUtils.isNotBlank(offerdata.get(key))) {
			value = offerdata.get(key).trim();
		}
//		if((offerdata.get(key) != null) && (!(offerdata.get(key).equals("-"))) && (!(offerdata.get(key).equals(""))) && (!(offerdata.get(key).equals(" "))) && (!(offerdata.get(key).trim().isEmpty()))) {
//			value = offerdata.get(key).trim();
//		}
		return value;
	}
	
	public HashMap<String, String> generateExpectedOfferDataForKit(HashMap<String, String> offerdata, String PPUSection, String PostPU, String kitppid, String giftppid, String brand, String campaign) throws ClassNotFoundException, SQLException {
		LinkedHashMap<String, String> expectedofferdata = new LinkedHashMap<String, String>();
				
		expectedofferdata.put("Brand", brand);
		expectedofferdata.put("Campaign", campaign);
		expectedofferdata.put("PagePattern", offerdata.get("PagePattern").trim());		
		
		// Check PrePU for current offercode
		// No gift for MeaningfulBeauty - one-shot campaign
		// And GiftPPID will carry Pre-Purchase value
		String offerprepu = "";
		if((brand.equalsIgnoreCase("MeaningfulBeauty")) && (campaign.equalsIgnoreCase("os"))) {
			if(giftppid.equalsIgnoreCase("Yes")) {
				offerprepu="Yes";
			}
			else {
				offerprepu="No";
			}
		}
		else {
			if(PPUSection.equalsIgnoreCase("Yes")) {
				offerprepu="Yes";
			}
			else {
				offerprepu="No";
			}	
		}		
		expectedofferdata.put("Offer Pre-Purchase", offerprepu);
		
		// Check PostPU for current offercode
		if(PostPU.equalsIgnoreCase("Yes")) {
			String offerpostpu = offerdata.get("Post Purchase Upsell to").trim();
			offerpostpu = offerpostpu.replaceAll("\\s+", "");
			if(offerpostpu.contains(kitppid)) {
				offerpostpu="Yes";
			}
			else {
				offerpostpu="No";
			}
			expectedofferdata.put("Offer Post-Purchase", offerpostpu);
		}		
		else {
			expectedofferdata.put("Offer Post-Purchase", "No");
		}
		
		// Kit ppid
		expectedofferdata.put("Kit PPID", kitppid);
		
		// Kit name
		String kitname = offerdata.get("Actual Kit Name (as in site)").trim();
		expectedofferdata.put("Kit Name", kitname);
		
		// Check Supplysize of PPID
		String supplysize = checkSupplySize(kitppid, offerdata);
		expectedofferdata.put("SupplySize", supplysize);
		
		String giftseperatelineitem = db_obj.checkgiftlineitem(brand, campaign);
		expectedofferdata.put("GiftSeperateLineItem", giftseperatelineitem);
	
		// Fragrance
		if(offerdata.get("PagePattern").trim().contains("fragrance")) {
			expectedofferdata.put("Fragrance", offerdata.get("Fragrance").trim());
		}
		
		//KitShade
		if(offerdata.get("PagePattern").trim().contains("kitshade")) {
			expectedofferdata.put("KitShade", offerdata.get("KitShade").trim());
		}
		
		//GiftShade
		if(offerdata.get("PagePattern").trim().contains("giftshade")) {
			expectedofferdata.put("GiftShade", offerdata.get("GiftShade").trim());
		}
		
		// 30 day PPID, Entry Pricing and Shipping
		String ppid30day = "";
		String expectedEntryPrice = "";
		String expectedEntryShipping = "";
		if(PPUSection.equalsIgnoreCase("Yes")) {
			ppid30day = offerdata.get("Pre-Purchase Entry PPID").trim();
			expectedEntryPrice = offerdata.get("Pre-Purchase Entry Pricing").trim();
			expectedEntryShipping = offerdata.get("Pre-Purchase Entry Shipping").trim();		
		}
		else {
			ppid30day = offerdata.get("Entry PPID").trim();
			expectedEntryPrice = offerdata.get("Entry Pricing").trim();
			expectedEntryShipping = offerdata.get("Entry Shipping").trim();
		}					
		expectedofferdata.put("30 day PPID", ppid30day);
		
		expectedEntryPrice = expectedEntryPrice.replace("$", "");
		expectedofferdata.put("Entry Pricing", expectedEntryPrice);
		
		expectedEntryShipping = expectedEntryShipping.replace("$", "");
		expectedofferdata.put("Entry Shipping", expectedEntryShipping);		
		
		String expectedcampaigngifts = "";
		String expectedrenewalplanid = "No Renewal Plan";
		String expectedinstallmentplanid = "No Installment Plan";
		String expectedcartlanguage = "No Cart Language";
		String expectedsuppcartlanguage = "No Supplemental Cart Language";
		String expectedfinalpricing = "";
		String expectedfinalshipping = "";
		String expectedprepuproduct = "No PrePU Product";
		String expectedpostpuproduct = "No PostPU Product";
		String continuitypricing = "";
		String continuityshipping = "";
		
		// Post-Purchase - No
		if(supplysize.equalsIgnoreCase("30")) {
			expectedfinalpricing = expectedEntryPrice;
			expectedfinalshipping = expectedEntryShipping;
			// Pre-Purchase - Yes
			if(PPUSection.equalsIgnoreCase("Yes")) {	
				if(offerdata.get("Pre-Purchase Entry Promotion 1") != null) {
					expectedcampaigngifts = offerdata.get("Pre-Purchase Entry Promotion 1").trim();
				}					
				if(offerdata.get("Pre-Purchase Entry Promotion 2") != null) {
					expectedprepuproduct = offerdata.get("Pre-Purchase Entry Promotion 2").trim();
				}
				
//				if(!(offerdata.get("Pre-Purchase Entry Renewal Plan").equalsIgnoreCase("-"))) {
					expectedrenewalplanid = offerdata.get("Pre-Purchase Entry Renewal Plan");
//				}				
				
//				if(!(offerdata.get("Pre Purchase Entry Cart Language").equalsIgnoreCase("-"))) {
					expectedcartlanguage = offerdata.get("Pre Purchase Entry Cart Language");
//				}							
//				if(!(offerdata.get("Pre Purchase Entry Supplemental Cart Language").equalsIgnoreCase("-"))) {
					expectedsuppcartlanguage = offerdata.get("Pre Purchase Entry Supplemental Cart Language");
//				}			
				
				// Continuity Pricing and Shipping
				if((offerdata.get("Pre Purchase Continuity Pricing (product)") != null) && (!(offerdata.get("Pre Purchase Continuity Pricing (product)").equalsIgnoreCase("-")))) {
					continuitypricing = offerdata.get("Pre Purchase Continuity Pricing (product)").trim();	
					continuitypricing = continuitypricing.replace("$", "");
				}
				else if((offerdata.get("Continuity Pricing (product)") != null) && (!(offerdata.get("Continuity Pricing (product)").equalsIgnoreCase("-")))) {
					continuitypricing = offerdata.get("Continuity Pricing (product)").trim();	
					continuitypricing = continuitypricing.replace("$", "");
				}
				else {
					continuitypricing = "No Continuity";
				}
				
				if((offerdata.get("Pre Purchase Continuity Shipping") != null) && (!(offerdata.get("Pre Purchase Continuity Shipping").equalsIgnoreCase("-")))) {
					continuityshipping = offerdata.get("Pre Purchase Continuity Shipping").trim();
					continuityshipping = continuityshipping.replace("$", "");
				}	
				else if((offerdata.get("Continuity Shipping") != null) && (!(offerdata.get("Continuity Shipping").equalsIgnoreCase("-")))) {
					continuityshipping = offerdata.get("Continuity Shipping").trim();	
					continuityshipping = continuityshipping.replace("$", "");
				}
				else {
					continuityshipping = "No Continuity";
				}
			}
			// Pre-Purchase - No
			else {
				if(offerdata.get("Entry Promotion 1") != null) {
					expectedcampaigngifts = offerdata.get("Entry Promotion 1").trim();
				}				
//				if(!(offerdata.get("Entry Cart Language").equalsIgnoreCase("-"))) {
					expectedcartlanguage = offerdata.get("Entry Cart Language");
//				}
				
//				if(!(offerdata.get("Entry Supplemental Cart Language").equalsIgnoreCase("-"))) {
					expectedsuppcartlanguage = offerdata.get("Entry Supplemental Cart Language");
//				}				
				
//				if(!(offerdata.get("Entry Renewal Plan").equalsIgnoreCase("-"))) {
					expectedrenewalplanid = offerdata.get("Entry Renewal Plan");
//				}												
								
				// Continuity Pricing and Shipping
				if((offerdata.get("Entry Continuity Pricing (product)") != null) && (!(offerdata.get("Entry Continuity Pricing (product)").equalsIgnoreCase("-")))) {
					continuitypricing = offerdata.get("Entry Continuity Pricing (product)").trim();	
					continuitypricing = continuitypricing.replace("$", "");
				}
				else if((offerdata.get("Continuity Pricing (product)") != null) && (!(offerdata.get("Continuity Pricing (product)").equalsIgnoreCase("-")))) {
					continuitypricing = offerdata.get("Continuity Pricing (product)").trim();	
					continuitypricing = continuitypricing.replace("$", "");
				}
				else {
					continuitypricing = "No Continuity";
				}
				
				if((offerdata.get("Entry Continuity Shipping") != null) && (!(offerdata.get("Entry Continuity Shipping").equalsIgnoreCase("-")))) {
					continuityshipping = offerdata.get("Entry Continuity Shipping").trim();
					continuityshipping = continuityshipping.replace("$", "");
				}	
				else if((offerdata.get("Continuity Shipping") != null) && (!(offerdata.get("Continuity Shipping").equalsIgnoreCase("-")))) {
					continuityshipping = offerdata.get("Continuity Shipping").trim();	
					continuityshipping = continuityshipping.replace("$", "");
				}
				else {
					continuityshipping = "No Continuity";
				}
			}					
		}
		// Post-Purchase - Yes
		else {
			expectedfinalpricing = offerdata.get("Post Purchase Upsell Pricing").trim();	
			expectedfinalshipping = offerdata.get("Post Purchase Upsell Shipping").trim();	
			if(offerdata.get("Post Purchase Upsell Promotion 1") != null) {
				expectedcampaigngifts = offerdata.get("Post Purchase Upsell Promotion 1").trim();
			}			
			if(PPUSection.equalsIgnoreCase("Yes")) {
				if(offerdata.get("Post Purchase Upsell Promotion 2") != null) {
					expectedprepuproduct = offerdata.get("Post Purchase Upsell Promotion 2").trim();
				}
			}
			
			if(offerdata.get("Post Purchase Upsell Promotion 3") != null) {
				expectedpostpuproduct = offerdata.get("Post Purchase Upsell Promotion 3").trim();
			}
//			if(!(offerdata.get("Post Purchase Cart Language").equalsIgnoreCase("-"))) {
				expectedcartlanguage = offerdata.get("Post Purchase Cart Language");
//			}
			
//			if(!(offerdata.get("Post Purchase Supplemental Cart Language").equalsIgnoreCase("-"))) {
				expectedsuppcartlanguage = offerdata.get("Post Purchase Supplemental Cart Language");
//			}			
			
//			if(!(offerdata.get("Post Purchase Renewal Plan").equalsIgnoreCase("-"))) {
				expectedrenewalplanid = offerdata.get("Post Purchase Renewal Plan");
//			}			
			
			if(offerdata.get("Post Purchase Upsell Payment Plan (Installment)") != null) {
				expectedinstallmentplanid = offerdata.get("Post Purchase Upsell Payment Plan (Installment)").trim();
			}
			
			// Continuity Pricing and Shipping
			if((offerdata.get("Continuity Pricing (product)") != null) && (!(offerdata.get("Continuity Pricing (product)").equalsIgnoreCase("-")))) {
				continuitypricing = offerdata.get("Continuity Pricing (product)").trim();	
				continuitypricing = continuitypricing.replace("$", "");
			}
			else {
				continuitypricing = "No Continuity";
			}
					
			if((offerdata.get("Continuity Shipping") != null) && (!(offerdata.get("Continuity Shipping").equalsIgnoreCase("-")))) {
				continuityshipping = offerdata.get("Continuity Shipping").trim();
				continuityshipping = continuityshipping.replace("$", "");
			}		
			else {
				continuityshipping = "No Continuity";
			}
		}		
		expectedfinalpricing = expectedfinalpricing.replace("$", "");
		expectedfinalshipping = expectedfinalshipping.replace("$", "");
		
		// Gift ppid
		// No gift for MeaningfulBeauty - one-shot campaign
		// And GiftPPID will carry Pre-Purchase value
		if(!(campaign.equalsIgnoreCase("os"))) {
			// There is a gift choice - so giftppid will be mentioned in run_input
			if(!(giftppid.equalsIgnoreCase("-"))) {
				expectedofferdata.put("Gift PPID", giftppid);
			}		
			// No Gift choice
			else {
				// If no seperate lineitem, then no GiftPPID
				if(giftseperatelineitem.equalsIgnoreCase("Yes")) {
					if((expectedcampaigngifts != null) && (!(expectedcampaigngifts.equals("-"))) && (!(expectedcampaigngifts.equals(""))) && (!(expectedcampaigngifts.equalsIgnoreCase("Free gift")))) {
						giftppid = String.join(",", bf_obj.getPPIDfromString(brand, expectedcampaigngifts));
						expectedofferdata.put("Gift PPID", giftppid);
					}
					else if((expectedcampaigngifts == null) || (expectedcampaigngifts.equals("-")) || (expectedcampaigngifts.equals("")) || (expectedcampaigngifts.equalsIgnoreCase("Free gift"))){
						expectedofferdata.put("Gift PPID", "No Gift");
					}
				}
				else {
					expectedofferdata.put("Gift PPID", "No Gift");
				}
			}
		}		
		
		expectedofferdata.put("Campaign Gifts", expectedcampaigngifts);
		expectedofferdata.put("PrePU Product", expectedprepuproduct);
		expectedofferdata.put("PostPU Product", expectedpostpuproduct);
		expectedofferdata.put("Cart Language", expectedcartlanguage);
		expectedofferdata.put("Supplemental Cart Language", expectedsuppcartlanguage);
		expectedofferdata.put("Renewal Plan Id", expectedrenewalplanid);
		expectedofferdata.put("Installment Plan Id", expectedinstallmentplanid);
		expectedofferdata.put("Final Pricing", expectedfinalpricing);
		expectedofferdata.put("Final Shipping", expectedfinalshipping);
		expectedofferdata.put("Continuity Pricing", continuitypricing);	
		expectedofferdata.put("Continuity Shipping", continuityshipping);
		
		return expectedofferdata;
	}

	public String checkPostPU(HashMap<String, String> offerdata, String brand) throws ClassNotFoundException, SQLException {
		String PostPU;
		if(offerdata.get("Post Purchase Upsell to") != null) {
			String postpuppid = offerdata.get("Post Purchase Upsell to").trim();
			
			String brandcode = db_obj.get_sourceproductlinecode(brand); 
			
			if(postpuppid.contains(brandcode)) {
				PostPU = "Yes";
			}
			else {
				PostPU = "No";
			}
		}
		else {
			PostPU = "No";
		}		
		return PostPU;
	}
	
	public String checkSupplySize(String ppid, HashMap<String, String> offerdata) {
		String supplysize = "";		
		if((offerdata.containsKey("Entry PPID")) && (offerdata.get("Entry PPID").contains(ppid))) {
			supplysize = "30";
		}
		else if((offerdata.containsKey("Pre-Purchase Entry PPID")) && (offerdata.get("Pre-Purchase Entry PPID").contains(ppid))) {
			supplysize = "30";
		}
		else if((offerdata.containsKey("Post Purchase Upsell to")) && (offerdata.get("Post Purchase Upsell to").contains(ppid))) {
			supplysize = "90";
		}
		return supplysize;
	}
	
	public String getGift(String PPUSection, String giftppid, HashMap<String, String> offerdata, String brand, String campaign) throws ClassNotFoundException, SQLException {
		String giftname = "No Gift";
		String giftvalue = "";
		
		String sourceproductlinecode = db_obj.get_sourceproductlinecode(brand);

		if(PPUSection.equalsIgnoreCase("Yes")) {
			giftvalue = offerdata.get("Pre-Purchase Entry Promotion 1");	
		}
		else {
			giftvalue = offerdata.get("Entry Promotion 1");	
		}
		
		if((giftvalue != null) && (!(giftvalue.equalsIgnoreCase("-")))){
			giftvalue = giftvalue.trim();
			if(!(giftppid.equalsIgnoreCase("-"))) {
				if(!((brand.equalsIgnoreCase("MeaningfulBeauty")) && (campaign.equalsIgnoreCase("os")))) {
					giftname = db_obj.getGiftname(brand, campaign, giftppid);
				}
			}
			else {
				if(giftvalue.contains(sourceproductlinecode)) {
					giftppid =  bf_obj.getPPIDfromString(brand, giftvalue).get(0);
					giftname = db_obj.getGiftname(brand, campaign, giftppid);
				}
				else {
					giftname = db_obj.getGiftname(brand, campaign, giftvalue);
				}
			}
		}
		return giftname;
	}
	
	public int getPPIDColumn(String[][] merchData, String ppid) {
		int ppidcolumn = 0;
		int temp = 0;
		String[] expectedrowNames = {"Entry PPID", "Pre-Purchase Entry PPID", "Post Purchase Upsell to"};
		
		for(int i=0; i<merchData.length; i++) {			
			String currentrowname = merchData[i][0];
			for(String name : expectedrowNames) {
				if((currentrowname != null) && (currentrowname.contains(name))) {
					for(int j=1; j<merchData[i].length; j++) {
						String rowPPID = merchData[i][j];
			            if((rowPPID != null) && (rowPPID.contains(ppid))) {
			            	ppidcolumn = j;
			            	temp = 1;
			            	break;
			            }
			        }
					if(temp == 1) {
						break;
					}
				}
			}	
			if(temp == 1) {
				break;
			}
	    }
		return ppidcolumn;
	}
	
	public String IsPrePurchase(String[][] merchData, String ppid) {
		String PPUSection = "No";
		int temp = 0;
		String[] expectedrowNames = {"Entry PPID", "Pre-Purchase Entry PPID", "Post Purchase Upsell to"};
		
		for(int i=0; i<merchData.length; i++) {			
			String currentrowname = merchData[i][0];
			if((currentrowname != null) && (currentrowname.equalsIgnoreCase("Pre Purchase Upsell"))) {
				PPUSection = "Yes";
			}
			for(String name : expectedrowNames) {
				if((currentrowname != null) && (currentrowname.contains(name))) {
					for(int j=1; j<merchData[i].length; j++) {
						String rowPPID = merchData[i][j];
			            if((rowPPID != null) && (rowPPID.contains(ppid))) {
			            	temp = 1;
			            	break;
			            }
			        }
					if(temp == 1) {
						break;
					}
				}
			}	
			if(temp == 1) {
				break;
			}
	    }
		return PPUSection;
	}
		
	public HashMap<String, String> getSourceCodeInfo(String[][] merchData, String sourcecodegroup) {
		LinkedHashMap<String, String> sourcecodedata = new LinkedHashMap<String, String>();
		int columnCount = merchData[0].length;
		
		int sourcecodegroupcolumn = 0;
		int vanityurlcolumn = 0;
		int sourcecodecolumn = 0;
		for(int i=0; i<columnCount; i++) {
			String colName = merchData[0][i];
			if(merchData[0][i] != null) {
				if(colName.equalsIgnoreCase("Vanity URL")) {
					vanityurlcolumn = i;
				}
				if(colName.equalsIgnoreCase("Source Code Group")) {
					sourcecodegroupcolumn = i;
				}
				if(colName.equalsIgnoreCase("Source Code")) {
					sourcecodecolumn = i;
				}
			}			
		}
		
		for(int i=0; i<merchData.length; i++) {	
			if(merchData[i][0] != null) {
				String sourcecodegroupinrow = merchData[i][sourcecodegroupcolumn];
				String sourcecodeinrow = merchData[i][sourcecodecolumn];
				String vanityurlinrow = merchData[i][vanityurlcolumn];

				sourcecodegroupinrow = sourcecodegroupinrow.replaceAll("[^a-zA-Z0-9$]+", "");
				sourcecodeinrow = sourcecodeinrow.replaceAll("[^a-zA-Z0-9$]+", "");
				vanityurlinrow = vanityurlinrow.replaceAll("[^a-zA-Z0-9$]+", "");
				sourcecodegroup = sourcecodegroup.replaceAll("[^a-zA-Z0-9$]+", "");
				if((sourcecodegroupinrow.toLowerCase().contains(sourcecodegroup.toLowerCase())) || (sourcecodeinrow.toLowerCase().contains(sourcecodegroup.toLowerCase())) || (vanityurlinrow.toLowerCase().contains(sourcecodegroup.toLowerCase()))) {
					for(int j=0; j<columnCount; j++) {
						if(merchData[0][j] != null) {
							sourcecodedata.put(merchData[0][j].trim(), merchData[i][j]);
						}
					}
					break;
				}
			}
			else {
				break;
			}
		}
		return sourcecodedata;
	}
	
	public HashMap<String, String> getColumnData(String[][] merchData, int column, String PPUSection) {
		LinkedHashMap<String, String> offerdata = new LinkedHashMap<String, String>();
		int entrystart = 0;
		int ppustart = 0;
		for(int i=0; i<merchData.length; i++) {	
			if(merchData[i][0] != null) {
				if(merchData[i][0].equalsIgnoreCase("Kit")) {		
					while(!(merchData[i][0].equalsIgnoreCase("Entry Kit"))) {
						i++;
						offerdata.put(merchData[i][0].trim(), merchData[i][column]);
					}
				}
				if(merchData[i][0].equalsIgnoreCase("Entry Kit")) {
					entrystart=i;
				}
				if(merchData[i][0].equalsIgnoreCase("End")) {
					ppustart=i;
					break;
				}
				if(merchData[i][0].equalsIgnoreCase("Pre Purchase Upsell")) {
					ppustart=i;
					break;
				}
			}
		}		

		int loopin = 0;
		int loopout = 0;
		if(PPUSection.equalsIgnoreCase("No")) {
			loopin = entrystart;
			loopout = ppustart;
		}
		else {
			loopin = ppustart+1;
			loopout = merchData.length;
		}
		
		for(int i=loopin; i<loopout; i++) {	
			if((merchData[i][0] != null) && (merchData[i][column] != null)) {
				if(merchData[i][0] == null) {
					merchData[i][0] = merchData[i-1][0];
				}
				offerdata.put(merchData[i][0], merchData[i][column]);
			}
		}
		return offerdata;
	}
	
	public LinkedHashMap<String, String> getCatalogPriceBookIDs(String[][] catalogData) {
		
		LinkedHashMap<String, String> CatalogPriceBookIDs = new LinkedHashMap<String, String>();

		for(int i=0; i<catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			if(colName == null) {
				break;
			}
			if(colName.contains("Acq One Time price")) {
				colName = colName.replaceAll("Acq One Time price", "");
				colName = colName.replaceAll("\\s+", "");				
				colName = colName.replaceAll("[^a-zA-Z0-9$]+", "");

				CatalogPriceBookIDs.put("Acq One Time price", colName);
			}
			else if(colName.contains("Subscribe and Save price")) {
				colName = colName.replaceAll("Subscribe and Save price", "");
				colName = colName.replaceAll("\\s+", "");				
				colName = colName.replaceAll("[^a-zA-Z0-9$]+", "");

				CatalogPriceBookIDs.put("Subscribe and Save price", colName);
			}			
			else if(colName.contains("Entry-Continuity Pricebook")) {
				colName = colName.replaceAll("Entry-Continuity Pricebook", "");
				colName = colName.replaceAll("\\s+", "");				
				colName = colName.replaceAll("[^a-zA-Z0-9$]+", "");

				CatalogPriceBookIDs.put("Entry-Continuity Pricebook", colName);
			}			
		}
		return CatalogPriceBookIDs;
	}
	
	public HashMap<String, String> getProdRowfromCatalog(String[][] catalogData, String ppid) {
		LinkedHashMap<String, String> productdata = new LinkedHashMap<String, String>();
		
		int ppidcolumn = 0;

		for(int i=0; i<catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			if((colName != null) && (colName.equalsIgnoreCase("PPID"))) {
				ppidcolumn = i;
			}

		}
		
		for(int i=0; i<catalogData.length; i++) {	
			String ppidinrow = catalogData[i][ppidcolumn].replaceAll("\\s+", "");

			if(ppidinrow.trim().equalsIgnoreCase(ppid.trim())){
				for(int j=0; j<catalogData[0].length; j++) {
					if(catalogData[0][j] != null) {
						if(catalogData[0][j].contains("Acq One Time price")) {
							productdata.put("Acq One Time price", catalogData[i][j]);
						}
						else if(catalogData[0][j].contains("Subscribe and Save price")) {
							productdata.put("Subscribe and Save price", catalogData[i][j]);
						}
						else if(catalogData[0][j].contains("Entry-Continuity Pricebook")) {
							productdata.put("Entry-Continuity Pricebook", catalogData[i][j]);
						}
						else {
							productdata.put(catalogData[0][j].trim(), catalogData[i][j]);
						}							
					}
				}
				break;
			}
		}
		return productdata;
	}
	
	public String checkShopKitPostPU(HashMap<String, String> offerdata, String brand) throws ClassNotFoundException, SQLException {
		String PostPU;
		if(offerdata.get("90 Day PPID") != null) {
			String postpuppid = offerdata.get("90 Day PPID").trim();
			
			String brandcode = db_obj.get_sourceproductlinecode(brand); 
			
			if(postpuppid.contains(brandcode)) {
				PostPU = "Yes";
			}
			else {
				PostPU = "No";
			}
		}
		else {
			PostPU = "No";
		}		
		return PostPU;
	}
	
	public List<String> fetch_random_singles(String[][] catalogData, int count) {

		int ppidcolumn = 0;
		for(int i=0; i<catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			if(colName.equalsIgnoreCase("PPID")) {
				ppidcolumn = i;
			}
		}
		
		List<String> ppidlist = new ArrayList<String>();
		for(int i=1; i<catalogData.length-1; i++) {	
			ppidlist.add(catalogData[i][ppidcolumn]);
		}
		
		List<String> randsingles = new ArrayList<String>();
		Random rand = new Random(); 
		for(int i=0; i<count; i++) {						
			randsingles.add(ppidlist.get(rand.nextInt(ppidlist.size())));
		}

		return randsingles;
	}
	
	public String getrandomPPID(String[][] merchData) {
		int ppidcolumn = 0;
		int temp = 0;
		String[] expectedrowNames = { "Entry PPID", "Pre-Purchase Entry PPID", "Post Purchase Upsell to" };
		List<String> all_ppid = new ArrayList<String>();
		for (int i = 0; i < merchData.length; i++) {
			String currentrowname = merchData[i][0];
			for (String name : expectedrowNames) {
				if ((currentrowname != null) && (currentrowname.contains(name))) {
					for (int j = 1; j < merchData[i].length; j++) {
						String rowPPID = merchData[i][j];
						if ((rowPPID != null)) {
							all_ppid.add(rowPPID);
						}
					}
				}
			}
		}

		Random rand = new Random();
		String random_ppid = all_ppid.get(rand.nextInt(all_ppid.size()));

		return random_ppid;
	}
	
	public List<String> fetch_random_ppid(String[][] catalogData, int count) {

		int ppidcolumn = 0;
		for (int i = 0; i < catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			if (colName.equalsIgnoreCase("PPID")) {
				ppidcolumn = i;
			}
		}

		List<String> ppidlist = new ArrayList<String>();
		for (int i = 1; i < catalogData.length - 1; i++) {
			ppidlist.add(catalogData[i][ppidcolumn]);
		}

		List<String> randsingles = new ArrayList<String>();
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			randsingles.add(ppidlist.get(rand.nextInt(ppidlist.size())));
		}

		return randsingles;
	}
}
