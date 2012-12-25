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
	
	@ManyToMany
	public List<Score> scores;
	
	public Date time;
	
	public Game(League league, Team homeTeam, Team awayTeam) {
		this.league = league;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
	}
	
	public void setScore(List<Integer> intScores) {
		this.scores = new ArrayList<Score>();
		for (Integer intScore : intScores) {
			Score score = new Score(intScore);
			this.scores.add(score);
		}
	}
	
	public boolean isPlayed() {
		return (scores != null && scores.size() != 0);
	}
	
	public Result getResultFor(Team team) {
		final Result result;
		
		if (!wasPlayedBy(team)) {
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
		final int thisTeam;
		final int otherTeam;
		
		if (team.equals(homeTeam)) {
			thisTeam = 0;
			otherTeam = 1;
		} else if (team.equals(awayTeam)) {
			thisTeam = 1;
			otherTeam = 0;
		} else {
			throw new IllegalArgumentException("Team didn't play this game.");
		}
		
		if (scores.get(thisTeam).compareTo(scores.get(otherTeam)) == 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isTie() {
		Score maxScore = Collections.max(scores);
		Score minScore = Collections.min(scores);
		return (maxScore.compareTo(minScore) == 0);
	}

	public int getGoalsForTeam(Team team) throws GameNotPlayedException {
		if (!wasPlayedBy(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} 
		if (!isPlayed()) {
			throw new GameNotPlayedException("Game is not played.");
		}
		
		final int thisTeam;
		if (homeTeam.equals(team)) {
			thisTeam = 0;
		} else {
			thisTeam = 1;
		}
		
		return scores.get(thisTeam).goals;
	}

	public int getGoalsAgainstTeam(Team team) throws GameNotPlayedException {
		if (!wasPlayedBy(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} 
		if (!isPlayed()) {
			throw new GameNotPlayedException("Game is not played.");
		}
		
		int allGoals = 0;
		for (Score score : scores) {
			allGoals += score.goals;
		}
		
		return allGoals - getGoalsForTeam(team);
	}
	
	public boolean wasPlayedBy(Team team) {
		if (homeTeam.equals(team) || awayTeam.equals(team)) {
			return true;
		} else {
			return false;
		}
	}
	

	@Override
	public Date getTime() {
		return time;
	}
}