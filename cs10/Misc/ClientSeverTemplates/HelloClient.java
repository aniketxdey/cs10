
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class HelloClient {
    public static void main(String[] args) {
        String hostName = "127.0.0.1";
        int portNumber = 4242;

        try (Socket socket = new Socket(hostName, portNumber);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner stdIn = new Scanner(System.in)) {
             
            String userInput;
            System.out.println("Connected to dictionary server. Enter GET word to retrieve a definition or SET word definition to define a word.");
            while ((userInput = stdIn.nextLine()) != null) {
                out.println(userInput);
                System.out.println("Server response: " + in.readLine());
                System.out.println("Enter another command or 'exit' to quit:");
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
}
