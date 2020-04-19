package gui.components.messageField;

import com.jfoenix.controls.JFXTabPane;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import handler.events.ObjectEvent;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import utils.FXUtils;

public class EmojiTabPane extends StackPane {	
	private JFXTabPane tabPane;
	private EmojiSkinChooser skinChooser;

	private EmojiTab emojiTabA;
	private EmojiTab emojiTabB;
	private EmojiTab emojiTabC;
	private EmojiTab emojiTabD;
	private EmojiTab emojiTabE;
	private EmojiTab emojiTabF;
	private EmojiTab emojiTabG;
	private EmojiTab emojiTabH;
	private EmojiTab emojiTabI;

	private int CATEGORY_A_LENGTH;
	private int CATEGORY_B_LENGTH;
	private int CATEGORY_C_LENGTH;
	private int CATEGORY_D_LENGTH;
	private int CATEGORY_E_LENGTH;
	private int CATEGORY_F_LENGTH;
	private int CATEGORY_G_LENGTH;
	private int CATEGORY_H_LENGTH;
	private int CATEGORY_I_LENGTH;

	public EmojiTabPane() {
		initialize();
	}
	
	private void initialize() {
		initProperties();
		initEmojiTabs();
		initAllEmojis();
		initEmojiTabPane();
		initEmojiSkinChooser();
		initRoot();
	}
	
	private void initProperties() {
		CATEGORY_A_LENGTH = 289;
		CATEGORY_B_LENGTH = 159;
		CATEGORY_C_LENGTH = 86;
		CATEGORY_D_LENGTH = 80;
		CATEGORY_E_LENGTH = 119;
		CATEGORY_F_LENGTH = 172;
		CATEGORY_G_LENGTH = 257;
		CATEGORY_H_LENGTH = 250;
		CATEGORY_I_LENGTH = 0;
	}
	
	private void initEmojiTabs() {
		FontAwesomeIconView iconTabA = new FontAwesomeIconView(FontAwesomeIcon.MALE);
		emojiTabA = new EmojiTab();
		emojiTabA.setGraphic(iconTabA);

		FontAwesomeIconView iconTabB = new FontAwesomeIconView(FontAwesomeIcon.CLOUD);
		emojiTabB = new EmojiTab();
		emojiTabB.setGraphic(iconTabB);

		FontAwesomeIconView iconTabC = new FontAwesomeIconView(FontAwesomeIcon.COFFEE);
		emojiTabC = new EmojiTab();
		emojiTabC.setGraphic(iconTabC);

		FontAwesomeIconView iconTabD = new FontAwesomeIconView(FontAwesomeIcon.SOCCER_BALL_ALT);
		emojiTabD = new EmojiTab();
		emojiTabD.setGraphic(iconTabD);

		FontAwesomeIconView iconTabE = new FontAwesomeIconView(FontAwesomeIcon.CAR);
		emojiTabE = new EmojiTab();
		emojiTabE.setGraphic(iconTabE);

		FontAwesomeIconView iconTabF = new FontAwesomeIconView(FontAwesomeIcon.DESKTOP);
		emojiTabF = new EmojiTab();
		emojiTabF.setGraphic(iconTabF);

		FontAwesomeIconView iconTabG = new FontAwesomeIconView(FontAwesomeIcon.HEART);
		emojiTabG = new EmojiTab();
		emojiTabG.setGraphic(iconTabG);

		FontAwesomeIconView iconTabH = new FontAwesomeIconView(FontAwesomeIcon.FLAG);
		emojiTabH = new EmojiTab();
		emojiTabH.setGraphic(iconTabH);

		FontAwesomeIconView iconTabI = new FontAwesomeIconView(FontAwesomeIcon.SERVER);
		emojiTabI = new EmojiTab();
		emojiTabI.setGraphic(iconTabI);
	}
	
	private void initEmojiTabPane() {
		tabPane = new JFXTabPane();
		tabPane.setPickOnBounds(true);
		tabPane.setTabMaxHeight(35d);
		tabPane.setTabMinHeight(35d);
		tabPane.getTabs().addAll(emojiTabA, emojiTabB, emojiTabC, emojiTabD, emojiTabE, emojiTabF, emojiTabG, emojiTabH, emojiTabI);
	}
	
	private void initEmojiSkinChooser() {
		skinChooser = new EmojiSkinChooser();
		skinChooser.setFromColor(EmojiSkinChooser.SKIN_COLORS[0]);
		skinChooser.setPickOnBounds(true);
		FXUtils.setFixedSizeOf(skinChooser, 25d, 25d);
	}
	
	private void initRoot() {
		getChildren().addAll(tabPane, skinChooser);
		EmojiTabPane.setAlignment(skinChooser, Pos.TOP_RIGHT);
		EmojiTabPane.setMargin(skinChooser, new Insets(0d, 15d, 5d, 5d));
	}
	
	private void initAllEmojis() {
		initAllEmojis(false, EmojiSkinColor.YELLOW);
	}
	
	private void initAllEmojis(boolean override, EmojiSkinColor color) {
		for(EmojiCategory category : EmojiCategory.values())
			initEmojis(category, color, override);
	}

	private void initEmojis(EmojiCategory category, EmojiSkinColor color, boolean override) {
		final EmojiTab smileyTab;
		final int smileyCount;
		final String smileyCategory = category.name();

		switch (category) {
		case A:
			smileyTab = emojiTabA;
			smileyCount = CATEGORY_A_LENGTH;
			break;
		case B:
			smileyTab = emojiTabB;
			smileyCount = CATEGORY_B_LENGTH;
			break;
		case C:
			smileyTab = emojiTabC;
			smileyCount = CATEGORY_C_LENGTH;
			break;
		case D:
			smileyTab = emojiTabD;
			smileyCount = CATEGORY_D_LENGTH;
			break;
		case E:
			smileyTab = emojiTabE;
			smileyCount = CATEGORY_E_LENGTH;
			break;
		case F:
			smileyTab = emojiTabF;
			smileyCount = CATEGORY_F_LENGTH;
			break;
		case G:
			smileyTab = emojiTabG;
			smileyCount = CATEGORY_G_LENGTH;
			break;
		case H:
			smileyTab = emojiTabH;
			smileyCount = CATEGORY_H_LENGTH;
			break;
		case I:
			smileyTab = emojiTabI;
			smileyCount = CATEGORY_I_LENGTH;
			break;
		default:
			smileyCount = 1;
			smileyTab = null;
		}

		new Thread(() -> {
			for (int i = 0; i < smileyCount; i++) {
				boolean[] withSkinColors = { false };
				int tempIndex = i;
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
				currentImageView.applyCss();
				currentImageView.setCache(true);
				currentImageView.getStyleClass().add("smiley");
								
				currentImageView.setOnMouseClicked(a -> {
					fireEvent(new ObjectEvent<String>(ObjectEvent.STRING, new String(currentImageTitle + (withSkinColors[0] ? EmojiSkinColor.toEmojiString(color) : ""))) {
						private static final long serialVersionUID = -1195663894069989722L;
					});
				});

				Platform.runLater(() -> {
					if (override)
						smileyTab.setEmoji(tempIndex, currentImageView);
					else
						smileyTab.addEmoji(currentImageView);
				});
			}
		}).start();
	}
	
	public void initAllEmojisWithSkinColor(EmojiSkinColor color) {
		initAllEmojis(true, color);
	}
	
	public EmojiSkinChooser getEmojiSkinChooser() {
		return skinChooser;
	}
}
