package com.estafet.openshift.dm.util;

import com.estafet.openshift.dm.config.Constants;
import com.estafet.openshift.dm.model.entity.DeviceOwnership;
import com.estafet.openshift.dm.model.exception.EmptyArgumentException;
import com.estafet.openshift.dm.model.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.estafet.openshift.dm.config.TestConstants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Delcho Delov on 13.04.17.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PersistenceProviderTest {

		@InjectMocks
		private PersistenceProvider providerMock;
		@Mock
		private Connection connectionMock = mock(Connection.class);
		@Mock
		private PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
		@Mock
		private ResultSet resultSetMock = mock(ResultSet.class);

		@Before
		public void setUp() throws Exception {
				MockitoAnnotations.initMocks(this);
		}

		@Test(expected = EmptyArgumentException.class)
		public void loadDeviceOwnershipEmptyThingName() throws Exception {
				providerMock.loadDeviceOwnership(EMPTY_STRING, connectionMock);
		}

		@Test(expected = EmptyArgumentException.class)
		public void loadDeviceOwnershipEmptyConnection() throws Exception {
				providerMock.loadDeviceOwnership(TEST_DEVICE_ID, null);
		}

		@Test(expected = ResourceNotFoundException.class)
		public void loadDeviceOwnershipDevNotFound() throws Exception {
				// mocks
				when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
				when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
				when(resultSetMock.next()).thenReturn(false);
				// method call
				providerMock.loadDeviceOwnership(TEST_DEVICE_ID, connectionMock);
		}

		@Test
		public void loadDeviceOwnershipOK() throws Exception {
				// mocks
				when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
				when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
				when(resultSetMock.next()).thenReturn(true);
				//id
				when(resultSetMock.getDouble(1)).thenReturn(TEST_ID_DBL);
				//customerId
				when(resultSetMock.getString(2)).thenReturn(TEST_MAIL);
				//thingTypeName
				when(resultSetMock.getString(3)).thenReturn(TEST_TYPE);
				//sn
				when(resultSetMock.getString(4)).thenReturn(TEST_SN);
				//own
				when(resultSetMock.getBoolean(5)).thenReturn(TEST_OWN);
				//validFrom
				when(resultSetMock.getString(6)).thenReturn(TEST_VALID_FROM);
				//validTo
				when(resultSetMock.getString(7)).thenReturn(null);

				// method call
				final DeviceOwnership deviceOwnership = providerMock.loadDeviceOwnership(TEST_DEVICE_ID, connectionMock);

				// asserts
				assertNotNull(deviceOwnership);
				assertThat(deviceOwnership.getCustomerId(), is(TEST_MAIL));
				assertThat(deviceOwnership.getId(), is(TEST_ID));
				assertThat(deviceOwnership.getSn(), is(TEST_SN));
				assertThat(deviceOwnership.getThingName(), is(TEST_DEVICE_ID));
				assertThat(deviceOwnership.getValidFrom(), is(TEST_VALID_FROM));
				assertNull(deviceOwnership.getValidTo());
		}

		@Test(expected = EmptyArgumentException.class)
		public void writeDeviceOwnershipClosedConnection() throws Exception {
				DeviceOwnership deviceOwnership = new DeviceOwnership(TEST_ID, TEST_MAIL, TEST_DEVICE_ID, TEST_TYPE, TEST_SN, TEST_OWN, TEST_VALID_FROM, TEST_VALID_TO, Constants.DEVICE_STATUS_DEFAULT);
				//mocks
				when(preparedStatementMock.getConnection()).thenReturn(connectionMock);
				when(connectionMock.isClosed()).thenReturn(true);

				//method call
				providerMock.writeDeviceOwnership(deviceOwnership, connectionMock);
		}

		@Test(expected = EmptyArgumentException.class)
		public void writeDeviceOwnershipMissingThingTypeConnection() throws Exception {
				DeviceOwnership deviceOwnership = new DeviceOwnership(TEST_ID, TEST_MAIL, TEST_DEVICE_ID, null, TEST_SN, TEST_OWN, TEST_VALID_FROM, TEST_VALID_TO, Constants.DEVICE_STATUS_DEFAULT);
				//mocks
				when(preparedStatementMock.getConnection()).thenReturn(connectionMock);
				when(connectionMock.isClosed()).thenReturn(false);

				//method call
				providerMock.writeDeviceOwnership(deviceOwnership, connectionMock);
		}

		@Test
		public void writeDeviceOwnership() throws Exception {
				DeviceOwnership deviceOwnership = new DeviceOwnership(TEST_ID, TEST_MAIL, TEST_DEVICE_ID, TEST_TYPE, TEST_SN, TEST_OWN, TEST_VALID_FROM, TEST_VALID_TO, Constants.DEVICE_STATUS_DEFAULT);
				//mocks
				when(preparedStatementMock.getConnection()).thenReturn(connectionMock);
				when(connectionMock.isClosed()).thenReturn(false);
				when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
				when(preparedStatementMock.executeUpdate()).thenReturn(1);

				//method call
				providerMock.writeDeviceOwnership(deviceOwnership, connectionMock);
		}

		@Test(expected = EmptyArgumentException.class)
		public void markDeviceOwnershipInvalidClosedConnection() throws Exception {
				DeviceOwnership deviceOwnership = new DeviceOwnership(TEST_MAIL, TEST_DEVICE_ID, TEST_TYPE,
								TEST_SN, TEST_OWN, TEST_VALID_FROM, Constants.DEVICE_STATUS_DEFAULT);
				providerMock.markDeviceOwnershipInvalid(deviceOwnership, connectionMock);
		}

		@Test
		public void markDeviceOwnershipInvalidOK() throws Exception {
				//mocks
				when(connectionMock.prepareStatement(any(String.class))).thenReturn(preparedStatementMock);
				when(preparedStatementMock.executeUpdate()).thenReturn(1);

				//method call
				DeviceOwnership deviceOwnership = new DeviceOwnership(TEST_ID, TEST_MAIL, TEST_DEVICE_ID, TEST_TYPE,
								TEST_SN, TEST_OWN, TEST_VALID_FROM, null, Constants.DEVICE_STATUS_DEFAULT);
				providerMock.markDeviceOwnershipInvalid(deviceOwnership, connectionMock);

		}

}