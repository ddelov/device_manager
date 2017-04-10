package com.estafet.openshift.model;


import com.estafet.openshift.model.device.AbstractDevice;
import com.estafet.openshift.model.exception.UnknownDeviceTypeException;

/**
 * Created by Delcho Delov on 6.2.2017 г..
 */
public interface DeviceManager {
    <T extends AbstractDevice> T createInstance(String deviceName, String thingTypeName, String sn) throws UnknownDeviceTypeException;
}
