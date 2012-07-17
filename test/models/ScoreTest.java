package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ScoreTest extends UnitTest {
	
	@Before
	public void setupDb() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test
	public void canLoadGameScores() {
		League allsvenskan = League.find("byName", "allsvenskan").first();
		List<Game> games = Game.find("byLeague", allsvenskan).fetch();

		assertThat(games.get(0).teams, is(notNullValue()));
		assertThat(games.get(0).teams.size(), is(2));
		
		assertThat(games.get(0).isPlayed(), is(true));
		assertThat(games.get(0).scores.get(0).goals, is(1));
		assertThat(games.get(0).scores.get(1).goals, is(0));
	}
}