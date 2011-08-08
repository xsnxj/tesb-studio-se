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
	public static final String UNIQUE_NAME_ID = "unique_name";
	public static final String ENDPOINT_URI = "endpoint_uri";
	public static final String FILE_PATH = "file_path";
	
	//pipe
	public static final String PF_DESTINATIONS = "destinations";

	//split
	public static final String SP_SPLIT_EXPRESS = "split_express";
	
	//bean
	public static final String BN_BEAN_CLASS = "bean_class";
	public static final String BN_BEAN_METHOD = "bean_method";
	

	// load balance
	public static final String LB_BALANCE_STRATEGY = "balance_strategy";
	public static final String LB_FAILOVER_STRATEGY = "failover_strategy";
	public static final String LB_RANDOM_STRATEGY = "random_strategy";
	public static final String LB_ROUND_STRATEGY = "round_strategy";
	public static final String LB_STICKY_STRATEGY = "sticky_strategy";
	public static final String LB_TOPIC_STRATEGY = "topic_strategy";
	public static final String LB_WEIGHT_STRATEGY = "weight_strategy";
	public static final String LB_CUSTOM_STRATEGY = "custom_strategy";

	public static final String LB_FAILOVER_TYPE = "failover_type";
	public static final String LB_BASIC_TYPE = "basic_type";
	public static final String LB_EXCEPTION_TYPE = "exception_type";
	public static final String LB_EXCEPTIONS = "exceptions";
	public static final String LB_ROUND_ROBIN_TYPE = "round_robin_type";
	public static final String LB_IS_ROUND_ROBIN = "is_round_robin";
	public static final String LB_MAXIMUM_ATTAMPTS = "maximum_attampts";

	public static final String LB_STICKY_EXPRESSION = "sticky_expression";

	// expression
	public static final String EP_EXPRESSION_TEXT = "expression_text";
	public static final String EP_EXPRESSION_TYPE = "expression_type";
	public static final String EP_CONSTANT_EXPRESSION = "constant";
	public static final String EP_EL_EXPRESSION = "el";
	public static final String EP_GROOVY_EXPRESSION = "groovy";
	public static final String EP_HEADER_EXPRESSION = "header";
	public static final String EP_JS_EXPRESSION = "javascript";
	public static final String EP_JXPATH_EXPRESSION = "jxpath";
	public static final String EP_LANGUAGE_EXPRESSION = "language";
	public static final String EP_METHODCALL_EXPRESSION = "methodcall";
	public static final String EP_MVEL_EXPRESSION = "mvel";
	public static final String EP_XPATH_EXPRESSION = "xpath";
	public static final String EP_XQUERY_EXPRESSION = "xquery";
	public static final String EP_OGNL_EXPRESSION = "ognl";
	public static final String EP_PHP_EXPRESSION = "php";
	public static final String EP_PROPERTY_EXPRESSION = "property";
	public static final String EP_PYTHON_EXPRESSION = "python";
	public static final String EP_RUBY_EXPRESSION = "ruby";
	public static final String EP_SIMPLE_EXPRESSION = "simple";
	public static final String EP_SPEL_EXPRESSION = "spel";
	public static final String EP_SQL_EXPRESSION = "sql";
	public static final String EP_TOKENIZER_EXPRESSION = "tokenizer";
	
	//onexception
	public static final String OE_MAX_REDELIVER_TIMES = "redeliver_times";
	public static final String OE_MAX_REDELIVER_DELAY = "redeliver_delay";
	public static final String OE_USE_ORIGINAL_MSG = "use_original_msg";
	public static final String OE_ASYNC_DELAY_REDELIVER = "async_delay_redeliver";
	public static final String OE_EXCEPTION_BEHAVIOR = "exception_behavior";
	public static final String OE_HANDLE_EXCEPTION = "handle_exception";
	public static final String OE_CONTINUE_EXCEPTION = "continue_exception";
	
	//set header
	public static final String SH_HEADER_NAME = "header_name";

	//convert
	public static final String CV_TARGET_TYPE_CLASS = "target_type_class";
	
	//throttler
	public static final String TH_TIME_PERIOD_MILL = "time_period_mill";
	public static final String TH_MAX_REQUEST_PER_PERIOD = "max_request_per_period";
	public static final String TH_ASYNC_DELAY = "async_delay";
	
	//routing slip
	public static final String RS_URI_DELIMITER = "uri_delimitr";
	
	//exchange pattern
	public static final String EX_EXCHANGE_TYPE = "exchange_type";
	public static final String EX_INONLY = "InOnly";
	public static final String EX_ROBUST_INONLY="RobustInOnly";
	public static final String EX_INOUT = "InOut";
	public static final String EX_INOPTIONOUT= "InOptionalOut";
	public static final String EX_OUTONLY = "OutOnly";
	public static final String EX_ROBUSTOUTONLY= "RobustOutOnly";
	public static final String EX_OUTIN = "OutIn";
	public static final String EX_OUTOPTIONALIN = "OutOptionalIn";
	
	//activemq
	public static final String AMQ_MESSAGE_TYPE = "message_type";
	public static final String AMQ_MSG_DESTINATION = "msg_destination";
	
	//ftp
	public static final String FTP_SCHEMA_TYPE = "ftp_schema_type";
	public static final String FTP_USERNAME = "ftp_username";
	public static final String FTP_PASSWORD = "ftp_password";
	public static final String FTP_SERVER = "ftp_server";
	public static final String FTP_PORT = "ftp_port";
	public static final String FTP_DIRECTORY = "ftp_directory";
	
	//idem
	public static final String ID_REPOSITORY_TYPE = "repository_type";
	public static final String ID_FILE_REPOSITORY = "File";
	public static final String ID_FILE_STORE = "file_store";
	public static final String ID_MEMORY_REPOSITORY = "Memory";
	public static final String ID_CACHE_SIZE = "cache_size";
	
	//wiretap
	public static final String WT_WIRETAP_COPY="wireTap_copy";
	public static final String WT_POPULATE_TYPE="populate_type";
	public static final String WT_NEW_EXPRESSION_POP="new_expression_pop";
	public static final String WT_NEW_PROCESSOR_POP="new_processor_pop";
	
	//aggregate
	public static final String AG_AGGREGATE_STRATEGY = "aggregate_strategy";
	public static final String AG_COMPLETION_SIEZE = "completion_size";
	public static final String AG_COMPLETION_TIMEOUT = "completion_timeout";
	public static final String AG_COMPLETION_INTERVAL = "completion_interval";
	public static final String AG_COMPLETION_FROM_BATCH = "completion_from_batch";
	public static final String AG_CHECK_COMPLETION = "check_completion";
	public static final String AG_IGNORE_INVALID = "ignore_invalid";
	public static final String AG_GROUP_EXCHANGES = "group_exchanges";
	public static final String AG_USE_PERSISTENCE = "use_persistance";
	public static final String AG_CLOSE_ON_COMPLETION = "close_on_completion";
	
	//content enrich
	public static final String ER_AGGREGATE_STRATEGY = AG_AGGREGATE_STRATEGY;
	public static final String ER_MERGE_DATA = "merge_data";
	public static final String ER_PRODUCER = "producer";
	public static final String ER_CONSUMER = "consumer";
	public static final String ER_TIMEOUT_STYLE = "time_out_style";
	public static final String ER_WAIT_UNTIL = "wait_until";
	public static final String ER_POLL_IMMED = "poll_immediatly";
	public static final String ER_WAIT_TIMEOUT = "wait_timeout";
	public static final String ER_RESOUCE_URI = "resource_uri";
	
	//dynamic
	public static final String DY_BEAN_CLASS = BN_BEAN_CLASS;
	public static final String DY_BEAN_METHOD = BN_BEAN_METHOD;
	
	//multicast
	public static final String ML_DESTINATIONS = PF_DESTINATIONS;
	public static final String ML_IS_PARALLEL = "is_parallel";
	public static final String ML_AGGREGATE_STRATEGY = ER_AGGREGATE_STRATEGY;
	public static final String ML_TIMEOUT = "mt_timeout";
	
	//jms
	public static final String JMS_SCHEMA_NAME = "jms_schema";
	public static final String JMS_TYPE = "jms_type";
	public static final String JMS_DESTINATION = "jms_destination";
}
