import java.io.*;

public class Server {
	public static void main(String[] args) {
		Server server = new Server();
		try {
			server.test();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	void test() throws IOException {
		int port = 11111;
		java.net.ServerSocket serverSocket = new java.net.ServerSocket(port);
		java.net.Socket client = warteAufAnmeldung(serverSocket);
		String nachricht = leseNachricht(client);
		System.out.println(nachricht);
		schreibeNachricht(client, "Hallo Client!");
	}

	java.net.Socket warteAufAnmeldung(java.net.ServerSocket serverSocket) throws IOException {
		java.net.Socket socket = serverSocket.accept(); // blockiert, bis sich ein Client angemeldet hat
		return socket;
	}

	String leseNachricht(java.net.Socket socket) throws IOException {
		BufferedReader bufferedReader =
			new BufferedReader(
			new InputStreamReader(
			socket.getInputStream()));
		char[] buffer = new char[200];
		int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
		String nachricht = new String(buffer, 0, anzahlZeichen);
		return nachricht;
	}

	void schreibeNachricht(java.net.Socket socket, String nachricht) throws IOException {
		PrintWriter printWriter =
			new PrintWriter(
			new OutputStreamWriter(
			socket.getOutputStream()));
		printWriter.print(nachricht);
		printWriter.flush();
	}
}
