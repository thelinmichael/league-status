package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

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
		
		Game game = new Game(league, team1, team2);
		league.games.add(game);
		
		assertThat(league.games.size(), is(numberOfGamesBeforeAddingAnotherGame + 1));
	}
		
	@Test
	public void canGetTeamsInLeague() {
		League league = League.find("byName", "allsvenskan").first();
	
		Team gefleIf = Team.find("byName", "gefle_if").first();
		Team djurgardensIf = Team.find("byName", "djurgardens_if").first();
		Team mjallby = Team.find("byName", "mjallby_aif").first();
		Team aik = Team.find("byName", "aik").first();
		Team atvidaberg = Team.find("byName", "atvidabergs_ff").first();
				
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
		
		GameBuilder builder = new GameBuilder(league, team1, team2);
		Game game1 = builder.time(new Date(377257800L)).build().save();

		builder = new GameBuilder(league, team1, team2);
		Game game2 = builder.time(new Date(477257800L)).build().save();

		builder = new GameBuilder(league, team2, team1);
		Game game3 = builder.time(new Date(577257800L)).build().save();
		
		builder = new GameBuilder(league, team2, team1);
		Game game4 = builder.time(new Date(677257800L)).build().save();
		
		league.games.addAll(Arrays.asList(game1, game2, game3, game4));

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
			league.getPointsFor(bogusTeam);
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
		
		assertThat(league.getPointsFor(czech), is(6));
		assertThat(league.getPointsFor(russia), is(4));
		assertThat(league.getPointsFor(greece), is(4));
		assertThat(league.getPointsFor(poland), is(2));
	}
	
	@Test
	public void canGetWinsTiesAndLossesAndOtherResultsForTeam() {
		League league = League.find("byName", "euro-group-a").first();
		
		Team czech = Team.find("byName", "czech").first();
		Team poland = Team.find("byName", "poland").first();
		Team greece = Team.find("byName", "greece").first();
		Team russia = Team.find("byName", "russia").first();
		
		assertThat(league.getNumberOfWinsFor(czech), is(2));
		assertThat(league.getNumberOfWinsFor(russia), is(1));
		assertThat(league.getNumberOfWinsFor(greece), is(1));
		assertThat(league.getNumberOfWinsFor(poland), is(0));
		assertThat(league.getNumberOfLossesFor(czech), is(1));
		assertThat(league.getNumberOfLossesFor(russia), is(1));
		assertThat(league.getNumberOfLossesFor(greece), is(1));
		assertThat(league.getNumberOfLossesFor(poland), is(1));
		assertThat(league.getNumberOfTiesFor(czech), is(0));
		assertThat(league.getNumberOfTiesFor(russia), is(1));
		assertThat(league.getNumberOfTiesFor(greece), is(1));
		assertThat(league.getNumberOfTiesFor(poland), is(2));
	}
	
	@Test
	public void canGetGoalsScoredAndGoalsScoredAgainstForAGivenTeam() {
		League league = League.find("byName", "euro-group-a").first();
		
		Team czech = Team.find("byName", "czech").first();
		Team poland = Team.find("byName", "poland").first();
		Team greece = Team.find("byName", "greece").first();
		Team russia = Team.find("byName", "russia").first();
		
		assertThat(league.getGoalsScoredBy(czech), is(4));
		assertThat(league.getGoalsConcededBy(czech), is(5));
		assertThat(league.getGoalsScoredBy(russia), is(5));
		assertThat(league.getGoalsConcededBy(russia), is(3));
		assertThat(league.getGoalsScoredBy(greece), is(3));
		assertThat(league.getGoalsConcededBy(greece), is(3));
		assertThat(league.getGoalsScoredBy(poland), is(2));
		assertThat(league.getGoalsConcededBy(poland), is(3));
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
		newGames.add(new Game(league, team1, team2));
		newGames.add(new Game(league, team1, team3));
		newGames.add(new Game(league, team1, team4));
		newGames.add(new Game(league, team2, team1));
		newGames.add(new Game(league, team2, team3));
		newGames.add(new Game(league, team2, team4));
		newGames.add(new Game(league, team3, team1));
		newGames.add(new Game(league, team3, team2));
		newGames.add(new Game(league, team3, team4));
		newGames.add(new Game(league, team4, team1));
		newGames.add(new Game(league, team4, team2));
		newGames.add(new Game(league, team4, team3));
		
		league.games.addAll(newGames);
		assertThat(league.games, is(notNullValue()));
		league.games.get(0).homeTeamScore = 1;
		league.games.get(0).awayTeamScore = 2;
		league.games.get(1).homeTeamScore = 5;
		league.games.get(1).awayTeamScore = 0;
		league.games.get(2).homeTeamScore = 2;
		league.games.get(2).awayTeamScore = 0;
		league.games.get(3).homeTeamScore = 0;
		league.games.get(3).awayTeamScore = 0;
		league.games.get(4).homeTeamScore = 4;
		league.games.get(4).awayTeamScore = 6;
		league.games.get(5).homeTeamScore = 2;
		league.games.get(5).awayTeamScore = 2;
		league.games.get(6).homeTeamScore = 10;
		league.games.get(6).awayTeamScore = 1;
		league.games.get(7).homeTeamScore = 1;
		league.games.get(7).awayTeamScore = 1;
		
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
		
		GameBuilder builder = new GameBuilder(league, team1, team2);
		Game game1 = builder.score(1,1).build();
		
		builder = new GameBuilder(league, team2, team1);
		Game game2 = builder.score(3,3).build();
		
		builder = new GameBuilder(league, team1, team3);
		Game game3 = builder.score(5,4).build();
		
		builder = new GameBuilder(league, team2, team3);
		Game game4 = builder.score(3,0).build();

		league.games.addAll(Arrays.asList(game1, game2, game3, game4));
		
		assertThat(league.getPointsFor(team1), is(5));
		assertThat(league.getPointsFor(team2), is(5));
		assertThat(league.getPointsFor(team3), is(0));
		
		assertThat(league.getGoalsScoredBy(team1) - league.getGoalsConcededBy(team1), is(1));
		assertThat(league.getGoalsScoredBy(team2) - league.getGoalsConcededBy(team2), is(3));
		assertThat(league.getGoalsScoredBy(team3) - league.getGoalsConcededBy(team3), is(-4));
		
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
		
		league.games.add(new Game(league, team2, team3));
		league.games.get(4).homeTeamScore = 2;
		league.games.get(4).awayTeamScore = 3;
		
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
		Game polandGreece = new Game(league, poland, greece);
		Game russiaCzech = new Game(league, russia, czech);
		Game greeceCzech = new Game(league, greece, czech);
		Game polandRussia = new Game(league, poland, russia);
		Game czechPoland = new Game(league, czech, poland);
		Game greeceRussia = new Game(league, greece, russia);
		newGames.addAll(Arrays.asList(polandGreece, russiaCzech, greeceCzech, polandRussia, czechPoland, greeceRussia));
		
		league.games.addAll(newGames);
		league.games.get(0).homeTeamScore = 1;
		league.games.get(0).awayTeamScore = 1;
		league.games.get(1).homeTeamScore = 4;
		league.games.get(1).awayTeamScore = 1;
		league.games.get(2).homeTeamScore = 1;
		league.games.get(2).awayTeamScore = 2;
		league.games.get(3).homeTeamScore = 1;
		league.games.get(3).awayTeamScore = 1;
		
		List<Game> russiaFinishedGames = league.getFinishedGamesPlayedBy(russia);
		List<Game> russiaAllGames = league.getGamesPlayedBy(russia);
		
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
	
	@Test
	public void canGetAllPossibleGameEndingCombinations_ofALeaguesRemainingGames_oneGame() {
		Football football = new Football();
		League league = new League("league", football);
		
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		league.teams = Arrays.asList(team1, team2);
		
		Game game1 = new Game(league, team1, team2);
		league.games = Arrays.asList(game1);
		
		DefaultMutableTreeNode node = league.getAllPossibleGameEndCombinations();
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
		
		DefaultMutableTreeNode node = league.getAllPossibleGameEndCombinations();
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
		
		assertThat(league.getBestPossibleRankFor(team1,node), is(1));
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
		
		assertThat(league.getBestPossibleRankFor(england), is(1));
		assertThat(league.getBestPossibleRankFor(france), is(1));
		assertThat(league.getBestPossibleRankFor(ukraine), is(1));
		assertThat(league.getBestPossibleRankFor(sweden), is(4));
	}
	
	@Test
	public void canGetWorstAndBestPossibleRankForTeams_BasedOnCombinationOfFutureGameOutcomes_severalGames() throws Exception {
		League league = League.find("byName", "euro-group-d").first();
		Team sweden = Team.find("byName", "sweden").first();
		Team england = Team.find("byName", "england").first();
		Team ukraine = Team.find("byName", "ukraine").first();
		Team france = Team.find("byName", "france").first();
		
		System.out.println("France: " + league.getGoalDifferenceFor(france) + " " + league.getPointsFor(france));
		System.out.println("England: " + league.getGoalDifferenceFor(england) + " " + league.getPointsFor(england));
		System.out.println("Ukraine: " + league.getGoalDifferenceFor(ukraine) + " " + league.getPointsFor(ukraine));
		System.out.println("Sweden: " + league.getGoalDifferenceFor(sweden) + " " + league.getPointsFor(sweden));
		
		assertThat(league.getBestPossibleRankFor(france), is(1));
		assertThat(league.getBestPossibleRankFor(england), is(1));
		assertThat(league.getBestPossibleRankFor(ukraine), is(1));
		assertThat(league.getBestPossibleRankFor(sweden), is(4));
		
		assertThat(league.getWorstPossibleRankFor(france), is(3));
		assertThat(league.getWorstPossibleRankFor(england), is(3));
		assertThat(league.getWorstPossibleRankFor(ukraine), is(3));
		assertThat(league.getWorstPossibleRankFor(sweden), is(4));
	}
	
	@Test
	public void canGetHowTeamRelatesToUpperAndLowerQualificationRank() throws Exception {
		League league = League.find("byName", "euro-group-d").first();
		Team sweden = Team.find("byName", "sweden").first();
		Team england = Team.find("byName", "england").first();
		Team ukraine = Team.find("byName", "ukraine").first();
		Team france = Team.find("byName", "france").first();
		
		assertThat(league.upperQualificationRank, is(2));
		assertThat(league.lowerQualificationRank, is(3));
		
		assertThat(league.isInUpperQualification(sweden), is(false));
		assertThat(league.isBelowUpperQualification(sweden), is(true));
		assertThat(league.isAboveLowerQualification(sweden), is(false));
		assertThat(league.isInLowerQualification(sweden), is(true));
		assertThat(league.isInUpperQualification(england), is(true));
		assertThat(league.isBelowUpperQualification(england), is(false));
		assertThat(league.isAboveLowerQualification(england), is(true));
		assertThat(league.isInLowerQualification(england), is(false));
		assertThat(league.isInUpperQualification(ukraine), is(false));
		assertThat(league.isBelowUpperQualification(ukraine), is(true));
		assertThat(league.isAboveLowerQualification(ukraine), is(false));
		assertThat(league.isInLowerQualification(ukraine), is(true));
		assertThat(league.isInUpperQualification(france), is(true));
		assertThat(league.isBelowUpperQualification(france), is(false));
		assertThat(league.isAboveLowerQualification(france), is(true));
		assertThat(league.isInLowerQualification(france), is(false));
	}
	
	@Test
	public void canGetIfARankIsInUpperOrLowerQualificationRank() throws Exception {
		League league = League.find("byName", "euro-group-d").first();
		
		assertThat(league.upperQualificationRank, is(2));
		assertThat(league.lowerQualificationRank, is(3));
		
		assertThat(league.isInUpperQualification(1), is(true));
		assertThat(league.isInUpperQualification(2), is(true));
		assertThat(league.isInUpperQualification(3), is(false));
		assertThat(league.isInUpperQualification(4), is(false));
		assertThat(league.isInLowerQualification(1), is(false));
		assertThat(league.isInLowerQualification(2), is(false));
		assertThat(league.isInLowerQualification(3), is(true));
		assertThat(league.isInLowerQualification(4), is(true));
		assertThat(league.isBelowUpperQualification(1), is(false));
		assertThat(league.isBelowUpperQualification(2), is(false));
		assertThat(league.isBelowUpperQualification(3), is(true));
		assertThat(league.isBelowUpperQualification(4), is(true));
		assertThat(league.isAboveLowerQualification(1), is(true));
		assertThat(league.isAboveLowerQualification(2), is(true));
		assertThat(league.isAboveLowerQualification(3), is(false));
		assertThat(league.isAboveLowerQualification(4), is(false));
	}
	
	@Test
	public void canGetRankForTeam() throws Exception {
		League league = League.find("byName", "euro-group-d").first();
		
		Team sweden = Team.find("byName", "sweden").first();
		Team england = Team.find("byName", "england").first();
		Team ukraine = Team.find("byName", "ukraine").first();
		Team france = Team.find("byName", "france").first();
		
		assertThat(league.getRankFor(france), is(1));
		assertThat(league.getRankFor(england), is(2));
		assertThat(league.getRankFor(ukraine), is(3));
		assertThat(league.getRankFor(sweden), is(4));
		
		Team team = new Team("Team not in league");
		try {
			league.getRankFor(team);
			fail();
		} catch (IllegalArgumentException e) {}
	}
}