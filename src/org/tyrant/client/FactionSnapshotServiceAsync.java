package org.tyrant.client;

import org.tyrant.shared.FactionDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FactionSnapshotServiceAsync {
	void greetServer(String name, AsyncCallback<FactionDTO> callback);
}
