package org.talend.camel.designer.util;

import java.io.InputStream;
import java.util.Scanner;

import org.talend.camel.core.model.camelProperties.CamelProcessItem;

public class CamelSpringUtil {

	private static String TMP_SPRING_CONTENT = null;

	private CamelSpringUtil() {
	}

	public static String getDefaultContent(CamelProcessItem item) {
		if (TMP_SPRING_CONTENT == null) {
			TMP_SPRING_CONTENT = getTmpSpringContent();
		}

		return TMP_SPRING_CONTENT.replace("${RouteItemName}", item
				.getProperty().getLabel());
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
