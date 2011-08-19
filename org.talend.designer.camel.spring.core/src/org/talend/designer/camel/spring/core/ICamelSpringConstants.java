package org.talend.designer.camel.spring.core;

public interface ICamelSpringConstants {

	// connections
	public static final int NULL_ROUTE = 0x0;
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
	public static final int WHEN = 31;
	public static final int CATCH = 32;
	public static final int FINALLY = 33;
	public static final int OTHER = 34;
	public static final int LOG = 35;
	public static final int UNKNOWN = 36;
	public static final int LENGTH = 37;

	public static final int TMP_TRY = -1;
	
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
	public static final String LB_FAILOVER_STRATEGY = "failover";
	public static final String LB_RANDOM_STRATEGY = "random";
	public static final String LB_ROUND_STRATEGY = "roundRobin";
	public static final String LB_STICKY_STRATEGY = "sticky";
	public static final String LB_TOPIC_STRATEGY = "topic";
	public static final String LB_WEIGHT_STRATEGY = "weight_strategy";
	public static final String LB_CUSTOM_STRATEGY = "custom";

	public static final String LB_FAILOVER_TYPE = "failover_type";
	public static final String LB_BASIC_TYPE = "basic_type";
	public static final String LB_EXCEPTION_TYPE = "exception_type";
	public static final String LB_EXCEPTIONS = "exceptions";
	public static final String LB_ROUND_ROBIN_TYPE = "round_robin_type";
	public static final String LB_IS_ROUND_ROBIN = "is_round_robin";
	public static final String LB_ATTAMPT_TYPE = "attampt_type";
	public static final String LB_ATTAMPT_FOREVER = "ALWAYS";
	public static final String LB_ATTAMPT_NEVER = "NEVER";
	public static final String LB_ATTAMPT_NUMBERS = "SOMETIMES";
	public static final String LB_MAXIMUM_ATTAMPTS = "maximum_attampts";
	public static final String LB_INHERIT_HANDLE = "inherit_handle";

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
	public static final String OE_EXCEPTIONS = LB_EXCEPTIONS;
	
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
	public static final String ID_FILE_REPOSITORY = "FILE";
	public static final String ID_FILE_STORE = "file_store";
	public static final String ID_MEMORY_REPOSITORY = "MEMORY";
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
	public static final String AG_COMPLETION_PREDICATE = "completion_predicate";
	public static final String AG_COMPLETION_FROM_BATCH = "completion_from_batch";
	public static final String AG_CHECK_COMPLETION = "check_completion";
	public static final String AG_IGNORE_INVALID = "ignore_invalid";
	public static final String AG_GROUP_EXCHANGES = "group_exchanges";
	public static final String AG_USE_PERSISTENCE = "use_persistance";
	public static final String AG_CLOSE_ON_COMPLETION = "close_on_completion";
	
	public static final String AG_REPOSITORY_TYPE = "repository_type";
	public static final String AG_AGGREGATION_REPO = "AGGREGATION";
	public static final String AG_RECOVERABLE_REPO = "RECOVERABLE";
	public static final String AG_REPOSITORY_NAME = "repository_name";
	public static final String AG_HAWTDB_REPO = "HAWTDB";
	public static final String AG_HAWTDB_PERSISFILE = "persistent_file";
	public static final String AG_RECOVER_INTERVAL = "recover_interval";
	public static final String AG_DEAD_LETTER_CHANNEL = "dead_letter_channel";
	public static final String AG_MAXIMUM_REDELIVERIES = "maximum_redeliveries";
	
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
	
	
	//output xml elements
	
	//ns
	public static final String BEANS_NS = "http://www.springframework.org/schema/beans";
	public static final String BEANS_XSD = "http://www.springframework.org/schema/beans/spring-beans-3.0.xsd";
	public static final String CAMEL_NS = "http://camel.apache.org/schema/spring";
	public static final String CAMEL_XSD = "http://camel.apache.org/schema/spring/camel-spring.xsd";
	public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String AMQ_NS = "http://activemq.apache.org/schema/core";
	public static final String AMQ_XSD = "http://activemq.apache.org/schema/core/activemq-core-5.5.0.xsd";
	public static final String CXF_NS = "http://camel.apache.org/schema/cxf";
	public static final String CXF_XSD = "http://camel.apache.org/schema/cxf/camel-cxf.xsd";
	public static final String XMLNS = "xmlns";
	public static final String XMLNS_XSI = "xmlns:xsi";
	public static final String XMLNS_CXF = "xmlns:cxf";
	public static final String XMLNS_AMQ = "xmlns:broker";
	public static final String NS_LOCATION = "xsi:schemaLocation";
	
	//cxf imports
	public static final String IMPORT_CXF= "classpath:META-INF/cxf/cxf.xml";
	public static final String IMPORT_SOAP= "classpath:META-INF/cxf/cxf-extension-soap.xml";
	public static final String IMPORT_JETTY= "classpath:META-INF/cxf/cxf-extension-http-jetty.xml";
	
	//elements and attributes
	public static final String BEANS_ELE = "beans";
	public static final String IMPORT_ELE = "import";
	public static final String CAMEL_CONTEXT_ELE = "camelContext";
	public static final String ROUTE_ELE = "route";
	public static final String BEAN_ELE = "bean";
	public static final String FROM_ELE = "from";
	public static final String TO_ELE = "to";
	public static final String URI_ATT = "uri";
	public static final String RESOURCE_ATT = "resource";
	public static final String SPLIT_ELE = "split";
	public static final String SETHEADER_ELE = "setHeader";
	public static final String SETBODY_ELE = "setBody";
	public static final String CONVERT_ELE = "convertBodyTo";
	public static final String ENRICH_ELE = "enrich";
	public static final String POLLENRICH_ELE = "pollEnrich";
	public static final String WIRETAP_ELE = "wireTap";
	public static final String AGGREGATE_ELE = "aggregate";
	public static final String DYNAMIC_ELE = "dynamicRouter";
	public static final String ROUTINGSLIP_ELE = "routingSlip";
	public static final String MSGROUTER_ELE = "choice";
	public static final String MSGFILTER_ELE = "filter";
	public static final String MULTICAST_ELE = "multicast";
	public static final String LOADBALANCE_ELE = "loadBalance";
	public static final String IDEMPOTENT_ELE = "idempotentConsumer";
	public static final String LOOP_ELE = "loop";
	public static final String STOP_ELE = "stop";
	public static final String DELAYER_ELE = "delay";
	public static final String EXCHANGEPATTERN_ELE = "setExchangePattern";
	public static final String PROCESSOR_ELE = "process";
	public static final String THROTTLER_ELE = "throttle";
	public static final String PIPES_ELE = "pipeline";
	public static final String INTERCEPT_ELE = "intercept";
	public static final String ONEXCEPTION_ELE = "onException";
	public static final String TRY_ELE = "doTry";
	public static final String CATCH_ELE = "doCatch";
	public static final String FINALLY_ELE = "doFinally";
	public static final String WHEN_ELE = "when";
	public static final String OTHERWISE_ELE = "otherwise";
	
}
