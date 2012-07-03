package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;
import util.Result;
import util.StatsPriority;

@Entity
public class League extends Model {
	
	public String name;
	
	@ManyToOne
	public Sport sport;
	
	public String displayName;
	
	@OneToMany(mappedBy="league")
	public List<Game> games;
	
	@OneToMany(mappedBy="league")
	public List<Team> teams;
	
	public League(String name, Sport sport) {
		this.sport = sport;
		this.name = name;
		this.displayName = name.replace(' ', '_').toLowerCase();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public List<Team> getTeams() {
		return this.teams;
	}
	
	public List<Game> getGames() {
		return this.games;
	}

	public void addGame(Game game) {
		if (games == null) {
			games = new ArrayList<Game>();
		}
		games.add(game);
	}
	
	public void addGames(List<Game> games) {
		if (this.games == null) {
			this.games = new ArrayList<Game>();
		}
		this.games.addAll(games);
	}
	
	public List<Game> getPlayedGames() {
		List<Game> playedGames = new ArrayList<Game>();
		for (Game game : getGames()) {
			if (game.isPlayed()) {
				playedGames.add(game);
			}
		}
 		return playedGames;
	}
	
	public List<Game> getRemainingGames() {
		List<Game> remainingGames = new ArrayList<Game>();
		for (Game game : getGames()) {
			if (!game.isPlayed()) {
				remainingGames.add(game);
			}
		}
		return remainingGames;
	}
	
	public List<Game> getGamesInChronologicalOrder() {
		if (games == null) {
			games = new ArrayList<Game>();
		}
		List<Game> sortedGames = new ArrayList<Game>(games);
		Collections.sort(sortedGames);
		
		return sortedGames;
	}
	
	public Integer getPointsForTeam(Team team) {
		Integer points = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				Result result = game.getResultFor(team);
				points += sport.getPointsFor(result);
			}
		}
		
		return points;
	}

	public Integer getWinsForTeam(Team team) {
		Integer wins = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				Result result = game.getResultFor(team);
				if (result == Result.WIN) {
					wins++;
				}
			}
		}
		
		return wins;
	}

	public Integer getLossesForTeam(Team team) {
		Integer losses = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				Result result = game.getResultFor(team);
				if (result == Result.LOSS) {
					losses++;
				}
			}
		}
		
		return losses;
	}

	public Integer getTiesForTeam(Team team) {
		Integer ties = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				Result result = game.getResultFor(team);
				if (result == Result.TIE) {
					ties++;
				}
			}
		}
		
		return ties;
	}

	public Integer getGoalsScoredByTeam(Team team) {
		Integer goals = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				goals += game.getGoalsForTeam(team);
			}
		}
		return goals;
	}

	public Integer getGoalsScoredAgainstTeam(Team team) {
		Integer goals = 0;
		
		if (!teams.contains(team)) {
			throw new IllegalArgumentException("Team not in league.");
		}
		
		for (Game game : games) {
			if (game.isPlayed() && game.teams.contains(team)) {
				goals += game.getGoalsAgainstTeam(team);
			}
		}
		return goals;
	}
	
	public List<Team> getTeamsByRank() {
		 return getTeamsByRank(sport.getStatsPriorities());
	}

	public List<Team> getTeamsByRank(List<StatsPriority> priorities) {
		List<TeamRank> teamRanks;
		List<Team> sortedTeams = new ArrayList<Team>();
		
		teamRanks = new ArrayList<TeamRank>();
		for (Team team : teams) {
			teamRanks.add(new TeamRank(team, priorities));
		}
		Collections.sort(teamRanks);
		Collections.reverse(teamRanks);

		sortedTeams = new ArrayList<Team>();
		for (TeamRank teamRank : teamRanks) {
			sortedTeams.add(teamRank.getTeam());
		}
		
		return sortedTeams;
	}
	
	public List<Game> getGamesPlayedByTeam(Team team) {
		List<Game> returnedGames = new ArrayList<Game>();
		for (Game game : games) {
			if (game.teams.contains(team)) {
				returnedGames.add(game);
			}
		}
		return returnedGames;
	}
	
	private class TeamRank implements Comparable {

		private List<StatsPriority> priorities;
		private Team team;

		public TeamRank(Team team, List<StatsPriority> priorities) {
			this.team = team;
			this.priorities = priorities;
		}
		
		public Team getTeam() {
			return team;
		}

		@Override
		public int compareTo(Object o) {
			TeamRank otherTeamRank = (TeamRank) o;
			
			if (priorities.size() == 0) {
				return 0;
			}
			
			int difference; 
			
			switch (priorities.get(0)) {
			case POINTS: {
				difference = getPointsForTeam(team).compareTo(getPointsForTeam(otherTeamRank.team));
				break;
			}
			case GOALS_SCORED: {
				Integer thisTeamGoalsScored = getGoalsScoredByTeam(team);
				Integer otherTeamGoalsScored = getGoalsScoredByTeam(otherTeamRank.team);
				
				difference = thisTeamGoalsScored.compareTo(otherTeamGoalsScored);
				break;
			}
			case GOAL_DIFFERENCE: {
				Integer thisTeamGoalDifference = getGoalsScoredByTeam(team) - getGoalsScoredAgainstTeam(team);
				Integer otherTeamGoalDifference = getGoalsScoredByTeam(otherTeamRank.team) - getGoalsScoredAgainstTeam(otherTeamRank.team);
				difference = thisTeamGoalDifference.compareTo(otherTeamGoalDifference);
				break;
			}
			case INDIVIDUAL_GAMES_BETWEEN_TEAMS: {
				List<Game> thisTeamsGames = getGamesPlayedByTeam(team);
				List<Game> otherTeamsGames = getGamesPlayedByTeam(otherTeamRank.team);
				thisTeamsGames.retainAll(otherTeamsGames);
				
				Integer thisTeamPoints = 0;
				Integer otherTeamPoints = 0;
				for (Game game : thisTeamsGames) {
					if (game.getResultFor(team) == Result.WIN) {
						thisTeamPoints += sport.getPointsForWin();
						otherTeamPoints += sport.getPointsForLoss();
					} else if (game.getResultFor(team) == Result.TIE) {
						thisTeamPoints += sport.getPointsForTie();
						otherTeamPoints += sport.getPointsForTie();
					} else {
						thisTeamPoints += sport.getPointsForLoss();
						otherTeamPoints += sport.getPointsForWin();
					}
				}
				difference = thisTeamPoints.compareTo(otherTeamPoints);
				break;
			}
			default: {
				throw new IllegalArgumentException("Can't handle this priority.");
			}
			}
			
			if (difference == 0) {
				List<StatsPriority> newPriorities = new ArrayList<StatsPriority>();
				for (int i = 1; i < priorities.size(); i++) {
					newPriorities.add(priorities.get(i));
				}
				priorities = newPriorities;
				return compareTo(otherTeamRank);
			} else {
				return difference;
			}
		}
	}
}