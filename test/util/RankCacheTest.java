package util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.List;

import models.Game;
import models.League;
import models.Team;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class RankCacheTest extends UnitTest {
	
	@Before
	public void setupDb() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("/mock/test_league.yml");
	}
	
	@Test
	public void shouldCacheTeamRanks() {
		League league = League.find("byName", "euro-group-d").first();
		Team sweden = Team.find("byName", "sweden").first();
		Team england = Team.find("byName", "england").first();
		Team ukraine = Team.find("byName", "ukraine").first();
		Team france = Team.find("byName", "france").first();
		
		league.rankCache = new RankCache();
		assertThat(league.rankCache.getTeamsByRank(), is(nullValue()));  
		
		List<Team> teams = league.getTeamsByRank();
		assertThat(teams.get(0), is(france));
		assertThat(teams.get(1), is(england));
		assertThat(teams.get(2), is(ukraine));
		assertThat(teams.get(3), is(sweden));

		List<Team> cachedTeams = league.rankCache.getTeamsByRank();
		assertThat(cachedTeams, is(notNullValue()));  
		assertThat(cachedTeams.get(0), is(france));
		assertThat(cachedTeams.get(1), is(england));
		assertThat(cachedTeams.get(2), is(ukraine));
		assertThat(cachedTeams.get(3), is(sweden));
	}
	
	@Test
	public void  shouldCacheBestRankForTeam() {
		League league = League.find("byName", "euro-group-d").first();
		Team england = Team.find("byName", "england").first();
		
		league.rankCache = new RankCache();
		assertThat(league.rankCache.getBestRankFor(england), is(nullValue()));  
		
		int bestRank = league.getBestPossibleRankFor(england);
		assertThat(bestRank, is(1));

		Integer cachedBestRank = league.rankCache.getBestRankFor(england);
		assertThat(cachedBestRank, is(1));  
	}
	
	@Test
	public void shouldCacheWorstRankForTeam() {
		League league = League.find("byName", "euro-group-d").first();
		Team england = Team.find("byName", "england").first();
		
		league.rankCache = new RankCache();
		assertThat(league.rankCache.getWorstRankFor(england), is(nullValue()));  
		
		int worstRank = league.getWorstPossibleRankFor(england);
		assertThat(worstRank, is(3));

		Integer cachedWorstRank = league.rankCache.getWorstRankFor(england);
		assertThat(cachedWorstRank, is(3));  
	}
	
	@Test
	public void shouldClearCacheWhenPublishingGame_whenGameIsAlreadyAddedToTheLeague() {
		League league = League.find("byName", "euro-group-d").first();
		Team england = Team.find("byName", "england").first();
		Team ukraine = Team.find("byName", "ukraine").first();
		
		league.rankCache = new RankCache();
		assertThat(league.rankCache.getTeamsByRank(), is(nullValue()));
		assertThat(league.rankCache.getWorstRankFor(england), is(nullValue()));  
		assertThat(league.rankCache.getBestRankFor(england), is(nullValue()));
		assertThat(league.rankCache.getWorstRankFor(ukraine), is(nullValue()));  
		assertThat(league.rankCache.getBestRankFor(ukraine), is(nullValue()));
		
		league.getTeamsByRank();
		league.getBestPossibleRankFor(england);
		league.getWorstPossibleRankFor(england);
		league.getBestPossibleRankFor(ukraine);
		league.getWorstPossibleRankFor(ukraine);
		
		assertThat(league.rankCache.getTeamsByRank(), is(notNullValue()));
		assertThat(league.rankCache.getWorstRankFor(england), is(notNullValue()));  
		assertThat(league.rankCache.getBestRankFor(england), is(notNullValue()));
		assertThat(league.rankCache.getWorstRankFor(ukraine), is(notNullValue()));  
		assertThat(league.rankCache.getBestRankFor(ukraine), is(notNullValue()));
		
		Game game = league.getRemainingGames().get(0);
		assertThat(game.homeTeam, is(england));
		assertThat(game.awayTeam, is(ukraine));
		game.setScore(1, 2);
		game.publishChangesToLeague();
		
		assertThat(league.rankCache.getTeamsByRank(), is(nullValue()));
		assertThat(league.rankCache.getWorstRankFor(england), is(nullValue()));  
		assertThat(league.rankCache.getBestRankFor(england), is(nullValue()));
		assertThat(league.rankCache.getWorstRankFor(ukraine), is(nullValue()));  
		assertThat(league.rankCache.getBestRankFor(ukraine), is(nullValue()));
	}
	
	@Test
	public void shouldClearCacheWhenGameIsAddedToLeagueAndPublishingIt() {
		League league = League.find("byName", "euro-group-d").first();
		Team england = Team.find("byName", "england").first();
		Team ukraine = Team.find("byName", "ukraine").first();
		
		league.rankCache = new RankCache();
		assertThat(league.rankCache.getTeamsByRank(), is(nullValue()));
		assertThat(league.rankCache.getWorstRankFor(england), is(nullValue()));  
		assertThat(league.rankCache.getBestRankFor(england), is(nullValue()));
		assertThat(league.rankCache.getWorstRankFor(ukraine), is(nullValue()));  
		assertThat(league.rankCache.getBestRankFor(ukraine), is(nullValue()));
		
		league.getTeamsByRank();
		league.getBestPossibleRankFor(england);
		league.getWorstPossibleRankFor(england);
		league.getBestPossibleRankFor(ukraine);
		league.getWorstPossibleRankFor(ukraine);
		
		assertThat(league.rankCache.getTeamsByRank(), is(notNullValue()));
		assertThat(league.rankCache.getWorstRankFor(england), is(notNullValue()));  
		assertThat(league.rankCache.getBestRankFor(england), is(notNullValue()));
		assertThat(league.rankCache.getWorstRankFor(ukraine), is(notNullValue()));  
		assertThat(league.rankCache.getBestRankFor(ukraine), is(notNullValue()));
		
		Game game = new Game(league, england, ukraine);
		game.publishChangesToLeague();
		
		assertThat(league.rankCache.getTeamsByRank(), is(nullValue()));
		assertThat(league.rankCache.getWorstRankFor(england), is(nullValue()));  
		assertThat(league.rankCache.getBestRankFor(england), is(nullValue()));
		assertThat(league.rankCache.getWorstRankFor(ukraine), is(nullValue()));  
		assertThat(league.rankCache.getBestRankFor(ukraine), is(nullValue()));
	}
	
	@Test
	public void shouldTakeLessTimeToGetBestRankForCachedCall() {
		League league = League.find("byName", "euro-group-d").first();
		Team england = Team.find("byName", "england").first();
		
		long startTime = System.currentTimeMillis();
		league.getBestPossibleRankFor(england);
		long timeForFirstCall = System.currentTimeMillis() - startTime;
		
		long secondStartTime = System.currentTimeMillis();
		league.getBestPossibleRankFor(england);
		long timeForSecondCall = System.currentTimeMillis() - secondStartTime;
		
		assertTrue(timeForFirstCall > timeForSecondCall);
	}
}