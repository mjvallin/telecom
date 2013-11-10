package Server;

/**
 * All supported Content-Types from MIME.
 *
 * @author Ming-Ju Lin
 * @author Jean-Sebastien Dery
 * @author Fakrul Islam
 * @author Sen Li
 * @author Nicholas Destounis
 *
 */
public enum ContentType {
	APPLICATION_JSON;
	
	@Override
	public String toString() {
		switch (this) {
		case APPLICATION_JSON:
			return ("application/json");
		default:
			return ("");
		}
	}
}


