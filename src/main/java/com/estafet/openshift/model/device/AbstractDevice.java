package com.estafet.openshift.model.device;

import com.estafet.openshift.model.enumerations.DeviceStatus;

import java.util.Map;

/**
 * Created by Delcho Delov on 06.04.17.
 */
public abstract class AbstractDevice {
		protected final String thingType;
		protected final String thingName;
		protected final String serialNumber;
		protected DeviceStatus deviceStatus;

		public AbstractDevice(String thingType, String thingName, String serialNumber) {
				this.thingType = thingType;
				this.thingName = thingName;
				this.serialNumber = serialNumber;
		}

		public String getThingType() {
				return thingType;
		}

		public String getThingName() {
				return thingName;
		}

		public String getSerialNumber() {
				return serialNumber;
		}

		public DeviceStatus getDeviceStatus() {
				return deviceStatus;
		}

		public void setDeviceStatus(DeviceStatus deviceStatus) {
				this.deviceStatus = deviceStatus;
		}
		protected abstract Map<String, String> getSpecificReadOnlyAttributes();

		public abstract Map<String, Object> getCurrentState();

}
