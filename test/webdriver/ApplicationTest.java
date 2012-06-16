package webdriver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import play.test.Fixtures;
import play.test.FunctionalTest;

public class ApplicationTest extends FunctionalTest {
	
	@Test
	public void badLeagueNameShowsErrorMessage() {
		WebDriver driver = new FirefoxDriver();
		driver.get("localhost:9000/league/bogusLeague");
		
		WebElement element = driver.findElement(By.className("errorMessage"));
		assertThat(element, is(notNullValue()));
		
		driver.close();
	}
	
	@Test
	public void showsLeagueTeamsInList() {
		WebDriver driver = new FirefoxDriver();
		driver.get("localhost:9000/league/allsvenskan");
		
		List<WebElement> teams = driver.findElements(By.className("team"));
		assertThat(teams.size(), is(5));
		
		driver.close();
	}
}
