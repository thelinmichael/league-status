package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Team;

public class RankCache {

	private List<Team> teamsByRank;
	
	private Map<Team, Integer> bestRanks = new HashMap<Team, Integer>();

	private Map<Team, Integer> worstRanks = new HashMap<Team, Integer>();
	
	public List<Team> getTeamsByRank() {
		return teamsByRank;
	}

	public void cacheTeamRank(List<Team> teamsByRank) {
		this.teamsByRank = teamsByRank;
	}
	
	public void clearCache() {
		this.teamsByRank = null;
		this.worstRanks.clear();
		this.bestRanks.clear();
	}

	public Integer getBestRankFor(Team team) {
		return bestRanks.get(team);
	}
	
	public Integer getWorstRankFor(Team team) {
		return worstRanks.get(team);
	}

	public void cacheBestRank(Team team, int bestRank) {
		this.bestRanks.put(team, bestRank);
	}

	public void cacheWorstRank(Team team, int worstRank) {
		this.worstRanks.put(team, worstRank);
	}
}