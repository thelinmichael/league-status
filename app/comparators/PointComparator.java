package comparators;

import java.util.Comparator;

import models.League;
import models.Team;

public class PointComparator implements Comparator<Team> {

	League league;
	
	public PointComparator(League league) {
		this.league = league;
	}
	
	@Override
	public int compare(Team team1, Team team2) {
		return league.getPointsForTeam(team1).compareTo(league.getPointsForTeam(team2));
	}

}
