package tests;

import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestOption {
	@Test
	void creation() {
		Option<String> maybe1 = Option.some("text");
		assertEquals(maybe1.get(), "text");

		Option<String> maybe2 = Option.some(null); // Option.some(null) is valid in Vavr
		assertFalse(maybe2.isEmpty());
		assertEquals(maybe2.get(), null);

		Option<String> maybe3 = Option.none();
		assertTrue(maybe3.isEmpty());

		Option<String> maybe4 = Option.of("text"); // returns Option.some("text")
		assertEquals(maybe4.get(), "text");

		Option<String> maybe5 = Option.of(null); // returns Option.none(); NPE-safe
		assertTrue(maybe5.isEmpty());
	}

	@Test
	void filter() {
		assertEquals(Option.of(1).filter(x -> x > 0).get(), 1); // filter yields Option.some(1)
		assertTrue(Option.of(1).filter(x -> x > 10).isEmpty()); // filter yields Option.none()
	}

	@Test
	void equals() {
		Option<String> maybeText1 = Option.of("he" + "llo");
		Option<String> maybeText2 = Option.of("hel" + "lo"); // to ensure the strings are not interned
		assertTrue(maybeText1.equals(maybeText2));
	}

	@Test
	void flatMap() {
		Option<Integer> wordLength = Option.of("text").flatMap(s -> Option.of(s.length()));

		assertFalse(wordLength.isEmpty());
		assertEquals(wordLength.get(), "text".length());
	}

	@Test
	void map() {
		Option<Integer> wordLength = Option.of("text").map(String::length);

		assertFalse(wordLength.isEmpty());
		assertEquals(wordLength.get(), "text".length());

		// Option.some(null) is valid in Vavr, which may result in unexpected behavior of map()
		Option<Integer> noWordLength = Option.of("text")
				// next line results in Option.some(null) which is valid in Vavr, may result in NPE down the road
				// .map(s -> (String)null)
				.flatMap(s -> Option.<String>of(null)) // this results in Option.none() which is NPE-safe
				.map(String::length);

		assertTrue(noWordLength.isEmpty());
	}

	@Test
	void getOrElse() {
		assertEquals(Option.none().getOrElse(3), Integer.valueOf(3));
		assertEquals(Option.none().getOrElse(() -> 3), Integer.valueOf(3));
	}
}
