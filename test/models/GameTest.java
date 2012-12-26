package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

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
		
		List<Game> games = Game.find("byLeague", allsvenskan).fetch();
		assertThat(games.size(), is(4));
		
		Game game = games.get(0);
		assertThat(game.league.name, is(notNullValue()));
		assertThat(game.homeTeamScore, is(notNullValue()));
		assertThat(game.awayTeamScore, is(notNullValue()));
		assertThat(game.homeTeam, is(notNullValue()));
		assertThat(game.awayTeam, is(notNullValue()));
	}
	
	@Test
	public void canGetParticipatingTeams() {
		Team team1 = new Team("Gefle IF");
		Team team2 = new Team("Djurgårdens IF");
		League league = new League("Allsvenskan", new Football());
		
		Game game = new Game(league, team1, team2);
		league.addGame(game);
		
		assertThat(league.games.contains(game), is(true));
	}
	
	@Test
	public void canSeeIfGameIsPlayed() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		League league = League.find("byName", "allsvenskan").first();
		
		Game game = new Game(league, team1, team2);
		assertThat(game.isPlayed(), is(false));
		
		game.homeTeamScore = 1;
		game.awayTeamScore = 2;
		assertThat(game.isPlayed(), is(true));
	}
	
	@Test
	public void canSetGameTime() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		League league = League.find("byName", "allsvenskan").first();

		Game game = new Game(league, team1, team2);
		Date time = new Date(0);
		game.time = time;
		game.save();
		Long id = game.getId(); 
		
		Game retrievedGame = Game.findById(id);
		assertThat(retrievedGame.time, is(time));
	}
	
	@Test
	public void canGetResultForTeam() {
		Football football = new Football().save();
		League league = new League("league", football);
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		
		Game game1 = new Game(league, team1, team2);
		Game game2 = new Game(league, team2, team1);
		
		assertThat(game1.getResultFor(team1), is(Result.UNDECIDED));
		
		game1.homeTeamScore = 0;
		game1.awayTeamScore = 0;
		assertThat(game1.getResultFor(team1), is(Result.TIE));

		game1.homeTeamScore = 1;
		game1.awayTeamScore = 0;
		assertThat(game1.getResultFor(team1), is(Result.WIN));

		game1.homeTeamScore = 0;
		game1.awayTeamScore = 1;
		assertThat(game1.getResultFor(team1), is(Result.LOSS));
		
		game2.homeTeamScore = 0; 
		game2.awayTeamScore = 0;
		assertThat(game2.getResultFor(team1), is(Result.TIE));
		
		game2.homeTeamScore = 1; 
		game2.awayTeamScore = 0;
		assertThat(game2.getResultFor(team1), is(Result.LOSS));
		
		game2.homeTeamScore = 0; 
		game2.awayTeamScore = 1;
		assertThat(game2.getResultFor(team1), is(Result.WIN));
	}
}