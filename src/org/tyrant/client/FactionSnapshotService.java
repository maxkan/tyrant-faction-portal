package org.tyrant.client;

import org.tyrant.shared.FactionDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface FactionSnapshotService extends RemoteService {
	FactionDTO greetServer(String name) throws IllegalArgumentException;
}
