package util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.List;

import models.League;
import models.Team;

import org.junit.Test;

import play.test.UnitTest;

public class RankCacheTest extends UnitTest {
	
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
}
