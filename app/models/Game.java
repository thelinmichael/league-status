package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
import util.HasTime;
import util.Result;
import exceptions.GameNotPlayedException;

@Entity
public class Game extends Model implements HasTime {
 
	@ManyToOne
	public League league;
	
	@ManyToOne
	public Team homeTeam;
	
	@ManyToOne
	public Team awayTeam;
	
	public Integer homeTeamScore;
	
	public Integer awayTeamScore;
	
	public Date time;
	
	public Game(League league, Team homeTeam, Team awayTeam) {
		this.league = league;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
	}
	
	public void setHomeTeamScore(Integer homeTeamScore) {
		this.homeTeamScore = homeTeamScore;
	}
	
	public void setAwayTeamScore(Integer awayTeamScore) {
		this.awayTeamScore = awayTeamScore;
	}
	
	public void setScore(Integer homeTeamScore, Integer awayTeamScore) {
		this.homeTeamScore = homeTeamScore;
		this.awayTeamScore = awayTeamScore;
	}

	/* TODO: The concept of publishing needs to be expanded on.
	 * For example, all league functions relating to games should 
	 * only use games that are published. Games should be able to
	 * be added to leagues without being visible, or affecting
	 * the stats of the teams in the league (or its cache).
	 */
	public void publishChangesToLeague() {
		league.clearCache();
	}
	
	public boolean isPlayed() {
		return (homeTeamScore != null && awayTeamScore != null);
	}
	
	public Result getResultFor(Team team) {
		final Result result;
		
		if (!isPlayedBy(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} 
		
		if (!isPlayed()) {
			result = Result.UNDECIDED; 
		} else if (isTie()) {
			result = Result.TIE;
		} else if (isWinFor(team)) {
			result = Result.WIN;
		} else {
			result = Result.LOSS;
		}
		
		return result;
	}

	private boolean isWinFor(Team team) {
		if (team.equals(homeTeam)) {
			return (homeTeamScore > awayTeamScore);
		} else if (team.equals(awayTeam)) {
			return (awayTeamScore > homeTeamScore);
		} else {
			throw new IllegalArgumentException("Team didn't play this game.");
		}
	}

	public boolean isTie() {
		return (homeTeamScore == awayTeamScore);
	}

	public int getGoalsForTeam(Team team) throws GameNotPlayedException {
		if (!isPlayedBy(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} 
		if (!isPlayed()) {
			throw new GameNotPlayedException("Game is not played.");
		}
		
		if (homeTeam.equals(team)) {
			return homeTeamScore;
		} else {
			return awayTeamScore;
		}
	}

	public int getGoalsAgainstTeam(Team team) throws GameNotPlayedException {
		if (!isPlayedBy(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} 
		if (!isPlayed()) {
			throw new GameNotPlayedException("Game is not played.");
		}
		
		if (team.equals(homeTeam)) {
			return awayTeamScore;
		} else {
			return homeTeamScore;
		}
	}
	
	public boolean isPlayedBy(Team team) {
		return (homeTeam.equals(team) || awayTeam.equals(team));
	}

	@Override
	public Date getTime() {
		return time;
	}
}