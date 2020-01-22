package main;

import gui.client.views.ClientChatView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.ssl.client.SecuredChatClient;

public class ClientLauncher extends Application{
	private Stage window;
	private Scene scene;
	private ClientChatView clientUI;
	private SecuredChatClient client;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		initAndShow(stage);
	}
	
	private void initAndShow(Stage stage) {
		init(stage, true);
	}
	
	private void init(Stage stage, boolean show) {
		initClientUI();
		initScene();
		initWindow(stage);
		initClient();
		if(show)
			window.show();
	}
	
	private void initClientUI() {
		clientUI = new ClientChatView(true);
	}
	
	private void initClient() {
		try {
			client = new SecuredChatClient("TLSv1.2", "localhost", 2122);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initScene() {
		scene = new Scene(clientUI, 1000d, 800d);
	}
	
	private void initWindow(Stage stage) {
		window = stage;
		clientUI.attachStage(stage);
		window.setScene(scene);
		window.setTitle("ReelTalk - Launcher");
	}
}
