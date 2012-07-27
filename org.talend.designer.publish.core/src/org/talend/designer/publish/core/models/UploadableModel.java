package org.talend.designer.publish.core.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
		uploadContent(targetURL, new StringEntity(content));
	}

	protected void uploadContent(URL targetURL, File content) throws IOException {
		uploadContent(targetURL, new FileEntity(content));
	}

	private void uploadContent(URL targetURL, HttpEntity entity)
			throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try{
			httpClient.getCredentialsProvider().setCredentials(
	                new AuthScope(targetURL.getHost(), targetURL.getPort()),
	                new UsernamePasswordCredentials(userName, password));
			
			HttpPut httpPut = new HttpPut(targetURL.toString());
			httpPut.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPut);
			StatusLine statusLine = response.getStatusLine();
			int responseCode = statusLine.getStatusCode();
			EntityUtils.consume(entity);
			if (responseCode > 399) {
				throw new IOException(responseCode+" "+statusLine.getReasonPhrase());
			}
		}catch (IOException e) {
			throw e;
		} finally {
			httpClient.getConnectionManager().shutdown();
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
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getCredentialsProvider().setCredentials(
	                new AuthScope(targetURL.getHost(), targetURL.getPort()),
	                new UsernamePasswordCredentials(userName, password));
			
			HttpGet httpGet = new HttpGet(targetURL.toString());
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				InputStream inputStream = entity.getContent();
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
				inputStream.close();
				EntityUtils.consume(entity);
				return sb.toString();
			}
			return null;
		} catch (FileNotFoundException e) {
			throw e;
		}finally{
			httpClient.getConnectionManager().shutdown();
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

}
