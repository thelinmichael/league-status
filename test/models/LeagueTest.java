package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import play.test.Fixtures;
import play.test.UnitTest;
import util.Result;
import util.StatsPriority;

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
		assertThat(allsvenskan.getTeams().size(), is(5));
		assertThat(allsvenskan.getGames().size(), is(4));
	}
	
	@Test
	public void canAddGamesToLeague() {
		League league = League.find("byName", "allsvenskan").first();

		int numberOfGamesBeforeAddingAnotherGame = league.getGames().size();
		
		Team team1 = league.getTeams().get(0);
		Team team2 = league.getTeams().get(1);
		
		Game game = new Game(league, Arrays.asList(team1, team2));
		league.addGame(game);
		
		assertThat(league.getGames().size(), is(numberOfGamesBeforeAddingAnotherGame + 1));
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
		
		Game game1 = new Game(league, Arrays.asList(team1, team2)).save();
		Game game2 = new Game(league, Arrays.asList(team1, team2)).save();
		Game game3 = new Game(league, Arrays.asList(team2, team1)).save();
		Game game4 = new Game(league, Arrays.asList(team2, team1)).save();
		
		Date date1 = new Date(377257800L);
		Date date2 = new Date(477257800L);
		Date date3 = new Date(577257800L);
		Date date4 = new Date(677257800L);
		
		game4.time = date1;
		game3.time = date2;
		game1.time = date3;
		game2.time = date4;
		
		league.addGame(game1);
		league.addGame(game2);
		league.addGame(game3);
		league.addGame(game4);
		
		List<Game> unsortedGames = league.getGames();
		assertThat(unsortedGames.get(0), is(game1));
		assertThat(unsortedGames.get(1), is(game2));
		assertThat(unsortedGames.get(2), is(game3));
		assertThat(unsortedGames.get(3), is(game4));
		
		List<Game> sortedGames = league.getGamesInChronologicalOrder();
		
		assertThat(sortedGames.get(0), is(game4));
		assertThat(sortedGames.get(1), is(game3));
		assertThat(sortedGames.get(2), is(game1));
		assertThat(sortedGames.get(3), is(game2));
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
		Team czech = Team.find("byLeagueAndName", league, "czech").first();
		Team russia = Team.find("byLeagueAndName", league, "russia").first();
		Team greece = Team.find("byLeagueAndName", league, "greece").first();
		Team poland = Team.find("byLeagueAndName", league, "poland").first();
		
		assertThat(league.getPointsForTeam(czech), is(6));
		assertThat(league.getPointsForTeam(russia), is(4));
		assertThat(league.getPointsForTeam(greece), is(4));
		assertThat(league.getPointsForTeam(poland), is(2));
	}
	
	@Test
	public void canGetWinsTiesAndLossesAndOtherResultsForTeam() {
		League league = League.find("byName", "euro-group-a").first();
		Team czech = Team.find("byLeagueAndName", league, "czech").first();
		Team russia = Team.find("byLeagueAndName", league, "russia").first();
		Team greece = Team.find("byLeagueAndName", league, "greece").first();
		Team poland = Team.find("byLeagueAndName", league, "poland").first();
		
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
		Team czech = Team.find("byLeagueAndName", league, "czech").first();
		Team russia = Team.find("byLeagueAndName", league, "russia").first();
		Team greece = Team.find("byLeagueAndName", league, "greece").first();
		Team poland = Team.find("byLeagueAndName", league, "poland").first();
		
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
		team1.league = league;
		team2.league = league;
		team3.league = league;
		team4.league = league;
		
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

		List<StatsPriority> priorities = Arrays.asList(StatsPriority.POINTS);
		List<Team> teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team3));
		assertThat(teamsOrderedByRank.get(1), is(team1));
		assertThat(teamsOrderedByRank.get(2), is(team2));
		assertThat(teamsOrderedByRank.get(3), is(team4));

		priorities = Arrays.asList(StatsPriority.GOALS_SCORED);
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team3));
		assertThat(teamsOrderedByRank.get(1), is(team2));
		assertThat(teamsOrderedByRank.get(2), is(team1));
		assertThat(teamsOrderedByRank.get(3), is(team4));
		
		priorities = Arrays.asList(StatsPriority.GOAL_DIFFERENCE);
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team3));
		assertThat(teamsOrderedByRank.get(1), is(team2));
		assertThat(teamsOrderedByRank.get(2), is(team4));
		assertThat(teamsOrderedByRank.get(3), is(team1));
	}
	
	@Test
	public void shouldRankTeamsInLeagueDifferentlyDependingOnSeveralPriorities() {
		League league = new League("Fantasy League", new Football());
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		league.teams = Arrays.asList(team1, team2, team3);
		team1.league = league;
		team2.league = league;
		team3.league = league;
		
		List<Game> newGames = new ArrayList<Game>();
		newGames.add(new Game(league, Arrays.asList(team1, team2)));
		newGames.add(new Game(league, Arrays.asList(team2, team1)));
		newGames.add(new Game(league, Arrays.asList(team1, team3)));
		newGames.add(new Game(league, Arrays.asList(team2, team3)));
		league.addGames(newGames);

		league.games.get(0).setScore(Arrays.asList(1, 1));
		league.games.get(1).setScore(Arrays.asList(3, 3));
		league.games.get(2).setScore(Arrays.asList(5, 4));
		league.games.get(3).setScore(Arrays.asList(3, 0));
		
		assertThat(league.getGoalsScoredByTeam(team1) - league.getGoalsScoredAgainstTeam(team1), is(1));
		assertThat(league.getGoalsScoredByTeam(team2) - league.getGoalsScoredAgainstTeam(team2), is(3));
		assertThat(league.getGoalsScoredByTeam(team3) - league.getGoalsScoredAgainstTeam(team3), is(-4));
		
		List<StatsPriority> priorities = Arrays.asList(StatsPriority.POINTS, StatsPriority.GOAL_DIFFERENCE);
		List<Team> teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team2));
		assertThat(teamsOrderedByRank.get(1), is(team1));
		assertThat(teamsOrderedByRank.get(2), is(team3));
		
		priorities = Arrays.asList(StatsPriority.POINTS, StatsPriority.GOALS_SCORED);
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team1));
		assertThat(teamsOrderedByRank.get(1), is(team2));
		assertThat(teamsOrderedByRank.get(2), is(team3));
		
		league.games.add(new Game(league, Arrays.asList(team2, team3)));
		league.games.get(4).setScore(Arrays.asList(2, 3));
		
		priorities = Arrays.asList(StatsPriority.POINTS, StatsPriority.GOALS_SCORED, StatsPriority.GOAL_DIFFERENCE);
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(team2));
		assertThat(teamsOrderedByRank.get(1), is(team1));
		assertThat(teamsOrderedByRank.get(2), is(team3));
	}
	
	@Test
	public void canGetTeamsOrderedByIndividualGames() {
		League league = League.find("byName", "euro-group-a").first();
		Team czech = Team.find("byLeagueAndName", league, "czech").first();
		Team russia = Team.find("byLeagueAndName", league, "russia").first();
		Team greece = Team.find("byLeagueAndName", league, "greece").first();
		Team poland = Team.find("byLeagueAndName", league, "poland").first();
		
		List<StatsPriority> priorities = Arrays.asList(StatsPriority.POINTS);
		List<Team> teamsOrderedByRank = league.getTeamsByRank(priorities);
		
		assertThat(teamsOrderedByRank.get(0), is(czech));
		assertThat(teamsOrderedByRank.get(1), is(russia));
		assertThat(teamsOrderedByRank.get(2), is(greece));
		assertThat(teamsOrderedByRank.get(3), is(poland));
		
		priorities = Arrays.asList(StatsPriority.POINTS, StatsPriority.INDIVIDUAL_GAMES_BETWEEN_TEAMS, StatsPriority.GOAL_DIFFERENCE, StatsPriority.GOALS_SCORED);
		teamsOrderedByRank = league.getTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0), is(czech));
		assertThat(teamsOrderedByRank.get(1), is(greece));
		assertThat(teamsOrderedByRank.get(2), is(russia));
		assertThat(teamsOrderedByRank.get(3), is(poland));
		
		priorities = Arrays.asList(StatsPriority.POINTS, StatsPriority.GOAL_DIFFERENCE, StatsPriority.INDIVIDUAL_GAMES_BETWEEN_TEAMS, StatsPriority.GOALS_SCORED);
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
		poland.league = league;
		greece.league = league;
		russia.league = league;
		czech.league = league;
		
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
}