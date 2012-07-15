package models;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;


import comparators.GoalDifferenceComparator;
import comparators.GoalsScoredComparator;
import comparators.IndividualGamesComparator;
import comparators.PointComparator;

import util.Result;

@Entity
public class Football extends Sport {

	public Integer win = 3;
	public Integer tie = 1;
	public Integer loss = 0;
	public String displayName = "Football";
	public String name = "football";
	
	public transient List<Comparator> comparators;
	
	@Override
	public Integer getPointsForWin() {
		return win;
	}

	@Override
	public Integer getPointsForLoss() {
		return loss;
	}

	@Override
	public Integer getPointsForTie() {
		return tie;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Integer getPointsFor(Result result) {
		if (result == Result.WIN) {
			return win;
		} else if (result == Result.TIE) {
			return tie;
		} else if (result == Result.LOSS) { 
			return loss;
		} else {
			throw new IllegalArgumentException("Result not available.");
		}
	}
	
	public List<Class<? extends Comparator<Team>>> getComparators() {
		return Arrays.asList(PointComparator.class, GoalDifferenceComparator.class, GoalsScoredComparator.class, IndividualGamesComparator.class);
	}
}
