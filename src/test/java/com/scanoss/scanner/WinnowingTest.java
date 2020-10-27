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
