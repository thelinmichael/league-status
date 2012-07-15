package models;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import comparators.ChronoComparator;

import play.db.jpa.Model;
import util.ChainedComparator;
import util.Result;

@Entity
public class League extends Model {
	
	public String name;
	
	public String displayName;

	@ManyToOne
	public Sport sport;
	
	@OneToMany(mappedBy="league")
	public List<Game> games;
	
	@OneToMany(mappedBy="league")
	public List<Team> teams;
	
	public League(String name, Sport sport) {
		this.sport = sport;
		this.name = name;
		this.displayName = makeDisplayName(name);
	}
	
	public String makeDisplayName(String teamName) {
		return teamName.replace(' ', '_').toLowerCase();
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
		for (Game game : games) {
			if (game.isPlayed()) {
				playedGames.add(game);
			}
		}
 		return playedGames;
	}
	
	public List<Game> getRemainingGames() {
		List<Game> remainingGames = new ArrayList<Game>();
		for (Game game : games) {
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
		Collections.sort(sortedGames, new ChronoComparator());
		Collections.reverse(sortedGames);
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
		return getResultForTeam(team, Result.WIN);
	}

	public Integer getLossesForTeam(Team team) {
		return getResultForTeam(team, Result.LOSS);
	}

	public Integer getTiesForTeam(Team team) {
		return getResultForTeam(team, Result.TIE);
	}
	
	private Integer getResultForTeam(Team team, Result result) {
		Integer numberOfResult = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team) && game.getResultFor(team) == result) {
				numberOfResult++;
			}
		}
		
		return numberOfResult;
	}
	
	public Integer getGoalsScoredByTeam(Team team) {
		Integer goals = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				goals += game.getGoalsForTeam(team);
			}
		}
		return goals;
	}

	public Integer getGoalsScoredAgainstTeam(Team team) {
		Integer goals = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				goals += game.getGoalsAgainstTeam(team);
			}
		}
		return goals;
	}
	
	public List<Team> getTeamsByRank() {
		List<Class<? extends Comparator<Team>>> comparatorClasses = sport.getComparators();
		List<Comparator<Team>> comparators = new ArrayList<Comparator<Team>>();
		for (Class<? extends Comparator<Team>> comparatorClass : comparatorClasses) {
			Constructor constructor;
			try {
				constructor = comparatorClass.getDeclaredConstructor(League.class);
				constructor.setAccessible(true);
				try {
					comparators.add((Comparator<Team>) constructor.newInstance(this));
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		return getTeamsByRank(comparators);
	}

	public List<Team> getTeamsByRank(List<Comparator<Team>> comparators) {
		List<Team> sortedTeams = new ArrayList<Team>(teams);
		Collections.sort(sortedTeams, new ChainedComparator(comparators));
		Collections.reverse(sortedTeams);
		return sortedTeams;
	}
	
	public List<Game> getFinishedGamesWithTeam(Team team) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		List<Game> returnedGames = new ArrayList<Game>();
		for (Game game : games) {
			if (game.teams.contains(team) && game.isPlayed()) {
				returnedGames.add(game);
			}
		}
		return returnedGames;
 	}
	
	public List<Game> getAllGamesWithTeam(Team team) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		List<Game> returnedGames = new ArrayList<Game>();
		
		for (Game game : games) {
			if (game.teams.contains(team)) {
				returnedGames.add(game);
			}
		}
		return returnedGames;
	}

}