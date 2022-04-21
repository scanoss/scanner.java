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

import org.junit.Test;

public class ScannerTest {
	
	@Test
	public void scanFile_try_c_plain() throws Exception{
		Scanner scanner = new Scanner(ScannerConf.defaultConf());
		scanner.scanFileAndSave("src/main/java/com/scanoss/scanner/Winnowing.java", null, "", null, "");
	}

	@Test
	public void scanFile_try_empty() throws Exception{
		Scanner scanner = new Scanner(ScannerConf.defaultConf());
		scanner.scanFileAndSave("src/test/resources/empty.java", null, "", null, "");
	}

}
