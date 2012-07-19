package org.tyrant.shared;

import java.io.Serializable;
import java.util.List;

public class FactionDTO implements Serializable {
	private static final long serialVersionUID = 4138449933703241241L;
	public String statsHtml;
	public List<UserWarResult> userResultsForLastWars;
}
