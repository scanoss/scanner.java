package com.scanoss.scanner;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class WinnowingTest {
	
	@Test
	public void wfpForFile_correct_for_try_c() throws IOException, NoSuchAlgorithmException {
		
		String actualWFP = Winnowing.wfpForFile("try.c", "src/test/resources/try.c");
		
		String expectedWFP = FileUtils.readFileToString(new File("src/test/resources/try-c.wfp"), Charset.defaultCharset());
		
		assertEquals(expectedWFP, actualWFP);
		
	}

}
