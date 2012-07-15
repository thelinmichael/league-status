package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
import sports.ISport;

@Entity
public class Score extends Model implements Comparable {
	
	public Integer goals;

	public Score(Integer goals) {
		this.goals = goals;
	}

	@Override
	public int compareTo(Object o2) {
		Score otherScore = (Score) o2;
		return goals.compareTo(otherScore.goals);
	}
	
	public boolean equals(Object o2) {
		return (this.compareTo(o2) == 0);
		
	}
}
