package org.talend.designer.publish.core.models;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

public abstract class UploadableModel {

	protected String userName;
	protected String password;
	protected String repositoryURL;
	protected String groupId;
	protected String artifactId;
	protected String version;
	
	public UploadableModel(String groupId, String artifactId,String version, String repositoryURL, String userName, String password) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.repositoryURL = repositoryURL;
		this.userName = userName;
		this.password = password;
	}

	public abstract void upload() throws Exception;
	
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getArtifactId() {
		return artifactId;
	}
	
	protected void uploadMd5AndSha1(String sourceFilePath, String sourceFileName,String content) throws IOException {
		uploadMd5AndSha1(sourceFilePath, sourceFileName, new ChecksumComputor(content));
	}
	
	protected void uploadMd5AndSha1(String sourceFilePath, String sourceFileName,
			File file) throws IOException {
		uploadMd5AndSha1(sourceFilePath, sourceFileName, new ChecksumComputor(file));
	}
	
	private void uploadMd5AndSha1(String sourceFilePath, String sourceFileName,
			ChecksumComputor checksumComputor) throws IOException {
		// upload pom md5
		String md5CheckSum = checksumComputor.getMD5CheckSum();
		URL pomMd5Url = new URL(sourceFilePath + ".md5");
		uploadContent(pomMd5Url, md5CheckSum);
		// upload pom sha1
		String sha1CheckSum = checksumComputor.getSHA1CheckSum();
		URL pomSha1Url = new URL(sourceFilePath + ".sha1");
		uploadContent(pomSha1Url, sha1CheckSum);
	}

	protected void uploadContent(URL targetURL, String content)
			throws IOException {
		BufferedInputStream bis = new BufferedInputStream(
				new ByteArrayInputStream(content.getBytes()));
		uploadContent(targetURL, bis);
	}

	protected void uploadContent(URL targetURL, File content) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				content));
		uploadContent(targetURL, bis);
	}

	private void uploadContent(URL targetURL, InputStream bis)
			throws IOException {
		Authenticator.setDefault(new RepositoryAuthentication(userName,
				password));
		String host = targetURL.getHost();
		int port = targetURL.getPort();
		HttpURLConnection connection = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			connection = (HttpURLConnection) targetURL.openConnection();
			connection.setDoOutput(true);

			connection.setRequestMethod("PUT");
			connection.setDoInput(true);
			connection.addRequestProperty("Host", host + ":" + port);
			connection.addRequestProperty("Accept",
					"text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
			connection.addRequestProperty("Connection", "keep-alive");

			byte[] contents = new byte[1024];
			outputStream = connection.getOutputStream();
			int count = -1;
			while ((count = bis.read(contents)) != -1) {
				outputStream.write(contents, 0, count);
			}

			System.out.println("response....");
			inputStream = connection.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream));
			String s = bufferedReader.readLine();
			while (s != null) {
				System.out.println(s);
				s = bufferedReader.readLine();
			}
			System.out.println("end");
		} catch (IOException e) {
			throw e;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * return the content file if the file not exist, return null
	 * 
	 * @param targetURL
	 * @param userName
	 * @param password
	 * @return null if file not found
	 * @throws IOException
	 */
	protected String readContent(URL targetURL) throws IOException {
		try {
			Authenticator.setDefault(new RepositoryAuthentication(userName,
					password));
			HttpURLConnection connection = (HttpURLConnection) targetURL
					.openConnection();
			connection.setDoInput(true);

			connection.setRequestMethod("GET");
			InputStream inputStream = connection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();

			String s = bufferedReader.readLine();
			while (s != null) {
				sb.append(s);
				sb.append("\n");
				s = bufferedReader.readLine();
			}
			bufferedReader.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	protected String getArtifactDestination()
			throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append(repositoryURL);
		if (!repositoryURL.endsWith("/")) {
			sb.append("/");
		}
		String replacedGroupId = groupId.replaceAll("\\.", "/");
		sb.append(replacedGroupId);
		sb.append("/");
		sb.append(artifactId);
		sb.append("/");
		return sb.toString();
	}

	class RepositoryAuthentication extends Authenticator {
		private String userName;
		private String password;

		public RepositoryAuthentication(String userName, String password) {
			super();
			this.userName = userName;
			this.password = password;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(userName, password.toCharArray());
		}
	}

}
