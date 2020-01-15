package main;

import gui.client.views.ClientChat;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientChatTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage window) {
		ClientChat clientGui = new ClientChat(true, window);
		
		Scene scene = new Scene(clientGui, 1000d, 800d);
		
		window.setScene(scene);
		window.show();
	}

}
