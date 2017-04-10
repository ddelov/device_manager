package org.blogger.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/device")
public class HelloResource {

	@GET
	@Produces(APPLICATION_JSON)
	public String ping(){

			return "Hello World!";
	}
}
