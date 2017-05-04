package com.estafet.openshift.dm.rest;

import com.estafet.openshift.dm.config.Constants;
import com.estafet.openshift.dm.model.entity.DeviceOwnership;
import com.estafet.openshift.dm.model.exception.DMException;
import com.estafet.openshift.dm.model.exception.EmptyArgumentException;
import com.estafet.openshift.dm.model.exception.ResourceNotFoundException;
import com.estafet.openshift.dm.util.PersistenceProvider;
import com.estafet.openshift.dm.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.estafet.openshift.dm.config.Constants.*;
import static com.estafet.openshift.dm.config.TestConstants.*;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Delcho Delov on 18.04.17.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceManagerServicesTest {

		private static final String VALID_2 = "20150809";
		@Mock
		private PersistenceProvider providerMock = mock(PersistenceProvider.class);
		@Mock
		private Connection connectionMock = mock(Connection.class);
		@Mock
		private PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
		@Mock
		private ResultSet resultSetMock = mock(ResultSet.class);

		private final DeviceManagerServices handler = new DeviceManagerServices() {
				@Override
				protected PersistenceProvider getPersistenceProvider() {
						return providerMock;
				}
		};

		@Before
		public void setUp() throws Exception {
				MockitoAnnotations.initMocks(this);
				when(providerMock.getCon()).thenReturn(connectionMock);
				when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
				when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
		}

		@Test
		public void getAllDevicesEmptyRoleParam() throws Exception {
				final Response allDevices = handler.getAllDevices(EMPTY_STRING, EMPTY_STRING);
				assertNotNull(allDevices);
				final String entity = (String) allDevices.getEntity();
				assertFalse(Utils.isEmpty(entity));
				assertThat(allDevices.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
				assertTrue(entity.contains(ROLE + " parameter is mandatory"));
		}

		@Test
		public void getAllDevicesEmptyCustomerParam() throws Exception {
				final Response allDevices = handler.getAllDevices(EMPTY_STRING, TEST_ROLE);
				assertNotNull(allDevices);
				final String entity = (String) allDevices.getEntity();
				assertFalse(Utils.isEmpty(entity));
				assertThat(allDevices.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
				assertTrue(entity.contains("As you have not MANAGER permissions granted, so you should specify your customer ID"));
		}

		@Test
		public void getAllDevicesDMException() throws Exception {
				when(providerMock.getCon()).thenThrow(SQLException.class);
				final Response response = handler.getAllDevices(TEST_MAIL, ROLE_MANAGER);
				assertNotNull(response);
				final String entity = (String) response.getEntity();
				assertFalse(Utils.isEmpty(entity));
				assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
				assertTrue(entity.contains("Could not open DB connection"));
		}

		@Test
		public void getAllDevicesManagerRole() throws Exception {
				//will return 2 devices
				when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
				//id
				when(resultSetMock.getDouble(1)).thenReturn(TEST_ID_DBL).thenReturn((double) (TEST_ID + 25));
				//thingName
				when(resultSetMock.getString(2)).thenReturn(TEST_DEVICE_ID).thenReturn(TEST_DEVICE_ID + 2);
				//type
				when(resultSetMock.getString(3)).thenReturn(TEST_TYPE).thenReturn(TEST_TYPE + 2);
				//sn
				when(resultSetMock.getString(4)).thenReturn(TEST_SN).thenReturn(TEST_SN + 2);
				//own
				when(resultSetMock.getBoolean(5)).thenReturn(TEST_OWN).thenReturn(!TEST_OWN);
				//validFrom
				when(resultSetMock.getString(6)).thenReturn(TEST_VALID_FROM).thenReturn(VALID_2);
				//validTo
				when(resultSetMock.getString(7)).thenReturn(TEST_VALID_TO).thenReturn(null);
				//customerId
				when(resultSetMock.getString(8)).thenReturn(TEST_MAIL).thenReturn(TEST_MAIL + 2);
				//status
				when(resultSetMock.getString(9)).thenReturn(Constants.DEVICE_STATUS_DEFAULT);
				final Response response = handler.getAllDevices(TEST_MAIL, ROLE_MANAGER);
				assertNotNull(response);
				assertThat(response.getStatus(), is(SC_OK));
				final List<Map<String, Object>> allDevices = (List<Map<String, Object>>) response.getEntity();
				assertNotNull(allDevices);
				assertFalse(allDevices.isEmpty());
				assertThat(allDevices.size(), is(2));
				{
						final Map<String, Object> firstDevProps = allDevices.get(0);
						assertThat((int) firstDevProps.get(COL_ID), is(TEST_ID));
						assertThat((String) firstDevProps.get(CUSTOMER_ID), is(TEST_MAIL));
						assertThat((String) firstDevProps.get(THING_NAME), is(TEST_DEVICE_ID));
						assertThat((String) firstDevProps.get(THING_TYPE), is(TEST_TYPE));
						assertThat((boolean) firstDevProps.get(COL_OWN), is(TEST_OWN));
						assertThat((String) firstDevProps.get(COL_SN), is(TEST_SN));
						assertThat((String) firstDevProps.get(VALID_FROM), is(TEST_VALID_FROM));
						assertThat((String) firstDevProps.get(VALID_TO), is(TEST_VALID_TO));
						assertThat((String) firstDevProps.get(DEVICE_STATUS), is(DEVICE_STATUS_DEFAULT));
				}
				{
						final Map<String, Object> secondDevProps = allDevices.get(1);
						assertThat((int) secondDevProps.get(COL_ID), is(TEST_ID + 25));
						assertThat((String) secondDevProps.get(CUSTOMER_ID), is(TEST_MAIL + 2));
						assertThat((String) secondDevProps.get(THING_NAME), is(TEST_DEVICE_ID + 2));
						assertThat((String) secondDevProps.get(THING_TYPE), is(TEST_TYPE + 2));
						assertThat((boolean) secondDevProps.get(COL_OWN), is(!TEST_OWN));
						assertThat((String) secondDevProps.get(COL_SN), is(TEST_SN + 2));
						assertThat((String) secondDevProps.get(VALID_FROM), is(VALID_2));
						assertNull(secondDevProps.get(VALID_TO));
				}
		}

		@Test
		public void getAllDevices4Customer() throws Exception {
				//will return 1 device
				when(resultSetMock.next()).thenReturn(true).thenReturn(false);
				//id
				when(resultSetMock.getDouble(1)).thenReturn(TEST_ID_DBL);
				//thingName
				when(resultSetMock.getString(2)).thenReturn(TEST_DEVICE_ID);
				//type
				when(resultSetMock.getString(3)).thenReturn(TEST_TYPE);
				//sn
				when(resultSetMock.getString(4)).thenReturn(TEST_SN);
				//own
				when(resultSetMock.getBoolean(5)).thenReturn(TEST_OWN);
				//validFrom
				when(resultSetMock.getString(6)).thenReturn(TEST_VALID_FROM);
				//validTo
				when(resultSetMock.getString(7)).thenReturn(TEST_VALID_TO);
				//customerId
				when(resultSetMock.getString(8)).thenReturn(TEST_MAIL);
				//status
				when(resultSetMock.getString(9)).thenReturn(Constants.DEVICE_STATUS_DEFAULT);
				final Response response = handler.getAllDevices(TEST_MAIL, TEST_ROLE);
				assertNotNull(response);
				assertThat(response.getStatus(), is(SC_OK));
				final List<Map<String, Object>> allDevices = (List<Map<String, Object>>) response.getEntity();
				assertNotNull(allDevices);
				assertFalse(allDevices.isEmpty());
				assertThat(allDevices.size(), is(1));
				{
						final Map<String, Object> firstDevProps = allDevices.get(0);
						assertThat((int) firstDevProps.get(COL_ID), is(TEST_ID));
						assertThat((String) firstDevProps.get(CUSTOMER_ID), is(TEST_MAIL));
						assertThat((String) firstDevProps.get(THING_NAME), is(TEST_DEVICE_ID));
						assertThat((String) firstDevProps.get(THING_TYPE), is(TEST_TYPE));
						assertThat((boolean) firstDevProps.get(COL_OWN), is(TEST_OWN));
						assertThat((String) firstDevProps.get(COL_SN), is(TEST_SN));
						assertThat((String) firstDevProps.get(VALID_FROM), is(TEST_VALID_FROM));
						assertThat((String) firstDevProps.get(VALID_TO), is(TEST_VALID_TO));
						assertThat((String) firstDevProps.get(DEVICE_STATUS), is(DEVICE_STATUS_DEFAULT));
				}
		}

		@Test
		public void deleteDeviceEmptyThingName() throws Exception {
				final Response response = handler.deleteDevice("{\"param1\":123.45}");
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
				final String entity = (String) response.getEntity();
				assertTrue(entity.contains(THING_NAME + " parameter is mandatory"));
		}

		@Test
		public void deleteDeviceDBConnectionNull() throws Exception {
				// mocks
				when(providerMock.loadDeviceOwnership(any(String.class), any(Connection.class))).thenThrow(SQLException.class);
				// call method
				final Response response = handler.deleteDevice(preparePayload());

				// asserts
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
				final String entity = (String) response.getEntity();
				assertTrue(entity.contains("Could not open DB connection"));
		}

		@Test
		public void deleteDeviceResourceNotFoundEx() throws Exception {
				// mocks
				when(providerMock.loadDeviceOwnership(any(String.class), any(Connection.class))).thenThrow(ResourceNotFoundException.class);
				// call method
				final Response response = handler.deleteDevice(preparePayload());

				// asserts
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
		}

		@Test
		public void deleteDeviceDMEx() throws Exception {
				// mocks
				when(providerMock.loadDeviceOwnership(any(String.class), any(Connection.class))).thenThrow(DMException.class);
				// call method
				final Response response = handler.deleteDevice(preparePayload());

				// asserts
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
		}

		@Ignore
		@Test
		public void deleteDeviceOK() throws Exception {
				// mocks
				when(providerMock.loadDeviceOwnership(any(String.class), any(Connection.class))).thenReturn(firstDevOwnership());
				// call method
				final Response response = handler.deleteDevice(preparePayload());

				// asserts
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
				final String entity = (String) response.getEntity();
				assertTrue(entity.contains("Device deleted"));
		}

		@Test
		public void registerDeviceEmptyPayload() throws Exception {
				// mocks
				// call method
				final Response response = handler.registerDevice(EMPTY_STRING, TEST_MAIL);

				// asserts
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
				final String entity = (String) response.getEntity();
				assertTrue(entity.contains("Missing request body"));
		}

		@Ignore
		@Test
		public void registerDeviceMissingParam() throws Exception {
				Gson gson = new GsonBuilder().create();
				final String[] MANDATORY_PARAMETERS = {COL_CUST_ID, COL_THING_NAME, COL_THING_TYPE, COL_SN, COL_OWN, COL_VALID_FROM};
				for (String paramName : MANDATORY_PARAMETERS) {
						// mocks
						final Map<String, Object> deviceMap = prepareDeviceMap();
						deviceMap.remove(paramName);
						// call method
						final Response response = handler.registerDevice(gson.toJson(deviceMap), TEST_MAIL);

						// asserts
						assertNotNull(response);
						assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
						final String entity = (String) response.getEntity();
						assertTrue(entity.contains(paramName + " parameter is mandatory"));
				}
		}

		@Test
		public void registerDeviceDBConnectionEx() throws Exception {
				Gson gson = new GsonBuilder().create();
//				final Map<String, Object> deviceMap = prepareDeviceMap();
				// mocks
				when(providerMock.getCon()).thenThrow(SQLException.class);
				// call method
				final Response response = handler.registerDevice(preparePayload(), TEST_MAIL);

				// asserts
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
				final String entity = (String) response.getEntity();
				assertTrue(entity.contains("Could not open DB connection"));
		}

		@Test
		public void registerDeviceExceptionOnWriteNew() throws Exception {
				Gson gson = new GsonBuilder().create();
//				final Map<String, Object> deviceMap = prepareDeviceMap();
				// mocks
				when(providerMock.loadDeviceOwnership(any(String.class), any(Connection.class))).thenReturn(firstDevOwnership());
				doThrow(EmptyArgumentException.class).when(providerMock).writeDeviceOwnership(any(DeviceOwnership.class), any(Connection.class));
				// call method
				final Response response = handler.registerDevice(preparePayload(), TEST_MAIL);

				// asserts
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
		}

		@Ignore
		@Test
		public void registerDeviceOK() throws Exception {
				Gson gson = new GsonBuilder().create();
//				final Map<String, Object> deviceMap = prepareDeviceMap();
				// mocks
				when(providerMock.loadDeviceOwnership(any(String.class), any(Connection.class))).thenReturn(firstDevOwnership());
				doThrow(ResourceNotFoundException.class).when(providerMock).markDeviceOwnershipInvalid(any(DeviceOwnership.class), any(Connection.class));
				// call method
				final Response response = handler.registerDevice(preparePayload(), TEST_MAIL);

				// asserts
				assertNotNull(response);
				assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
				final String entity = (String) response.getEntity();
				assertTrue(entity.contains("Device registered"));
		}

		private Map<String, Object> prepareDeviceMap() {
				final Map<String, Object> res = new HashMap<>(6);
				res.put(THING_NAME, TEST_DEVICE_ID);
				res.put(THING_TYPE, TEST_TYPE);
				res.put(COL_SN, TEST_SN);
				res.put(COL_OWN, TEST_OWN);
				res.put(VALID_FROM, TEST_VALID_FROM);
				return res;
		}
		private String preparePayload(){
				Gson gson = new GsonBuilder().create();
				return gson.toJson(prepareDeviceMap());
		}

		private DeviceOwnership firstDevOwnership() {
				return new DeviceOwnership(TEST_ID, TEST_MAIL, TEST_DEVICE_ID, TEST_TYPE, TEST_SN, TEST_OWN, TEST_VALID_FROM, TEST_VALID_TO, Constants.DEVICE_STATUS_DEFAULT);
		}

}