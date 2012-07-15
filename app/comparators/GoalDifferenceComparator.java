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
		Integer team1GoalDifference = league.getGoalsScoredByTeam(team1) - league.getGoalsScoredAgainstTeam(team1);
		Integer team2GoalDifference = league.getGoalsScoredByTeam(team2) - league.getGoalsScoredAgainstTeam(team2);
		return team1GoalDifference.compareTo(team2GoalDifference);
	}

}
