package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;

import edu.seg2105.edu.server.backend.ServerConsole;
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
  final private String loginIDKey = "loginID";
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   * (same function as what demonstrated during lecture)
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  private void handleLoginCommand(String message, ConnectionToClient client) throws IOException {
	    // Ensure #login is only allowed as the first command
	    if (client.getInfo(loginIDKey) != null) {
	        client.sendToClient("ERROR: You are already logged in. Connection will be closed.");
	        client.close();
	        return;
	    }
	    String loginID = message.substring(7).trim(); // Extract login ID
	    if (loginID.isEmpty()) {
	        client.sendToClient("ERROR: Login ID cannot be empty. Connection will be closed.");
	        client.close();
	    } else {
	        client.setInfo(loginIDKey, loginID); // Save login ID using loginIDKey
	        client.sendToClient("Login successful. Welcome, " + loginID + "!");
	        System.out.println("Client logged in with ID: " + loginID);
	    }
	}
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	  String message = msg.toString();
	  try {
	      String loginID = (String) client.getInfo(loginIDKey);  // Get client ID if logged in
	      if (loginID == null && message.startsWith("#login ")) {
	          handleLoginCommand(message, client);  // Handle login
	      } else if (loginID != null) {
	          System.out.println("Message received from " + loginID + ": " + message);  // Display message
	          sendToAllClients(loginID + ": " + message);  // Broadcast message to all clients
	      } else {
	          client.sendToClient("ERROR: You must log in first using #login <loginID>");
	          client.close();
	      }
	  } catch (IOException e) {
	      System.out.println("Error handling message from client: " + e.getMessage());
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
 * @throws IOException 
   */
  public static void main(String[] args) throws IOException 
  {
	  EchoServer es = new EchoServer(5555);
    int port = 5555; //Port to listen on
    es.listen();
    
    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }

    ServerConsole console = new ServerConsole(port, sv);
    console.accept(); 
  }
  
  public void handleMessageFromServerConsole(String message) {
	  if (message.startsWith("#")) {
		  String[] parameters = message.split(" ");
		  String command = parameters[0];
		  switch (command) {
		  	case "#quit":
		  		try {
		  			close();
		  			System.out.println("Server Stopping.");
		  		} catch (IOException e) {
		  			System.exit(1);
		  		}
		  		System.exit(0);
		  		break;
		  	case "#stop":
		  		this.stopListening();
	  			System.out.println("Server has stopped listening for connections.");
		  		break;
		  	case "#close":
		  		try {
		  			close();
		  			System.out.println("Server Stopping.");
		  		} catch (IOException e) {
		  			System.exit(1);
		  		}
		  		System.exit(0);
		  		break;
		  	case "#setport":
		  		if (!this.isListening() && this.getNumberOfClients() < 1) {
		  			super.setPort(Integer.parseInt(parameters[1]));
		  			System.out.println("Port set to " +
		  					Integer.parseInt(parameters[1]));
		  		} else {
		  			System.out.println("Can't do that now. Server is connected.");
		  		}
		  		break;
          		  	case "#start":
		  		if (!isListening()) {
		  			try {
		  				listen();
		  				System.out.println("Server started listening for new clients.");
		  			} catch (IOException e) {
		  				System.out.println("Faced an error while closing");
		  			}
		  		} else {
		  			System.out.println("We are already started and listening for clients!.");
		  		}
		  		break;
		  	case "#getport":
		  		System.out.println("Current port is " + this.getPort());
		  		break;
		  	default:
		  		System.out.println("Invalid command: '" + command+ "'");
		  		break;
		  }
	  } else {
		  this.sendToAllClients(message);
	  	}
  }
  //print out a message when a client just connected
  @Override
  protected void clientConnected(ConnectionToClient client) {
	    System.out.println("Client connected: " + client);
	}
  //print out a message when a client disconnected
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
      String loginID = (String) client.getInfo(loginIDKey);
      System.out.println("Client disconnected: " + (loginID != null ? loginID : "Unknown"));
  }
  
  public void handleMessageFromServer(Object msg) {
	    System.out.println("Server Message: " + msg);
	    this.sendToAllClients("Server Message: " + msg); 
	}
  public void broadcastToClients(String message) {
	    System.out.println("Broadcasting to clients: " + message);

	    Thread[] clientThreadList = getClientConnections();
	    if (clientThreadList != null) {
	        for (Thread clientThread : clientThreadList) {
	            try {
	                ((ConnectionToClient) clientThread).sendToClient(message);
	            } catch (IOException e) {
	                System.out.println("Failed to send message to a client.");
	            }
	        }
	    }
	}

  
}
//End of EchoServer class
