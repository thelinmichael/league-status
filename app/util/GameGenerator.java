package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Game;
import models.League;
import models.Team;

public class GameGenerator {
	public static List<Game> generateAllVsAll(League league) {
		List<Team> teams = league.getTeams();
		
		List<Game> games = new ArrayList<Game>();
		
		int i, j;
		for (i = 0; i < teams.size() - 1; i++) {
			for (j = i+1; j < teams.size(); j++) {
				games.add(new Game(league, Arrays.asList(new Team[] { teams.get(i), teams.get(j) })));
				games.add(new Game(league, Arrays.asList(new Team[] { teams.get(j), teams.get(i) })));
			}
		}
		
		return games;
	}
}