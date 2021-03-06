package com.estafet.openshift.dm.config;


/**
 * Created by Delcho Delov on 04.04.17.
 *
 */
public interface Constants {
		String DATE_PATTERN = "yyyyMMdd";

		String SCHEMA_NAME = "openshift";

		String DRIVER = "org.postgresql.Driver";
		String CONNECTION_URL = "jdbc:postgresql://" + System.getenv("DB_HOST") + ":5432/" + SCHEMA_NAME;
		String USERNAME = "debil4o";
		String PASSWORD = "debil4o";

		//DB tables
		String TABLE_NAME_DEVICE_OWNERSHIP = "dev_ownership";

		//DB columns
		String COL_ID = "id";
		String COL_THING_NAME = "thing_name";
		String COL_CUST_ID = "customer_id";
		String COL_VALID_FROM = "valid_from";
		String COL_VALID_TO = "valid_to";
		String COL_OWN = "own";
		String COL_THING_TYPE = "thing_type";
		String COL_SN = "sn";
		String COL_STATUS = "status";

		// HTTP & parameters
		String HDR_CUSTOMER_ID = "customer_id";
		String ROLE = "role";
		String ROLE_MANAGER = "manager";
		int INVALID_ID = -1;

		String THING_NAME = "thingName";
		String THING_TYPE = "thingType";
		String VALID_FROM = "validFrom";
		String VALID_TO = "validTo";
		String CUSTOMER_ID = "customerId";
		String DEVICE_STATUS = "deviceStatus";
		String DEVICE_STATUS_DEFAULT = "ON";
		String DESIRED_STATUS = "action";

		String PUT_URL = System.getenv("PUT_URL");//"http://iot-reg-iot-registry.192.168.42.182.nip.io/register";
		String DELETE_URL = System.getenv("DELETE_URL");//"http://iot-reg-iot-registry.192.168.42.182.nip.io/delete";
}