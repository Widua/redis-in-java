import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
    System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
          serverSocket = new ServerSocket(port);
          serverSocket.setReuseAddress(true);
          clientSocket = serverSocket.accept();
          try(
                  InputStream input = clientSocket.getInputStream();
                  OutputStream output = clientSocket.getOutputStream();
                  ) {
              while (true) {

                  byte[] inputData = new byte[100];
                  input.read(inputData);

                  System.out.println("Received: " + new String(inputData));
                  output.write("+PONG\r\n".getBytes());
              }
          }
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        } finally {
          try {
            if (clientSocket != null) {
              clientSocket.close();
            }
          } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
          }
        }
  }
}
