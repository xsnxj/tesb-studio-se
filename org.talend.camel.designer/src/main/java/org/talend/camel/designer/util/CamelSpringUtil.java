package org.talend.camel.designer.util;

import java.io.InputStream;
import java.util.Scanner;

import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.core.model.utils.JavaResourcesHelper;

public class CamelSpringUtil {

	private static String TMP_SPRING_CONTENT = null;
	static {
		TMP_SPRING_CONTENT = getTmpSpringContent();
	}

	private CamelSpringUtil() {
	}

	public static String getDefaultContent(CamelProcessItem item) {
		try {
			String defaultContent = TMP_SPRING_CONTENT;
			String projectFolderName = JavaResourcesHelper
					.getProjectFolderName(item);
			String packageName = JavaResourcesHelper.getJobFolderName(item
					.getProperty().getLabel(), item.getProperty().getVersion());
			String className = item.getProperty().getLabel();
			defaultContent = defaultContent.replace("@ROUTEBUILDER_CLASS_ID@",
					className.toLowerCase()).replace("@ROUTEBUILDER_CLASS_NAME@",
					projectFolderName + "." + packageName + "." + className);
			return defaultContent;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String getTmpSpringContent() {
		try {
			InputStream is = CamelSpringUtil.class
					.getResourceAsStream("spring.xml");
			String defaultContent = new Scanner(is).useDelimiter("\\A").next();
			is.close();
			return defaultContent;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
