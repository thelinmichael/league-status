package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;

@Entity
public class Team extends Model {
	
	public String name;
	public String displayName;
	
	@ManyToMany(mappedBy="teams")
	public List<Game> games;
	
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