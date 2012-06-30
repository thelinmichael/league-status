package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Score extends Model {
	
	public int goals;

	public Score(int goals) {
		this.goals = goals;
	}
	
	public String toString() {
		return String.valueOf(goals);
	}

}
