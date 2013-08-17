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
package org.apache.rat.report.xml.writer;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.apache.rat.report.xml.writer.impl.base.XmlWriter;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class XmlWriterUtilsTest.
 */
public class XmlWriterUtilsTest {

	/** The out. */
	StringWriter out;

	/** The writer. */
	IXmlWriter writer;

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		out = new StringWriter();
		writer = new XmlWriter(out);
		writer.openElement("alpha");
	}

	/**
	 * Write true.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void writeTrue() throws Exception {
		new XmlWriterUtils().writeAttribute(writer, "name", true);
		assertEquals("Attribute written as True", "<alpha name='true'",
				out.toString());
	}

	/**
	 * Write false.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void writeFalse() throws Exception {
		new XmlWriterUtils().writeAttribute(writer, "name", false);
		assertEquals("Attribute written as False", "<alpha name='false'",
				out.toString());
	}
}
