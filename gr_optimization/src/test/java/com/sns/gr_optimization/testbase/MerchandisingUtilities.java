package com.sns.gr_optimization.testbase;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MerchandisingUtilities {
	
	DBUtilities db_obj = new DBUtilities();
	BuyflowUtilities bf_obj = new BuyflowUtilities();
	
	public HashMap<String, String> generateExpectedOfferData(HashMap<String, String> offerdata, HashMap<String, String> sourcecodedata, String PPUSection, String PostPU, String kitppid, String giftppid, String brand, String campaign) throws ClassNotFoundException, SQLException {
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
//		System.out.println("SupplySize" + supplysize);		
		
		// Gift name
		String giftname = getGift(PPUSection, giftppid, offerdata, brand, campaign);
		expectedofferdata.put("Gift Name", giftname);
		String giftseperatelineitem = "";
		if(!(giftname.equalsIgnoreCase("No Gift"))) {
			giftseperatelineitem = db_obj.checkgiftlineitem(brand, campaign);
			expectedofferdata.put("GiftSeperateLineItem", giftseperatelineitem);
		}
		
		// Fragrance
		if(offerdata.get("PagePattern").trim().contains("fragrance")) {
			expectedofferdata.put("Fragrance", offerdata.get("Fragrance").trim());
		}
		
		//KitShade
		if(offerdata.get("PagePattern").trim().contains("kitshade")) {
			expectedofferdata.put("KitShade", offerdata.get("KitShade").trim());
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
				if(!(giftname.equalsIgnoreCase("No Gift"))) {
					expectedcampaigngifts = offerdata.get("Pre-Purchase Entry Promotion 1").trim();
				}					
				if(offerdata.get("Pre-Purchase Entry Promotion 2") != null) {
					expectedprepuproduct = offerdata.get("Pre-Purchase Entry Promotion 2").trim();
				}
				if(offerdata.get("Pre-Purchase Entry Renewal Plan") != null) {
					expectedrenewalplanid = offerdata.get("Pre-Purchase Entry Renewal Plan").trim();
				}
				if(offerdata.get("Pre Purchase Entry Cart Language") != null) {
					expectedcartlanguage = offerdata.get("Pre Purchase Entry Cart Language").trim();
				}				
				
				if(offerdata.get("Pre Purchase Entry Supplemental Cart Language") != null) {
					expectedsuppcartlanguage = offerdata.get("Pre Purchase Entry Supplemental Cart Language").trim();
				}				
				
				// Continuity Pricing and Shipping
				if(offerdata.get("Pre Purchase Continuity Pricing (product)") != null) {
					continuitypricing = offerdata.get("Pre Purchase Continuity Pricing (product)").trim();	
					continuitypricing = continuitypricing.replace("$", "");
				}
				else if(offerdata.get("Continuity Pricing (product)") != null) {
					continuitypricing = offerdata.get("Continuity Pricing (product)").trim();	
					continuitypricing = continuitypricing.replace("$", "");
				}
				else {
					continuitypricing = "No Continuity";
				}
				
				if(offerdata.get("Pre Purchase Continuity Shipping") != null) {
					continuityshipping = offerdata.get("Pre Purchase Continuity Shipping").trim();
					continuityshipping = continuityshipping.replace("$", "");
				}	
				else if(offerdata.get("Continuity Shipping") != null) {
					continuityshipping = offerdata.get("Continuity Shipping").trim();	
					continuityshipping = continuityshipping.replace("$", "");
				}
				else {
					continuityshipping = "No Continuity";
				}
			}
			// Pre-Purchase - No
			else {
				if(!(giftname.equalsIgnoreCase("No Gift"))) {
					expectedcampaigngifts = offerdata.get("Entry Promotion 1").trim();
				}				
				if(offerdata.get("Entry Cart Language") != null) {
					expectedcartlanguage = offerdata.get("Entry Cart Language").trim();
				}
				if(offerdata.get("Entry Supplemental Cart Language") != null) {
					expectedsuppcartlanguage = offerdata.get("Entry Supplemental Cart Language").trim();
				}
				if(offerdata.get("Entry Renewal Plan") != null) {
					expectedrenewalplanid = offerdata.get("Entry Renewal Plan").trim();
				}				
								
				// Continuity Pricing and Shipping
				if(offerdata.get("Entry Continuity Pricing (product)") != null) {
					continuitypricing = offerdata.get("Entry Continuity Pricing (product)").trim();	
					continuitypricing = continuitypricing.replace("$", "");
				}
				else if(offerdata.get("Continuity Pricing (product)") != null) {
					continuitypricing = offerdata.get("Continuity Pricing (product)").trim();	
					continuitypricing = continuitypricing.replace("$", "");
				}
				else {
					continuitypricing = "No Continuity";
				}
				
				if(offerdata.get("Entry Continuity Shipping") != null) {
					continuityshipping = offerdata.get("Entry Continuity Shipping").trim();
					continuityshipping = continuityshipping.replace("$", "");
				}	
				else if(offerdata.get("Continuity Shipping") != null) {
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
			if(!(giftname.equalsIgnoreCase("No Gift"))) {
				expectedcampaigngifts = offerdata.get("Post Purchase Upsell Promotion 1").trim();
			}			
			if(offerdata.get("Post Purchase Upsell Promotion 2") != null) {
				expectedprepuproduct = offerdata.get("Post Purchase Upsell Promotion 2").trim();
			}
			if(offerdata.get("Post Purchase Upsell Promotion 3") != null) {
				expectedpostpuproduct = offerdata.get("Post Purchase Upsell Promotion 3").trim();
			}
			if(offerdata.get("Post Purchase Cart Language") != null) {
				expectedcartlanguage = offerdata.get("Post Purchase Cart Language").trim();
			}
			if(offerdata.get("Post Purchase Supplemental Cart Language") != null) {
				expectedsuppcartlanguage = offerdata.get("Post Purchase Supplemental Cart Language").trim();
			}
			if(offerdata.get("Post Purchase Renewal Plan") != null) {
				expectedrenewalplanid = offerdata.get("Post Purchase Renewal Plan").trim();
			}
			if(offerdata.get("Post Purchase Upsell Payment Plan (Installment)") != null) {
				expectedinstallmentplanid = offerdata.get("Post Purchase Upsell Payment Plan (Installment)").trim();
			}
			
			// Continuity Pricing and Shipping
			if(offerdata.get("Continuity Pricing (product)") != null) {
				continuitypricing = offerdata.get("Continuity Pricing (product)").trim();	
				continuitypricing = continuitypricing.replace("$", "");
			}
			else {
				continuitypricing = "No Continuity";
			}
					
			if(offerdata.get("Continuity Shipping") != null) {
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
			if(!(giftppid.equalsIgnoreCase("-"))) {
				expectedofferdata.put("Gift PPID", giftppid);
			}		
			else {
				// If no seperate lineitem, then no GiftPPID
				if(giftseperatelineitem.equalsIgnoreCase("Yes")) {
					if((expectedcampaigngifts != null) && (!(expectedcampaigngifts.equals("-")))) {
						giftppid = bf_obj.getPPIDfromString(brand, expectedcampaigngifts).get(0);
						expectedofferdata.put("Gift PPID", giftppid);
					}
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
		
		expectedofferdata.put("Media ID", sourcecodedata.get("Media ID"));
		expectedofferdata.put("Creative ID", sourcecodedata.get("Creative ID"));
		expectedofferdata.put("Venue ID", sourcecodedata.get("Venue ID"));
		expectedofferdata.put("Price Book ID", sourcecodedata.get("Price Book ID"));
		
		return expectedofferdata;
	}

	public String checkPostPU(HashMap<String, String> offerdata) {
		String PostPU;
		if(offerdata.get("Post Purchase Upsell to") != null) {
			String postpuppid = offerdata.get("Post Purchase Upsell to").trim();
			if(postpuppid.matches("^[A-Z0-9]*$")) {
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
//			if(offerdata.get("Entry PPID").contains(ppid)) {
				supplysize = "30";
//			}
		}
		else if((offerdata.containsKey("Pre-Purchase Entry PPID")) && (offerdata.get("Pre-Purchase Entry PPID").contains(ppid))) {
//			if(offerdata.get("Pre-Purchase Entry PPID").contains(ppid)) {
				supplysize = "30";
//			}
		}
		else if((offerdata.containsKey("Post Purchase Upsell to")) && (offerdata.get("Post Purchase Upsell to").contains(ppid))) {
//			if(offerdata.get("Post Purchase Upsell to").contains(ppid)) {
				supplysize = "90";
//			}
		}
		return supplysize;
	}
	
	public String getGift(String PPUSection, String giftppid, HashMap<String, String> offerdata, String brand, String campaign) throws ClassNotFoundException, SQLException {
		String giftname = "No Gift";
		String giftvalue = "";
		
		String sourceproductlinecode = db_obj.get_sourceproductlinecode(brand);
//		System.out.println(sourceproductlinecode);
		if(PPUSection.equalsIgnoreCase("Yes")) {
			giftvalue = offerdata.get("Pre-Purchase Entry Promotion 1");	
		}
		else {
			giftvalue = offerdata.get("Entry Promotion 1");	
		}
//		System.out.println(giftvalue);
		if((giftvalue != null) && (!(giftvalue.equalsIgnoreCase("-")))){
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
//		System.out.println(columnCount);
		
		int sourcecodegroupcolumn = 0;
		int vanityurlcolumn = 0;
		int sourcecodecolumn = 0;
		for(int i=0; i<columnCount; i++) {
			String colName = merchData[0][i];
//			System.out.println(merchData[0][i]);
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
		
		for(int i=0; i<merchData.length; i++) {	
			if(merchData[i][0] != null) {
				String sourcecodegroupinrow = merchData[i][sourcecodegroupcolumn];
				String sourcecodeinrow = merchData[i][sourcecodecolumn];
				String vanityurlinrow = merchData[i][vanityurlcolumn];

				sourcecodegroupinrow = sourcecodegroupinrow.replaceAll("[^a-zA-Z0-9$]+", "");
				sourcecodeinrow = sourcecodeinrow.replaceAll("[^a-zA-Z0-9$]+", "");
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
//		System.out.println(sourcecodedata);
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
}
