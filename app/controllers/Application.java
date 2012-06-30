package controllers;

import java.util.List;

import models.League;
import play.mvc.Controller;
import util.LeagueStats;
import util.Stats;

public class Application extends Controller {

    public static void index() {
    	List<League> leagues = League.findAll();
    	render(leagues);
    }
    
    public static void league(String leagueName) {
    	League league = League.find("byName", leagueName).first();
    	notFoundIfNull(league);
    	LeagueStats stats = Stats.getLeagueStats(league);
    	render(league, stats);
    }
}