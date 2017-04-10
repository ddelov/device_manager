package com.estafet.openshift.model.entity;

import com.estafet.openshift.model.exception.EmptyArgumentException;
import com.estafet.openshift.model.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.*;
import java.util.Calendar;

import static com.estafet.openshift.config.Constants.*;
import static com.estafet.openshift.config.Queries.SQL_GET_SHADOW_DATA;
import static com.estafet.openshift.config.Queries.SQL_INSERT_SHADOW_DATA;
import static com.estafet.openshift.config.Queries.SQL_UPDATE_SHADOW_DATA;

/**
 * Created by Delcho Delov on 04.04.17.
 */
@Entity
@Table(name = TABLE_NAME_SHADOW_DATA, schema = SCHEMA_NAME)
public class ShadowData {
		private static Logger log = Logger.getLogger(ShadowData.class);
		@Id
		@Column(name = COL_THING_NAME, nullable = false)
		private String thingName;

		@Column(name = COL_TSTAMP, nullable = false)
		private Timestamp tstamp;

		@Column(name = COL_REPORTED)
		private String reported;
		@Column(name = COL_DESIRED)
		private String desired;

		public ShadowData() {
		}

		public ShadowData(String thingName, String reported, String desired) {
				this.thingName = thingName;
				this.reported = reported;
				this.desired = desired;
				Calendar now = Calendar.getInstance();
				this.tstamp = new Timestamp(now.getTimeInMillis());
		}

		public String getThingName() {
				return thingName;
		}

		public void setThingName(String thingName) {
				this.thingName = thingName;
		}

		public Timestamp getTstamp() {
				return tstamp;
		}

		public void setTstamp(Timestamp tstamp) {
				this.tstamp = tstamp;
		}

		public String getReported() {
				return reported;
		}

		public void setReported(String reported) {
				this.reported = reported;
		}

		public String getDesired() {
				return desired;
		}

		public void setDesired(String desired) {
				this.desired = desired;
		}

		@Override
		public String toString() {
				return "ShadowData{" +
								"thingName='" + thingName + '\'' +
								", tstamp=" + tstamp +
								", reported='" + reported + '\'' +
								", desired='" + desired + '\'' +
								'}';
		}

		public void loadState(Connection conn) throws ResourceNotFoundException, SQLException, EmptyArgumentException {
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection");
				}
				final PreparedStatement preparedStatement = conn.prepareStatement(SQL_GET_SHADOW_DATA);
				preparedStatement.setString(1, thingName);
				final ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
						this.tstamp = resultSet.getTimestamp(1);
						this.reported = resultSet.getString(2);
						this.desired = resultSet.getString(3);
				} else {
						throw new ResourceNotFoundException("Device data not found");
				}
		}

		public void writeToDb(Connection conn) throws SQLException, EmptyArgumentException {
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection");
				}

				PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_SHADOW_DATA);
				try {
						loadState(conn);
				} catch (ResourceNotFoundException noProblem) {
						log.debug("data not found - insert as a new record");
						ps = conn.prepareStatement(SQL_INSERT_SHADOW_DATA);
				}
				ps.setString(1, reported);
				ps.setString(2, desired);
				ps.setString(3, thingName);

				final int i = ps.executeUpdate();
				log.debug("Affected rows: " + i);
		}
}