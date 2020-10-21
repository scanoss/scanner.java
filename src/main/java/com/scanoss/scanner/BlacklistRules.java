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
