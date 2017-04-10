package com.estafet.openshift.model.device.impl;

import com.estafet.openshift.model.device.AbstractDevice;

import java.util.HashMap;
import java.util.Map;

import static com.estafet.openshift.config.Constants.TYPE_NAME_CAMERA;

/**
 * Created by Delcho Delov on 04.04.17.
 */
public class Camera extends AbstractDevice {

    //shadow properties

    /**
     * Instantiates a new device instance.
     *
     * @param thingName the thing name
     */
    public Camera(String thingName, String serialNumber) {
        super(TYPE_NAME_CAMERA, thingName, serialNumber);
    }

    @Override
    protected Map<String, String> getSpecificReadOnlyAttributes() {
        return null;
    }

    @Override
    public Map<String, Object> getCurrentState() {
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("deviceStatus", getDeviceStatus());
        return payload;
    }

}
