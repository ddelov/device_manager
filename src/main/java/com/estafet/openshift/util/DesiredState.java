package com.estafet.openshift.util;

import java.io.Serializable;
import java.util.Map;

/**
 * Transfer in opposite direction is presented by {@link ReportedState}
 * The only reason to exist both classes is JSON de/serialization - they are simpler replacement of (otherwise needed)
 * serializer and deserializer classes
 *
 * Created by Delcho Delov on 12/12/16.
 */
public final class DesiredState implements Serializable{
    final private Map<String, Object> desired;

		DesiredState(Map<String, Object> payload) {
            this.desired = payload;
        }

    Map<String, Object> getDesired() {
            return desired;
        }

}
