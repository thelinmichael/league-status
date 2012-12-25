package models;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;

import comparators.GoalDifferenceComparator;
import comparators.GoalsScoredComparator;
import comparators.IndividualGamesComparator;
import comparators.PointComparator;

/* European Cup football differs from 'regular' football in  
 * that individual games between teams are considered more important
 * than the teams' goal difference. */
@Entity
public class EurocupFootball extends Football {
	
	@Override
	public List<Class<? extends Comparator<Team>>> getComparators() {
		return Arrays.asList(PointComparator.class, IndividualGamesComparator.class, GoalDifferenceComparator.class, GoalsScoredComparator.class);
	}

}
