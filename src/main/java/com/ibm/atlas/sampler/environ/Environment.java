/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2016, 2018
 */

package com.ibm.atlas.sampler.environ;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.ibm.atlas.sampler.params.NameValueParameter;
import com.ibm.atlas.sampler.params.ValueParameter;
import com.ibm.atlas.sampler.resources.NameValueResponse;
import com.ibm.atlas.sampler.service.Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path(value = "/environmentVariable")
@Api(value = "Sample microservice : Java Environment variables")
public class Environment {

	@Context
	UriInfo uriInfo;

	@Inject
	Logger log;
	
	@Inject
	Service jzosService;	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Gets a list of environment variables", 
	              notes = "This API uses JZOS to retrieve a JSON formatted list of .")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Ok", response = NameValueParameter.class, responseContainer = "List")})
	public List<NameValueResponse> getAll() {	
		return jzosService.getPropertyList() ;
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Creates a new environment variables", 
	              notes = "This API uses JZOS to perform the process.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Environment variable created", response = NameValueParameter.class, responseContainer = "List")})
	public Response add(
			@ApiParam(value = "Name and Value of an environmental variable") NameValueParameter parameter) {	
		List<NameValueResponse> parameters = jzosService.setProperty(parameter) ;
		return Response.status(Status.CREATED).entity(parameters).build();
	}
	@PUT
	@Path(value = "{attribute}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Updates the value of an existing environment variable", 
	              notes = "This API uses JZOS to perform the process.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Updated the environment variable")})
	public Response update(
			@ApiParam(value = "Environment variable name", required = true)  @PathParam("attribute") String attribute,
			@ApiParam(value = "Value of an environmental variable") ValueParameter parameter) 
	{	
		jzosService.updateProperty(attribute, parameter) ;
		return Response.status(Status.OK).build();
	}
	@DELETE
	@Path(value = "{attribute}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Deletes an existing environment variable", 
	              notes = "This API uses JZOS to delete the environment variable.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Deleted the environment variable")})
	public Response delete(
			@ApiParam(value = "Name of an environmental variable", required = true)  @PathParam("attribute") String attribute) {	
		jzosService.deleteKey(attribute) ;
		return Response.status(Status.OK).build();
	}
	@GET
	@Path(value = "{attribute}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Gets a specific environment variables", 
	              notes = "This API uses JZOS to perform the process.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Ok", response = NameValueResponse.class, responseContainer = "List"),
		@ApiResponse(code = 404, message = "Environment variable does not exist")
	})
	public List<NameValueResponse> getOne(
		@ApiParam(value = "Name of an environmental variable", required = true)  @PathParam("attribute") String attribute) {	
		return jzosService.getProperty(attribute);
	}
	
}