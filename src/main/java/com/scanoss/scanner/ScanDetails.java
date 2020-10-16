package com.scanoss.scanner;

public class ScanDetails {
	private final String wfp;
	private final String scanType;
	private final String sbomPath;
	private final String format;
	private final String outFile;
	public ScanDetails(String wfp, String scanType, String sbomPath, String format, String outfile) {
		super();
		this.wfp = wfp;
		this.scanType = scanType;
		this.sbomPath = sbomPath;
		this.format = format;
		this.outFile = outfile;
	}
	public String getOutFile() {
		return outFile;
	}
	public String getWfp() {
		return wfp;
	}
	
	public String getScanType() {
		return scanType;
	}
	public String getSbomPath() {
		return sbomPath;
	}
	public String getFormat() {
		return format;
	}
	
	

}
