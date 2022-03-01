import tcpclient.TCPClient;
import java.io.*;
import java.net.*;

public class MyRunnable implements Runnable {
    private static int STATIC_BUFFER_SIZE = 1024;
    private static boolean shutdown = false;
    private static Integer timeout = null;
    private static Integer limit = null;
    private Socket connectionSocket;

    public MyRunnable (Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        try {
            connectionSocket.setSoTimeout(2000);
            
            String host = null;
            int port = 0;
            String serverStatus = null; //Displayed at HTTP site
            String dataContentString = "";
            String stringDecoder = "";
            byte[] fromClientBuffer = new byte[STATIC_BUFFER_SIZE];

            InputStream inputStream = connectionSocket.getInputStream(); //reads the input data
            OutputStream outputStream = connectionSocket.getOutputStream(); //write the output data

            String validConnection = "HTTP /1.1 200 OK\r\n\r\n";
            outputStream.write(validConnection.getBytes("UTF-8"));
            int fromClientLength = inputStream.read(fromClientBuffer);

            while(fromClientLength != -1) {
                stringDecoder = new String(fromClientBuffer, 0, fromClientLength);
                String[] stringSplitter = stringDecoder.split("[?&= ]", 10);

                //the URL is being picked apart and the values are being extracted via a string split method.
                try {
                    if (stringSplitter[0].equals("GET") && stringSplitter[1].equals("/ask") && stringDecoder.contains("HTTP/1.1")) {
                        serverStatus = ("HTTP/1.1 200 OK \r\n\r\n");
                        for (int i = 0; i < stringSplitter.length; i++) {
                            if (stringSplitter[i].equals("hostname")) {
                                host = stringSplitter[i + 1];
                            }
                            else if (stringSplitter[i].equals("port")) {
                                port = Integer.parseInt(stringSplitter[i + 1]);
                            }
                            else if (stringSplitter[i].equals("string")) {
                                dataContentString = stringSplitter[i + 1];
                            }
                            else if (stringSplitter[i].equals("shutdown")) {
                                shutdown = Boolean.parseBoolean(stringSplitter[i + 1]);
                            }
                            else if (stringSplitter[i].equals("limit")) {
                                limit = Integer.parseInt(stringSplitter[i + 1]);
                            }
                            else if (stringSplitter[i].equals("timeout")) {
                                timeout = Integer.parseInt(stringSplitter[i + 1]);
                            }
                        }
                        outputStream.write(serverStatus.getBytes("UTF-8"));
                    } 
                    else { serverStatus = ("HTTP/1.1 400 Bad Request \r\n"); } // if ask is removed
                } catch(NumberFormatException ex){}

                if (stringDecoder.contains("\n")) { break; }
            }

                if (!(serverStatus.contains("HTTP/1.1 400 Bad Request"))) { //if connection is successfull
                    try {
                        byte[] toServerBytes = dataContentString.getBytes("UTF-8");
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        byte[] result = tcpClient.askServer(host, port, toServerBytes);
                        outputStream.write(result);
                    } catch (IOException ex) {
                        serverStatus = ("HTTP/1.1 404 Not Found \r\n"); //if no hostname or hostname not recognized
                        outputStream.write(serverStatus.getBytes("UTF-8"));
                    }
                } 
                else { outputStream.write(serverStatus.getBytes("UTF-8")); }

                connectionSocket.close();
        }catch(NumberFormatException | IOException ex){}        
    }       
}



