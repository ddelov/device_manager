package com.estafet.openshift.model.entity;

import javax.persistence.*;

import static com.estafet.openshift.config.Constants.*;

/**
 * Created by Delcho Delov on 04.04.17.
 */
@Entity
@Table(name= TABLE_NAME_CUSTOMER, schema=SCHEMA_NAME)
public class Customer {
		@Id
		@GeneratedValue
		@Column(name = COL_ID, nullable = false)
		private int id;

		@Column(name = COL_EMAIL,nullable = false, length = 100)
		private String email;//used as a unique id for business purposes

		@Column(name = COL_USERNAME,nullable = false, length = 100)
		private String username;
		@Column(name = COL_PASSWORD,nullable = false, length = 100)
		private String password;

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

		@Override
		public int hashCode() {
				return getEmail().hashCode();
		}
}