package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;
import util.GameBuilder;

import comparators.GoalDifferenceComparator;
import comparators.GoalsScoredComparator;
import comparators.IndividualGamesComparator;
import comparators.PointComparator;

public class LeagueTest extends UnitTest { 
	
	@Before
	public void setupDb() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test
	public void canGetTeamsAndGamesFromLeague() {
		League allsvenskan = League.find("byName", "allsvenskan").first();
		
		assertThat(allsvenskan, is(notNullValue()));
		assertThat(allsvenskan.teams.size(), is(5));
		assertThat(allsvenskan.games.size(), is(4));
	}
	
	@Test
	public void canAddGamesToLeague() {
		League league = League.find("byName", "allsvenskan").first();

		int numberOfGamesBeforeAddingAnotherGame = league.games.size();
		
		Team team1 = league.teams.get(0);
		Team team2 = league.teams.get(1);
		
		Game game = new Game(league, Arrays.asList(team1, team2));
		league.addGame(game);
		
		assertThat(league.games.size(), is(numberOfGamesBeforeAddingAnotherGame + 1));
	}
		
	@Test
	public void canGetTeamsInLeague() {
		League league = League.find("byName", "allsvenskan").first();
	
		Team gefleIf = Team.find("byName", "gefle_if").first();
		Team djurgardensIf = Team.find("byName", "djurgardens_if").first();
		Team mjallby = Team.find("byName", "mjallby").first();
		Team aik = Team.find("byName", "aik").first();
		Team atvidaberg = Team.find("byName", "atvidaberg").first();
				
		assertThat(league.teams.size(), is(5));
		assertThat(league.teams.containsAll(Arrays.asList(gefleIf, djurgardensIf, mjallby, aik, atvidaberg)), is(true));
	}
	
	@Test
	public void canGetALeaguesPlayedGames_andRemainingGames() {
		League league = League.find("byName", "allsvenskan").first();

		assertThat(league.getPlayedGames().size(), is(3));
		assertThat(league.getRemainingGames().size(), is(1));
	}
	
	@Test
	public void canGetGamesInChronologicalOrder() {
		Football football = new Football().save();
		League league = new League("league", football);
		Team team1 = new Team("team1").save();
		Team team2 = new Team("team2").save();
		league.teams = Arrays.asList(team1, team2);
		league.save();
		
		GameBuilder builder = new GameBuilder(league, Arrays.asList(team1, team2));
		Game game1 = builder.time(new Date(377257800L)).build().save();

		builder = new GameBuilder(league, Arrays.asList(team1, team2));
		Game game2 = builder.time(new Date(477257800L)).build().save();

		builder = new GameBuilder(league, Arrays.asList(team2, team1));
		Game game3 = builder.time(new Date(577257800L)).build().save();
		
		builder = new GameBuilder(league, Arrays.asList(team2, team1));
		Game game4 = builder.time(new Date(677257800L)).build().save();
		
		league.addGames(Arrays.asList(game1, game2, game3, game4));

		List<Game> unsortedGames = league.games;
		assertThat(unsortedGames.get(0), is(game1));
		assertThat(unsortedGames.get(1), is(game2));
		assertThat(unsortedGames.get(2), is(game3));
		assertThat(unsortedGames.get(3), is(game4));
		
		List<Game> sortedGames = league.getGamesInChronologicalOrder();
		
		assertThat(sortedGames.get(0), is(game4));
		assertThat(sortedGames.get(1), is(game3));
		assertThat(sortedGames.get(2), is(game2));
		assertThat(sortedGames.get(3), is(game1));
	}
	
	@Test
	public void shouldThrowExceptionWhenGettingPointsForTeamNotInLeague() {
		League league = League.find("byName", "euro-group-a").first();
		Team bogusTeam = new Team("Bogus Team");
		
		try {
			league.getPointsForTeam(bogusTeam);
			fail();
		} catch(IllegalArgumentException e) {
		}
	}
	
	@Test
	public void canGetNumberOfPointsForTeam() {
		League league = League.find("byName", "euro-group-a").first();
		
		Team czech = Team.find("byName", "czech").first();
		Team poland = Team.find("byName", "poland").first();
		Team greece = Team.find("byName", "greece").first();
		Team russia = Team.find("byName", "russia").first();
		
		assertThat(league.getPointsForTeam(czech), is(6));
		assertThat(league.getPointsForTeam(russia), is(4));
		assertThat(league.getPointsForTeam(greece), is(4));
		assertThat(league.getPointsForTeam(poland), is(2));
	}
	
	@Test
	public void canGetWinsTiesAndLossesAndOtherResultsForTeam() {
		League league = League.find("byName", "euro-group-a").first();
		
		Team czech = Team.find("byName", "czech").first();
		Team poland = Team.find("byName", "poland").first();
		Team greece = Team.find("byName", "greece").first();
		Team russia = Team.find("byName", "russia").first();
		
		assertThat(league.getWinsForTeam(czech), is(2));
		assertThat(league.getWinsForTeam(russia), is(1));
		assertThat(league.getWinsForTeam(greece), is(1));
		assertThat(league.getWinsForTeam(poland), is(0));
		assertThat(league.getLossesForTeam(czech), is(1));
		assertThat(league.getLossesForTeam(russia), is(1));
		assertThat(league.getLossesForTeam(greece), is(1));
		assertThat(league.getLossesForTeam(poland), is(1));
		assertThat(league.getTiesForTeam(czech), is(0));
		assertThat(league.getTiesForTeam(russia), is(1));
		assertThat(league.getTiesForTeam(greece), is(1));
		assertThat(league.getTiesForTeam(poland), is(2));
	}
	
	@Test
	public void canGetGoalsScoredAndGoalsScoredAgainstForAGivenTeam() {
		League league = League.find("byName", "euro-group-a").first();
		
		Team czech = Team.find("byName", "czech").first();
		Team poland = Team.find("byName", "poland").first();
		Team greece = Team.find("byName", "greece").first();
		Team russia = Team.find("byName", "russia").first();
		
		assertThat(league.getGoalsScoredByTeam(czech), is(4));
		assertThat(league.getGoalsScoredAgainstTeam(czech), is(5));
		assertThat(league.getGoalsScoredByTeam(russia), is(5));
		assertThat(league.getGoalsScoredAgainstTeam(russia), is(3));
		assertThat(league.getGoalsScoredByTeam(greece), is(3));
		assertThat(league.getGoalsScoredAgainstTeam(greece), is(3));
		assertThat(league.getGoalsScoredByTeam(poland), is(2));
		assertThat(league.getGoalsScoredAgainstTeam(poland), is(3));
	}
	
	@Test
	public void shouldRankTeamsInLeagueDifferentlyDependingOnSinglePriority() {
		League league = new League("Fantasy League", new Football());
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		Team team4 = new Team("team4");
		league.teams = Arrays.asList(team1, team2, team3, team4);
		
		List<Game> newGames = new ArrayList<Game>();
		newGames.add(new Game(league, Arrays.asList(team1, team2)));
		newGames.add(new Game(league, Arrays.asList(team1, team3)));
		newGames.add(new Game(league, Arrays.asList(team1, team4)));
		newGames.add(new Game(league, Arrays.asList(team2, team1)));
		newGames.add(new Game(league, Arrays.asList(team2, team3)));
		newGames.add(new Game(league, Arrays.asList(team2, team4)));
		newGames.add(new Game(league, Arrays.asList(team3, team1)));
		newGames.add(new Game(league, Arrays.asList(team3, team2)));
		newGames.add(new Game(league, Arrays.asList(team3, team4)));
		newGames.add(new Game(league, Arrays.asList(team4, team1)));
		newGames.add(new Game(league, Arrays.asList(team4, team2)));
		newGames.add(new Game(league, Arrays.asList(team4, team3)));
		
		league.addGames(newGames);
		assertThat(league.games, is(notNullValue()));
		league.games.get(0).setScore(Arrays.asList(1, 2));
		league.games.get(1).setScore(Arrays.asList(5, 0));
		league.games.get(2).setScore(Arrays.asList(2, 0));
		league.games.get(3).setScore(Arrays.asList(0, 0));
		league.games.get(4).setScore(Arrays.asList(4, 6));
		league.games.get(5).setScore(Arrays.asList(2, 2));
		league.games.get(6).setScore(Arrays.asList(10, 1));
		league.games.get(7).setScore(Arrays.asList(1, 1));
		
		assertThat(league.teams.get(0), is(team1));
		assertThat(league.teams.get(1), is(team2));
		assertThat(league.teams.get(2), is(team3));
		assertThat(league.teams.get(3), is(team4));

		List<Comparator<Team>> comparators = Arrays.asList((Comparator<Team>) new PointComparator(league));
		List<Team> teamsOrderedByRank = league.getTeamsByRank(comparators);
		assertThat(teamsOrderedByRank.get(0), is(team3));
		assertThat(teamsOrderedByRank.get(1), is(team1));
		assertThat(teamsOrderedByRank.get(2), is(team2));
		assertThat(teamsOrderedByRank.get(3), is(team4));

		comparators = Arrays.asList((Comparator<Team>) new GoalsScoredComparator(league));
		teamsOrderedByRank = league.getTeamsByRank(comparators);
		assertThat(teamsOrderedByRank.get(0), is(team3));
		assertThat(teamsOrderedByRank.get(1), is(team2));
		assertThat(teamsOrderedByRank.get(2), is(team1));
		assertThat(teamsOrderedByRank.get(3), is(team4));
		
		comparators = Arrays.asList((Comparator<Team>) new GoalDifferenceComparator(league));
		teamsOrderedByRank = league.getTeamsByRank(comparators);
		assertThat(teamsOrderedByRank.get(0), is(team3));
		assertThat(teamsOrderedByRank.get(1), is(team2));
		assertThat(teamsOrderedByRank.get(2), is(team4));
		assertThat(teamsOrderedByRank.get(3), is(team1));
	}
	
	@Test
	public void shouldRankTeamsInLeagueDifferentlyDependingOnSeveralPriorities() {
		Sport football = new Football().save();
		League league = new League("Fantasy League", football).save();
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		league.teams = Arrays.asList(team1, team2, team3);
		
		GameBuilder builder = new GameBuilder(league, Arrays.asList(team1, team2));
		Game game1 = builder.score(Arrays.asList(1,1)).build();
		
		builder = new GameBuilder(league, Arrays.asList(team2, team1));
		Game game2 = builder.score(Arrays.asList(3,3)).build();
		
		builder = new GameBuilder(league, Arrays.asList(team1, team3));
		Game game3 = builder.score(Arrays.asList(5,4)).build();
		
		builder = new GameBuilder(league, Arrays.asList(team2, team3));
		Game game4 = builder.score(Arrays.asList(3,0)).build();

		league.addGames(Arrays.asList(game1, game2, game3, game4));
		
		assertThat(league.getPointsForTeam(team1), is(5));
		assertThat(league.getPointsForTeam(team2), is(5));
		assertThat(league.getPointsForTeam(team3), is(0));
		
		assertThat(league.getGoalsScoredByTeam(team1) - league.getGoalsScoredAgainstTeam(team1), is(1));
		assertThat(league.getGoalsScoredByTeam(team2) - league.getGoalsScoredAgainstTeam(team2), is(3));
		assertThat(league.getGoalsScoredByTeam(team3) - league.getGoalsScoredAgainstTeam(team3), is(-4));
		
		List<Comparator<Team>> priorities = Arrays.asList((Comparator<Team>) new PointComparator(league), (Comparator<Team>) new GoalsScoredComparator(league));
		List<Team> teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team1));
		assertThat(teamsOrderedByRank.get(1), is(team2));
		assertThat(teamsOrderedByRank.get(2), is(team3));
		
		priorities = Arrays.asList((Comparator<Team>) new PointComparator(league), (Comparator<Team>) new GoalsScoredComparator(league));
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team1));
		assertThat(teamsOrderedByRank.get(1), is(team2));
		assertThat(teamsOrderedByRank.get(2), is(team3));
		
		league.games.add(new Game(league, Arrays.asList(team2, team3)));
		league.games.get(4).setScore(Arrays.asList(2, 3));
		
		priorities = Arrays.asList((Comparator<Team>) new PointComparator(league), (Comparator<Team>) new GoalsScoredComparator(league), (Comparator<Team>) new GoalDifferenceComparator(league));
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team2));
		assertThat(teamsOrderedByRank.get(1), is(team1));
		assertThat(teamsOrderedByRank.get(2), is(team3));
	}
	
	@Test
	public void canGetTeamsOrderedByIndividualGames() {
		League league = League.find("byName", "euro-group-a").first();
		
		Team czech = Team.find("byName", "czech").first();
		Team poland = Team.find("byName", "poland").first();
		Team greece = Team.find("byName", "greece").first();
		Team russia = Team.find("byName", "russia").first();
		
		List<Comparator<Team>> priorities = Arrays.asList((Comparator<Team>) new PointComparator(league));
		List<Team> teamsOrderedByRank = league.getTeamsByRank(priorities);
		
		assertThat(teamsOrderedByRank.get(0), is(czech));
		assertThat(teamsOrderedByRank.get(1), is(russia));
		assertThat(teamsOrderedByRank.get(2), is(greece));
		assertThat(teamsOrderedByRank.get(3), is(poland));
		
		priorities = Arrays.asList((Comparator<Team>) new PointComparator(league), (Comparator<Team>) new IndividualGamesComparator(league), (Comparator<Team>) new GoalDifferenceComparator(league), (Comparator<Team>) new GoalsScoredComparator(league));
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(czech));
		assertThat(teamsOrderedByRank.get(1), is(greece));
		assertThat(teamsOrderedByRank.get(2), is(russia));
		assertThat(teamsOrderedByRank.get(3), is(poland));
		
		priorities = Arrays.asList((Comparator<Team>) new PointComparator(league), (Comparator<Team>) new GoalDifferenceComparator(league), (Comparator<Team>) new IndividualGamesComparator(league), (Comparator<Team>) new GoalsScoredComparator(league));
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(czech));
		assertThat(teamsOrderedByRank.get(1), is(russia));
		assertThat(teamsOrderedByRank.get(2), is(greece));
		assertThat(teamsOrderedByRank.get(3), is(poland));
	}
	
	@Test
	public void canGetAllAndAllFinishedGamesForGivenTeam() {
		League league = new League("Euro Cup Group A", new Football());
		Team poland = new Team("Poland");
		Team greece = new Team("Greece");
		Team russia = new Team("Russia");
		Team czech = new Team("Czech");
		league.teams = Arrays.asList(poland, greece, russia, czech);
		
		List<Game> newGames = new ArrayList<Game>();
		Game polandGreece = new Game(league, Arrays.asList(poland, greece));
		Game russiaCzech = new Game(league, Arrays.asList(russia, czech));
		Game greeceCzech = new Game(league, Arrays.asList(greece, czech));
		Game polandRussia = new Game(league, Arrays.asList(poland, russia));
		Game czechPoland = new Game(league, Arrays.asList(czech, poland));
		Game greeceRussia = new Game(league, Arrays.asList(greece, russia));
		newGames.addAll(Arrays.asList(polandGreece, russiaCzech, greeceCzech, polandRussia, czechPoland, greeceRussia));
		
		league.addGames(newGames);
		league.games.get(0).setScore(Arrays.asList(1, 1));
		league.games.get(1).setScore(Arrays.asList(4, 1));
		league.games.get(2).setScore(Arrays.asList(1, 2));
		league.games.get(3).setScore(Arrays.asList(1, 1));

		List<Game> russiaFinishedGames = league.getFinishedGamesWithTeam(russia);
		List<Game> russiaAllGames = league.getAllGamesWithTeam(russia);
		
		assertThat(russiaAllGames.size(), is(3));
		assertThat(russiaAllGames.containsAll(Arrays.asList(russiaCzech, polandRussia, greeceRussia)), is(true));
		assertThat(russiaFinishedGames.size(), is(2));
		assertThat(russiaFinishedGames.containsAll(Arrays.asList(russiaCzech, polandRussia)), is(true));
	}
	
	@Test
	public void canMakeDisplayName() {
		String teamName = "Gefle";
		League league = new League(teamName, new Football());
		
		assertThat(league.makeDisplayName(teamName), is("gefle"));
		
		teamName = "Gefle IF";
		league = new League(teamName, new Football());
		
		assertThat(league.makeDisplayName(teamName), is("gefle_if"));
	}
	
	@Ignore
	@Test
	public void canGetAllPossibleGameEndingCombinations_ofALeaguesRemainingGames_oneGame() {
		Football football = new Football();
		League league = new League("league", football);
		
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		league.teams = Arrays.asList(team1, team2);
		
		Game game1 = new Game(league,Arrays.asList(team1, team2));
		league.games = Arrays.asList(game1);
		
		DefaultMutableTreeNode node = league.getAllPossibleGameEndCombinations();
		assertThat(node.getChildCount(), is(3));
		
		DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getChildAt(0);
		Game firstChildGame = (Game) firstChild.getUserObject();
		assertThat(firstChildGame.teams.get(0), is(team1));
		assertThat(firstChildGame.teams.get(1), is(team2));
		
		DefaultMutableTreeNode secondChild = (DefaultMutableTreeNode) node.getChildAt(1);
		Game secondChildGame = (Game) secondChild.getUserObject();
		assertThat(secondChildGame.teams.get(0), is(team1));
		assertThat(secondChildGame.teams.get(1), is(team2));
		
		DefaultMutableTreeNode thirdChild = (DefaultMutableTreeNode) node.getChildAt(2);
		Game thirdChildGame = (Game) thirdChild.getUserObject();
		assertThat(thirdChildGame.teams.get(0), is(team1));
		assertThat(thirdChildGame.teams.get(1), is(team2));
	}
	
	@Ignore
	@Test
	public void canGetBestPossibleRankForATeam_BasedOnCombinationsOfFutureGameOutcomes_oneGame() {
		Football football = new Football();
		League league = new League("league", football);
		
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		league.teams = Arrays.asList(team1, team2);
		
		Game game1 = new Game(league,Arrays.asList(team1, team2));
		league.games = Arrays.asList(game1);
		
		DefaultMutableTreeNode node = league.getAllPossibleGameEndCombinations();
		assertThat(node.getChildCount(), is(3));
		assertThat(node.getLeafCount(), is(3));
		
		DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getChildAt(0);
		Game firstChildGame = (Game) firstChild.getUserObject();
		assertThat(firstChildGame.teams.get(0), is(team1));
		assertThat(firstChildGame.teams.get(1), is(team2));
		
		DefaultMutableTreeNode secondChild = (DefaultMutableTreeNode) node.getChildAt(1);
		Game secondChildGame = (Game) secondChild.getUserObject();
		assertThat(secondChildGame.teams.get(0), is(team1));
		assertThat(secondChildGame.teams.get(1), is(team2));
		
		DefaultMutableTreeNode thirdChild = (DefaultMutableTreeNode) node.getChildAt(2);
		Game thirdChildGame = (Game) thirdChild.getUserObject();
		assertThat(thirdChildGame.teams.get(0), is(team1));
		assertThat(thirdChildGame.teams.get(1), is(team2));
		
		assertThat(league.getBestPossibleRankForTeam(team1,node), is(1));
	}
	
	@Test
	public void canGetBestPossibleRankForTeams_BasedOnCombinationsOfFutureGameOutcomes_severalGames() {
		League league = League.find("byName", "euro-group-d").first();
		Team sweden = Team.find("byName", "sweden").first();
		Team england = Team.find("byName", "england").first();
		Team ukraine = Team.find("byName", "ukraine").first();
		Team france = Team.find("byName", "france").first();
		
		DefaultMutableTreeNode node = league.getAllPossibleGameEndCombinations();
		assertThat(node.getLeafCount(), is(9));
		
		assertThat(league.getBestPossibleRankForTeam(england), is(1));
		assertThat(league.getBestPossibleRankForTeam(france), is(1));
		assertThat(league.getBestPossibleRankForTeam(ukraine), is(1));
		assertThat(league.getBestPossibleRankForTeam(sweden), is(4));
	}
}