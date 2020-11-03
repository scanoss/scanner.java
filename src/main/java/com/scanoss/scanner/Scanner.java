/*
 * Copyright (C) 2018-2020 SCANOSS TECNOLOGIAS SL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */
package com.scanoss.scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scanner {

	private static final Logger log = LoggerFactory.getLogger(Scanner.class);
	private static final String BLACKLIST_OPT = "blacklist";
	private static final String IDENTIFY_OPT = "identify";
	private static final String IGNORE_OPT = "ignore";
	private static final String TMP_SCAN_WFP = "/tmp/scan.wfp";
	private final ScannerConf scannerConf;

	public Scanner(ScannerConf scannerConf) {
		super();
		this.scannerConf = scannerConf;

	}

	/**
	 * Scans a file
	 * 
	 * @param filename path to the file to be scanned
	 * @param scanType Type of scan, leave empty for default.
	 * @param sbomPath Optional path to a valid SBOM.json file
	 * @param format   Format of the scan. Leave empty for default value.
	 * @return an InputStream with the response body
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public InputStream scanFile(String filename, ScanType scanType, String sbomPath, ScanFormat format)
			throws NoSuchAlgorithmException, IOException, InterruptedException {
		String wfpString = Winnowing.wfpForFile(filename, filename);
		FileUtils.writeStringToFile(new File(TMP_SCAN_WFP), wfpString, StandardCharsets.UTF_8);
		ScanDetails details = new ScanDetails(TMP_SCAN_WFP, scanType, sbomPath, format);
		return doScan(details);

	}

	/**
	 * Scans a directory and saves the result to a file or prints to STDOUT. 
	 * @param dir Directory to scan
	 * @param scanType Type of scan, leave empty for default.
	 * @param sbomPath Optional path to a valid SBOM.json file
	 * @param format   Format of the scan. Leave empty for default value.
	 * @param outfile  Output file, empty for output to STDOUT
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void scanDirectory(String dir, ScanType scanType, String sbomPath, ScanFormat format, String outfile) throws IOException, InterruptedException{
		StringBuilder wfp = new StringBuilder();
		Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
				if (!Files.isDirectory(file) && !BlacklistRules.hasBlacklistedExt(file.toString())) {
					try {
						wfp.append(Winnowing.wfpForFile(file.toString(), file.toString()));
					} catch (NoSuchAlgorithmException | IOException e) {
						log.warn("Exception while creating wfp for file: {}",file.toString(),e);
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});
		FileUtils.writeStringToFile(new File(TMP_SCAN_WFP), wfp.toString(), StandardCharsets.UTF_8);
		ScanDetails details = new ScanDetails(TMP_SCAN_WFP, scanType, sbomPath, format);
		InputStream inputStream =  doScan(details);
		OutputStream out = StringUtils.isEmpty(outfile) ? System.out : new FileOutputStream(new File(outfile));

		IOUtils.copy(inputStream, out);
	}

	/**
	 * Scans a file and either saves it to a file or prints to STDOUT
	 * 
	 * @param filename path to the file to be scanned
	 * @param scanType Type of scan, leave empty for default.
	 * @param sbomPath Optional path to a valid SBOM.json file
	 * @param format   Format of the scan. Leave empty for default value.
	 * @param outfile  Output file, empty for output to STDOUT
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void scanFileAndSave(String filename, ScanType scanType, String sbomPath, ScanFormat format, String outfile)
			throws NoSuchAlgorithmException, IOException, InterruptedException {

		InputStream inputStream = scanFile(filename, scanType, sbomPath, format);
		OutputStream out = StringUtils.isEmpty(outfile) ? System.out : new FileOutputStream(new File(outfile));

		IOUtils.copy(inputStream, out);

	}

	public void scanFileAndSave(String filename, ScanDetails scanDetails, String outfile)
			throws NoSuchAlgorithmException, IOException, InterruptedException {
		scanFileAndSave(filename, scanDetails.getScanType(), scanDetails.getSbomPath(), scanDetails.getFormat(),
				outfile);

	}

	public static void main(String[] args) throws ParseException, NoSuchAlgorithmException, IOException, InterruptedException {
		Options options = new Options();
		options.addOption(new Option(IGNORE_OPT, true, "Scan and ignore components in SBOM file"));
		options.addOption(new Option(IDENTIFY_OPT, true, "Scan and identify components in SBOM file"));
		options.addOption(new Option(BLACKLIST_OPT, true, "Scan and blacklist components in SBOM file"));
		options.addOption(new Option("o", "output", true, "Save output to file"));
		options.addOption(
				new Option("f", "format", true, "Optional format for the scan result. One of: plain, spdx, cyclonedx"));
		options.addOption(new Option("h", false, "Shows usage"));
		Option input = new Option("i", "input", true, "The file to be scanned");
		input.setRequired(true);
		options.addOption(input);
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		if (ArrayUtils.isEmpty(args) || cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("scanner", options);
		}
		String filename = cmd.getOptionValue("i");

		String outfilename = "";
		String sbompath = "";
		ScanType scanType = null;
		if (cmd.hasOption("o")) {
			outfilename = cmd.getOptionValue("o");
		}
		ScanFormat format = ScanFormat.plain;
		if (cmd.hasOption("f")) {
			format = ScanFormat.valueOf(cmd.getOptionValue("f"));
		}
		if (cmd.hasOption(IGNORE_OPT)) {
			scanType = ScanType.ignore;
			sbompath = cmd.getOptionValue(IGNORE_OPT);
		} else if(cmd.hasOption(IDENTIFY_OPT)) {
			scanType = ScanType.identify;
			sbompath = cmd.getOptionValue(IDENTIFY_OPT);
		} else if(cmd.hasOption(BLACKLIST_OPT)) {
			scanType = ScanType.blacklist;
			sbompath = cmd.getOptionValue(BLACKLIST_OPT);
		}
		
		ScanDetails details = new ScanDetails(null, scanType,sbompath, format);
		String overrideAPIURL = System.getenv("SCANOSS_API_URL");
		String overrideAPIKEY = System.getenv("SCANOSS_API_KEY");
		ScannerConf conf = ScannerConf.defaultConf();
		if (StringUtils.isNotEmpty(overrideAPIURL)) {
			conf = new ScannerConf(overrideAPIURL, overrideAPIKEY);
		}
		Scanner scanner = new Scanner(conf);
		scanner.scanFileAndSave(filename, details, outfilename);

	}

	private InputStream doScan(ScanDetails details) throws IOException, InterruptedException {

		Map<Object, Object> data = scanFormData(details);

		String boundary = new BigInteger(256, new Random()).toString();

		HttpRequest request = HttpRequest.newBuilder(scannerConf.getApiURL()).POST(ofMimeMultipartData(data, boundary))
				.header("Content-Type", "multipart/form-data;boundary=" + boundary).header("Accept", "application/json")
				.header("X-Session", scannerConf.getApiKey()).build();

		HttpClient client = HttpClient.newBuilder().build();

		HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());

		return response.body();

	}

	private static Map<Object, Object> scanFormData(ScanDetails details) throws IOException {
		Map<Object, Object> data = new HashMap<>();
		if (details.getScanType() != null && StringUtils.isNotEmpty(details.getSbomPath())) {
			String sbomContents = FileUtils.readFileToString(new File(details.getSbomPath()), StandardCharsets.UTF_8);
			data.put("type", details.getScanType());
			data.put("assets", sbomContents);
		}
		if (details.getFormat() != null) {
			data.put("format", details.getFormat());
		}

		Path wfPath = Paths.get(details.getWfp());
		data.put("file", wfPath);
		return data;
	}

	private static BodyPublisher ofMimeMultipartData(Map<Object, Object> data, String boundary) throws IOException {
		var byteArrays = new ArrayList<byte[]>();
		byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
				.getBytes(StandardCharsets.UTF_8);
		for (Map.Entry<Object, Object> entry : data.entrySet()) {
			byteArrays.add(separator);

			if (entry.getValue() instanceof Path) {
				var path = (Path) entry.getValue();
				String mimeType = Files.probeContentType(path);
				byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName() + "\"\r\nContent-Type: "
						+ mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
				byteArrays.add(Files.readAllBytes(path));
				byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
			} else {
				byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
						.getBytes(StandardCharsets.UTF_8));
			}
		}
		byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
		return BodyPublishers.ofByteArrays(byteArrays);
	}

}
