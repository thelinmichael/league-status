package sports;

import java.util.List;

import util.Result;
import util.StatsPriority;

public interface ISport {
	
	public Integer getPointsForWin();
	public Integer getPointsForLoss();
	public Integer getPointsForTie();
	public Integer getPointsFor(Result result);
	
	public String getDisplayName();
	public String getName();
	public List<StatsPriority> getStatsPriorities();
	public void setStatsPriorities(List<StatsPriority> priorities);
}
