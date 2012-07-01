package controllers;

import java.util.List;

import models.League;
import models.Team;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
    	List<League> leagues = League.findAll();
    	render(leagues);
    }
    
    public static void league(String leagueName) {
    	League league = League.find("byName", leagueName).first();
    	notFoundIfNull(league);
    	render(league);
    }
    
    public static void team(String teamName) {
    	Team team = Team.find("byName", teamName).first();
    	notFoundIfNull(team);
    	render(team);
    }
}