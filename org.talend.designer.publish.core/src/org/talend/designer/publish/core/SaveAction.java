package org.talend.designer.publish.core;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.designer.publish.core.models.PomModel;

public class SaveAction {

	public static boolean savePom(String pomFilePath, String groupId,
			String artifactId, String version) throws IOException {
		return savePom(pomFilePath, groupId, artifactId, version, "jar");
	}

	public static boolean savePom(String pomFilePath, String groupId,
			String artifactId, String version, String packaging)
			throws IOException {
		PomModel pm = new PomModel(groupId, artifactId, version, packaging,
				null, null, null);
		return savePom(pomFilePath, pm);
	}

	public static boolean savePom(String pomFilePath, PomModel pm)
			throws IOException {
		if (pm == null || pomFilePath == null) {
			return false;
		}
		return save(pomFilePath, pm.getPomContent());
	}

	public static boolean saveFeature(String featureFilePath, String groupId,
			String featureNamePrefix, String version) throws IOException {
		FeaturesModel fm = new FeaturesModel(groupId, featureNamePrefix,
				version, null, null, null);
		return saveFeature(featureFilePath, fm);
	}

	public static boolean saveFeature(String featureFilePath, FeaturesModel fm)
			throws IOException {
		if (fm == null || featureFilePath == null) {
			return false;
		}
		return save(featureFilePath, fm.getFeatureContent());
	}

	private static boolean save(String filePath, String content)
			throws IOException {
		if (content == null || filePath == null) {
			return false;
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filePath)));
		bw.write(content);
		bw.flush();
		bw.close();
		return true;
	}

	public static void main(String[] args) throws IOException {
		// savePom("C:/data/pom/pom1.xml", "org.talend.esb", "TestPom", "1.0");
		// savePom("C:/data/pom/pom2.xml", "org.talend.esb", "TestPom", "1.0",
		// "bundle");
		// PomModel pm = new PomModel("org.talend.esb", "TestPom", "1.0",
		// "bundle", null, null, null);
		// savePom("C:/data/pom/pom3.xml", pm);

		// saveFeature("C:/data/feature/f1.xml", "org.talend.esb",
		// "TestFeature",
		// "1.0");
		// FeaturesModel fm = new FeaturesModel("org.talend.esb", "TestFeature",
		// "1.0", null, null, null);
		// saveFeature("C:/data/feature/f2.xml", fm);
		//
		// BundleModel b1 = new BundleModel(new File("userRoutines.jar"),
		// "org.talend.liugang", "userRoutines1", "1.0",
		// "http://localhost:8080/archiva/repository/snapshots/", "gliu",
		// "liugang123");
		// BundleModel b2 = new BundleModel(new File("userRoutines.jar"),
		// "trg.talend.liugang", "userRoutines2", "1.0",
		// "http://localhost:8080/archiva/repository/snapshots/", "gliu",
		// "liugang123");
		// fm.addSubBundle(b1);
		// fm.addSubBundle(b2);
		// saveFeature("C:/data/feature/f3.xml", fm);
	}

}
