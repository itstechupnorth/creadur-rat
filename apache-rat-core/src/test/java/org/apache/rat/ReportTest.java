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
package org.apache.rat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.security.Permission;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.rat.analysis.IHeaderMatcher;
import org.apache.rat.analysis.generation.GeneratedLicenseNotRequired;
import org.apache.rat.analysis.util.HeaderMatcherMultiplexer;
import org.apache.rat.report.claim.ClaimStatistic;
import org.apache.rat.test.utils.Resources;
import org.apache.rat.walker.DirectoryWalker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class ReportTest.
 */
public class ReportTest {

	/**
	 * The Class ExitException.
	 */
	protected static class ExitException extends SecurityException {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The status. */
		public final int status;

		/**
		 * Instantiates a new exit exception.
		 * 
		 * @param status
		 *            the status
		 */
		public ExitException(int status) {
			super("There is no escape!");
			this.status = status;
		}
	}

	/**
	 * The Class NoExitSecurityManager.
	 */
	private static class NoExitSecurityManager extends SecurityManager {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.lang.SecurityManager#checkPermission(java.security.Permission)
		 */
		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.lang.SecurityManager#checkPermission(java.security.Permission,
		 * java.lang.Object)
		 */
		@Override
		public void checkPermission(Permission perm, Object context) {
			// allow anything.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.SecurityManager#checkExit(int)
		 */
		@Override
		public void checkExit(int status) {
			super.checkExit(status);
			throw new ExitException(status);
		}
	}

	/**
	 * The Class ExitSecurityManager.
	 */
	private static class ExitSecurityManager extends SecurityManager {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.lang.SecurityManager#checkPermission(java.security.Permission)
		 */
		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.lang.SecurityManager#checkPermission(java.security.Permission,
		 * java.lang.Object)
		 */
		@Override
		public void checkPermission(Permission perm, Object context) {
			// allow anything.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.SecurityManager#checkExit(int)
		 */
		@Override
		public void checkExit(int status) {
			super.checkExit(status);
		}
	}

	/** The Constant HEADER. */
	private static final String HEADER = "\n"
			+ "*****************************************************\n"
			+ "Summary\n" + "-------\n" + "Generated at: ";

	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		System.setSecurityManager(new NoExitSecurityManager());
	}

	/**
	 * Gets the elements reports.
	 * 
	 * @param pElementsPath
	 *            the elements path
	 * @return the elements reports
	 */
	private static String getElementsReports(final String pElementsPath) {
		return "Notes: 2\n" + "Binaries: 1\n" + "Archives: 1\n"
				+ "Standards: 6\n" + "\n" + "Apache Licensed: 3\n"
				+ "Generated Documents: 0\n" + "\n"
				+ "JavaDocs are generated and so license header is optional\n"
				+ "Generated files do not required license headers\n" + "\n"
				+ "2 Unknown Licenses\n" + "\n"
				+ "*******************************\n" + "\n"
				+ "Unapproved licenses:\n" + "\n" + "  "
				+ pElementsPath
				+ "/Source.java\n"
				+ "  "
				+ pElementsPath
				+ "/sub/Empty.txt\n"
				+ "\n"
				+ "*******************************\n"
				+ "\n"
				+ "Archives:\n"
				+ "\n"
				+ " + "
				+ pElementsPath
				+ "/dummy.jar\n"
				+ " \n"
				+ "*****************************************************\n"
				+ "  Files with Apache License headers will be marked AL\n"
				+ "  Binary files (which do not require AL headers) will be marked B\n"
				+ "  Compressed archives will be marked A\n"
				+ "  Notices, licenses etc will be marked N\n"
				+ "  MIT   "
				+ pElementsPath
				+ "/ILoggerFactory.java\n"
				+ "  B     "
				+ pElementsPath
				+ "/Image.png\n"
				+ "  N     "
				+ pElementsPath
				+ "/LICENSE\n"
				+ "  N     "
				+ pElementsPath
				+ "/NOTICE\n"
				+ " !????? "
				+ pElementsPath
				+ "/Source.java\n"
				+ "  AL    "
				+ pElementsPath
				+ "/Text.txt\n"
				+ "  AL    "
				+ pElementsPath
				+ "/Xml.xml\n"
				+ "  AL    "
				+ pElementsPath
				+ "/buildr.rb\n"
				+ "  A     "
				+ pElementsPath
				+ "/dummy.jar\n"
				+ " !????? "
				+ pElementsPath
				+ "/sub/Empty.txt\n"
				+ " \n"
				+ "*****************************************************\n"
				+ " Printing headers for files without AL header...\n"
				+ " \n"
				+ " \n"
				+ "=======================================================================\n"
				+ "=="
				+ pElementsPath
				+ "/Source.java\n"
				+ "=======================================================================\n"
				+ "package elements;\n"
				+ "\n"
				+ "/*\n"
				+ " * This file does intentionally *NOT* contain an AL license header,\n"
				+ " * because it is used in the test suite.\n"
				+ " */\n"
				+ "public class Source {\n"
				+ "\n"
				+ "}\n"
				+ "\n"
				+ "=======================================================================\n"
				+ "=="
				+ pElementsPath
				+ "/sub/Empty.txt\n"
				+ "=======================================================================\n"
				+ "\n";
	}

	/**
	 * Plain report.
	 * 
	 * @throws TransformerConfigurationException
	 *             the transformer configuration exception
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testPlainReport() throws TransformerConfigurationException,
			FileNotFoundException, IOException, InterruptedException {
		StringWriter out = new StringWriter();
		HeaderMatcherMultiplexer matcherMultiplexer = new HeaderMatcherMultiplexer(
				Defaults.DEFAULT_MATCHERS);
		final String elementsPath = Resources
				.getResourceDirectory("elements/Source.java");
		final ReportConfiguration configuration = new ReportConfiguration();
		configuration.setHeaderMatcher(matcherMultiplexer);
		Report.report(out, new DirectoryWalker(new File(elementsPath)),
				new Defaults().getPlainStyleSheet(), configuration);
		String result = out.getBuffer().toString();
		final String lineSeparator = System.getProperty("line.separator");
		assertTrue("'Generated at' is present in " + result,
				result.startsWith(HEADER.replaceAll("\n", lineSeparator)));
		final int generatedAtLineEnd = result.indexOf(lineSeparator,
				HEADER.length());
		final String elementsReports = getElementsReports(elementsPath);
		assertEquals("Report created",
				elementsReports.replaceAll("\n", lineSeparator),
				result.substring(generatedAtLineEnd + lineSeparator.length()));
	}

	/**
	 * Test input file filter.
	 */
	@Test
	public void testInputFileFilter() {
		Report report = new Report(null);
		final Pattern pattern = Pattern.compile(
				".*Copyright [0-9]{4}(\\-[0-9]{4})? " + ".*",
				Pattern.CASE_INSENSITIVE);
		FilenameFilter inputFileFilter = new FilenameFilter() {
			public boolean accept(final File dir, final String name) {
				boolean result = false;
				if (pattern == null) {
					result = true;
				} else {
					result ^= pattern.matcher(name).matches();
				}
				return result;
			}
		};
		report.setInputFileFilter(inputFileFilter);
		assertNotNull(report.getInputFileFilter());
	}

	/**
	 * Test report claim statistic.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testReportClaimStatistic() throws IOException {
		Report report = new Report(
				Resources.getResourceDirectory("elements/Source.java"));
		PrintStream out = System.out;
		ReportConfiguration configuration = new ReportConfiguration();
		IHeaderMatcher headerMatcher = new GeneratedLicenseNotRequired();
		configuration.setHeaderMatcher(headerMatcher);
		ClaimStatistic claimStatistic = report.report(out, configuration);
		assertNotNull(claimStatistic);
	}

	/**
	 * Test report claim statistic file.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testReportClaimStatisticFile() throws IOException {
		Report report = new Report("src/test/resources/elements/Source.java");
		PrintStream out = System.out;
		ReportConfiguration configuration = new ReportConfiguration();
		IHeaderMatcher headerMatcher = new GeneratedLicenseNotRequired();
		configuration.setHeaderMatcher(headerMatcher);
		ClaimStatistic claimStatistic = report.report(out, configuration);
		assertNotNull(claimStatistic);
	}

	/**
	 * Test report claim statistic not file.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testReportClaimStatisticNotFile() throws IOException {
		Report report = new Report("src/test/resources/elements/Source.txt");
		PrintStream out = System.out;
		ReportConfiguration configuration = new ReportConfiguration();
		IHeaderMatcher headerMatcher = new GeneratedLicenseNotRequired();
		configuration.setHeaderMatcher(headerMatcher);
		ClaimStatistic claimStatistic = report.report(out, configuration);
		assertNull(claimStatistic);
	}

	/**
	 * Test style report.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformerConfigurationException
	 *             the transformer configuration exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testStyleReport() throws IOException,
			TransformerConfigurationException, InterruptedException {
		Report report = new Report(
				Resources.getResourceDirectory("elements/Source.java"));
		PrintStream out = System.out;
		ReportConfiguration configuration = new ReportConfiguration();
		IHeaderMatcher headerMatcher = new GeneratedLicenseNotRequired();
		configuration.setHeaderMatcher(headerMatcher);
		report.styleReport(out, configuration);
	}

	/**
	 * Test main.
	 * 
	 * @throws TransformerConfigurationException
	 *             the transformer configuration exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public void testMain() throws TransformerConfigurationException,
			IOException, InterruptedException {
		try {
			String[] args = null;
			Report.main(args);
		} catch (ExitException e) {
			assertEquals("Exit status", 0, e.status);
		}
	}

	/**
	 * Sets the down.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void setDown() throws Exception {
		System.setSecurityManager(new ExitSecurityManager());
	}

}
