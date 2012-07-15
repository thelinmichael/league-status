package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
import util.HasTime;
import util.Result;

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
		this.scores = new ArrayList<Score>();
		for (Integer intScore : intScores) {
			Score score = new Score(intScore);
			this.scores.add(score);
		}
	}
	
	public boolean isPlayed() {
		return (scores != null && scores.size() != 0);
	}
	
	public Team getHomeTeam() {
		return teams.get(0);
	}
	
	public Team getAwayTeam() {
		return teams.get(1);
	}
	
	public Integer getHomeTeamGoals() {
		if (isPlayed()) { 
			return scores.get(0).goals;
		} else {
			return null;
		}
	}
	
	public Integer getAwayTeamGoals() {
		if (isPlayed()) { 
			return scores.get(1).goals;
		} else {
			return null;
		}
	}

	public Result getResultFor(Team team) {
		final Result result;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} else if (!isPlayed()) {
			result = Result.UNDECIDED; 
		} else if (team.name == getHomeTeam().name) {
			if (getHomeTeamGoals() > getAwayTeamGoals()) {
				result = Result.WIN;
			} else if (getHomeTeamGoals() == getAwayTeamGoals()) {
				result = Result.TIE;
			} else {
				result = Result.LOSS;
			}
		} else {
			if (getHomeTeamGoals() > getAwayTeamGoals()) {
				result = Result.LOSS;
			} else if (getHomeTeamGoals() == getAwayTeamGoals()) {
				result = Result.TIE;
			} else {
				result = Result.WIN;
			}
		}
		
		return result;
	}

	public int getGoalsForTeam(Team team) {
		final int goals; 
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} else if (!isPlayed()) {
			throw new IllegalArgumentException("Game is not played.");
		}
		
		if (team.name == getHomeTeam().name) {
			goals = getHomeTeamGoals();
		} else {
			goals = getAwayTeamGoals();
		}
		
		return goals;
	}

	public int getGoalsAgainstTeam(Team team) {
		final int goals; 
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team did not play this game.");
		} else if (!isPlayed()) {
			throw new IllegalArgumentException("Game is not played.");
		}
		
		if (team.name == getHomeTeam().name) {
			goals = getAwayTeamGoals();
		} else {
			goals = getHomeTeamGoals();
		}
		
		return goals; 
	}

	@Override
	public Date getTime() {
		return time;
	}
}