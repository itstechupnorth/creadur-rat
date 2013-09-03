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
package org.apache.rat.report.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.rat.ReportConfiguration;
import org.apache.rat.analysis.DefaultAnalyser;
import org.apache.rat.policy.DefaultPolicy;
import org.apache.rat.report.RatReport;
import org.apache.rat.report.claim.ClaimStatistic;
import org.apache.rat.report.claim.impl.ClaimAggregator;
import org.apache.rat.report.claim.impl.xml.SimpleXmlClaimReporter;
import org.apache.rat.report.claim.util.LicenseAddingReport;
import org.apache.rat.report.claim.util.Pipeline;
import org.apache.rat.report.xml.writer.IXmlWriter;

/**
 * Creates reports.
 * 
 */
public class XmlReportFactory {

	/**
	 * Instantiates a new xml report factory.
	 */
	public XmlReportFactory() {
		super();
	}

	/**
	 * Creates a new XmlReport object.
	 * 
	 * @param writer
	 *            the writer
	 * @param pStatistic
	 *            the statistic
	 * @param pConfiguration
	 *            the configuration
	 * @return the rat report
	 */
	public final RatReport createStandardReport(final IXmlWriter writer,
			final ClaimStatistic pStatistic,
			final ReportConfiguration pConfiguration) {
		final List<RatReport> reporters = new ArrayList<RatReport>();
		if (pStatistic != null) {
			reporters.add(new ClaimAggregator(pStatistic));
		}
		if (pConfiguration.isAddingLicenses()) {
			reporters.add(new LicenseAddingReport(pConfiguration
					.getCopyrightMessage(), pConfiguration
					.isAddingLicensesForced()));
		}
		reporters.add(new SimpleXmlClaimReporter(writer));
		final DefaultAnalyser analyser = new DefaultAnalyser(
				pConfiguration.getHeaderMatcher());
		final DefaultPolicy policy = new DefaultPolicy(
				pConfiguration.getApprovedLicenseNames());
		return new Pipeline(analyser, policy, reporters);
	}
}
