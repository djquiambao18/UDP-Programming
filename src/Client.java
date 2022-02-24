import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Client {
    public static void main(String[] args) throws IOException {

        // socket - without port number, OS chooses a random one available

        Message client = new Message(); //instantiate message class
        DatagramSocket datagramSocket = new DatagramSocket();
        System.out.print("Enter username: "); //prompt user to enter a username
        Scanner sc = new Scanner(System.in); //prompt for user input
        client.setUserName(sc.nextLine()); //set client username to user input.
        System.out.print("\nType 'JOIN' (without the apostrophes) to join the chat: ");
        client.setMessage(sc.next());   //set message
        String message = client.getMessage(); // for convenience.
        //for input validation only: requires JOIN once.

        if(message.equalsIgnoreCase("JOIN")) {
            client.setMessageType(0);
            while (!message.equalsIgnoreCase("LEAVE")) {
                String formattedMessage =
                byte[] data = message.getBytes();


                DatagramPacket packet = new DatagramPacket(
                        data,
                        data.length,
                        InetAddress.getLocalHost(), //IP address of server
                        8989 //Port Number of server
                );
                datagramSocket.send(packet);
                System.out.print("\nType 'POST' to send message or 'LEAVE' (without the apostrophes) to exit the chat: ");
                message = sc.next();
                if(message.equalsIgnoreCase("LEAVE"))
                    datagramSocket.close();

            }
        }
        System.out.println("Client - logging out");
    }
}
