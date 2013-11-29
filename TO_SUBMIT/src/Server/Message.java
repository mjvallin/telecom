package Server;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Message entity class
 * 
 * @author Ming-Ju Lin
 * @author Jean-Sebastien Dery
 * @author Fakrul Islam
 * @author Sen Li
 * @author Nicholas Destounis
 * 
 */

public class Message {
	private final String UID = "uid";

	public String from;
	public String to;
	public String content;
	@SuppressWarnings("unused")
	public int uid;
	JSONObject jsonObj;

	public Message(String from, String to, String content) {
		this.from = from;
		this.to = to;
		this.content = content;

		// which vars to copy into json obj
		String[] vars = { UID, "from", "to", "content" };

		jsonObj = new JSONObject(this, vars);
	}

	/**
	 * Sets the UID in the Message object. It is used to help the client keeping
	 * track of the messages it has received.
	 * 
	 * @param uid
	 *            The UID to be set.
	 * @throws JSONException
	 */
	public void setUid(int uid) throws JSONException {
		jsonObj.put(UID, uid);
		this.uid = uid;
	}

	public String getJSONString() {
		return jsonObj.toString();
	}
}