package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

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
			System.out.println("[INFO] The extracted body is=" + requestBody);
			processPOSTRequest(requestBody);
		} else if(Pattern.matches(optionRequestPattern, requestLine)) {
			// handle preflight request
			// TODO(mingju): if we get a preflight request,
			// 		         we should reply back so POST request will follow (?)
			// 			     or better, somebody find a way to avoid preflight request

//			dataOutputStream.writeBytes("HTTP/1.1 200 OK" + CRLF + "\n");
//			dataOutputStream.writeBytes("Access-Control-Allow-Origin: http://localhost/" + CRLF);
//			dataOutputStream.writeBytes("Access-Control-Allow-Methods: GET, POST, PUT" + CRLF);
//			dataOutputStream.writeBytes("Access-Control-Request-Headers: " +
//									"accept, x-requested-with, content-type" + CRLF);
//			dataOutputStream.writeBytes("Content-Type: " +
//									"application/x-www-form-urlencoded" + CRLF + "\n");


		} else {
			throw new Exception("Cannot determine if the request is GET or POST.");
		}

		/*// Get and display the header lines
		String headerLine;
		do {
			headerLine = bufferedInput.readLine();
			System.out.println("Additional header line: " + headerLine);
		} while (headerLine != null);
		System.out.println("No more header lines to display...");

		// STEP 3a: Prepare and Send the HTTP Response message
		// Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which we'll assume is "GET"
		String fileName = tokens.nextToken();

		// Prepend a "." to the file name so that the file request is in the
		// current directory
		fileName = "." + fileName;

		// Open the requested file
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		// Construct the response message header
		String statusLine = null;
		String contentTypeLine = "Content-Type:";
		String errorMessage = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>404 Not Found</BODY></HTML>";

		// Fill in the values of statusLine and contentTypeLine based on whether
		// or not the requested file was found
		if (fileExists) {
			statusLine = "HTTP/1.0" + SP + 200 + SP + "OK" + CRLF;
			contentTypeLine = contentTypeLine + SP
					+ RequestHandler.contentType(fileName) + CRLF;
		} else {
			statusLine = "HTTP/1.0" + SP + 404 + SP + "Not Found" + CRLF;
			contentTypeLine = contentTypeLine + SP + "text/html" + CRLF;
		}

		// Send a HTTP response header containing the status line and
		// content-type line. Don't forget to include a blank line after the
		// content-type to signal the end of the header.
		dataOutputStream.writeBytes(statusLine);
		dataOutputStream.writeBytes(contentTypeLine);
		dataOutputStream.writeBytes(CRLF);

		System.out.println("Header sent to the cliend:\n" + statusLine
				+ contentTypeLine);

		// Send the body of the message (the web object)
		// You may use the sendBytes helper method provided
		if (fileExists) {
			RequestHandler.sendBytes(fis, socketOutputStream);
		} else {
			dataOutputStream.writeBytes(errorMessage);
		}*/

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


	private int extractContentLength(String line) {
		String numberOfCharacters = line.substring(16, line.length());
		return (Integer.parseInt(numberOfCharacters));
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

	/*
	 * FIXME: this method is not useful util we manage to get a POST
	 * 		  request instead of an OPTIONS
	 * TODO(mingju): a lot of assumption here:
	 * assume request is in the form of:
	 * and the message is always nice. (for now at least)
	 */
	private void processPOSTRequest(String request) {

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