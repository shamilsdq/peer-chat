package com.shamilsdq.peerchat;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;



public class NetworkController 
{
    
    private final int PORT = 5023;
    private final int BUFFER_SIZE = 2048;
    
    private byte[] buffer;
    private DatagramSocket socket;
    private DatagramPacket packet;
    
    
    public NetworkController() throws SocketException
    {
        this.buffer = new byte[BUFFER_SIZE];
        this.socket = new DatagramSocket(PORT);
        this.packet = new DatagramPacket(this.buffer, this.buffer.length);
    }
    
    
    public void sendMessage(String chatter, String content) 
            throws SocketException, UnknownHostException, IOException
    {
        byte[] temp = content.getBytes();
        this.packet.setData(temp);
        this.packet.setAddress(InetAddress.getByName(chatter));
        this.packet.setPort(PORT);
        this.socket.send(this.packet);
    }
    
    public void receiveMessage() throws IOException
    {
        this.socket.receive(this.packet);
        String chatter = this.packet.getAddress().getHostName();
        String message = new String(this.packet.getData(), 0, this.packet.getLength());
        
        App.recieveMessage(chatter, message);
    }
    
}
