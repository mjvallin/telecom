package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * "GET allmessages=username HTTP/1.1" : Get all messages for a specific user.
 * "GET lastmessages=username&lastUid HTTP/1.1" : Gets the messages that were not sent yet to the front-end.
 * 
 * "POST login HTTP/1.1" : Logins a user to the server.
 * "Body contain the username and password"
 * 
 * "POST sendmessage HTTP/1.1" : Sends a message to a user.
 * "Body contains From, To and Message"
 *
 * @author michaelrabbat
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
	private String postLoginPattern = ".*[Ll][Oo][Gg][Ii].*";
	private String postStoreMessagePattern = ".*[Ss][Ee][Nn][Dd][Mm][Ee][Ss][Ss][Aa][Gg][Ee].*";
	private String contentLengthPattern = "[Cc][Oo][Nn][Tt][Ee][Nn][Tt][-][Ll][Ee][Nn][Gg][Tt][Hh].*";
	private String getRequestGetAllMessages = ".*[Aa][Ll][Ll][Mm][Ee][Ss][Ss][Aa][Gg][Ee][Ss].*";
	private String getRequestGetLastMessages = ".*[Ll][Aa][Ss][Tt][Mm][Ee][Ss][Ss][Aa][Gg][Ee][Ss].*";
	private String getBootstrap = ".*bootstrap.?css.*";
	private String getAngularResource = ".*angular-resource.?js.*";
	private String getAngular = ".*angular.?js.*";
	private String getChatImplementation = ".*chatimplementation.?js.*";
	private final int ASCII_SPACE_CHAR = 32;
	private final String AMP = "&";
	private final String CRLF = "\r\n";
	private final String SP = " ";
	
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
			
			boolean sendingFileToClient = false;
			String fileName = null;
			
			if (request.equals(" /")) {
				System.out.println("[INFO] Serving the main page to the client.");
				fileName = "index.html";
				sendingFileToClient = true;
			} else if (Pattern.matches(getBootstrap, requestLine)) {
				System.out.println("[INFO] Serving bootstrap.css to the client.");
				fileName = "css/bootstrap.css";
				sendingFileToClient = true;
			} else if (Pattern.matches(getAngular, requestLine)) {
				System.out.println("[INFO] Serving angular.js to the client.");
				fileName = "js/angular.js";
				sendingFileToClient = true;
			} else if (Pattern.matches(getChatImplementation, requestLine)) {
				System.out.println("[INFO] Serving chatimplementation.js to the client.");
				fileName = "js/chatimplementation.js";
				sendingFileToClient = true;
			} else if (Pattern.matches(getAngularResource, requestLine)) {
				System.out.println("[INFO] Serving angular-resource.js to the client.");
				fileName = "js/angular-resource.js";
				sendingFileToClient = true;
			}
			
			// Serve the page to the client.
			// The index and folders related to the client stuff need to be in the workspace folder.
			if (sendingFileToClient) {				
				System.out.println("[INFO] Opening the file=" + fileName);
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
					contentTypeLine = contentTypeLine + SP + RequestHandler.contentType(fileName) + CRLF;
				} else {
					statusLine = "HTTP/1.0" + SP + 404 + SP + "Not Found" + CRLF;
					contentTypeLine = contentTypeLine + SP + "text/html" + CRLF;
				}

				dataOutputStream.writeBytes(statusLine);
				dataOutputStream.writeBytes(contentTypeLine);
				dataOutputStream.writeBytes(CRLF);
				
				System.out.println("Header sent to the cliend:\n" + statusLine + contentTypeLine);
				
				// Send the body of the message (the web object)
				// You may use the sendBytes helper method provided
				if (fileExists) {
					RequestHandler.sendBytes(fis, socketOutputStream);
				} else {
					dataOutputStream.writeBytes(errorMessage);
				}
				
				socketInputStream.close();
				socketOutputStream.close();
				this.socket.close();
				return;
			}
			
			response = processGETRequest(request);
		} else if (Pattern.matches(postRequestPattern, requestLine)) {
			System.out.println("[INFO] The received request is a POST.");
			request = extractBodyFromPOSTRequest(bufferedInput);
			response = processPOSTRequest(requestLine, request);
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
	 * Private helper method to read the file and send it to the socket
	 * @param fis
	 * @param os
	 * @throws Exception
	 */
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception	{
		// Allocate a 1k buffer to hold bytes on their way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;
		
		// Copy requested file into the socket's output stream
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}
	
	/**
	 * Private method that returns the appropriate MIME-type string based on the
	 * suffix of the appended file
	 * @param fileName
	 * @return
	 */
	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		} else if (fileName.endsWith(".js")) {
			return ("text/javascript");
		} else if (fileName.endsWith(".css")) {
			return ("text/css");
		}
		
		System.out.println("[WARNING] Cannot find proper Content-Type for file name=" + fileName);
		
		return "application/octet-stream";
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
		if (Pattern.matches(getRequestGetAllMessages, request)) {
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
		try {
			int startPosition = request.indexOf("=") + 1;
			int endPosition = request.indexOf(AMP, startPosition);
			
			// Verifies that it has find the position of the two symbols.
			if (startPosition < 0 || endPosition < 0) {
				throw new RuntimeException("Either the start position of the end position is negative.");
			}
			
			String userName = request.substring(startPosition, endPosition);
			
			System.out.println("[INFO] The username is=" + userName);
			
			String lastUidReceived = request.substring(endPosition + 1, request.length());
			
			System.out.println("[INFO] The last received uid is=" + lastUidReceived);
			
			int lastUid = Integer.parseInt(lastUidReceived);
			
			// Fetches all the messages from the database.
			String lastMessagesInJSON = DBHandler.getLastMessages(userName, lastUid);
			if (lastMessagesInJSON != null) {
				return (new ResponseMessage(ResponseCode.OK, ContentType.APPLICATION_JSON, lastMessagesInJSON));
			} else {
				return (new ResponseMessage(ResponseCode.NO_CONTENT, ContentType.TEXT_PLAIN, "The user has no new messages."));
			}
		} catch(Exception e) {
			e.printStackTrace();
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
	}
	
	/**
	 * Authenticates a user with its username and password.
	 * 
	 * @param request The request to be processed (the GET and HTTP version must have been removed).
	 * @return The response ready to be sent back to the client.
	 */
	private ResponseMessage authenticateUser(String request) {
		System.out.println("[INFO] POST Body=" + request);
		
		try {
			// Get the username and password from the POST's body.
			String[] contents = request.split("\\&");
			String userName = "", userPassword = "";
			for(int i = 0; i < contents.length; ++i) {
				String[] tmp = contents[i].split("\\=");
	
				if(tmp[0].equals("username")) {
					userName = tmp[1];
				} else if(tmp[0].equals("password")) {
					userPassword = tmp[1];
				}
			}
			
			System.out.println("Username=" + userName);
			System.out.println("Password=" + userPassword);
		
			if (!DBHandler.authenticateUser(userName, userPassword)) {
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

	/**
	 * Process the POST request which is to store a message for a specific user on the database.
	 * 
	 * @param request The request to be processed.
	 * @return The response that will be sent back to the client.
	 */
	private ResponseMessage processStoreMessage(String request) {
		System.out.println("[INFO] Processing POST request.");
		
		Message messageToStore = createMessageToBeStored(request);
		// Handles if a problem occurred when creating the Message.
		if (messageToStore == null) {
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}
		
		try {			
			DBHandler.storeMessage(messageToStore, messageToStore.to);
			DBHandler.storeMessage(messageToStore, messageToStore.from);
		} catch(Exception e) {
			e.printStackTrace();
			return (ResponseMessage.responseMessageFactory(DefaultResponses.SERVER_ERROR_MESSAGE));
		}

		String response = DBHandler.getMessages(messageToStore.from);
		
		return (new ResponseMessage(ResponseCode.OK, ContentType.TEXT_PLAIN, response));
	}
	
	private ResponseMessage processPOSTRequest(String requestHeader, String requestBody) {		
		if (Pattern.matches(postLoginPattern, requestHeader)) {
			System.out.println("[INFO] Login user.");
			return (authenticateUser(requestBody));
		} else if (Pattern.matches(postStoreMessagePattern, requestHeader)) {
			System.out.println("[INFO] Storing message.");
			return (processStoreMessage(requestBody));
		} else {
			return (ResponseMessage.responseMessageFactory(DefaultResponses.BAD_REQUEST_FROM_CLIENT));
		}
	}

	/**
	 * Creates the Message object that will be stored in the database.
	 * 
	 * @param request The request containing the message to be stored.
	 * @return The message to be stored in the database and NULL if an error occurred while creating it.
	 */
	private Message createMessageToBeStored(String request) {
		try {
			String[] contents = request.split("\\&");
			String from = "", to = "", content = "";
			for(int i = 0; i < contents.length; ++i) {
				String[] tmp = contents[i].split("\\=");
	
				if(tmp[0].equals("from")) {
					from = tmp[1];
				} else if(tmp[0].equals("to")) {
					to = tmp[1];
				} else if(tmp[0].equals("message")) {
					content = tmp[1].replace('+', ' ');
				}
			}
			
			System.out.println("[INFO] Storing message: from=" + from + " to=" + to + " message=" + content);
			
			return (new Message(from, to, content));
		} catch(Exception e) {
			e.printStackTrace();
			return (null);
		}
	}
}