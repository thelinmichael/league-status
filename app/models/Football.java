package models;

import javax.persistence.Entity;

import sports.ISport;

@Entity
public class Football extends Sport {

	public Integer win = 3;
	public Integer tie = 1;
	public Integer loss = 0;
	public String displayName = "Football";
	public String name = "football";
	
	@Override
	public Integer pointsForWin() {
		return win;
	}

	@Override
	public Integer pointsForLoss() {
		return loss;
	}

	@Override
	public Integer pointsForTie() {
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
}
