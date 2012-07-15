package util;

import models.Team;

public interface HomeAndAwayTeams {
	public Team getHomeTeam();
	public Team getAwayTeam();
	public void setHomeTeam(Team team);
	public void setAwayTeam(Team team);
	public Integer getHomeTeamGoals();
	public Integer getAwayTeamGoals();
}
