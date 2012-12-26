package util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import models.Game;
import models.League;
import models.Team;

public class RankCalculator {

	public static int getBestPossibleRankFor(League league, Team team, DefaultMutableTreeNode rootNode) {
		if (!league.teams.contains(team)) {
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
		
		List<Game> oldGames = new ArrayList<Game>(league.games);
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
			List<Game> playedGames = new ArrayList(league.getPlayedGames());
			playedGames.addAll(outcome);
			league.games = playedGames;
			league.clearRankCache();
			List<Team> teams = league.getTeamsByRank();
			if (bestRank == null || (teams.indexOf(team) + 1) < bestRank) {
				bestRank = teams.indexOf(team) + 1;
			}
			league.games = new ArrayList<Game>(oldGames);
		}
		
		return bestRank;
	}

	public static int getWorstPossibleRankFor(League league, Team team, DefaultMutableTreeNode rootNode) {
		if (!league.teams.contains(team)) {
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
		
		List<Game> oldGames = new ArrayList<Game>(league.games);
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
			
			List<Game> playedGames = new ArrayList(league.getPlayedGames());
			playedGames.addAll(outcome);
			league.games = playedGames;
			league.clearRankCache();
			List<Team> teams = league.getTeamsByRank();
			if (worstRank == null || (teams.indexOf(team) + 1) > worstRank) {
				worstRank = teams.indexOf(team) + 1;
			}
			league.games = new ArrayList<Game>(oldGames);
		}
		
		return worstRank;
	}
	
	/* A tree structure where every node represents a finished game, 
	 * except for the root node which is empty. 
	 */
	public static DefaultMutableTreeNode getAllPossibleGameEndCombinations(League league) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		addPossibleCombinations(league.getRemainingGames(), rootNode);
		return rootNode;
	}

	private static void addPossibleCombinations(List<Game> remainingGames, DefaultMutableTreeNode rootNode) {
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
}
