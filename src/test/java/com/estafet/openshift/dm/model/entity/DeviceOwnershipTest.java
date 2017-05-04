package com.estafet.openshift.dm.model.entity;

import com.estafet.openshift.dm.config.Constants;
import com.estafet.openshift.dm.config.TestConstants;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Delcho Delov on 13.04.17.
 *
 */
public class DeviceOwnershipTest {

		private final DeviceOwnership deviceOwnership = new DeviceOwnership(TestConstants.TEST_ID, TestConstants.TEST_MAIL, TestConstants.TEST_DEVICE_ID, TestConstants.TEST_TYPE, TestConstants.TEST_SN, TestConstants.TEST_OWN, TestConstants.TEST_VALID_FROM, TestConstants.TEST_VALID_TO, Constants.DEVICE_STATUS_DEFAULT);

		@Test
		public void asMap() throws Exception {
				final Map<String, Object> map = deviceOwnership.asMap();
				assertThat((String) map.get(Constants.CUSTOMER_ID), CoreMatchers.is(TestConstants.TEST_MAIL));
				assertThat((String) map.get(Constants.THING_NAME), CoreMatchers.is(TestConstants.TEST_DEVICE_ID));
				assertThat((String) map.get(Constants.THING_TYPE), CoreMatchers.is(TestConstants.TEST_TYPE));
				assertThat((String) map.get(Constants.COL_SN), CoreMatchers.is(TestConstants.TEST_SN));
				assertThat((boolean) map.get(Constants.COL_OWN), CoreMatchers.is(TestConstants.TEST_OWN));
				assertThat((String) map.get(Constants.VALID_FROM), CoreMatchers.is(TestConstants.TEST_VALID_FROM));
				assertThat((String) map.get(Constants.VALID_TO), CoreMatchers.is(TestConstants.TEST_VALID_TO));
		}

}