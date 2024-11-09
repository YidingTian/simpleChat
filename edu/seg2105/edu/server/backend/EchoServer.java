package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.edu.*;
import ocsf.server.*;

//import edu.seg2105.edu.server.backend.ServerConsole;
/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 


{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  ServerConsole console;
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */

  public EchoServer(int port, ServerConsole console) 
  {
    super(port);
    this.console = console;

  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  
	
    String myStr = (String) msg;
    if(myStr.startsWith("#login")) {
    	if(client.getInfo("loginID") == null) {
    	
    		String[] tokens= myStr.split(" ", 2);
        	if(tokens.length>1) {
        		String loginID = tokens[1];
        		client.setInfo("loginID", loginID);
        		System.out.println(loginID + " has logged on");
        		try {
					client.sendToClient("Login successful as " + loginID);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}else {
        	
        		System.out.println("No login ID has been provided with #login command. ");
        		closeConnectionWithError(client, "Login ID required");
        	}
    	} else {
    		try {
				client.sendToClient("Error: Already logged in.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		closeConnectionWithError(client, "Login command can only be used once after connected to server");
    	}
    	
    
    }
    else {
    	String message = ((String) msg).replaceFirst("^[^>]+>\\s*", "") ;
        System.out.println("Message received: " + message + " from " + client.getInfo("loginID"));
    	 this.sendToAllClients(msg);
    }
   
  }
  
  private void closeConnectionWithError(ConnectionToClient client, String errorMessage) {
	  try {
		  client.sendToClient(errorMessage);
		  client.close();
	  }catch(IOException e) {
		  System.err.println("Failed to close connection: " + e.getMessage());
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
	/**
	 * Hook method called each time a new client connection is
	 * accepted. The default implementation does nothing.
	 * @param client the connection connected to the client.
	 */
	protected void clientConnected(ConnectionToClient client) {
		System.out.println("A new client has connected to the server.");
	}

	/**
	 * Hook method called each time a client disconnects.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		String loginId = (String) client.getInfo("loginID");
		
		   if (loginId != null) {
		        System.out.println(loginId + " has disconnected!");
		    } else {
		        System.out.println("An anonymous client has disconnected.");
		    }
		
	}
	@Override 
	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		
		try {
			client.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void handleMessageFromServerUI(String message) {
		console.display(message);
	}

}
//End of EchoServer class
