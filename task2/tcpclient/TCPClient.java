package tcpclient;
import java.net.*; //Socket class
import java.io.*;

public class TCPClient {
    private static int STATIC_BUFFER_SIZE=1024;
    private boolean shutdown = false;
    private Integer timeout = null;
    private Integer limit = null;

    public TCPClient() {}
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        /*Arrays that stores the information we send and recive*/
        byte[] fromUserBuffer = new byte[STATIC_BUFFER_SIZE]; 
        byte[] fromServerBuffer = new byte[STATIC_BUFFER_SIZE];
        ByteArrayOutputStream bs = new ByteArrayOutputStream(); //dynamic array

        /*Creates a socket for communication*/
        Socket clientSocket = new Socket(hostname, port);
        if(timeout != null) { clientSocket.setSoTimeout(timeout); } //enable time out in ms
        
        InputStream inputStream = clientSocket.getInputStream(); //reads the input data
        OutputStream outputStream = clientSocket.getOutputStream(); // writes the output data

        int fromUserLength = 0; // counts how many bytes we have read.
        try {
            outputStream.write(toServerBytes); //sends bytes on socket

            /*disables output stream*/
            if(shutdown) { 
                System.out.println("SERVER SHUTDOWN");
                clientSocket.shutdownOutput(); 
            }
            
            if(limit == null) {
                while( (fromUserLength = inputStream.read(fromServerBuffer)) != -1) {
                    bs.write(fromServerBuffer, 0, fromUserLength);
                } 
            }
            else if(limit < STATIC_BUFFER_SIZE) {
                inputStream.read(fromServerBuffer, 0, limit);
                bs.write(fromServerBuffer);
            }
            else { //if limit > buffersize
                int tempBuffertSize = 0;
                while(limit > 0 && (fromUserLength = inputStream.read(fromServerBuffer, 0, tempBuffertSize)) != -1) {
                    bs.write(fromServerBuffer, 0, fromUserLength);
                    tempBuffertSize = limit - fromUserLength; // the remaining bytes to read.
                }   
            }
                
        }catch(SocketTimeoutException exc) {
            clientSocket.shutdownOutput();
        }
        fromUserBuffer = bs.toByteArray();
        return fromUserBuffer;
    }    
}
