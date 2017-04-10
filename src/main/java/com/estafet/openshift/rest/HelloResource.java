package com.estafet.openshift.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Delcho Delov on 10.04.17.
 */
@Path("/")
public class HelloResource {

		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public String ping(){
				return "Hello World!";
		}
}