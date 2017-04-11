package com.estafet.openshift.model.entity;

import com.estafet.openshift.model.exception.DMException;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.estafet.openshift.config.Constants.*;
import static com.estafet.openshift.config.Queries.SQL_INSERT_CUSTOMER;
import static com.estafet.openshift.config.Queries.SQL_LOAD_CUSTOMER;

/**
 * Created by Delcho Delov on 04.04.17.
 */
@Entity
@Table(name= TABLE_NAME_CUSTOMER, schema=SCHEMA_NAME)
public class Customer {
		private static Logger log = Logger.getLogger(Customer.class);
		@Id
		@GeneratedValue
		@Column(name = COL_ID, nullable = false)
		private int id;

		@Column(name = COL_EMAIL,nullable = false, length = 100)
		private String email;//restriction in DB: UNIQUE

		@Column(name = COL_USERNAME,nullable = false, length = 100)
		private String username;
		@Column(name = COL_PASSWORD,nullable = false, length = 100)
		private String password;

		public Customer() {
				//for JSON only
		}
		public Customer(String username) {
				this.username = username;
		}

		public String getUsername() {
				return username;
		}

		public void setUsername(String username) {
				this.username = username;
		}

		public String getPassword() {
				return password;
		}

		public void setPassword(String password) {
				this.password = password;
		}

		public String getEmail() {
				return email;
		}

		public void setEmail(String email) {
				this.email = email;
		}

		public long getId() {
				return id;
		}

		public void setId(int id) {
				this.id = id;
		}

		@Override
		public String toString() {
				return "Customer{" +
								"id=" + id +
								", username='" + username + '\'' +
								", password='" + password + '\'' +
								", email='" + email + '\'' +
								'}';
		}

		@Override
		public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;

				Customer customer = (Customer) o;

				return getEmail().equals(customer.getEmail());
		}
		public boolean loadByUsername(Connection conn) throws SQLException, EmptyArgumentException {
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection");
				}
				log.info(">> Customer.loadByUsername()");
				final PreparedStatement preparedStatement = conn.prepareStatement(SQL_LOAD_CUSTOMER);
				preparedStatement.setString(1, username);
				final ResultSet resultSet = preparedStatement.executeQuery();
				boolean res = false;
				if (resultSet.next()) {
						final Double dblId = resultSet.getDouble(1);
						this.id = dblId.intValue();
						this.password = resultSet.getString(2);
						this.email = resultSet.getString(3);
						res = true;
				}
				log.info("<< Customer.loadByUsername()");
				return res;
		}

		public void writeToDb(Connection conn) throws DMException, SQLException {
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection");
				}
				log.info(">>Customer.writeToDb()");
				PreparedStatement ps = conn.prepareStatement(SQL_INSERT_CUSTOMER);
				ps.setString(1, username);
				ps.setString(2, password);
				ps.setString(3, email);

				final int i = ps.executeUpdate();
				log.info("Affected rows: " + i);
				log.info("<<Customer.writeToDb()");

		}
				@Override
		public int hashCode() {
				return getEmail().hashCode();
		}
}