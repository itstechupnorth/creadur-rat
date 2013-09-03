/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.rat.analysis.license;

import static org.apache.rat.api.domain.LicenseFamilyBuilder.aLicenseFamily;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * The Class BaseLicenseTest.
 */
public class BaseLicenseTest {

	/**
	 * Test license family constructor sets notes.
	 */
    @Test
    public void testLicenseFamilyConstructorSetsNotes() {
        final String someNotes = "Some notes about a license family";
        assertThat(new BaseLicense(aLicenseFamily().withNotes(someNotes)
                .build()).getNotes(), is(someNotes));
    }

	/**
	 * Test license family constructor sets category.
	 */
    @Test
    public void testLicenseFamilyConstructorSetsCategory() {
        final String someCategory = "http://some.category.org";
        assertThat(new BaseLicense(aLicenseFamily().withCategory(someCategory)
                .build()).getLicenseFamilyCategory(), is(someCategory));
    }

	/**
	 * Test license family constructor sets name.
	 */
    @Test
    public void testLicenseFamilyConstructorSetsName() {
        final String someName = "http://some.name.org";
        assertThat(
                new BaseLicense(aLicenseFamily().withName(someName).build())
                        .getLicenseFamilyName(),
                is(someName));
    }

	/**
	 * Test sets category.
	 */
	@Test
	public void testSetsCategory() {
		final String someName = "http://some.name.org";
		BaseLicense baseLicense = new BaseLicense();
		baseLicense.setLicenseFamilyCategory(someName);
		assertThat(baseLicense.getLicenseFamilyCategory(), is(someName));
	}

	/**
	 * Test sets name.
	 */
	@Test
	public void testSetsName() {
		final String someName = "http://some.name.org";
		BaseLicense baseLicense = new BaseLicense();
		baseLicense.setLicenseFamilyName(someName);
		assertThat(baseLicense.getLicenseFamilyName(), is(someName));
	}

	/**
	 * Test sets notes.
	 */
	@Test
	public void testSetsNotes() {
		final String someName = "http://some.name.org";
		BaseLicense baseLicense = new BaseLicense();
		baseLicense.setNotes(someName);
		assertThat(baseLicense.getNotes(), is(someName));
	}

}
