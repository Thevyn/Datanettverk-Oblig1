/**
 * Multiclient socket multiclient.server with JavaFX: ServerTask class
 * DATS/ITPE2410 Networking and Cloud Computing, Spring 2019
 * Raju Shrestha, OsloMet
 **/
package MultiClientServer;

import javafx.collections.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.w3c.dom.html.HTMLBodyElement;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  ServerTask class which waits for a client to connect in a separate thread using (extending) Java's Task<> class
 */
public class ServerTask extends Task<Void>
{
    int portNumber;

    private ObservableMap<String, String> clientMessageMap = FXCollections.observableMap(new HashMap<>());
    public ObservableMap<String, String> getClientMessageMap() {return clientMessageMap;}

    public ServerTask(int portNumber)
    {
        this.portNumber = portNumber;
    }

    @Override
    public Void call() throws Exception
    {
        try (ServerSocket serverSocket = new ServerSocket(portNumber))
        {
            while (true)
            {
                Socket conn = serverSocket.accept();

                ClientService cs = new ClientService(conn);
                String client = conn.getInetAddress().getHostAddress() + ":" + conn.getPort();

                // Listens to the changes in the messageProperty of cs object
                cs.messageProperty().addListener((obs, oldMessage, newMessage) ->
                {
                    switch(newMessage.toLowerCase())
                    {
                        case "connected":
                            clientMessageMap.put(client, "");break;
                        case "disconnected":
                            clientMessageMap.remove(client);break;
                        default:
                            clientMessageMap.put(client,newMessage);
                    }
                    updateMessage(client + ": " + newMessage);
                });

                cs.start();
            }
        } catch (IOException e)
        {
            System.out.println("Exception!!! "+e.getMessage());
        }

        return null;
    }

    public void start()
    {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     *  ClientService class which serves a client in a separate thread using (extending) Java's Service<> class
     */
    private static class ClientService extends Service<Void>
    {
        Socket connectSocket;
        private String client;

        public ClientService(Socket connectSocket)
        {
            this.connectSocket = connectSocket;
            client = connectSocket.getInetAddress().getHostAddress();
        }

        @Override
        protected Task<Void> createTask()
        {
            Task<Void> task =  new Task<Void>()
            {
                @Override
                public Void call() throws InterruptedException
                {
                    updateMessage("Connected");


                    try (PrintWriter out = new PrintWriter(connectSocket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(connectSocket.getInputStream())))
                    {
                        String inText;
                        while ((inText = in.readLine()) != null)
                        {
                            String outText = "";

                            // Convert the received text to uppercase
                            //String outText = inText.toUpperCase();
                            try{
                                outText = getTime(inText);
                            }catch(Exception e){
                                outText = "Couldn't find that location. Please try again";
                                e.printStackTrace();
                        }
                            // Write the converted uppercase string to the connection socket
                            out.println(outText);

                            updateMessage("<< " + inText + "\n>> " + outText);

                        }
                    } catch (IOException e)
                    {
                        System.out.println("Exception!!! "+e.getMessage());
                    }
                    finally
                    {
                        updateMessage("Disconnected"); // out of the loop - indicates client disconnection
                    }
                    return null;
                }
            };
            return task ;
        }

        private String getTime(String input) throws Exception{


            input = input.replaceAll(" ", "");
            URLConnection connection = new URL("https://www.worldtimeserver.com/search.aspx?searchfor=" + input).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                    Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                if(line.contains("<span id=\"theTime\" class=\"fontTS\">")) {
                    line = r.readLine();
                    line = line.replaceAll("\\s+","");
                    sb.append(line);
                }
                if(line.contains("<span class=\"font6\">")) {
                    line = r.readLine();
                    line = line.replaceAll("</span>", "");
                    line = line.replaceAll("\\s+","");
                    line = line.replaceAll(","," ");
                    sb.append(" ");
                    sb.append(line);
                    System.out.println(sb);
                    break;
                }

            }
            return sb.toString();
        }


    }
}