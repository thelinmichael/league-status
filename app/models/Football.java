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

	public final static Integer POINTS_FOR_WIN = 3;
	public final static Integer POINTS_FOR_TIE = 1;
	public final static Integer POINTS_FOR_LOSS = 0;
	public final static Integer SEASON_START_POINTS = 0;
	
	public final static String DISPLAY_NAME = "Football";
	public final static String NAME = "football";
	
	public transient List<Comparator> comparators;
	
	@Override
	public Integer getPointsForWin() {
		return POINTS_FOR_WIN;
	}

	@Override
	public Integer getPointsForLoss() {
		return POINTS_FOR_LOSS;
	}

	@Override
	public Integer getPointsForTie() {
		return POINTS_FOR_TIE;
	}
	
	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	public Integer getPointsFor(Result result) {
		if (result == Result.WIN) {
			return POINTS_FOR_WIN;
		} else if (result == Result.TIE) {
			return POINTS_FOR_TIE;
		} else if (result == Result.LOSS) { 
			return POINTS_FOR_LOSS;
		} else {
			throw new IllegalArgumentException("Result not available.");
		}
	}
	
	public List<Class<? extends Comparator<Team>>> getComparators() {
		return Arrays.asList(PointComparator.class, GoalDifferenceComparator.class, GoalsScoredComparator.class, IndividualGamesComparator.class);
	}
}
