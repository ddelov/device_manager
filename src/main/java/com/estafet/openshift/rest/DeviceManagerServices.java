package com.estafet.openshift.rest;

import com.estafet.openshift.model.BaseDeviceManager;
import com.estafet.openshift.model.DeviceManager;
import com.estafet.openshift.model.device.AbstractDevice;
import com.estafet.openshift.model.entity.DeviceOwnership;
import com.estafet.openshift.model.entity.ShadowData;
import com.estafet.openshift.model.exception.DMException;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import com.estafet.openshift.util.ConnectionProvider;
import com.estafet.openshift.util.ReportedState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

import static com.estafet.openshift.config.Constants.*;
import static com.estafet.openshift.config.Queries.SQL_GET_SHADOW_DATA;
import static javax.faces.component.UIInput.isEmpty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Delcho Delov on 06.04.17.
 */
@Path("/device")
public class DeviceManagerServices {
		private Logger log = Logger.getLogger(DeviceManagerServices.class);
		private final DeviceManager deviceManager = new BaseDeviceManager();


		@GET
		@Path("/getShadowData")
		@Produces(APPLICATION_JSON)
		@Consumes(APPLICATION_JSON)
		public Response getShadowData()
		{
				log.info("Calling DeviceManagerServices.getShadowData() method");
				ShadowData shadowData = null;
				try (Connection conn = ConnectionProvider.getCon()) {
						PreparedStatement ps = conn.prepareStatement(SQL_GET_SHADOW_DATA);
						final String thingName = "DUMMY_DEV";
						ps.setString(1, thingName);
						final ResultSet resultSet = ps.executeQuery();
						if (resultSet.next()) {
								final Timestamp timestamp = resultSet.getTimestamp(1);
								final String reported = resultSet.getString(2);
								final String desired = resultSet.getString(3);
								shadowData = new ShadowData(thingName, reported, desired);
								shadowData.setTstamp(timestamp);
						}
						log.info(shadowData);
				} catch (Exception e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
				}
				log.info("Exit DeviceManagerServices.getShadowData() method");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).entity(shadowData).build();
		}


		@POST
		@Path("/add")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response addNewDevice(String jsonPayload) {
				log.info("Calling DeviceManager.addNewDevice() method");
				Gson gson = new GsonBuilder().create();
				try {
						//1. extract input parameters
						final Map<String, Object> body = gson.fromJson(jsonPayload, Map.class);
						if (body == null || body.isEmpty()) {
								throw new EmptyArgumentException("Missing request body");
						}
						log.info("body: "+body);
						if (!body.containsKey(COL_CUST_ID)) {
								log.error(COL_CUST_ID + " parameter is mandatory");
								throw new EmptyArgumentException(COL_CUST_ID + " parameter is mandatory");
						}
						final Double customerId = (Double) body.get(COL_CUST_ID);

						final String thingName = (String) body.get(COL_THING_NAME);
						if (isEmpty(thingName)) {
								log.error(COL_THING_NAME + " parameter is mandatory");
								throw new EmptyArgumentException(COL_THING_NAME + " parameter is mandatory");
						}
						final String thingType = (String) body.get(COL_THING_TYPE);
						if (isEmpty(thingType)) {
								log.error(COL_THING_TYPE + " parameter is mandatory");
								throw new EmptyArgumentException(COL_THING_TYPE + " parameter is mandatory");
						}
						final String sn = (String) body.get(COL_SN);
						if (isEmpty(sn)) {
								log.error(COL_SN + " parameter is mandatory");
								throw new EmptyArgumentException(COL_SN + " parameter is mandatory");
						}
						if (!body.containsKey(COL_OWN)) {
								log.error(COL_OWN + " parameter is mandatory");
								throw new EmptyArgumentException(COL_OWN + " parameter is mandatory");
						}
						final boolean own = (boolean) body.get(COL_OWN);
						final String validFrom = (String) body.get(COL_VALID_FROM);
						if (isEmpty(validFrom)) {
								log.error(COL_VALID_FROM + " parameter is mandatory");
								throw new EmptyArgumentException(COL_VALID_FROM + " parameter is mandatory");
						}
						//2.
						final DeviceOwnership deviceOwnership = new DeviceOwnership(customerId.intValue(), thingName, thingType, sn, own);
						deviceOwnership.setValidFrom(validFrom);
						registerDevice(deviceOwnership);
				} catch (DMException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
				}

				log.info("Exit DeviceManager.addNewDevice() method");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).entity("Customer inserted").build();
		}

		/**
		 * Executes in a transaction
		 * Notes:
		 * 1. thing type is not checked - any non-empty value is accepted
		 *
		 * @param deviceOwnership
		 * @throws DMException
		 */

		private void registerDevice(DeviceOwnership deviceOwnership) throws DMException {
				try (Connection conn = ConnectionProvider.getCon()) {
						try {
								//1. search device current record in DeviceOwnership and mark as invalid
								final DeviceOwnership copy = new DeviceOwnership(deviceOwnership.getThingName());
								if (copy.loadLastActive(conn)) {
										copy.setValidTo(Calendar.getInstance()); // effective immediately - today device is no longer active
										copy.writeToDb(conn);
								}
								String thingTypeName = deviceOwnership.getThingTypeName();
//				listThingTypesRequest.setThingTypeName(thingTypeName);
								AbstractDevice device = deviceManager.createInstance(deviceOwnership.getThingName(), thingTypeName, deviceOwnership.getSn());
								ReportedState reportedState = new ReportedState(device.getCurrentState());
								final Gson json = new Gson();
								final String initialStatePayload = json.toJson(reportedState);
								log.info("intitialStateJson = " + initialStatePayload);
								ShadowData shadowData = new ShadowData(deviceOwnership.getThingName(), initialStatePayload, null);
								shadowData.writeToDb(conn);
								deviceOwnership.writeToDb(conn);

								conn.commit();
						} catch (SQLException e) {
								conn.rollback();
								throw new DMException("Could not register device ", e);
						}
				} catch (SQLException e) {
						throw new DMException("Could not open DB connection", e);
				}
		}

}
