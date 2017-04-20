package com.estafet.openshift.registry;

/**
 * Created by Delcho Delov on 19.04.17.
 */
public interface Rule{
		void onUpdate(String payload);
		String getEndpoint();
}
