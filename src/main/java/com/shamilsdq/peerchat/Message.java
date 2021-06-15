package com.shamilsdq.peerchat;



public class Message 
{
    
    private final String content;
    private final boolean isSent;
    
    
    public Message(String content, boolean isSent)
    {
        this.content = content;
        this.isSent = isSent;
    }
    
    
    public String getContent()
    {
        return this.content;
    }
    
    public boolean getIsSent()
    {
        return this.isSent;
    }
    
}
