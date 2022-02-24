import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server {
        public static void main(String[] args){
            int currentPort = 8989;
            HashMap<Integer, DatagramPacket> datagramPackets = new HashMap<>();
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            Message clientMsg;
            try {
                DatagramSocket datagramSocket = new DatagramSocket(currentPort);
                final int buffer_length = 1000;
                while(true){
                    //datagramsocket: interface between app and udp

//                currentPort++;
                    //buffer to receive a message

                    //provides connectionless service provided by UDP
                    DatagramPacket buffer = new DatagramPacket(new byte[buffer_length], buffer_length);

                    datagramSocket.receive(buffer); //throws IOException
                    // 2nd Milestone:
                    // if join -> client list
                    // if post -> show it on terminal (as it is)
                    // if leave -> remove the client from the client list

                    //processing later
                    //extract the data (actual message)
                    //remove unnecessary part of the buffer (len = 1000)
                    byte[] message = Arrays.copyOfRange(
                            buffer.getData(),
                            0,
                            buffer.getLength());

                    //byte stream as an input -> string:
                    String str_message = new String(message);
                    clientMsg = gson.fromJson(str_message, Message.class);

                    //close the socket:
                    switch (clientMsg.getMessageType()) {
                        case 0 -> System.out.println(clientMsg.getUsername() + " with IP: " + buffer.getAddress() + " and port# " + buffer.getPort() + " has joined the chat!");
                        case 1 -> System.out.println(clientMsg.getUsername() + " SAYS: " + clientMsg.getMessage());
                        case 2 -> {
                            System.out.println(clientMsg.getUsername() + " has left the chat.");
                        }
                        default -> System.out.println("Unrecognized message type!");
                    }

//                    System.out.println("Message: " + clientMsg.toString());
                    if(clientMsg.getMessageType() == 2)
                        break;
                }
                System.out.println("Server shutting down...");
                datagramSocket.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
}
