import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class MessageTest
{
    public static void main(String[] args)
    {
        String[] messageList = new String[] {
            "Test Start x", 
            "A multiline text with gibberish to check how the message ui holds up. Don't mind me :)", 
            "Blah blah blah",
            "Test End x"
        };

        try {
            DatagramSocket socket = new DatagramSocket(5024);

            byte[] buff = new byte[10];
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            packet.setAddress(InetAddress.getByName("127.0.0.1"));
            packet.setPort(5023);

            for (int i = 0; i < messageList.length; i++) {
                System.out.println("Sending message " + i);
                byte[] temp = new byte[messageList[i].getBytes().length];
                temp = messageList[i].getBytes();
                packet.setData(temp, 0, temp.length);
                socket.send(packet);

                System.out.println(new String(packet.getData()));
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}