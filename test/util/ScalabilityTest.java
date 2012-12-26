package util;

import java.util.Arrays;

import models.Football;
import models.League;
import models.Team;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;

// This test will not run as a part of the regression suite. 
// It is only here to check how long time it takes to run certain
// parts of the application.
public class ScalabilityTest {

	/*
	 * Results (23:07 26th Dec)
	 *  0 Games remaining, 1 team:      5 ms
	 *  2 Games remaining, 2 teams:    23 ms
	 *  6 Games remaining, 3 teams:    77 ms
	 * 12 Games remaining, 4 teams: 15994 ms (second hit: 0 ms)
	 * 20 Games remaining, 5 teams: Java heap space error.
	 */
	@Test
	public void howLongDoesItTakeToGetTheBestRankForATeam_forALargeLeagueWithManyRemainingGames() {
		League league = new League("largeLeague", new Football());
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		Team team4 = new Team("team4");
		Team team5 = new Team("team5");
		
		league.teams = Arrays.asList(team1, team2, team3, team4);
		league.games = GameGenerator.generateAllVsAll(league);
		
		System.out.println("Number of games remaining: " + league.getRemainingGames().size());
		
		long startTime = System.currentTimeMillis();
		league.getBestPossibleRankFor(team1);
		long timeToGetBestRank = System.currentTimeMillis() - startTime;
		
		startTime = System.currentTimeMillis();
		league.getBestPossibleRankFor(team1);
		long timeToGetBestRankAgain = System.currentTimeMillis() - startTime;
		
		System.out.println("Time it took to get best rank for a team: " + timeToGetBestRank + " (ms)");
		System.out.println("Time it took to get best rank for a team again: " + timeToGetBestRankAgain + " (ms)");
	}
	
	// Number of leafs getAllPossibleGameEndCombinations will create:
		// 3^(number of remaining games) -> O(c^n) for c > 1, n remaining games. Exponential growth. 
	
		// 1 Remaining games -> 3 leafs
		// 2 Remaining games -> 9 leafs 
		// 3 Remaining games -> 27 leafs 
		// 4 Remaining games -> 64 leafs 
		// 5 Remaining games -> 243 leafs
		// 6 Remaining games -> 729 leafs 
		// 10 Remaining games -> 59049 leafs
		// 15 Remaining games -> 14 million leafs
		// 20 Remaining games -> 3 billion leafs
		// 25 Remaining games -> 847 billion leafs
		// 30 Remaining games -> 205 trillion leafs
	
	// Idea to solve: Calculate best possible rank for one end combination at a time.
	// Will still take a _long_ time to solve, but at the heap space won't be filled.
	
	/*
	 * Results (23:07 26th Dec)
	 * 12 Games remaining, 4 teams: 718 ms 
	 * 20 Games remaining, 5 teams: Java heap space error.
	 */
	@Test 
	public void howLongDoesItTakeToGetAllPossibleScenarios() {
		League league = new League("largeLeague", new Football());
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");
		Team team3 = new Team("team3");
		Team team4 = new Team("team4");
		Team team5 = new Team("team5");
		
		league.teams = Arrays.asList(team1, team2, team3, team4);
		league.games = GameGenerator.generateAllVsAll(league);
		
		System.out.println(league.teams.size() + " teams. Number of games remaining: " + league.getRemainingGames().size());
		
		long startTime = System.currentTimeMillis();
		FutureRankCalculator.getAllPossibleGameEndCombinations(league);
		long timeItTook = System.currentTimeMillis() - startTime;
		
		System.out.println("Time it took to get all possible Game End Combinations: " + timeItTook + " (ms)");
		
	}
}