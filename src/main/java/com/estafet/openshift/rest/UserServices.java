package com.estafet.openshift.rest;

import com.estafet.openshift.model.BaseDeviceManager;
import com.estafet.openshift.model.DeviceManager;
import com.estafet.openshift.model.entity.Customer;
import com.estafet.openshift.model.exception.DMException;
import com.estafet.openshift.model.exception.DMSQLException;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import com.estafet.openshift.model.exception.ResourceNotFoundException;
import com.estafet.openshift.util.ConnectionProvider;
import com.estafet.openshift.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;

import static com.estafet.openshift.config.Constants.COL_USERNAME;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Delcho Delov on 06.04.17.
 */
@Path("/user")
@Deprecated
public class UserServices {
		private Logger log = Logger.getLogger(UserServices.class);
		private final DeviceManager deviceManager = new BaseDeviceManager();


		@GET
		@Path("/byUsername")
		@Produces(APPLICATION_JSON)
		public Response searchByUsername(@QueryParam(COL_USERNAME)String username)
		{
				log.info(">> UserServices.loadByUsername()");
				Gson gson = new GsonBuilder().create();
				try {
						final Customer customer = loadByUsername(username);
						log.info("<< UserServices.loadByUsername()");
						// return HTTP response 200 in case of success
						return Response.status(HttpServletResponse.SC_OK).entity(customer).build();
				} catch (ResourceNotFoundException e) {
						log.warn(e.getMessage());
						log.info("<< UserServices.loadByUsername()");
						return Response.status(HttpServletResponse.SC_OK).entity(e.getMessage()).build();
				} catch (DMException e) {
						log.error(e.getMessage(), e);
						log.info("<< UserServices.loadByUsername()");
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
				}
		}

		private Customer loadByUsername(String username) throws DMSQLException, EmptyArgumentException, ResourceNotFoundException {
				if(Utils.isEmpty(username)){
						throw new EmptyArgumentException("username");
				}
				Customer customer = new Customer(username);
				try (Connection conn = ConnectionProvider.getCon()) {
						if(!customer.loadByUsername(conn)){
								throw new ResourceNotFoundException("Could not find any data for customer with username "+username);
						}
				} catch (SQLException e) {
						throw new DMSQLException("Problem with DB", e);
				}
				return customer;
		}

}
