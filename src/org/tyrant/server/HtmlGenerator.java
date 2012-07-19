package org.tyrant.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tyrant.shared.UserWarResult;

public class HtmlGenerator {
	public String getWarResultsHtml(Map<Integer, String> warIdToName, Map<Integer, Map<Integer, UserWarResult>> usrResults,
			Map<Integer, Map<Integer, UserWarResult>> warResults, Map<Integer, UserWarResult> usrLastResults, Map<Integer, String> usrIdToName, Integer wars) {

		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><style>.fourth td{border-bottom:1px dashed gray; white-space: nowrap;}; th{background-color: gray};}</style></head><body>");

		sb.append("<table id=\"detailsTbl\" cellspacing=\"2\" >");
		sb.append("<th>").append("</th>");

		List<Entry<Integer, UserWarResult>> el = new ArrayList<Entry<Integer, UserWarResult>>(usrLastResults.entrySet());
		for (Entry<Integer, UserWarResult> i : el) {
			Integer usr = i.getKey();
			UserWarResult r = usrLastResults.get(usr);
			String reshtml = "<span style=\"font-size: 70%; font-weight: bold; color: brown\">(" + (r.points - r.pointsAgainst) + ")</span>";
			sb.append("<th><b>").append(usrIdToName.get(usr)).append("</b>" + reshtml + "</th>");
		}

		sb.append("\n");

		int day = 0;
		int cnt = 1;
		for (Integer warId : warResults.keySet()) {
			if (cnt++ > wars) {
				break;
			}
			sb.append("<tr" + (++day % 4 == 0 ? " class=\"fourth\"" : "") + ">");
			sb.append("<td>").append("<b>").append(warIdToName.get(warId)).append("</b>").append("</td>");
			int i = 0;
			for (Entry<Integer, UserWarResult> ii : el) {
				Integer usr = ii.getKey();
				UserWarResult r = warResults.get(warId).get(usr);

				boolean b = r == null;
				boolean a = !b && ((r.points - r.pointsAgainst) < 0);
				sb.append("<td style=\"background:" + (i++ % 2 == 1 ? "#EAEAEA" : "#EEEEEE") + " ;color:" + (a ? "brown" : "black") + "\">").append(
						b ? "" : (r.points));
				if (!b) {
					if (a) {
						sb.append("<b>");
					}
					sb.append("(").append(r.points - r.pointsAgainst).append(")");
					sb.append("<span style=\"font-size: 70%; font-weight: bold;\">");
					sb.append("(" + r.wins).append("/" + r.losses + ")");
					sb.append("</span>");
					if (a) {
						sb.append("</b>");
					}
				} else {
					sb.append("-");
				}
				sb.append("</td>");
			}
			sb.append("</tr>");
			sb.append("\n");
		}
		sb.append("</table>");

		sb.append("</body></html>");
		return sb.toString();
	}
}
