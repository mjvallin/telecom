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
			request = extractContentFromGETRequest(requestLine);
			processGETRequest(request);
		} else if (Pattern.matches(postRequestPattern, requestLine)) {
			System.out.println("[INFO] The received request is a POST.");
			String requestBody = extractBodyFromPOSTRequest(bufferedInput);
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

		socketInputStream.close();
		socketOutputStream.close();
		this.socket.close();
	}

	/**
	 * Extracts the content of a GET request.
	 *
	 * @param headerLine The header line to be parsed.
	 * @return The extracted content from the GET request.
	 */
	private String extractContentFromGETRequest(String headerLine) {
		int startPosition = headerLine.indexOf(ASCII_SPACE_CHAR);
		System.out.println("Matcher ending position: " + startPosition);
		int endPosition = headerLine.indexOf(ASCII_SPACE_CHAR, startPosition+1);
		String request = headerLine.substring(startPosition, endPosition);
		System.out.println("[INFO] The extracted request is =" + request);
		return (request);
	}

	private void processGETRequest(String request) {
		if (Pattern.matches(usersRequestPattern, request)) {

		}
	}

	/**
	 * Extracts the content length from the MIME field Content-Length.
	 * 
	 * @param line The line containing the Content-Length header field (MIME formatted)
	 * @return The extracted content length and 0 if no numbers were found in the String.
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

	/**
	 * Extracts the body section of a POST request.
	 * 
	 * @param bufferedInput The BufferedReader of the received request from the client.
	 * @return The extracted body section of the POST request.
	 * @throws IOException 
	 */
	private String extractBodyFromPOSTRequest(BufferedReader bufferedInput) throws IOException {
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

		// Reads the required number of characters from the request.
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

		System.out.println("[INFO] Storing message: from=" + from + " to=" + to + " message=" + content);
		
		DBHandler.storeMessage(new Message(from, to, content));
	}
}