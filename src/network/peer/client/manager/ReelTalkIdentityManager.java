package network.peer.client.manager;

import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBase;

public class ReelTalkIdentityManager {
	protected ClientAccount clientAccount;
	
	public ReelTalkIdentityManager() {
		clientAccount = null;
	}
	
	public boolean isSameIdentity(int id, String username, String password) {
		return isSameIdentity(username, password) && id == getClientId();
	}
	
	public boolean isSameIdentity(String username, String password) {
		if(!hasClientAccount() || username == null || password == null)
			return false;
		return getClientUsername().equals(username) && getClientPassword().equals(password);
	}
	
	public String getClientUsername() {
		if(!hasClientAccount())
			return null;
		return clientAccount.getProfile().getBase().getUsername();
	}
	
	public void setClientUsername(String value) {
		if(!hasClientAccount())
			return;
		ClientBase newBase = clientAccount.getProfile().getBase().toBuilder().setUsername(value).build();
		setClientAccount(clientAccount.toBuilder().setProfile(clientAccount.getProfile().toBuilder().setBase(newBase)).build());
	}
	
	public String getClientPassword() {
		return clientAccount.getPassword();
	}
	
	public void setClientPassword(String value) {
		if(!hasClientAccount())
			return;
		setClientAccount(clientAccount.toBuilder().setPassword(value).build());
	}
	
	public int getClientId() {
		return !hasClientAccount() ? -1 : clientAccount.getProfile().getBase().getId();
	}
	
	public void setClientId(int id) {
		if(!hasClientAccount())
			return;
		ClientBase newBase = clientAccount.getProfile().getBase().toBuilder().setId(id).build();
		setClientAccount(clientAccount.toBuilder().setProfile(clientAccount.getProfile().toBuilder().setBase(newBase)).build());
	}
	
	public boolean hasClientAccount() {
		return clientAccount != null;
	}
	
	public ClientAccount getClientAccount() {
		return clientAccount;
	}
	
	public void setClientAccount(ClientAccount account) {
		clientAccount = account;
	}
}
