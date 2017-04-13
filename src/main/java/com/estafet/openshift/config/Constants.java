package com.estafet.openshift.config;


/**
 * Created by Delcho Delov on 04.04.17.
 */

public interface Constants {
		String DATE_PATTERN = "yyyyMMdd";

		String SCHEMA_NAME = "openshift";

		String DRIVER="org.postgresql.Driver";
		String CONNECTION_URL = "jdbc:postgresql://"+System.getenv("DB_HOST")+":5432/"+SCHEMA_NAME;
		String USERNAME="debil4o";
		String PASSWORD="debil4o";

		//DB tables
		String TABLE_NAME_CUSTOMER = "customer";// id; username; password; email
		String TABLE_NAME_DEVICE_OWNERSHIP = "dev_ownership";//
		String TABLE_NAME_SHADOW_DATA = "shadow_data";//thing_name;stamp;reported(json);desired(json)
		String TABLE_NAME_LEAKS_DATA = "leaks_data";

		//DB columns
		String COL_ID = "id";
		String COL_USERNAME = "username";
		String COL_PASSWORD = "password";
		String COL_EMAIL = "email";
		String COL_TSTAMP = "tstamp";
		String COL_THING_NAME = "thing_name";
		String COL_REPORTED = "reported";
		String COL_DESIRED = "desired";
		String COL_CUST_ID = "cust_id";
		String COL_VALID_FROM = "valid_from";
		String COL_VALID_TO = "valid_to";
		String COL_OWN = "own";
		String COL_THING_TYPE = "thing_type";
		String COL_SN = "sn";

		// service related
		int CLIENT_EXECUTION_TIMEOUT = 20000;

		// HTTP & parameters
		String HEADERS = "headers";
		//		public static final String TOKEN_ID = "tokenId";
		// header parameters should contains only
		String HDR_CUSTOMER_ID = "customer_id";
		String HDR_THING_NAME = "thing_name";
		String HDR_THING_TYPE = "thing_type";
		String ROLE = "role";
		String ROLE_MANAGER = "manager";
		String ROLE_CUSTOMER = "customer";
		String NEW_DEVICE_STATUS = "action";
		String RESPONCE_RESULT = "result";
		String CERTIFICATE_ARN = "certificateArn";

}