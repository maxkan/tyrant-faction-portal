package org.max.server.processors;

import static tyrant.shared.RequestProfile.toMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.jackson.JsonNode;
import org.tyrant.shared.UserWarResult;

import tyrant.shared.RequestProfile;
import tyrant.shared.TyrantSession;
import tyrant.shared.utils.Storage;

public class WarHistoryProcessor implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int MAX_WARS = 30;

	private Map<Integer, Map<Integer, UserWarResult>> usrResults = new LinkedHashMap<Integer, Map<Integer, UserWarResult>>();
	private Map<Integer, Map<Integer, UserWarResult>> warResults = new TreeMap<Integer, Map<Integer, UserWarResult>>(Collections.reverseOrder());
	private Map<Integer, String> warIdToName = new LinkedHashMap<Integer, String>();
	private Set<Integer> envolvedUserIDs = new HashSet<Integer>();

	private final transient TyrantSession session;

	public WarHistoryProcessor(TyrantSession session) {
		WarHistoryProcessor wps = Storage.deserializeSingleton(WarHistoryProcessor.class);
		if (wps != null) {
			usrResults = wps.usrResults;
			warResults = wps.warResults;
		}
		this.session = session;
		// loadWarsHistory();
	}

	public List<UserWarResult> getLastWarsResults(int numWars) {
		List<UserWarResult> toReturn = new ArrayList<UserWarResult>(usrResults.keySet().size());
		Iterator<Integer> users = usrResults.keySet().iterator();
		while (users.hasNext()) {
			Integer userId = users.next();

			int i = 0;
			UserWarResult ur = new UserWarResult();
			Iterator<Entry<Integer, Map<Integer, UserWarResult>>> it = warResults.entrySet().iterator();
			while (it.hasNext() && ++i <= numWars) {
				UserWarResult warResult = it.next().getValue().get(userId);
				if (warResult != null) {
					ur.setUserName(warResult.userName);
					ur.add(warResult);
				}
			}
			if (ur.getUserName() != null) {
				toReturn.add(ur);
			}
		}
		Collections.sort(toReturn, new Comparator<UserWarResult>() {
			@Override
			public int compare(UserWarResult o1, UserWarResult o2) {
				return new Integer(o2.getPoints() - o2.getPointsAgainst()).compareTo(new Integer(o1.getPoints() - o1.getPointsAgainst()));
			}
		});

		return toReturn;
	}

	public void loadWarsHistory() {
		boolean changed = false;
		List<Integer> warsIds = getUnprocessedWarsIds();
		for (Integer warId : warsIds) {
			if (fillDetailedWarHistory(warId, session.getFactionId())) {
				changed = true;
			}
		}
		enrichWithNames();

		Map<Integer, Map<Integer, UserWarResult>> res = new LinkedHashMap<Integer, Map<Integer, UserWarResult>>();
		for (Integer id : warIdToName.keySet()) {
			res.put(id, warResults.get(id));
		}
		warResults = res;

		if (changed) {
			Storage.serializeSingleton(this);
		}
	}

	private void enrichWithNames() {
		UserNameProcessor nameProcessor = new UserNameProcessor(session);
		nameProcessor.fillUserNames(envolvedUserIDs);
		for (Map<Integer, UserWarResult> i : warResults.values()) {
			if (i != null) {
				for (UserWarResult res : i.values()) {
					if (res != null && res.userName == null) {
						res.userName = nameProcessor.getName(res.userId);
					}
				}
			}
		}
	}

	private boolean fillDetailedWarHistory(Integer warId, int factionId) {
		boolean changed = false;
		JsonNode oldWar = new RequestProfile(session, "getFactionWarRankings", toMap("faction_war_id", warId.toString())).getJsonResponse();
		Iterator<JsonNode> factionResults = oldWar.get("rankings").get(String.valueOf(factionId)).getElements();
		System.out.println("getting war details, war id:" + warId);
		while (factionResults.hasNext()) {
			JsonNode userRes = factionResults.next();
			Integer userId = Integer.valueOf(userRes.get("user_id").getTextValue());
			Integer wins = Integer.valueOf(userRes.get("wins").getTextValue());
			Integer losses = Integer.valueOf(userRes.get("losses").getTextValue());
			Integer points = Integer.valueOf(userRes.get("points").getTextValue());
			Integer pointsAgainst = Integer.valueOf(userRes.get("points_against").getTextValue());
			Integer battlesFought = Integer.valueOf(userRes.get("battles_fought").getTextValue());
			Integer factionWarId = Integer.valueOf(userRes.get("faction_war_id").getTextValue());
			envolvedUserIDs.add(userId);

			UserWarResult res = new UserWarResult(userId, wins, losses, points, pointsAgainst, battlesFought, factionWarId);

			Map<Integer, UserWarResult> warToResult = usrResults.get(userId);
			if (warToResult == null) {
				warToResult = new LinkedHashMap<Integer, UserWarResult>();
				usrResults.put(userId, warToResult);
			}
			warToResult.put(factionWarId, res);

			Map<Integer, UserWarResult> usrToResult = warResults.get(factionWarId);
			if (usrToResult == null) {
				usrToResult = new LinkedHashMap<Integer, UserWarResult>();
				warResults.put(factionWarId, usrToResult);
				changed = true;
			}
			usrToResult.put(userId, res);
		}
		return changed;
	}

	private List<Integer> getUnprocessedWarsIds() {
		List<Integer> unprocessedWars = new LinkedList<Integer>();
		JsonNode oldWars = new RequestProfile(session, "getOldFactionWars", toMap("?", "")).getJsonResponse();
		Iterator<JsonNode> wars = oldWars.get("wars").getElements();
		int i = 0;
		while (wars.hasNext() && i < MAX_WARS) {
			JsonNode war = wars.next();
			Integer warId = war.get("faction_war_id").asInt();
			String name = war.get("name").asText();
			warIdToName.put(warId, name);
			if (!warResults.containsKey(warId)) {
				unprocessedWars.add(warId);
			}
			i++;
		}
		return unprocessedWars;
	}

	public Map<Integer, Map<Integer, UserWarResult>> getUsrResults() {
		return usrResults;
	}

	public void setUsrResults(Map<Integer, Map<Integer, UserWarResult>> usrResults) {
		this.usrResults = usrResults;
	}

	public Map<Integer, Map<Integer, UserWarResult>> getWarResults() {
		return warResults;
	}

	public void setWarResults(Map<Integer, Map<Integer, UserWarResult>> warResults) {
		this.warResults = warResults;
	}

	public Map<Integer, String> getWarIdToName() {
		return warIdToName;
	}

	public void setWarIdToName(Map<Integer, String> warIdToName) {
		this.warIdToName = warIdToName;
	}
}
