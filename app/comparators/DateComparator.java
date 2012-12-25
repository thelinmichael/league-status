package comparators;

import java.util.Comparator;

import util.HasTime;

import models.Game;

public class DateComparator implements Comparator<HasTime> {

	@Override
	public int compare(HasTime o1, HasTime o2) {
		if (o1.getTime() == null && o2.getTime()== null) {
			return 0;
		} else if (o1.getTime() == null && o2.getTime() != null) {
			return -1;
		} else if (o1.getTime() != null && o2.getTime() == null) {
			return 1;
		} else {
			return o1.getTime().compareTo(o2.getTime());
		}
	}
}
