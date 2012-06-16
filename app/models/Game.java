package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Game extends Model {

	@ManyToOne
	public League league;
	
	@ManyToMany
	public List<Team> teams;
	
	public Game(List<Team> teams) {
		this.teams = teams;
	}

	public List<Team> getTeams() {
		return teams;
	}
}