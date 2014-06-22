package org.talend.designer.publish.core.models;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.archiva.checksum.Checksum;
import org.apache.archiva.checksum.ChecksumAlgorithm;

public class ChecksumComputor {

	private String md5;
	private String sha1;

	public ChecksumComputor(String content) throws IOException {
		super();
		md5 = computeMD5Checksum(content);
		sha1 = computeSHA1Checksum(content);
	}

	public ChecksumComputor(File file) throws IOException {
		super();
		md5 = computeMD5Checksum(file);
		sha1 = computeSHA1Checksum(file);
	}

	private String computeMD5Checksum(String content) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(
				new ByteArrayInputStream(content.getBytes()));
		return computeMD5Checksum(bis);
	}

	private String computeSHA1Checksum(String content) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(
				new ByteArrayInputStream(content.getBytes()));
		return computeSHA1Checksum(bis);
	}

	private String computeMD5Checksum(File content) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				content));
		return computeMD5Checksum(bis);
	}

	private String computeSHA1Checksum(File content) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				content));
		return computeSHA1Checksum(bis);
	}

	private String computeMD5Checksum(InputStream is) throws IOException {
		Checksum checksum = new Checksum(ChecksumAlgorithm.MD5);
		checksum.update(is);
		String checksumString = checksum.getChecksum();
		is.close();
		return checksumString;
	}

	private String computeSHA1Checksum(InputStream is) throws IOException {
		Checksum checksum = new Checksum(ChecksumAlgorithm.SHA1);
		checksum.update(is);
		String checksumString = checksum.getChecksum();
		is.close();
		return checksumString;
	}

	public String getMD5CheckSum() {
		return md5;
	}

	public String getSHA1CheckSum() {
		return sha1;
	}

}
