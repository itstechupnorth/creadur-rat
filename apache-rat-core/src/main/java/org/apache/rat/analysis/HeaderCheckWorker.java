package org.apache.rat.analysis;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

/**
 * <p>
 * Reads from a stream to check license.
 * </p>
 * <p>
 * <strong>Note</strong> that this class is not thread safe.
 * </p>
 */
class HeaderCheckWorker {

	/** The Constant DEFAULT_NUMBER_OF_RETAINED_HEADER_LINES. */
	public static final int DEFAULT_NUMBER_OF_RETAINED_HEADER_LINES = 50;

	private static final int ZERO = 0;

	/** The number of retained header lines. */
	private final int numberOfRetainedHeaderLines;

	/** The reader. */
	private final BufferedReader reader;

	/** The matcher. */
	private final IHeaderMatcher matcher;

	/** The subject. */
	private final Document subject;

	/** The match. */
	private boolean match;

	/** The header lines to read. */
	private int headerLinesToRead;

	/** The finished. */
	private boolean finished;

	/**
	 * Instantiates a new header check worker.
	 * 
	 * @param reader
	 *            the reader
	 * @param numberOfRetainedHeaderLine
	 *            the number of retained header line
	 * @param matcher
	 *            the matcher
	 * @param name
	 *            the name
	 */
	public HeaderCheckWorker(final Reader reader,
			final int numberOfRetainedHeaderLine,
			final IHeaderMatcher matcher, final Document name) {
		this(new BufferedReader(reader), numberOfRetainedHeaderLine, matcher,
				name);
	}

	/**
	 * Convenience constructor wraps given <code>Reader</code> in a
	 * <code>BufferedReader</code>.
	 * 
	 * @param reader
	 *            a <code>Reader</code> for the content, not null
	 * @param matcher
	 *            the matcher
	 * @param name
	 *            the name of the checked content, possibly null
	 */
	public HeaderCheckWorker(final Reader reader, final IHeaderMatcher matcher,
			final Document name) {
		this(new BufferedReader(reader), matcher, name);
	}

	/**
	 * Instantiates a new header check worker.
	 * 
	 * @param reader
	 *            the reader
	 * @param matcher
	 *            the matcher
	 * @param name
	 *            the name
	 */
	public HeaderCheckWorker(final BufferedReader reader,
			final IHeaderMatcher matcher, final Document name) {
		this(reader, DEFAULT_NUMBER_OF_RETAINED_HEADER_LINES, matcher, name);
	}

	/**
	 * Instantiates a new header check worker.
	 * 
	 * @param reader
	 *            the reader
	 * @param numberOfRetainedHeaderLine
	 *            the number of retained header line
	 * @param matcher
	 *            the matcher
	 * @param name
	 *            the name
	 */
	public HeaderCheckWorker(final BufferedReader reader,
			final int numberOfRetainedHeaderLine, final IHeaderMatcher matcher,
			final Document name) {
		this.reader = reader;
		this.numberOfRetainedHeaderLines = numberOfRetainedHeaderLine;
		this.matcher = matcher;
		this.subject = name;
	}

	/**
	 * Checks if is finished.
	 * 
	 * @return true, if is finished
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Read.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void read() throws IOException {
		if (!finished) {
			final StringBuilder headers = new StringBuilder();
			headerLinesToRead = numberOfRetainedHeaderLines;
			boolean goOn = true;
			while (goOn) {
				goOn = readLine(headers);
			}
			if (!match) {
				final String notes = headers.toString();
				final MetaData metaData = subject.getMetaData();
				metaData.set(new MetaData.Datum(MetaData.RAT_URL_HEADER_SAMPLE,
						notes));
				metaData.set(new MetaData.Datum(
						MetaData.RAT_URL_HEADER_CATEGORY,
						MetaData.RAT_LICENSE_FAMILY_CATEGORY_VALUE_UNKNOWN));
				metaData.set(MetaData.RAT_LICENSE_FAMILY_NAME_DATUM_UNKNOWN);
			}
			reader.close();
			matcher.reset();
		}
		finished = true;
	}

	/**
	 * Read line.
	 * 
	 * @param headers
	 *            the headers
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean readLine(final StringBuilder headers) throws IOException {
		String line = reader.readLine();
		boolean result = line != null;
		if (result) {
			if (headerLinesToRead-- > ZERO) {
				headers.append(line);
				headers.append('\n');
			}
			match = matcher.match(subject, line);
			result ^= match;
		}
		return result;
	}
}
