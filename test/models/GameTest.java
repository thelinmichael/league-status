package models;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;
import util.Result;

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
		League league = new League("Allsvenskan", new Football());
		
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
	
	@Test
	public void canGetHomeAndAwayTeam() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		League league = League.find("byName", "allsvenskan").first();
		Game game = new Game(league, Arrays.asList(team1, team2));
		
		assertThat(game.getHomeTeam(), is(team1));
		assertThat(game.getAwayTeam(), is(team2));
	}
	
	@Test
	public void canGetHomeAndAwayTeamGoals() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		League league = League.find("byName", "allsvenskan").first();
		Game game = new Game(league, Arrays.asList(team1, team2));
		
		assertThat(game.getHomeTeamGoals(), is(nullValue()));
		assertThat(game.getAwayTeamGoals(), is(nullValue()));
		
		game.setScore(Arrays.asList(new Integer[] {1,1} ));

		assertThat(game.getHomeTeamGoals(), is(1));
		assertThat(game.getAwayTeamGoals(), is(1));
	}
	
	@Test
	public void canGetResultForTeam() {
		Football football = new Football().save();
		League league = new League("league", football);
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		league.teams = Arrays.asList(new Team[] { team1, team2 } );
		
		Game game1 = new Game(league, Arrays.asList(new Team[] { team1, team2 } ));
		Game game2 = new Game(league, Arrays.asList(new Team[] { team2, team1 } ));
		
		assertThat(game1.getResultFor(team1), is(Result.UNDECIDED));
		
		game1.setScore(Arrays.asList(new Integer[] { 0, 0 }));
		assertThat(game1.getResultFor(team1), is(Result.TIE));

		game1.setScore(Arrays.asList(new Integer[] { 1, 0 }));
		assertThat(game1.getResultFor(team1), is(Result.WIN));

		game1.setScore(Arrays.asList(new Integer[] { 0, 1 }));
		assertThat(game1.getResultFor(team1), is(Result.LOSS));
		
		game2.setScore(Arrays.asList(new Integer[] { 0, 0 }));
		assertThat(game2.getResultFor(team1), is(Result.TIE));
		
		game2.setScore(Arrays.asList(new Integer[] { 1, 0 }));
		assertThat(game2.getResultFor(team1), is(Result.LOSS));
		
		game2.setScore(Arrays.asList(new Integer[] { 0, 1 }));
		assertThat(game2.getResultFor(team1), is(Result.WIN));
	}
}