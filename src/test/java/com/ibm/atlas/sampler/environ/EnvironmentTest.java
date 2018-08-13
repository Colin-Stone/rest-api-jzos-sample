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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ibm.atlas.sampler.params.NameValueParameter;
import com.ibm.atlas.sampler.params.ValueParameter;
import com.ibm.atlas.sampler.resources.NameValueResponse;
import com.ibm.atlas.sampler.service.Service;
import com.ibm.jzos.ZUtil;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({"com.ibm.jzos.ZUtil"})
@PrepareForTest({ZUtil.class,Response.class,ResponseBuilder.class, WebApplicationException.class, 
	Response.StatusType.class, Logger.class})
public class EnvironmentTest {
	private Environment testclass;
	private Service service;
	
	private String[] variables = new String[] {"JAVA_HOME=/java/J8.0_64/", "FILE_MANAGER_HLQ=PP.FILEMAN.V13"};
	private String goodJSON = "{\"attribute\":\"TEST_HOME\",\"value\":\"/java/J8.0_64/\"}";
	private NameValueParameter setVariable1 =  new NameValueParameter("TEST_HOME", "/java/J8.0_64/");
	private NameValueParameter setVariable2 =  new NameValueParameter("JAVA_HOME", "/java/J8.0_64/");
	private NameValueParameter setVariable3 =  new NameValueParameter("TEST_HOME", null);
	private ValueParameter goodUpdateJSON = new ValueParameter("/java/J8.0_64/");
	private String setFailJSON = "{\"attribute\":\"FILE_MANAGER_HLQ\",\"value\":\"/java/J8.0_64/\"}";
	private ValueParameter badJSON1 = new ValueParameter("{\"attribute\"\"FILE_MANAGER_HLQ\",\"value\",\"/java/J8.0_64/\"}");
	private String badJSON2 = "{\"attrute\"\"GER_HLQ\",\"value\":\"/java/J8.0_64/\"}";
	private String badJSON3 = "{\"attribute\"\"GER_HLQ\",\"vae\":\"/java/J8.0_64/\"}";
	private ValueParameter badUpdateJSON3 = new ValueParameter("{\"vae\":\"/java/J8.0_64/\"}");
	
	@Before
	public void init() throws Exception {
		testclass = new Environment();
		service = new Service();
		MemberModifier.field(Environment.class, "jzosService").set(testclass, service);
		MemberModifier.field(Environment.class, "log").set(testclass, Logger.getLogger("Tester"));
		MemberModifier.field(Service.class, "log").set(service, Logger.getLogger("Tester"));
		
		PowerMockito.mockStatic(ZUtil.class);
		PowerMockito.when(ZUtil.environ()).thenReturn(variables);
		PowerMockito.when(ZUtil.getEnv("FILE_MANAGER_HLQ")).thenReturn("PP.FILEMAN.V13");
		PowerMockito.when(ZUtil.getEnv("F_HLQ")).thenReturn(null);
		PowerMockito.when(ZUtil.getEnv("JAVA_HOME")).thenReturn("/java/J8.0_64/");
		PowerMockito.when(ZUtil.getEnv("NOEXIST")).thenReturn(null);

		PowerMockito.mockStatic(Response.class);
		ResponseBuilder responseBuilder = (ResponseBuilder) PowerMockito.mock(ResponseBuilder.class);
		PowerMockito.when(Response.status(Status.OK)).thenReturn(responseBuilder);
		Response response = PowerMockito.mock(Response.class);
		PowerMockito.when(response.getStatus()).thenReturn(200);
		PowerMockito.when(Response.status(Status.BAD_REQUEST)).thenReturn(responseBuilder);
		PowerMockito.when(Response.status(Status.NOT_FOUND)).thenReturn(responseBuilder);
		PowerMockito.when(Response.status(Status.CREATED)).thenReturn(responseBuilder);
		PowerMockito.when(responseBuilder.entity(Mockito.any())).thenReturn(responseBuilder);
		PowerMockito.when(responseBuilder.type(Mockito.anyString())).thenReturn(responseBuilder);
		PowerMockito.when(responseBuilder.build()).thenReturn(response);		
		StatusType statusType = PowerMockito.mock(Response.StatusType.class);
		PowerMockito.when(response.getStatusInfo()).thenReturn(statusType);
		PowerMockito.when(statusType.getStatusCode()).thenReturn(Status.BAD_REQUEST.getStatusCode());
		PowerMockito.when(statusType.getReasonPhrase()).thenReturn("Busted");
	}
	
	
	
	@Test
	public void testGetAll() {
		List<NameValueResponse> results = testclass.getAll();
		assertEquals("EnvironmentTest.testGetAll incorrect results", 2, results.size());
	}

	@Test
	public void testAdd() {
		Response response = testclass.add(new NameValueParameter("F_HLQ", "Test"));
		assertEquals("EnvironmentTest.testAdd incorrect results", Status.OK.getStatusCode(), response.getStatus() );
	}
	@Test(expected = WebApplicationException.class)
	public void testAddFailNullPAssed() {
		Response response = testclass.add(new NameValueParameter("F_HLQ", null));
		fail("testAddFailNullPAssed should have thrown exception");	}
	@Test
	public void testUpdate() {
		Response response = testclass.update("FILE_MANAGER_HLQ", new ValueParameter("Test"));
		assertEquals("EnvironmentTest.testUpdate incorrect results", Status.OK.getStatusCode(), response.getStatus() );
	}
	@Test(expected = WebApplicationException.class)
	public void testUpdateFailExistsAlready() {
		Response response = testclass.update("F_HLQ", new ValueParameter("Test"));
		fail("testUpdateFailExistsAlready should have thrown exception");	}

	@Test(expected = WebApplicationException.class)
	public void testUpdateFailNullPassed() {
		Response response = testclass.update("F_HLQ", new ValueParameter(null));
		fail("testUpdateFailNullPassed should have thrown exception");
	}

	@Test
	public void testDelete() {
		Response response = testclass.delete("FILE_MANAGER_HLQ");
		assertEquals("EnvironmentTest.testDelete incorrect results", Status.OK.getStatusCode(), response.getStatus() );
	}

	@Test(expected = WebApplicationException.class)
	public void testDeleteFailNoKey() {
		Response response = testclass.delete("F_HLQ");
		assertEquals("EnvironmentTest.testDeleteFailNoKey incorrect results", Status.OK.getStatusCode(), response.getStatus() );
	}
	@Test
	public void testGetOne() {
		List<NameValueResponse> results = testclass.getOne("FILE_MANAGER_HLQ");
		assertEquals("EnvironmentTest.testGetOne incorrect result size", 1, results.size());
		assertEquals("EnvironmentTest.testGetOne incorrect key name", "FILE_MANAGER_HLQ", results.get(0).getName());
		assertEquals("EnvironmentTest.testGetOne incorrect value", "PP.FILEMAN.V13", results.get(0).getValue());
	}
	
	@Test(expected = WebApplicationException.class)
	public void testGetOneFailNoKey() {
		List<NameValueResponse> results = testclass.getOne("FIL_MANAGER_HLQ");
	}

}
