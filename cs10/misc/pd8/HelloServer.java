
import java.net.*;
import java.io.*;
import java.util.HashMap;

public class HelloServer {
	private static HashMap<String, String> dictionary = new HashMap<>();

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(4242);
		System.out.println("Server is waiting for connection...");

		while (true) {
			Socket clientSocket = serverSocket.accept();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.startsWith("GET ")) {
					String word = inputLine.substring(4).trim();
					String definition = dictionary.getOrDefault(word, "Word not found");
					out.println(definition);
				} else if (inputLine.startsWith("SET ")) {
					int firstSpace = inputLine.indexOf(" ");
					int secondSpace = inputLine.indexOf(" ", firstSpace + 1);
					if (secondSpace != -1) {
						String word = inputLine.substring(firstSpace + 1, secondSpace).trim();
						String definition = inputLine.substring(secondSpace + 1).trim();
						dictionary.put(word, definition);
						out.println("Word '" + word + "' defined");
					} else {
						out.println("Invalid SET command");
					}
				}
			}
			out.close();
			in.close();
			clientSocket.close();
		}
	}
}
