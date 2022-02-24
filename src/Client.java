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
                    8989 //Port Number of server)
            );
            datagramSocket.send(packet);
            while (!message.equalsIgnoreCase("LEAVE") || client.getMessageType() != 2) {

                /*
                String formattedMessage = "{\"messageType\":" + client.getMessageType() + ", \"username\":\"" + client.getUsername()
                        + "\", \"message\":\"" + client.getMessage() + "\"}";
                */

//                data = str_jsonForm.getBytes();
//
//                DatagramPacket packet = new DatagramPacket(
//                        data,
//                        data.length,
//                        InetAddress.getLocalHost(), //IP address of server
//                        8989 //Port Number of server
//                );

//                datagramSocket.send(packet);
                System.out.print("\nType 'POST' to send message or 'LEAVE' (without the apostrophes) to exit the chat: ");
                message = sc.next();
                message = message.toUpperCase();
                if (message.equalsIgnoreCase("POST")) {
                    client.setMessageType(1);
                    System.out.print("\nType in your message: ");
                    sc.nextLine();
                    message = sc.nextLine();
                    client.setMessage(message);
                } else if (message.equalsIgnoreCase("LEAVE")) {
                    System.out.println("Leaving the chat...");
                    client.setMessageType(2);
                    client.setMessage("LEAVE");
//                    str_jsonForm = gson.toJson(client);
//                    data = str_jsonForm.getBytes();
//                    packet.setData(data);
//                    packet.setLength(data.length);
                } else {
                    System.out.println("Unrecognized command");
                    continue;
                }
                str_jsonForm = gson.toJson(client);
                data = str_jsonForm.getBytes();
                packet.setData(data);
                packet.setLength(data.length);
                datagramSocket.send(packet);
            }
        }
        datagramSocket.close();
        System.out.println("Client - logging out");
    }

}
