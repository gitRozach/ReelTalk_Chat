package controller;


import java.io.IOException;

import gui.client.components.layouts.LoadableStackPane;
import gui.client.views.ClientChatView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import network.ssl.client.SecuredChatClient;
import network.ssl.communication.MessagePacket;
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
		initStage(stage);
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
	
	private void initRootPane() {
		rootPane = new LoadableStackPane();
		rootPane.initialize();
	}
	
	private void initChatView() {
		chatView = new ClientChatView(true, window);
	}
	
	protected void onKeyPressed(KeyEvent event) {
		
	}
	
	private void onMessageReceived(MessagePacket message) {
		
	}
	
	private void onMessageSent(MessagePacket message) {
		
	}
	
	private void onMessageAcknowledged(MessagePacket message) {
		
	}
	
	private void onMessageFailedTimeout(MessagePacket message) {
		
	}
	
	private void onMessageFailedOffline(MessagePacket message) {
		
	}
	
	public void loadView(Node view) {
		rootPane.loadContent(view, 1500L);
	}

}
