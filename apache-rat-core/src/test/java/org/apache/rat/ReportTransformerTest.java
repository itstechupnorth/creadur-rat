/*
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 */
package org.apache.rat;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

/**
 * The Class ReportTransformerTest.
 */
public class ReportTransformerTest {

	/** The Constant SIMPLE_CONTENT. */
	private static final String SIMPLE_CONTENT = "<?xml version='1.0'?>"
			+ "<directory name='sub'>"
			+ "<standard name='Empty.txt'>"
			+ "<license code='?????' name='UNKNOWN' version='' approved='false' generated='false'></license>"
			+ "</standard>" + "<directory name='.svn' restricted='true'/>"
			+ "</directory>";

	/**
	 * Test transform.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testTransform() throws Exception {
		StringWriter writer = new StringWriter();
		assertNotNull("Writer no to be null", writer);
		StringReader stringReader = new StringReader(SIMPLE_CONTENT);
		ReportTransformer transformer = new ReportTransformer(writer,
				new BufferedReader(new FileReader(new File(
						"src/main/resources/org/apache/rat/plain-rat.xsl"))),
				stringReader);
		transformer.transform();
	}

}
