package gui.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import gui.animations.ScaleFadeAnimation;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.FXUtils;

public class LoginView extends StackPane {	
	private Stage parentWindow;
	
	private JFXTabPane tabPane;
	private Tab loginTab;
	private Tab registerTab;
	private Tab hostTab;
	private VBox loginContainer;
	private VBox registerContainer;
	private VBox hostContainer;
	
	private JFXTextField loginAddressField;
	private JFXTextField loginPortField;
	private JFXTextField loginUsernameField;
	private JFXPasswordField loginPasswordField;
	private JFXToggleButton loginConfigurePortButton;
	private JFXCheckBox loginRememberServerButton;
	private JFXCheckBox loginRememberLoginButton;
	private JFXButton loginButton;
	private JFXButton loginCancelButton;
	
	private JFXTextField registerAddressField;
	private JFXTextField registerPortField;
	private JFXTextField registerUsernameField;
	private JFXPasswordField registerPasswordField;
	private JFXPasswordField registerPasswordRepeatField;
	private JFXToggleButton registerConfigurePortButton;
	private JFXCheckBox registerRememberServerButton;
	private JFXCheckBox registerRememberLoginButton;
	private JFXButton registerButton;
	private JFXButton registerCancelButton;
	
	private JFXTextField hostPortField;
	private JFXComboBox<String> hostDirectories;
	private JFXCheckBox hostRememberDirectory;
	private JFXButton hostButton;
	private JFXButton hostCancelButton;
	
	private ScaleFadeAnimation showLoginBoxAnimation;
	private ScaleFadeAnimation showRegisterBoxAnimation;
	private ScaleFadeAnimation showHostBoxAnimation;
	
	private DoubleProperty minViewWidthProperty;
	private DoubleProperty minViewHeightProperty;
	private IntegerProperty defaultPortProperty;
	
	public LoginView() {
		this(false, null);
	}
	
	public LoginView(boolean initialize) {
		this(initialize, null);
	}
	
	public LoginView(Stage parentStage) {
		this(false, parentStage);
	}
	
	public LoginView(boolean initialize, Stage parentStage) {
		super();
		if(initialize)
			initialize();
		setParentWindow(parentStage);
	}
	
	public void onTabPaneItemClicked(int selectedItemIndex) {
		if(selectedItemIndex == 0)
			showLoginBox();
		else if(selectedItemIndex == 1)
			showRegisterBox();
		else if(selectedItemIndex == 2)
			showHostBox();
	}
	
	public void onConfigureLoginPortButtonClicked() {
		if(loginConfigurePortButton.isSelected()) {
			addLoginPortField();
			loginPortField.requestFocus();
			loginPortField.positionCaret(loginPortField.getText().length());
		}
		else {
			removeLoginPortField();
			setLoginPortText(Integer.toString(getDefaultPort()));
			JFXTextField textFieldToFocus = null;
			
			if(getLoginAddressText().isEmpty())
				textFieldToFocus = loginAddressField;
			else if(loginConfigurePortButton.isSelected())
				textFieldToFocus = loginPortField;
			else
				textFieldToFocus = loginUsernameField;
			if(textFieldToFocus != null) {
				textFieldToFocus.requestFocus();
				textFieldToFocus.positionCaret(textFieldToFocus.getText().length());
			}
		}
	}
	
	public void onConfigureRegisterPortButtonClicked() {
		if(registerConfigurePortButton.isSelected()) {
			addRegisterPortField();
			registerPortField.requestFocus();
			registerPortField.positionCaret(registerPortField.getText().length());
		}
		else {
			removeRegisterPortField();
			setRegisterPortText(Integer.toString(getDefaultPort()));
			JFXTextField textFieldToFocus = null;
			
			if(getRegisterAddressText().isEmpty())
				textFieldToFocus = registerAddressField;
			else if(registerConfigurePortButton.isSelected())
				textFieldToFocus = registerPortField;
			else
				textFieldToFocus = registerUsernameField;
			if(textFieldToFocus != null) {
				textFieldToFocus.requestFocus();
				textFieldToFocus.positionCaret(textFieldToFocus.getText().length());
			}
		}
	}
	
	public void initialize() {
		initStylesheets();
		initProperties();
		initLoginControls();
		initRegisterControls();
		initHostControls();
		initContainers();
		initAnimations();
		initRoot();
	}
	
	private void initAnimations() {
		showLoginBoxAnimation = new ScaleFadeAnimation();
		showLoginBoxAnimation.setFadeDuration(Duration.seconds(0.5d));
		showLoginBoxAnimation.setScaleDuration(Duration.seconds(0.3d));
		showLoginBoxAnimation.setScaleContent(loginContainer);
		showLoginBoxAnimation.setFadeContent(loginContainer);
		showLoginBoxAnimation.setFadeFromValue(0d);
		showLoginBoxAnimation.setFadeToValue(1d);
		showLoginBoxAnimation.setScaleFromValue(0.7d);
		showLoginBoxAnimation.setScaleToValue(1d);
		
		showRegisterBoxAnimation = new ScaleFadeAnimation();
		showRegisterBoxAnimation.setFadeDuration(Duration.seconds(0.5d));
		showRegisterBoxAnimation.setScaleDuration(Duration.seconds(0.3d));
		showRegisterBoxAnimation.setScaleContent(registerContainer);
		showRegisterBoxAnimation.setFadeContent(registerContainer);
		showRegisterBoxAnimation.setFadeFromValue(0d);
		showRegisterBoxAnimation.setFadeToValue(1d);
		showRegisterBoxAnimation.setScaleFromValue(0.7d);
		showRegisterBoxAnimation.setScaleToValue(1d);
		
		showHostBoxAnimation = new ScaleFadeAnimation();
		showHostBoxAnimation.setFadeDuration(Duration.seconds(0.5d));
		showHostBoxAnimation.setScaleDuration(Duration.seconds(0.3d));
		showHostBoxAnimation.setScaleContent(hostContainer);
		showHostBoxAnimation.setFadeContent(hostContainer);
		showHostBoxAnimation.setFadeFromValue(0d);
		showHostBoxAnimation.setFadeToValue(1d);
		showHostBoxAnimation.setScaleFromValue(0.7d);
		showHostBoxAnimation.setScaleToValue(1d);
	}
	
	private void showLoginBox() {
		if(showLoginBoxAnimation != null)
			showLoginBoxAnimation.play();
	}
	
	private void showRegisterBox() {
		if(showRegisterBoxAnimation != null)
			showRegisterBoxAnimation.play();
	}
	
	private void showHostBox() {
		if(showHostBoxAnimation != null)
			showHostBoxAnimation.play();
	}
	
	private void initRoot() {
		setAlignment(Pos.CENTER);
		setPadding(new Insets(150d, 200d, 0d, 200d));
		setMinWidth(getMinViewWidth());
		setMinHeight(getMinViewHeight());
		getChildren().add(tabPane);
	}
	
	private void initContainers() {
		initLoginContainers();
		initRegisterContainers();
		initHostContainers();
		initTabs();
		initTabPane();
	}
	
	private void initTabs() {
		loginTab = new Tab("Anmelden", loginContainer);
		registerTab = new Tab("Registrieren", registerContainer);
		hostTab = new Tab("Hosten", hostContainer);
	}
	
	private void initTabPane() {
		tabPane = new JFXTabPane();
		tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> onTabPaneItemClicked(newV.intValue()));
		tabPane.addEventFilter(MouseEvent.ANY, me -> {
			if(me.getTarget() == tabPane && me.getEventType() == MouseEvent.DRAG_DETECTED || me.getEventType() == MouseEvent.MOUSE_DRAGGED)
				me.consume();
		});
		tabPane.maxWidthProperty().bind(widthProperty().divide(1.5d));
		tabPane.maxHeightProperty().bind(heightProperty());
		tabPane.widthProperty().addListener((obs, oldV, newV) -> {
			FXUtils.setFixedTabWidth(tabPane, newV.doubleValue() / 3d);
			FXUtils.setFixedWidthOf(loginContainer, newV.doubleValue() / 1.5d);
			FXUtils.setFixedWidthOf(registerContainer, newV.doubleValue() / 1.5d);
			FXUtils.setFixedWidthOf(hostContainer, newV.doubleValue() / 1.5d);
			FXUtils.setFixedWidthOf(loginButton, newV.doubleValue() / 3d);
			FXUtils.setFixedWidthOf(loginCancelButton, newV.doubleValue() / 3d);
			FXUtils.setFixedWidthOf(registerButton, newV.doubleValue() / 3d);
			FXUtils.setFixedWidthOf(registerCancelButton, newV.doubleValue() / 3d);
			FXUtils.setFixedWidthOf(hostButton, newV.doubleValue() / 3d);
			FXUtils.setFixedWidthOf(hostCancelButton, newV.doubleValue() / 3d);
		});
		tabPane.getTabs().addAll(loginTab, registerTab, hostTab);
	}
	
	private void initHostContainers() {
		FontAwesomeIconView portIcon = new FontAwesomeIconView(FontAwesomeIcon.SERVER);
		portIcon.setGlyphSize(25d);
		portIcon.setWrappingWidth(25d);
		FontAwesomeIconView directoryIcon = new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN);
		directoryIcon.setGlyphSize(25d);
		directoryIcon.setWrappingWidth(25d);
		
		HBox hostPortBox = createIconBox(portIcon, 20d, hostPortField);
		HBox.setHgrow(hostPortField, Priority.ALWAYS);
		
		HBox hostDirectoryBox = createIconBox(directoryIcon, 20d, hostDirectories);
		
		HBox hostRememberDirectoryBox = new HBox(hostRememberDirectory);
		hostRememberDirectoryBox.setFillHeight(true);
		hostRememberDirectoryBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox hostSpacer = new HBox();
		
		HBox hostButtons = new HBox(20d);
		hostButtons.setAlignment(Pos.CENTER);
		hostButtons.setPadding(new Insets(100d, 0d, 0d, 0d));
		hostButtons.getChildren().addAll(hostButton, hostCancelButton);
		HBox.setHgrow(hostButton, Priority.ALWAYS);
		HBox.setHgrow(hostCancelButton, Priority.ALWAYS);
		
		hostContainer = new VBox(20d);
		hostContainer.setPadding(new Insets(150d, 0d, 150d, 0d));
		hostContainer.setAlignment(Pos.TOP_CENTER);
		hostContainer.setFillWidth(true);
		hostContainer.getChildren().addAll(hostPortBox, hostDirectoryBox, hostRememberDirectoryBox, hostSpacer, hostButtons);
		VBox.setVgrow(hostSpacer, Priority.SOMETIMES);
	}
	
	private void initRegisterContainers() {
		FontAwesomeIconView addressIcon = new FontAwesomeIconView(FontAwesomeIcon.SERVER);
		addressIcon.setGlyphSize(25d);
		addressIcon.setWrappingWidth(25d);
		FontAwesomeIconView usernameIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
		usernameIcon.setGlyphSize(25d);
		usernameIcon.setWrappingWidth(25d);
		FontAwesomeIconView passwordIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
		passwordIcon.setGlyphSize(25d);
		passwordIcon.setWrappingWidth(25d);
		
		HBox registerAddressBox = createIconBox(addressIcon, 20d, registerAddressField, registerPortField);
		HBox.setHgrow(registerAddressField, Priority.ALWAYS);
		
		HBox registerUsernameBox = createIconBox(usernameIcon, 20d, registerUsernameField);
		HBox.setHgrow(registerUsernameField, Priority.ALWAYS);
		
		HBox registerPasswordBox = createIconBox(passwordIcon, 20d, registerPasswordField, registerPasswordRepeatField);
		HBox.setHgrow(registerPasswordField, Priority.ALWAYS);
		HBox.setHgrow(registerPasswordRepeatField, Priority.ALWAYS);
		
		HBox registerConfigurePortBox = new HBox(registerConfigurePortButton);
		registerConfigurePortBox.setFillHeight(true);
		registerConfigurePortBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox registerRememberServerBox = new HBox(registerRememberServerButton);
		registerRememberServerBox.setFillHeight(true);
		registerRememberServerBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox registerRememberLoginBox = new HBox(registerRememberLoginButton);
		registerRememberLoginBox.setFillHeight(true);
		registerRememberLoginBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox registerSpacer = new HBox();
		
		HBox registerButtons = new HBox(20d);
		registerButtons.setAlignment(Pos.CENTER);
		registerButtons.setPadding(new Insets(100d, 0d, 0d, 0d));
		registerButtons.getChildren().addAll(registerButton, registerCancelButton);
		HBox.setHgrow(registerButton, Priority.ALWAYS);
		HBox.setHgrow(registerCancelButton, Priority.ALWAYS);
		
		registerContainer = new VBox(20d);
		registerContainer.setPadding(new Insets(150d, 0d, 150d, 0d));
		registerContainer.setAlignment(Pos.TOP_CENTER);
		registerContainer.setFillWidth(true);
		registerContainer.getChildren().addAll(registerAddressBox, registerUsernameBox, registerPasswordBox, registerConfigurePortBox, registerRememberServerBox, registerRememberLoginBox, registerSpacer, registerButtons);
		VBox.setVgrow(registerSpacer, Priority.SOMETIMES);
	}
	
	private void initLoginContainers() {
		FontAwesomeIconView addressIcon = new FontAwesomeIconView(FontAwesomeIcon.SERVER);
		addressIcon.setGlyphSize(25d);
		addressIcon.setWrappingWidth(25d);
		FontAwesomeIconView usernameIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
		usernameIcon.setGlyphSize(25d);
		usernameIcon.setWrappingWidth(25d);
		FontAwesomeIconView passwordIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
		passwordIcon.setGlyphSize(25d);
		passwordIcon.setWrappingWidth(25d);
		
		HBox loginAddressBox = createIconBox(addressIcon, 20d, loginAddressField);
		HBox.setHgrow(loginAddressField, Priority.ALWAYS);
		
		HBox loginUsernameBox = createIconBox(usernameIcon, 20d, loginUsernameField);
		HBox.setHgrow(loginUsernameField, Priority.ALWAYS);
		
		HBox loginPasswordBox = createIconBox(passwordIcon, 20d, loginPasswordField);
		HBox.setHgrow(loginPasswordField, Priority.ALWAYS);
		
		HBox loginConfigurePortBox = new HBox(loginConfigurePortButton);
		loginConfigurePortBox.setFillHeight(true);
		loginConfigurePortBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox loginRememberServerBox = new HBox(loginRememberServerButton);
		loginRememberServerBox.setFillHeight(true);
		loginRememberServerBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox loginRememberLoginBox = new HBox(loginRememberLoginButton);
		loginRememberLoginBox.setFillHeight(true);
		loginRememberLoginBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox loginSpacer = new HBox();
		
		HBox loginButtons = new HBox(20d);
		loginButtons.setAlignment(Pos.CENTER);
		loginButtons.setPadding(new Insets(100d, 0d, 0d, 0d));
		loginButtons.getChildren().addAll(loginButton, loginCancelButton);
		HBox.setHgrow(loginButton, Priority.ALWAYS);
		HBox.setHgrow(loginCancelButton, Priority.ALWAYS);
		
		loginContainer = new VBox(20d);
		loginContainer.setPadding(new Insets(150d, 0d, 150d, 0d));
		loginContainer.setAlignment(Pos.TOP_CENTER);
		loginContainer.setFillWidth(true);
		loginContainer.getChildren().addAll(loginAddressBox, loginUsernameBox, loginPasswordBox, loginConfigurePortBox, loginRememberServerBox, loginRememberLoginBox, loginSpacer, loginButtons);
		VBox.setVgrow(loginSpacer, Priority.SOMETIMES);
	}
	
	private void initLoginControls() {
		loginAddressField = new JFXTextField();
		loginAddressField.setPromptText("Server-Adresse");
		
		loginPortField = new JFXTextField();
		loginPortField.setText(Integer.toString(getDefaultPort()));
		loginPortField.setPromptText("Server-Port");
		FXUtils.setFixedWidthOf(loginPortField, 150d);
		
		loginUsernameField = new JFXTextField();
		loginUsernameField.setPromptText("Benutzername");
		
		loginPasswordField = new JFXPasswordField();
		loginPasswordField.setPromptText("Passwort");
		
		loginConfigurePortButton = new JFXToggleButton();
		loginConfigurePortButton.setOnAction(a -> onConfigureLoginPortButtonClicked());
		loginConfigurePortButton.setText("Port konfigurieren");
		loginConfigurePortButton.setSelected(false);		
		
		loginRememberServerButton = new JFXCheckBox("Server merken");
		loginRememberLoginButton = new JFXCheckBox("Benutzer merken");
		
		loginButton = new JFXButton("Anmelden");
		loginButton.getStyleClass().add("confirm-button");
		
		loginCancelButton = new JFXButton("Abbrechen");
		loginCancelButton.getStyleClass().add("cancel-button");
	}
	
	private void initRegisterControls() {
		registerAddressField = new JFXTextField();
		registerAddressField.setPromptText("Server-Adresse");
		
		registerPortField = new JFXTextField();
		registerPortField.setText(Integer.toString(getDefaultPort()));
		registerPortField.setPromptText("Server-Port");
		FXUtils.setFixedWidthOf(registerPortField, 150d);
		
		registerUsernameField = new JFXTextField();
		registerUsernameField.setPromptText("Benutzername");
		
		registerPasswordField = new JFXPasswordField();
		registerPasswordField.setPromptText("Passwort");
		
		registerPasswordRepeatField = new JFXPasswordField();
		registerPasswordRepeatField.setPromptText("Password wiederholen");
		
		registerConfigurePortButton = new JFXToggleButton();
		registerConfigurePortButton.setOnAction(a -> onConfigureRegisterPortButtonClicked());
		registerConfigurePortButton.setText("Port konfigurieren");		
		registerConfigurePortButton.setSelected(true);
		
		registerRememberServerButton = new JFXCheckBox("Server merken");
		registerRememberLoginButton = new JFXCheckBox("Benutzer merken");
		
		registerButton = new JFXButton("Registrieren");
		registerButton.getStyleClass().add("confirm-button");
		
		registerCancelButton = new JFXButton("Abbrechen");
		registerCancelButton.getStyleClass().add("cancel-button");
	}
	
	private void initHostControls() {
		hostPortField = new JFXTextField();
		hostPortField.setPromptText("Server-Port");
		hostDirectories = new JFXComboBox<String>();
		hostDirectories.prefWidthProperty().bind(hostPortField.widthProperty());
		hostRememberDirectory = new JFXCheckBox("Verzeichnis merken");
		hostButton = new JFXButton("Server starten");
		hostButton.getStyleClass().add("confirm-button");
		hostCancelButton = new JFXButton("Abbrechen");
		hostCancelButton.getStyleClass().add("cancel-button");
	}
	
	private void initProperties() {
		minViewWidthProperty = new SimpleDoubleProperty(1000d);
		minViewHeightProperty = new SimpleDoubleProperty(1000d);
		defaultPortProperty = new SimpleIntegerProperty(2199);
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/defaultStyle/LoginView.css");
	}
	
	private void addLoginPortField() {
		HBox root = (HBox)loginContainer.getChildren().get(0);
		if(!root.getChildren().contains(loginPortField))
			root.getChildren().add(loginPortField);
	}
	
	private void removeLoginPortField() {
		((HBox)(loginContainer.getChildren().get(0))).getChildren().remove(loginPortField);
	}
	
	private void addRegisterPortField() {
		HBox root = (HBox)registerContainer.getChildren().get(0);
		if(!root.getChildren().contains(registerPortField))
			root.getChildren().add(registerPortField);
	}
	
	private void removeRegisterPortField() {
		((HBox)(registerContainer.getChildren().get(0))).getChildren().remove(registerPortField);
	}
	
	private HBox createIconBox(Node icon, double spacing, Node ... controls) {
		return createIconBox(icon, true, spacing, controls);
	}
	
	private HBox createIconBox(Node icon, boolean leftSide, double spacing, Node ... controls) {
		HBox box = new HBox(spacing);
		box.setFillHeight(true);
		box.setAlignment(Pos.CENTER_LEFT);
		if(icon != null)
			box.getChildren().add(icon);
		if(controls != null)
			box.getChildren().addAll(controls);
		return box;
	}
	
	public Stage getParentWindow() {
		return parentWindow;
	}
	
	public void setParentWindow(Stage window) {
		parentWindow = window;
		if(window == null)
			return;
		parentWindow.setMinWidth(getMinWidth());
		parentWindow.setMinHeight(getMinHeight());
	}
	
	public JFXTabPane getTabPane() {
		return tabPane;
	}
	
	public VBox getLoginContainer() {
		return loginContainer;
	}
	
	public VBox getRegisterContainer() {
		return registerContainer;
	}
	
	public VBox getHostContainer() {
		return hostContainer;
	}
	
	public JFXTextField getLoginAddressField() {
		return loginAddressField;
	}
	
	public String getLoginAddressText() {
		return loginAddressField.getText();
	}
	
	public void setLoginAddressText(String value) {
		loginAddressField.setText(value);		
	}
	
	public boolean isLoginPortVisible() {
		return ((HBox)loginContainer.getChildren().get(0)).getChildren().contains(loginPortField);
	}
	
	public JFXTextField getLoginPortField() {
		return loginPortField;
	}
	
	public String getLoginPortText() {
		return loginPortField.getText();
	}
	
	public void setLoginPortText(String value) {
		loginPortField.setText(value);		
	}
	
	public JFXTextField getLoginUsernameField() {
		return loginUsernameField;
	}
	
	public String getLoginUsernameText() {
		return loginUsernameField.getText();
	}
	
	public void setLoginUsernameText(String value) {
		loginUsernameField.setText(value);	
	}
	
	public JFXPasswordField getLoginPasswordField() {
		return loginPasswordField;
	}
	
	public String getLoginPasswordText() {
		return loginPasswordField.getText();
	}
	
	public void setLoginPasswordText(String value) {
		loginPasswordField.setText(value);
	}
	
	public JFXToggleButton getLoginConfigurePortButton() {
		return loginConfigurePortButton;
	}
	
	public boolean isLoginConfigurePortButtonSelected() {
		return loginConfigurePortButton.isSelected();
	}
	
	public void setLoginConfigurePortButtonSelected(boolean value) {
		loginConfigurePortButton.setSelected(value);
	}
	
	public JFXCheckBox getLoginRememberServerButton() {
		return loginRememberServerButton;
	}
	
	public boolean isLoginRememberServerButtonSelected() {
		return loginRememberServerButton.isSelected();
	}
	
	public void setLoginRememberServerButtonSelected(boolean value) {
		loginRememberServerButton.setSelected(value);
	}
	
	public JFXCheckBox getLoginRememberLoginButton() {
		return loginRememberLoginButton;
	}
	
	public boolean isLoginRememberLoginButtonSelected() {
		return loginRememberLoginButton.isSelected();
	}
	
	public void setLoginRememberLoginButtonSelected(boolean value) {
		loginRememberLoginButton.setSelected(value);
	}
	
	public JFXButton getLoginButton() {
		return loginButton;
	}
	
	public JFXButton getLoginCancelButton() {
		return loginCancelButton;
	}
	
	public JFXTextField getRegisterAddressField() {
		return registerAddressField;
	}
	
	public String getRegisterAddressText() {
		return registerAddressField.getText();
	}
	
	public void setRegisterAddressText(String value) {
		registerAddressField.setText(value);
	}
	
	public boolean isRegisterPortVisible() {
		return ((HBox)registerContainer.getChildren().get(0)).getChildren().contains(registerPortField);
	}
	
	public JFXTextField getRegisterPortField() {
		return registerPortField;
	}
	
	public String getRegisterPortText() {
		return registerPortField.getText();
	}
	
	public void setRegisterPortText(String value) {
		registerPortField.setText(value);	
	}
	
	public JFXTextField getRegisterUsernameField() {
		return registerUsernameField;
	}
	
	public String getRegisterUsernameText() {
		return registerUsernameField.getText();
	}
	
	public void setRegisterUsernameText(String value) {
		registerUsernameField.setText(value);
	}
	
	public JFXPasswordField getRegisterPasswordField() {
		return registerPasswordField;
	}
	
	public String getRegisterPasswordText() {
		return registerPasswordField.getText();
	}
	
	public void setRegisterPasswordText(String value) {
		registerPasswordField.setText(value);	
	}
	
	public JFXPasswordField getRegisterPasswordRepeatField() {
		return registerPasswordRepeatField;
	}
	
	public String getRegisterPasswordRepeatText() {
		return registerPasswordRepeatField.getText();
	}
	
	public void setRegisterPasswordRepeatText(String value) {
		registerPasswordRepeatField.setText(value);	
	}
	
	public JFXToggleButton getRegisterConfigurePortButton() {
		return registerConfigurePortButton;
	}
	
	public JFXCheckBox getRegisterRememberServerButton() {
		return registerRememberServerButton;
	}
	
	public JFXCheckBox getRegisterRememberLoginButton() {
		return registerRememberLoginButton;
	}
	
	public JFXButton getRegisterButton() {
		return registerButton;
	}
	
	public JFXButton getRegisterCancelButton() {
		return registerCancelButton;
	}
	
	public JFXTextField getHostPortField() {
		return hostPortField;
	}
	
	public String getHostPortText() {
		return hostPortField.getText();
	}
	
	public void setHostPortFieldText(String value) {
		hostPortField.setText(value);	
	}
	
	public JFXButton getHostButton() {
		return hostButton;
	}
	
	public JFXButton getHostCancelButton() {
		return hostCancelButton;
	}
	
	public ReadOnlyDoubleProperty minViewWidthProperty() {
		return minViewWidthProperty;
	}
	
	public double getMinViewWidth() {
		return minViewWidthProperty.get();
	}
	
	public void setMinViewWidth(double value) {
		minViewWidthProperty.set(value);
	}
	
	public ReadOnlyDoubleProperty minViewHeightProperty() {
		return minViewHeightProperty;
	}
	
	public double getMinViewHeight() {
		return minViewHeightProperty.get();
	}
	
	public void setMinViewHeight(double value) {
		minViewHeightProperty.set(value);
	}
	
	public IntegerProperty defaultPortProperty() {
		return defaultPortProperty;
	}
	
	public int getDefaultPort() {
		return defaultPortProperty.get();
	}
	
	public void setDefaultPort(int value) {
		defaultPortProperty.set(value);
	}
}
