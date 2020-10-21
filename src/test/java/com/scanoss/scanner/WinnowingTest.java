// SPDX-License-Identifier: GPL-2.0-or-later
/*
 * Copyright (C) 2018-2020 SCANOSS LTD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
		
		String actualWFP = Winnowing.wfpForFile("Winnowing.java", "src/main/java/com/scanoss/scanner/Winnowing.java").trim();
		
		String expectedWFP = FileUtils.readFileToString(new File("src/test/resources/Winnowing.java-scan.wfp"), Charset.defaultCharset());
		
		assertEquals(expectedWFP, actualWFP);
		
	}

}
