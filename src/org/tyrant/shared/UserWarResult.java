package org.tyrant.shared;

import java.io.Serializable;

public class UserWarResult implements Serializable {
	private static final long serialVersionUID = 1L;

	public String userName;
	public Integer userId = 0;
	public Integer wins = 0;
	public Integer losses = 0;
	public Integer points = 0;
	public Integer pointsAgainst = 0;
	public Integer battlesFought = 0;
	public Integer factionWarId = 0;

	public UserWarResult(Integer userId, Integer wins, Integer losses, Integer points, Integer pointsAgainst, Integer battlesFought, Integer factionWarId) {
		super();
		this.userId = userId;
		this.wins = wins;
		this.losses = losses;
		this.points = points;
		this.pointsAgainst = pointsAgainst;
		this.battlesFought = battlesFought;
		this.factionWarId = factionWarId;
	}

	public UserWarResult() {
	}

	public void add(UserWarResult res) {
		userId = res.userId;
		userName = res.userName;
		factionWarId = res.factionWarId;
		wins += res.wins;
		losses += res.losses;
		points += res.points;
		pointsAgainst += res.pointsAgainst;
		battlesFought += res.battlesFought;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getWins() {
		return wins;
	}

	public void setWins(Integer wins) {
		this.wins = wins;
	}

	public Integer getLosses() {
		return losses;
	}

	public void setLosses(Integer losses) {
		this.losses = losses;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Integer getPointsAgainst() {
		return pointsAgainst;
	}

	public void setPointsAgainst(Integer pointsAgainst) {
		this.pointsAgainst = pointsAgainst;
	}

	public Integer getBattlesFought() {
		return battlesFought;
	}

	public void setBattlesFought(Integer battlesFought) {
		this.battlesFought = battlesFought;
	}

	public Integer getFactionWarId() {
		return factionWarId;
	}

	public void setFactionWarId(Integer factionWarId) {
		this.factionWarId = factionWarId;
	}
}