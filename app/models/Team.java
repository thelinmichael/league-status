package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Team extends Model {
	
	public String name;
	
	public String displayName;
	
	@ManyToMany(mappedBy="teams")
	public List<Game> games;
	
	@ManyToOne
	public League league;
	
	@OneToMany(mappedBy="team", cascade=CascadeType.ALL)
	public List<Player> players;
	
	public Team(String name) {
		this.name = name;
		this.displayName = name.replace(' ', '_').toLowerCase();
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getName() {
		return name;
	}
	
	public League getLeague() {
		return league;
	}
}