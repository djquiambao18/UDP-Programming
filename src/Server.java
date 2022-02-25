import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server {
        public static void main(String[] args){
            int currentPort = 8989;
            //for keeping track of unique clients with "unique" userNames
            ConcurrentMap<String, DatagramPacket> datagramPackets = new ConcurrentHashMap<>();
            //for jsonify-ing/stringify-ing to/from Java object.
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            Message clientMsg; //for use with handling client data
            try {
                DatagramSocket datagramSocket = new DatagramSocket(currentPort);
                final int buffer_length = 1000;
                //do while guarantees at least run once.
                do {

                    //buffer to receive a message

                    //provides connectionless service provided by UDP
                    DatagramPacket buffer = new DatagramPacket(new byte[buffer_length], buffer_length);

                    //receive client message
                    datagramSocket.receive(buffer); //throws IOException

                    //remove unnecessary part of the buffer (len = 1000)
                    byte[] message = Arrays.copyOfRange(
                            buffer.getData(),
                            0,
                            buffer.getLength());

                    //byte stream as an input -> string:
                    String str_message = new String(message);
                    clientMsg = gson.fromJson(str_message, Message.class);

                    switch (clientMsg.getMessageType()) {
                        //JOIN only happens once. If this is reattempted/bypassed (from client side code manipulation) server does not re-add client
                        case 0 -> {
                                //redundancy validation: if user already joined server, then will prompt message and skips the loop for its current iteration.
                                if(datagramPackets.containsKey(clientMsg.getUsername())){
                                    System.out.println("Already joined the server");
                                    continue;
                                }
//                            if(datagramPackets.containsKey(clientMsg.getUsername())){
//                                DatagramSocket server_ds = new DatagramSocket();
//                                byte [] server_message = new String("Username already taken. Pick a different username!").getBytes();
//                                DatagramPacket server_pack = new DatagramPacket(new byte[buffer_length])
//                            }
                            System.out.println(clientMsg.getUsername() + " with IP: " + buffer.getAddress() + " and port# " + buffer.getPort() + " has joined the chat!");
                            datagramPackets.put(clientMsg.getUsername(), buffer);
                        }
                        //POST
                        case 1 -> {
                            System.out.println(clientMsg.getUsername() + " SAYS: " + clientMsg.getMessage());
                        }
                        //LEAVE
                        case 2 -> {
                            System.out.println(clientMsg.getUsername() + " has left the chat.");
                            System.out.println("Client IP: " + datagramPackets.get(clientMsg.getUsername()).getAddress()
                                    + " and Port #: "
                                    + datagramPackets.get(clientMsg.getUsername()).getPort() + " removed.");
                            datagramPackets.remove(clientMsg.getUsername());
                        }
                        default -> {
                            System.out.println("Unrecognized message type!");
                        }
                    }

                } while (!datagramPackets.isEmpty()); //ends loop if client list is empty. shutting down the server afterwards.
                System.out.println("Server shutting down...");
                datagramSocket.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
}
