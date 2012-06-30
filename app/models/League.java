package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class League extends Model {
	
	public String name;
	
	public String displayName;
	
	@OneToMany(mappedBy="league")
	public List<Game> games;
	
	@OneToMany(mappedBy="league")
	public List<Team> teams;
	
	public League(String name) {
		this.name = name;
		this.displayName = name.replace(' ', '_').toLowerCase();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public List<Team> getTeams() {
		return this.teams;
	}
	
	public List<Game> getGames() {
		return this.games;
	}

	public void addGame(Game game) {
		if (games == null) {
			games = new ArrayList<Game>();
		}
		games.add(game);
	}
}