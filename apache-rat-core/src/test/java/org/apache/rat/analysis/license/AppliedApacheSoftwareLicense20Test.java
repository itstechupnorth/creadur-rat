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
package org.apache.rat.analysis.license;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;
import org.apache.rat.api.MetaData.Datum;
import org.apache.rat.document.MockLocation;
import org.apache.rat.test.utils.Resources;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class AppliedApacheSoftwareLicense20Test {

	private static final String HEADER = "/*\n"
			+ " *  Copyright 2012-2013 FooBar.\n"
			+ " *\n"
			+ " *  Licensed under the Apache License, Version 2.0 (the \"License\");\n"
			+ " *  you may not use this file except in compliance with the License.\n"
			+ " *\n"
			+ " *  You may obtain a copy of the License at\n"
			+ " *       http://www.apache.org/licenses/LICENSE-2.0\n"
			+ " *\n"
			+ " *  Unless required by applicable law or agreed to in writing, software\n"
			+ " *  distributed under the License is distributed on an \"AS IS\" BASIS,\n"
			+ " *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
			+ " *  See the License for the specific language governing permissions and\n"
			+ " *  limitations under the License.\n" + " */\n";

	private AppliedApacheSoftwareLicense20 license;


	@Before
	public void setUp() throws Exception {
		license = new AppliedApacheSoftwareLicense20("FooBar");
	}

	@Test
	public void testMatchAppliedApacheLicense() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new StringReader(
				HEADER));
		String line = bufferedReader.readLine();
		boolean result = false;
		final Document subject = new MockLocation("subject");
		while (line != null) {
			result = license.match(subject, line);
			line = bufferedReader.readLine();
		}
		assertTrue("Applied AL2.0 license should be matched", result);
		license.reset();
	}

	@Test
	public void testNoMatchAppliedApacheLicense() throws IOException {
		BufferedReader bufferedReader = Resources
				.getBufferedResourceReader("elements/Source.java");
		String line = bufferedReader.readLine();
		boolean result = false;
		final Document subject = new MockLocation("subject");
		while (line != null) {
			result = license.match(subject, line);
			line = bufferedReader.readLine();
		}
		assertFalse("Applied AL2.0 license should not be matched", result);
		license.reset();
	}

	@Test
	public void testNotNullCopyRightOwner() {
		Assert.assertNotNull(license.getCopyRightOwner());
	}

	@Test
	public void testHasCopyrightPattern() {
		assertTrue("copyrightPattern not null", license.hasCopyrightPattern());
	}

	@Test
	public void testHasFullText() {
		Datum licenseFamilyCategory = new Datum(
				MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY,
				MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY);
		Datum licenseFamilyName = new Datum(
				MetaData.RAT_URL_LICENSE_FAMILY_NAME,
				MetaData.RAT_URL_LICENSE_FAMILY_NAME);
		String fullText = "";
		FullTextMatchingLicense fullTextMatchingLicense = new FullTextMatchingLicense(
				licenseFamilyCategory, licenseFamilyName, null, fullText);
		assertTrue("fullText not null", fullTextMatchingLicense.hasFullText());
	}
}
