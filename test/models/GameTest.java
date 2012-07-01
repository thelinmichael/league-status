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

public class GameTest extends UnitTest{
	
	@Before
	public void setupDb() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}

	@Test
	public void canGetGameScoresAndTeamsFromFixtures() {
		League allsvenskan = League.find("byName", "allsvenskan").first();
		
		// Game related
		List<Game> games = Game.find("byLeague", allsvenskan).fetch();
		assertThat(games.size(), is(4));
		
		Game game = games.get(0);
		assertThat(game.league.name, is(notNullValue()));
		assertThat(game.scores, is(notNullValue()));
		assertThat(game.teams, is(notNullValue()));
	}

	
	@Test
	public void canGetParticipatingTeams() {
		Team team1 = new Team("Gefle IF");
		Team team2 = new Team("Djurgårdens IF");
		League league = new League("Allsvenskan");
		
		Game game = new Game(league, Arrays.asList(new Team[] {team1, team2}));
		league.addGame(game);
		
		assertThat(league.getGames().contains(game), is(true));
	}
	
	@Test
	public void canSeeIfGameIsPlayed() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		League league = League.find("byName", "allsvenskan").first();
		
		Game game = new Game(league, Arrays.asList(team1, team2));
		assertThat(game.isPlayed(), is(false));
		
		game.setScore(Arrays.asList(new Integer[] {1,2}));
		assertThat(game.isPlayed(), is(true));
	}
	
	@Test
	public void canSetGameTime() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		League league = League.find("byName", "allsvenskan").first();

		Game game = new Game(league, Arrays.asList(team1, team2));
		Date time = new Date(0);
		game.time = time;
		game.save();
		Long id = game.getId(); 
		
		Game retrievedGame = Game.findById(id);
		assertThat(retrievedGame.time, is(time));
	}
}