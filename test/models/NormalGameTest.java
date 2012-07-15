package models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.Arrays;

import org.junit.Test;

import play.test.UnitTest;

public class NormalGameTest extends UnitTest {

	@Test
	public void canGetHomeAndAwayTeam() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		
		League league = League.find("byName", "allsvenskan").first();
		NormalGame game = new NormalGame(league, Arrays.asList(team1, team2));
		
		assertThat(game.getHomeTeam(), is(team1));
		assertThat(game.getAwayTeam(), is(team2));
	}
	
	@Test
	public void canGetHomeAndAwayTeamGoals() {
		Team team1 = Team.find("byName", "gefle_if").first();
		Team team2 = Team.find("byName", "aik").first();
		League league = League.find("byName", "allsvenskan").first();
		NormalGame game = new NormalGame(league, Arrays.asList(team1, team2));
		
		assertThat(game.getHomeTeamGoals(), is(nullValue()));
		assertThat(game.getAwayTeamGoals(), is(nullValue()));
		
		game.setScore(Arrays.asList(1,1));

		assertThat(game.getHomeTeamGoals(), is(1));
		assertThat(game.getAwayTeamGoals(), is(1));
	}
}
