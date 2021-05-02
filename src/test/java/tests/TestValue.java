package tests;

import io.vavr.Tuple2;
import io.vavr.Value;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
		Value<Integer> someValue = List.of(1,2,3);
		assertTrue(someValue.corresponds(Arrays.asList(1,2,3), (x, y) -> x == y));
		assertFalse(someValue.equals(Arrays.asList(1,2,3)));
		assertTrue(someValue.eq(Arrays.asList(1,2,3)));
	}

	@Test
	void toJavaCollections() {
		Value<Integer> someValue = List.of(3,4,5);

		Object[] javaArray = someValue.toJavaArray();
		assertEquals(3, javaArray.length);
		assertEquals(3, javaArray[0]);
		assertEquals(4, javaArray[1]);
		assertEquals(5, javaArray[2]);

		java.util.Collection<Integer> javaCollection = someValue.toJavaCollection(capacity -> new ArrayList<>(capacity));
		assertEquals(3, javaCollection.size());
		assertTrue(javaCollection.containsAll(Arrays.asList(5,4,3)));

		java.util.List<Integer> javaList = someValue.toJavaList();
		assertEquals(3, javaList.size());
		assertTrue(javaList.containsAll(Arrays.asList(5,4,3)));

		java.util.Map<Integer, String> javaMap = someValue.toJavaMap((Integer element) -> new Tuple2<>(element, String.valueOf(element)));
		assertEquals(3, javaMap.size());
		assertEquals("3", javaMap.get(3));
		assertEquals("4", javaMap.get(4));
		assertEquals("5", javaMap.get(5));

		java.util.Optional<Integer> javaOptional = someValue.toJavaOptional();
		assertFalse(javaOptional.isEmpty());
		assertEquals(3, javaOptional.get()); // takes first element of the Value

		java.util.Set<Integer> javaSet = someValue.toJavaSet();
		assertEquals(3, javaSet.size());
		assertTrue(javaSet.containsAll(Arrays.asList(5,4,3)));

		java.util.stream.Stream<Integer> javaStream = someValue.toJavaStream();
		assertEquals(3, javaStream.count());
	}

	@Test
	void toControl() {
		Value<Integer> someValue = List.of(3,4,5);
		Value<Integer> emptyValue = List.of();

		Either<Exception, Integer> either = someValue.toEither(() -> new RuntimeException());
		assertTrue(either.isRight());
		assertEquals(3, either.get()); // picks first element

		Either<Exception, Integer> otherEither = emptyValue.toEither(() -> new RuntimeException());
		assertTrue(otherEither.isLeft());
		assertEquals(RuntimeException.class, otherEither.getLeft().getClass());

		Try<Integer> someTry = someValue.toTry(() -> new RuntimeException());
		assertTrue(someTry.isSuccess());
		assertEquals(3, someTry.get()); // picks first element

		Try<Integer> anotherTry = emptyValue.toTry(() -> new RuntimeException());
		assertTrue(anotherTry.isFailure());
		assertEquals(RuntimeException.class, anotherTry.getCause().getClass());

		Option<Integer> option = someValue.toOption();
		assertTrue(option.isDefined());
		assertEquals(3, option.get()); // picks first element

		Option<Integer> anotherOption = emptyValue.toOption();
		assertTrue(anotherOption.isEmpty());

		Validation<Exception, Integer> validation = someValue.toValidation(() -> new IllegalArgumentException());
		assertTrue(validation.isValid());
		assertEquals(3, validation.get());

		Validation<Exception, Integer> anotherValidation = emptyValue.toValidation(() -> new IllegalArgumentException());
		assertTrue(anotherValidation.isInvalid());
		assertEquals(IllegalArgumentException.class, anotherValidation.getError().getClass());
	}
}
