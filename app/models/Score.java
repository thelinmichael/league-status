package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;
import util.ISport;

@Entity
public class Score extends Model implements Comparable<Score> {
	
	public Integer goals;

	public Score(Integer goals) {
		this.goals = goals;
	}

	public boolean equals(Object o2) {
		return (this.compareTo((Score) o2) == 0);
	}

	@Override
	public int compareTo(Score otherScore) {
		return goals.compareTo(otherScore.goals);
	}
}
