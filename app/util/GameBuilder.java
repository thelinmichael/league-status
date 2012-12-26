package util;

import java.util.Date;
import java.util.List;

import models.Game;
import models.League;
import models.Team;

public class GameBuilder {
	Game game;
	
	public GameBuilder(League league, Team homeTeam, Team awayTeam) {
		game = new Game(league, homeTeam, awayTeam);
	}
	
	public GameBuilder score(Integer homeTeamScore, Integer awayTeamScore) {
		game.homeTeamScore = homeTeamScore;
		game.awayTeamScore = awayTeamScore;
		return this;
	}
	
	public GameBuilder time(Date time) {
		game.time = time;
		return this;
	}
	
	public Game build() {
		return game;
	}
}
