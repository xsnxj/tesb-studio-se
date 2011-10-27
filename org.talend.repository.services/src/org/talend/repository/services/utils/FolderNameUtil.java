package org.talend.repository.services.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class FolderNameUtil {

	public static String replaceAllLimited(String input) {
		if (input == null) {
			return input;
		}
		String[] split = input.split("/");
		if (split.length <= 1) {
			// return input;
			split = new String[] { input };
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
			String replaceAll = split[i].replaceAll("\\p{Punct}", "RP");
			sb.append(replaceAll);
			if (i < split.length - 1) {
				sb.append("/");
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) throws MalformedURLException,
			URISyntaxException {
		// String replaceAllLimited =
		// replaceAllLimited("http://www.example.com/Customer_0.1/customer");
		// System.out.println(replaceAllLimited);
		String[] s = new String[] { "sdf://ftp.is.co.za/rfc/rfc1808.txt",

		"http://www.ietf.org/rfc/rfc2396.txt",

		"ldap://[2001:db8::7]/c=GB?objectClass?one",

		"mailto:John.Doe@example.com",

		"news:comp.infosystems.www.servers.unix",

		"tel:+1-816-555-1212",

		"telnet://192.0.2.16:80/",

		"urn:oasis:names:specification:docbook:dtd:xml:4.1.2"

		};
		for (String t : s) {
			URI uri = new URI(t);
			String scheme = uri.getScheme();
			if (scheme != null) {
				t = t.substring(scheme.length() + 1);
			}
			System.out.println(replaceAllLimited(t));
		}
	}

}
