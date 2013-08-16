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
package org.apache.rat.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.rat.api.Document;
import org.junit.Test;

/**
 * The Class ToNameTransformerTest.
 */
public class ToNameTransformerTest {

	/** The transformer. */
	private ToNameTransformer transformer = new ToNameTransformer();

	/**
	 * Transform location.
	 */
	@Test
	public void testTransformLocation() {
		Document location = new MockLocation();
		Object result = transformer.transform(location);
		assertEquals("Transform into name", location.getName(), result);
	}

	/**
	 * Transform null.
	 */
	@Test
	public void testTransformNull() {
		Object result = transformer.transform(null);
		assertNull("Null transforms to null", result);
	}

	/**
	 * Test transform not null.
	 */
	@Test
	public void testTransformNotNull() {
		assertNotNull("Null transforms to null",
				ToNameTransformer.toNameTransformer());
	}
}
