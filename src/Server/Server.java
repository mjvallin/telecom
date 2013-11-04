package Server;

/**
 * 
 * 
 * This class implements a multi-threaded HTTP 1.0-compliant web server. The
 * root directory from which files are served is the same directory from which
 * this application is executed. When the server encounters an error, it sends a
 * response message with the appropriate HTML code so that the error information
 * is displayed.  
 * 
 * @author Ming-Ju Lin
 * @author Jean-Sebastien Dery
 * @author Fakrul Islam
 * @author Sen Li
 * @author Nicholas Destounis
 *
 */
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is the main class which runs he loop that listens for incoming requests
 * and spawns new threads to handle each request.
 * 
 * @author michaelrabbat
 * 
 */
public class Server {

	/**
	 * Defines the port on which the server will be listening to.
	 */
	private static final int port = 1234;

	public static void main(String argx[]) throws Exception {

		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(port);

		// Enter an infinite loop and process incoming connections
		// Use Ctrl-C to quit the application
		while (true) {
			// Listen for a new TCP connection request
			Socket connectionSocket = welcomeSocket.accept();

			// Construct an RequestHandler object to process the request message.
			RequestHandler currentHttpRequest = new RequestHandler(connectionSocket);

			// Creates a new thread to process the current request.
			Thread processingThread = new Thread(currentHttpRequest);

			// Starts the thread that will handle the requests.
			processingThread.start();
		}
	}
}