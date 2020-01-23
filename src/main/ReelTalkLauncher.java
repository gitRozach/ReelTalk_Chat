package main;


import java.io.IOException;

import gui.client.components.layouts.LoadableStackPane;
import gui.client.views.ClientChatView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.ssl.client.SecuredChatClient;
import network.ssl.server.SecuredChatServer;

public class ReelTalkLauncher extends Application {
	private Stage window;
	private LoadableStackPane rootPane;
	
	private static final String HOST_PROTOCOL = "TLSv1.2";
	private static final String HOST_ADDRESS = "localhost";
	private static final int HOST_PORT = 2199;
	private SecuredChatServer chatServer;
	
	private SecuredChatClient chatClient;
	
//	private LoginView loginView;
	private ClientChatView chatView;
//	private ServerHostView hostView;
	
	
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
		Thread.sleep(100L);
	}
	
	private void startClient() throws Exception {
		chatClient.connect();
		Thread.sleep(100L);
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
		chatClient.setChatView(chatView);
		chatView.setClient(chatClient);
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
	
	public void loadView(Node view) {
		rootPane.loadContent(view, 1500L);
	}

}
