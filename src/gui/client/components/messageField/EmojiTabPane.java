package gui.client.components.messageField;

import com.jfoenix.controls.JFXTabPane;

import gui.client.components.messageField.messageFieldItems.SmileyMessageItem;
import gui.tools.GUITools;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import network.ssl.client.utils.CUtils;

public class EmojiTabPane extends StackPane {
	private EventHandler<ActionEvent> onEmojiPressed;
	
	private JFXTabPane tabPane;
	private EmojiSkinChooser skinChooser;

	private EmojiTab smileyTabA;
	private EmojiTab smileyTabB;
	private EmojiTab smileyTabC;
	private EmojiTab smileyTabD;
	private EmojiTab smileyTabE;
	private EmojiTab smileyTabF;
	private EmojiTab smileyTabG;
	private EmojiTab smileyTabH;
	private EmojiTab smileyTabI;

	private final int CATEGORY_A_LENGTH = 289;
	private final int CATEGORY_B_LENGTH = 159;
	private final int CATEGORY_C_LENGTH = 86;
	private final int CATEGORY_D_LENGTH = 80;
	private final int CATEGORY_E_LENGTH = 119;
	private final int CATEGORY_F_LENGTH = 172;
	private final int CATEGORY_G_LENGTH = 257;
	private final int CATEGORY_H_LENGTH = 250;
	private final int CATEGORY_I_LENGTH = 0;

	public EmojiTabPane() {
		initEventHandlers();
		tabPane = new JFXTabPane();
		tabPane.setPickOnBounds(true);
		tabPane.setTabMaxHeight(35d);
		tabPane.setTabMinHeight(35d);

		smileyTabA = new EmojiTab();
		Label labelTabA = new Label("A");
		labelTabA.setFont(CUtils.CFont(15d));
		labelTabA.setTextFill(Color.DARKGRAY);
		smileyTabA.setGraphic(labelTabA);
		initSmileys(EmojiCategory.A, false);

		smileyTabB = new EmojiTab();
		Label labelTabB = new Label("B");
		labelTabB.setFont(CUtils.CFont(15d));
		labelTabB.setTextFill(Color.DARKGRAY);
		smileyTabB.setGraphic(labelTabB);
		initSmileys(EmojiCategory.B, false);

		smileyTabC = new EmojiTab();
		Label labelTabC = new Label("C");
		labelTabC.setFont(CUtils.CFont(15d));
		labelTabC.setTextFill(Color.DARKGRAY);
		smileyTabC.setGraphic(labelTabC);
		initSmileys(EmojiCategory.C, false);

		smileyTabD = new EmojiTab();
		Label labelTabD = new Label("D");
		labelTabD.setFont(CUtils.CFont(15d));
		labelTabD.setTextFill(Color.DARKGRAY);
		smileyTabD.setGraphic(labelTabD);
		initSmileys(EmojiCategory.D, false);

		smileyTabE = new EmojiTab();
		Label labelTabE = new Label("E");
		labelTabE.setFont(CUtils.CFont(15d));
		labelTabE.setTextFill(Color.DARKGRAY);
		smileyTabE.setGraphic(labelTabE);
		initSmileys(EmojiCategory.E, false);

		smileyTabF = new EmojiTab();
		Label labelTabF = new Label("F");
		labelTabF.setFont(CUtils.CFont(15d));
		labelTabF.setTextFill(Color.DARKGRAY);
		smileyTabF.setGraphic(labelTabF);
		initSmileys(EmojiCategory.F, false);

		smileyTabG = new EmojiTab();
		Label labelTabG = new Label("G");
		labelTabG.setFont(CUtils.CFont(15d));
		labelTabG.setTextFill(Color.DARKGRAY);
		smileyTabG.setGraphic(labelTabG);
		initSmileys(EmojiCategory.G, false);

		smileyTabH = new EmojiTab();
		Label labelTabH = new Label("H");
		labelTabH.setFont(CUtils.CFont(15d));
		labelTabH.setTextFill(Color.DARKGRAY);
		smileyTabH.setGraphic(labelTabH);
		initSmileys(EmojiCategory.H, false);

		smileyTabI = new EmojiTab();
		Label labelTabI = new Label("I");
		labelTabI.setFont(CUtils.CFont(15d));
		labelTabI.setTextFill(Color.DARKGRAY);
		smileyTabI.setGraphic(labelTabI);
		initSmileys(EmojiCategory.I, false);

		skinChooser = new EmojiSkinChooser();
		skinChooser.setFromColor(skinChooser.skinColors[0]);
		skinChooser.setPickOnBounds(true);
		GUITools.setFixedSizeOf(skinChooser, 25d, 25d);
		EmojiTabPane.setAlignment(skinChooser, Pos.TOP_RIGHT);
		EmojiTabPane.setMargin(skinChooser, new Insets(0d, 15d, 5d, 5d));

		tabPane.getTabs().addAll(smileyTabA, smileyTabB, smileyTabC, smileyTabD, smileyTabE, smileyTabF,
				smileyTabG, smileyTabH, smileyTabI);

		getChildren().addAll(tabPane, skinChooser);
	}
	
	private void initEventHandlers() {
		onEmojiPressed = actionEvent -> {};
	}

	private void initSmileys(EmojiCategory category, EmojiSkinColor color, boolean override) {
		final EmojiTab smileyTab;
		final int smileyCount;
		final String smileyCategory = category.name();

		switch (category) {
		case A:
			smileyTab = smileyTabA;
			smileyCount = CATEGORY_A_LENGTH;
			break;
		case B:
			smileyTab = smileyTabB;
			smileyCount = CATEGORY_B_LENGTH;
			break;
		case C:
			smileyTab = smileyTabC;
			smileyCount = CATEGORY_C_LENGTH;
			break;
		case D:
			smileyTab = smileyTabD;
			smileyCount = CATEGORY_D_LENGTH;
			break;
		case E:
			smileyTab = smileyTabE;
			smileyCount = CATEGORY_E_LENGTH;
			break;
		case F:
			smileyTab = smileyTabF;
			smileyCount = CATEGORY_F_LENGTH;
			break;
		case G:
			smileyTab = smileyTabG;
			smileyCount = CATEGORY_G_LENGTH;
			break;
		case H:
			smileyTab = smileyTabH;
			smileyCount = CATEGORY_H_LENGTH;
			break;
		case I:
			smileyTab = smileyTabI;
			smileyCount = CATEGORY_I_LENGTH;
			break;
		default:
			smileyCount = 1;
			smileyTab = null;
		}

		new Thread(() -> {
			for (int i = 0; i < smileyCount; i++) {
				boolean[] withSkinColors = { false };
				int[] tempIndex = { i };
				String currentImageTitle = smileyCategory + (i + 1);
				Image currentImage;
				ImageView currentImageView;

				try {
					currentImage = new Image("/resources/smileys/category" + smileyCategory + "/" + currentImageTitle + ".png");
					withSkinColors[0] = false;
				} 
				catch (IllegalArgumentException e) {
					currentImage = new Image("/resources/smileys/category" + smileyCategory + "/" + currentImageTitle + EmojiSkinColor.toEmojiString(color) + ".png");
					withSkinColors[0] = true;
				}

				currentImageView = new ImageView(currentImage);
				currentImageView.setSmooth(true);
				currentImageView.setCache(true);
				currentImageView.getStyleClass().add("smiley");
				
				currentImageView.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, a -> {});
				
				currentImageView.setOnMouseClicked(a -> onSmileyClicked(currentImageTitle
						+ (withSkinColors[0] ? EmojiSkinColor.toEmojiString(color) : "")));

				Platform.runLater(() -> {
					if (override)
						smileyTab.setSmiley(tempIndex[0], currentImageView);
					else
						smileyTab.addSmiley(currentImageView);
				});
			}
		}).start();
	}
	
	private void onSmileyClicked(String smileyText) {
		String currentText = inputField.getCurrentText();
		int currentTextPos = inputField.getOldCaretPosition();
		
		System.out.println("Current Text: " + currentText);
		System.out.println("Current Pos: " + currentTextPos);
		
		if(!currentText.isEmpty()) {
			String firstWord = currentText.substring(0, currentTextPos);
			String secondWord = currentText.substring(currentTextPos);
			
			System.out.println("First Word: " + firstWord);
			System.out.println("Second Word: " + secondWord);
			
			if(!firstWord.isEmpty())
				inputField.addText(firstWord);
			inputField.addItem(new SmileyMessageItem("/resources/smileys/category" + smileyText.charAt(0) + "/" + smileyText + ".png", smileyText));
			if(!secondWord.isEmpty())
				inputField.addText(secondWord);
		}			
		else
			inputField.addItem(new SmileyMessageItem("/resources/smileys/category" + smileyText.charAt(0) + "/" + smileyText + ".png", smileyText));
		inputField.getTextField().requestFocus();
	}

	private void initSmileys(EmojiCategory category, boolean override) {
		initSmileys(category, EmojiSkinColor.YELLOW, override);
	}
	
	public void setOnEmojiClicked(EventHandler<ActionEvent> handler) {
		if(handler == null)
			onEmojiPressed = event -> {};
		else
			onEmojiPressed = handler;	
	}
}
