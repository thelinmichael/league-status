package sports;

import util.Result;

public interface ISport {
	
	public Integer getPointsForWin();
	public Integer getPointsForLoss();
	public Integer getPointsForTie();
	public Integer getPointsFor(Result result);
	
	public String getDisplayName();
	public String getName();

}
