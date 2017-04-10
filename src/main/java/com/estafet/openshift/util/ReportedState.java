package com.estafet.openshift.util;

import java.io.Serializable;
import java.util.Map;

/**
 * Transfer in opposite direction is presented by {@link DesiredState}
 * Shifted parameters are part of the internal map
 * The only reason to exist both classes is JSON de/serialization - they are simpler replacement of (otherwise needed)
 * serializer and deserializer classes
 * <p>
 * Created by Delcho Delov on 12/12/16.
 */
public final class ReportedState implements Serializable {
		final private Map<String, Object> reported;

		public ReportedState(Map<String, Object> payload) {
				this.reported = payload;
		}

		Map<String, Object> getReported() {
				return reported;
		}

}
