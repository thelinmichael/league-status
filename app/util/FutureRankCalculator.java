package util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import models.Game;
import models.League;
import models.Team;

public class FutureRankCalculator {
	
	public static int getBestPossibleRankFor(League league, Team team) {
		if (!league.teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		return getBestPossibleRankFor(league, team, getAllPossibleGameEndCombinations(league));
	}

	private static int getBestPossibleRankFor(League league, Team team, DefaultMutableTreeNode rootNode) {
		return getPossibleRankFor(league, team, rootNode, true);
	}
	
	public static int getWorstPossibleRankFor(League league, Team team) {
		return getWorstPossibleRankFor(league, team, getAllPossibleGameEndCombinations(league));
	}
	
	private static int getWorstPossibleRankFor(League league, Team team, DefaultMutableTreeNode rootNode) {
		return getPossibleRankFor(league, team, rootNode, false);
	}
	
	private static int getPossibleRankFor(League league, Team team, DefaultMutableTreeNode rootNode, boolean isForBestRank) {
		Integer possibleRank = null;
		
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
		
		for (List<Game> outcome : possibleOutcomes) {
			
			/* Go through every game for this list of possible outcomes,
			 * and set the scores to a ridiculously large number. This is
			 * done to prevent goal difference to affect the team's best 
			 * or worst possible rank.
			 */
			for (Game game : outcome) {
				if (isForBestRank) {
					if (game.isPlayedBy(team) && game.getResultFor(team) == Result.WIN) {
						if (game.homeTeam.equals(team)) {
							game.homeTeamScore = 9999;
						} else {
							game.awayTeamScore = 9999;
						}
					}
				} else {
					if (game.isPlayedBy(team) && game.getResultFor(team) == Result.LOSS) {
						if (game.homeTeam.equals(team)) {
							game.awayTeamScore = 9999;
						} else {
							game.homeTeamScore = 9999;
						}
					}
				}
			}
			
			/* Merge the already played games with the possible outcomes. */
			List<Game> playedGames = new ArrayList(league.getPlayedGames());
			playedGames.addAll(outcome);
			
			List<Team> teams = league.getTeamsByRankForGames(playedGames);
			
			if (isForBestRank) {
				if (possibleRank == null || (teams.indexOf(team) + 1) < possibleRank) {
					possibleRank = teams.indexOf(team) + 1;
				}
			} else {
				if (possibleRank == null || (teams.indexOf(team) + 1) > possibleRank) {
					possibleRank = teams.indexOf(team) + 1;
				}
			}
		}
		
		return possibleRank;
		
	}
	
	/* A tree structure where every node represents a finished game, 
	 * except for the root node which is empty. 
	 */
	// TODO: See if this is cachable.
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
		
		// Get every leaf in the tree. 
		Enumeration enumeration = rootNode.depthFirstEnumeration();
		List<DefaultMutableTreeNode> leafs = new ArrayList<DefaultMutableTreeNode>();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) enumeration.nextElement());
			if (node.isLeaf()){
				leafs.add(node);
			}
		}
		
		// For every leaf, add three new child nodes, one for each possible outcome (win, tie, loss)
		// of the first game in the list of remaining games.
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
		
		// Use function recursively until there's no remaining games left.
		addPossibleCombinations(remainingGames, rootNode);
	}
}