package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Score extends Model {
	
	private int goals;

	public Score(int goals) {
		this.goals = goals;
	}

}
