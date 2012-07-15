package models;

import play.test.UnitTest;

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
}