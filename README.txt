WARNING:
BEFORE YOU DO ANYTHING, REMEMBER NOT TO CHANGE THE STRUCTURE OF CLIENT FOLDER.

All files and folders need to be present in order for the program to work
Here is the folder structure:
   /client
    css/
    db/
    js/
    index.html
    WebMessagingApp.jar  

To run the application, please do the following:

================To set up the back-end server=============
1. Open a command prompt(terminal) and change the current working directory to where WebMessagingApp.jar is.
The path to WebMessagingApp.jar relatively to this README file is: ./src/client/
(type “cd src/client” without the quotation mark)
   
2. Run the server in command prompt as follows: java -jar WebMesagingApp.jar
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