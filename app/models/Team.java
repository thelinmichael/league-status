package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Team extends Model {
	
	public String name;
	public String displayName;
	
	@OneToMany(mappedBy="homeTeam")
	public List<Game> homeTeamGames;
	
	@OneToMany(mappedBy="awayTeam")
	public List<Game> awayTeamGames;
	
	@ManyToMany
	public List<League> leagues;
	
	public Team(String name) {
		this.name = name;
		this.displayName = name.replace(' ', '_').toLowerCase();
	}
	
	public String toString() {
		return displayName;
	}
}