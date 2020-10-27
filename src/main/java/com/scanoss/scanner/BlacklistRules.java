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

public class BlacklistRules {

	public static final String[] BLACKLIST_EXTS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "ac",
			"am", "bmp", "build", "cfg", "chm", "changelog", "class", "cmake", "conf", "config", "contributors",
			"copying", "csproj", "css", "csv", "cvsignore", "dat", "data", "dtd", "dts", "dtsi", "eps", "geojson",
			"gif", "gitignore", "glif", "gmo", "guess", "hex", "html", "htm", "ico", "in", "inc", "info", "ini",
			"ipynb", "jpg", "jpeg", "json", "license", "log", "m4", "map", "markdown", "md", "md5", "mk", "makefile",
			"meta", "mxml", "notice", "out", "pdf", "pem", "phtml", "png", "po", "prefs", "properties", "readme",
			"result", "rst", "scss", "sha", "sha1", "sha2", "sha256", "sln", "spec", "sub", "svg", "svn-base", "tab",
			"template", "test", "tex", "todo", "txt", "utf-8", "version", "vim", "wav", "xht", "xhtml", "xml", "xpm",
			"xsd", "xul", "yaml", "yml" };

	public static boolean isMarkupOrJSON(String src) {
		return (src.charAt(0) == '{' || src.startsWith("<?xml") || src.startsWith("<html") || src.startsWith("<AC3D"));
	}

}
