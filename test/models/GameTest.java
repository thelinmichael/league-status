package models;

import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
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
		League league = new League("Allsvenskan");
		Game game = new Game(Arrays.asList(team1, team2), league);

		assertThat(game.getTeams().contains(team1), is(true));
		assertThat(game.getTeams().contains(team2), is(true));
	}
	
	@Test
	public void canSeeIfGameIsPlayed() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		League league = League.find("byName", "allsvenskan").first();
		
		Game game = new Game(Arrays.asList(team1, team2), league);
		assertThat(game.isPlayed, is(false));
		
		game.setScore(Arrays.asList(new Integer[] {1,2}));
		assertThat(game.isPlayed, is(true));
	}
}