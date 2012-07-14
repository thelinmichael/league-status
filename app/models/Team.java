package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Team extends Model implements Comparable {
	
	public String name;
	
	public String displayName;
	
	@ManyToMany(mappedBy="teams")
	public List<Game> games;
	
	@ManyToOne
	public League league;
	
	public Team(String name) {
		this.name = name;
		this.displayName = name.replace(' ', '_').toLowerCase();
	}
	
	public String toString() {
		return displayName;
	}

	@Override
	public int compareTo(Object object) {
		Team otherTeam = (Team) object;
		if (!league.teams.contains(otherTeam)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		return league.getPointsForTeam(otherTeam).compareTo(league.getPointsForTeam(this));
	}
}