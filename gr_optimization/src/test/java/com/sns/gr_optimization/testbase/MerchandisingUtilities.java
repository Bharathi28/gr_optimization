package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MerchandisingUtilities {
	
	DBUtilities db_obj = new DBUtilities();
	
	public HashMap<String, String> generateExpectedOfferData(HashMap<String, String> offerdata, HashMap<String, String> sourcecodedata, String PPUSection, String pagepattern, String kitppid, String giftppid, String brand, String campaign) throws ClassNotFoundException, SQLException {
		LinkedHashMap<String, String> expectedofferdata = new LinkedHashMap<String, String>();
				
		expectedofferdata.put("Brand", brand);
		expectedofferdata.put("Campaign", campaign);
		expectedofferdata.put("PagePattern", pagepattern);		
		
		// Check PrePU for current offercode
		String offerprepu = "";
		if(PPUSection.equalsIgnoreCase("Yes")) {
			offerprepu="Yes";
		}
		else {
			offerprepu="No";
		}	
		expectedofferdata.put("Offer Pre-Purchase", offerprepu);
		
		// Check PostPU for current offercode
		String offerpostpu = offerdata.get("Post Purchase Upsell to");
		if(offerpostpu.contains(kitppid)) {
			offerpostpu="Yes";
		}
		else {
			offerpostpu="No";
		}
		expectedofferdata.put("Offer Post-Purchase", offerpostpu);
		
		// Kit ppid
		expectedofferdata.put("Kit PPID", kitppid);
		
		// Kit name
		String kitname = offerdata.get("Actual Kit Name (as in site)");
		expectedofferdata.put("Kit Name", kitname);
		
		// Check Supplysize of PPID
		String supplysize = checkSupplySize(kitppid, offerdata);
		expectedofferdata.put("SupplySize", supplysize);
		System.out.println("SupplySize" + supplysize);
		
		// Gift ppid
		if(!(giftppid.equalsIgnoreCase("-"))) {
			expectedofferdata.put("Gift PPID", giftppid);
		}		
		
		// Gift name
		String giftname = getGift(PPUSection, giftppid, offerdata, brand, campaign);
		expectedofferdata.put("Gift Name", giftname);
		
		// Fragrance
		if(pagepattern.contains("fragrance")) {
			expectedofferdata.put("Fragrance", offerdata.get("Fragrance"));
		}
		
		// 30 day PPID, Entry Pricing and Shipping
		String ppid30day = "";
		String expectedEntryPrice = "";
		String expectedEntryShipping = "";
		if(PPUSection.equalsIgnoreCase("Yes")) {
			ppid30day = offerdata.get("Pre-Purchase Entry PPID");
			expectedEntryPrice = offerdata.get("Pre-Purchase Entry Pricing");
			expectedEntryShipping = offerdata.get("Pre-Purchase Entry Shipping");
		}
		else {
			ppid30day = offerdata.get("Entry PPID");
			expectedEntryPrice = offerdata.get("Entry Pricing");
			expectedEntryShipping = offerdata.get("Entry Shipping");
		}					
		expectedofferdata.put("30 day PPID", ppid30day);
		
		expectedEntryPrice = expectedEntryPrice.replace("$", "");
		expectedofferdata.put("Entry Pricing", expectedEntryPrice);
		
		expectedEntryShipping = expectedEntryShipping.replace("$", "");
		expectedofferdata.put("Entry Shipping", expectedEntryShipping);
		
		// Continuity Pricing and Shipping
		String continuitypricing = offerdata.get("Continuity Pricing (product)");	
		continuitypricing = continuitypricing.replace("$", "");
		expectedofferdata.put("Continuity Pricing", continuitypricing);
		
		String continuityshipping = offerdata.get("Continuity Shipping");
		continuityshipping = continuityshipping.replace("$", "");
		expectedofferdata.put("Continuity Shipping", continuityshipping);
		
		String expectedcampaigngifts = "";
		String expectedrenewalplanid = "";
		String expectedinstallmentplanid = "";
		String expectedcartlanguage = "";
		String expectedsuppcartlanguage = "";
		String expectedfinalpricing = "";
		String expectedfinalshipping = "";
		
		// Post-Purchase - No
		if(supplysize.equalsIgnoreCase("30")) {
			expectedfinalpricing = expectedEntryPrice;
			expectedfinalshipping = expectedEntryShipping;
			// Pre-Purchase - Yes
			if(PPUSection.equalsIgnoreCase("Yes")) {					
				expectedcampaigngifts = offerdata.get("Pre-Purchase Entry Promotion 1");
				expectedcartlanguage = offerdata.get("Pre Purchase Entry Cart Language");
				expectedsuppcartlanguage = offerdata.get("Pre Purchase Entry Supplemental Cart Language");
				expectedrenewalplanid = offerdata.get("Pre-Purchase Entry Renewal Plan");
			}
			// Pre-Purchase - No
			else {
				expectedcampaigngifts = offerdata.get("Entry Promotion 1");
				expectedcartlanguage = offerdata.get("Entry Cart Language");
				expectedsuppcartlanguage = offerdata.get("Entry Supplemental Cart Language");
				expectedrenewalplanid = offerdata.get("Entry Renewal Plan");
			}					
		}
		// Post-Purchase - Yes
		else {
			expectedfinalpricing = offerdata.get("Post Purchase Upsell Pricing");	
			expectedfinalshipping = offerdata.get("Post Purchase Upsell Shipping");	
			expectedcampaigngifts = offerdata.get("Post Purchase Upsell Promotion 1");
			expectedcartlanguage = offerdata.get("Post Purchase Cart Language");
			expectedsuppcartlanguage = offerdata.get("Post Purchase Supplemental Cart Language");
			expectedrenewalplanid = offerdata.get("Post Purchase Renewal Plan");
			expectedinstallmentplanid = offerdata.get("Post Purchase Upsell Payment Plan (Installment)");
		}		
		expectedfinalpricing = expectedfinalpricing.replace("$", "");
		expectedfinalshipping = expectedfinalshipping.replace("$", "");
		
		expectedofferdata.put("Campaign Gifts", expectedcampaigngifts);
		expectedofferdata.put("Cart Language", expectedcartlanguage);
		expectedofferdata.put("Supplemental Cart Language", expectedsuppcartlanguage);
		expectedofferdata.put("Renewal Plan Id", expectedrenewalplanid);
		expectedofferdata.put("Installment Plan Id", expectedinstallmentplanid);
		expectedofferdata.put("Final Pricing", expectedfinalpricing);
		expectedofferdata.put("Final Shipping", expectedfinalshipping);
		
		expectedofferdata.put("Media ID", sourcecodedata.get("Media ID"));
		expectedofferdata.put("Creative ID", sourcecodedata.get("Creative ID"));
		expectedofferdata.put("Venue ID", sourcecodedata.get("Venue ID"));
		
		return expectedofferdata;
	}	

	public String checkPostPU(HashMap<String, String> offerdata) {
		String PostPU;
		String postpuppid = offerdata.get("Post Purchase Upsell to");
		if(postpuppid.matches("^[A-Z0-9]*$")) {
			PostPU = "Yes";
		}
		else {
			PostPU = "No";
		}
		return PostPU;
	}
	
	public String checkSupplySize(String ppid, HashMap<String, String> offerdata) {
		String supplysize = "";		
		System.out.println(offerdata.get("Entry PPID"));
		System.out.println(offerdata.get("Post Purchase Upsell to"));
		if(offerdata.containsKey("Entry PPID")) {
			if(offerdata.get("Entry PPID").contains(ppid)) {
				supplysize = "30";
			}
		}
		else if(offerdata.containsKey("Pre-Purchase Entry PPID")) {
			if(offerdata.get("Pre-Purchase Entry PPID").contains(ppid)) {
				supplysize = "30";
			}
		}
		else if(offerdata.containsKey("Post Purchase Upsell to")) {
			if(offerdata.get("Post Purchase Upsell to").contains(ppid)) {
				supplysize = "90";
			}
		}
		return supplysize;
	}
	
	public String getGift(String PPUSection, String giftppid, HashMap<String, String> offerdata, String brand, String campaign) throws ClassNotFoundException, SQLException {
		String giftname = "";
		if(PPUSection.equalsIgnoreCase("Yes")) {
			if(!(offerdata.get("Pre-Purchase Entry Promotion 1").equalsIgnoreCase("-"))) {
				giftname = db_obj.getGiftname(brand, campaign, giftppid);
			}	
		}
		else {
			if(!(offerdata.get("Entry Promotion 1").equalsIgnoreCase("-"))) {
				giftname = db_obj.getGiftname(brand, campaign, giftppid);
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
//			System.out.println("currentrowname : " + currentrowname);
			for(String name : expectedrowNames) {
				if((currentrowname != null) && (currentrowname.contains(name))) {
					for(int j=1; j<merchData[i].length; j++) {
						String rowPPID = merchData[i][j];
//						System.out.println("rowPPID : " + rowPPID);
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
//			System.out.println("currentrowname : " + currentrowname);
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
		System.out.println(columnCount);
		
		int sourcecodegroupcolumn = 0;
		for(int i=0; i<columnCount; i++) {
			String colName = merchData[0][i];
			System.out.println(merchData[0][i]);
			if(colName.equalsIgnoreCase("Source Code Group")) {
				sourcecodegroupcolumn = i;
				break;
			}
		}
		
		for(int i=0; i<merchData.length; i++) {	
			if(merchData[i][0] != null) {
				String sourcecodeinrow = merchData[i][sourcecodegroupcolumn];
//				String sourcecodeinrow = merchData[i][3];
				sourcecodeinrow = sourcecodeinrow.replaceAll("[^a-zA-Z0-9$]+", "");
				sourcecodegroup = sourcecodegroup.replaceAll("[^a-zA-Z0-9$]+", "");
				if(sourcecodeinrow.equalsIgnoreCase(sourcecodegroup)) {
					for(int j=0; j<columnCount; j++) {
						sourcecodedata.put(merchData[0][j], merchData[i][j]);
					}
				}
			}
			else {
				break;
			}
		}
		System.out.println(sourcecodedata);
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
						offerdata.put(merchData[i][0], merchData[i][column]);
					}
					
				}
				if(merchData[i][0].equalsIgnoreCase("Entry Kit")) {
					entrystart=i;
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
}
