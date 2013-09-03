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
package org.apache.rat.report.claim.util;

import java.io.File;
import java.io.IOException;

import org.apache.rat.annotation.AbstractLicenceAppender;
import org.apache.rat.annotation.ApacheV2LicenceAppender;
import org.apache.rat.api.MetaData;
import org.apache.rat.api.MetaData.Datum;
import org.apache.rat.report.AbstractReport;

/**
 * The Class LicenseAddingReport.
 */
public class LicenseAddingReport extends AbstractReport {

	/** The appender. */
	private final AbstractLicenceAppender appender;

	/**
	 * Instantiates a new license adding report.
	 * 
	 * @param pCopyrightMsg
	 *            the copyright msg
	 * @param pForced
	 *            the forced
	 */
	public LicenseAddingReport(final String pCopyrightMsg, final boolean pForced) {
		super();
		appender = pCopyrightMsg == null ? new ApacheV2LicenceAppender()
				: new ApacheV2LicenceAppender(pCopyrightMsg);
		appender.setForce(pForced);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.rat.report.AbstractReport#report(org.apache.rat.api.Document)
	 */
	@Override
	public void report(final org.apache.rat.api.Document document)
			throws IOException {
		final MetaData metaData = document.getMetaData();
		final Datum licenseHeader = metaData
				.get(MetaData.RAT_URL_HEADER_CATEGORY);
		if (licenseHeader == null
				|| MetaData.RAT_LICENSE_FAMILY_CATEGORY_DATUM_UNKNOWN
						.getValue().equals(licenseHeader.getValue())) {
			final File file = new File(document.getName());
			if (file.isFile()) {
				appender.append(file);
			}
		}
		metaData.getData();
	}
}
