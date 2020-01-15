package gui.client.components.messages;

public class CFileStackDownloadMessage extends CFileDownloadMessage {

	public CFileStackDownloadMessage(String sender, String key, String... messageFiles) {
		super(sender, key, messageFiles[0]);
	}
}
