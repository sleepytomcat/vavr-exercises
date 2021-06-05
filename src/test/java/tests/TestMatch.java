package tests;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Success;
import static io.vavr.Patterns.$Tuple2;
import static io.vavr.Predicates.*;
import static org.junit.jupiter.api.Assertions.*;

class TestMatch {
	@Test
	void simpleMatch() {
		Function<Integer, String> fizzBuzzFn = number -> Match(number).of(
				Case($(x -> x % 15 == 0), "fizz-buzz"),
				Case($(x -> x % 3 == 0), "fizz"),
				Case($(x -> x % 5 == 0), "buzz"),
				Case($(), String::valueOf)
		);

		assertEquals(fizzBuzzFn.apply(1), "1");
		assertEquals(fizzBuzzFn.apply(6), "fizz");
		assertEquals(fizzBuzzFn.apply(10), "buzz");
		assertEquals(fizzBuzzFn.apply(30), "fizz-buzz");

		Object something = "hello";
		String whatIsIt = Match(something).of(
				Case($(isNull()), "nothing"),
				Case($(instanceOf(String.class)), str -> '[' + str + ']'),
				Case($(instanceOf(Throwable.class)), ex -> "failure"),
				Case($(), "something unknown")
		);

		assertEquals(whatIsIt, "[hello]");
	}

	@Test
	void matchWithPredicates() {
		String whatIsIt = Match((Object)"hello").of(
				Case($(isNull()), "nothing"),
				Case($(instanceOf(String.class)), str -> '[' + str + ']'),
				Case($(instanceOf(Throwable.class)), "exception"),
				Case($(), "something unknown")
		);
		assertEquals(whatIsIt, "[hello]");

		Function<Integer, Option<String>> nowManyFn = count -> Match(count).option(
				Case($(x -> x < 0), "negative amount"),
				Case($(x -> x == 0), "none"),
				Case($(x -> x < 4), "a few"),
				Case($(isIn(4, 5, 6)), "several")
		);
		assertTrue(nowManyFn.apply(5).isDefined());
		assertEquals(nowManyFn.apply(5), Some("several"));
		assertFalse(nowManyFn.apply(1000).isDefined());
	}

	@Test
	void matchWithPatterns() {
		Function<Try<Tuple2<String, Integer>>, String> tryFn = trySomething -> Match(trySomething).of(
				Case($Success($Tuple2($(isIn("hello", "world")), $())), tuple2 -> String.valueOf(tuple2._2)),
				Case($Success($Tuple2($(), $(42))), tuple2 -> tuple2._1),
				Case($(), "oops")
		);

		Try<Tuple2<String, Integer>> try1 = Try.of(() -> Tuple.of("answer", 42));
		assertEquals(tryFn.apply(try1), "answer");

		Try<Tuple2<String, Integer>> try2 = Try.of(() -> Tuple.of("hello", 7));
		assertEquals(tryFn.apply(try2), "7");
	}
}
