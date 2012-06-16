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
	public void canGetPlayedGames_andRemainingGames() {
	}
	
	@Test
	public void canGetStatsForTeamInLeague() {
	}
}