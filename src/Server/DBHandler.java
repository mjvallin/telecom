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

public class DBHandler 
{	
	public static final String DB_FOLDER = "db/";
	public static final String DB_FILE_TYPE = ".txt";
	
	public static final String DB_AUTH_FILE = "logins";

	public static void storeMessage(Message m)
	{
		String filename = m.to + DB_FILE_TYPE;
		String path = DB_FOLDER + filename;
		
		File f = new File(path);
		
		try {
			FileWriter fr = new FileWriter(f, true);
			PrintWriter pr = new PrintWriter(fr);
			
			//write the json string of the message to the file
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
	
	public static String getMessages(String username)
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
	
	public static boolean isAuthenticated(String username, String password)
	{
		Boolean foundMatch = false;
		
		File f = new File(DB_FOLDER + DB_AUTH_FILE + DB_FILE_TYPE);
		
		FileReader fReader;
		try {
			fReader = new FileReader(f);
			BufferedReader bReader = new BufferedReader(fReader);
			
			String s = bReader.readLine();
			
			while(s != null)
			{
				String[] pair = s.split("\\s+");
				if (pair[0].equals(username) && pair[1].equals(password))
				{
					foundMatch = true;
					break;
				}
				
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
		
		return foundMatch;
	}
	
	public static String getallUsernames()
	{
		File f = new File(DB_FOLDER + DB_AUTH_FILE + DB_FILE_TYPE);
		FileReader fReader;
		
		JSONArray usernamesInJSON = new JSONArray();
		
		try {
			fReader = new FileReader(f);
			
			BufferedReader bReader = new BufferedReader(fReader);
			
			String s = bReader.readLine();
			
			while(s != null)
			{
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
	
	public static void main(String[] args)
	{	
		DBHandler db = new DBHandler();
		
		/*
		Message mOne = new Message("nadim", "nick", "Hey man whats going on doodsky?");
		Message mTwo = new Message("js", "nick", "you my boy");
		
		
		storeMessage(mOne);
		storeMessage(mTwo);
		
		String s = getMessages("nick");
		System.out.println(s);
		*/
		
		System.out.println(isAuthenticated("nick", "123456"));
		System.out.println(isAuthenticated("nick", "1266656"));
		System.out.println(getallUsernames());
	}
}