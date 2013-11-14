package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles all the request made by the client.
 * 
 * Under is the supported commands:
 * "GET allmessages=nick HTTP/1.1" : Get all messages for a specific user.
 * "GET authenticate=nick/password=userPassword HTTP/1.1" : Authenticate a user with its username and password.
 * "GET lastmessages=nick HTTP/1.1" : Gets the messages that were not sent yet to the front-end.
 *
 * @author Ming-Ju Lin
 * @author Jean-Sebastien Dery
 * @author Fakrul Islam
 * @author Sen Li
 * @author Nicholas Destounis
 *
 */
public class RequestHandler implements Runnable {
	private Socket socket;
	private String getRequestPattern = "[Gg][Ee][Tt].*";
	private String postRequestPattern = "[Pp][Oo][Ss][Tt].*";
	private String contentLengthPattern = "[Cc][Oo][Nn][Tt][Ee][Nn][Tt][-][Ll][Ee][Nn][Gg][Tt][Hh].*";
	private String getRequestAuthenticate = ".*[Aa][Uu][Tt][Hh][Ee][Nn][Tt][Ii][Cc][Aa][Tt][Ee].*[Pp][Aa][Ss][Ss][Ww][Oo][Rr][Dd].*";
	private String getRequestGetAllMessages = ".*[Aa][Ll][Ll][Mm][Ee][Ss][Ss][Aa][Gg][Ee][Ss].*";
	private String getRequestGetLastMessages = ".*[Ll][Aa][Ss][Tt][Mm][Ee][Ss][Ss][Aa][Gg][Ee][Ss].*";
	private final int ASCII_SPACE_CHAR = 32;
	
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
		InputStreamReader socketInputStream = new InputStreamReader(this.socket.getInputStream());
		OutputStream socketOutputStream = this.socket.getOutputStream();

		// Set up input stream filters
		BufferedReader bufferedInput = new BufferedReader(socketInputStream);
		DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream);

		// Get the request line of the HTTP request message
		String requestLine = bufferedInput.readLine();
		String request;
		ResponseMessage response = null;
		
		// FIXME: this is only for testing purposes.
		//requestLine = "GET allmessages=nick HTTP/1.1";
		//requestLine = "GET authenticate=nick/password=userPassword HTTP/1.1";
		//requestLine = "GET lastmessages=nick HTTP/1.1";
		
		System.out.println("[INFO] The request is: " + requestLine);
		
		if (Pattern.matches(getRequestPattern, requestLine)) {
			System.out.println("[INFO] The received request is a GET.");
			request = extractContentFromGETRequest(requestLine);
			response = processGETRequest(request);
		} else if (Pattern.matches(postRequestPattern, requestLine)) {
			System.out.println("[INFO] The received request is a POST.");
			String requestBody = extractBodyFromPOSTRequest(bufferedInput);
			System.out.println("[INFO] The extracted body is [" + requestBody + "]");
			response = processPOSTRequest(requestBody);
		} else {
			response = ResponseMessage.responseMessageFactory(DefaultResponses.BAD_REQUEST_FROM_CLIENT);
		}
		
		// Prints the response on the console.
		String responseToSend = response.toString();
		System.out.println("[INFO] Response sent to the client:\n" + responseToSend + "");
		// Sends the response back to the client.
		dataOutputStream.writeBytes(responseToSend);

		// Closes all the open streams.
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
		int endPosition = headerLine.indexOf(ASCII_SPACE_CHAR, startPosition+1);
		String request = headerLine.substring(startPosition, endPosition);
		System.out.println("[INFO] The extracted request is: " + request);
		return (request);
	}

	/**
	 * Processes the GET request depending on its content.
	 * 
	 * @param request The request to be processed (the GET and HTTP version must have been removed).
	 * @return The response ready to be sent back to the client.
	 */
	private ResponseMessage processGETRequest(String request) {
		if (Pattern.matches(getRequestAuthenticate, request)) {
			System.out.println("[INFO] Authenticate user.");
			return (authenticateUser(request));
		} else if (Pattern.matches(getRequestGetAllMessages, request)) {
			System.out.println("[INFO] Get all messages.");
			return (getAllMessages(request));
		} else if (Pattern.matches(getRequestGetLastMessages, request)) {
			System.out.println("[INFO] Get last messages.");
			return (getLastMessages(request));
		}
		
		System.out.println("[ERROR] No request type determined.");
		return (ResponseMessage.responseMessageFactory(DefaultResponses.BAD_REQUEST_FROM_CLIENT));
	}
	
	/**
	 * Returns the last messages that were fetched a specific user.
	 * 
	 * @param request The request to be processed (the GET and HTTP version must have been removed).
	 * @return The response ready to be sent back to the client.
	 */
	private ResponseMessage getLastMessages(String request) {
		int startPosition = request.indexOf("=") + 1;
		
		// Verifies that it has find the position of the symbol.
		if (startPosition < 0) {
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
		
		String userName = request.substring(startPosition, request.length());
		
		try {
			// Verifies if the user is already authenticated.
			if (!DBHandler.isUserAuthenticated(userName)) {
				System.out.println("[WARNING] User not authenticated.");
				return (new ResponseMessage(ResponseCode.UNAUTHORIZED, ContentType.TEXT_PLAIN, "User is not authenticated."));
			}
		} catch(Exception e) {
			e.printStackTrace();
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
		
		// Fetches all the messages from the database.
		String lastMessagesInJSON = DBHandler.getLastMessages(userName);
		if (lastMessagesInJSON != null) {
			return (new ResponseMessage(ResponseCode.OK, ContentType.APPLICATION_JSON, lastMessagesInJSON));
		} else {
			return (new ResponseMessage(ResponseCode.NO_CONTENT, ContentType.TEXT_PLAIN, "The user has no new messages."));
		}
		
	}
	
	/**
	 * Authenticates a user with its username and password.
	 * 
	 * @param request The request to be processed (the GET and HTTP version must have been removed).
	 * @return The response ready to be sent back to the client.
	 */
	private ResponseMessage authenticateUser(String request) {
		int startPosition = request.indexOf("=") + 1;
		int endPosition = request.indexOf("&", startPosition);
		
		// Verifies that it has find the position of the two symbols.
		if (startPosition < 0 || endPosition < 0) {
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
		
		String userName = request.substring(startPosition, endPosition);
		startPosition = request.indexOf("=", endPosition+1) + 1;
		// Verifies that it has find the position of the symbol.
		if (startPosition < 0) {
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
		String userPassword = request.substring(startPosition, request.length());
		
		System.out.println("[INFO] The user to authenticate: " + userName + " and its password: " + userPassword);
		
		try {
			if (!DBHandler.authenticateUser(userName, userPassword)) {
				System.out.println("[WARNING] User already authenticated.");
				return (new ResponseMessage(ResponseCode.UNAUTHORIZED, ContentType.TEXT_PLAIN, "The user was not successfuly authenticated."));
			}
		} catch(Exception e) {
			e.printStackTrace();
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
		
		return (new ResponseMessage(ResponseCode.OK, ContentType.TEXT_PLAIN, "The user was successfuly authenticated."));
	}
	
	/**
	 * Gets all the messages from a user. The request must be of the form "allmessages=username".
	 * 
	 * @param request The request to be processed. Needs to be of the form "allmessages=username".
	 * @return The response that will be sent back to the client.
	 */
	private ResponseMessage getAllMessages(String request) {
		int startPosition = request.indexOf("=") + 1;
		
		// Verifies that it has find the position of the symbol.
		if (startPosition < 0) {
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
		
		String userName = request.substring(startPosition, request.length());
		
		try {
			// Verifies if the user is already authenticated.
			if (!DBHandler.isUserAuthenticated(userName)) {
				System.out.println("[WARNING] User not authenticated.");
				return (new ResponseMessage(ResponseCode.UNAUTHORIZED, ContentType.TEXT_PLAIN, "User is not authenticated."));
			}
		} catch(Exception e) {
			e.printStackTrace();
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
		
		// Fetches all the messages from the database.
		String allMessagesInJSON = DBHandler.getMessages(userName);
		return (new ResponseMessage(ResponseCode.OK, ContentType.APPLICATION_JSON, allMessagesInJSON));
	}

	/**
	 * Extracts the content length from the MIME field Content-Length.
	 * 
	 * @param line The line containing the Content-Length header field (MIME formatted)
	 * @return The extracted content length and 0 if no numbers were found in the String.
	 */
	private int extractContentLength(String line) {
		// Fetches the position at which the Content-Length is defined.
		Pattern singleDigit = Pattern.compile("\\d");
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
		System.out.println("[INFO] Extracting body from POST request.");

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


	private ResponseMessage processPOSTRequest(String request) {
		System.out.println("[INFO] Processing POST request.");
		
		Message messageToStore = storeMessage(request);
		
		try {
			// Verifies if the user is already authenticated.
			if (!DBHandler.isUserAuthenticated(messageToStore.from)) {
				System.out.println("[WARNING] User not authenticated.");
				return (new ResponseMessage(ResponseCode.UNAUTHORIZED, ContentType.TEXT_PLAIN, "User is not authenticated."));
			}
			
			DBHandler.storeMessage(messageToStore);
		} catch(Exception e) {
			e.printStackTrace();
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}

		return (new ResponseMessage(ResponseCode.OK, ContentType.TEXT_PLAIN, "The message was stored succesfully."));
	}

	// TODO(mingju): format this nicely.
	//				 parse from a JSON String
	private Message storeMessage(String request) {
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
		
		return (new Message(from, to, content));
	}
}