package eetac.upc.edu.dsa.raul.libros.api.links;

import java.net.URI;

public class URITemplateBuilder {
	private final static String COLON = ":";
	private final static String DOUBLE_FWD_SLASH = "//";
	private final static String QUESTION_MARK = "?";
	private final static String HASH = "#";

	public final static String buildTemplatedURI(URI uri) {
		StringBuilder sb = new StringBuilder();
		sb.append(uri.getScheme());
		sb.append(COLON);
		if (uri.getAuthority() != null) {
			sb.append(DOUBLE_FWD_SLASH);
			sb.append(uri.getAuthority());
		}
		if (uri.getPath() != null)
			sb.append(uri.getPath());
		if (uri.getQuery() != null) {
			sb.append(QUESTION_MARK);
			sb.append(uri.getQuery());
		}
		if (uri.getFragment() != null) {
			sb.append(HASH);
			sb.append(uri.getFragment());
		}

		return sb.toString();
	}
}
