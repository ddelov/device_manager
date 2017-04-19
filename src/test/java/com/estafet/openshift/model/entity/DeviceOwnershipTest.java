package com.estafet.openshift.model.entity;

import org.junit.Test;

import java.util.Map;

import static com.estafet.openshift.config.Constants.*;
import static com.estafet.openshift.config.TestConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Delcho Delov on 13.04.17.
 *
 */
public class DeviceOwnershipTest {

		private final DeviceOwnership deviceOwnership = new DeviceOwnership(TEST_ID, TEST_MAIL, TEST_DEVICE_ID, TEST_TYPE, TEST_SN, TEST_OWN, TEST_VALID_FROM, TEST_VALID_TO);

		@Test
		public void asMap() throws Exception {
				final Map<String, Object> map = deviceOwnership.asMap();
				assertThat((String) map.get(COL_CUST_ID), is(TEST_MAIL));
				assertThat((String) map.get(COL_THING_NAME), is(TEST_DEVICE_ID));
				assertThat((String) map.get(COL_THING_TYPE), is(TEST_TYPE));
				assertThat((String) map.get(COL_SN), is(TEST_SN));
				assertThat((boolean) map.get(COL_OWN), is(TEST_OWN));
				assertThat((String) map.get(COL_VALID_FROM), is(TEST_VALID_FROM));
				assertThat((String) map.get(COL_VALID_TO), is(TEST_VALID_TO));
		}

}