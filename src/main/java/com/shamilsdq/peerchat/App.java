package com.shamilsdq.peerchat;

import java.util.HashMap;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class App extends Application 
{
    private static Scene scene;
    private static GUIController gui;
    private static NetworkController network;
    
    private static Task receiveMessageTask;
    private static Task sendMessageTask;
    
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
        
        // default chat with self
        addChatter("127.0.0.1");
        showChat("127.0.0.1");
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
            receiveMessageTask = new Task<Void>() {
                @Override
                protected Void call() {
                    int count = 0;
                    while (true)
                    {
                        if (isCancelled()) 
                            break;
                        
                        try
                        {
                            count += 1;
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
                    return null;
                }
            };
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                receiveMessageTask.cancel();
            }));
            new Thread(receiveMessageTask).start();
        }
        catch (SocketException ex)
        {
            System.out.println(ex);
            gui.showError("Socket Exception encountered");
            Platform.exit();
        }
    }

    
    public static void addChatter(String chatter)
    {   
        if (table.containsKey(chatter) == false)
        {
            table.put(chatter, new Chat());
            gui.addChatter(chatter);
        }
    }
    
    public static void showChat(String chatter) 
    {
        if (table.containsKey(chatter))
            gui.showChat(chatter, table.get(chatter));
    }
   
    
    public static void sendMessage()
    {
        String chatter = gui.getChatter();
        String messageContent = gui.getMessage();
        
        if (messageContent.trim().length() == 0) return;

        sendMessageTask = new Task<Void>() {
            @Override
            protected Void call() {
                try 
                {
                    network.sendMessage(chatter, messageContent);
                }
                catch (SocketException ex) 
                {
                    System.out.println(ex);
                    gui.showWarning("Socket Exception occurred");
                }
                catch (UnknownHostException ex) 
                {
                    System.out.println(ex);
                    gui.showWarning("UnknowHost Exception occurred");
                }
                catch (IOException ex) 
                {
                    System.out.println(ex);
                    gui.showWarning("IO Exception occurred");
                }
                return null;
            }
        };
        
        new Thread(sendMessageTask).start();
            
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
            System.out.flush();
            gui.addMessage(chatter, message);
        }
        
        System.out.flush();
    }
    
    
    @Override
    public void stop()
    {
        if (receiveMessageTask != null && receiveMessageTask.isRunning())
            receiveMessageTask.cancel();
        network.close();
    }
    
    
    public static void main(String[] args) 
    {
        launch();
    }

}