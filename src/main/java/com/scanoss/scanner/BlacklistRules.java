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
