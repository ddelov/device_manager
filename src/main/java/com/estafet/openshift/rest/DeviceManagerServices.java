package com.estafet.openshift.rest;

import com.estafet.openshift.model.BaseDeviceManager;
import com.estafet.openshift.model.DeviceManager;
import com.estafet.openshift.model.device.AbstractDevice;
import com.estafet.openshift.model.entity.DeviceOwnership;
import com.estafet.openshift.model.enumerations.DeviceStatus;
import com.estafet.openshift.model.exception.DMException;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import com.estafet.openshift.util.ConnectionProvider;
import com.estafet.openshift.util.ReportedState;
import com.estafet.openshift.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.estafet.openshift.config.Constants.*;
import static com.estafet.openshift.config.Queries.SQL_GET_ALL_DEV_OWNERSHIP;
import static com.estafet.openshift.config.Queries.SQL_GET_DEV_OWNERSHIP_BY_CUSTOMER;
import static javax.faces.component.UIInput.isEmpty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Delcho Delov on 06.04.17.
 */
@Path("/")
public class DeviceManagerServices {
		private Logger log = Logger.getLogger(DeviceManagerServices.class);
		private final DeviceManager deviceManager = new BaseDeviceManager();

		@GET
		@Path("/")
		@Produces(APPLICATION_JSON)
		public String hello() {
				return "Welcome to OpenShift, Mr. Delov!";
		}


		@GET
		@Path("/getAllDevices")
		@Produces(APPLICATION_JSON)
		public Response getAllDevices(@HeaderParam(HDR_CUSTOMER_ID) String customerId,
																	 @HeaderParam(ROLE)String role) {
				log.debug(">> DeviceManagerServices.getAllDevices()");
				//check parameters
				try {
						if(Utils.isEmpty(role)){
								log.error(ROLE + " parameter is mandatory");
								throw new EmptyArgumentException(ROLE + " parameter is mandatory");
						}
						if(Utils.isEmpty(customerId) && !ROLE_MANAGER.equalsIgnoreCase(role)){
								log.error("As you have not MANAGER permissions granted, so you should specify your customer ID");
								throw new EmptyArgumentException("As you have not MANAGER permissions granted, so you should specify your customer ID");
						}
				}catch (EmptyArgumentException e){
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
				}
				//processing
				List<Map<String, Object>> allDevices;
				try {
						if (ROLE_MANAGER.equalsIgnoreCase(role)) {
								allDevices = listMyDevices(null);
						} else {
								allDevices = listMyDevices(customerId);
						}
				}catch (DMException e){
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
				}
				log.debug("<< DeviceManagerServices.getAllDevices()");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).entity(allDevices).build();
		}


		@DELETE
		@Path("/deleteDevice")
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteDevice(@HeaderParam(HDR_THING_NAME) String thingName){
				log.debug(">> DeviceManagerServices.deleteDevice()");
				//check parameters
				try {
						if (Utils.isEmpty(thingName)) {
								log.error(HDR_THING_NAME + " parameter is mandatory");
								throw new EmptyArgumentException(HDR_THING_NAME + " parameter is mandatory");
						}
				}catch (EmptyArgumentException e){
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
				}
				//invalidate device ownership record
				try (Connection conn = ConnectionProvider.getCon()) {
						boolean deviceFound = false;
						try {
								//1. search device current record in DeviceOwnership and mark as invalid
								final DeviceOwnership deviceOwnership = new DeviceOwnership(thingName);
								deviceFound = deviceOwnership.loadLastActive(conn);
								if (deviceFound) {
										deviceOwnership.setValidTo(Calendar.getInstance()); // effective immediately - today device is no longer active
										deviceOwnership.writeToDb(conn);
										conn.commit();
								}
						} catch (DMException e) {
								if(deviceFound){
										conn.rollback();
								}
								log.error(e.getMessage(), e);
								return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
						}
				} catch (SQLException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Could not open DB connection").build();
				}
				log.debug("<< DeviceManagerServices.deleteDevice()");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).build();
		}


		@POST
		@Path("/registerDevice")
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		@Produces(MediaType.APPLICATION_JSON)
		public Response registerDevice(MultivaluedMap<String, String> body) {
				log.debug(">> DeviceManagerServices.registerDevice()");
				//1. extract input parameters
				try {
						//1. extract input parameters
						if (body == null || body.isEmpty()) {
								throw new EmptyArgumentException("Missing request body");
						}
						log.info("body: "+body);
						if (!body.containsKey(COL_CUST_ID)) {
								log.error(COL_CUST_ID + " parameter is mandatory");
								throw new EmptyArgumentException(COL_CUST_ID + " parameter is mandatory");
						}
						final String customerId = body.getFirst(COL_CUST_ID);
						if (isEmpty(customerId)) {
								log.error(COL_THING_NAME + " parameter is mandatory");
								throw new EmptyArgumentException(COL_THING_NAME + " parameter is mandatory");
						}

						final String thingName = body.getFirst(COL_THING_NAME);
						if (isEmpty(thingName)) {
								log.error(COL_THING_NAME + " parameter is mandatory");
								throw new EmptyArgumentException(COL_THING_NAME + " parameter is mandatory");
						}
						final String thingType = body.getFirst(COL_THING_TYPE);
						if (isEmpty(thingType)) {
								log.error(COL_THING_TYPE + " parameter is mandatory");
								throw new EmptyArgumentException(COL_THING_TYPE + " parameter is mandatory");
						}
						final String sn = body.getFirst(COL_SN);
						if (isEmpty(sn)) {
								log.error(COL_SN + " parameter is mandatory");
								throw new EmptyArgumentException(COL_SN + " parameter is mandatory");
						}
						if (!body.containsKey(COL_OWN)) {
								log.error(COL_OWN + " parameter is mandatory");
								throw new EmptyArgumentException(COL_OWN + " parameter is mandatory");
						}
						final boolean own = Boolean.getBoolean(body.getFirst(COL_OWN));
						final String validFrom = body.getFirst(COL_VALID_FROM);
						if (isEmpty(validFrom)) {
								log.error(COL_VALID_FROM + " parameter is mandatory");
								throw new EmptyArgumentException(COL_VALID_FROM + " parameter is mandatory");
						}
						//2.processing
						final DeviceOwnership deviceOwnership = new DeviceOwnership(customerId, thingName, thingType, sn, own);
						deviceOwnership.setValidFrom(validFrom);
						registerDevice(deviceOwnership);
				} catch (DMException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
				}

				log.debug("<< DeviceManagerServices.registerDevice()");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).entity("Device registered").build();
		}

		@POST
		@Path("/registerDeviceJson")
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
						final String customerId = (String) body.get(COL_CUST_ID);
						if (isEmpty(customerId)) {
								log.error(COL_THING_NAME + " parameter is mandatory");
								throw new EmptyArgumentException(COL_THING_NAME + " parameter is mandatory");
						}

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
						//2.processing
						final DeviceOwnership deviceOwnership = new DeviceOwnership(customerId, thingName, thingType, sn, own);
						deviceOwnership.setValidFrom(validFrom);
						registerDevice(deviceOwnership);
				} catch (DMException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
				}

				log.info("Exit DeviceManager.addNewDevice() method");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).entity("Device registered").build();
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
//								ShadowData shadowData = new ShadowData(deviceOwnership.getThingName(), initialStatePayload, null);
//								shadowData.writeToDb(conn);
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

		private List<Map<String,Object>> listMyDevices(String customerId) throws DMException {
				final List<Map<String, Object>> devices = new LinkedList<>();
				try (Connection conn = ConnectionProvider.getCon()) {
						PreparedStatement ps =  conn.prepareStatement(SQL_GET_ALL_DEV_OWNERSHIP);
						if(!Utils.isEmpty(customerId)){
								ps = conn.prepareStatement(SQL_GET_DEV_OWNERSHIP_BY_CUSTOMER);
								ps.setString(1, customerId);
						}
						final ResultSet resultSet = ps.executeQuery();
						if (resultSet.next()) {
								final String thingName = resultSet.getString(1);
								final String thingTypeName = resultSet.getString(2);
								final String sn = resultSet.getString(3);
								boolean own = resultSet.getBoolean(4);
								final String validFrom = resultSet.getString(5);
								final String validTo = resultSet.getString(6);
								final String customerIdRead = resultSet.getString(7);
								final DeviceOwnership deviceOwnership = new DeviceOwnership(customerIdRead, thingName, thingTypeName, sn, own);
								final Map<String, Object> propertiesMap = deviceOwnership.asMap();
								//TODO get real device status from registry
								DeviceStatus deviceStatus = DEVICE_STATUS_DEFAULT;
								log.debug("deviceStatus = " + deviceStatus.name());
								propertiesMap.put("deviceStatus", deviceStatus.name());
								devices.add(propertiesMap);
						}
				} catch (SQLException e) {
						throw new DMException("Could not open DB connection", e);
				}
				return devices;
		}

}
