package tests;

import io.vavr.collection.Array;
import io.vavr.collection.Traversable;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import io.vavr.PartialFunction;

class TestTraversable {
	static final Traversable<Character> SOME_CHARACTERS = Array.of('a', 'b', 'c', '$');
	static final Traversable<Character> ONE_CHARACTER = Array.of('a');
	static final Traversable<Character> NONE_CHARACTERS = Array.empty();

	@Test
	void basicOperations() {
		// collect(PartialFunction)
		// Value.contains(Object)
		// containsAll(Iterable)
		// length()
		PartialFunction<Character, Character> uppercase = new PartialFunction<>() {
			@Override
			public Character apply(Character x) {
				return (char) (x - 'a' + 'A');
			}

			@Override
			public boolean isDefinedAt(Character x) {
				return x >= 'a' && x <= 'z';
			}
		};
		Traversable<Character> inversedNumbers = SOME_CHARACTERS.collect(uppercase);
		assertEquals(3, inversedNumbers.length());
		assertTrue(inversedNumbers.contains('A'));
		assertTrue(inversedNumbers.contains('B'));
		assertTrue(inversedNumbers.contains('C'));
		assertTrue(inversedNumbers.containsAll(Arrays.asList('A', 'B', 'C')));

		// head()
		assertEquals('a', SOME_CHARACTERS.head());
		assertThrows(NoSuchElementException.class, () -> NONE_CHARACTERS.head());

		// headOption()
		assertTrue(SOME_CHARACTERS.headOption().isDefined());
		assertEquals('a', SOME_CHARACTERS.headOption().get());
		assertTrue(NONE_CHARACTERS.headOption().isEmpty());

		// init()
		assertEquals(SOME_CHARACTERS.init(), Array.of('a', 'b', 'c'));
		assertEquals(ONE_CHARACTER.init(), Array.empty());
		assertThrows(UnsupportedOperationException.class, () -> NONE_CHARACTERS.init());

		// initOption()
		assertTrue(SOME_CHARACTERS.initOption().isDefined());
		assertEquals(SOME_CHARACTERS.initOption().get(), Array.of('a', 'b', 'c'));
		assertTrue(ONE_CHARACTER.initOption().isDefined());
		assertTrue(ONE_CHARACTER.initOption().get().isEmpty());
		assertTrue(NONE_CHARACTERS.initOption().isEmpty());

		// isEmpty()
		assertFalse(SOME_CHARACTERS.isEmpty());
		assertTrue(NONE_CHARACTERS.isEmpty());

		// last()
		assertEquals('$', SOME_CHARACTERS.last());
		assertThrows(NoSuchElementException.class, () -> NONE_CHARACTERS.last());

		// lastOption()
		assertTrue(SOME_CHARACTERS.lastOption().isDefined());
		assertEquals('$', SOME_CHARACTERS.lastOption().get());
		assertTrue(NONE_CHARACTERS.lastOption().isEmpty());


		/*
		size()
		tail()
		tailOption()
		*/
	}

	@Test
	void iteration() {
		/*
		forEachWithIndex(ObjIntConsumer)
		grouped(int)
		iterator()
		slideBy(Function)
		sliding(int)
		sliding(int, int)
		*/
	}

	@Test
	void numericOperations() {
		/*
		average()
		max()
		maxBy(Comparator)
		maxBy(Function)
		min()
		minBy(Comparator)
		minBy(Function)
		product()
		sum()
		 */
	}

	@Test
	void reductionFolding() {
		/*
		count(Predicate)
		fold(Object, BiFunction)
		foldLeft(Object, BiFunction)
		foldRight(Object, BiFunction)
		mkString()
		mkString(CharSequence)
		mkString(CharSequence, CharSequence, CharSequence)
		reduce(BiFunction)
		reduceOption(BiFunction)
		reduceLeft(BiFunction)
		reduceLeftOption(BiFunction)
		reduceRight(BiFunction)
		reduceRightOption(BiFunction)
		 */
	}

	@Test
	void selection() {
		/*
		drop(int)
		dropRight(int)
		dropUntil(Predicate)
		dropWhile(Predicate)
		filter(Predicate)
		filterNot(Predicate)
		find(Predicate)
		findLast(Predicate)
		groupBy(Function)
		partition(Predicate)
		retainAll(Iterable)
		take(int)
		takeRight(int)
		takeUntil(Predicate)
		takeWhile(Predicate)
		*/
	}

	@Test
	void tests() {
		/*
		existsUnique(Predicate)
		hasDefiniteSize()
		isDistinct()
		isOrdered()
		isSequential()
		isTraversableAgain()
		 */
	}

	@Test
	void transformation() {
		/*
		distinct()
		distinctBy(Comparator)
		distinctBy(Function)
		flatMap(Function)
		map(Function)
		replace(Object, Object)
		replaceAll(Object, Object)
		scan(Object, BiFunction)
		scanLeft(Object, BiFunction)
		scanRight(Object, BiFunction)
		span(Predicate)
		unzip(Function)
		unzip3(Function)
		zip(Iterable)
		zipAll(Iterable, Object, Object)
		zipWithIndex()
		 */
	}
}