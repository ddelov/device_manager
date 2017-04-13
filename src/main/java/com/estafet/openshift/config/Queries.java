package com.estafet.openshift.config;

import static com.estafet.openshift.config.Constants.*;

/**
 * Created by Delcho Delov on 06.04.17.
 */
public interface Queries {
		//SQL statements
		String SQL_CHECK_TBL_CUST_EXIST = "SELECT 1 " +
						"   FROM   pg_tables" +
						"   WHERE  schemaname = '" + SCHEMA_NAME + "'" +
						"   AND    tablename = '" + TABLE_NAME_CUSTOMER + "'";
		String SQL_CREATE_TABLE_CUST = "CREATE TABLE "+ SCHEMA_NAME + '.' + TABLE_NAME_CUSTOMER +
						" ("+COL_ID+" SERIAL NOT NULL PRIMARY KEY, "
						+COL_USERNAME+" varchar(100) NOT NULL, "
						+COL_PASSWORD+" varchar(100) NOT NULL, "
						+COL_EMAIL+" varchar(100) NOT NULL"
						+ ")";
		String SQL_INSERT_CUSTOMER = "insert into " + SCHEMA_NAME + '.' + TABLE_NAME_CUSTOMER +
						" ("+ COL_USERNAME +","+COL_PASSWORD+","+ COL_EMAIL +") values(?,?,?)";
		String SQL_LOAD_CUSTOMER = "select "+ COL_ID +","+COL_PASSWORD+","+ COL_EMAIL +
						" from " + SCHEMA_NAME + '.' + TABLE_NAME_CUSTOMER +
						" where " + COL_USERNAME + " like ?";

		String SQL_LOAD_LAST_ACTIVE_OWNERSHIP = "select " + COL_ID +"," + COL_CUST_ID +"," +
						COL_THING_TYPE +","+COL_SN +","+COL_OWN +","+COL_VALID_FROM +","+COL_VALID_TO+
						" from "+SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP+
						" where "+COL_THING_NAME+" like ? and ("+
							//all next parameters are today(formatted)
							" ("+COL_VALID_FROM +" <= ? and "+ COL_VALID_TO + " is NULL )"+
							" or ("+ COL_VALID_TO+" >= ? )"+
						")";

		String SQL_INSERT_DEV_OWNERSHIP = "insert into " + SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP +
						" ("+ COL_CUST_ID +","+ COL_THING_TYPE +","+COL_SN +","+COL_OWN +","+COL_VALID_FROM+","+COL_VALID_TO+","+COL_THING_NAME+
						" ) values (?,?,?,?,?,?,?)";
		String SQL_MARK_DEV_OWNERSHIP_INVALID = "update "+SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP +
						" set "+ COL_VALID_TO+" = ? where "+ COL_ID + " = ?";
		String SQL_GET_ALL_DEV_OWNERSHIP = "select " + COL_ID +", "+ COL_THING_NAME +","+ COL_THING_TYPE +","+COL_SN +","+COL_OWN +","+COL_VALID_FROM+","+COL_VALID_TO+","+COL_CUST_ID+
						" from "+SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP;
		String SQL_GET_DEV_OWNERSHIP_BY_CUSTOMER = "select "+ COL_ID +", " + COL_THING_NAME +","+ COL_THING_TYPE +","+COL_SN +","+COL_OWN +","+COL_VALID_FROM+","+COL_VALID_TO+","+COL_CUST_ID+
						" from "+SCHEMA_NAME + '.' + TABLE_NAME_DEVICE_OWNERSHIP+
						" where "+COL_CUST_ID+" like ?";

		String SQL_GET_SHADOW_DATA = "select "+ COL_TSTAMP+","+ COL_REPORTED +","+COL_DESIRED+
						" from "+SCHEMA_NAME + '.' + TABLE_NAME_SHADOW_DATA+" where "+COL_THING_NAME + " LIKE ?";
		String SQL_INSERT_SHADOW_DATA = "insert into " + SCHEMA_NAME + '.' + TABLE_NAME_SHADOW_DATA +
						" ("+ COL_REPORTED +","+COL_DESIRED+","+ COL_THING_NAME +","+COL_TSTAMP
						+" ) values(?,?,?,localtimestamp)";
		String SQL_UPDATE_SHADOW_DATA = "update "+SCHEMA_NAME + '.' + TABLE_NAME_SHADOW_DATA +
						" set "+COL_REPORTED + " = ?, "+COL_DESIRED + " = ?, "+COL_TSTAMP+" = localtimestamp where "+
						COL_THING_NAME + " = ?";

}
