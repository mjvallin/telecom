package Server;

/**
 * All supported response Status-Code from HTTP/1.1.
 *
 * @author Ming-Ju Lin
 * @author Jean-Sebastien Dery
 * @author Fakrul Islam
 * @author Sen Li
 * @author Nicholas Destounis
 *
 */
public enum ResponseCode {
	OK,
	NOT_FOUND,
	CREATED,
	ACCEPTED;
	
	@Override
	public String toString() {
		switch (this) {
		case OK:
			return ("200 OK");
		case NOT_FOUND:
			return ("404 Not Found");
		case CREATED:
			return ("201 Created");
		case ACCEPTED:
			return ("202 Accepted");
		default:
			return ("");
		}
	}
}