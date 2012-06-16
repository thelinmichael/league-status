package jobs;

import java.util.List;

import models.League;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class StartUp extends Job {
	
	public void doJob() {
		if (Play.configuration.getProperty("application.mode").equals("dev")) {
			System.out.println("Loading fixtures...");
			Fixtures.deleteAllModels();
			Fixtures.loadModels("/mock/test_league.yml");
			
			List<League> leagues = League.findAll();
			System.out.println("Loaded " + leagues.size() + " leagues from fixtures.");
		} 
	}
	
}
