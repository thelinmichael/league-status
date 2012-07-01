package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;
import sports.ISport;

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
		League league = new League("league");
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
		League league = new League("league");
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
		
		assertThat(league.sport.pointsForWin(), is(3));
		assertThat(league.sport.pointsForTie(), is(1));
		assertThat(league.sport.pointsForLoss(), is(0));
	}
	
	@Test
	public void canGetSport() {
		League league = League.find("byName", "allsvenskan").first();
		assertThat(league.sport.getName(), is("football"));
	}
}