package controller;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {
	
	@Before
	public void before() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test
	public void gettingALeagueWorks() {
		Response response = GET("/league/allsvenskan");
		assertIsOk(response);
	}
	
	@Test
	public void gettingALeagueWithBadNameReturns404() {
		Response response = GET("/league/bogusLeagueName");
		assertIsNotFound(response);	}
}
