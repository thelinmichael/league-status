package controllers;

import java.util.List;

import models.League;
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
}