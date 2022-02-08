package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private static int STATIC_BUFFER_SIZE=2048;

    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        byte[] fromUserBuffer = new byte[STATIC_BUFFER_SIZE]; 
        byte[] fromServerBuffer = new byte[STATIC_BUFFER_SIZE];
        Socket clientSocket = new Socket(hostname, port); 
        
        return new byte[0];
    }
}
