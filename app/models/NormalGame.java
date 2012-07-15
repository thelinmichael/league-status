package models;

import java.util.List;

import javax.persistence.Entity;


import util.HomeAndAwayTeams;

@Entity
public class NormalGame extends Game implements HomeAndAwayTeams {

	public Team homeTeam;
	public Team awayTeam;

	public NormalGame(League league, List<Team> teams) {
		super(league, teams);
		homeTeam = teams.get(0);
		awayTeam = teams.get(1);
	}
	
	public Integer getHomeTeamGoals() {
		if (isPlayed()) { 
			return scores.get(0).goals;
		} else {
			return null;
		}
	}
	
	public Integer getAwayTeamGoals() {
		if (isPlayed()) { 
			return scores.get(1).goals;
		} else {
			return null;
		}
	}
	
	public Team getHomeTeam() {
		return teams.get(0);
	}
	
	public Team getAwayTeam() {
		return teams.get(1);
	}

	@Override
	public void setAwayTeam(Team team) {
		awayTeam = team;
	}

	@Override
	public void setHomeTeam(Team team) {
		homeTeam = team;
	}
}
