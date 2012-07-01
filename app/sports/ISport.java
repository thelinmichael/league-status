package sports;

public interface ISport {
	
	public Integer pointsForWin();
	public Integer pointsForLoss();
	public Integer pointsForTie();
	
	public String getDisplayName();
	public String getName();

}
