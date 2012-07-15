package util;

import java.util.Comparator;
import java.util.List;

import models.Team;

public interface ISport<T> {
	public Integer getPointsForWin();
	public Integer getPointsForLoss();
	public Integer getPointsForTie();
	public Integer getPointsFor(Result result);
	
	public String getDisplayName();
	public String getName();
	public List<Class<? extends Comparator<T>>> getComparators();
}
