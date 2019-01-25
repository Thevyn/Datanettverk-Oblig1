package UDPSingle;


import java.net.*;
import java.io.*;
import java.nio.charset.Charset;

public class ServerUDP
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
                System.err.println("Usage: java ServerUDP [<port number>]");
                System.exit(1);
            }
        }

        System.out.println("Hi, I am EchoUCase UDP server!");

        try
        (
            // create an UDP/datagram socket for server on the given port
            DatagramSocket serverSocket =
                    new DatagramSocket(portNumber);
        )
        {
            String inText;
            do
            {
                byte[] buf = new byte[1024];


                // create datagram packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                // read datagram packet from the socket
                serverSocket.receive(packet);

                // extract text from the packet
                inText = new String(packet.getData());
                inText = inText.trim();

                // convert to uppercase
                String outText = inText.toUpperCase();

                // put the processed output text as array of bytes into the buffer
                buf = outText.getBytes();

                // get client's internet "address" and "port" from the hostname from the packet
                InetAddress clientAddr = packet.getAddress();
                int clientPort = packet.getPort();

                System.out.println("Client [" + clientAddr.getHostAddress() +  ":" + clientPort +"] > " + inText);

                // create datagram packet with the uppercase text to send back to the client
                packet = new DatagramPacket(buf, buf.length, clientAddr, clientPort);

                // send the uppercase text back to the client
                serverSocket.send(packet);

                System.out.println("I (Server) [" + InetAddress.getLocalHost() + ":" + portNumber + "] > " + outText);


            } while (inText != null);

            System.out.println("I am done, Bye!");

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
