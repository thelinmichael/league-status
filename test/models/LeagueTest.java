package models;

import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class LeagueTest extends UnitTest { 
	
	@Before
	public void setupDb() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test
	public void canGetTeamsFromLeague() {
		League league = League.find("byName", "allsvenskan").first();
		assertThat(league, is(notNullValue()));

		assertThat(league.getTeams().size(), is(5));
	}
	
	@Test
	public void canAddGames() {
		League league = League.find("byName", "allsvenskan").first();

		int numberOfGames = league.getGames().size();
		
		Team team1 = league.getTeams().get(0);
		Team team2 = league.getTeams().get(1);
		
		Game game = new Game(Arrays.asList(new Team[] {team1, team2}), league);
		
		league.addGame(game);
		assertThat(league.getGames().size(), is(numberOfGames + 1));
	}
	
	@Test
	public void canGetPlayedGames_andRemainingGames() {
		League league = League.find("byName", "allsvenskan").first();
		
		Team team1 = league.getTeams().get(0);
		Team team2 = league.getTeams().get(1);
		
		int numberOfGames = league.getGames().size();
		int numberOfPlayedGames = league.getPlayedGames().size();
		int numberOfRemainingGames = league.getRemainingGames().size();
		
		assertThat(numberOfPlayedGames + numberOfRemainingGames, is(numberOfGames));
		
		Game game1 = new Game(Arrays.asList(new Team[] {team1, team2}), league);
		Game game2 = new Game(Arrays.asList(new Team[] {team1, team2}), league);
		Game game3 = new Game(Arrays.asList(new Team[] {team2, team1}), league);
		Game game4 = new Game(Arrays.asList(new Team[] {team2, team1}), league);
		
		game1.setScore(Arrays.asList(new Integer[] {1,2}));
		game2.setScore(Arrays.asList(new Integer[] {2,1}));
		game3.setScore(Arrays.asList(new Integer[] {1,1}));
		
		league.addGame(game1);
		league.addGame(game2);
		league.addGame(game3);
		league.addGame(game4);
		
		assertThat(league.getPlayedGames().size(), is(numberOfPlayedGames+3));
		
		assertThat(league.getRemainingGames().size(), is(numberOfRemainingGames+1));
		assertThat(league.getRemainingGames().contains(game4), is(true));
	}
	
	@Test
	public void canGetStatsForTeamInLeague() {
		
	}
	
	@Test
	public void shouldGenerateAGameWhereEveryoneMeetsEveryoneTwice() {
		
	}
}