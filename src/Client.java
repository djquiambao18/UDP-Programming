import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {

        //Persistent variables and initial input from user:
        Message client = new Message(); //instantiate message class
        DatagramSocket datagramSocket = new DatagramSocket();
        System.out.print("Enter username: "); //prompt user to enter a username
        Scanner sc = new Scanner(System.in); //prompt for user input
        client.setUsername(sc.nextLine()); //set client username to user input.
        System.out.print("\nType 'JOIN' (without the apostrophes) to join the chat: ");
        client.setMessage(sc.nextLine());   //set message
        String message = client.getMessage(); // for convenience.

        //Instantiate thread for use with client receiving message
        ClientReceiverThread th_cReceive = new ClientReceiverThread();

        //instantiates helper objects and attributes when client asks to 'JOIN'
        //for input validation only: requires JOIN once.
        if (message.equalsIgnoreCase("JOIN")) {
            client.setMessageType(0);
            //Gson library helps with convenience in formatting string to/from JSON.
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String str_jsonForm = gson.toJson(client);
            //get bytes of JSON form:
            byte[] data = str_jsonForm.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getLocalHost(), //IP address of server
                    8989 //Port Number of server
            );
            datagramSocket.send(packet);
            //th_cReceive.port = datagramSocket.getLocalPort();
            th_cReceive.start();
            ClientReceiverThread.port++;
            th_cReceive.start();
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
        th_cReceive.interrupt();
        datagramSocket.close(); //close client after client asks to leave.
        System.out.println("Client - logging out");

    }

    //Additional thread to keep receiving messages from the server.
    static class ClientReceiverThread extends Thread {
        static int port = 8001;
        public void run(){
            final int buffer_length = 1024;
            Message messageReceived;
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            byte [] c_receiver;
            DatagramPacket c_packet;
            try{
                DatagramSocket client_ds = new DatagramSocket(port);
                //keep receiving until thread is interrupted.
                while(!interrupted()){
                        c_packet = new DatagramPacket(new byte[buffer_length], buffer_length);
                        client_ds.receive(c_packet);
                        client_ds.getBroadcast();
                        c_receiver = Arrays.copyOfRange(c_packet.getData(), 0, c_packet.getLength());
                        String str_msg = new String(c_receiver);
                        messageReceived = gson.fromJson(str_msg, Message.class);
                            System.out.println(messageReceived.getUsername() + " SAYS: "
                                    + messageReceived.getMessage());

                    }
                    client_ds.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }

        }

    }

}
