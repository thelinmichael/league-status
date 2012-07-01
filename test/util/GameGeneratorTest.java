package util;

import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.List;

import models.Football;
import models.Game;
import models.League;
import models.Team;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class GameGeneratorTest extends UnitTest {

	@Before
	public void before() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test 
	public void shouldReturnTwoGamesPerTeamInLeague() {
		League league = League.find("byName", "allsvenskan").first();
		assertThat(league.getTeams().size(), is(5));
		
		List<Game> generatedGames = GameGenerator.generateAllVsAll(league);
		assertThat(generatedGames.size(), is(20));
		
		league = new League("smallLeague", new Football());
		Team team = new Team("onlyTeam");
		league.teams = Arrays.asList(new Team[] { team });
		generatedGames = GameGenerator.generateAllVsAll(league);
		assertThat(generatedGames.size(), is(0));

		league = new League("twoTeamLeague", new Football());
		Team firstTeam = new Team("firstTeam");
		Team secondteam = new Team("secondTeam");
		league.teams = Arrays.asList(new Team[] { firstTeam, secondteam });
		generatedGames = GameGenerator.generateAllVsAll(league);
		assertThat(generatedGames.size(), is(2));
	}
}
