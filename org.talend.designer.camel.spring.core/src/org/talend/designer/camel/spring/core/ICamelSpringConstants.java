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

	// components
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

	// constants
	public static final String UNIQUE_NAME_ID = "UNIQUE_NAME";
	public static final String ENDPOINT_URI = "ENDPOINT_URI";
	public static final String FILE_PATH = "FILE_PATH";
	public static final String SPLIT_EXPRESS = "SPLIT_EXPRESS";
	
	//bean
	public static final String BEAN_CLASS = "BEAN_CLASS";
	public static final String BEAN_METHOD = "BEAN_METHOD";
	

	// LOAD BALANCE
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

	// expression
	public static final String EXPRESSION_TEXT = "EXPRESSION_TEXT";
	public static final String EXPRESSION_TYPE = "EXPRESSION_TYPE";
	public static final String CONSTANT_EXPRESSION = "constant";
	public static final String EL_EXPRESSION = "el";
	public static final String GROOVY_EXPRESSION = "groovy";
	public static final String HEADER_EXPRESSION = "header";
	public static final String JS_EXPRESSION = "javascript";
	public static final String JXPATH_EXPRESSION = "jxpath";
	public static final String LANGUAGE_EXPRESSION = "language";
	public static final String METHODCALL_EXPRESSION = "methodcall";
	public static final String MVEL_EXPRESSION = "mvel";
	public static final String XPATH_EXPRESSION = "xpath";
	public static final String XQUERY_EXPRESSION = "xquery";
	public static final String OGNL_EXPRESSION = "ognl";
	public static final String PHP_EXPRESSION = "php";
	public static final String PROPERTY_EXPRESSION = "property";
	public static final String PYTHON_EXPRESSION = "python";
	public static final String RUBY_EXPRESSION = "ruby";
	public static final String SIMPLE_EXPRESSION = "simple";
	public static final String SPEL_EXPRESSION = "spel";
	public static final String SQL_EXPRESSION = "sql";
	public static final String TOKENIZER_EXPRESSION = "tokenizer";
	
	//onexception
	public static final String MAX_REDELIVER_TIMES = "redeliver_times";
	public static final String MAX_REDELIVER_DELAY = "redeliver_delay";
	public static final String USE_ORIGINAL_MSG = "use_original_msg";
	public static final String ASYNC_DELAY_REDELIVER = "async_delay_redeliver";
	public static final String EXCEPTION_BEHAVIOR = "exception_behavior";
	public static final String HANDLE_EXCEPTION = "handle_exception";
	public static final String CONTINUE_EXCEPTION = "continue_exception";
	
	//set header
	public static final String HEADER_NAME = "header_name";

	//convert
	public static final String TARGET_TYPE_CLASS = "target_type_class";
	
	//throttler
	public static final String TIME_PERIOD_MILL = "time_period_mill";
	public static final String MAX_REQUEST_PER_PERIOD = "max_request_per_period";
	public static final String ASYNC_DELAY = "async_delay";
	
	//routing slip
	public static final String URI_DELIMITER = "uri_delimitr";
	
	//exchange pattern
	public static final String EXCHANGE_TYPE = "exchange_type";
	public static final String INONLY = "InOnly";
	public static final String ROBUST_INONLY="RobustInOnly";
	public static final String INOUT = "InOut";
	public static final String INOPTIONOUT= "InOptionalOut";
	public static final String OUTONLY = "OutOnly";
	public static final String ROBUSTOUTONLY= "RobustOutOnly";
	public static final String OUTIN = "OutIn";
	public static final String OUTOPTIONALIN = "OutOptionalIn";
}
