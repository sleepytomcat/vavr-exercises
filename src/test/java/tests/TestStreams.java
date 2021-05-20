package tests;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

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

		// int sequence: Stream.from(startIntegerValue)
		Stream<Integer> streamFrom = Stream.from(0);
		assertFalse(streamFrom.hasDefiniteSize());
		assertEquals(Array.of(0,1,2,3), streamFrom.take(4));

		// int sequence: Stream.range(from, toExclusive)
		Stream<Integer> rangeStream = Stream.range(0, 3);
		assertEquals(Array.of(0, 1, 2), rangeStream.toArray()); // does not include upper bound

		// int sequence: Stream.rangeClosed(from, toInclusive)
		Stream<Integer> closedRangeStream = Stream.rangeClosed(0, 3);
		assertEquals(Array.of(0, 1, 2, 3), closedRangeStream.toArray()); // includes upper bound

		// Stream.fill(n, element)
		Stream<String> fillStream = Stream.fill(3, "a");
		assertEquals(Array.of("a", "a", "a"), fillStream.toArray());

		// generator: Stream.cons(Object, Supplier)
		Stream<Integer> randomStream = Stream.cons(3, () -> Stream.of(4, 5, 6, 7));
		assertEquals(Array.of(3, 4, 5 , 6, 7), randomStream.toArray());

		// generator: Stream.continually(Supplier)
		Stream<Double> randomNumbersStream = Stream.continually(Math::random);
		assertFalse(randomNumbersStream.hasDefiniteSize());

		// generator: Stream.iterate(seed, nextGenerator)
		Stream<String> stringStream = Stream.iterate("a", str -> str + "a");
		assertEquals(Array.of("a", "aa", "aaa"), stringStream.take(3).toArray());

		// generator: unfold
		Stream<String> unfoldedStream = Stream.unfoldRight(0, index -> index > 3
						? Option.none()
						: Option.some(Tuple.of(String.valueOf(index), index + 1))
		);
		assertEquals(Array.of("0", "1", "2", "3"), unfoldedStream.toArray());
	}

	@Test
	void manipulations() {
		// appendAll
		Stream<Integer> someNumbers = Stream.rangeClosed(0,1);
		Stream<Integer> infiniteNumbers = Stream.from(2);
		Stream<Integer> lotsOfNumbers = someNumbers.appendAll(infiniteNumbers);
		assertEquals(Array.of(0,1,2,3), lotsOfNumbers.take(4).toArray());

		// concat
		Stream<Integer> fewNumbers = Stream.of(1,2);
		Stream<Integer> fewMoreNumers = Stream.of(3,4);
		Stream<Integer> moreNumbers = Stream.concat(fewNumbers, fewMoreNumers);
		assertEquals(Array.of(1,2,3,4), moreNumbers.toArray());

		// replace
		assertEquals(Array.of(1,0,2,2), Stream.of(1,2,2,2).replace(2, 0).toArray());

		// zip
		Stream<String> strings = Stream.of("one", "two");
		Stream<Integer> numbers = Stream.of(1, 2);
		Stream<Tuple2<Integer, String>> zipped = numbers.zip(strings);
		assertEquals(Array.of(Tuple.of(1, "one"), Tuple.of(2, "two")), zipped.toArray());

		// unzip
		class Person {
			public Person(String firstName, String lastName) {this.firstName = firstName; this.lastName = lastName;}
			public String firstName;
			public String lastName;
		}
		Stream<Person> persons = Stream.of(new Person("Ivan", "Petrov"), new Person("Petr", "Ivanov"));
		Tuple2<Stream<String>, Stream<String>> unzipped = persons.unzip(person -> Tuple.of(person.firstName, person.lastName));
		assertEquals(Array.of("Ivan", "Petr"), unzipped._1().toArray());
		assertEquals(Array.of("Petrov", "Ivanov"), unzipped._2().toArray());
	}
}
