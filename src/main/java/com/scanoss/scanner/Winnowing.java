package com.scanoss.scanner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

/**
 * 
 * 
 * <p>
 * Winnowing Algorithm implementation for SCANOSS.
 * </p>
 * <p>
 * This module implements an adaptation of the original winnowing algorithm by S. Schleimer, D. S. Wilkerson and A. Aiken as described in their
 * seminal article which can be found here: https://theory.stanford.edu/~aiken/publications/papers/sigmod03.pdf
 * </p>
 * <p>
 * The winnowing algorithm is configured using two parameters, the gram size and the window size.
 * </p>
 * <p>
 * For SCANOSS and OSSKB the values need to be: - GRAM: 30 - WINDOW: 64
 * </p>
 * <p>
 * The result of performing the Winnowing algorithm is a string called WFP (Winnowing FingerPrint). A WFP contains optionally the name of the source
 * component and the results of the Winnowing algorithm for each file.
 * </p>
 * <p>
 * EXAMPLE output:
 * </p>
 * test-component.wfp
 * 
 * <pre>
 * component=f9fc398cec3f9dd52aa76ce5b13e5f75,test-component.zip file=cae3ae667a54d731ca934e2867b32aaa,948,test/test-file1.c<
 * 4=579be9fb
 * 5=9d9eefda,58533be6,6bb11697
 * 6=80188a22,f9bb9220
 * 10=750988e0,b6785a0d 
 * 12=600c7ec9
 * 13=595544cc 
 * 18=e3cb3b0f 
 * 19=e8f7133d
 * file=cae3ae667a54d731ca934e2867b32aaa,1843,test/test-file2.c 
 * 2=58fb3eed 
 * 3=f5f7f458 
 * 4=aba6add1 
 * 8=53762a72,0d274008,6be2454a 
 * 10=239c7dfa 
 * 12=0b2188c9
 * 15=bd9c4b10,d5c8f9fb 
 * 16=eb7309dd,63aebec5 
 * 19=316e10eb [...]
 * </pre>
 * 
 * Where component is the MD5 hash and path of the component (It could be a path to a compressed file or a URL). file is the MD5 hash, file length and
 * file path being fingerprinted, followed by a list of WFP fingerprints with their corresponding line numbers.
 *
 */
public class Winnowing {

	private static final int GRAM = 30;
	private static final int WINDOW = 64;

	private static final long MAX_CRC32 = 4294967296L;

	private static byte[] toLittleEndian(long number) {
		byte[] b = new byte[4];
		b[0] = (byte) (number & 0xFF);
		b[1] = (byte) ((number >> 8) & 0xFF);
		b[2] = (byte) ((number >> 16) & 0xFF);
		b[3] = (byte) ((number >> 24) & 0xFF);
		return b;
	}

	private static char normalize(char c) {
		if (c < '0' || c > 'z') {
			return 0;
		} else if (c <= '9' || c >= 'a') {
			return c;
		} else if (c >= 'A' && c <= 'Z') {
			return (char) (c + 32);
		} else {
			return 0;
		}
	}

	private static boolean isBinaryFile(File f) throws IOException {
		String type = Files.probeContentType(f.toPath());
		if (type == null) {
			// type couldn't be determined, assume binary
			return true;
		} else {
			return !type.startsWith("text");
		}
	}

	private static String zeroPaddedString(String hexString) {
		while (hexString.length() != 8) {
			hexString = "0" + hexString;
		}
		return hexString;
	}

	private static long crc32c(String s) {
		Checksum checksum = new CRC32C();
		checksum.update(s.getBytes());
		return checksum.getValue();
	}
	
	private static String crc32cHex(long l) {
		Checksum checksum = new CRC32C();
		checksum.update(toLittleEndian(l));
		return zeroPaddedString(Long.toHexString(checksum.getValue()));
	}


	private static long min(List<Long> l) {
		List<Long> sortedList = new ArrayList<>(l);
		Collections.sort(sortedList);
		return sortedList.get(0);
	}

	/**
	 * Calculates the WFP
	 * 
	 * @param filename
	 * @param filepath
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String wfpForFile(String filename, String filepath) throws NoSuchAlgorithmException, IOException {
		File file = new File(filepath);
		String fileContents = FileUtils.readFileToString(file, Charset.defaultCharset());
		String fileMD5 = DigestUtils.md5Hex(fileContents);

		StringBuilder wfpBuilder = new StringBuilder();
		wfpBuilder.append(String.format("file=%s,%d,%s\n", fileMD5, fileContents.length(), filename));
		// Skip snippet analysis for binaries or non source code files.
		if (isBinaryFile(file) || BlacklistRules.isMarkupOrJSON(fileContents)) {
			return wfpBuilder.toString();
		}
		String gram = "";
		List<Long> window = new ArrayList<>();
		char normalized = 0;
		long minHash = MAX_CRC32;
		long lastHash = MAX_CRC32;
		int lastLine = 0;
		int line = 1;
		String output = "";

		for (char c : fileContents.toCharArray()) {
			if (c == '\n') {
				line++;
				normalized = 0;
			} else {
				normalized = normalize(c);
			}

			if (normalized > 0) {
				gram += normalized;

				if (gram.length() >= GRAM) {
					Long gramCRC32 = crc32c(gram);
					window.add(gramCRC32);

					if (window.size() >= WINDOW) {
						minHash = min(window);
						if (minHash != lastHash) {
							String minHashHex = crc32cHex(minHash);
							if (lastLine != line) {
								if (output.length() > 0) {
									wfpBuilder.append(output + "\n");
								} 
								output = String.format("%d=%s", line, minHashHex);
								
							} else {
								output += "," + minHashHex;
							}

							lastLine = line;
							lastHash = minHash;
						}
						// Shift window
						window.remove(0);
					}
					// Shift gram
					gram = gram.substring(1);
				}

			}

		}
		if (output.length() > 0)

		{
			wfpBuilder.append(output + "\n");
		}

		return wfpBuilder.toString();

	}

}
