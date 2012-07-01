package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
import sports.ISport;

@Entity
public class Score extends Model {
	
	public Integer goals;

	public Score(Integer goals) {
		this.goals = goals;
	}
	
}
