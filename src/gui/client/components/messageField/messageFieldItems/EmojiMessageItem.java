package gui.client.components.messageField.messageFieldItems;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import network.ssl.client.utils.CUtils;

public class EmojiMessageItem extends ImageView implements MessageFieldItem {
	protected String filePath;
	protected String smileyName;
	protected EmojiSize smileySize;
	
	public enum EmojiSize {
		SMALL(16, 16), NORMAL(32, 32), LARGE(48, 48);
		int width;
		int height;
		
		private EmojiSize(int w, int h) {
			width = w;
			height = h;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
	}
	
	public EmojiMessageItem(String path) {
		this(path, ":" + CUtils.getFileName(path, false) + ":", EmojiSize.NORMAL);
	}
	
	public EmojiMessageItem(String path, String name) {
		this(path, name, EmojiSize.NORMAL);
	}
	
	public EmojiMessageItem(String path, String name, EmojiSize size) {
		initProperties(path, name, size);
		initImage(filePath);
		initStyleClass("smiley-message-item");
	}
	
	private void initProperties(String path, String name, EmojiSize size) {
		filePath = path;
		smileyName = name;
		smileySize = size;
	}
	
	private void initImage(String filePath) {
		setImage(new Image(filePath, smileySize.getWidth(), smileySize.getHeight(), true, true));
	}
	
	private void initStyleClass(String styleClass) {
		getStyleClass().add(styleClass);
	}

	@Override
	public String toMessageString() {
		return ":" + smileyName + ":";
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public String getName() {
		return smileyName;
	}
	
	public EmojiSize getSize() {
		return smileySize;
	}
}
