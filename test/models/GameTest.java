package models;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class GameTest extends UnitTest{
	
	@Before
	public void setupDb() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test
	public void canGetParticipatingTeams() {
		Team team1 = new Team("Gefle IF");
		Team team2 = new Team("Djurgårdens IF");
		Game game = new Game(Arrays.asList(team1, team2));

		assertThat(game.getTeams().contains(team1), is(true));
		assertThat(game.getTeams().contains(team2), is(true));
	}
}