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
	public void canGetDisplayName() {
		Team gefle = Team.find("byName", "gefle_if").first();
		
		assertThat(gefle, is(notNullValue()));
		assertThat(gefle.getDisplayName(), is("Gefle IF"));
	}
	
	@Test
	public void canGetPlayersInSpecificTeam() {
		Team gefle = Team.find("byName", "gefle_if").first();

		List<Player> players = Player.find("byTeam", gefle).fetch();
		
		assertThat(players.size(), is(2));
		assertThat(players.get(0).getName(), is("michael_thelin"));
		assertThat(players.get(1).getName(), is("zlatan_ibrahimovic"));
	}
	
	@Test
	public void canGetLeague() {
		Team gefleIf = Team.find("byName", "gefle_if").first();
		assertThat(gefleIf, is(notNullValue()));
		assertThat(gefleIf.getLeague().getDisplayName(), is("Allsvenskan"));
	}
	
	@Test
	public void canGetTeamsInLeague() {
		League league = League.find("byName", "allsvenskan").first();
		
		assertThat(league, is(notNullValue()));
		
		List<Team> teams = Team.find("byLeague", league).fetch();
		assertThat(teams.size(), is(5));
	}
}