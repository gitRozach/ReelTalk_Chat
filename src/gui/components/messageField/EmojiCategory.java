package gui.components.messageField;

public enum EmojiCategory {
	A(0), B(1), C(2), D(3), E(4), F(5), G(6), H(7), I(8);
	
	private int value;
	
	private EmojiCategory(int v) {
		value = v;
	}
	
	public int getIntValue() {
		return value;
	}

	public static EmojiCategory getByInt(int value) {
		switch (value) {
		case 0:
			return A;
		case 1:
			return B;
		case 2:
			return C;
		case 3:
			return D;
		case 4:
			return E;
		case 5:
			return F;
		case 6:
			return G;
		case 7:
			return H;
		case 8:
			return I;
		default:
			return null;
		}
	}
}
