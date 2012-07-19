package org.max.server.processors;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import tyrant.shared.RequestProfile;
import tyrant.shared.TyrantSession;
import tyrant.shared.utils.Storage;

public class UserNameProcessor implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<Integer, String> usrIdToName = new TreeMap<Integer, String>();

	private final transient TyrantSession session;

	public UserNameProcessor(TyrantSession session) {

		UserNameProcessor singleton = Storage.deserializeSingleton(UserNameProcessor.class);
		if (singleton != null) {
			usrIdToName = singleton.usrIdToName;
		}
		this.session = session;
	}

	public String getName(Integer userId) {
		return usrIdToName.get(userId);
	}

	public void fillUserNames(Collection<Integer> userIds) {
		boolean changed = false;
		for (Integer userId : userIds) {
			if (fillUserName(userId)) {
				changed = true;
			}
		}

		if (changed) {
			Storage.serializeSingleton(this);
		}
	}

	private boolean fillUserName(Integer userId) {
		if (!usrIdToName.containsKey(userId)) {
			System.out.println("get name for user id:" + userId);
			String name = new RequestProfile(session, "getName", "target_id", userId).getJsonResponse().get("name").asText();
			usrIdToName.put(userId, name);
			return true;
		} else {
			return false;
		}
	}

	public Map<Integer, String> getUsrIdToName() {
		return usrIdToName;
	}

	public void setUsrIdToName(Map<Integer, String> usrIdToName) {
		this.usrIdToName = usrIdToName;
	}
}
