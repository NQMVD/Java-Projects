import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Thread;

public class EchoThread extends Thread {
    protected Socket socket;
    MessageHandler handler;
    DataOutputStream outToClient;
    boolean DEBUG = false;
    private boolean kill;

    public EchoThread(Socket clientSocket, MessageHandler h) {
        this.socket = clientSocket;
        this.handler = h;
        this.kill = false;
    }

    public void run() {
        DataInputStream inFromClient;
        
        try {
            inFromClient = new DataInputStream(socket.getInputStream());
            outToClient = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            if (DEBUG) e.printStackTrace();
            return;
        }

        while (true) {
            try {
                String line = inFromClient.readUTF();
                if ((line == null) || line.equals("QUIT")) {
                    socket.close();
                    return;
                } else if (line.equals("KILLTHESERVER")) {
                    this.kill = true;
                    return;
                } else {
                    System.out.println(line + "\n\r");
                    handler.addMessage(line);
                }
            } catch (IOException e) {
                if (DEBUG) e.printStackTrace();
                return;
            }
        }
    }

    public void sendBack(String message) {
        try {
            if (DEBUG) System.out.print("Sending back...");
            outToClient.writeUTF(message);
            if (DEBUG) System.out.println(" Done!");
        } catch (IOException e) {
            if (DEBUG) e.printStackTrace();
        }
    }

    public boolean killServer() {
        return this.kill;
    }
}