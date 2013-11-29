WARNING:
BEFORE YOU DO ANYTHING, REMEMBER NOT TO CHANGE THE STRUCTURE OF CLIENT FOLDER.

To run the application, please do the following:

================To set up the back-end server=============
1. Open a command prompt(terminal) and change the current working directory to ./src/client/ 
relative to the directory containing this README file. There should be a file in this directory 
called WebMessagingApp.jar

2. Run the server in command prompt as follows: java -jar WebMessagingApp.jar
===========================================================

================To run the front-end client================
3. Run the client by opening a web browser (tested browser: Chrome, Safari) and type localhost:1234 in the address bar and press enter

To login, you may look at usernames and passwords in db/logins.txt where every line
contains a  username followed by a password separated by a space in between. 
Here are some usernames and passwords you can use to login:	
username	|	Passwod
---------------------------------------
user1		|	pwd
user2		|	pwd
user3		|	pwd
===========================================================
--------------------------------------------------------------------------------------------

ALTERNATIVE: 
THE FOLLOWING IS NEEDED ONLY IF YOU ARE INTERESTED IN COMPILING THE CODE BY YOURSELF

compile and run using eclipse for simplicty:

1. Create an Eclipse Project
2. Create a package named Server and put all the java files in ./src/Server in the package
3. Create a package named org.json and put all the java file in ./src/org/json in the package
4. Copy paste everything in ./src/client to your Eclipse workspace (at the same level with the .project file)
5. Run Server.java
6. Run the client by opening a web browser (tested browser: Chrome, Safari) and type localhost:1234 in the address bar and press enter
