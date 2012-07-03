package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;
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
	public void canAddGames() {
		League league = League.find("byName", "allsvenskan").first();

		int numberOfGames = league.getGames().size();
		
		Team team1 = league.getTeams().get(0);
		Team team2 = league.getTeams().get(1);
		
		Game game = new Game(league, Arrays.asList(new Team[] {team1, team2}));
		
		league.addGame(game);
		assertThat(league.getGames().size(), is(numberOfGames + 1));
	}
		
	@Test
	public void canGetTeamsInLeague() {
		League league = League.find("byName", "allsvenskan").first();

		assertThat(league, is(notNullValue()));
		
		List<Team> teams = league.getTeams();
		assertThat(teams.size(), is(5));
	}
	
	@Test
	public void canGetALeaguesPlayedGames_andRemainingGames() {
		Football football = new Football().save();
		League league = new League("league", football);
		Team team1 = new Team("team1").save();
		Team team2 = new Team("team2").save();
		league.teams = Arrays.asList(new Team[] { team1, team2 });
		league.save();
		
		Game game1 = new Game(league, Arrays.asList(new Team[] {team1, team2})).save();
		Game game2 = new Game(league, Arrays.asList(new Team[] {team1, team2})).save();
		Game game3 = new Game(league, Arrays.asList(new Team[] {team2, team1})).save();
		Game game4 = new Game(league, Arrays.asList(new Team[] {team2, team1})).save();
		
		game1.setScore(Arrays.asList(new Integer[] {1,2}));
		game2.setScore(Arrays.asList(new Integer[] {2,1}));
		game3.setScore(Arrays.asList(new Integer[] {5,0}));
		
		league.addGame(game1);
		league.addGame(game2);
		league.addGame(game3);
		league.addGame(game4);
	
		assertThat(league.getPlayedGames().size(), is(3));
		assertThat(league.getRemainingGames().size(), is(1));
		assertThat(league.getPlayedGames().contains(game1), is(true));
		assertThat(league.getPlayedGames().contains(game2), is(true));
		assertThat(league.getPlayedGames().contains(game3), is(true));
		assertThat(league.getRemainingGames().contains(game4), is(true));
	}
	
	@Test
	public void canGetGamesInChronologicalOrder() {
		Football football = new Football().save();
		League league = new League("league", football);
		Team team1 = new Team("team1").save();
		Team team2 = new Team("team2").save();
		league.teams = Arrays.asList(new Team[] { team1, team2 });
		league.save();
		
		Game game1 = new Game(league, Arrays.asList(new Team[] {team1, team2})).save();
		Game game2 = new Game(league, Arrays.asList(new Team[] {team1, team2})).save();
		Game game3 = new Game(league, Arrays.asList(new Team[] {team2, team1})).save();
		Game game4 = new Game(league, Arrays.asList(new Team[] {team2, team1})).save();
		
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
	public void canGetNumberOfPointsForWinLossAndTie() {
		League league = League.find("byName", "allsvenskan").first();
		Sport football = new Football();
		league.sport = football;
		
		assertThat(league.sport.getPointsForWin(), is(3));
		assertThat(league.sport.getPointsForTie(), is(1));
		assertThat(league.sport.getPointsForLoss(), is(0));
	}
	
	@Test
	public void canGetSport() {
		League league = League.find("byName", "allsvenskan").first();
		assertThat(league.sport.getName(), is("football"));
	}
	
	@Test
	public void canGetNumberOfPoints() {
		League allsvenskan = League.find("byName", "allsvenskan").first();
		Team gefleIf = Team.find("byName", "gefle_if").first();
		
		Integer gefleIf_points = allsvenskan.getPointsForTeam(gefleIf);
		
		assertThat(gefleIf_points, is(3));
		
		League league = new League("Fantasy League", new Football());
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		Team team4 = new Team("team4");
		league.teams = Arrays.asList(new Team[] { team1, team2, team3, team4 } );
		
		List<Game> newGames = new ArrayList<Game>();
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team2 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team3 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team3 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team2 } )));
		
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team2 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team3 } )));
		
		league.addGames(newGames);
		assertThat(league.games, is(notNullValue()));
		league.games.get(0).setScore(Arrays.asList(new Integer[] { 1, 2 } ));
		league.games.get(1).setScore(Arrays.asList(new Integer[] { 5, 0 } ));
		league.games.get(2).setScore(Arrays.asList(new Integer[] { 1, 0 } ));
		league.games.get(3).setScore(Arrays.asList(new Integer[] { 0, 0 } ));
		league.games.get(4).setScore(Arrays.asList(new Integer[] { 4, 6 } ));
		league.games.get(5).setScore(Arrays.asList(new Integer[] { 2, 2 } ));
		league.games.get(6).setScore(Arrays.asList(new Integer[] { 2, 1 } ));
		league.games.get(7).setScore(Arrays.asList(new Integer[] { 1, 1 } ));
		
		assertThat(league.getPointsForTeam(team1), is(7));
		assertThat(league.getPointsForTeam(team2), is(6));
		assertThat(league.getPointsForTeam(team3), is(7));
		assertThat(league.getPointsForTeam(team4), is(1));
	}
	
	@Test
	public void canGetWinsTiesAndLossesForTeam() {
		League allsvenskan = League.find("byName", "allsvenskan").first();
		Team gefleIf = Team.find("byName", "gefle_if").first();
		
		Integer gefleIf_points = allsvenskan.getPointsForTeam(gefleIf);
		
		assertThat(gefleIf_points, is(3));
		
		League league = new League("Fantasy League", new Football());
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		Team team4 = new Team("team4");
		league.teams = Arrays.asList(new Team[] { team1, team2, team3, team4 } );
		
		List<Game> newGames = new ArrayList<Game>();
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team2 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team3 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team3 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team2 } )));
		
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team2 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team3 } )));
		
		league.addGames(newGames);
		assertThat(league.games, is(notNullValue()));
		league.games.get(0).setScore(Arrays.asList(new Integer[] { 1, 2 } ));
		league.games.get(1).setScore(Arrays.asList(new Integer[] { 5, 0 } ));
		league.games.get(2).setScore(Arrays.asList(new Integer[] { 1, 0 } ));
		league.games.get(3).setScore(Arrays.asList(new Integer[] { 0, 0 } ));
		league.games.get(4).setScore(Arrays.asList(new Integer[] { 4, 6 } ));
		league.games.get(5).setScore(Arrays.asList(new Integer[] { 2, 2 } ));
		league.games.get(6).setScore(Arrays.asList(new Integer[] { 2, 1 } ));
		league.games.get(7).setScore(Arrays.asList(new Integer[] { 1, 1 } ));
		
		assertThat(league.getWinsForTeam(team1), is(2));
		assertThat(league.getWinsForTeam(team2), is(1));
		assertThat(league.getWinsForTeam(team3), is(2));
		assertThat(league.getWinsForTeam(team4), is(0));
		assertThat(league.getLossesForTeam(team1), is(2));
		assertThat(league.getLossesForTeam(team2), is(1));
		assertThat(league.getLossesForTeam(team3), is(1));
		assertThat(league.getLossesForTeam(team4), is(1));
		assertThat(league.getTiesForTeam(team1), is(1));
		assertThat(league.getTiesForTeam(team2), is(3));
		assertThat(league.getTiesForTeam(team3), is(1));
		assertThat(league.getTiesForTeam(team4), is(1));
	}
	
	@Test
	public void canGetGoalsScoredAndGoalsScoredAgainstForAGivenTeam() {
		League league = new League("Fantasy League", new Football());
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		Team team4 = new Team("team4");
		league.teams = Arrays.asList(new Team[] { team1, team2, team3, team4 } );
		
		List<Game> newGames = new ArrayList<Game>();
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team2 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team3 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team3 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team2 } )));
		
		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team4 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team2 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team3 } )));
		
		league.addGames(newGames);
		assertThat(league.games, is(notNullValue()));
		league.games.get(0).setScore(Arrays.asList(new Integer[] { 1, 2 } ));
		league.games.get(1).setScore(Arrays.asList(new Integer[] { 5, 0 } ));
		league.games.get(2).setScore(Arrays.asList(new Integer[] { 1, 0 } ));
		league.games.get(3).setScore(Arrays.asList(new Integer[] { 0, 0 } ));
		league.games.get(4).setScore(Arrays.asList(new Integer[] { 4, 6 } ));
		league.games.get(5).setScore(Arrays.asList(new Integer[] { 2, 2 } ));
		league.games.get(6).setScore(Arrays.asList(new Integer[] { 2, 1 } ));
		league.games.get(7).setScore(Arrays.asList(new Integer[] { 1, 1 } ));
		
		assertThat(league.getGoalsScoredByTeam(team1), is(8));
		assertThat(league.getGoalsScoredAgainstTeam(team1), is(4));
		assertThat(league.getGoalsScoredByTeam(team2), is(9));
		assertThat(league.getGoalsScoredAgainstTeam(team2), is(10));
		assertThat(league.getGoalsScoredByTeam(team3), is(9));
		assertThat(league.getGoalsScoredAgainstTeam(team3), is(11));
		assertThat(league.getGoalsScoredByTeam(team4), is(2));
		assertThat(league.getGoalsScoredAgainstTeam(team4), is(3));
	}
	
//	@Test
//	public void canGetTeamsOrderedByPointsWithOnePrioritiy() {
//		League league = new League("Fantasy League", new Football());
//		Team team1 = new Team("team1");
//		Team team2 = new Team("team2");
//		Team team3 = new Team("team3");
//		Team team4 = new Team("team4");
//		league.teams = Arrays.asList(new Team[] { team1, team2, team3, team4 } );
//		team1.league = league;
//		team2.league = league;
//		team3.league = league;
//		team4.league = league;
//		
//		List<Game> newGames = new ArrayList<Game>();
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team2 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team3 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team4 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team1 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team3 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team4 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team1 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team2 } )));
//		
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team3, team4 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team1 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team2 } )));
//		newGames.add(new Game(league, Arrays.asList(new Team[] { team4, team3 } )));
//		
//		league.addGames(newGames);
//		assertThat(league.games, is(notNullValue()));
//		league.games.get(0).setScore(Arrays.asList(new Integer[] { 1, 2 } ));
//		league.games.get(1).setScore(Arrays.asList(new Integer[] { 5, 0 } ));
//		league.games.get(2).setScore(Arrays.asList(new Integer[] { 1, 0 } ));
//		league.games.get(3).setScore(Arrays.asList(new Integer[] { 0, 0 } ));
//		league.games.get(4).setScore(Arrays.asList(new Integer[] { 4, 6 } ));
//		league.games.get(5).setScore(Arrays.asList(new Integer[] { 2, 2 } ));
//		league.games.get(6).setScore(Arrays.asList(new Integer[] { 10, 1 } ));
//		league.games.get(7).setScore(Arrays.asList(new Integer[] { 1, 1 } ));
//		
//		assertThat(league.teams.get(0), is(team1));
//		assertThat(league.teams.get(1), is(team2));
//		assertThat(league.teams.get(2), is(team3));
//		assertThat(league.teams.get(3), is(team4));
//
//		List<StatsPriority> priorities = Arrays.asList(new StatsPriority[] { StatsPriority.POINTS } );
//		List<Team> teamsOrderedByRank = league.sortTeamsByRank(priorities);
//		assertThat(teamsOrderedByRank.get(0), is(team1));
//		assertThat(teamsOrderedByRank.get(1), is(team3));
//		assertThat(teamsOrderedByRank.get(2), is(team2));
//		assertThat(teamsOrderedByRank.get(3), is(team4));
//
//		priorities = Arrays.asList(new StatsPriority[] { StatsPriority.GOALS_SCORED } );
//		teamsOrderedByRank = league.sortTeamsByRank(priorities);
//		assertThat(teamsOrderedByRank.get(0), is(team3));
//		assertThat(teamsOrderedByRank.get(1), is(team2));
//		assertThat(teamsOrderedByRank.get(2), is(team1));
//		assertThat(teamsOrderedByRank.get(3), is(team4));
//		
//		priorities = Arrays.asList(new StatsPriority[] { StatsPriority.GOAL_DIFFERENCE } );
//		teamsOrderedByRank = league.sortTeamsByRank(priorities);
//		assertThat(teamsOrderedByRank.get(0), is(team1));
//		assertThat(teamsOrderedByRank.get(1), is(team2));
//		assertThat(teamsOrderedByRank.get(2), is(team3));
//		assertThat(teamsOrderedByRank.get(3), is(team4));
//	}
	
	@Test
	public void canGetTeamsOrderedByPointsWithSeveralPriorities() {
		League league = new League("Fantasy League", new Football());
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		league.teams = Arrays.asList(new Team[] { team1, team2, team3 } );
		team1.league = league;
		team2.league = league;
		team3.league = league;
		
		List<Game> newGames = new ArrayList<Game>();
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team2 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team1 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team1, team3 } )));
		newGames.add(new Game(league, Arrays.asList(new Team[] { team2, team3 } )));
		
		league.addGames(newGames);
		assertThat(league.games, is(notNullValue()));
		league.games.get(0).setScore(Arrays.asList(new Integer[] { 1, 1 } ));
		league.games.get(1).setScore(Arrays.asList(new Integer[] { 3, 3 } ));
		league.games.get(2).setScore(Arrays.asList(new Integer[] { 5, 4 } ));
		league.games.get(3).setScore(Arrays.asList(new Integer[] { 3, 0 } ));
		
		assertThat(league.getGoalsScoredByTeam(team1) - league.getGoalsScoredAgainstTeam(team1), is(1));
		assertThat(league.getGoalsScoredByTeam(team2) - league.getGoalsScoredAgainstTeam(team2), is(3));
		assertThat(league.getGoalsScoredByTeam(team3) - league.getGoalsScoredAgainstTeam(team3), is(-4));
		
		List<StatsPriority> priorities = Arrays.asList(new StatsPriority[] { StatsPriority.POINTS, StatsPriority.GOAL_DIFFERENCE } );
		List<Team> teamsOrderedByRank = league.sortTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0).getName(), is("team2"));
		assertThat(teamsOrderedByRank.get(1).getName(), is("team1"));
		assertThat(teamsOrderedByRank.get(2).getName(), is("team3"));
		
		priorities = Arrays.asList(new StatsPriority[] { StatsPriority.POINTS, StatsPriority.GOALS_SCORED } );
		teamsOrderedByRank = league.sortTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0).getName(), is("team1"));
		assertThat(teamsOrderedByRank.get(1).getName(), is("team2"));
		assertThat(teamsOrderedByRank.get(2).getName(), is("team3"));
		
		league.games.add(new Game(league, Arrays.asList(new Team[] { team2, team3 } )));
		league.games.get(4).setScore(Arrays.asList(new Integer[] { 2, 3 } ));
		
		priorities = Arrays.asList(new StatsPriority[] { StatsPriority.POINTS, StatsPriority.GOALS_SCORED, StatsPriority.GOAL_DIFFERENCE } );
		teamsOrderedByRank = league.sortTeamsByRank(priorities);
		assertThat(teamsOrderedByRank.get(0).getName(), is("team2"));
		assertThat(teamsOrderedByRank.get(1).getName(), is("team1"));
		assertThat(teamsOrderedByRank.get(2).getName(), is("team3"));
	}
}