// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private static String loginID;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 

    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	  //System.out.println(msg);
    clientUI.display(msg.toString());
    
    
  }
  //from lecture
  public static void main(String[] args) throws IOException {
	    if (args.length < 1) {
	        System.out.println("No login ID provided! Using default ID.");
	        loginID = "defaultUser";
	    } else {
	        loginID = args[0];
	    }
	    ChatIF clientUI = new ChatIF() {
	        @Override
	        public void display(String message) {
	            System.out.println(message);
	        }
	    };

	    ChatClient cc = new ChatClient(loginID, "localhost", 5555, clientUI);
	    cc.openConnection();
	    cc.sendToServer("Hello!");
	}

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
      try
      {
          if (message.startsWith("#")) {
              handleCommand(message);
          } else {
              sendToServer(message); 
          }
      }
      catch(IOException e)
      {
          clientUI.display("Could not send message to server. Terminating client.");
          quit();
      }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
	/**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
  protected void connectionException(Exception exception) {
      System.out.println("The connection to the server has been lost: " + exception.getMessage());
      System.exit(0); // Exit the client application
  }
	/**
	 * Implement Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be override by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
  	protected void connectionClosed() {
  	    System.out.println("Disconnected.");
  	    System.exit(0); // Exit the client application
  	}
    private void handleCommand(String command){
        if(command.equals("#quit")){
          quit();
        }
        else if(command.equals("#logoff")){
          try {
            if(isConnected()){
            
            closeConnection();}
            else{
              clientUI.display("Error, no client logged in" );
            }
          } catch (IOException e) {
            clientUI.display("error in quit command, please check if there is any error in the ChatClient" );
          }
        }
        else if(command.equals("#sethost")){
          if(!isConnected()){
            String newHost = command.substring(9).trim();
            clientUI.display("new host assigned");
          setHost(newHost);}
          else{
            clientUI.display("the client is still connected");
          }
        }
        else if(command.equals("#setport")){
          if(!isConnected()){
            int newport = Integer.parseInt(command.substring(9).trim());
            clientUI.display(("new port has been assigned" ));
          setPort(newport);}
          {
            clientUI.display("the client is still connected");
          }
        }
        else if(command.equals("#login")){
          if(!isConnected()){
            try {
              openConnection();
              clientUI.display("login successfull");
            } catch (IOException e) {
              clientUI.display("Error in login, please check if there is any error in the ChatClient" );
            }
          }
          else{
            clientUI.display("Error in login, please check if there is any error in the ChatClient");
          }
            }
        else if(command.equals("#gethost")){
          clientUI.display(getHost());
        }
        else if(command.equals("#getport")){
          int integer_value = getPort();
          clientUI.display(String.valueOf(integer_value));
            }
    }
    @Override
    protected void connectionEstablished() {
        try {
            sendToServer("#login " + loginID); // Send #login command with login ID
        } catch (IOException e) {
            clientUI.display("Error: Could not send login message to server.");
        }
    }
}
//End of ChatClient class
