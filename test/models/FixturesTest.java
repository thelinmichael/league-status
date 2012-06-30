package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class FixturesTest extends UnitTest {
	
	@Before
	public void setupDb() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test
	public void canLoadFixturesCorrectly() {
		League allsvenskan = League.find("byName", "allsvenskan").first();
		
		assertThat(allsvenskan, is(notNullValue()));
		assertThat(allsvenskan.getTeams().size(), is(5));
		assertThat(allsvenskan.getGames().size(), is(4));
		
		List<Game> games = Game.find("byLeague", allsvenskan).fetch();
		assertThat(games.size(), is(4));
	}	
}
