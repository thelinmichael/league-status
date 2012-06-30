package util;

import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.List;

import models.Game;
import models.League;
import models.Team;
import models.TeamTest;

import org.junit.Before;
import org.junit.Test;

import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

public class StatsTest extends UnitTest {
	
	@Test
	public void canGetALeaguesPlayedGames_andRemainingGames() {
		League league = new League("league");
		Team team1 = new Team("team1").save();
		Team team2 = new Team("team2").save();
		league.teams = Arrays.asList(new Team[] { team1, team2 });
		league.save();
		
		Game game1 = new Game(league, Arrays.asList(new Team[] {team1, team2})).save();
		Game game2 = new Game(league, Arrays.asList(new Team[] {team1, team2})).save();
		Game game3 = new Game(league, Arrays.asList(new Team[] {team2, team1})).save();
		Game game4 = new Game(league, Arrays.asList(new Team[] {team2, team1})).save();
		
		game1.setScore(Arrays.asList(new Integer[] {1,2}));
		game2.setScore(Arrays.asList(new Integer[] {2,1}));
		game3.setScore(Arrays.asList(new Integer[] {5,0}));
		
		league.addGame(game1);
		league.addGame(game2);
		league.addGame(game3);
		league.addGame(game4);
	
		LeagueStats stats = Stats.getLeagueStats(league);
		
		assertThat(stats.getPlayedGames().size(), is(3));
		assertThat(stats.getRemainingGames().size(), is(1));
		assertThat(stats.getPlayedGames().contains(game1), is(true));
		assertThat(stats.getPlayedGames().contains(game2), is(true));
		assertThat(stats.getPlayedGames().contains(game3), is(true));
		assertThat(stats.getRemainingGames().contains(game4), is(true));
	}
}
