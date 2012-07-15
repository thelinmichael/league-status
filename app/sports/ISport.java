package sports;

import java.util.Comparator;
import java.util.List;

import models.Team;

import util.Result;

public interface ISport<T> {
	
	public Integer getPointsForWin();
	public Integer getPointsForLoss();
	public Integer getPointsForTie();
	public Integer getPointsFor(Result result);
	
	public String getDisplayName();
	public String getName();
	public List<Class<? extends Comparator<T>>> getComparators();
}
