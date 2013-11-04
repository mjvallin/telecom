package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBHandler 
{	
	public static final String DB_FOLDER = "/db/";
	public static final String DB_FILE_TYPE = ".txt";
	
	public void storeMessage(Message m)
	{
		String filename = m.to + DB_FILE_TYPE;
		String path = DB_FOLDER + filename;
		
		File f = new File(path);
		
		try {
			PrintWriter pr = new PrintWriter(f);
			pr.println(m.getJSONString());
			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMessages(String username)
	{
		String filename = username + DB_FILE_TYPE;
		String path = DB_FOLDER + filename;
		
		JSONArray messagesInJSON = new JSONArray();
		
		File f = new File(path);
		try {
			FileReader fReader = new FileReader(f);
			BufferedReader bReader = new BufferedReader(fReader);
			
			String s = bReader.readLine();
			while (s != null)
			{
				messagesInJSON.put(new JSONObject(s));
				s = bReader.readLine();
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return messagesInJSON.toString();
	}
}