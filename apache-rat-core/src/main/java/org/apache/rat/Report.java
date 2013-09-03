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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.rat.report.IReportable;
import org.apache.rat.report.RatReport;
import org.apache.rat.report.claim.ClaimStatistic;
import org.apache.rat.report.xml.XmlReportFactory;
import org.apache.rat.report.xml.writer.IXmlWriter;
import org.apache.rat.report.xml.writer.impl.base.XmlWriter;
import org.apache.rat.walker.ArchiveWalker;
import org.apache.rat.walker.DirectoryWalker;

/**
 * The Class Report.
 */
public final class Report {

	/** The Constant EXCLUDE_CLI. */
	private static final char EXCLUDE_CLI = 'e';

	/** The Constant EXCLUDE_FILE_CLI. */
	private static final char EXCLUDE_FILE_CLI = 'E';

	/** The Constant STYLESHEET_CLI. */
	private static final char STYLESHEET_CLI = 's';

	/** The base directory. */
	private final String baseDirectory;

	/** The input file filter. */
	private FilenameFilter inputFileFilter;


	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws TransformerConfigurationException
	 */
	public static void main(String args[]) throws IOException,
			TransformerConfigurationException, InterruptedException {
		final ReportConfiguration configuration = new ReportConfiguration();
		configuration.setHeaderMatcher(new Defaults().createDefaultMatcher());
		Options opts = buildOptions();

		PosixParser parser = new PosixParser();
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(opts, args);
		} catch (ParseException e) {
			System.err
					.println("Please use the \"--help\" option to see a list of valid commands and options");
			System.exit(1);
			return; // dummy return (won't be reached) to avoid Eclipse
					// complaint about possible NPE for "cl"
		}

		if (commandLine.hasOption('h')) {
			printUsage(opts);
		}

		args = commandLine.getArgs();
		if (args == null || args.length != 1) {
			printUsage(opts);
		} else {
			Report report = new Report(args[0]);

			if (commandLine.hasOption('a') || commandLine.hasOption('A')) {
				configuration.setAddingLicenses(true);
				configuration.setAddingLicensesForced(commandLine
						.hasOption('f'));
				configuration.setCopyrightMessage(commandLine
						.getOptionValue("c"));
			}

			if (commandLine.hasOption(EXCLUDE_CLI)) {
				String[] excludes = commandLine.getOptionValues(EXCLUDE_CLI);
				if (excludes != null) {
					final FilenameFilter filter = new NotFileFilter(
							new WildcardFileFilter(excludes));
					report.setInputFileFilter(filter);
				}
			} else if (commandLine.hasOption(EXCLUDE_FILE_CLI)) {
				String excludeFileName = commandLine
						.getOptionValue(EXCLUDE_FILE_CLI);
				if (excludeFileName != null) {
					List<String> excludes = FileUtils.readLines(new File(
							excludeFileName));
					final OrFileFilter orFilter = new OrFileFilter();
					for (String exclude : excludes) {
						orFilter.addFileFilter(new RegexFileFilter(exclude));
					}
					final FilenameFilter filter = new NotFileFilter(orFilter);
					report.setInputFileFilter(filter);
				}
			}
			if (commandLine.hasOption('x')) {
				report.report(System.out, configuration);
			} else {
				if (commandLine.hasOption(STYLESHEET_CLI)) {
					String[] style = commandLine
							.getOptionValues(STYLESHEET_CLI);
					if (style.length != 1) {
						System.err
								.println("please specify a single stylesheet");
						System.exit(1);
					}
					try {
						report(System.out, report.getDirectory(System.out),
								new FileInputStream(style[0]), configuration);
					} catch (FileNotFoundException fnfe) {
						System.err.println("stylesheet " + style[0]
								+ " doesn't exist");
						System.exit(1);
					}
				} else {
					report.styleReport(System.out, configuration);
				}
			}
		}
	}

	/**
	 * Builds the options.
	 * 
	 * @return the options
	 */
	private static Options buildOptions() {
		Options opts = new Options();

		Option help = new Option("h", "help", false,
				"Print help for the Rat command line interface and exit");
		opts.addOption(help);

		OptionGroup addLicenceGroup = new OptionGroup();
		String addLicenceDesc = "Add the default licence header to any file with an unknown licence that is not in the exclusion list. By default new files will be created with the licence header, to force the modification of existing files use the --force option.";

		Option addLicence = new Option("a", "addLicence", false, addLicenceDesc);
		addLicenceGroup.addOption(addLicence);
		Option addLicense = new Option("A", "addLicense", false, addLicenceDesc);
		addLicenceGroup.addOption(addLicense);
		opts.addOptionGroup(addLicenceGroup);

		Option write = new Option(
				"f",
				"force",
				false,
				"Forces any changes in files to be written directly to the source files (i.e. new files are not created)");
		opts.addOption(write);

		Option copyright = new Option(
				"c",
				"copyright",
				true,
				"The copyright message to use in the licence headers, usually in the form of \"Copyright 2008 Foo\"");
		opts.addOption(copyright);

		@SuppressWarnings("static-access")
		// ignore OptionBuilder design fault
		final Option exclude = OptionBuilder
				.withArgName("expression")
				.withLongOpt("exclude")
				.hasArgs()
				.withDescription(
						"Excludes files matching wildcard <expression>. "
								+ "Note that --dir is required when using this parameter. "
								+ "Allows multiple arguments.")
				.create(EXCLUDE_CLI);
		opts.addOption(exclude);

		@SuppressWarnings("static-access")
		// ignore OptionBuilder design fault
		final Option excludeFile = OptionBuilder
				.withArgName("fileName")
				.withLongOpt("exclude-file")
				.hasArgs()
				.withDescription(
						"Excludes files matching regular expression in <file> "
								+ "Note that --dir is required when using this parameter. ")
				.create(EXCLUDE_FILE_CLI);
		opts.addOption(excludeFile);

		Option dir = new Option("d", "dir", false,
				"Used to indicate source when using --exclude");
		opts.addOption(dir);

		OptionGroup outputType = new OptionGroup();

		Option xml = new Option("x", "xml", false,
				"Output the report in raw XML format.  Not compatible with -s");
		outputType.addOption(xml);

		Option xslt = new Option(String.valueOf(STYLESHEET_CLI), "stylesheet",
				true, "XSLT stylesheet to use when creating the"
						+ " report.  Not compatible with -x");
		outputType.addOption(xslt);
		opts.addOptionGroup(outputType);

		return opts;
	}

	/**
	 * Prints the usage.
	 * 
	 * @param opts
	 *            the opts
	 */
	private static void printUsage(final Options opts) {
		HelpFormatter helpFormatter = new HelpFormatter();
		String header = "Options";

		StringBuilder footer = new StringBuilder("\n");
		footer.append("NOTE:\n");
		footer.append("Rat is really little more than a grep ATM\n");
		footer.append("Rat is also rather memory hungry ATM\n");
		footer.append("Rat is very basic ATM\n");
		footer.append("Rat highlights possible issues\n");
		footer.append("Rat reports require intepretation\n");
		footer.append("Rat often requires some tuning before it runs well against a project\n");
		footer.append("Rat relies on heuristics: it may miss issues\n");

		helpFormatter.printHelp("java rat.report [options] [DIR|TARBALL]",
				header, opts,
				footer.toString(), false);
		System.exit(0);
	}

	/**
	 * Instantiates a new report.
	 * 
	 * @param baseDirectory
	 *            the base directory
	 */
	public Report(final String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	/**
	 * Gets the current filter used to select files.
	 * 
	 * @return current file filter, or null when no filter has been set
	 */
	public FilenameFilter getInputFileFilter() {
		return inputFileFilter;
	}

	/**
	 * Sets the current filter used to select files.
	 * 
	 * @param inputFileFilter
	 *            filter, or null when no filter has been set
	 */
	public void setInputFileFilter(final FilenameFilter inputFileFilter) {
		this.inputFileFilter = inputFileFilter;
	}

	/**
	 * Report.
	 * 
	 * @param out
	 *            the out
	 * @param configuration
	 *            the configuration
	 * @return the claim statistic
	 * @throws IOException
	 * @since Rat 0.8
	 */
	public ClaimStatistic report(final PrintStream out,
			final ReportConfiguration configuration) throws IOException {
		ClaimStatistic result = null;
		final IReportable base = getDirectory(out);
		if (base != null) {
			result = report(base, new OutputStreamWriter(out), configuration);
		}
		return result;
	}

	/**
	 * Gets the directory.
	 * 
	 * @param out
	 *            the out
	 * @return the directory
	 */
	private IReportable getDirectory(final PrintStream out) {
		File base = new File(baseDirectory);
		IReportable result = null;
		if (base.exists()) {
			if (base.isDirectory()) {
				result = new DirectoryWalker(base, inputFileFilter);
			} else {
				try {
					result = new ArchiveWalker(base, inputFileFilter);
				} catch (IOException ex) {
					out.print("ERROR: ");
					out.print(baseDirectory);
					out.print(" is not valid gzip data.\n");
				}
			}
		} else {
			out.print("ERROR: ");
			out.print(baseDirectory);
			out.print(" does not exist.\n");
		}
		return result;
	}

	/**
	 * Output a report in the default style and default licence header matcher.
	 * 
	 * @param out
	 *            - the output stream to recieve the styled report
	 * @param configuration
	 *            the configuration to use
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws TransformerConfigurationException
	 *             the exception
	 * @since Rat 0.8
	 */
	public void styleReport(final PrintStream out,
			final ReportConfiguration configuration)
			throws TransformerConfigurationException, IOException,
			InterruptedException
 {
		final IReportable base = getDirectory(out);
		if (base != null) {
			InputStream style = new Defaults().getDefaultStyleSheet();
			report(out, base, style, configuration);
		}
	}

	/**
	 * Output a report that is styled using a defined stylesheet.
	 * 
	 * @param out
	 *            the stream to write the report to
	 * @param base
	 *            the files or directories to report on
	 * @param style
	 *            an input stream representing the stylesheet to use for styling
	 *            the report
	 * @param pConfiguration
	 *            the configuration
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformerConfigurationException
	 *             the transformer configuration exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public static void report(final PrintStream out, final IReportable base,
			final InputStream style, final ReportConfiguration pConfiguration)
			throws IOException, TransformerConfigurationException,
			InterruptedException {
		report(new OutputStreamWriter(out), base, style, pConfiguration);
	}

	/**
	 * Output a report that is styled using a defined stylesheet.
	 * 
	 * @param out
	 *            the writer to write the report to
	 * @param base
	 *            the files or directories to report on
	 * @param style
	 *            an input stream representing the stylesheet to use for styling
	 *            the report
	 * @param pConfiguration
	 *            the configuration
	 * @return the claim statistic
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformerConfigurationException
	 *             the transformer configuration exception
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public static ClaimStatistic report(final Writer out,
			final IReportable base, final InputStream style,
			final ReportConfiguration pConfiguration)
			throws IOException, TransformerConfigurationException,
			FileNotFoundException, InterruptedException {
		PipedReader reader = new PipedReader();
		PipedWriter writer = new PipedWriter(reader);
		ReportTransformer transformer = new ReportTransformer(out, style,
				reader);
		Thread transformerThread = new Thread(transformer);
		transformerThread.start();
		final ClaimStatistic statistic = report(base, writer, pConfiguration);
		writer.flush();
		writer.close();
		transformerThread.join();
		return statistic;
	}

	/**
	 * Report.
	 * 
	 * @param container
	 *            the files or directories to report on
	 * @param out
	 *            the writer to write the report to
	 * @param pConfiguration
	 *            the configuration
	 * @return the claim statistic
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static ClaimStatistic report(final IReportable container,
			final Writer out, final ReportConfiguration pConfiguration)
			throws IOException {
		IXmlWriter writer = new XmlWriter(out);
		final ClaimStatistic statistic = new ClaimStatistic();
		RatReport report = new XmlReportFactory().createStandardReport(writer,
				statistic, pConfiguration);
		report.startReport();
		container.run(report);
		report.endReport();
		writer.closeDocument();
		return statistic;
	}
}
