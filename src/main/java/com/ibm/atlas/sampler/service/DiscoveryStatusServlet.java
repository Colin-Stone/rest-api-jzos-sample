package com.ibm.atlas.sampler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.atlas.sampler.model.App;
import com.ibm.atlas.sampler.model.DiscoveryInfo;
import com.ibm.atlas.sampler.model.InstanceInfo;
import com.ibm.atlas.sampler.model.MFaasInfo;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.ResourceBundle;

@Path(value = "/application/")
public class DiscoveryStatusServlet  {
    private static ResourceBundle eurekaProperties = ResourceBundle.getBundle("eureka-client");
    
    @Context
	UriInfo uriInfo;

	@Inject
	Logger log;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Gets App Info", 
    notes = "test")
    @Path(value = "/info")
    public static String getAppInfo() throws IOException {
        InstanceInfo instanceInfo = new InstanceInfo(
                new App(
                        eurekaProperties.getString("eureka.metadata.mfaas.api-info.apiVersionProperties.v1.title"),
                        eurekaProperties.getString("eureka.metadata.mfaas.api-info.apiVersionProperties.v1.description"),
                        eurekaProperties.getString("eureka.metadata.mfaas.api-info.apiVersionProperties.v1.version")
                ),
                new MFaasInfo(
                        new DiscoveryInfo(
                                eurekaProperties.getString("eureka.service.hostname"),
                                Boolean.valueOf(eurekaProperties.getString("eureka.securePortEnabled")),
                                eurekaProperties.getString("eureka.name"),
                                Integer.valueOf(eurekaProperties.getString("eureka.port")),
                                "CLIENT",
                                eurekaProperties.getString("eureka.name"),
                                Boolean.TRUE,
                                eurekaProperties.getString("eureka.metadata.mfaas.discovery.catalogUiTile.description")
                        )
                )
        );
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(instanceInfo);
        return json;
    }
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/health")
    public static String getHealth() throws IOException {
        return "UP";
    }
}
