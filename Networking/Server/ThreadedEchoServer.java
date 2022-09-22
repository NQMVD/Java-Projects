import java.net.*;
import java.io.*;
import java.util.*;

public class ThreadedEchoServer {
    static final int PORT = 1234;
    static MessageHandler handler;
    static boolean DEBUG = false;
    static boolean killServer = false;;

    public static void main(String args[]) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        handler = new MessageHandler();

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            if (DEBUG) e.printStackTrace();
        }

        UpdateThread updater = new UpdateThread();
        updater.start();

        while (!killServer) {
            if (updater.killServer()) {
                killServer = true;
                return;
            }

            try {
                socket = serverSocket.accept();
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                if (DEBUG) e.printStackTrace();
            }

            updater.add(new EchoThread(socket, handler));
            handler.increase();
        }
    }

    public static class UpdateThread extends Thread {
        private ArrayList<EchoThread> threads;
        private boolean killServer;

        UpdateThread() {
            threads = new ArrayList<>();
        }

        public void run() {
            while (true) {
                for (EchoThread t : threads) {
                    if (t.killServer()) this.killServer = true;
                }

                if (handler.size() > 0) {
                    if (DEBUG) System.out.println("Handler has a Message! \n" + handler.info());
                    for (EchoThread t : threads) {
                        if (DEBUG) System.out.println("Seding to " + t.getName());
                        t.sendBack(handler.getMessage());
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    if (DEBUG) e.printStackTrace();
                }
            }
        }

        public void add(EchoThread echoThread) {
            threads.add(echoThread);
            threads.get(threads.size()-1).start();
        }

        public boolean killServer() {
            return this.killServer;
        }
    }
}