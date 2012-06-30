package util;

import java.util.ArrayList;
import java.util.List;

import models.League;
import models.Team;

public class Stats {

	public static LeagueStats getLeagueStats(League league) {
		return new LeagueStats(league);
	}

	public static TeamStats getTeamStats(Team team) {
		return new TeamStats(team);
	}
}
