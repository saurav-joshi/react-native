package com.iaasimov.utils;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.38 $ 
 */
public class StringConstants {

	public static final String USER_UUID = "uuid";
	
	public static final String CUSTOMER_UUID = "cuuid";
	
	public static final int DEFAULT_CACHE_TTL = 60 * 60 * 24; // cache for 1 day
	
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	
	public static final int PAGE_SIZE = 10;
	
	public static enum CONVERSATION_TYPE {QUERY, NEW, BEGIN}
	
	public static enum BOOKING_TYPE {ACTIVE, CONFIRM, CANCEL}
	
	public static class RestPath {
		public static final String CAMPAIGN = "/campaign";
	};
	
	public static class TYPE {
		public static final String CAMPAIGN = "campaign";
		public static final String CLIENT = "client";
	};
	
	public static class FB {
		public static final String GREETING_DEFINED_PAYLOAD = "GREETING_DEFINED_PAYLOAD";
		public static final String LIKE_DEFINED_PAYLOAD = "DEFINED_PAYLOAD_LIKE";
		public static final String DISLIKE_DEFINED_PAYLOAD = "DEFINED_PAYLOAD_DISLIKE";
		public static final String BOOK_IT_DEFINED_PAYLOAD = "BOOK_IT_DEFINED_PAYLOAD";
		public static final String CONFIRM_BOOK_DEFINED_PAYLOAD = "CONFIRM_BOOK_DEFINED_PAYLOAD";
		public static final String CANCEL_BOOK_DEFINED_PAYLOAD = "CANCEL_BOOK_DEFINED_PAYLOAD";
		public static final String SUGGEST_DEFINED_PAYLOAD = "SUGGEST_DEFINED_PAYLOAD";
		public static final String LOCATION_SUGGEST_DEFINED_PAYLOAD = "LOCATION_SUGGEST_DEFINED_PAYLOAD";
	};
}