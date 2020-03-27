package controller;


import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import com.google.protobuf.GeneratedMessageV3;

import gui.components.messageField.EmojiCategory;
import gui.components.messageField.EmojiSkinColor;
import gui.components.messageField.EmojiTabPane;
import gui.components.messageField.EmojiTextField;
import gui.components.messageField.items.EmojiMessageItem;
import gui.components.messages.GUIMessage;
import gui.layouts.LoadableStackPane;
import gui.views.client.ClientChatView;
import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.messages.ProtobufMessage;
import network.peer.client.ReelTalkClient;
import network.peer.server.ReelTalkServer;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientRequests.ChannelMessagePostRequest;
import protobuf.wrapper.ClientRequests;

public class ReelTalkSession extends Application {
	private static final String HOST_PROTOCOL = "TLSv1.2";
	private static final String HOST_ADDRESS = "localhost";
	private static final int HOST_PORT = 2199;
	
	private Stage window;
	private LoadableStackPane rootPane;
	private ClientChatView chatView;
	
	private ReelTalkServer chatServer;
	private ReelTalkClient chatClient;
	
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
		if(chatClient.connect()) {
			chatClient.sendMessage(ClientRequests.newLoginRequest(1, "TestoRozach", "rozachPass"));
		}
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
		chatServer = new ReelTalkServer(HOST_PROTOCOL, HOST_ADDRESS, HOST_PORT);
	}
	
	private void initClient() throws Exception {
		chatClient = new ReelTalkClient(HOST_PROTOCOL, HOST_ADDRESS, HOST_PORT);
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
		chatClient.setOnMessageReceived(new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {
				if(event != null && event.getAttachedObject() != null)
					handleClientReception(event.getAttachedObject());
			}
		});
		
		chatClient.setOnMessageSent(new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {
				
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
	
	private void handleClientReception(ProtobufMessage byteMessage) {
		if(byteMessage.hasMessage())
			handleClientEvent(byteMessage.getMessage());
	}
	
	private void handleClientEvent(GeneratedMessageV3 event) {
		if(event == null)
			return;
		if(event instanceof ChannelMessagePostEvent) {
			ChannelMessagePostEvent channelMessage = (ChannelMessagePostEvent) event;
			chatView.getMessageView().addMessageAnimated(new GUIMessage(channelMessage	.getMessage(0)
																						.getMessageBase()
																						.getSenderUsername(), 
																		channelMessage	.getMessage(0)
																						.getMessageBase()
																						.getMessageText()));
		}
	}
	
	public void loadView(Node view) {
		rootPane.loadContent(view, 1500L);
	}
	
	private void onInputFieldEnterPressed() {
		ChannelMessagePostRequest request = ClientRequests.newChannelMessagePostRequest(1, "TestoRozach", "rozachPass", 1, chatView.getMessageInputField().getText());
		chatClient.sendMessage(request);
		chatView.getMessageInputField().getTextField().clear();
	}
	
	private void onEmojiPressed(ObjectEvent<String> event) {		
		EmojiTextField emojiTextField = chatView.getMessageInputField().getTextField();
		String smileyText = event.getAttachedObject();
		
		String currentText = emojiTextField.getCurrentText();
		int currentTextPos = emojiTextField.getOldCaretPosition();
		
		if(!currentText.isEmpty()) {
			String firstWord = currentText.substring(0, currentTextPos);
			String secondWord = currentText.substring(currentTextPos);
			
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
