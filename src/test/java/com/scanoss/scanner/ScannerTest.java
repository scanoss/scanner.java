package com.scanoss.scanner;

import org.junit.Test;

public class ScannerTest {
	
	@Test
	public void scanFile_try_c_plain() throws Exception{
		Scanner scanner = new Scanner(ScannerConf.defaultConf());
		scanner.scanFile("src/test/resources/try.c", "", "", "", "");
	}

}
