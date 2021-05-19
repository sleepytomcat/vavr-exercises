package tests;

import io.vavr.Tuple2;
import io.vavr.Value;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestStreams {
	@Test
	void creation() {
		// Stream.empty()
		Stream<Integer> emptyStream = Stream.empty();
		assertTrue(emptyStream.isEmpty());

		// Stream.of(x)
		Stream<String> oneElementStream = Stream.of("hello"); // single element
		assertEquals(Array.of("hello"), oneElementStream.toArray());

		// Stream.of(Object...)
		Stream<Object> objectsStream = Stream.of("hello", Integer.valueOf(3)); // Object...
		assertEquals(Array.of("hello", 3), objectsStream.toArray());

		// Stream.ofAll(Iterable)
		Stream<Integer> streamOfIterable = Stream.ofAll(Array.of(2,3));
		assertEquals(Array.of(2, 3), streamOfIterable.toArray());

		// Stream.ofAll(<primitive array>)
		int[] arrayOfNumbers = {2, 3};
		Stream<Integer> streamOfArray = Stream.ofAll(arrayOfNumbers); // Java array
		assertEquals(Array.of(2, 3), streamOfArray.toArray());

		// int sequence: from(startIntegerValue)
		Stream<Integer> streamFrom = Stream.from(0);
		assertFalse(streamFrom.hasDefiniteSize());
		assertEquals(Array.of(0,1,2,3), streamFrom.take(4));

		// int sequence: range(0, 3)
		Stream<Integer> rangeStream = Stream.range(0, 3);
		assertEquals(Array.of(0, 1, 2), rangeStream.toArray()); // does not include upper bound

		// int sequence: rangeClosed()
		Stream<Integer> closedRangeStream = Stream.rangeClosed(0, 3);
		assertEquals(Array.of(0, 1, 2, 3), closedRangeStream.toArray()); // includes upper bound

		// generator: cons(Object, Supplier)
		Stream<Integer> randomStream = Stream.cons(3, () -> Stream.of(4, 5, 6, 7));
		assertEquals(Array.of(3, 4, 5 , 6, 7), randomStream.toArray());

		// generator: continually(Supplier)
		Stream<Double> randomNumbersStream = Stream.continually(Math::random);
		assertFalse(randomNumbersStream.hasDefiniteSize());

		// generator: iterate
		Stream<String> stringStream = Stream.iterate("a", str -> str + "a");
		assertEquals(Array.of("a", "aa", "aaa"), stringStream.take(3).toArray());
	}
}
