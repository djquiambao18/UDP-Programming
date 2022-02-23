import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;


public class Server {
        public static void main(String[] args) throws IOException {
            int currentPort = 8989;
//            int offset = currentPort; //for offsetting array for client processes.
//            ArrayList<DatagramSocket>
            while (true) {
                //datagramsocket: interface between app and udp
                DatagramSocket datagramSocket = new DatagramSocket(currentPort);

                //buffer to receive a message
                final int buffer_length = 1000;
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
                System.out.println("Message: " + new String(message));

                //close the socket:
                datagramSocket.close();
            }
        }
}
