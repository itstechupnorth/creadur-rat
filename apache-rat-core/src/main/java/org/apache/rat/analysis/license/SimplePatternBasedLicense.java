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

import org.apache.rat.analysis.IHeaderMatcher;
import org.apache.rat.api.Document;
import org.apache.rat.api.domain.LicenseFamily;

/**
 * The Class SimplePatternBasedLicense.
 * 
 * @since Rat 0.8
 */
public class SimplePatternBasedLicense extends BaseLicense implements
        IHeaderMatcher {

    /** The patterns. */
    private String[] patterns;

    private static final int ZERO = 0;

    /**
     * Constructs empty base license. Useful when creating an instance via
     * reflection.
     */
    public SimplePatternBasedLicense() {
        super();
    }

    /**
     * Constructs a license indicated by the given patterns.
     * 
     * @param pLicenseFamily
     *            not null
     * @param pPatterns
     *            not null
     */
    public SimplePatternBasedLicense(final LicenseFamily pLicenseFamily,
            final String... pPatterns) {
        super(pLicenseFamily);
        setPatterns(pPatterns);
    }

    /**
     * Gets the patterns.
     * 
     * @return the patterns
     */
    public String[] getPatterns() {
        return this.patterns.clone();
    }

    /**
     * Sets the patterns.
     * 
     * @param pPatterns
     *            the new patterns
     */
    public void setPatterns(final String... pPatterns) {
        this.patterns = pPatterns;
    }

    /**
     * Does the line match this license pattern?
     * 
     * @param pLine
     *            the line
     * @return true when the line matches, false otherwise
     */
    protected boolean matches(final String pLine) {
        boolean result = false;
        final String[] pttrns = getPatterns();
        if (pLine != null && pttrns != null) {
            for (final String pttrn : pttrns) {
                if (pLine.indexOf(pttrn, 0) >= ZERO) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * @see org.apache.rat.analysis.IHeaderMatcher#reset()
     */
    public void reset() {
        // Nothing to do
    }

    /**
     * @see org.apache.rat.analysis.IHeaderMatcher#match(org.apache.rat.api.Document,
     *      java.lang.String)
     */
    public boolean match(final Document pSubject, final String pLine) {
        final boolean result = matches(pLine);
        if (result) {
            reportOnLicense(pSubject);
        }
        return result;
    }
}
