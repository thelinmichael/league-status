package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Game extends Model {

	@ManyToOne
	public League league;
	
	@ManyToMany
	public List<Team> teams;
	
	@OneToMany
	public List<Score> scores;
	
	public Date time;
	
	public Game(League league, List<Team> teams) {
		this.league = league;
		this.teams = teams;
	}
	
	public List<Team> getTeams() {
		return teams;
	}
	
	public void setScore(List<Integer> intScores) {
		if (this.scores == null) {
			this.scores = new ArrayList<Score>();
		}
		
		for (Integer intScore : intScores) {
			Score score = new Score(intScore);
			this.scores.add(score);
		}
	}
	
	public boolean isPlayed() {
		return (scores != null && scores.size() != 0);
	}
	
}