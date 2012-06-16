package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Player extends Model {
	
	public String name;
	
	public String displayName;
	
	@ManyToOne
	public Team team;
	
	public Player(String name) {
		this.name = name;
		this.displayName = name.replace(' ', '_').toLowerCase();
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}

}
