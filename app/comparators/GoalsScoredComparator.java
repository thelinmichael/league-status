package comparators;

import java.util.Comparator;

import models.League;
import models.Team;

public class GoalsScoredComparator implements Comparator<Team> {
	
	private League league;

	public GoalsScoredComparator(League league) {
		this.league = league;
	}

	@Override
	public int compare(Team team1, Team team2) {
		Integer thisTeamGoalsScored = league.getGoalsScoredBy(team1);
		Integer otherTeamGoalsScored = league.getGoalsScoredBy(team2);

		return thisTeamGoalsScored.compareTo(otherTeamGoalsScored);
	}

}
