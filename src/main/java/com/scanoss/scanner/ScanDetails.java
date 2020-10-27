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

public class ScanDetails {
	private final String wfp;
	private final ScanType scanType;
	private final String sbomPath;
	private final ScanFormat format;
	public ScanDetails(String wfp, ScanType scanType, String sbomPath, ScanFormat format) {
		super();
		this.wfp = wfp;
		this.scanType = scanType;
		this.sbomPath = sbomPath;
		this.format = format;
	}
	
	public String getWfp() {
		return wfp;
	}
	
	public ScanType getScanType() {
		return scanType;
	}
	public String getSbomPath() {
		return sbomPath;
	}
	public ScanFormat getFormat() {
		return format;
	}
	
	

}
