package com.estafet.openshift.util;

/**
 * Created by Delcho Delov on 04.04.17.
 */

import com.estafet.openshift.config.Constants;
import com.estafet.openshift.model.entity.DeviceOwnership;
import com.estafet.openshift.model.exception.DMException;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import com.estafet.openshift.model.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.estafet.openshift.config.Constants.DATE_PATTERN;
import static com.estafet.openshift.config.Queries.SQL_INSERT_DEV_OWNERSHIP;
import static com.estafet.openshift.config.Queries.SQL_LOAD_LAST_ACTIVE_OWNERSHIP;
import static com.estafet.openshift.util.Utils.isEmpty;

public class PersistenceProvider {
		private Connection con=null;
		private static final Logger log = Logger.getLogger(PersistenceProvider.class);
		static{
				try{
						log.debug("PersistenceProvider static initializer");
						Class.forName(Constants.DRIVER);
						log.info("============ DB Connection URL: "+ Constants.CONNECTION_URL);
				}catch(Exception e){
						log.error(e.getMessage(), e);
				}
		}

		public Connection getCon(){
				try {
						if(con==null || con.isClosed()){
								con=DriverManager.getConnection(Constants.CONNECTION_URL, Constants.USERNAME, Constants.PASSWORD);
								log.debug("Connection established");
								con.setAutoCommit(false);
						}
				} catch (SQLException e) {
						log.error(e.getMessage(), e);
				}
				log.debug("returns connection: " + con);
				return con;
		}
		/**
		 * Search last active record fi device ONLY BY THING ID. If no such record exists returns false. If DR returns
		 * more than 1 record, this is indication to error
		 *
		 * @return true if record is loaded from DB, false-no such record
		 */
		public DeviceOwnership loadDeviceOwnership(String thingName, Connection conn) throws SQLException, EmptyArgumentException, ResourceNotFoundException {
				log.debug(">> loadDeviceOwnership()");
				if(isEmpty(thingName)){
						throw new EmptyArgumentException("Parameter thingName is mandatory");
				}
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection");
				}
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				today.set(Calendar.MILLISECOND, 0);
				final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
				sdf.setTimeZone(today.getTimeZone());
				final String now = sdf.format(today.getTime());

				final PreparedStatement preparedStatement = conn.prepareStatement(SQL_LOAD_LAST_ACTIVE_OWNERSHIP);
				preparedStatement.setString(1, thingName);
				preparedStatement.setString(2, now);
				preparedStatement.setString(3, now);
				final ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
						final Double dblId = resultSet.getDouble(1);
						int id = dblId.intValue();
						final String customerId = resultSet.getString(2);
						final String thingTypeName = resultSet.getString(3);
						final String sn = resultSet.getString(4);
						boolean own = resultSet.getBoolean(5);
						final String validFrom = resultSet.getString(6);
						final String validTo = resultSet.getString(7);
						final DeviceOwnership deviceOwnership = new DeviceOwnership(id, customerId, thingName, thingTypeName, sn, own, validFrom, validTo);
						log.debug("Found " + deviceOwnership);
						return deviceOwnership;
				}else{
						log.debug("Device " + thingName + " not found");
						throw new ResourceNotFoundException("Device " + thingName + " not found");
				}
		}

		/**
		 * Should be wrapped in transaction
		 * @param conn
		 * @throws DMException
		 * @throws SQLException
		 */
		public void writeDeviceOwnership(DeviceOwnership deviceOwnership, Connection conn) throws DMException, SQLException {
				log.info(">> writeDeviceOwnership()");
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection is mandatory parameter");
				}
				final String customerId = deviceOwnership.getCustomerId();
				final String thingName = deviceOwnership.getThingName();
				final String typeName = deviceOwnership.getThingTypeName();
				final String sn = deviceOwnership.getSn();
				final String validFrom = deviceOwnership.getValidFrom();
				if(isEmpty(customerId)||isEmpty(thingName)||isEmpty(typeName)||isEmpty(sn)||isEmpty(validFrom)){
						throw new EmptyArgumentException("DeviceOwnership instance is not complete");
				}
				final boolean own = deviceOwnership.isOwn();
				final String validTo = deviceOwnership.getValidTo();

				PreparedStatement ps = conn.prepareStatement(SQL_INSERT_DEV_OWNERSHIP);
				ps.setString(1, customerId);
				ps.setString(2, typeName);
				ps.setString(3, sn);
				ps.setBoolean(4, own);
				ps.setString(5, validFrom);
				ps.setString(6, validTo);
				ps.setString(7, thingName);

				final int i = ps.executeUpdate();
				log.info("Affected rows: " + i);
				log.info("<<DeviceOwnership.writeToDb()");
		}


}