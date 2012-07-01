package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;
import util.Result;

@Entity
public class League extends Model {
	
	public String name;
	
	@ManyToOne
	public Sport sport;
	
	public String displayName;
	
	@OneToMany(mappedBy="league")
	public List<Game> games;
	
	@OneToMany(mappedBy="league")
	public List<Team> teams;
	
	public League(String name, Sport sport) {
		this.sport = sport;
		this.name = name;
		this.displayName = name.replace(' ', '_').toLowerCase();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public List<Team> getTeams() {
		return this.teams;
	}
	
	public List<Game> getGames() {
		return this.games;
	}

	public void addGame(Game game) {
		if (games == null) {
			games = new ArrayList<Game>();
		}
		games.add(game);
	}
	
	public void addGames(List<Game> games) {
		if (this.games == null) {
			this.games = new ArrayList<Game>();
		}
		this.games.addAll(games);
	}
	
	public List<Game> getPlayedGames() {
		List<Game> playedGames = new ArrayList<Game>();
		for (Game game : getGames()) {
			if (game.isPlayed()) {
				playedGames.add(game);
			}
		}
 		return playedGames;
	}
	
	public List<Game> getRemainingGames() {
		List<Game> remainingGames = new ArrayList<Game>();
		for (Game game : getGames()) {
			if (!game.isPlayed()) {
				remainingGames.add(game);
			}
		}
		return remainingGames;
	}
	
	public List<Game> getGamesInChronologicalOrder() {
		if (games == null) {
			games = new ArrayList<Game>();
		}
		List<Game> sortedGames = new ArrayList<Game>(games);
		Collections.sort(sortedGames);
		
		return sortedGames;
	}
	
	public Integer getPointsForTeam(Team team) {
		Integer points = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				Result result = game.getResultFor(team);
				points += sport.getPointsFor(result);
			}
		}
		
		return points;
	}

	public Integer getWinsForTeam(Team team) {
		Integer wins = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				Result result = game.getResultFor(team);
				if (result == Result.WIN) {
					wins++;
				}
			}
		}
		
		return wins;
	}

	public Integer getLossesForTeam(Team team) {
		Integer losses = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				Result result = game.getResultFor(team);
				if (result == Result.LOSS) {
					losses++;
				}
			}
		}
		
		return losses;
	}

	public Integer getTiesForTeam(Team team) {
		Integer ties = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				Result result = game.getResultFor(team);
				if (result == Result.TIE) {
					ties++;
				}
			}
		}
		
		return ties;
	}
}