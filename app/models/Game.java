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
	
	@ManyToMany
	public List<Team> teams;
	
	@ManyToMany
	public List<Score> scores;
	
	public Date time;
	
	public Game(League league, List<Team> teams) {
		this.league = league;
		this.teams = teams;
	}
	
	public void setScore(List<Integer> intScores) {
		league.clearTeamRank();
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
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} else if (!isPlayed()) {
			result = Result.UNDECIDED; 
		} else if (isTie()) {
			result = Result.TIE;
		} else if (getTeamsWithHighestScores().contains(team)) {
			result = Result.WIN;
		} else {
			result = Result.LOSS;
		}
		
		return result;
	}

	public boolean isTie() {
		Score maxScore = Collections.max(scores);
		Score minScore = Collections.min(scores);
		return (maxScore.compareTo(minScore) == 0);
	}

	public List<Team> getTeamsWithHighestScores() {
		List<Team> winners = new ArrayList<Team>();
		Score maxScore = Collections.max(scores);
		for (Team team : teams) {
			if (scores.get(teams.indexOf(team)).equals(maxScore)) {
				winners.add(team);
			}
		}
		return winners;
	}
	
	public int getGoalsForTeam(Team team) throws GameNotPlayedException {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		}
		if (!isPlayed()) {
			throw new GameNotPlayedException("Game is not played.");
		}
		
		return scores.get(teams.indexOf(team)).goals;
	}

	public int getGoalsAgainstTeam(Team team) throws GameNotPlayedException {
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} else if (!isPlayed()) {
			throw new GameNotPlayedException("Game is not played.");
		}
		
		int allGoals = 0;
		for (Score score : scores) {
			allGoals += score.goals;
		}
		
		return allGoals - getGoalsForTeam(team);
	}
	

	@Override
	public Date getTime() {
		return time;
	}
}