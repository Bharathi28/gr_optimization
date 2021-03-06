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
	
	public HashMap<String, String> generateExpectedSourceCodeData(HashMap<String, String> sourcecodedata, String brand, String campaign, String type) throws ClassNotFoundException, SQLException {
		LinkedHashMap<String, String> expectedsourcecodedata = new LinkedHashMap<String, String>();
				
		expectedsourcecodedata.put("Media ID", sourcecodedata.get("Media ID"));
		expectedsourcecodedata.put("Creative ID", sourcecodedata.get("Creative ID"));
		expectedsourcecodedata.put("Venue ID", sourcecodedata.get("Venue ID"));
		
		if((brand.equalsIgnoreCase("Smileactives")) && ((campaign.equalsIgnoreCase("specialoffer2")) || (campaign.equalsIgnoreCase("specialoffer3")))) {
			String pricebook = sourcecodedata.get("Price Book ID");
			String[] arr = pricebook.split("\n");
			String pattern = "";
			int set = 0;
			
			if(type.contains("entrykit")) {
				pattern ="entry";
			}
			else if(type.equalsIgnoreCase("oneshot")) {
				pattern = "oneshot";
			}
			
			for(String value : arr) {
				if(value.toLowerCase().contains(pattern)) {
					String[] arr1 = value.split(" ");
					for(String value1 : arr1) {
						if(value1.contains("PSAA")) {
							expectedsourcecodedata.put("Price Book ID", value1);
							set = 1;
							break;
						}
					}
				}
				if(set==1) {
					break;
				}
			}
		}
		else {
			expectedsourcecodedata.put("Price Book ID", sourcecodedata.get("Price Book ID"));
		}		
		return expectedsourcecodedata;
	}
	
	public HashMap<String, String> generateExpectedOfferDataForProduct(HashMap<String, String> offerdata, HashMap<String, String> ProductShipFreq, String paymenttype, String kitppid, String Expshipfreq, String PostPU, String brand, String campaign, String category, LinkedHashMap<String, String> CatalogPriceBookIDs) throws ClassNotFoundException, SQLException {
		LinkedHashMap<String, String> expectedofferdata = new LinkedHashMap<String, String>();
		
		expectedofferdata.put("Brand", brand);
		expectedofferdata.put("Campaign", campaign);
		expectedofferdata.put("Category", category);
		
		if(offerdata.get("Master Product") != null) {
			expectedofferdata.put("Master PPID", offerdata.get("Master Product").trim());
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
		
		String prepu_option = "No";
		String offerprepu = "";
		if(offerdata.get("PrePU") != null) {
			if(offerdata.get("PrePU").equalsIgnoreCase("Yes")) {
				prepu_option = "Yes";
				offerprepu = "Yes";
			}
			else if (offerdata.get("PrePU").equalsIgnoreCase("No")) {
				prepu_option = "Yes";
				offerprepu = "No";
			}
		}
		expectedofferdata.put("Offer Pre-Purchase", offerprepu);
		
		String ProductPPID = "";
		String PPID30Day = "";
		String Price = "";
		String RenewalPlanID = "";
		String CartLanguage = "";
		String SupplementalCartLanguage = "";		
					
		String onetime_subscribe_option = "";
		if((offerdata.get("Subscribe and Save price") == null) || (offerdata.get("Subscribe and Save price").trim().equalsIgnoreCase(" ")) || (!(offerdata.get("Subscribe and Save price").matches(".*\\d.*"))) ||
				(offerdata.get("Acq One Time price") == null) || (offerdata.get("Acq One Time price").trim().equalsIgnoreCase(" ")) || (!(offerdata.get("Acq One Time price").matches(".*\\d.*")))) {
			onetime_subscribe_option = "No";
		}
		else {
			onetime_subscribe_option = "Yes";
		}
		
		String size_option = "No";
		if(offerdata.get("Product Name").trim().equalsIgnoreCase("THAT INNER LOVE™")) {
			size_option = "Yes";
		}
		if(offerdata.get("Product Name").trim().equalsIgnoreCase("Beauty Sleep Supplement")) {
			size_option = "Yes";
		}
		
		String freq_option = "No";
		if(category.equalsIgnoreCase("SubscribeandSave")) {	
			if(!(Expshipfreq.equalsIgnoreCase("-"))) {
				freq_option = "Yes";
			}
		}		
		
		String payment_option = "No";		
		if(offerdata.get("Product Name").trim().contains("Star Power Duo")) {
			payment_option = "Yes";
			if(paymenttype.equalsIgnoreCase("")) {				
				paymenttype = "OnePay";
			}
			expectedofferdata.put("PaymentType", paymenttype);
		}				
		
		String pagepattern = generatePagePattern(category, shade, brand, onetime_subscribe_option, size_option, freq_option, payment_option, prepu_option);
		expectedofferdata.put("PagePattern", pagepattern);
		
		if((offerdata.get("Post Purchase PPID") != null) && (kitppid.equalsIgnoreCase(offerdata.get("Post Purchase PPID").trim()))) {
			ProductPPID = offerdata.get("Post Purchase PPID").trim();
			PPID30Day  = offerdata.get("PPID").trim();
			Price = offerdata.get("Post Purchase Price").trim();
			RenewalPlanID = offerdata.get("Post Purchase Renewal Plan ID").trim();
			CartLanguage = offerdata.get("Post Purchase Cart Language").trim();
			SupplementalCartLanguage = offerdata.get("Post Purchase Supplemental Cart Language").trim();
				
			expectedofferdata.put("Offer Post-Purchase", "Yes");
			expectedofferdata.put("SupplySize", "90");
		}	
		else {
			ProductPPID = offerdata.get("PPID").trim();
			PPID30Day  = offerdata.get("PPID").trim();
			if(category.equalsIgnoreCase("Product")) {
				Price = offerdata.get("Acq One Time price").trim();
			}
			else if(category.equalsIgnoreCase("SubscribeandSave")) {
				if(offerdata.get("Subscribe and Save price") != null) {
					Price = offerdata.get("Subscribe and Save price").trim();					
				}	
			}
			else if(category.equalsIgnoreCase("ShopKit")) {
				Price = offerdata.get("Entry-Continuity Pricebook").trim();
			}
			
			if(offerdata.get("Renewal Plan ID") != null) {
				RenewalPlanID = offerdata.get("Renewal Plan ID").trim();
			}					
			
			if(paymenttype.equalsIgnoreCase("TwoPay")) {
				if(offerdata.get("Cart Language") != null) {
					CartLanguage = offerdata.get("Cart Language-TwoPay").trim();
				}				
				if(offerdata.get("Supplementary Cart Language") != null) {
					SupplementalCartLanguage = offerdata.get("Supplementary Cart Language-TwoPay").trim();
				}
			}	
			else {
				if(offerdata.get("Cart Language") != null) {
					CartLanguage = offerdata.get("Cart Language").trim();
				}				
				if(offerdata.get("Supplementary Cart Language") != null) {
					SupplementalCartLanguage = offerdata.get("Supplementary Cart Language").trim();
				}
			}								
			
			expectedofferdata.put("Offer Post-Purchase", "No");
			expectedofferdata.put("SupplySize", "30");
		}
		
		expectedofferdata.put("Product PPID", ProductPPID);
		expectedofferdata.put("30 Day PPID", PPID30Day);
		
		if((offerdata.get("Gift") != null) && (!(offerdata.get("Gift").equalsIgnoreCase(" ")))) {
			String gifts = String.join(",", bf_obj.getPPIDfromString(brand, offerdata.get("Gift").trim()));
			expectedofferdata.put("Gift", gifts);				
		}
		else {
			expectedofferdata.put("Gift", "No Gift");
		}
		
		if(category.equalsIgnoreCase("Product")) {
			expectedofferdata.put("Price", Price);
			expectedofferdata.put("Renewal Plan Id", "No Renewal Plan Id");
			expectedofferdata.put("Cart Language", "No Cart Language");
			expectedofferdata.put("Supplemental Cart Language", "No Supplemental Cart Language");
			expectedofferdata.put("Continuity Pricing", "No Continuity Pricing");
			expectedofferdata.put("Continuity Shipping", "No Continuity Shipping");
			expectedofferdata.put("Price Book Id", CatalogPriceBookIDs.get("Acq One Time price"));
		}
		else if(category.equalsIgnoreCase("SubscribeandSave")) {			
			
			if(paymenttype.equalsIgnoreCase("TwoPay")) {
				Price = Price.replace("$", "");
				double priceValue = (Double.parseDouble(Price))/2;
				double roundOff_price = Math.floor(priceValue * 100.0) / 100.0;
				Price = String.valueOf(roundOff_price);
			}
			expectedofferdata.put("Price", Price);		
						
			if(brand.equalsIgnoreCase("JLoBeauty")) {
				if(offerdata.get("Product Name").trim().equalsIgnoreCase("THAT INNER LOVE™")) {
					expectedofferdata.put("Renewal Plan Id", RenewalPlanID);
				}
				else {
					if(Expshipfreq.equalsIgnoreCase("-")) {
						Expshipfreq = "30 Day";
					}
					expectedofferdata.put("Shipping Frequency", Expshipfreq);
					
					if(paymenttype.equalsIgnoreCase("TwoPay")) {
						String ShipFreqCol = Expshipfreq + " RP-TwoPay";
						expectedofferdata.put("Renewal Plan Id", ProductShipFreq.get(ShipFreqCol).trim());
					}
					else {
						String ShipFreqCol = Expshipfreq + " RP";
						expectedofferdata.put("Renewal Plan Id", ProductShipFreq.get(ShipFreqCol).trim());
					}								
					
					if(Expshipfreq.contains("30")) {
						CartLanguage = CartLanguage.replace("90 days", "30 days");
						SupplementalCartLanguage = SupplementalCartLanguage.replace("90 days", "30 days");
					}
					else if(Expshipfreq.contains("60")) {
						CartLanguage = CartLanguage.replace("90 days", "60 days");
						SupplementalCartLanguage = SupplementalCartLanguage.replace("90 days", "60 days");
					}		
					
					// Pre-Purchase Upsell for Star Power Duo
					if(offerdata.get("Product Name").trim().contains("Star Power Duo")) {
						expectedofferdata.put("Offer Pre-Purchase", offerdata.get("Pre-Purchase").trim());
					}
				}				
			}
			else if(brand.equalsIgnoreCase("WestmoreBeauty")) {
				if(Expshipfreq.equalsIgnoreCase("-")) {
					Expshipfreq = "1 month";
				}
				expectedofferdata.put("Shipping Frequency", Expshipfreq);
				
				String ShipFreqCol = Expshipfreq + " RP";
				expectedofferdata.put("Renewal Plan Id", ProductShipFreq.get(ShipFreqCol).trim());	
				
				if(Expshipfreq.contains("1 Month")) {
					CartLanguage = CartLanguage.replace("two months", "one month");
					CartLanguage = CartLanguage.replace("Two months", "One month");
//					CartLanguage = CartLanguage.replace("every one month", "every month");
					SupplementalCartLanguage = SupplementalCartLanguage.replace("two months", "one month");
					SupplementalCartLanguage = SupplementalCartLanguage.replace("Two months", "One month");
//					SupplementalCartLanguage = SupplementalCartLanguage.replace("every one month", "every month");
				}
				else if(Expshipfreq.contains("3 Months")) {
					CartLanguage = CartLanguage.replace("two", "three");
					CartLanguage = CartLanguage.replace("Two", "Three");
					SupplementalCartLanguage = SupplementalCartLanguage.replace("two", "three");
					SupplementalCartLanguage = SupplementalCartLanguage.replace("Two", "Three");
				}
			}
			else {
				expectedofferdata.put("Renewal Plan Id", RenewalPlanID);
			}
			expectedofferdata.put("Cart Language", CartLanguage);			
			expectedofferdata.put("Supplemental Cart Language", SupplementalCartLanguage);
			
			String[] lang_price_arr = lang_obj.parse_cart_language(CartLanguage);
			String cart_lang_price = lang_price_arr[1];
			String cart_lang_shipping = lang_price_arr[2];	
			expectedofferdata.put("Continuity Pricing", cart_lang_price);
			expectedofferdata.put("Continuity Shipping", cart_lang_shipping);
			
			expectedofferdata.put("Price Book Id", CatalogPriceBookIDs.get("Subscribe and Save price"));
		}		
		else if(category.equalsIgnoreCase("ShopKit")) {

//			if(offerdata.get("Gift") != null) {
////				List<String> gifts = bf_obj.getPPIDfromString(brand, offerdata.get("Gift").trim());
////				expectedofferdata.put("Gift", gifts.get(0));	
//				
//				String gifts = String.join(",", bf_obj.getPPIDfromString(brand, offerdata.get("Gift").trim()));
//				expectedofferdata.put("Gift", gifts);
//			}
//			else {
//				expectedofferdata.put("Gift", "No Gift");
//			}
			expectedofferdata.put("Price", Price);
			expectedofferdata.put("Renewal Plan Id", RenewalPlanID);
			expectedofferdata.put("Cart Language", CartLanguage);
			expectedofferdata.put("Supplemental Cart Language", SupplementalCartLanguage);

			String[] lang_price_arr = lang_obj.parse_cart_language(CartLanguage);			
			String cart_lang_price = lang_price_arr[1];
			String cart_lang_shipping = lang_price_arr[2];	
			expectedofferdata.put("Continuity Pricing", cart_lang_price);
			expectedofferdata.put("Continuity Shipping", cart_lang_shipping);
			
			expectedofferdata.put("Price Book Id", CatalogPriceBookIDs.get("Entry-Continuity Pricebook"));
			
			if(offerdata.containsKey("Post Purchase Product")) {
				if(offerdata.get("Post Purchase Product") != null) {								
					String PostPUProduct = String.join(",", bf_obj.getPPIDfromString(brand, offerdata.get("Post Purchase Product").trim()));
					expectedofferdata.put("Post Purchase Product", PostPUProduct);
				}
			}
		}		
		return expectedofferdata;
	}
	
	public String generatePagePattern(String category, String shade, String brand, String subscribe_option, String size_option, String freq_option, String payment_option, String prepu_option) {
		String pagepattern = "product-";
		
		
		
		if(!(shade.equalsIgnoreCase("No Shade"))) {
			pagepattern = pagepattern + "shade-";
		}
		
//		if(brand.equalsIgnoreCase("JLoBeauty")) {
			if(size_option.equalsIgnoreCase("Yes")) {
				pagepattern = pagepattern + "size-";
			}
//		}
			if(category.equalsIgnoreCase("ShopKit")) {
				if(prepu_option.equalsIgnoreCase("Yes")) {
					pagepattern = pagepattern + "prepu-";
				}
			}
		
		
		if(subscribe_option.equalsIgnoreCase("Yes")) {
			if(category.equalsIgnoreCase("Product")) {
				pagepattern = pagepattern + "onetime-";
			}
			else if(category.equalsIgnoreCase("SubscribeandSave")) {
				pagepattern = pagepattern + "subscribe-";				

				if(freq_option.equalsIgnoreCase("Yes")) {
					pagepattern = pagepattern + "frequency-";
				}
					
				if(payment_option.equalsIgnoreCase("Yes")) {
					pagepattern = pagepattern + "paymenttype-";
				}
			}
		}					
		else {
			if(freq_option.equalsIgnoreCase("Yes")) {
				pagepattern = pagepattern + "frequency-";
			}
			if(payment_option.equalsIgnoreCase("Yes")) {
				pagepattern = pagepattern + "paymenttype-";
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
	
	public HashMap<String, String> generateExpectedOfferDataForKit(HashMap<String, String> offerdata, HashMap<String, String> KitShipFreq, String PPUSection, String PostPU, String kitppid, String giftppid, String brand, String campaign, String category) throws ClassNotFoundException, SQLException {
		LinkedHashMap<String, String> expectedofferdata = new LinkedHashMap<String, String>();
				
		expectedofferdata.put("Brand", brand);
		expectedofferdata.put("Campaign", campaign);
		expectedofferdata.put("Category", category);
		expectedofferdata.put("PagePattern", offerdata.get("PagePattern").trim());		
		
		String Expshipfreq = "";
		if((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) {
			Expshipfreq = giftppid;
		}
//		System.out.println("shipfreq:" + Expshipfreq);
		
		// Check PrePU for current offercode
		// No gift for MeaningfulBeauty - one-shot campaign
		// And GiftPPID will carry Pre-Purchase value
		String offerprepu = "";
		if(((brand.equalsIgnoreCase("MeaningfulBeauty")) && (campaign.equalsIgnoreCase("os"))) || ((brand.equalsIgnoreCase("CrepeErase")) && (campaign.equalsIgnoreCase("advanced-one")))){
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
		String offerpostpu = "";
		if(PostPU.equalsIgnoreCase("Yes")) {
			offerpostpu = offerdata.get("Post Purchase Upsell to").trim();
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
		
		if(!(category.equalsIgnoreCase("Kit"))) {
			expectedofferdata.put("Product Name", kitname);			
			String masterPPID = offerdata.get("Product Group").trim();		
			expectedofferdata.put("Master PPID", masterPPID);
		}		
		
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
		
		// 30 day PPID
		String ppid30day = "";
		
		if(PPUSection.equalsIgnoreCase("Yes")) {
			ppid30day = offerdata.get("Pre-Purchase Entry PPID").trim();
		}
		else {
			ppid30day = offerdata.get("Entry PPID").trim();
		}					
		expectedofferdata.put("30 Day PPID", ppid30day);	
		
		String expectedEntryPrice = "";
		String expectedEntryShipping = "";
		String expectedcampaigngifts = "";
		String expectedrenewalplanid = "No Renewal Plan";
		String expectedinstallmentplanid = "No Installment Plan";
		String expectedcartlanguage = "No Cart Language";
		String expectedsuppcartlanguage = "No Supplemental Cart Language";
		String expectedprepuproduct = "No PrePU Product";
		String expectedpostpuproduct = "No PostPU Product";
		String continuitypricing = "";
		String continuityshipping = "";
		
		if(offerdata.containsKey("Free Gift")) {
			expectedcampaigngifts = offerdata.get("Free Gift");
		}
		
		// Shipping Frequency
		String SASpecialoffer2sel = "";
		String expShipFreq = "";
		if((brand.equalsIgnoreCase("Smileactives")) && ((campaign.equalsIgnoreCase("specialoffer2")) || (campaign.equalsIgnoreCase("specialoffer3")))) {
			String[] arr = giftppid.split("-");
			SASpecialoffer2sel = arr[0];			
			
			if(SASpecialoffer2sel.equalsIgnoreCase("entrykit")) {
				expShipFreq = arr[1];
				expectedofferdata.put("Shipping Frequency", expShipFreq);
				
				expectedrenewalplanid = KitShipFreq.get("Renewal Plan");
				expectedcartlanguage = KitShipFreq.get("Cart Language");
				expectedsuppcartlanguage = KitShipFreq.get("Supplementary Cart Language");
			}
		}
		
		// Post-Purchase - No
		if(supplysize.equalsIgnoreCase("30")) {
			// Pre-Purchase - Yes
			if(PPUSection.equalsIgnoreCase("Yes")) {	
				
				if(((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) && (Expshipfreq.equalsIgnoreCase("Onetime"))){
					expectedEntryPrice = offerdata.get("Pre-Purchase One-time Entry Pricing").trim();
				}
				else {
					expectedEntryPrice = offerdata.get("Pre-Purchase Entry Pricing").trim();
				}
				
				expectedEntryShipping = offerdata.get("Pre-Purchase Entry Shipping").trim();
				
				if(offerdata.containsKey("PrePU Product")) {
					expectedprepuproduct = offerdata.get("PrePU Product");
				}
				
				if(!(SASpecialoffer2sel.equalsIgnoreCase("entrykit"))) {
					if(offerdata.get("Pre-Purchase Entry Renewal Plan") != null) {
						expectedrenewalplanid = offerdata.get("Pre-Purchase Entry Renewal Plan");
					}			
					if(offerdata.get("Pre Purchase Entry Cart Language") != null) {
						expectedcartlanguage = offerdata.get("Pre Purchase Entry Cart Language");
					}							
					if(offerdata.get("Pre Purchase Entry Supplemental Cart Language") != null) {
						expectedsuppcartlanguage = offerdata.get("Pre Purchase Entry Supplemental Cart Language");
					}
				}											
				
				// Continuity Pricing and Shipping
				if((brand.equalsIgnoreCase("Smileactives")) && ((campaign.equalsIgnoreCase("core2")) || (campaign.equalsIgnoreCase("specialoffer")))) {
					if((offerdata.get("Pre Purchase Continuity Pricing (product)") != null) && (!(offerdata.get("Pre Purchase Continuity Pricing (product)").equalsIgnoreCase("-")))) {
						continuitypricing = offerdata.get("Pre Purchase Continuity Pricing (product)").trim();	
						continuitypricing = continuitypricing.replace("$", "");
					}
					else {
						continuitypricing = "No Continuity";
					}
					if((offerdata.get("Pre Purchase Continuity Shipping") != null) && (!(offerdata.get("Pre Purchase Continuity Shipping").equalsIgnoreCase("-")))) {
						continuityshipping = offerdata.get("Pre Purchase Continuity Shipping").trim();
						continuityshipping = continuityshipping.replace("$", "");
					}	
					else {
						continuityshipping = "No Continuity";
					}
				}
				else {
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
			}
			// Pre-Purchase - No
			else {
				if(((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) && (Expshipfreq.equalsIgnoreCase("Onetime"))){
					expectedEntryPrice = offerdata.get("Entry One-time Pricing").trim();
				}
				else {
					expectedEntryPrice = offerdata.get("Entry Pricing").trim();
				}
				
				expectedEntryShipping = offerdata.get("Entry Shipping").trim();
				
				if(!(SASpecialoffer2sel.equalsIgnoreCase("entrykit"))) {
					if(offerdata.get("Entry Cart Language") != null) {
						expectedcartlanguage = offerdata.get("Entry Cart Language");
					}				
					if(offerdata.get("Entry Supplemental Cart Language") != null) {
						expectedsuppcartlanguage = offerdata.get("Entry Supplemental Cart Language");
					}								
					if(offerdata.get("Entry Renewal Plan") != null) {
						expectedrenewalplanid = offerdata.get("Entry Renewal Plan");
					}	
				}															
								
				// Continuity Pricing and Shipping
				if((brand.equalsIgnoreCase("Smileactives")) && (campaign.equalsIgnoreCase("core2"))) {
					if((offerdata.get("Entry Continuity Pricing (product)") != null) && (!(offerdata.get("Entry Continuity Pricing (product)").equalsIgnoreCase("-")))) {
						continuitypricing = offerdata.get("Entry Continuity Pricing (product)").trim();	
						continuitypricing = continuitypricing.replace("$", "");
					}
					else {
						continuitypricing = "No Continuity";
					}
					if((offerdata.get("Entry Continuity Shipping") != null) && (!(offerdata.get("Entry Continuity Shipping").equalsIgnoreCase("-")))) {
						continuityshipping = offerdata.get("Entry Continuity Shipping").trim();
						continuityshipping = continuityshipping.replace("$", "");
					}	
					else {
						continuityshipping = "No Continuity";
					}
				}
				else {
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
		}
		// Post-Purchase - Yes
		else {
			if(((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) && (Expshipfreq.equalsIgnoreCase("Onetime"))){
				expectedEntryPrice = offerdata.get("Post Purchase One-time Upsell Pricing").trim();
			}
			else {
				expectedEntryPrice = offerdata.get("Post Purchase Upsell Pricing").trim();	
			}
			
			expectedEntryShipping = offerdata.get("Post Purchase Upsell Shipping").trim();	
			
			if(PPUSection.equalsIgnoreCase("Yes")) {
				if(offerdata.containsKey("PrePU Product")) {
					expectedprepuproduct = offerdata.get("PrePU Product");
				}
			}
			
			if(offerdata.containsKey("PostPU Product")) {
				expectedpostpuproduct = offerdata.get("PostPU Product");
			}
			
			if(!(SASpecialoffer2sel.equalsIgnoreCase("entrykit"))) {
				if(offerdata.get("Post Purchase Cart Language") != null) {
					expectedcartlanguage = offerdata.get("Post Purchase Cart Language");
				}				
				if(offerdata.get("Post Purchase Supplemental Cart Language") != null) {
					expectedsuppcartlanguage = offerdata.get("Post Purchase Supplemental Cart Language");
				}							
				if(offerdata.get("Post Purchase Renewal Plan") != null) {
					expectedrenewalplanid = offerdata.get("Post Purchase Renewal Plan");
				}	
			}					
			
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
	
		// Gift ppid
		// No gift for MeaningfulBeauty - one-shot campaign
		// And GiftPPID will carry Pre-Purchase value
		if((!(campaign.equalsIgnoreCase("os"))) && (!(campaign.equalsIgnoreCase("Order30fsh2b"))) && (!(campaign.equalsIgnoreCase("advanced-one"))) && (!(campaign.equalsIgnoreCase("specialoffer2"))) && (!(campaign.equalsIgnoreCase("specialoffer3"))) && (!(category.equalsIgnoreCase("FCP"))) && (!(category.equalsIgnoreCase("BCP"))) && (!(category.equalsIgnoreCase("Browgel")))) {
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
		
		//
		if((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("BCP")) || (category.equalsIgnoreCase("Browgel"))) {			
			expectedofferdata.put("Shipping Frequency", Expshipfreq);
			
			if(category.equalsIgnoreCase("FCP")) {
				if((offerprepu.equalsIgnoreCase("No")) && (offerpostpu.equalsIgnoreCase("No"))) {
					expectedcampaigngifts = "";
					expectedofferdata.put("Gift PPID", "No Gift");
				}
			}
			if(category.equalsIgnoreCase("BCP")) {
				if(offerprepu.equalsIgnoreCase("No")) {
					expectedcampaigngifts = "";
					expectedofferdata.put("Gift PPID", "No Gift");
					expectedpostpuproduct = "No PostPU Product";
				}
			}			
			if(Expshipfreq.equalsIgnoreCase("Onetime")) {
				expectedrenewalplanid = "No Renewal Plan";
				expectedinstallmentplanid = "No Installment Plan";
				expectedcartlanguage = "No Cart Language";
				expectedsuppcartlanguage = "No Supplemental Cart Language";										
			}
			else {
				if(Expshipfreq.equalsIgnoreCase("-")) {
					Expshipfreq = "1 Month";
				}			
				
				String ShipFreqCol = Expshipfreq + " RP";
				expectedrenewalplanid = KitShipFreq.get(ShipFreqCol);	
				
				if(Expshipfreq.contains("1 Month")) {
										
					expectedcartlanguage = expectedcartlanguage.replace("two months", "one month");
					expectedcartlanguage = expectedcartlanguage.replace("Two months", "One month");					
//					expectedcartlanguage = expectedcartlanguage.replace("every one month", "every month");						
					
					expectedsuppcartlanguage = expectedsuppcartlanguage.replace("two months", "one month");
					expectedsuppcartlanguage = expectedsuppcartlanguage.replace("Two months", "One month");
//					expectedsuppcartlanguage = expectedsuppcartlanguage.replace("every one month", "every month");
					
					if((category.equalsIgnoreCase("FCP")) || (category.equalsIgnoreCase("Browgel"))) {
						if((offerprepu.equalsIgnoreCase("No")) && (offerpostpu.equalsIgnoreCase("No"))) {
							expectedcartlanguage = expectedcartlanguage.replace("every one month", "every month");
							expectedsuppcartlanguage = expectedsuppcartlanguage.replace("every one month", "every month");
						}
					}					
				}
				else if(Expshipfreq.contains("3 Months")) {
					expectedcartlanguage = expectedcartlanguage.replace("two", "three");
					expectedcartlanguage = expectedcartlanguage.replace("Two", "Three");
					expectedsuppcartlanguage = expectedsuppcartlanguage.replace("two", "three");
					expectedsuppcartlanguage = expectedsuppcartlanguage.replace("Two", "Three");
				}
			}
		}
//		expectedofferdata.put("Shipping Frequency", Expshipfreq);
		
		expectedEntryPrice = expectedEntryPrice.replace("$", "");
		expectedofferdata.put("Entry Pricing", expectedEntryPrice);
		
		expectedEntryShipping = expectedEntryShipping.replace("$", "");
		expectedofferdata.put("Entry Shipping", expectedEntryShipping);		
		
		expectedofferdata.put("Campaign Gifts", expectedcampaigngifts);
		expectedofferdata.put("PrePU Product", expectedprepuproduct);
		expectedofferdata.put("PostPU Product", expectedpostpuproduct);
		expectedofferdata.put("Cart Language", expectedcartlanguage);
		expectedofferdata.put("Supplemental Cart Language", expectedsuppcartlanguage);
		expectedofferdata.put("Renewal Plan Id", expectedrenewalplanid);
		expectedofferdata.put("Installment Plan Id", expectedinstallmentplanid);		
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
	
	public String getOneShotPriceBookID(String[][] catalogData){
		String pricebookid = "";
		
		for(int i=0; i<catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			if(colName == null) {
				break;
			}
			if(colName.contains("Acq One Time Pricebook")) {
				colName = colName.replaceAll("Acq One Time Pricebook", "");
				colName = colName.replaceAll("\\s+", "");				
				colName = colName.replaceAll("[^a-zA-Z0-9$]+", "");

				pricebookid = colName;
			}
		}		
		return pricebookid;
	}
	public LinkedHashMap<String, String> getCatalogPriceBookIDs(String[][] catalogData, String ppid, String category) {
		
		LinkedHashMap<String, String> CatalogPriceBookIDs = new LinkedHashMap<String, String>();
		
		if(category.equalsIgnoreCase("SubscribeandSave")) {
			int ppidcolumn = 0;		
			int categorycolumn = 0;
			int contppidcolumn = 0;

			for(int i=0; i<catalogData[0].length; i++) {
				String colName = catalogData[0][i];
				if(colName != null) {
					if(colName.equalsIgnoreCase("PPID")) {
						ppidcolumn = i;
					}				
					if(colName.equalsIgnoreCase("Kit or Single")) {
						categorycolumn = i;
					}
					if(colName.equalsIgnoreCase("Post Purchase PPID")) {
						contppidcolumn = i;
					}
				}
			}
			
			String ssheader="";
			for(int i=0; i<catalogData.length; i++) {				
				String ppidinrow = catalogData[i][ppidcolumn].replaceAll("\\s+", "");	
				String categoryinrow = catalogData[i][categorycolumn].replaceAll("\\s+", "");
				String contppidinrow = null;
				if(catalogData[i][contppidcolumn] != null) {
					contppidinrow = catalogData[i][contppidcolumn].replaceAll("\\s+", "");
				}							
				
				if(categoryinrow.equalsIgnoreCase("Single")) {
					if(((ppidinrow != null) && (ppidinrow.trim().equalsIgnoreCase(ppid.trim()))) || ((contppidinrow != null) && (contppidinrow.trim().equalsIgnoreCase(ppid.trim())))) {
						for(int j=0; j<catalogData[0].length; j++) {
							if(catalogData[0][j].contains("Subscribe and Save")) {
								if((catalogData[i][j] != null) && (catalogData[i][j].matches(".*\\d.*"))) {
									ssheader = catalogData[0][j];
									
									ssheader = ssheader.replaceAll("Subscribe and Save price", "");
									ssheader = ssheader.replaceAll("Subscribe and Save", "");
									ssheader = ssheader.replaceAll("Pricebook", "");
									ssheader = ssheader.replaceAll("15% off", "");
									ssheader = ssheader.replaceAll("20% off", "");
									ssheader = ssheader.replaceAll("\\s+", "");				
									ssheader = ssheader.replaceAll("[^a-zA-Z0-9$]+", "");
//									System.out.println(ssheader);
									CatalogPriceBookIDs.put("Subscribe and Save price", ssheader);
								}									
							}
							if(!(ssheader.equalsIgnoreCase(""))) {
								break;
							}
						}						
					}
				}	
				if(!(ssheader.equalsIgnoreCase(""))) {
					break;
				}
			}		
		}
		else {
			for(int i=0; i<catalogData[0].length; i++) {						
				String colName = catalogData[0][i];
				if(colName == null) {
					break;
				}
				if(colName.contains("Acq One Time Pricebook")) {
					colName = colName.replaceAll("Acq One Time Pricebook", "");
					colName = colName.replaceAll("\\s+", "");				
					colName = colName.replaceAll("[^a-zA-Z0-9$]+", "");

					CatalogPriceBookIDs.put("Acq One Time price", colName);
				}
//				else if(colName.contains("Subscribe and Save")) {
////					if(catalogData[i][j].matches(".*\\d.*")) {
////						
////					}
//					colName = colName.replaceAll("Subscribe and Save price", "");
//					colName = colName.replaceAll("\\s+", "");				
//					colName = colName.replaceAll("[^a-zA-Z0-9$]+", "");
	//
//					CatalogPriceBookIDs.put("Subscribe and Save price", colName);
//				}			
				else if(colName.contains("Entry-Continuity Pricebook")) {
					colName = colName.replaceAll("Entry-Continuity Pricebook", "");
					colName = colName.replaceAll("\\s+", "");				
					colName = colName.replaceAll("[^a-zA-Z0-9$]+", "");

					CatalogPriceBookIDs.put("Entry-Continuity Pricebook", colName);
				}			
			}
		}		
		return CatalogPriceBookIDs;
	}
	
	public HashMap<String, String> getProdRowfromCatalog(String[][] catalogData, String ppid, String category) {
		LinkedHashMap<String, String> productdata = new LinkedHashMap<String, String>();
		
		int ppidcolumn = 0;
		int categorycolumn = 0;
		int contppidcolumn = 0;

		for(int i=0; i<catalogData[0].length; i++) {
			String colName = catalogData[0][i];
			if(colName != null) {
				if(colName.equalsIgnoreCase("PPID")) {
					ppidcolumn = i;
				}
				if(colName.equalsIgnoreCase("Kit or Single")) {
					categorycolumn = i;
				}
				if(colName.equalsIgnoreCase("Post Purchase PPID")) {
					contppidcolumn = i;
				}
			}
		}
		
		for(int i=0; i<catalogData.length; i++) {	
			String ppidinrow = catalogData[i][ppidcolumn].replaceAll("\\s+", "");
			String categoryinrow = catalogData[i][categorycolumn].replaceAll("\\s+", "");
			String contppidinrow = null;
			if(catalogData[i][contppidcolumn] != null) {
				contppidinrow = catalogData[i][contppidcolumn].replaceAll("\\s+", "");
			}			 
			
			if(category.equalsIgnoreCase("ShopKit")) {
				if(categoryinrow.equalsIgnoreCase("Kit")) {
					if(((ppidinrow != null) && (ppidinrow.trim().equalsIgnoreCase(ppid.trim()))) || ((contppidinrow != null) && (contppidinrow.trim().equalsIgnoreCase(ppid.trim())))) {
						for(int j=0; j<catalogData[0].length; j++) {
							if(catalogData[0][j] != null) {
								if(catalogData[0][j].contains("Entry-Continuity Pricebook")) {
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
			}
			else {
				if(categoryinrow.equalsIgnoreCase("Single")) {
					if(((ppidinrow != null) && (ppidinrow.trim().equalsIgnoreCase(ppid.trim()))) || ((contppidinrow != null) && (contppidinrow.trim().equalsIgnoreCase(ppid.trim())))) {
						for(int j=0; j<catalogData[0].length; j++) {
							if(catalogData[0][j] != null) {
								if(catalogData[0][j].contains("Acq One Time Pricebook")) {
									productdata.put("Acq One Time price", catalogData[i][j]);
								}
								else if(catalogData[0][j].contains("Subscribe and Save")) {
									if((catalogData[i][j] != null) && (catalogData[i][j].matches(".*\\d.*"))) {
										productdata.put("Subscribe and Save price", catalogData[i][j]);
									}									
								}
								else {
									productdata.put(catalogData[0][j].trim(), catalogData[i][j]);
								}		
							}
						}
						break;
					}
				}
			}
		}
		return productdata;
	}
	
	public HashMap<String, String> getProdShippingFrequency(String[][] shipFreqData, String ppid) {
		LinkedHashMap<String, String> shipfreqmap = new LinkedHashMap<String, String>();
		int ppidcolumn = 0;
			
		for(int i=0; i<shipFreqData[0].length; i++) {
			String colName = shipFreqData[0][i];
			if((colName != null) && (colName.equalsIgnoreCase("PPID"))) {
				ppidcolumn = i;
			}
		}		
		for(int i=0; i<shipFreqData.length; i++) {			
			String ppidinrow = shipFreqData[i][ppidcolumn].replaceAll("\\s+", "");			
			if(ppidinrow.trim().equalsIgnoreCase(ppid.trim())){
				for(int j=0; j<shipFreqData[0].length; j++) {
					if(shipFreqData[0][j] != null) {
						shipfreqmap.put(shipFreqData[0][j].trim(), shipFreqData[i][j]);
					}
				}
			}
		}		
		return shipfreqmap;		
	}
	
	public HashMap<String, String> getKitShippingFrequency(String[][] shipFreqData, String ppid, String expShipFreq) {
		LinkedHashMap<String, String> shipfreqmap = new LinkedHashMap<String, String>();
		int ppidcolumn = 0;
		int shipfreqcolumn = 0;
					
		for(int i=0; i<shipFreqData[0].length; i++) {
			String colName = shipFreqData[0][i];
			if((colName != null) && (colName.equalsIgnoreCase("Entry PPID"))) {
				ppidcolumn = i;
			}
			if((colName != null) && (colName.equalsIgnoreCase("Shipping Frequency"))) {
				shipfreqcolumn = i;
			}
		}
		for(int i=0; i<shipFreqData.length-1; i++) {	
			
			String ppidinrow = shipFreqData[i][ppidcolumn].replaceAll("\\s+", "");
			String shipfreqinrow = shipFreqData[i][shipfreqcolumn];
			
			if((ppidinrow.trim().equalsIgnoreCase(ppid.trim())) && (shipfreqinrow.toLowerCase().contains(expShipFreq.toLowerCase()))){					
				for(int j=0; j<shipFreqData[0].length; j++) {
					if(shipFreqData[0][j] != null) {
						shipfreqmap.put(shipFreqData[0][j].trim(), shipFreqData[i][j]);
					}					
				}
				break;
			}
		}	
		return shipfreqmap;		
	}
	
	public String checkShopPostPU(HashMap<String, String> offerdata, String brand) throws ClassNotFoundException, SQLException {
		String PostPU;
		if(offerdata.get("Post Purchase PPID") != null) {
			String postpuppid = offerdata.get("Post Purchase PPID").trim();
			
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
		
	public String calculateShippingforProduct(String brand, List<String> subtotal_list, String jloShippingSelect, String currentCategory, List<String> category_list) {
		String shipping_calc = "";
		
		String subtotal_str = bf_obj.CalculateTotalPrice(subtotal_list);
		double subtotal_calc = Double.parseDouble(subtotal_str); 		
		
		if(brand.equalsIgnoreCase("JLoBeauty")) {
						
			if(currentCategory.equalsIgnoreCase("Product")) {
				if(jloShippingSelect.equals("Free Shipping")) {
					if(subtotal_calc > 50) {
						shipping_calc = "FREE";
					}
					else {
						shipping_calc = "$4.99";
					}
				}
				else if(jloShippingSelect.equals("Two Day Shipping")) {
					if(subtotal_calc > 50) {
						shipping_calc = "$5.99";
					}
					else {
						shipping_calc = "$9.99";
					}
				}
			}
			else if(currentCategory.equalsIgnoreCase("SubscribeandSave")) {
				if(jloShippingSelect.equals("Free Shipping")) {
					shipping_calc = "FREE";
				}
				else if(jloShippingSelect.equals("Two Day Shipping")) {
					
					if((subtotal_calc == 49.50) || (subtotal_calc == 69.90)) {
						shipping_calc = "$5.99";
					}
					else {
						if(subtotal_calc > 50) {
							shipping_calc = "$5.99";
						}
						else {
							shipping_calc = "$9.99";
						}
					}					
				}
			}					
		}
		else {
			if((category_list.contains("Kit")) || (category_list.contains("SubscribeandSave")) || (category_list.contains("ShopKit"))) {
				shipping_calc = "FREE";
			}
			else {						
				if(brand.equalsIgnoreCase("MeaningfulBeauty")) {
					if(subtotal_calc > 89) {
						shipping_calc = "$8.99";
					}
					else if(subtotal_calc > 59) {
						shipping_calc = "$7.99";
					}
					else if(subtotal_calc > 40) {
						shipping_calc = "$6.99";
					}
					else {
						shipping_calc = "$5.99";
					}
				}
				else if(brand.equalsIgnoreCase("Smileactives")) {
					if(subtotal_calc > 100) {
						shipping_calc = "FREE";
					}
					else if(subtotal_calc > 50) {
						shipping_calc = "$2.99";
					}
					else {
						shipping_calc = "$4.99";
					}
				}
				else {
					if(subtotal_calc > 49) {
						shipping_calc = "FREE";
					}
					else {
						if(brand.equalsIgnoreCase("JLoBeauty")) {
							shipping_calc = "$4.99";
						}
						else if(brand.equalsIgnoreCase("CrepeErase")){
							shipping_calc = "$5.99";
						}		
						else {
							shipping_calc = "$4.99";
						}
					}
				} 
			}
		}
		return shipping_calc;
	}
	
	public List<String> getPostPU(List<String> subtotal_list, List<String> category_list, List<String> supplysize_list, List<String> offer_postpurchase_list) {
		String postPUCategory = category_list.get(0);
		String SupplySize = supplysize_list.get(0);
		String OfferPostPurchase = offer_postpurchase_list.get(0);
		
		Double highPrice = Double.valueOf(subtotal_list.get(0));
		for(int i=1; i<subtotal_list.size(); i++) {
			Double priceValue = Double.valueOf(subtotal_list.get(i));
			if(priceValue > highPrice) {
				highPrice = priceValue;
				postPUCategory = category_list.get(i);
				SupplySize = supplysize_list.get(i);
				OfferPostPurchase = offer_postpurchase_list.get(i);
			}			
		}				
		
		if((postPUCategory.equalsIgnoreCase("Kit")) || (postPUCategory.equalsIgnoreCase("ShopKit")) || (postPUCategory.equalsIgnoreCase("FCP")) || (postPUCategory.equalsIgnoreCase("BCP")) || (postPUCategory.equalsIgnoreCase("Browgel"))) {
			postPUCategory = "PostPU";
		}
		else if((postPUCategory.equalsIgnoreCase("Product")) || (postPUCategory.equalsIgnoreCase("SubscribeandSave"))) {
			postPUCategory = "ProductPostPU";
		}
		
		List<String> PostPU_data = new ArrayList<String>();
		PostPU_data.add(postPUCategory);
		PostPU_data.add(SupplySize);
		PostPU_data.add(OfferPostPurchase);
		return PostPU_data;		
	}
}
