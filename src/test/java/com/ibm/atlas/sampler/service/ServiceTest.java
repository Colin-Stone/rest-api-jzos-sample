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

import com.ibm.atlas.sampler.environ.Environment;
import com.ibm.atlas.sampler.params.NameValueParameter;
import com.ibm.atlas.sampler.params.ValueParameter;
import com.ibm.atlas.sampler.resources.NameValueResponse;
import com.ibm.jzos.ZUtil;

import java.util.logging.Logger;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({"com.ibm.jzos.ZUtil"})
@PrepareForTest({ZUtil.class,Response.class,ResponseBuilder.class, WebApplicationException.class, Response.StatusType.class, Logger.class})
public class ServiceTest {

	private String[] variables = new String[] {"JAVA_HOME=/java/J8.0_64/", "FILE_MANAGER_HLQ=PP.FILEMAN.V13"};
	private Service service;
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
		service = new Service();
		MemberModifier.field(Service.class, "log").set(service, Logger.getLogger("Tester"));
		PowerMockito.mockStatic(ZUtil.class);
		PowerMockito.when(ZUtil.environ()).thenReturn(variables);
		PowerMockito.when(ZUtil.getEnv("FILE_MANAGER_HLQ")).thenReturn("PP.FILEMAN.V13");
		PowerMockito.when(ZUtil.getEnv("JAVA_HOME")).thenReturn("/java/J8.0_64/");
		PowerMockito.when(ZUtil.getEnv("NOEXIST")).thenReturn(null);
		Response response = PowerMockito.mock(Response.class);
		PowerMockito.mockStatic(Response.class);
		ResponseBuilder responseBuilder = (ResponseBuilder) PowerMockito.mock(ResponseBuilder.class);
		PowerMockito.when(Response.status(Status.NOT_FOUND)).thenReturn(responseBuilder);
		PowerMockito.when(Response.status(Status.BAD_REQUEST)).thenReturn(responseBuilder);
		PowerMockito.when(responseBuilder.entity(Mockito.anyString())).thenReturn(responseBuilder);
		PowerMockito.when(responseBuilder.type(Mockito.anyString())).thenReturn(responseBuilder);
		PowerMockito.when(responseBuilder.build()).thenReturn(response);		
		StatusType statusType = PowerMockito.mock(Response.StatusType.class);
		PowerMockito.when(response.getStatusInfo()).thenReturn(statusType);
		PowerMockito.when(statusType.getStatusCode()).thenReturn(Status.NOT_FOUND.getStatusCode());
		PowerMockito.when(statusType.getReasonPhrase()).thenReturn("Busted");
		PowerMockito.when(responseBuilder.type(Mockito.anyString())).thenReturn(responseBuilder);
		PowerMockito.when(responseBuilder.build()).thenReturn(response);	
	}
	
	@Test
	public void testValidGetPropertyList() throws Exception {
		List<NameValueResponse> theList = service.getPropertyList();
		assertEquals("ServiceTest.testValidGetPropertyList failed list size", 2, theList.size());
		for (NameValueResponse pair: theList) {
			if (pair.getName().equals("JAVA_HOME")) {
				assertEquals("ServiceTest.testValidGetPropertyList failed compare element 1", "/java/J8.0_64/", pair.getValue());
			} else if (pair.getName().equals("FILE_MANAGER_HLQ")) {
				assertEquals("ServiceTest.testValidGetPropertyList failed compare element 2", "PP.FILEMAN.V13", pair.getValue());
			} else {
				fail("ServiceTest.testValidGetPropertyList failed compare extra element");
			}
		}
	}
	@Test
	public void testGetPropertyPass()  throws Exception {
		List<NameValueResponse> theList = service.getProperty("FILE_MANAGER_HLQ");
		assertEquals("ServiceTest.testGetPropertyPass failed list size", 1, theList.size());
		for (NameValueResponse pair: theList) {
			if (pair.getName().equals("FILE_MANAGER_HLQ")) {
				assertEquals("ServiceTest.testGetPropertyPass failed compare element", "PP.FILEMAN.V13", pair.getValue());
			} else {
				fail("ServiceTest.testGetPropertyPass failed compare extra element");
			}
		}
	}
	@Test(expected = WebApplicationException.class)
	public void testGetPropertyFail()  throws Exception {
		service.getProperty("NO_EXIST");
	}
	@Test
	public void testSetPropertyPass()  throws Exception {
		service.setProperty(setVariable1);
	}
	@Test(expected = WebApplicationException.class)
	public void testSetPropertyFail1()  throws Exception {
		service.setProperty(setVariable2);
	}
	@Test(expected = WebApplicationException.class)
	public void testSetPropertyFail2()  throws Exception {
		service.setProperty(setVariable3);
	}
	@Test
	public void testUpdatePropertyPass()  throws Exception {
		service.updateProperty("FILE_MANAGER_HLQ", goodUpdateJSON);
	}
//	@Test(expected = WebApplicationException.class)
	public void testUpdatePropertyFail1()  throws Exception {
		service.updateProperty("FILE_MANAGER_HLQ", badJSON1);
	}
	@Test(expected = WebApplicationException.class)
	public void testUpdatePropertyFail2()  throws Exception {
		service.updateProperty("FILliE_MANAGER_HLQ", goodUpdateJSON);
	}
//	@Test(expected = WebApplicationException.class)
	public void testUpdatePropertyFail3()  throws Exception {
		service.updateProperty("FILE_MANAGER_HLQ", badUpdateJSON3);
	}
	@Test
	public void testDeletePropertyPass()  throws Exception {
		service.deleteKey("FILE_MANAGER_HLQ");
	}
	@Test(expected = WebApplicationException.class)
	public void testDeletePropertyFail()  throws Exception {
		service.deleteKey("FILFFR_MANGLER_HLQ");
	}
}
