package com.estafet.openshift.model;


import com.estafet.openshift.model.device.AbstractDevice;
import com.estafet.openshift.model.device.impl.Camera;
import com.estafet.openshift.model.device.impl.LeakDetector;
import com.estafet.openshift.model.exception.UnknownDeviceTypeException;
import org.apache.log4j.Logger;

import static com.estafet.openshift.config.Constants.TYPE_NAME_CAMERA;
import static com.estafet.openshift.config.Constants.TYPE_NAME_LEAKDETECTOR;

/**
 * Created by Delcho Delov on 6.2.2017 г..
 */
public class BaseDeviceManager implements DeviceManager {
    private final static Logger log = Logger.getLogger(BaseDeviceManager.class);
    @Override
    public AbstractDevice createInstance(String deviceName, String thingTypeName, String sn) throws UnknownDeviceTypeException {
        if(thingTypeName==null){
            log.warn("Thing type name not specified. This implementation does not know which device to return.");
            return null;
        }
        if(thingTypeName.equals(TYPE_NAME_LEAKDETECTOR)){
            return new LeakDetector(deviceName, sn);
        }
        if(thingTypeName.equals(TYPE_NAME_CAMERA)){
            return new Camera(deviceName, sn);
        }
        log.warn("Unknown type device requested");
        throw new UnknownDeviceTypeException(thingTypeName);
    }
}
