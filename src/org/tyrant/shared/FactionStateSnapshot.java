package org.tyrant.shared;

import java.io.Serializable;

public class FactionStateSnapshot implements Serializable {
	private static final long serialVersionUID = -7675054142639143362L;

	public FactionStateSnapshot() {
	}

	public FactionStateSnapshot(String name, Integer factionId, Integer rating, Boolean defWarGoing) {
		super();
		this.name = name;
		this.factionId = factionId;
		this.rating = rating;
		this.defWarGoing = defWarGoing;
	}

	public String name;
	public Integer factionId;
	public Integer rating;
	public Boolean defWarGoing;
}