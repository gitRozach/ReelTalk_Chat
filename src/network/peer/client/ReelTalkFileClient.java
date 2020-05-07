package network.peer.client;

public class ReelTalkFileClient extends SecuredProtobufClient {
	protected String filePath;
	
	public ReelTalkFileClient(String protocol, String hostAddress, int hostPort) throws Exception {
		super(protocol, hostAddress, hostPort);
	}
	
	//Probeweise 
	
	public void requestFileDownload(String fileName, String downloadKey) {
		
	}
	
	public void requestFileUpload(String filePath) {
		
	}
}
