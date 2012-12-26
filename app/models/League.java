package models;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.commons.collections.comparators.ComparatorChain;

import play.db.jpa.Model;
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
	
	private transient List<Team> teamsByRank;
	
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
	
	public List<Team> getTeamsByRank() {
		/* Cached rank */
		if (teamsByRank != null) {
			return teamsByRank;
		}
		
		/* Get comparators for this league. 
		 * The comparators decide how teams are ranked, e.g.
		 * if points are more valuable than goal difference, 
		 * or goal difference being more valuable than goals scored.
		 */
		sport.getComparators();
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
		
		/* Caching ranks */
		teamsByRank = getTeamsByRank(comparators);
		
		return teamsByRank;
	}

	public List<Team> getTeamsByRank(List<Comparator<Team>> comparators) {
		List<Team> sortedTeams = new ArrayList<Team>(teams);
		
		ComparatorChain chainedComparators =  new ComparatorChain();
		for (Comparator<Team> comparator : comparators) {
			chainedComparators.addComparator(comparator);
		}
		
		Collections.sort(sortedTeams, chainedComparators);
		Collections.reverse(sortedTeams);
		
		return sortedTeams;
	}
	
	public int getBestPossibleRankFor(Team team) {
		return getBestPossibleRankFor(team, getAllPossibleGameEndCombinations());
	}
	
	public int getWorstPossibleRankFor(Team team) {
		return getWorstPossibleRankFor(team, getAllPossibleGameEndCombinations());
	}
	
	public int getBestPossibleRankFor(Team team, DefaultMutableTreeNode rootNode) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		Integer bestRank = null;
		
		List<List<Game>> possibleOutcomes = new ArrayList<List<Game>>(); 
		DefaultMutableTreeNode leaf = rootNode.getFirstLeaf();
		while (leaf != null) {
			TreeNode[] pathToRoot = leaf.getPath();
			List<Game> outcome = new ArrayList<Game>();
			for (TreeNode node : pathToRoot) {
				if (node == rootNode) {
					continue;
				} 
				Game gameInNode = (Game) ((DefaultMutableTreeNode) node).getUserObject();
				outcome.add(gameInNode); 
			}
			possibleOutcomes.add(outcome);
			leaf = leaf.getNextLeaf();	
		}
		
		List<Game> oldGames = new ArrayList<Game>(games);
		for (List<Game> outcome : possibleOutcomes) {
			for (Game game : outcome) {
				if (game.isPlayedBy(team) && game.getResultFor(team) == Result.WIN) {
					if (game.homeTeam.equals(team)) {
						game.homeTeamScore = 9999;
					} else {
						game.awayTeamScore = 9999;
					}
				}
			}
			List<Game> playedGames = new ArrayList(getPlayedGames());
			playedGames.addAll(outcome);
			games = playedGames;
			clearRankCache();
			List<Team> teams = getTeamsByRank();
			if (bestRank == null || (teams.indexOf(team) + 1) < bestRank) {
				bestRank = teams.indexOf(team) + 1;
			}
			games = new ArrayList<Game>(oldGames);
		}
		
		return bestRank;
	}

	public Integer getWorstPossibleRankFor(Team team, DefaultMutableTreeNode rootNode) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		Integer worstRank = null;
		
		List<List<Game>> possibleOutcomes = new ArrayList<List<Game>>(); 
		DefaultMutableTreeNode leaf = rootNode.getFirstLeaf();
		while (leaf != null) {
			TreeNode[] pathToRoot = leaf.getPath();
			List<Game> outcome = new ArrayList<Game>();
			for (TreeNode node : pathToRoot) {
				if (node == rootNode) {
					continue;
				} 
				Game gameInNode = (Game) ((DefaultMutableTreeNode) node).getUserObject();
				outcome.add(gameInNode); 
			}
			possibleOutcomes.add(outcome);
			leaf = leaf.getNextLeaf();	
		}
		
		List<Game> oldGames = new ArrayList<Game>(games);
		for (List<Game> outcome : possibleOutcomes) {
			for (Game game : outcome) {
				if (game.isPlayedBy(team) && game.getResultFor(team) == Result.LOSS) {
					if (game.homeTeam.equals(team)) {
						game.awayTeamScore = 9999;
					} else {
						game.homeTeamScore = 9999;
					}
				}
			}
			
			List<Game> playedGames = new ArrayList(getPlayedGames());
			playedGames.addAll(outcome);
			games = playedGames;
			clearRankCache();
			List<Team> teams = getTeamsByRank();
			if (worstRank == null || (teams.indexOf(team) + 1) > worstRank) {
				worstRank = teams.indexOf(team) + 1;
			}
			games = new ArrayList<Game>(oldGames);
		}
		
		return worstRank;
	}
	
	/* A tree structure where every node represents a finished game, 
	 * except for the root node which is empty. 
	 */
	public DefaultMutableTreeNode getAllPossibleGameEndCombinations() {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		addPossibleCombinations(getRemainingGames(), rootNode);
		return rootNode;
	}

	private void addPossibleCombinations(List<Game> remainingGames, DefaultMutableTreeNode rootNode) {
		if (remainingGames == null || remainingGames.size() == 0) {
			return;
		}
		
		Game game = remainingGames.get(0);
		remainingGames.remove(0);
		
		Enumeration enumeration = rootNode.depthFirstEnumeration();
		List<DefaultMutableTreeNode> leafs = new ArrayList<DefaultMutableTreeNode>();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) enumeration.nextElement());
			if (node.isLeaf()){
				leafs.add(node);
			}
		}
		
		for (DefaultMutableTreeNode leaf : leafs) { 
			Game possibleOutcome1 = new Game(game.league, game.homeTeam, game.awayTeam);
			Game possibleOutcome2 = new Game(game.league, game.homeTeam, game.awayTeam);
			Game possibleOutcome3 = new Game(game.league, game.homeTeam, game.awayTeam);
			possibleOutcome1.homeTeamScore = 0;
			possibleOutcome1.awayTeamScore = 0;
			possibleOutcome2.homeTeamScore = 1;
			possibleOutcome2.awayTeamScore = 0;
			possibleOutcome3.homeTeamScore = 0;
			possibleOutcome3.awayTeamScore = 1;
			DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(possibleOutcome1);
			DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(possibleOutcome2);
			DefaultMutableTreeNode node3 = new DefaultMutableTreeNode(possibleOutcome3);
			
			leaf.add(node1);
			leaf.add(node2);
			leaf.add(node3);
		}
		
		addPossibleCombinations(remainingGames, rootNode);
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
	
	public void clearRankCache() {
		teamsByRank = null;
	}
}