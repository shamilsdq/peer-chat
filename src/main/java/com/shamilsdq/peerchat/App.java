package com.shamilsdq.peerchat;

import java.util.HashMap;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class App extends Application 
{
    private static Scene scene;
    private static GUIController gui;
    private static NetworkController network;
    
    private static HashMap<String, Chat> table;
    

    @Override
    public void start(Stage stage) throws IOException 
    {
        table = new HashMap<String, Chat>();
        
        initGUI();
        initNetwork();
        
        // display GUI
        stage.setScene(scene);
        stage.setTitle("PEER-CHAT");
        stage.setMinWidth(800.0);
        stage.setMinHeight(560.0);
        stage.show();
    }
    
    
    private void initGUI() throws IOException 
    {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("GUI.fxml"));
        scene = new Scene((Parent) fxmlLoader.load());
        gui = (GUIController) fxmlLoader.getController();
    }
    
    private void initNetwork() 
    {
        try 
        {
            network = new NetworkController();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true)
                    {
                        try
                        {
                            System.out.println("waiting for message");
                            network.receiveMessage();
                        }
                        catch (SocketException ex) 
                        {
                            System.out.println(ex);
                            gui.showWarning("Socket Exception occurred");
                        }
                        catch (IOException ex)
                        {
                            System.out.println(ex);
                            gui.showWarning("IO Exception occurred");
                        }
                    }
                }
            });
        }
        catch (SocketException ex)
        {
            gui.showError("Socket Exception encountered");
            Platform.exit();
        }
        // TODO: initialize Network Components
    }

    
    public static void addChatter(String chatter)
    {   
        if (table.containsKey(chatter) == false)
        {
            table.put(chatter, new Chat());
            gui.addChatter(chatter);
        }
    }
    
    public static void showChatter(String chatter) 
    {
        if (table.containsKey(chatter))
            gui.showChat(chatter, table.get(chatter));
    }
   
    
    public static void sendMessage()
    {
        String chatter = gui.getChatter();
        String messageContent = gui.getMessage();
        
        if (messageContent.trim().length() == 0) return;
        
        try 
        {
            network.sendMessage(chatter, messageContent);
        }
        catch (SocketException ex) 
        {
            gui.showWarning("Socket Exception encountered");
            return;
        }
        catch (UnknownHostException ex)
        {
            gui.showWarning("Unknown Host Exception encountered");
            return;
        }
        catch (IOException ex) 
        {
            gui.showWarning("IO Exception encountered");
            return;
        }
            
        Message message = new Message(messageContent, true);
        table.get(chatter).addMessage(message);
        gui.addMessage(chatter, message);
    }
    
    public static void recieveMessage(String chatter, String messageContent)
    {
        // wil add new chatter if does not exists
        App.addChatter(chatter);
        
        Message message = new Message(messageContent, false);
        table.get(chatter).addMessage(message);
        
        if (gui.getChatter().equals(chatter))
        {
            gui.addMessage(chatter, message);
        }
    }
    
    
    public static void main(String[] args) 
    {
        launch();
    }

}