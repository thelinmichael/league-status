package util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.Arrays;

import javax.swing.tree.DefaultMutableTreeNode;

import models.Football;
import models.Game;
import models.League;
import models.Team;

import org.junit.Test;

import play.test.UnitTest;

public class FutureRankCalculatorTest extends UnitTest {

	@Test
	public void canGetAllPossibleGameEndingCombinations_ofALeaguesRemainingGames_oneGame() {
		Football football = new Football();
		League league = new League("league", football);
		
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		league.teams = Arrays.asList(team1, team2);
		
		Game game1 = new Game(league, team1, team2);
		league.games = Arrays.asList(game1);
		
		DefaultMutableTreeNode node = FutureRankCalculator.getAllPossibleGameEndCombinations(league);
		assertThat(node.getChildCount(), is(3));
		
		DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getChildAt(0);
		Game firstChildGame = (Game) firstChild.getUserObject();
		assertThat(firstChildGame.homeTeam, is(team1));
		assertThat(firstChildGame.awayTeam, is(team2));
		
		DefaultMutableTreeNode secondChild = (DefaultMutableTreeNode) node.getChildAt(1);
		Game secondChildGame = (Game) secondChild.getUserObject();
		assertThat(secondChildGame.homeTeam, is(team1));
		assertThat(secondChildGame.awayTeam, is(team2));
		
		DefaultMutableTreeNode thirdChild = (DefaultMutableTreeNode) node.getChildAt(2);
		Game thirdChildGame = (Game) thirdChild.getUserObject();
		assertThat(thirdChildGame.homeTeam, is(team1));
		assertThat(thirdChildGame.awayTeam, is(team2));
	}
	
	@Test
	public void canGetBestPossibleRankForATeam_BasedOnCombinationsOfFutureGameOutcomes_oneGame() {
		Football football = new Football();
		League league = new League("league", football);
		
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		league.teams = Arrays.asList(team1, team2);
		
		Game game1 = new Game(league, team1, team2);
		league.games = Arrays.asList(game1);
		
		DefaultMutableTreeNode node = FutureRankCalculator.getAllPossibleGameEndCombinations(league);
		assertThat(node.getChildCount(), is(3));
		assertThat(node.getLeafCount(), is(3));
		
		DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getChildAt(0);
		Game firstChildGame = (Game) firstChild.getUserObject();
		assertThat(firstChildGame.homeTeam, is(team1));
		assertThat(firstChildGame.awayTeam, is(team2));
		
		DefaultMutableTreeNode secondChild = (DefaultMutableTreeNode) node.getChildAt(1);
		Game secondChildGame = (Game) secondChild.getUserObject();
		assertThat(secondChildGame.homeTeam, is(team1));
		assertThat(secondChildGame.awayTeam, is(team2));
		
		DefaultMutableTreeNode thirdChild = (DefaultMutableTreeNode) node.getChildAt(2);
		Game thirdChildGame = (Game) thirdChild.getUserObject();
		assertThat(thirdChildGame.homeTeam, is(team1));
		assertThat(thirdChildGame.awayTeam, is(team2));
		
		assertThat(FutureRankCalculator.getBestPossibleRankFor(league, team1), is(1));
	}
	
	@Test
	public void canGetBestPossibleRankForTeams_BasedOnCombinationsOfFutureGameOutcomes_severalGames() {
		League league = League.find("byName", "euro-group-d").first();
		Team sweden = Team.find("byName", "sweden").first();
		Team england = Team.find("byName", "england").first();
		Team ukraine = Team.find("byName", "ukraine").first();
		Team france = Team.find("byName", "france").first();
		
		DefaultMutableTreeNode node = FutureRankCalculator.getAllPossibleGameEndCombinations(league);
		assertThat(node.getLeafCount(), is(9));
		
		assertThat(league.getBestPossibleRankFor(england), is(1));
		assertThat(league.getBestPossibleRankFor(france), is(1));
		assertThat(league.getBestPossibleRankFor(ukraine), is(1));
		assertThat(league.getBestPossibleRankFor(sweden), is(4));
	}
	
}