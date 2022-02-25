import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {

        // socket - without port number, OS chooses a random one available
        Message client = new Message(); //instantiate message class
        DatagramSocket datagramSocket = new DatagramSocket();
        System.out.print("Enter username: "); //prompt user to enter a username
        Scanner sc = new Scanner(System.in); //prompt for user input
        client.setUsername(sc.nextLine()); //set client username to user input.
        System.out.print("\nType 'JOIN' (without the apostrophes) to join the chat: ");
        client.setMessage(sc.nextLine());   //set message
        String message = client.getMessage(); // for convenience.
        //for input validation only: requires JOIN once.

        //instantiates helper objects and attributes when client asks to 'JOIN'
        if (message.equalsIgnoreCase("JOIN")) {
            client.setMessageType(0);
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String str_jsonForm = gson.toJson(client);
            byte[] data = str_jsonForm.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getLocalHost(), //IP address of server
                    8989 //Port Number of server
            );
            datagramSocket.send(packet);

            while (!message.equalsIgnoreCase("LEAVE") || client.getMessageType() != 2) {

                System.out.print("\nType 'POST' to send message or 'LEAVE' (without the apostrophes) to exit the chat: ");
                message = sc.next();
                message = message.toUpperCase();

                //POST
                if (message.equalsIgnoreCase("POST")) {
                    //update appropriate attributes of Message object.
                    client.setMessageType(1);
                    System.out.print("\nType in your message: ");
                    sc.nextLine(); // Required for scanner buffer, otherwise user cannot input message.
                    message = sc.nextLine();
                    client.setMessage(message);
                }
                //LEAVE
                else if (message.equalsIgnoreCase("LEAVE")) {
                    System.out.println("Leaving the chat...");
                    //update appropriate attributes of Message object.
                    client.setMessageType(2);
                    client.setMessage("LEAVE");
                } else {
                    System.out.println("Unrecognized command");
                    continue;
                }
                //JSONIFY->stringify client info then get Bytes to send over the network. guarantees that server receives "LEAVE" before exiting client.
                str_jsonForm = gson.toJson(client);
                data = str_jsonForm.getBytes();
                packet.setData(data);
                packet.setLength(data.length);
                datagramSocket.send(packet); //send the data over the network.
            }
        }
        datagramSocket.close(); //close client after client asks to leave.
        System.out.println("Client - logging out");
    }

}
