package util;

import java.util.ArrayList;
import java.util.List;

import models.Game;
import models.League;
import models.Team;

public class TeamRank implements Comparable {

	private List<StatsPriority> priorities;
	private Team team;
	private League league;

	public TeamRank(Team team, List<StatsPriority> priorities, League league) {
		this.team = team;
		this.priorities = priorities;
		this.league = league;
	}

	public Team getTeam() {
		return team;
	}

	@Override
	public int compareTo(Object other) {
		TeamRank otherTeamRank = (TeamRank) other;

		if (priorities.size() == 0) {
			return 0;
		}

		int difference; 

		switch (priorities.get(0)) {
		case POINTS: {
			difference = league.getPointsForTeam(team).compareTo(league.getPointsForTeam(otherTeamRank.team));
			break;
		}
		case GOALS_SCORED: {
			Integer thisTeamGoalsScored = league.getGoalsScoredByTeam(team);
			Integer otherTeamGoalsScored = league.getGoalsScoredByTeam(otherTeamRank.team);

			difference = thisTeamGoalsScored.compareTo(otherTeamGoalsScored);
			break;
		}
		case GOAL_DIFFERENCE: {
			Integer thisTeamGoalDifference = league.getGoalsScoredByTeam(team) - league.getGoalsScoredAgainstTeam(team);
			Integer otherTeamGoalDifference = league.getGoalsScoredByTeam(otherTeamRank.team) - league.getGoalsScoredAgainstTeam(otherTeamRank.team);
			difference = thisTeamGoalDifference.compareTo(otherTeamGoalDifference);
			break;
		}
		case INDIVIDUAL_GAMES_BETWEEN_TEAMS: {
			List<Game> thisTeamsGames = league.getAllGamesWithTeam(team);
			List<Game> otherTeamsGames = league.getAllGamesWithTeam(otherTeamRank.team);
			thisTeamsGames.retainAll(otherTeamsGames);

			Integer thisTeamPoints = 0;
			Integer otherTeamPoints = 0;
			for (Game game : thisTeamsGames) {
				if (game.getResultFor(team) == Result.WIN) {
					thisTeamPoints += league.sport.getPointsForWin();
					otherTeamPoints += league.sport.getPointsForLoss();
				} else if (game.getResultFor(team) == Result.TIE) {
					thisTeamPoints += league.sport.getPointsForTie();
					otherTeamPoints += league.sport.getPointsForTie();
				} else {
					thisTeamPoints += league.sport.getPointsForLoss();
					otherTeamPoints += league.sport.getPointsForWin();
				}
			}
			difference = thisTeamPoints.compareTo(otherTeamPoints);
			break;
		}
		default: {
			throw new IllegalArgumentException("Can't handle this priority.");
		}
		}

		if (difference == 0) {
			List<StatsPriority> newPriorities = new ArrayList<StatsPriority>();
			for (int i = 1; i < priorities.size(); i++) {
				newPriorities.add(priorities.get(i));
			}
			priorities = newPriorities;
			return compareTo(otherTeamRank);
		} else {
			return difference;
		}
	}
}
