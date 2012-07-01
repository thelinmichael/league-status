package models;

import java.util.List;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

public class TeamTest extends UnitTest {
	
	@Before
	public void setupDb() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test
	public void canLoadFixturesCorrectly() {
		League allsvenskan = League.find("byName", "allsvenskan").first();
		
		List<Game> games = Game.find("byLeague", allsvenskan).fetch();

		Team gefleIf = Team.find("byName", "gefle_if").first();
		assertThat(gefleIf, is(notNullValue()));
		assertThat(gefleIf.games.size(), is(1));
		
		assertThat(games.get(0).getTeams(), is(notNullValue()));
		assertThat(games.get(0).getTeams().size(), is(2));
		
		List<Game> gefleIf_games = gefleIf.games;
		List<Score> score = gefleIf_games.get(0).scores;
		
		assertThat(score, is(notNullValue()));
		assertThat(score.get(0).goals, is(1));
		assertThat(score.get(1).goals, is(0));
		
		Team team = Team.find("byName", "djurgardens_if").first();
		List<Game> djurgarden_games = team.games;
		Game dif_aik = djurgarden_games.get(0);
		List<Score> dif_aik_scores = dif_aik.scores;
		assertThat(dif_aik_scores.get(0).goals, is(1));
		assertThat(dif_aik_scores.get(1).goals, is(0));
	}
	
	@Test
	public void canGetDisplayName() {
		Team gefle = Team.find("byName", "gefle_if").first();
		
		assertThat(gefle, is(notNullValue()));
		assertThat(gefle.getDisplayName(), is("Gefle IF"));
	}
	
	@Test
	public void canGetLeague() {
		Team gefleIf = Team.find("byName", "gefle_if").first();
		assertThat(gefleIf, is(notNullValue()));
		assertThat(gefleIf.getLeague().getDisplayName(), is("Allsvenskan"));
	}
}