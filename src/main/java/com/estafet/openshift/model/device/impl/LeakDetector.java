package com.estafet.openshift.model.device.impl;


import com.estafet.openshift.model.device.AbstractDevice;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.estafet.openshift.config.Constants.*;

/**
 * Created by Delcho Delov on 04.04.17.
 */
public class LeakDetector extends AbstractDevice {
    private static final Logger log = Logger.getLogger(LeakDetector.class);

    //shadow properties
    private boolean leak = LEAK_DEFAULT;
    private float pressure = PRESSURE_DEFAULT;

    /**
     * Instantiates a new device instance.
     *
     * @param thingName the thing name
     */
    public LeakDetector(String thingName, String serialNumber) {
        super(TYPE_NAME_LEAKDETECTOR, thingName, serialNumber);
    }

    public boolean isLeak() {
        return leak;
    }

    public void setLeak(boolean hasLeak) {
        this.leak = hasLeak;
    }

    public float getPressure() {
        log.debug("getPressure" + pressure);
        return pressure;
    }

    public void setPressure(float pressure) {
        log.debug("setPressure" + pressure);
        this.pressure = pressure;
    }

    @Override
    protected Map<String, String> getSpecificReadOnlyAttributes() {
        return null;
    }

    @Override
    public Map<String, Object> getCurrentState()
    {
        final Map<String, Object> payload = new HashMap<>(3);
        payload.put("deviceStatus", getDeviceStatus());
        payload.put("hasLeak", isLeak());
        payload.put("pressure", getPressure());
        return payload;
    }
}
