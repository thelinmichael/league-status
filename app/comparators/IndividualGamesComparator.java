package comparators;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import models.Game;
import models.League;
import models.Team;
import util.Result;

public class IndividualGamesComparator implements Comparator<Team> {

	League league;
	
	public IndividualGamesComparator(League league) {
		this.league = league;
	}

	@Override
	public int compare(Team team1, Team team2) {
		List<Game> team1Games = league.getAllGamesPlayedBy(team1);
		List<Game> team2Games = league.getAllGamesPlayedBy(team2);
				
		Set<Game> games = Sets.intersection(new HashSet(team1Games), new HashSet(team2Games));

		Integer team1Points = league.sport.getPointsAtSeasonStart();
		Integer team2Points = league.sport.getPointsAtSeasonStart();
		
		for (Game game : games) {
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