package tests;

import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TestOption {
	@Test
	public void optionCreation() {
		Option<Integer> maybe1 = Option.of(1);
		Option<Integer> maybe2 = Option.some(2);
		Option<String> maybeString = Option.of("some text");
		assertEquals(maybe1.get(), Integer.valueOf(1));
		assertEquals(maybe2.get(), Integer.valueOf(2));
		assertEquals(maybeString.get(), "some text");

		Option<String> maybeNull = Option.of(null); // valid in vavr; yields Option.none()
		Option<String> maybeOtherNull = Option.none(); // same as Option.of(null)
		assertTrue(maybeNull.isEmpty());
		assertTrue(maybeOtherNull.isEmpty());
		// this will throw NoSuchElementException as maybeNull is Option.none()
		assertThrows(NoSuchElementException.class, () -> maybeNull.get());
	}

	@Test
	public void optionNone() {
		assertTrue(Option.none().isEmpty()); // filter yields Option.none()
		assertEquals(Option.none().getOrElse(3), Integer.valueOf(3));
		assertEquals(Option.none().getOrElse(() -> 3), Integer.valueOf(3));
		assertTrue(Option.of(1).filter(x -> x > 10).isEmpty()); // filter yields Option.none()
	}

	@Test
	public void optionTransformations() {
		Integer num = Option.of("hello")
				.map(s -> s.length())
				.map(l -> l * 2)
				.getOrElse(-1);
		assertEquals(num, Integer.valueOf("hello".length() * 2));

		Integer otherNum = Option.<String>of(null) // same as Option.none()
				.map(s -> s.length())
				.map(l -> l * 2)
				.getOrElse(-1); // yields -1 because value is Option.none()
		assertEquals(otherNum, Integer.valueOf(-1));

		String greeting = Option.of("hello")
				.flatMap(s -> Option.of(s + "!"))
				.getOrElse("");
		assertEquals(greeting, "hello!");
	}

	@Test
	public void optionComparison() {
		Option<String> maybeText1 = Option.of("he" + "llo");
		Option<String> maybeText2 = Option.of("hel" + "lo"); // to ensure the strings are not interned
		assertTrue(maybeText1.equals(maybeText2));
	}

	@Test
	public void someOfNullControversy() {
		assertThrows(NullPointerException.class, () -> helperMethodWrong());
	}

	static Integer helperMethodWrong() {
		return Option.of("hello")
				.map(str -> (String) null) // results in Option.some(null)
				.map(str -> str.length()) // NullPointerException, as str is null
				.getOrElse(-1);
	}

	@Test
	public void handlingNullRightWay() {
		assertEquals(helperMethodRight(), Integer.valueOf(-1));
	}

	static Integer helperMethodRight() {
		return Option.of("hello")
				.map(str -> (String) null) // results in Option.some(null)
				.flatMap(str -> Option.of(str)) // str is null; Option.of(null) yields Option.none()
				.map(str -> str.length()) // no exception, valid call for Option.none()
				.getOrElse(-1);
	}
}
