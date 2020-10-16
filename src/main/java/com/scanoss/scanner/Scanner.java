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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class Scanner {

	private static final String TMP_SCAN_WFP = "/tmp/scan.wfp";
	private final ScannerConf scannerConf;
	

	public Scanner(ScannerConf scannerConf) {
		super();
		this.scannerConf = scannerConf;
		
	}
	
	public void scanFile(String filename, String scanType, String sbomPath, String format, String outfile) throws NoSuchAlgorithmException, IOException, InterruptedException {
		String wfpString = Winnowing.wfpForFile(filename, filename);
		FileUtils.writeStringToFile(new File(TMP_SCAN_WFP), wfpString, StandardCharsets.UTF_8);
		ScanDetails details = new ScanDetails(TMP_SCAN_WFP, scanType, sbomPath, format, outfile);
		doScan(details);
		
	}

	private void doScan(ScanDetails details) throws IOException, InterruptedException {

		Map<Object, Object> data = scanFormData(details);

		String boundary = new BigInteger(256, new Random()).toString();

		HttpRequest request = HttpRequest.newBuilder(scannerConf.getApiURL()).POST(ofMimeMultipartData(data, boundary))
				.header("Content-Type", "multipart/form-data;boundary=" + boundary).header("Accept", "application/json")
				.header("X-Session", scannerConf.getApiKey()).build();

		HttpClient client = HttpClient.newBuilder().build();

		HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
		
		OutputStream out = StringUtils.isEmpty(details.getOutFile()) ? System.out : new FileOutputStream(new File(details.getOutFile()));
		
		IOUtils.copy(response.body(), out);

	}

	private static Map<Object, Object> scanFormData(ScanDetails details) throws IOException {
		Map<Object, Object> data = new HashMap<>();
		if (StringUtils.isNotEmpty(details.getScanType())) {
			String sbomContents = FileUtils.readFileToString(new File(details.getSbomPath()), StandardCharsets.UTF_8);
			data.put("type", details.getScanType());
			data.put("assets", sbomContents);
		}
		if (StringUtils.isNotEmpty(details.getFormat())) {
			data.put("format", details.getFormat());
		}

		Path wfPath = Paths.get(details.getWfp());
		data.put("file", wfPath);
		return data;
	}

	public static BodyPublisher ofMimeMultipartData(Map<Object, Object> data, String boundary) throws IOException {
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
