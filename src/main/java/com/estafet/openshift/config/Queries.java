package com.estafet.openshift.config;

import static com.estafet.openshift.config.Constants.*;

/**
 * Created by Delcho Delov on 06.04.17.
 *
 */
public interface Queries {
		//SQL statements
		String SQL_LOAD_LAST_ACTIVE_OWNERSHIP = "select " + COL_ID + "," + COL_CUST_ID + "," +
						COL_THING_TYPE + "," + COL_SN + "," + COL_OWN + "," + COL_VALID_FROM + "," + COL_VALID_TO +
						" from " + SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP +
						" where " + COL_THING_NAME + " like ? and (" +
						//all next parameters are today(formatted)
						" (" + COL_VALID_FROM + " <= ? and " + COL_VALID_TO + " is NULL )" +
						" or (" + COL_VALID_TO + " > ? )" +
						")";

		String SQL_INSERT_DEV_OWNERSHIP = "insert into " + SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP +
						" (" + COL_CUST_ID + "," + COL_THING_TYPE + "," + COL_SN + "," + COL_OWN + "," + COL_VALID_FROM + "," + COL_VALID_TO + "," + COL_THING_NAME +
						" ) values (?,?,?,?,?,?,?)";
		String SQL_MARK_DEV_OWNERSHIP_INVALID = "update " + SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP +
						" set " + COL_VALID_TO + " = ? where " + COL_ID + " = ?";
		String SQL_GET_ALL_DEV_OWNERSHIP = "select " + COL_ID + ", " + COL_THING_NAME + "," + COL_THING_TYPE + "," + COL_SN + "," + COL_OWN + "," + COL_VALID_FROM + "," + COL_VALID_TO + "," + COL_CUST_ID +
						" from " + SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP +
						" where (" +
						//all next parameters are today(formatted)
						" (" + COL_VALID_FROM + " <= ? and " + COL_VALID_TO + " is NULL )" +
						" or (" + COL_VALID_TO + " > ? )" +
						" )";
		String SQL_GET_DEV_OWNERSHIP_BY_CUSTOMER = "select " + COL_ID + ", " + COL_THING_NAME + "," + COL_THING_TYPE + "," + COL_SN + "," + COL_OWN + "," + COL_VALID_FROM + "," + COL_VALID_TO + "," + COL_CUST_ID +
						" from " + SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP +
						" where (" +
						//all next parameters are today(formatted)
						" (" + COL_VALID_FROM + " <= ? and " + COL_VALID_TO + " is NULL )" +
						" or (" + COL_VALID_TO + " > ? )" +
						" ) and " + COL_CUST_ID + " like ?";

}
