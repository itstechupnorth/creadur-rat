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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.apache.rat.analysis.IHeaderMatcher;
import org.apache.rat.analysis.license.CDDL1License;
import org.apache.rat.api.Document;
import org.apache.rat.document.MockLocation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 */
public class CDDL1LicenseTest {

    private static final String LICENSE_LINE =
            " DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.\n\n"
            + "Copyright 2011-2013 Tirasa. All rights reserved.\n\n"
            + "The contents of this file are subject to the terms of the Common Development\n"
            + "and Distribution License(\"CDDL\") (the \"License\"). You may not use this file\n"
            + "except in compliance with the License.\n\n"
            + "You can obtain a copy of the License at https://oss.oracle.com/licenses/CDDL\n"
            + "See the License for the specific language governing permissions and limitations\n"
            + "under the License.";

    /**
     * To ease testing provide a map with a given license version and the string to test for.
     */
    private static Map<IHeaderMatcher, String> licenseStringMap;

    private Document subject;
    
    /**
	 * Inits the licences under test.
	 */
    @BeforeClass
    public static void initLicencesUnderTest() {
        licenseStringMap = new HashMap<IHeaderMatcher, String>();
        licenseStringMap.put(new CDDL1License(), LICENSE_LINE);
        assertEquals("Must be One element", 1, licenseStringMap.entrySet()
				.size());
    }

    @Before
    public final void initSubject() {
        this.subject = new MockLocation("subject");
    }

    @Test
    public void testNegativeMatchCDDL1License() throws Exception {
    	for (Map.Entry<IHeaderMatcher, String> licenceUnderTest : licenseStringMap
				.entrySet()) {
			assertFalse(
					"Not Matches the  CDDL1 License",
					licenceUnderTest.getKey().match(subject,
							"'Behold, Telemachus! (nor fear the sight,)"));
		}
    }
    
	@Test
	public void testNegativeMatchCDDL1LicenseEmptyPattern() throws Exception {
		CDDL1License licenseCDDL1 = new CDDL1License();
		licenseCDDL1.setPatterns(new String[0]);
		assertFalse("Not Matches the  CDDL1 License", licenseCDDL1.match(
				subject, "'Behold, Telemachus! (nor fear the sight,)"));
	}

    @Test
    public void testPositiveMatchCDDL1License() throws Exception {
    	for (Map.Entry<IHeaderMatcher, String> licenceUnderTest : licenseStringMap.entrySet()) {
			assertTrue("Not Matches the  CDDL1 License", licenceUnderTest
					.getKey()
					.match(subject, "\t" + licenceUnderTest.getValue()));
        }
    }
    
	@Test
	public void testNotes() {
		assertThat(
				new CDDL1License().getNotes(),
				is("Note that CDDL1 requires a NOTICE. All modifications require notes. See https://oss.oracle.com/licenses/CDDL."));
	}

	@Test
	public void testCategory() {
		assertThat(new CDDL1License().getLicenseFamilyCategory(), is("CDDL1"));
	}

	@Test
	public void testName() {
		assertThat(new CDDL1License().getLicenseFamilyName(),
				is("COMMON DEVELOPMENT AND DISTRIBUTION LICENSE Version 1.0"));
	}
}
