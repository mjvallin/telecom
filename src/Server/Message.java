package Server;

import org.json.JSONObject;

public class Message
{
	public String from;
	public String to;
	public String content;
	JSONObject jsonObj;
	
	public Message(String from, String to, String content)
	{
		this.from = from;
		this.to = to;
		this.content = content;
		
		//which vars to copy into json obj
		String[] vars = {"from", "to", "content"};
		
		jsonObj = new JSONObject(this, vars);
	}
	
	public String getJSONString()
	{
		return jsonObj.toString();
	}
}