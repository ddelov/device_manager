package com.estafet.openshift.model.entity;

import javax.persistence.*;

import static com.estafet.openshift.config.Constants.*;

/**
 * Created by Delcho Delov on 04.04.17.
 */
@Entity
@Table(name= TABLE_NAME_LEAKS_DATA, schema=SCHEMA_NAME)
public class LeakData {
		@Id
		@GeneratedValue
		@Column(name = COL_ID, nullable = false)
		private int id;


		@Column(name = COL_THING_NAME, nullable = false)
		private String thingName;
		@Column(name = COL_LEAK_DETECTED, nullable = false)
		private boolean leakDetected = false;
		@Column(name = COL_PRESSURE, nullable = false)
		private float pressure = 0.0f;
		@Column(name = COL_TSTAMP, nullable = false)
		private long stamp;

		public long getId() {
				return id;
		}

		public void setId(int id) {
				this.id = id;
		}

		public String getThingName() {
				return thingName;
		}

		public void setThingName(String thingName) {
				this.thingName = thingName;
		}

		public boolean isLeakDetected() {
				return leakDetected;
		}

		public void setLeakDetected(boolean leakDetected) {
				this.leakDetected = leakDetected;
		}

		public float getPressure() {
				return pressure;
		}

		public void setPressure(float pressure) {
				this.pressure = pressure;
		}

		public long getStamp() {
				return stamp;
		}

		public void setStamp(long stamp) {
				this.stamp = stamp;
		}

		@Override
		public String toString() {
				return "LeakData{" +
								"id=" + id +
								", thingName='" + thingName + '\'' +
								", leakDetected=" + leakDetected +
								", pressure=" + pressure +
								", stamp=" + stamp +
								'}';
		}
}