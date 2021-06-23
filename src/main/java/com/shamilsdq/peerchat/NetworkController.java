package com.shamilsdq.peerchat;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;



public class NetworkController 
{
    
    private final int PORT = 5023;
    private final int TIMEOUT_MS = 50;
    private final int BUFFER_SIZE = 2048;
    
    private byte[] buffer;
    private DatagramSocket socket;
    private DatagramPacket packet;
    
    private boolean RECEIVELOCK;
    private boolean SENDLOCK;
    
    
    public NetworkController() throws SocketException
    {
        this.buffer = new byte[BUFFER_SIZE];
        this.socket = new DatagramSocket(PORT);
        this.socket.setSoTimeout(TIMEOUT_MS);
        this.packet = new DatagramPacket(this.buffer, this.buffer.length);
        
        this.RECEIVELOCK = false;
        this.SENDLOCK = false;
    }
    
    
    public void sendMessage(String chatter, String content) 
            throws SocketException, UnknownHostException, IOException
    {   
        byte[] temp = content.getBytes();
        this.packet.setData(temp);
        this.packet.setAddress(InetAddress.getByName(chatter));
        this.packet.setPort(PORT);
        
        this.SENDLOCK = true; // send operation intended
        while (this.RECEIVELOCK); // wait until receive operation is over
        this.socket.send(this.packet);
        this.SENDLOCK = false; // send operation completed
    }
    
    public void receiveMessage() throws IOException, SocketTimeoutException
    {
        while (this.SENDLOCK); // wait until send operation is completed
        this.RECEIVELOCK = true; // receive operation started
        try
        {
            this.socket.receive(this.packet);
            String chatter = this.packet.getAddress().getHostName();
            String message = new String(this.packet.getData(), 0, this.packet.getLength());
            App.recieveMessage(chatter, message);
        }
        catch (SocketTimeoutException ex){}
        this.RECEIVELOCK = false; // receive operation completed
        
        try 
        {
            Thread.sleep(10);
        }
        catch (Exception ex) {}
        
    }
    
    
    public void close()
    {
        this.socket.close();
    }
}
