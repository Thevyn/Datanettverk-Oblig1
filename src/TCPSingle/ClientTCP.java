package TCPSingle;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientTCP {
    public static void main(String[] args) throws IOException {
      Scanner sc = new Scanner(System.in);
        System.out.println("Enter the Server IP address: ");
      String hostName = sc.nextLine(); // Default host, localhost
      int portNumber = 5555; // Default port to use
      if (args.length > 0) {
           hostName = args[0];
           if (args.length > 1)
           {
             portNumber = Integer.parseInt(args[1]);
             if (args.length > 2)
             {
               System.err.println("Usage: java ClientTCP [<host name>] [<port number>]");
               System.exit(1);
             }
           }
        }


        System.out.println("Type in desired city or country ");

        try
        (
            // create TCP socket for the given hostName, remote port PortNumber
            Socket clientSocket = new Socket(hostName, portNumber);

            // Stream writer to the socket
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            // Stream reader from the socket
            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
            // Keyboard input reader
            BufferedReader stdIn =
                    new BufferedReader(
                            new InputStreamReader(System.in))
        )
        {
            String userInput;
            // Loop until null input string
            System.out.print("I (Client) [" + InetAddress.getLocalHost()  + ":" + clientSocket.getLocalPort() + "] > ");
            while ((userInput = stdIn.readLine()) != null && !userInput.isEmpty())
            {
                // write keyboard input to the socket
                out.println(userInput);

                // read from the socket and display
                String inText = in.readLine();

                System.out.println("Server [" + hostName +  ":" + portNumber + "] > " + inText);
                System.out.print("I (Client) [" + clientSocket.getLocalAddress().getHostAddress() + ":" + clientSocket.getLocalPort() + "] > ");
            }
        } catch (UnknownHostException e)
        {
            System.err.println("Unknown host " + hostName);
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
