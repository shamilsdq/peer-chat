package com.shamilsdq.peerchat;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;



public class GUIController 
{

    // SIDE PANE ELEMENTS
    @FXML private Button chatterAddButton;
    @FXML private VBox chatterListVBox;
    
    // MAIN PANE ELEMENTS
    @FXML private Label chatterIpLabel;
    @FXML private VBox messageListVBox;
    @FXML private TextField messageInputTextField;
    @FXML private Button messageSendButton;
    
    private String chatter;
    private Chat chat;
    
    
    public void initialize() 
    {
        // disable message input by default
        this.messageInputTextField.setDisable(true);
        this.messageSendButton.setDisable(true);
        
        // add actions to buttons
        this.chatterAddButton.setOnAction(new ChatterAddButtonHandler());
        this.messageSendButton.setOnAction(new MessageSendButtonHandler());
    }
    
    
    public String getChatter()
    {
        return this.chatter;
    }
    
    public String getMessage()
    {
        String message = this.messageInputTextField.getText();
        this.messageInputTextField.clear();
        return message;
    }
    
    
    public void addChatter(String chatterIp) 
    {
        Button chatterButton = new Button(chatterIp);
        chatterButton.setOnAction(new ChatterButtonHandler(chatterIp));
        chatterButton.getStyleClass().add("chatterButton");
        this.chatterListVBox.getChildren().add(chatterButton);
    }
    
    public void addMessage(String chatter, Message msg) 
    {
        if (this.chatter.equals(chatter) == false) 
            return;
        
        HBox hbox = new HBox();
        Label label = new Label(msg.getContent());
        
        if (msg.getIsSent())
        {
            hbox.getStyleClass().add("messageWrapperRight");
            label.getStyleClass().add("messageRight");
        }
        else
        {
            hbox.getStyleClass().add("messageWrapperLeft");
            label.getStyleClass().add("messageLeft");
        }
            
        hbox.getChildren().add(label);
        this.messageListVBox.getChildren().add(hbox);
    }
    
    public void showChat(String newChatter, Chat newChat) 
    {
        if (this.chatter != null && this.chatter.equals(newChatter))
            return;
        
        this.chatter = newChatter;
        this.chat = newChat;
        
        // update chatter label
        this.chatterIpLabel.setText(this.chatter);
        
        // replace messages
        this.messageListVBox.getChildren().clear();
        ArrayList<Message> messages = this.chat.getMessages();
        
        for (int i = 0; i < messages.size(); ++i) 
        {
            this.addMessage(this.chatter, messages.get(i));
        }
        
        // enable fields
        this.messageInputTextField.setDisable(false);
        this.messageSendButton.setDisable(false);
    }
    
    // ALERTS
    
    public void showError(String error)
    {
        Alert alert = new Alert(AlertType.ERROR, error, ButtonType.OK);
        alert.showAndWait();
    }
    
    public void showWarning(String warning)
    {
        Alert alert = new Alert(AlertType.WARNING, warning, ButtonType.OK);
        alert.showAndWait();
    }
    
    // EVENT HANDLERS
    
    class ChatterAddButtonHandler implements EventHandler<ActionEvent> 
    {
        private String pattern;
        
        public ChatterAddButtonHandler()
        {
            pattern = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])" 
                    + "\\.(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])" 
                    + "\\.(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])" 
                    + "\\.(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
        }
        
        @Override
        public void handle(ActionEvent e)
        {
            TextInputDialog dialog = new TextInputDialog("0.0.0.0");
            dialog.setHeaderText("New Chatter");
            dialog.setContentText("Chatter IP");
            
            dialog.showAndWait();
            String newChatterAddress = dialog.getEditor().getText();
            
            if (newChatterAddress.matches(pattern))
                App.addChatter(newChatterAddress);
        }
    }
    
    class ChatterButtonHandler implements EventHandler<ActionEvent>
    {
        private String chatter;
        
        public ChatterButtonHandler(String chatter)
        {
            this.chatter = chatter;
        }
        
        @Override
        public void handle(ActionEvent e)
        {
            App.showChatter(this.chatter);
        }
    }
    
    class MessageSendButtonHandler implements EventHandler<ActionEvent>
    {      
        @Override
        public void handle(ActionEvent e)
        {
            // handle message send button click
            App.sendMessage();
        }
    }
            
}
