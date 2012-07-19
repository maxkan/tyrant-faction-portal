package org.max.server.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.max.server.processors.WarHistoryProcessor;

import tyrant.shared.Application;
import tyrant.shared.TyrantSession;

public class FactionPortal extends Application {
	public Map<Integer, String> factionMembers;

	public FactionPortal(String userId, String flashCode, String kongGameAuthToken) {
		super(userId, flashCode, kongGameAuthToken);
	}

	@Override
	public void init(TyrantSession session, JsonNode rootNode) {
		Iterator<JsonNode> members = rootNode.get("faction_info").get("members").get("members").getElements();
		factionMembers = new HashMap<Integer, String>();
		while (members.hasNext()) {
			JsonNode member = members.next();
			factionMembers.put(member.get("user_id").asInt(), member.get("name").asText());
		}
	}

	public WarHistoryProcessor updateWarsHistory() {
		WarHistoryProcessor whp = new WarHistoryProcessor(session);
		whp.loadWarsHistory();
		return whp;
	}

	public WarHistoryProcessor getWarsHistory() {
		return new WarHistoryProcessor(session);

	}

}
