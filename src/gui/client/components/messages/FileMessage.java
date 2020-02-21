//package gui.client.components.messages;
//
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
//import javafx.event.EventHandler;
//import javafx.scene.input.MouseEvent;
//
//public interface FileMessage {
//	public double getProgress();
//
//	public void setProgress(double value);
//
//	public FileClientState getState();
//
//	public void setState(FileClientState state);
//
//	public void setOnRetryClicked(EventHandler<MouseEvent> me);
//
//	public void setOnCancelClicked(EventHandler<MouseEvent> me);
//
//	public static FontAwesomeIcon getFileIcon(String icon) {
//		if (icon.startsWith("."))
//			icon = icon.substring(1);
//		switch (icon.toLowerCase()) {
//		case "txt":
//			return FontAwesomeIcon.FILE_TEXT_ALT;
//		case "pdf":
//			return FontAwesomeIcon.FILE_PDF_ALT;
//		case "rar":
//			return FontAwesomeIcon.FILE_ZIP_ALT;
//		default:
//			return FontAwesomeIcon.FILE;
//		}
//	}
//}
