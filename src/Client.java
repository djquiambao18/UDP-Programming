import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args) throws IOException {

        // socket - without port number, OS chooses a random one available

        DatagramSocket datagramSocket = new DatagramSocket();

        String message = "hello";
        byte[] data = message.getBytes();

        DatagramPacket packet = new DatagramPacket(
                data,
                data.length,
                InetAddress.getLocalHost(), //IP address of server
                8989 //Port Number of server
                );
        datagramSocket.send(packet);
        datagramSocket.close();

    }
}
