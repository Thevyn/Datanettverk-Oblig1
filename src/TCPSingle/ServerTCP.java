package TCPSingle;

import java.net.*;
import java.io.*;
import java.nio.charset.Charset;

public class ServerTCP
{
    public static void main(String[] args) throws IOException
    {
        int portNumber = 5555; // Default port to use

        if (args.length > 0)
        {
            if (args.length == 1)
                portNumber = Integer.parseInt(args[0]);
            else
            {
                System.err.println("Usage: java ServerTCP [<port number>]");
                System.exit(1);
            }
        }

        System.out.println("Hi, I am a TCP server");

        // try() with resource makes sure that all the resources are automatically
        // closed whether there is any exception or not!!!
        try (
                // Create server socket with the given port number
                ServerSocket serverSocket =
                        new ServerSocket(portNumber);
                // create connection socket, server begins listening
                // for incoming TCP requests
                Socket connectSocket = serverSocket.accept();

                // Stream writer to the connection socket
                PrintWriter out =
                        new PrintWriter(connectSocket.getOutputStream(), true);
                // Stream reader from the connection socket
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connectSocket.getInputStream()));
        )
        {
            InetAddress clientAddr = connectSocket.getInetAddress();
            int clientPort = connectSocket.getPort();
            String inText;


            // read from the connection socket
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

                System.out.println("<< " + inText + "\n>> " + outText);

            }


        } catch (IOException e)
        {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }

    private static String getTime(String input) throws Exception{


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

}
