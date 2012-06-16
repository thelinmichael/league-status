package controllers;

import java.util.Arrays;
import java.util.List;

import models.League;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
    	render();
    }
    
    public static void league(String leagueName) {
    	League league = League.find("byName", leagueName).first();
    	
    	if (league == null) {
    		flash.put("errorMessage", "League not found");
    	}
    	
    	render(league);
    }
}