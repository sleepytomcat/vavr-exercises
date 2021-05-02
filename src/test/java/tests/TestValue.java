package tests;

import io.vavr.Value;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

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

	@Test
	void get() {
		Value<Integer> someValue = List.of(1);
		assertFalse(someValue.isSingleValued()); // because this is a list
		assertEquals(1, someValue.get());

		Value<Integer> someOtherValue = List.empty();
		assertTrue(someOtherValue.isEmpty());
		assertFalse(someOtherValue.contains(1));
		assertFalse(someOtherValue.exists(element -> element == 1));

		assertEquals(1, someOtherValue.getOrElse(1));
		assertEquals(1, someOtherValue.getOrElse(() -> 1));
		assertEquals(null, someOtherValue.getOrNull());
		assertThrows(RuntimeException.class, () -> someOtherValue.getOrElseThrow(() -> new RuntimeException()));
		assertEquals(1, someOtherValue.getOrElseTry(() -> 1));
	}

	@Test
	void collect() {
		Value<Integer> someValue = List.of(1,2,3);
		assertEquals(6, someValue.collect(Collectors.summingInt(x -> x)));
	}

	@Test
	void correspondsAndEquals() {
		assertTrue(List.of(1,2,3).corresponds(Arrays.asList(1,2,3), (x, y) -> x == y));

		assertFalse(List.of(1,2,3).equals(Arrays.asList(1,2,3)));
		assertTrue(List.of(1,2,3).eq(Arrays.asList(1,2,3)));
	}
}
