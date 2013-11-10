package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * Handles all the request made by the client.
 *
 * @author Ming-Ju Lin
 * @author Jean-Sebastien Dery
 * @author Fakrul Islam
 * @author Sen Li
 * @author Nicholas Destounis
 *
 */
public class RequestHandler implements Runnable {
	final String CRLF = "\r\n";
	final String SP = " ";

	private Socket socket;
	private String getRequestPattern = "[Gg][Ee][Tt].*";
	private String postRequestPattern = "[Pp][Oo][Ss][Tt].*";
	private String optionRequestPattern = "[Oo][Pp][Tt][Ii][Oo][Nn][Ss].*";
	private String usersRequestPattern = "[Uu][Ss][Ee][Rr][Ss]";
	private String contentLengthPattern = "[Cc][Oo][Nn][Tt][Ee][Nn][Tt][-][Ll][Ee][Nn][Gg][Tt][Hh].*";
	private String singleDigitNumberPattern = "[0123456789]";
	private final int ASCII_SPACE_CHAR = 32;
	private final int GET_REQUEST_START_POS = 4;
	private final int POST_REQUEST_START_POS = 5;

	/**
	 * Constructor takes the socket for this request
	 */
	public RequestHandler(Socket socket) throws Exception {
		this.socket = socket;
	}

	/**
	 * Implement the run() method of the Runnable interface.
	 */
	@Override
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * This is where the action occurs
	 *
	 * @throws Exception
	 */
	private void processRequest() throws Exception {
		InputStreamReader socketInputStream = new InputStreamReader(
				this.socket.getInputStream());
		OutputStream socketOutputStream = this.socket.getOutputStream();

		// Set up input stream filters
		BufferedReader bufferedInput = new BufferedReader(socketInputStream);
		DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream);

		// Get the request line of the HTTP request message
		String requestLine = bufferedInput.readLine();
		String request;

		System.out.println("requestLine: " + requestLine);

		if (Pattern.matches(getRequestPattern, requestLine)) {
			System.out.println("[INFO] The received request is a GET.");
			request = extractRequestFromHeaderLine(GET_REQUEST_START_POS, requestLine);
			processGETRequest(request);
		} else if (Pattern.matches(postRequestPattern, requestLine)) {
			System.out.println("[INFO] The received request is a POST.");
			String requestBody = extractBodyFromRequest(bufferedInput);
			System.out.println("[INFO] The extracted body is [" + requestBody + "]");
			processPOSTRequest(requestBody);

			
			
			// TODO(mingju): Client doesn't pick up this for some reason
			//			     need to debug. My guess is AngularJS' problem again.
			dataOutputStream.writeBytes("HTTP/1.1 200 OK" + CRLF);
			dataOutputStream.writeBytes("Content-Type: application/json" + CRLF);
			JSONObject json = new JSONObject();
			json.put("name", "valentine");
			System.out.println(json);
			dataOutputStream.writeBytes(json + CRLF + "\n");


		} else {
			throw new Exception("Cannot determine if the request type.");
		}

		// STEP 2b: Close the input/output streams and socket before returning
		socketInputStream.close();
		socketOutputStream.close();
		this.socket.close();
	}

	/**
	 * Returns the request within the HTTP request header line.
	 *
	 * @param headerLine
	 *            The header line to be parsed.
	 * @return The extracted request.
	 */
	private String extractRequestFromHeaderLine(int requestStartPost, String headerLine) {
		int endPosition = headerLine.indexOf(ASCII_SPACE_CHAR, requestStartPost);
		String request = headerLine.substring(requestStartPost, endPosition);
		System.out.println("[INFO] The extracted request is =" + request);
		return (request);
	}

	private void processGETRequest(String request) {
		if (Pattern.matches(usersRequestPattern, request)) {

		}
	}

	/**
	 * Extracts the content length from defined in the Content-Length header field.
	 * 
	 * 
	 * @param line The line containing the Content-Length header field (MIME formatted)
	 * @return The extracted content length.
	 */
	private int extractContentLength(String line) {
		// Fetches the position at which the Content-Length is defined.
		Pattern singleDigit = Pattern.compile(singleDigitNumberPattern);
		Matcher matcher = singleDigit.matcher(line);
		
		if (matcher.find()) {
			int startingPosition = matcher.start();
			int endingPosition = matcher.end() + 1;
			String contentLength = line.substring(startingPosition, endingPosition);
			return (Integer.parseInt(contentLength));
		} else {
			return (0);
		}
	}

	private String extractBodyFromRequest(BufferedReader bufferedInput) throws IOException {
		System.out.println("[INFO] Processing POST request.");

		// Go through the request's header so we can reach the body.
		int contentLength = 0;
		String headerLine;
		do {
			headerLine = bufferedInput.readLine();
			System.out.println("[DEBUG] Traversing the request header=" + headerLine);

			// If the header is the Content-Length, fetch the number of characters stored.
			if (Pattern.matches(contentLengthPattern, headerLine)) {
				contentLength = extractContentLength(headerLine);
				System.out.println("[INFO] The number of characters to read is=" + contentLength);
			}
		} while (!headerLine.isEmpty());

		// Reads the required number of characters.
		char[] readBody = new char[contentLength];
		bufferedInput.read(readBody, 0, contentLength);

		return (new String(readBody));
	}

	
	private void processPOSTRequest(String request) {

		/*
		 * for now just storeMessage
		 * later on we will handle login request as well
		 */
		storeMessage(request);


	}

	// TODO(mingju): format this nicely.
	//				 parse from a JSON String
	private void storeMessage(String request) {
		String[] contents = request.split("\\&");
		String from = "", to = "", content = "";
		for(int i = 0; i < contents.length; ++i) {
			String[] tmp = contents[i].split("\\=");

			if(tmp[0].equals("from")) {
				from = tmp[1];
			} else if(tmp[0].equals("to")) {
				to = tmp[1];
			} else if(tmp[0].equals("message")) {
				//TODO(mingju): @Nadim, gimme the name of the textarea here
				// assume content is nicely formatted
				content = tmp[1].replace('+', ' ');
			}
		}

		DBHandler.storeMessage(new Message(from, to, content));
	}

	/**
	 * Private method that returns the appropriate MIME-type string based on the
	 * suffix of the appended file
	 *
	 * @param fileName
	 * @return
	 */
	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		} else if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
			return ("image/jpeg");
		} else if (fileName.endsWith(".gif")) {
			return ("image/gif");
		}
		// STEP 3b: Add code here to deal with GIFs and JPEGs
		return "application/octet-stream";
	}

	/**
	 * Private helper method to read the file and send it to the socket
	 *
	 * @param fis
	 * @param os
	 * @throws Exception
	 */
	private static void sendBytes(FileInputStream fis, OutputStream os)
			throws Exception {
		// Allocate a 1k buffer to hold bytes on their way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}
}