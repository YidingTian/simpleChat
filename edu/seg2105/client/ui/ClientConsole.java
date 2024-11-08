package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginID, String host, int port) 
  {
    try 
    {
        client= new ChatClient(loginID , host, port, this);
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
      while (true) 
      {
          try 
          {
              String message = fromConsole.nextLine();  // Read input from the console
              client.handleMessageFromClientUI(message);  // Send the message to the server
          } 
          catch (Exception ex) 
          {

              break;  // Break if there’s an error to avoid infinite loop
          }
      }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
      String loginID = "";
      String host = "localhost";  // Default to the localhost
      int port = DEFAULT_PORT;  // Port default to 5555
  
      // Get login ID, either from args or prompt
      if (args.length > 0) {
          loginID = args[0];
      } else {
          System.out.print("Enter login ID: ");
          Scanner scanner = new Scanner(System.in);
          loginID = scanner.nextLine().trim();  // Do not close this scanner
      }
  
      if (loginID.isEmpty()) {
          System.err.println("No login ID provided. Exiting.");
          System.exit(1);
      }
  
      // Parse host and port if provided
      if (args.length > 1) host = args[1];
      if (args.length > 2) port = Integer.parseInt(args[2]);
  
      // Set up the ClientConsole with correct loginID, host, and port
      ClientConsole chat = new ClientConsole(loginID, host, port);
      chat.accept();  // Start waiting for console input
  }
  public void handleMessageFromServer(Object msg) {
      System.out.println("Message from server: " + msg.toString()); // Display in ClientConsole
  }
}
//End of ConsoleChat class
