package org.blogger.rest.resources;

import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Delcho Delov on 06.04.17.
 */

//@ApplicationPath("/")
@Path("/")
public class DeviceManagerServices {
		private Logger log = Logger.getLogger(DeviceManagerServices.class);
//		private final DeviceManager deviceManager = new BaseDeviceManager();

		@GET
		@Path("/")
		@Produces(APPLICATION_JSON)
		public String hello() {
				return "Welcome to OpenShift, Mr. Delov!";
		}


//		@POST
//		@Path("/addNewDevice")
//		@Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_JSON)
//		public Response addNewDevice(String jsonPayload) {
//				log.info("Calling DeviceManager.addNewDevice() method");
//				Gson gson = new GsonBuilder().create();
//				try {
//						//1. extract input parameters
//						final Map<String, Object> body = gson.fromJson(jsonPayload, Map.class);
//						if (body == null || body.isEmpty()) {
//								throw new EmptyArgumentException("Missing request body");
//						}
//
//						if (!body.containsKey(COL_CUST_ID)) {
//								log.error(COL_CUST_ID + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_CUST_ID + " parameter is mandatory");
//						}
//						final int customerId = (int) body.get(COL_CUST_ID);
//
//						final String thingName = (String) body.get(COL_THING_NAME);
//						if (isEmpty(thingName)) {
//								log.error(COL_THING_NAME + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_THING_NAME + " parameter is mandatory");
//						}
//						final String thingType = (String) body.get(COL_THING_TYPE);
//						if (isEmpty(thingType)) {
//								log.error(COL_THING_TYPE + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_THING_TYPE + " parameter is mandatory");
//						}
//						final String sn = (String) body.get(COL_SN);
//						if (isEmpty(sn)) {
//								log.error(COL_SN + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_SN + " parameter is mandatory");
//						}
//						if (!body.containsKey(COL_OWN)) {
//								log.error(COL_OWN + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_OWN + " parameter is mandatory");
//						}
//						final boolean own = (boolean) body.get(COL_OWN);
//						final String validFrom = (String) body.get(COL_VALID_FROM);
//						if (isEmpty(validFrom)) {
//								log.error(COL_VALID_FROM + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_VALID_FROM + " parameter is mandatory");
//						}
//						//2.
//						final DeviceOwnership deviceOwnership = new DeviceOwnership(customerId, thingName, thingType, sn, own);
//						deviceOwnership.setValidFrom(validFrom);
//						registerDevice(deviceOwnership);
//				} catch (DMException e) {
//						log.error(e.getMessage(), e);
//						return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
//				}
//
//				log.info("Exit DeviceManager.addNewDevice() method");
//				// return HTTP response 200 in case of success
//				return Response.status(HttpServletResponse.SC_OK).entity("Customer inserted").build();
//		}
//
//		/**
//		 * Executes in a transaction
//		 * Notes:
//		 * 1. thing type is not checked - any non-empty value is accepted
//		 *
//		 * @param deviceOwnership
//		 * @throws DMException
//		 */
//
//		protected void registerDevice(DeviceOwnership deviceOwnership) throws DMException {
//				try (Connection conn = ConnectionProvider.getCon()) {
//						try {
//								//1. search device current record in DeviceOwnership and mark as invalid
//								if (deviceOwnership.loadLastActive(conn)) {
//										deviceOwnership.setValidTo(Calendar.getInstance()); // effective immediately - today device is no longer active
//										deviceOwnership.writeToDb(conn);
//								}
//								String thingTypeName = deviceOwnership.getThingTypeName();
////				listThingTypesRequest.setThingTypeName(thingTypeName);
//								AbstractDevice device = deviceManager.createInstance(deviceOwnership.getThingName(), thingTypeName, deviceOwnership.getSn());
//								ReportedState reportedState = new ReportedState(device.getCurrentState());
//								final Gson json = new Gson();
//								final String initialStatePayload = json.toJson(reportedState);
//								log.info("intitialStateJson = " + initialStatePayload);
//								ShadowData shadowData = new ShadowData(deviceOwnership.getThingName(), initialStatePayload, null);
//								shadowData.writeToDb(conn);
//								deviceOwnership.writeToDb(conn);
//
//								conn.commit();
//						} catch (SQLException e) {
//								conn.rollback();
//								throw new DMException("Could not register device ", e);
//						}
//				} catch (SQLException e) {
//						throw new DMException("Could not open DB connection", e);
//				}
//		}
//
}
