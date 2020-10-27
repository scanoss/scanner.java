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

import java.net.URI;

public class ScannerConf {
	
	public static String OSSKB_URL = "https://osskb.org/api/scan/direct"; 
	
	private final URI apiURI;
	private final String apiKey;
	public ScannerConf(String apiURL, String apiKey) {
		super();
		this.apiURI = URI.create(apiURL);
		this.apiKey = apiKey;
	}
	public URI getApiURL() {
		return apiURI;
	}
	public String getApiKey() {
		return apiKey;
	}
	
	
	public static ScannerConf defaultConf() {
		return new ScannerConf(OSSKB_URL, "");
	}
	
	

}
