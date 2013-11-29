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
	ACCEPTED,
	UNAUTHORIZED,
	INTERNAL_SERVER_ERROR,
	BAD_REQUEST,
	NO_CONTENT;
	
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
		case UNAUTHORIZED:
			return ("401 Unauthorized");
		case INTERNAL_SERVER_ERROR:
			return ("500 Internal Server Error");
		case BAD_REQUEST:
			return ("400 Bad Request");
		case NO_CONTENT:
			return ("204 No Content");
		default:
			return ("");
		}
	}
}