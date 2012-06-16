package models;

import java.util.ArrayList;
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
	public List<Team> teams = new ArrayList<Team>();

	@OneToMany
	public List<Score> scores;
	
	public boolean isPlayed = false;
	
	public Game(List<Team> teams, League league) {
		this.league = league;
		this.teams = teams;
	}
	
	public void setScore(List<Integer> scores) {
		if (scores == null || scores.size() != teams.size()) {
			throw new IllegalArgumentException();
		} 
		this.scores = new ArrayList<Score>();
		for (Integer score : scores) {
			this.scores.add(new Score(score));
		}
		this.isPlayed = true;
	}

	public List<Team> getTeams() {
		return teams;
	}
}