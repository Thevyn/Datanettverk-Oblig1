package MultiClientServer;

public class StartMultiServer {
    public static void main(String[] args) throws Exception {
        ServerTask serverTask = new ServerTask(5555);
        serverTask.start();
        System.out.println("Server is running, please call the Clients.");
    }
}
