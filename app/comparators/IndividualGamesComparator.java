package comparators;

import java.util.Comparator;
import java.util.List;

import util.Result;

import models.Game;
import models.League;
import models.Team;

public class IndividualGamesComparator implements Comparator<Team> {

	League league;
	
	public IndividualGamesComparator(League league) {
		this.league = league;
	}

	@Override
	public int compare(Team team1, Team team2) {
		List<Game> team1Games = league.getAllGamesWithTeam(team1);
		List<Game> team2Games = league.getAllGamesWithTeam(team2);
		
		team1Games.retainAll(team2Games);

		Integer team1Points = 0;
		Integer team2Points = 0;
		
		for (Game game : team1Games) {
			if (game.getResultFor(team1) == Result.WIN) {
				team1Points += league.sport.getPointsForWin();
				team2Points += league.sport.getPointsForLoss();
			} else if (game.getResultFor(team1) == Result.TIE) {
				team1Points += league.sport.getPointsForTie();
				team2Points += league.sport.getPointsForTie();
			} else {
				team1Points += league.sport.getPointsForLoss();
				team2Points += league.sport.getPointsForWin();
			}
		}
		
		return team1Points.compareTo(team2Points);
	}
}
