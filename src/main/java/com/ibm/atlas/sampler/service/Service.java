/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2016, 2018
 */

package com.ibm.atlas.sampler.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ibm.atlas.sampler.params.NameValueParameter;
import com.ibm.atlas.sampler.params.ValueParameter;
import com.ibm.atlas.sampler.resources.NameValueResponse;
import com.ibm.json.java.JSONObject;
import com.ibm.jzos.ZUtil;

@Stateless
public class Service {

	@Inject
	Logger log;
	
	public Service() {}

	public List<NameValueResponse> getPropertyList() {
		List<NameValueResponse> pairs = new ArrayList<NameValueResponse>();
		try {
			for (String element:ZUtil.environ()) {
				String[] tokens = element.split(" |=");
				pairs.add(new NameValueResponse(tokens[0],tokens[1]));
			}
		} catch (Exception e) {
			String error = String.format("Attempt to read environment list failed: %s", e.getMessage());
			log.severe(error);
			Response errorResponse = Response.serverError().entity(error).type(MediaType.TEXT_PLAIN).build();
			throw new WebApplicationException(errorResponse);
		}
		return pairs;
	}

	public List<NameValueResponse> getProperty(String attribute) {
		List<NameValueResponse> pairs = new ArrayList<NameValueResponse>();
		String value = null;
		try {
			value =  ZUtil.getEnv(attribute);
		} catch (Exception e) {
			String error = String.format("Attempt to read environment %s failed: %s", attribute, e.getMessage());
			log.severe(error);
			Response errorResponse = Response.serverError().entity(error).type(MediaType.TEXT_PLAIN).build();
			throw new WebApplicationException(errorResponse);
		}
		if (value==null) {
			String error = String.format("Environment variable %s not found", attribute); 
			Response errorResponse = Response.status(Status.NOT_FOUND).entity(error).type(MediaType.TEXT_PLAIN).build();
			throw new WebApplicationException(errorResponse);
		}
		pairs.add(new NameValueResponse(attribute,value));
		return pairs;
	}

	public List<NameValueResponse> setProperty(NameValueParameter request) {
		List<NameValueResponse> pairs = new ArrayList<NameValueResponse>();
		if (ZUtil.getEnv(request.getName())!=null) {
			throwup("The attribute already exists. Use PUT to update it"); //$NON-NLS-1$
		}
		if (request.getValue() != null ) {
			ZUtil.setEnv(request.getName(),request.getValue());
			pairs.add(new NameValueResponse(request.getName(), request.getValue()));
		} else {
			throwup("Value equates to null. No action taken"); //$NON-NLS-1$
		}
		return pairs;
	}
	protected WebApplicationException throwup(String error) {
		log.log(Level.SEVERE, error);
		Response errorResponse = Response.status(Status.BAD_REQUEST).
				entity(error).
				type(MediaType.TEXT_PLAIN).
				build();
		throw new WebApplicationException(errorResponse);
	}
	public void updateProperty(String attribute, ValueParameter value) {
		if (ZUtil.getEnv(attribute)==null) {
			throwup("The attribute does not exist. Use POST to create it"); //$NON-NLS-1$
		}
		if (value.getValue() != null ) {
			System.out.println("value.getValue() "+value.getValue());
			ZUtil.setEnv(attribute,value.getValue());
		} else {
			throwup("Value equates to null. No action taken"); //$NON-NLS-1$
		} 
	}
	public void deleteKey(String attribute) {
		if (ZUtil.getEnv(attribute)==null) {
			throwup("The attribute does not exist anyway. No further action"); //$NON-NLS-1$
		}
		ZUtil.setEnv(attribute, null);
	}
}