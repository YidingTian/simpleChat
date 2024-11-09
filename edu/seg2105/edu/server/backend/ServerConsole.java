package edu.seg2105.edu.server.backend;
import java.io.IOException;
import java.util.Scanner;
import edu.seg2105.client.common.ChatIF;
public class ServerConsole implements ChatIF {
	final public static int DEFAULT_PORT = 5555;
	
	EchoServer server;
	
	Scanner fromConsole;
	
	public ServerConsole(int port){
		this.server = new EchoServer(port, this);
		try {
            server.listen();
        } catch (IOException e) {
            System.out.println("ERROR - Could not listen for clients!");
        }

        // Start a new thread to handle console input commands
      	fromConsole = new Scanner(System.in);
}
	
	private void handleCommand(String command)  {
		if(command.equals("#quit")) {
			System.out.println("Server is shutting down");
			server.stopListening();
			try {
				server.close();
			} catch(IOException e) {
				System.out.println("Error closing the server: " + e.getMessage());
			}
			
			System.exit(0);
		}
		else if(command.equals("#stop")) {
			server.stopListening();
		}
		else if(command.equals("#close")) {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(command.startsWith("#setport")) {
			if(!server.isListening() && server.getNumberOfClients() == 0) {
				int port = Integer.parseInt(command.split(" ")[1]);
				server.setPort(port);
				System.out.println("Port has been set to " + port);
			}
			else {
				System.out.println("Error: Cannot change port while server is active or when clients are connected");
			}
		}
		else if(command.equals("#start")) {
			if(!server.isListening()) {
				try {
					server.listen();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Server is already listening.");
			}
		}
		else if(command.equals("#getport")) {
			System.out.println("current port is " + server.getPort());
		}
	}
    
    
	@Override
	public void display(String message) {
	
		System.out.println("SERVER MSG> " + message);
		server.sendToAllClients("SERVER MSG> " + message);
	}
/**
 * This method is responsible for the creation of 
 * the server instance (there is no UI in this phase).
 *
 * @param args[0] The port number to listen on. The default port is 5555 
 *          if no argument is entered.
 * @throws IOException 
 */
	public static void main(String[] args) {
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		}
		catch(Throwable t) {
			port = EchoServer.DEFAULT_PORT;
		}
		ServerConsole sc = new ServerConsole(port);
		EchoServer sv = sc.server;
		try 
	    {
	      sv.listen(); //Start listening for connections
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
		sc.accept();
	}

	public void accept() {
        try {
  
            String message;
            while (true) {
                message = fromConsole.nextLine();
                if (message.startsWith("#")) {
                    handleCommand(message);
                } else {
                    server.handleMessageFromServerUI(message);
                }
            }
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from server console!");
            ex.printStackTrace();
        }
  
    }
}
//End of ServerConsole class
