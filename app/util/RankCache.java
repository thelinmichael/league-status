package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;

import models.League;
import models.Team;

public class RankCache {

	private List<Team> teamsByRank;
	
	public List<Team> getTeamsByRank() {
		return teamsByRank;
	}

	public void cacheTeamRank(List<Team> teamsByRank) {
		this.teamsByRank = teamsByRank;
	}
	
	public void clearCache() {
		teamsByRank = null;
	}

}