package com.estafet.openshift.model.exception;

/**
 * Created by Delcho Delov on 8.2.2017 г..
 */
public class UnknownDeviceTypeException extends DMException {
		private String thingTypeName;
		public UnknownDeviceTypeException(String thingTypeName) {
				super("Unknown device type requested " + thingTypeName);
		}

		public String getThingTypeName() {
				return thingTypeName;
		}

		public void setThingTypeName(String thingTypeName) {
				this.thingTypeName = thingTypeName;
		}
}
