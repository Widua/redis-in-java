package handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable{

    private final Socket clientSocket;
    private Map<String,String> vars = new HashMap<>();

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
                String[] command = new String(inputData).split("\r\n");
                if (command.length == 1){
                    break;
                }
                switch (command[2].toLowerCase()){
                    case "ping" -> {
                        output.write("+PONG\r\n".getBytes());
                    }
                    case "echo" -> {
                        String arg = command[4];
                        output.write(String.format("$%s\r\n%s\r\n",arg.length(),arg).getBytes());
                    }
                    case "set" -> {
                        String argName = command[4];
                        String argVal = command[6];
                        vars.put(argName,argVal);
                        output.write("+OK\r\n".getBytes());
                    }
                    case "get" -> {
                        String argName = command[4];
                        if (!vars.containsKey(argName)){
                            output.write("$-1\r\n".getBytes());
                        }
                        String argVal = vars.get(argName);
                        output.write(String.format("$%s\r\n%s\r\n",argVal.length(),argVal).getBytes());
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
