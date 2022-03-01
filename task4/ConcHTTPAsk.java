import java.net.*;
import java.io.*;

public class ConcHTTPAsk {
    
    public static void main( String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        System.out.println("Listening for connection on port " + serverSocket);

        while(true) {
            Socket connectionSocket = serverSocket.accept();
            MyRunnable client = new MyRunnable(connectionSocket);
            Thread thread = new Thread(client);
            thread.start();
        }
    }
}



