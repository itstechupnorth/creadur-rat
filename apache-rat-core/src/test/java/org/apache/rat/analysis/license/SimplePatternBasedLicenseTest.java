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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * The Class SimplePatternBasedLicenseTest.
 */
public class SimplePatternBasedLicenseTest {

	/**
	 * Test license family constructor sets notes.
	 */
    @Test
    public void testLicenseFamilyConstructorSetsNotes() {
		String someNotes = "Some notes about a license family";
        assertThat(
                new SimplePatternBasedLicense(aLicenseFamily().withNotes(
                        someNotes).build()).getNotes(), is(someNotes));
    }

	/**
	 * Test license family constructor sets category.
	 */
    @Test
    public void testLicenseFamilyConstructorSetsCategory() {
		String someCategory = "http://some.category.org";
        assertThat(
                new SimplePatternBasedLicense(aLicenseFamily().withCategory(
                        someCategory).build()).getLicenseFamilyCategory(),
                is(someCategory));
    }

	/**
	 * Test license family constructor sets name.
	 */
    @Test
    public void testLicenseFamilyConstructorSetsName() {
		String someName = "http://some.name.org";
        assertThat(
                new SimplePatternBasedLicense(aLicenseFamily().withName(
                        someName).build()).getLicenseFamilyName(), is(someName));
    }

	/**
	 * Test license family constructor default.
	 */
	@Test
	public void testLicenseFamilyConstructorDefault() {
		assertNotNull("Not null Object", new SimplePatternBasedLicense());
	}
}
