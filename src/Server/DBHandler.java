package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHandler {
	public static final String DB_FOLDER = "db/";
	public static final String DB_FILE_TYPE = ".txt";

	public static final String DB_AUTH_FILE = "logins";

	/**
	 * Stores a message for a specific user in the database.
	 * 
	 * @param m
	 *            The message to be stored.
	 */
	public static void storeMessage(Message m) {
		String filename = m.to + DB_FILE_TYPE;
		String path = DB_FOLDER + filename;

		File f = new File(path);

		try {
			FileWriter fr = new FileWriter(f, true);
			PrintWriter pr = new PrintWriter(fr);

			// write the json string of the message to the file
			pr.println(m.getJSONString());

			fr.close();
			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns all the messages for a specific user in a JSON formatted String.
	 * 
	 * @param username
	 *            The user that we will fetch all the messages.
	 * @return All the JSON formatted messages.
	 */
	public static String getMessages(String username) {
		String filename = username + DB_FILE_TYPE;
		String path = DB_FOLDER + filename;

		JSONArray messagesInJSON = new JSONArray();

		File f = new File(path);
		try {
			FileReader fReader = new FileReader(f);
			BufferedReader bReader = new BufferedReader(fReader);

			String s = bReader.readLine();
			while (s != null) {
				messagesInJSON.put(new JSONObject(s));
				s = bReader.readLine();
			}

			fReader.close();
			bReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return messagesInJSON.toString();
	}

	/**
	 * Will verify if the user has been authenticated on the server.
	 * 
	 * @param username
	 *            The user to verify.
	 * @return True if the user is already authenticated, and false otherwise.
	 * @throws Exception
	 */
	public static boolean isUserAuthenticated(String username) throws Exception {
		// TODO: To be filled.
		return (true);
	}

	/**
	 * Gets all the fetched messages from a specific user. Each messages are
	 * assigned a UID (Unique IDentifier that is incremented from 1 to
	 * "infinity".
	 * 
	 * @param username
	 *            The user to which we need to verify if there are any new
	 *            messages.
	 * @return The JSON formatted list of new messages and NULL if there is
	 *         nothing that the User has not already fetched.
	 */
	public static String getLastMessages(String username) {
		// TODO: To be filled.
		return (null);
	}

	/**
	 * Authenticates a user based on its username and password. It will save the
	 * state of the authentication for the user.
	 * 
	 * @param username
	 *            The user to authenticate.
	 * @param password
	 *            Its password.
	 * @return True if the user was successfuly authenticated, and false
	 *         otherwise.
	 * @throws IOException
	 */
	public static boolean authenticateUser(String username, String password)
			throws IOException {
		// TODO (ejeadry): We need to store the state of the user here, and make
		// sure that he cannot login on different computers.

		Boolean foundMatch = false;

		File f = new File(DB_FOLDER + DB_AUTH_FILE + DB_FILE_TYPE);

		FileReader fReader;
		fReader = new FileReader(f);
		BufferedReader bReader = new BufferedReader(fReader);

		String s = bReader.readLine();

		while (s != null) {
			String[] pair = s.split("\\s+");
			if (pair[0].equals(username) && pair[1].equals(password)) {
				foundMatch = true;
				break;
			}

			s = bReader.readLine();
		}

		fReader.close();
		bReader.close();

		return foundMatch;
	}

	/**
	 * Returns all the users present in the database.
	 * 
	 * @return The JSON formatted String that contains the list of all users.
	 */
	public static String getallUsernames() {
		File f = new File(DB_FOLDER + DB_AUTH_FILE + DB_FILE_TYPE);
		FileReader fReader;

		JSONArray usernamesInJSON = new JSONArray();

		try {
			fReader = new FileReader(f);

			BufferedReader bReader = new BufferedReader(fReader);

			String s = bReader.readLine();

			while (s != null) {
				String[] pair = s.split("\\s+");

				usernamesInJSON.put(pair[0]);

				s = bReader.readLine();
			}

			fReader.close();
			bReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return usernamesInJSON.toString();
	}

	// public static void main(String[] args) {
	// DBHandler db = new DBHandler();
	//
	// /*
	// * Message mOne = new Message("nadim", "nick",
	// * "Hey man whats going on doodsky?"); Message mTwo = new Message("js",
	// * "nick", "you my boy");
	// *
	// *
	// * storeMessage(mOne); storeMessage(mTwo);
	// *
	// * String s = getMessages("nick"); System.out.println(s);
	// */
	//
	// System.out.println(authenticateUser("nick", "123456"));
	// System.out.println(authenticateUser("nick", "1266656"));
	// System.out.println(getallUsernames());
	// }
}