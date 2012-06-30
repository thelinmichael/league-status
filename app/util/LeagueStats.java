package util;

import java.util.ArrayList;
import java.util.List;

import models.Game;
import models.League;

public class LeagueStats {

	private League league;

	public LeagueStats(League league) {
		this.league = league;
	}
	
	public List<Game> getPlayedGames() {
		List<Game> playedGames = new ArrayList<Game>();
		for (Game game : league.getGames()) {
			if (game.isPlayed()) {
				playedGames.add(game);
			}
		}
 		return playedGames;
	}
	
	public List<Game> getRemainingGames() {
		List<Game> remainingGames = new ArrayList<Game>();
		for (Game game : league.getGames()) {
			if (!game.isPlayed()) {
				remainingGames.add(game);
			}
		}
		return remainingGames;
	}
}
