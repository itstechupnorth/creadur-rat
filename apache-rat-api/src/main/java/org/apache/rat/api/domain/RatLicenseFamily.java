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
package org.apache.rat.api.domain;

import static org.apache.rat.api.domain.LicenseFamilyBuilder.aLicenseFamily;

/**
 * Enumerates standard license families known to Rat.
 */
public enum RatLicenseFamily {

    W3C("W3C Software Copyright", "W3C  ", "");

    /** @see LicenseFamily#getName() */
    private final String name;
    /** @see LicenseFamily#getCategory() */
    private final String category;
    /** @see LicenseFamily#getNotes() */
    private final String notes;
    /** Constructed from other data */
    private final LicenseFamily licenseFamily;

    /**
     * Constructs an instance.
     * 
     * @param name
     *            not null
     * @param category
     *            not null
     * @param notes
     *            not null
     */
    private RatLicenseFamily(final String name, final String category,
            final String notes) {
        this.name = name;
        this.category = category;
        this.notes = notes;
        this.licenseFamily =
                aLicenseFamily().withCategory(getCategory())
                        .withName(getName()).withNotes(getNotes()).build();
    }

    /**
     * @see LicenseFamily#getName()
     * 
     * @return not null
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see LicenseFamily#getCategory()
     * 
     * @return possibly null
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * @see LicenseFamily#getNotes()
     * 
     * @return possibly null
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Gets a {@link LicenseFamily} representing this data.
     * 
     * @return not null
     */
    public LicenseFamily licenseFamily() {
        return this.licenseFamily;
    }
}
