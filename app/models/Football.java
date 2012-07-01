package models;

import javax.persistence.Entity;

import sports.ISport;
import util.Result;

@Entity
public class Football extends Sport {

	public Integer win = 3;
	public Integer tie = 1;
	public Integer loss = 0;
	public String displayName = "Football";
	public String name = "football";
	
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

}
