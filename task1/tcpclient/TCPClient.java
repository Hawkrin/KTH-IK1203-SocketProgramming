package tcpclient;
import java.net.*; //Socket class
import java.io.*;

public class TCPClient {
    private static int STATIC_BUFFER_SIZE=1024;

    public TCPClient() {}

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        /*Arrays that stores the information we send and recive*/
        //byte[] fromUserBuffer = new byte[STATIC_BUFFER_SIZE]; 
        byte[] fromServerBuffer = new byte[STATIC_BUFFER_SIZE];
        ByteArrayOutputStream bs = new ByteArrayOutputStream(); //dynamic array

        /*Creates a socket for communication*/
        Socket clientSocket = new Socket(hostname, port);

        InputStream inputStream = clientSocket.getInputStream(); //reads the input data
        OutputStream outputStream = clientSocket.getOutputStream(); // writes the output data
        outputStream.write(toServerBytes); //sends bytes on socket
        
        int fromUserLength = 0; // counts how many bytes we have read.
        do {
            fromUserLength = inputStream.read(fromServerBuffer); //reads the data
            bs.write(fromServerBuffer, 0, fromUserLength);
        } while(inputStream.read() != -1);
        
        clientSocket.close();
        return bs.toByteArray();
    }
}
