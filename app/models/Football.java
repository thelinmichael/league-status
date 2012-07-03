package models;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import com.sun.org.glassfish.external.statistics.Stats;

import sports.ISport;
import util.Result;
import util.StatsPriority;

@Entity
public class Football extends Sport {

	public Integer win = 3;
	public Integer tie = 1;
	public Integer loss = 0;
	public String displayName = "Football";
	public String name = "football";
	
	public transient List<StatsPriority> priorities;
	
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

	@Override
	public List<StatsPriority> getStatsPriorities() {
		List<StatsPriority> priorities = Arrays.asList(new StatsPriority[] { 
														StatsPriority.POINTS, 
														StatsPriority.GOAL_DIFFERENCE,
													    StatsPriority.INDIVIDUAL_GAMES_BETWEEN_TEAMS,
														StatsPriority.GOALS_SCORED
		});
		return priorities;
	}
	
	public void setStatsPriorities(List<StatsPriority> priorities) {
		this.priorities = priorities;
	}
}
