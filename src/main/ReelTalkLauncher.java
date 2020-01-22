package main;


import gui.client.components.layouts.LoadableStackPane;
import gui.client.views.ClientChatView;
import gui.client.views.LoginView;
import gui.client.views.ServerHostView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReelTalkLauncher extends Application {
	private Stage window;
	private LoadableStackPane rootPane;
	
	private LoginView loginView;
	private ClientChatView chatView;
	private ServerHostView hostView;
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		initialize(primaryStage);
		loadView(chatView);
	}
	
	public void initialize(Stage stage) {
		initChatView();
		initRootPane();
		initStage(stage);
		window.show();
	}
	
	private void initStage(Stage stage) {
		window = stage;
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
