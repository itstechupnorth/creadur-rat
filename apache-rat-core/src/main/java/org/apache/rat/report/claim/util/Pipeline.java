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

import java.io.IOException;
import java.util.List;

import org.apache.rat.analysis.DefaultAnalyser;
import org.apache.rat.api.Document;
import org.apache.rat.policy.DefaultPolicy;
import org.apache.rat.report.RatReport;

/**
 * The Class Pipeline.
 */
public class Pipeline implements RatReport {

	/** The analyser. */
	private final DefaultAnalyser analyser;

	/** The policy. */
	private final DefaultPolicy policy;

	/** The reporters. */
	private final List<? extends RatReport> reporters;

	/**
	 * Instantiates a new pipeline.
	 * 
	 * @param analyser
	 *            the analyser
	 * @param policy
	 *            the policy
	 * @param reporters
	 *            the reporters
	 */
	public Pipeline(final DefaultAnalyser analyser, final DefaultPolicy policy,
			final List<? extends RatReport> reporters) {
		super();
		this.analyser = analyser;
		this.policy = policy;
		this.reporters = reporters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.rat.report.RatReport#report(org.apache.rat.api.Document)
	 */
	public void report(final Document document) throws IOException {
		if (this.analyser != null) {
				this.analyser.analyse(document);
		}

		if (this.policy != null) {
			this.policy.analyse(document);
		}

		for (RatReport report : this.reporters) {
			report.report(document);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.rat.report.RatReport#startReport()
	 */
	public void startReport() throws IOException {
		for (RatReport report : this.reporters) {
			report.startReport();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.rat.report.RatReport#endReport()
	 */
	public void endReport() throws IOException {
		for (RatReport report : this.reporters) {
			report.endReport();
		}
	}
}
