package handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        while (true){
            try{
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                byte[] inputData = new byte[100];

                input.read(inputData);
                System.out.println("Received: "+new String(inputData));
                output.write("+PONG\r\n".getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
