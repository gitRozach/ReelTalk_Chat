package controller;


import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import gui.client.components.layouts.LoadableStackPane;
import gui.client.components.messageField.EmojiCategory;
import gui.client.components.messageField.EmojiSkinColor;
import gui.client.components.messageField.EmojiTabPane;
import gui.client.components.messageField.EmojiTextField;
import gui.client.components.messageField.messageFieldItems.EmojiMessageItem;
import gui.client.components.messages.GUIMessage;
import gui.client.views.ClientChatView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.client.eventHandlers.ObjectEvent;
import network.client.eventHandlers.ObjectEventHandler;
import network.ssl.client.SecuredChatClient;
import network.ssl.communication.ByteMessage;
import network.ssl.communication.MessagePacket;
import network.ssl.communication.events.ChannelMessageEvent;
import network.ssl.communication.events.ClientEvent;
import network.ssl.communication.requests.ChannelMessageRequest;
import network.ssl.server.SecuredChatServer;

public class ReelTalkSession extends Application {
	private static final String HOST_PROTOCOL = "TLSv1.2";
	private static final String HOST_ADDRESS = "localhost";
	private static final int HOST_PORT = 2199;
	
	private Stage window;
	private LoadableStackPane rootPane;
	private ClientChatView chatView;
	
	private SecuredChatServer chatServer;
	private SecuredChatClient chatClient;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		initialize(primaryStage);
		startServer();
		startClient();
		loadView(chatView);
	}
	
	private void startServer() throws Exception {
		chatServer.start();
	}
	
	private void startClient() throws Exception {
		chatClient.connect();
	}
	
	public void closeAll() {
		try {
			chatClient.disconnect();
			chatServer.stop();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initialize(Stage stage) throws Exception {
		initServer();
		initChatView();
		initRootPane();
		initClient();
		initEventHandlers();
		initStage(stage);
		initFonts();
		window.show();
	}
	
	private void initServer() throws Exception {
		chatServer = new SecuredChatServer(HOST_PROTOCOL, HOST_ADDRESS, HOST_PORT);
	}
	
	private void initClient() throws Exception {
		chatClient = new SecuredChatClient(HOST_PROTOCOL, HOST_ADDRESS, HOST_PORT);
	}
	
	private void initStage(Stage stage) {
		window = stage;
		window.setOnCloseRequest(a -> closeAll());
		window.setTitle("ReelTalk - Launcher");
		window.setScene(new Scene(rootPane, 1000d, 800d));
	}
	
	private void initFonts() {
		try {
			InputStream fontStream = getClass().getResourceAsStream("/resources/fonts/OpenSansEmoji.ttf");
			Font inputFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(inputFont);
			chatView.getMessageInputField().getTextField().getInputField().setFont(javafx.scene.text.Font.loadFont(fontStream, 15f));
		} 
		catch (FontFormatException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initRootPane() {
		rootPane = new LoadableStackPane();
		rootPane.initialize();
	}
	
	private void initChatView() {
		chatView = new ClientChatView(true, window);
	}
	
	private void initEventHandlers() {
		chatClient.setOnMessageReceived(new ObjectEventHandler<ByteMessage>() {
			@Override
			public void handle(ObjectEvent<ByteMessage> event) {
				if(event != null && event.getAttachedObject() != null)
					handleClientReception(event.getAttachedObject());
			}
		});
		
		chatClient.setOnMessageSent(new ObjectEventHandler<ByteMessage>() {
			@Override
			public void handle(ObjectEvent<ByteMessage> event) {
				
			}
		});
		
		chatView.getMessageInputField().setOnEnterPressed(a -> onInputFieldEnterPressed());
		chatView.getMessageInputField().getTextField().setOnEnterPressed(b -> onInputFieldEnterPressed());
		
		chatView.getMessageInputField().setOnEmojiPressed(new ObjectEventHandler<String>() {
			@Override
			public void handle(ObjectEvent<String> event) {
				onEmojiPressed(event);
			}
		});
		
		chatView.getMessageInputField().getEmojiPane().getEmojiSkinChooser().setOnSkinChooserClicked(c -> onSkinChooserClicked());
	}
	
	private void handleClientReception(ByteMessage byteMessage) {
		MessagePacket reception = MessagePacket.deserialize(byteMessage.getMessageBytes());
		if(reception == null)
			return;
		if(reception instanceof ClientEvent)
			handleClientEvent((ClientEvent)reception);
	}
	
	private void handleClientEvent(ClientEvent event) {
		if(event == null)
			return;
		if(event instanceof ChannelMessageEvent) {
			ChannelMessageEvent channelMessage = (ChannelMessageEvent) event;
			chatView.getMessageView().addMessageAnimated(new GUIMessage(channelMessage.getSender(), channelMessage.getMessage()));
		}
	}
	
	public void loadView(Node view) {
		rootPane.loadContent(view, 1500L);
	}
	
	private void onInputFieldEnterPressed() {
		chatClient.sendMessage(new ChannelMessageRequest("Rozach", "testo", 0, chatView.getMessageInputField().getText()));
		chatView.getMessageInputField().getTextField().clear();
	}
	
	private void onEmojiPressed(ObjectEvent<String> event) {		
		EmojiTextField emojiTextField = chatView.getMessageInputField().getTextField();
		String smileyText = event.getAttachedObject();
		
		String currentText = emojiTextField.getCurrentText();
		int currentTextPos = emojiTextField.getOldCaretPosition();
		
		System.out.println("Current Text: " + currentText);
		System.out.println("Current Pos: " + currentTextPos);
		
		if(!currentText.isEmpty()) {
			String firstWord = currentText.substring(0, currentTextPos);
			String secondWord = currentText.substring(currentTextPos);
			
			System.out.println("First Word: " + firstWord);
			System.out.println("Second Word: " + secondWord);
			
			if(!firstWord.isEmpty())
				emojiTextField.addText(firstWord);
			emojiTextField.addItem(new EmojiMessageItem("/resources/smileys/category" + smileyText.charAt(0) + "/" + smileyText + ".png", smileyText));
			if(!secondWord.isEmpty())
				emojiTextField.addText(secondWord);
		}			
		else
			emojiTextField.addItem(new EmojiMessageItem("/resources/smileys/category" + smileyText.charAt(0) + "/" + smileyText + ".png", smileyText));
		emojiTextField.getInputField().requestFocus();	
	}

	private void onSkinChooserClicked() {
		EmojiTabPane emojiTabPane = chatView.getMessageInputField().getEmojiPane();
		for (int i = 0; i < EmojiCategory.values().length; i++)
			emojiTabPane.initSmileys(EmojiCategory.getByInt(i), EmojiSkinColor.getByInt(emojiTabPane.getEmojiSkinChooser().getCurrentColorIndex()), true);
	}
	
}
