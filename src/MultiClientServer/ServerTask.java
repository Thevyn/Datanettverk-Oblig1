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
                                outText = "couldn't find that location";
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
            URLConnection connection = new URL("https://www.google.com/search?q=" + input + "+Time").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            BufferedReader r  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            String outp = sb.toString();

            String[] outp2 = outp.split("gsrt vk_bk dDoNo\" aria-level=\"3\" role=\"heading\">");
            outp = outp2[1];
            //picking out everything before the <div> element ends
            String[] totalDiv = outp.split(input);
            outp2 = totalDiv;
            outp = outp2[0];
            outp2 = outp.split("<");

            String time = outp2[0];

            outp = totalDiv[0];
            outp2 = outp.split("vk_sh\">");
            outp = outp2[1];
            outp2 = outp.split("<");
            String day = outp2[0];

            outp = totalDiv[0];
            outp2 = outp.split("KfQeJ\">");
            outp = outp2[1];
            outp2 = outp.split("<");
            String date = outp2[0];



            System.out.println("time in " + input + ": " + time + " day: " + day + date);
            return "time in " + input + ": " + time + " day: " + day + date;
        }

        /**
         * Evaluate the intext if it is a valid binary expression. Otherwise, reverse the text.
         * @param intext
         * @return
         */
        private String ProcessString(String intext)
        {
            String outtext;

            Pattern p = Pattern.compile("(\\d*\\.?\\d+)\\s*([\\+\\-\\*/])\\s*(\\d*\\.?\\d+)");
            Matcher m = p.matcher(intext);
            if (m.matches())
            {
                double operand1 = Double.valueOf(m.group(1));
                char operator = m.group(2).charAt(0);
                double operand2 = Double.valueOf(m.group(3));

                double result=0;
                switch(operator)
                {
                    case '+': result = operand1 + operand2;break;
                    case '-': result = operand1 - operand2;break;
                    case '*': result = operand1 * operand2;break;
                    case '/': result = operand1 / operand2;break;
                }
                outtext = String.valueOf(result);
            }
            else
                outtext = new StringBuffer(intext).reverse().toString();

            return outtext;
        }
    }
}