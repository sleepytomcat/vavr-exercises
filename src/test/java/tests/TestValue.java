package tests;

import io.vavr.Value;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestValue {
	@Test
	void valueIsIterable() {
		Value<Integer> someValue = List.of(1);

		// for
		for (Integer element: someValue) {
			assertEquals(1, element);
		}

		// .iterator()
		java.util.Iterator<Integer> valueIterator = someValue.iterator();
		while (valueIterator.hasNext()) {
			Integer number = valueIterator.next();
			assertEquals(1, number);
		}

		// .forEach()
		someValue.forEach(
				element -> assertEquals(1, element)
		);
	}
}
