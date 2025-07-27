package handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private Map<String, String> vars = new HashMap<>();
    private Map<String, List<String>> lists = new HashMap<>();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                byte[] inputData = new byte[100];

                input.read(inputData);
                String[] command = new String(inputData).split("\r\n");
                if (command.length == 1) {
                    break;
                }
                switch (command[2].toLowerCase()) {
                    case "ping" -> {
                        output.write("+PONG\r\n".getBytes());
                    }
                    case "echo" -> {
                        String arg = command[4];
                        output.write(String.format("$%s\r\n%s\r\n", arg.length(), arg).getBytes());
                    }
                    case "set" -> {
                        String argName = command[4];
                        String argVal = command[6];
                        vars.put(argName, argVal);
                        output.write("+OK\r\n".getBytes());

                        if (command.length > 8) {
                            String ex = command[8];
                            new Thread(() -> {
                                if (ex.equalsIgnoreCase("px")) {
                                    try {
                                        Thread.sleep(Long.parseLong(command[10]));
                                        vars.remove(argName);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }).start();
                        }
                    }
                    case "get" -> {
                        String argName = command[4];
                        if (!vars.containsKey(argName)) {
                            output.write("$-1\r\n".getBytes());
                        }
                        String argVal = vars.get(argName);
                        output.write(String.format("$%s\r\n%s\r\n", argVal.length(), argVal).getBytes());
                    }
                    case "rpush" -> {
                        String listName = command[4];
                        String element = command[6];

                        if (lists.containsKey(listName)){
                            List<String> list = new ArrayList<>(lists.get(listName));
                            list.add(element);
                            lists.put(listName,list);
                        } else {
                            lists.put(listName,List.of(element));
                        }

                        output.write(String.format(":%s\r\n",lists.get(listName).size()).getBytes());
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
