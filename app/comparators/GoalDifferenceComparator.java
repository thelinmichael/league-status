package comparators;

import java.util.Comparator;

import models.League;
import models.Team;

public class GoalDifferenceComparator implements Comparator<Team> {

	private League league;

	public GoalDifferenceComparator(League league) {
		this.league = league;
	}
	
	@Override
	public int compare(Team team1, Team team2) {
		Integer team1GoalDifference = league.getGoalsScoredBy(team1) - league.getGoalsConcededBy(team1);
		Integer team2GoalDifference = league.getGoalsScoredBy(team2) - league.getGoalsConcededBy(team2);
		
		return team1GoalDifference.compareTo(team2GoalDifference);
	}

}