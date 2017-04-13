package com.estafet.openshift.model.entity;

import com.estafet.openshift.model.exception.DMException;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.estafet.openshift.config.Constants.*;
import static com.estafet.openshift.config.Queries.SQL_INSERT_DEV_OWNERSHIP;
import static com.estafet.openshift.config.Queries.SQL_LOAD_LAST_ACTIVE_OWNERSHIP;

/**
 * Created by Delcho Delov on 9.3.2017 г..
 */
@Entity
@Table(name=TABLE_NAME_DEVICE_OWNERSHIP, schema=SCHEMA_NAME)
public class DeviceOwnership {
		private static Logger log= Logger.getLogger(DeviceOwnership.class);
		@Id
		@GeneratedValue
		@Column(name = COL_ID, nullable = false)
		private int id;

		@Column(name = COL_CUST_ID,nullable = false)
//		@ManyToOne
//		private Customer userId;// FK to Users
		private String customerId;// customer email for the moment

		@Column(name = COL_THING_NAME,nullable = false)
		private String thingName; //thingName
		@Column(name = COL_THING_TYPE,nullable = false)
		private String thingTypeName;
		@Column(name = COL_SN,nullable = false)
		private String sn;
		@Column(name = COL_OWN,nullable = false)
		private boolean own;
		@Column(name = COL_VALID_FROM,nullable = false)
		private String validFrom; // date in format 'yyyyMMdd'
		@Column(name = COL_VALID_TO)
		private String validTo; // date in format 'yyyyMMdd'

		public DeviceOwnership() {//for JSON purposes
		}

		public DeviceOwnership(String customerId, String thingName, String thingTypeName, String sn, boolean own) {
				this.customerId = customerId;
				this.thingName = thingName;
				this.thingTypeName = thingTypeName;
				this.sn = sn;
				this.own = own;
		}

		public DeviceOwnership(String thingName) {
				this.thingName = thingName;
		}

		public int getId() {
				return id;
		}

		public void setId(int id) {
				this.id = id;
		}

		public String getCustomerId() {
				return customerId;
		}

		public String getThingName() {
				return thingName;
		}

		public boolean isOwn() {
				return own;
		}

		public String getValidFrom() {
				return validFrom;
		}

		public String getValidTo() {
				return validTo;
		}

		public void setValidFrom(Calendar calendar) {
				if(calendar!=null){
						final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
						sdf.setTimeZone(calendar.getTimeZone());
						this.validFrom = sdf.format(calendar.getTime());
				}
		}
		public void setValidTo(Calendar calendar) {
				if(calendar!=null){
						final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
						sdf.setTimeZone(calendar.getTimeZone());
						this.validTo = sdf.format(calendar.getTime());
				}
		}

		public String getThingTypeName() {
				return thingTypeName;
		}

		public String getSn() {
				return sn;
		}

		/**
		 * Search last active record fi device ONLY BY THING ID. If no such record exists returns false. If DR returns
		 * more than 1 record, this is indication to error
		 *
		 * @return true if record is loaded from DB, false-no such record
		 */
		public boolean loadLastActive(Connection conn) throws SQLException, EmptyArgumentException {
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection");
				}
				log.info(">>DeviceOwnership.loadLastActive()");
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
				boolean res = false;
				if (resultSet.next()) {
						final Double dblId = resultSet.getDouble(1);
						this.id = dblId.intValue();
						this.customerId = resultSet.getString(2);
						this.thingName = resultSet.getString(3);
						this.thingTypeName = resultSet.getString(4);
						this.sn = resultSet.getString(5);
						this.own = resultSet.getBoolean(6);
						this.validFrom = resultSet.getString(7);
						this.validTo = resultSet.getString(8);
						res = true;
						log.debug("Found " + this);
				}else{
						log.debug("Device " + thingName + " not found");
				}
				log.info("<<DeviceOwnership.loadLastActive()");
				return res;
		}

		/**
		 * Should be wrapped in transaction
		 * @param conn
		 * @throws DMException
		 * @throws SQLException
		 */
		public void writeToDb(Connection conn) throws DMException, SQLException {
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection");
				}
				log.info(">>DeviceOwnership.writeToDb()");

				PreparedStatement ps = conn.prepareStatement(SQL_INSERT_DEV_OWNERSHIP);
				ps.setString(1, customerId);
				ps.setString(2, thingTypeName);
				ps.setString(3, sn);
				ps.setBoolean(4, own);
				ps.setString(5, validFrom);
				ps.setString(6, validTo);
				ps.setString(7, thingName);

				final int i = ps.executeUpdate();
				log.info("Affected rows: " + i);
				log.info("<<DeviceOwnership.writeToDb()");
		}

		public void setCustomerId(String customerId) {
				this.customerId = customerId;
		}

		public void setThingName(String thingName) {
				this.thingName = thingName;
		}

		public void setThingTypeName(String thingTypeName) {
				this.thingTypeName = thingTypeName;
		}

		public void setSn(String sn) {
				this.sn = sn;
		}

		public void setOwn(boolean own) {
				this.own = own;
		}

		public void setValidFrom(String validFrom) {
				this.validFrom = validFrom;
		}

		public void setValidTo(String validTo) {
				this.validTo = validTo;
		}

		public Map<String, Object> asMap() {
				Map<String, Object> res = new HashMap<>(8);
				res.put(COL_ID, getId());
				res.put(COL_CUST_ID, getCustomerId());
				res.put(COL_THING_NAME, getThingName());
				res.put(COL_THING_TYPE, getThingTypeName());
				res.put(COL_SN, getSn());
				res.put(COL_OWN, isOwn());
				res.put(COL_VALID_FROM, getValidFrom());
				res.put(COL_VALID_TO, getValidTo());
				return res;
		}

}
