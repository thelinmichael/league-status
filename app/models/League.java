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

import comparators.ChronoComparator;

import exceptions.GameNotPlayedException;

@Entity
public class League extends Model {
	
	public String name;
	public String displayName;
	
	public Integer upperQualificationRank;
	public Integer lowerQualificationRank;

	@ManyToOne
	public Sport sport;
	
	@OneToMany(mappedBy="league")
	public List<Game> games;
	
	@ManyToMany(mappedBy="leagues")
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
	
	public Integer getGoalDifferenceForTeam(Team team) {
		return (getGoalsScoredByTeam(team) - getGoalsScoredAgainstTeam(team));
	}
	
	public Integer getGoalsScoredByTeam(Team team) {
		Integer goals = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				try {
					goals += game.getGoalsForTeam(team);
				} catch (GameNotPlayedException e) {}
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
				try {
					goals += game.getGoalsAgainstTeam(team);
				} catch (GameNotPlayedException e) {}
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
		ComparatorChain chainedComparators =  new ComparatorChain();
		for (Comparator<Team> comparator : comparators) {
			chainedComparators.addComparator(comparator);
		}
		Collections.sort(sortedTeams, chainedComparators);
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
			Game possibleOutcome1 = new Game(game.league, game.teams);
			Game possibleOutcome2 = new Game(game.league, game.teams);
			Game possibleOutcome3 = new Game(game.league, game.teams);
			possibleOutcome1.setScore(Arrays.asList(0,0));
			possibleOutcome2.setScore(Arrays.asList(1,0));
			possibleOutcome3.setScore(Arrays.asList(0,1));
			DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(possibleOutcome1);
			DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(possibleOutcome2);
			DefaultMutableTreeNode node3 = new DefaultMutableTreeNode(possibleOutcome3);
			
			leaf.add(node1);
			leaf.add(node2);
			leaf.add(node3);
		}
		
		addPossibleCombinations(remainingGames, rootNode);
	}

	public int getBestPossibleRankForTeam(Team team) {
		return getBestPossibleRankForTeam(team, getAllPossibleGameEndCombinations());
	}
	
	public int getWorstPossibleRankForTeam(Team team) {
		return getWorstPossibleRankForTeam(team, getAllPossibleGameEndCombinations());
	}
	
	public int getBestPossibleRankForTeam(Team team, DefaultMutableTreeNode rootNode) {
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
				if (game.teams.contains(team) && game.getResultFor(team) == Result.WIN) {
					game.scores.set(game.teams.indexOf(team), new Score(9999)); 
				}
			}
			List<Game> playedGames = new ArrayList(getPlayedGames());
			playedGames.addAll(outcome);
			games = playedGames;
			List<Team> teams = getTeamsByRank();
			if (bestRank == null || (teams.indexOf(team) + 1) < bestRank) {
				bestRank = teams.indexOf(team) + 1;
			}
			games = new ArrayList<Game>(oldGames);
		}
		
		return bestRank;
	}

	public Integer getWorstPossibleRankForTeam(Team team, DefaultMutableTreeNode rootNode) {
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
				if (game.teams.contains(team) && game.getResultFor(team) == Result.LOSS) {
					for (Team teamInGame : game.teams) {
						if (!teamInGame.equals(team)) { 
							game.scores.set(game.teams.indexOf(teamInGame), new Score(9999)); 
						}
					}
				}
			}
			List<Game> playedGames = new ArrayList(getPlayedGames());
			playedGames.addAll(outcome);
			games = playedGames;
			List<Team> teams = getTeamsByRank();
			if (worstRank == null || (teams.indexOf(team) + 1) > worstRank) {
				worstRank = teams.indexOf(team) + 1;
			}
			games = new ArrayList<Game>(oldGames);
		}
		
		return worstRank;
	}

	public boolean isInUpperQualification(Team team) {
		if (upperQualificationRank == null) { 
			return false;
		}
		return isInUpperQualification(getRankForTeam(team));
	}

	public boolean isBelowUpperQualification(Team team) {
		if (lowerQualificationRank == null) { 
			return false;
		}
		return isBelowUpperQualification(getRankForTeam(team));
	}

	public boolean isAboveLowerQualification(Team team) {
		if (upperQualificationRank == null) { 
			return false;
		}
		return isAboveLowerQualification(getRankForTeam(team));
	}

	public boolean isInLowerQualification(Team team) {
		if (lowerQualificationRank == null) { 
			return false;
		}
		return isInLowerQualification(getRankForTeam(team));
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

	public Integer getRankForTeam(Team team) {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		List<Team> teamsByRank = getTeamsByRank();
		return teamsByRank.indexOf(team) + 1;
	}
}