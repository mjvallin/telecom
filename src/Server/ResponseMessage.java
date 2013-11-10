package Server;

/**
 * Defines the response that will be sent back to the client.
 *
 * @author Ming-Ju Lin
 * @author Jean-Sebastien Dery
 * @author Fakrul Islam
 * @author Sen Li
 * @author Nicholas Destounis
 *
 */
public class ResponseMessage {
	
	private final String HTTP_VERSION = "HTTP/1.1";
	private final String SP = " ";
	private final String CRLF = "\r\n";
	private final String CONTENT_TYPE = "Content-Type:";
	private final String LINE_FEED = "\n";
	
	private ResponseCode statusCode;
	private ContentType contentType;
	private String content;

	public ResponseMessage(ResponseCode statusCode, ContentType contentType, String content) {
		this.statusCode = statusCode;
		this.contentType = contentType;
		this.content = content;
	}
	
	@Override
	public String toString() {
		StringBuilder response = new StringBuilder(100);
		response.append(HTTP_VERSION + SP + statusCode.toString() + CRLF);
		response.append(CONTENT_TYPE + SP + contentType.toString() + CRLF);
		response.append(content + CRLF + LINE_FEED);
		
		return (response.toString());
	}
}