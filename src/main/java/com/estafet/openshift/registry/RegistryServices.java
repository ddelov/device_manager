package com.estafet.openshift.registry;

import com.estafet.openshift.dm.util.Utils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Delcho Delov on 19.04.17.
 */
@Path("/registry")
public class RegistryServices{
		private final Logger log = Logger.getLogger(RegistryServices.class);

		private ConcurrentMap<String, List<String>> deviceTopics = new ConcurrentHashMap<>();
		@GET
		@Path("/get/{device_id}")
		@Produces(APPLICATION_JSON)
		public Response getListeners(@PathParam("device_id")String deviceId) {
				log.debug(">> RegistryServices.getListeners("+deviceId+")");
				final List<String> rules = deviceTopics.get(deviceId);
				return Response.status(HttpServletResponse.SC_OK).entity(rules).build();
		}

		//================ for device manager ===========================
//		public void deleteDevice(String deviceId){
//				deviceTopics.remove(deviceId);
//		}
//		@POST
//		@Path("/registerDevice/{device_id}")
//		@Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_JSON)
//		public Response registerDevice(@PathParam("device_id")String deviceId) {
//				log.debug(">> RegistryServices.registerDevice("+deviceId+")");
//				deviceTopics.putIfAbsent(deviceId, new LinkedList<String>());
//				return Response.status(HttpServletResponse.SC_OK).build();
//		}

		private void registerDevice(String deviceId) {
				log.debug(">> RegistryServices.registerDevice("+deviceId+ ")");
				deviceTopics.putIfAbsent(deviceId, new LinkedList<String>());
		}
		//================ for simulator interaction ====================
		@POST
		@Path("/send/{device_id}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response send(@PathParam("device_id")String deviceId, String jsonState) {
				log.debug(">> RegistryServices.send("+deviceId+ ", "+ jsonState +")");
				final List<Integer> retCodeList = sendStatePrv(deviceId, jsonState);
				return Response.status(HttpServletResponse.SC_OK).entity(retCodeList).build();
		}

		private List<Integer> sendStatePrv(String deviceId, String jsonState){
				registerDevice(deviceId);//harmless method
				final List<String> topicListeners = deviceTopics.get(deviceId);
				log.debug("found "+ topicListeners.size() + " listeners");
				List<Integer> res = new LinkedList<>();
				for (String endpoint : topicListeners) {
						log.debug("Sending notification to "+ endpoint);
						try {
								final int code = Utils.makePostJsonRequest(endpoint, jsonState);
								res.add(code);
						} catch (IOException e) {
								log.error(e.getMessage(), e);
								res.add(-1);
						}
				}
				return res;
		}

		// for rules/listeners interaction

		@POST
		@Path("/registerRule/{device_id}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response registerRule(@PathParam("device_id")String deviceId, String ruleEndpoint) {
				log.debug(">> RegistryServices.registerRule("+deviceId+ ", "+ ruleEndpoint +")");
				registerDevice(deviceId);
				deviceTopics.get(deviceId).add(ruleEndpoint);
				return Response.status(HttpServletResponse.SC_OK).entity("Rule registered").build();
		}

		@DELETE
		@Path("/deleteRule/{thing_name}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteRule(@PathParam("device_id")String deviceId, String ruleEndpoint) {
				log.debug(">> RegistryServices.deleteRule("+deviceId+ ", "+ ruleEndpoint +")");
				final List<String> listeners = deviceTopics.get(deviceId);
				final Iterator<String> iterator = listeners.iterator();
				boolean found = false;
				while(iterator.hasNext()){
						final String listener = iterator.next();
						if(listener.equals(ruleEndpoint)){
								iterator.remove();
								found=true;
								break;
						}
				}
				final String message = found?"Rule unregistered":"Rule is not found";
				return Response.status(HttpServletResponse.SC_OK).entity(message).build();
		}
}