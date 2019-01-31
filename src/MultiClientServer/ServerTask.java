
package MultiClientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.Charset;


public class ServerTask
{
    int portNumber;

    public ServerTask(int portNumber)
    {
        this.portNumber = portNumber;
    }


    public void start() throws Exception
    {
        try (
                ServerSocket serverSocket = new ServerSocket(this.portNumber)
        )
        {
            while (true)
            {
                Socket conn = serverSocket.accept();
                ClientService cs = new ClientService(conn);
                cs.start();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    /**
     *  ClientService class which serves a client in a separate thread using (extending) Java's Service<> class
     */
    private static class ClientService extends Thread
    {
        Socket connectSocket;
        private String client;

        public ClientService(Socket connectSocket)
        {
            this.connectSocket = connectSocket;
            client = connectSocket.getInetAddress().getHostAddress();
        }

        public void run()
        {

                    try (
                            PrintWriter out = new PrintWriter(connectSocket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(connectSocket.getInputStream()))
                    )
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


                        }
                    } catch (IOException e)
                    {
                        System.out.println("Exception!!! "+e.getMessage());
                    }
        }

        private String getTime(String input) throws Exception{

            String url = "https://www.worldtimeserver.com/search.aspx?searchfor=" + URLEncoder.encode(input, "UTF-8");
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                    Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                if(line.contains("<span id=\"theTime\" class=\"fontTS\">")) {
                    line = r.readLine();
                    line = line.replaceAll("^\\s+", "");
                    sb.append(line);
                }
                if(line.contains("<span class=\"font6\">")) {
                    line = r.readLine();
                    line = line.replaceAll("</span>", "");
                    line = line.replaceAll("^\\s+", "");
                    sb.append(" - ");
                    sb.append(line);
                    System.out.println(sb);
                    break;
                }

            }
            return sb.toString();
        }
    }
}