package models;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.collections.comparators.ComparatorChain;

import play.db.jpa.Model;
import util.FutureRankCalculator;
import util.RankCache;
import util.Result;

import comparators.DateComparator;

import exceptions.GameNotPlayedException;

@Entity
public class League extends Model {
	
	public String name;
	public String displayName;
	
	/* Upper and lower qualification rank represents the 
	 * rank in the table that separates teams to promotion or relegation. 
	 */
	public Integer upperQualificationRank;
	public Integer lowerQualificationRank;

	@ManyToOne
	public Sport sport;
	
	@OneToMany(mappedBy="league")
	public List<Game> games = new ArrayList<Game>();
	
	@ManyToMany(mappedBy="leagues")
	public List<Team> teams;
	
	private transient RankCache rankCache = new RankCache();
	
	public League(String name, Sport sport) {
		this.sport = sport;
		this.name = name;
		this.displayName = makeDisplayName(name);
	}
	
	public String makeDisplayName(String teamName) {
		return teamName.replace(' ', '_').toLowerCase();
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
	
	/* Returns games in order by date, 
	 * most recent game first. 
	 */
	public List<Game> getGamesInChronologicalOrder() {
		List<Game> sortedGames = new ArrayList<Game>(games);
		Collections.sort(sortedGames, new DateComparator());
		Collections.reverse(sortedGames);
		return sortedGames;
	}
	
	public Integer getPointsFor(Team team) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		Integer totalPoints = sport.getPointsAtSeasonStart();
		
		for (Game game : games) {
			if (game.isPlayed() && game.isPlayedBy(team)) {
				Result result = game.getResultFor(team);
				totalPoints += sport.getPointsFor(result);
			} 
		}

		return totalPoints;
	}
	
	public Integer getNumberOfWinsFor(Team team) {
		return getNumberOfResultFor(team, Result.WIN);
	}

	public Integer getNumberOfLossesFor(Team team) {
		return getNumberOfResultFor(team, Result.LOSS);
	}

	public Integer getNumberOfTiesFor(Team team) {
		return getNumberOfResultFor(team, Result.TIE);
	}
	
	private Integer getNumberOfResultFor(Team team, Result result) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		Integer numberOfResult = 0;
		
		for (Game game : games) {
			if (game.isPlayed() && game.isPlayedBy(team) && game.getResultFor(team) == result) {
				numberOfResult++;
			}
		}
		
		return numberOfResult;
	}
	
	public Integer getGoalDifferenceFor(Team team) {
		return (getGoalsScoredBy(team) - getGoalsConcededBy(team));
	}
	
	public Integer getGoalsScoredBy(Team team) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		Integer totalGoals = 0;
		
		for (Game game : games) {
			if (game.isPlayedBy(team)) {
				try {
					totalGoals += game.getGoalsForTeam(team);
				} catch (GameNotPlayedException e) {
					continue;
				}
			}
		}
		return totalGoals;
	}

	public Integer getGoalsConcededBy(Team team) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		Integer totalGoals = 0;
		
		for (Game game : games) {
			if (game.isPlayedBy(team)) {
				try {
					totalGoals += game.getGoalsAgainstTeam(team);
				} catch (GameNotPlayedException e) {
					continue;
				}
			}
		}
		return totalGoals;
	}
	
	public List<Game> getFinishedGamesPlayedBy(Team team) {
		List<Game> gamesPlayedByTeam = getGamesPlayedBy(team);
		
		List<Game> finishedGamesPlayedByTeam = new ArrayList<Game>();
		for (Game game : gamesPlayedByTeam) {
			if (game.isPlayed()) {
				finishedGamesPlayedByTeam.add(game);
			}
		}
		return finishedGamesPlayedByTeam;
 	}
	
	public int getNumberOfFinishedGamesBy(Team team) {
		return getFinishedGamesPlayedBy(team).size();
	}
	
	public List<Game> getGamesPlayedBy(Team team) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		List<Game> gamesPlayedByTeam = new ArrayList<Game>();
		
		for (Game game : games) {
			if (game.isPlayedBy(team)) {
				gamesPlayedByTeam.add(game);
			}
		}
		return gamesPlayedByTeam;
	}
	
	public int getBestPossibleRankFor(Team team) {
		return FutureRankCalculator.getBestPossibleRankFor(this, team);
	}
	
	public int getWorstPossibleRankFor(Team team) {
		return FutureRankCalculator.getWorstPossibleRankFor(this, team);
	}

	public boolean isInUpperQualification(Team team) {
		if (upperQualificationRank == null) { 
			return false;
		}
		return isInUpperQualification(getRankFor(team));
	}

	public boolean isBelowUpperQualification(Team team) {
		if (lowerQualificationRank == null) { 
			return false;
		}
		return isBelowUpperQualification(getRankFor(team));
	}

	public boolean isAboveLowerQualification(Team team) {
		if (upperQualificationRank == null) { 
			return false;
		}
		return isAboveLowerQualification(getRankFor(team));
	}

	public boolean isInLowerQualification(Team team) {
		if (lowerQualificationRank == null) { 
			return false;
		}
		return isInLowerQualification(getRankFor(team));
	}

	public boolean isInUpperQualification(int rank) {
		if (upperQualificationRank == null) { 
			return false;
		}
		return (rank <= upperQualificationRank);
	}

	public boolean isInLowerQualification(int rank) {
		if (lowerQualificationRank == null) { 
			return false;
		}
		return (rank >= lowerQualificationRank);
	}

	public boolean isBelowUpperQualification(int rank) {
		if (upperQualificationRank == null) { 
			return false;
		}
		return (rank > upperQualificationRank);
	}

	public boolean isAboveLowerQualification(int rank) {
		if (upperQualificationRank == null) { 
			return false;
		}
		return (rank < lowerQualificationRank);
	}

	public Integer getRankFor(Team team) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		List<Team> teamsByRank = getTeamsByRank();
		return teamsByRank.indexOf(team) + 1;
	}
	
	public List<Team> getTeamsByRank() {
		if (rankCache == null) {
			rankCache = new RankCache();
		}
		
		/* Cached rank */
		if (rankCache.getTeamsByRank() != null) {
			return rankCache.getTeamsByRank();
		}
		
		List<Comparator<Team>> comparators = getTeamComparators(); 
		
		/* Caching ranks */
		List<Team> teamsByRank = getTeamsByRank(comparators);
		rankCache.cacheTeamRank(teamsByRank);
		
		return teamsByRank;
	}

	public List<Team> getTeamsByRank(List<Comparator<Team>> comparators) {
		return getTeamsByRankForGames(games, comparators);
	}

	public List<Team> getTeamsByRankForGames(List<Game> games) {
		return getTeamsByRankForGames(games, getTeamComparators());
	}
	
	private List<Team> getTeamsByRankForGames(List<Game> games, List<Comparator<Team>> comparators) {
		List<Team> sortedTeams = new ArrayList<Team>(teams);
		
		// Temporarily change the games in this league so that teams are ranked accordingly.
		List<Game> originalGames = new ArrayList<Game>(this.games);
		this.games = games;
		
		ComparatorChain chainedComparators = new ComparatorChain();
		for (Comparator<Team> comparator : comparators) {
			chainedComparators.addComparator(comparator);
		}

		Collections.sort(sortedTeams, chainedComparators);
		Collections.reverse(sortedTeams);
		
		// Change the games back.
		this.games = originalGames;
		
		return sortedTeams;
	}

	/* Get comparators for this league. 
	 * The comparators decide how teams are ranked, e.g.
	 * if points are more valuable than goal difference, 
	 * or goal difference being more valuable than goals scored.
	 */
	private List<Comparator<Team>> getTeamComparators() {
		List<Comparator<Team>> comparators = new ArrayList<Comparator<Team>>();
		List<Class<? extends Comparator<Team>>> comparatorClasses = sport.getComparators();
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
		return comparators;
	}
}