package tests;

import io.vavr.PartialFunction;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.util.*;

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

		// Option.some(null) is valid in Vavr, which may result in unexpected behavior of map() compared to java.lang.Optional
		Option<Integer> noWordLength = Option.of("text")
				// next line results in Option.some(null) which is valid in Vavr, may result in NPE down the road
				// .map(s -> (String)null)
				.flatMap(s -> Option.<String>of(null)) // this results in Option.none() which is NPE-safe
				.map(String::length);

		assertTrue(noWordLength.isEmpty());
	}

	@Test
	void get() {
		assertEquals(Option.some(3).get(), Integer.valueOf(3));
		assertThrows(NoSuchElementException.class, () -> Option.none().get());
	}

	@Test
	void getOrElse() {
		assertEquals(Option.none().getOrElse(3), Integer.valueOf(3));
		assertEquals(Option.none().getOrElse(() -> 3), Integer.valueOf(3));
		assertThrows(IllegalArgumentException.class, () -> Option.none().getOrElseThrow(() -> new IllegalArgumentException()));
	}

	@Test
	void fold() {
		int a = Option.<Integer>none()
				.fold(() -> 5, x -> x * 2);

		int b = Option.some(3)
				.fold(() -> 5, x -> x * 2);

		assertEquals(a, 5);
		assertEquals(b, 6);
	}

	@Test
	void peek() {
		List<Integer> list = new ArrayList<>();
		Option.some(3).peek(list::add);
		assertEquals(list.size(), 1);
	}

	@Test
	void onEmpty() {
		List<Integer> list = new ArrayList<>();
		Option.none().onEmpty(() -> list.add(0));
		assertEquals(list.size(), 1);
	}

	@Test
	void orElse() {
		Option<String> noText = Option.none();
		Option<String> alternative = Option.some("hello");
		Option<String> result = noText.orElse(alternative);
		assertEquals(result.get(), alternative.get());

		Option<String> resultSupplied = noText.orElse(() -> alternative);
		assertEquals(resultSupplied.get(), alternative.get());
	}

	@Test
	void when() {
		Option<String> text = Option.when(true, "hello");
		Option<String> textSupplied = Option.when(true, () -> "hello");
		Option<String> noText = Option.when(false, "hello");

		assertTrue(noText.isEmpty());
		assertEquals(text.get(), "hello");
		assertEquals(textSupplied.get(), "hello");
	}

	@Test
	void ofOptional() {
		Optional<String> javaOptionalText = Optional.of("hello");
		Option<String> text = Option.ofOptional(javaOptionalText);
		assertEquals(text.get(), javaOptionalText.get());
	}

	@Test
	void transform() {
		Option<String> text = Option.of("hello");
		int length = text.transform(x -> x.get().length());
		assertEquals(length, "hello".length());
	}

	@Test
	void collect() {
		PartialFunction<Integer, Integer> undefinedFor0 = new PartialFunction<>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Integer apply(Integer t) {
				return t + 1; // fix: https://github.com/vavr-io/vavr/pull/2580
			}

			@Override
			public boolean isDefinedAt(Integer value) {
				return value != 0;
			}
		};

		Option<Integer> zero = Option.some(0);
		assertTrue(zero.collect(undefinedFor0).isEmpty());
	}

	@Test
	void sequence() {
		Collection<Option<String>> maybeWords = Arrays.asList(
				Option.of("hello"),
				Option.of("world")
		);

		Option<Seq<String>> wordsSequence = Option.sequence(maybeWords);

		assertEquals(maybeWords.size(), wordsSequence.get().size());
	}

	@Test
	void traverse() {
		Collection<String> maybeWords = Arrays.asList(
				"hello",
				"world"
		);

		Option<Seq<Integer>> wordsSequence = Option.traverse(maybeWords, str -> Option.some(str.length()));

		assertEquals(maybeWords.size(), wordsSequence.get().size());
	}
}
