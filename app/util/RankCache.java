package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Team;

public class RankCache {

	private List<Team> teamsByRank;
	
	private Map<Team, Integer> bestRanks = new HashMap<Team, Integer>();
	
	public List<Team> getTeamsByRank() {
		return teamsByRank;
	}

	public void cacheTeamRank(List<Team> teamsByRank) {
		this.teamsByRank = teamsByRank;
	}
	
	public void clearCache() {
		teamsByRank = null;
	}

	public Integer getBestRankFor(Team team) {
		return bestRanks.get(team);
	}

	public void cacheBestRank(Team team, int bestRank) {
		this.bestRanks.put(team, bestRank);
	}

}