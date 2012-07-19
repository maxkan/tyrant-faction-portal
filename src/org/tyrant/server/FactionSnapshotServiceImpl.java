package org.tyrant.server;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.max.server.basic.FactionPortal;
import org.max.server.processors.WarHistoryProcessor;
import org.tyrant.client.FactionSnapshotService;
import org.tyrant.shared.FactionDTO;
import org.tyrant.shared.UserWarResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class FactionSnapshotServiceImpl extends RemoteServiceServlet implements FactionSnapshotService {

	public FactionDTO greetServer(String input) throws IllegalArgumentException {

		FactionPortal portal = new FactionPortal(UserData.userId, UserData.flashcode, UserData.authToken);
		try {
			FactionDTO factionDTO = new FactionDTO();
			WarHistoryProcessor warHistory = portal.getWarsHistory();
			factionDTO.userResultsForLastWars = warHistory.getLastWarsResults(30);
			factionDTO.statsHtml = new HtmlGenerator().getWarResultsHtml(warHistory.getWarIdToName(), warHistory.getUsrResults(), warHistory.getWarResults(),
					usrLastResultsAsMap(factionDTO.userResultsForLastWars), portal.factionMembers, 30);
			return factionDTO;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private Map<Integer, UserWarResult> usrLastResultsAsMap(List<UserWarResult> list) {
		Map<Integer, UserWarResult> map = new LinkedHashMap<Integer, UserWarResult>();
		for (UserWarResult userWarResult : list) {
			map.put(userWarResult.getUserId(), userWarResult);
		}
		return map;
	}
}
