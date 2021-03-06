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
		assertThat(gefle.displayName, is("Gefle IF"));
	}
	
	@Test
	public void canGetLeagues() {
		Team gefleIf = Team.find("byName", "gefle_if").first();
		assertThat(gefleIf, is(notNullValue()));
		assertThat(gefleIf.leagues.get(0).displayName, is("Allsvenskan"));
	}
}