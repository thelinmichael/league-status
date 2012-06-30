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
import util.Stats;

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
}