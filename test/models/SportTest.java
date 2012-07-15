package models;

import play.test.UnitTest;
import util.Result;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class SportTest extends UnitTest {

	@Test
	public void canGetNumberOfPointsForWinLossAndTie() {
		Sport football = new Football();
		
		assertThat(football.getPointsForWin(), is(3));
		assertThat(football.getPointsForTie(), is(1));
		assertThat(football.getPointsForLoss(), is(0));
	}
	
	@Test
	public void canGetPointsForResult() {
		Sport football = new Football();
		
		
		assertThat(football.getPointsFor(Result.WIN), is(3));
		assertThat(football.getPointsFor(Result.LOSS), is(0));
		assertThat(football.getPointsFor(Result.TIE), is(1));
		try {
			football.getPointsFor(Result.UNDECIDED); 
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
}