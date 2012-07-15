package util;

import java.util.Comparator;
import java.util.List;

public class ChainedComparator<T> implements Comparator {

	private List<Comparator<T>> comparators;
	
	public ChainedComparator(List<Comparator<T>> comparators) {
		this.comparators = comparators;
	}
	
	@Override
	public int compare(Object o1, Object o2) {
		int result;
		
		for (Comparator comparator : comparators) {
			result = comparator.compare(o1, o2);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}
}
