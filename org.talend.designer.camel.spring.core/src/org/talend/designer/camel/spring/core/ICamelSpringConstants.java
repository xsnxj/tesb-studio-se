package org.talend.designer.camel.spring.core;

public interface ICamelSpringConstants {

	// connections
	public static final int NULL = 0x0;
	public static final int ROUTE = 0x1;
	public static final int ROUTE_WHEN = ROUTE << 1;
	public static final int ROUTE_OTHER = ROUTE_WHEN << 1;
	public static final int ROUTE_TRY = ROUTE_OTHER << 1;
	public static final int ROUTE_CATCH = ROUTE_TRY << 1;
	public static final int ROUTE_FINALLY = ROUTE_CATCH << 1;
	public static final int ROUTE_ENDBLOCK = ROUTE_FINALLY << 1;

	//components
	public static final int INTERCEPT = 0;
	public static final int EXCEPTION = 1;
	public static final int TRY = 2;
	public static final int ACTIVEMQ = 3;
	public static final int BEAN = 4;
	public static final int CXF = 5;
	public static final int FILE = 6;
	public static final int FTP = 7;
	public static final int JMS = 8;
	public static final int MSGENDPOINT = 9;
	public static final int PF = 10;
	public static final int LOOP = 11;
	public static final int STOP = 12;
	public static final int DELAY = 13;
	public static final int PROCESSOR = 14;
	public static final int THROTTLER = 15;
	public static final int AGGREGATE = 16;
	public static final int DYNAMIC = 17;
	public static final int IDEM = 18;
	public static final int BALANCE = 19;
	public static final int FILTER = 20;
	public static final int MSGROUTER = 21;
	public static final int MULTICAST = 22;
	public static final int ROUTINGSLIP = 23;
	public static final int SPLIT = 24;
	public static final int WIRETAP = 25;
	public static final int ENRICH = 26;
	public static final int CONVERT = 27;
	public static final int SETBODY = 28;
	public static final int SETHEADER = 29;
	public static final int PATTERN = 30;
	public static final int LOG = 31;
	public static final int UNKNOWN = 32;

	//constants
	public static final String UNIQUE_NAME_ID = "UNIQUE_NAME";
	public static final String ENDPOINT_URI = "ENDPOINT_URI";
	public static final String FILE_PATH = "FILE_PATH";
	public static final String SPLIT_EXPRESS = "SPLIT_EXPRESS";
	public static final String BEAN_CLASS = "BEAN_CLASS";
	
	//LOAD BALANCE
	public static final String BALANCE_STRATEGY = "BALANCE_STRATEGY";
	public static final String FAILOVER_STRATEGY = "FAILOVER_STRATEGY";
	public static final String RANDOM_STRATEGY = "RANDOM_STRATEGY";
	public static final String ROUND_STRATEGY = "ROUND_STRATEGY";
	public static final String STICKY_STRATEGY = "STICKY_STRATEGY";
	public static final String TOPIC_STRATEGY = "TOPIC_STRATEGY";
	public static final String WEIGHT_STRATEGY = "WEIGHT_STRATEGY";
	public static final String CUSTOM_STRATEGY = "CUSTOM_STRATEGY";

	public static final String FAILOVER_TYPE = "FAILOVER_TYPE";
	public static final String BASIC_TYPE = "BASIC_TYPE";
	public static final String EXCEPTION_TYPE = "EXCEPTION_TYPE";
	public static final String EXCEPTIONS = "EXCEPTIONS";
	public static final String ROUND_ROBIN_TYPE = "ROUND_ROBIN_TYPE";
	public static final String IS_ROUND_ROBIN = "IS_ROUND_ROBIN";
	public static final String MAXIMUM_ATTAMPTS = "MAXIMUM_ATTAMPTS";

	public static final String STICKY_EXPRESSION = "STICKY_EXPRESSION";
	
	
	
	
	
}
