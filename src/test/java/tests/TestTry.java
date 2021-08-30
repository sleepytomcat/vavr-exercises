package tests;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TestEither {
	@Test
	void creation() {
		Either<RuntimeException, String> someEither = Either.right("hello");
		assertTrue(someEither.isRight());
		assertEquals("hello", someEither.get());

		Either<Integer, String> someOtherEither = Either.left(1);
		assertTrue(someOtherEither.isLeft());
		assertEquals(1, someOtherEither.getLeft());

		Either<RuntimeException, String> otherEither = Either.left(new ArithmeticException());
		assertTrue(otherEither.isLeft());
		assertTrue(otherEither.isEmpty());
	}

	@Test
	void fromTry() {
		Either<Throwable, Integer> someEither = Try.of(() -> 1/0).toEither();
		assertTrue(someEither.isLeft());
		assertEquals(ArithmeticException.class, someEither.getLeft().getClass());
	}

	@Test
	void filtering() {
		Either<String, Integer> someEither = Either.right(3);

		Either<String, Integer> filteredEither = someEither.filterOrElse(x -> x > 10, x -> "value was " + x.toString());
		assertTrue(filteredEither.isLeft());
		assertEquals("value was 3", filteredEither.getLeft());

		Option<Either<String, Integer>> maybeFilteredEither = someEither.filter(x -> x > 10);
		assertTrue(maybeFilteredEither.isEmpty());

		Option<Either<String, Integer>> maybeOtherFilteredEither = someEither.filter(x -> x > 1);
		assertTrue(maybeOtherFilteredEither.isDefined());
		assertTrue(maybeOtherFilteredEither.get().isRight());
		assertEquals(3, maybeOtherFilteredEither.get().get());

		Either<String, Integer> otherEither = Either.left("no number");
		Option<Either<String, Integer>> maybeAnotherFilteredEither = otherEither.filter(x -> x > 10);
		assertTrue(maybeAnotherFilteredEither.isDefined());
		assertTrue(maybeAnotherFilteredEither.get().isLeft());
		assertEquals("no number", maybeAnotherFilteredEither.get().getLeft());
	}

	@Test
	void get() {
		Either<String, Integer> someEither = Either.left("not a number");

		assertNull(someEither.getOrNull());
		assertThrows(NoSuchElementException.class, () -> someEither.get());

		assertEquals(123, someEither.getOrElse(123));
		assertEquals(456, someEither.getOrElse(() -> 456));
		assertEquals("not a number".length(), someEither.getOrElseGet(str -> str.length()));
		assertThrows(RuntimeException.class, () -> someEither.getOrElseThrow(str -> new RuntimeException(str)));
		assertEquals(456, someEither.getOrElseTry(() -> 456));

		assertThrows(ArithmeticException.class, () -> someEither.getOrElse(() -> {throw new ArithmeticException();})); // unchecked only
		assertThrows(Exception.class, () -> someEither.getOrElseTry(() -> {throw new Exception();})); // allows for checked exceptions
	}

	@Test
	void mapping() {
		Either<String, Integer> leftEither = Either.left("not a number");

		Either<Integer, Integer> mappedLeftEither = leftEither.mapLeft(str -> str.length());
		assertTrue(mappedLeftEither.isLeft());
		assertEquals("not a number".length(), mappedLeftEither.getLeft());

		Either<Throwable, Double> otherEither = leftEither.bimap(left -> new RuntimeException(left), num -> Double.valueOf(num));
		assertTrue(otherEither.isLeft());
		assertEquals(RuntimeException.class, otherEither.getLeft().getClass());

		Either<String, Integer> rightEither = Either.right(3);
		Either<String, Integer> mappedRightEither = rightEither.map(x -> x + 1);
		assertTrue(mappedRightEither.isRight());
		assertEquals(3+1, mappedRightEither.get());
	}
}
