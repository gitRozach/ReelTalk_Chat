package gui.client.components.messageField;

public enum EmojiSkinColor {
	YELLOW(0), LIGHT_WHITE(1), WHITE(2), DEEP_WHITE(3), LIGHT_BLACK(4), BLACK(5);

	private int value;
	
	private EmojiSkinColor(int v) {
		value = v;
	}
	
	public int getIntValue() {
		return value;
	}
	
	public static EmojiSkinColor getByInt(int value) {
		switch (value) {
		case 0:
			return YELLOW;
		case 1:
			return LIGHT_WHITE;
		case 2:
			return WHITE;
		case 3:
			return DEEP_WHITE;
		case 4:
			return LIGHT_BLACK;
		case 5:
			return BLACK;
		default:
			return null;
		}
	}
	
	public static String toEmojiString(EmojiSkinColor color) {
		switch (color) {
		case YELLOW:
			return "A";
		case LIGHT_WHITE:
			return "B";
		case WHITE:
			return "C";
		case DEEP_WHITE:
			return "D";
		case LIGHT_BLACK:
			return "E";
		case BLACK:
			return "F";
		default:
			return null;
		}
	}
}
