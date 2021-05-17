package tests;

import io.vavr.PartialFunction;
import io.vavr.collection.Array;
import io.vavr.collection.Iterator;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

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

		// tail()
		assertEquals(SOME_CHARACTERS.tail(), Array.of('b', 'c', '$'));
		assertEquals(ONE_CHARACTER.tail(), Array.empty());
		assertThrows(UnsupportedOperationException.class, () -> NONE_CHARACTERS.tail());

		// tailOption()
		assertTrue(SOME_CHARACTERS.tailOption().isDefined());
		assertEquals(SOME_CHARACTERS.tailOption().get(), Array.of('b', 'c', '$'));
		assertTrue(ONE_CHARACTER.tailOption().isDefined());
		assertTrue(ONE_CHARACTER.tailOption().get().isEmpty());
		assertTrue(NONE_CHARACTERS.tailOption().isEmpty());

		// size()
		assertTrue(SOME_CHARACTERS.size() > 0);
		assertEquals(0, NONE_CHARACTERS.size());
	}

	@Test
	void iteration() {
		// forEachWithIndex(ObjIntConsumer)
		ONE_CHARACTER.forEachWithIndex((character, index) -> {
			assertEquals(0, index);
			assertEquals('a', character);
		});

		// grouped(int): grouping Traversable<> into blocks of given size.
		Iterator<? extends Traversable<Character>> iterator = SOME_CHARACTERS.grouped(3);
		Traversable<Character> firstBlock = iterator.get();
		assertEquals(3, firstBlock.size());
		assertEquals(Array.of('a','b','c'), firstBlock); // three elements in first block
		Traversable<Character> secondBlock = iterator.next();
		assertEquals(1, secondBlock.size()); // only one element in the last block
		assertEquals(Array.of('$'), secondBlock);

		// iterator()
		Iterator<Character> characterIterator = SOME_CHARACTERS.iterator();
		assertTrue(characterIterator.hasNext());
		assertEquals('a', characterIterator.next());
		assertEquals('b', characterIterator.next());
		assertEquals('c', characterIterator.next());
		assertEquals('$', characterIterator.next());
		assertFalse(characterIterator.hasNext());

		// slideBy(Function): grouping Traversable<> into blocks according to classifier function.
		// Next element will be assigned to the same group ('sliding window') as previous when classifier
		// returns same value for both.
		Traversable<Integer> numbers = Array.of(1,2,4,10,11,3,4);
		Iterator<? extends Traversable<Integer>> numbersIterator = numbers.slideBy(x -> x/10);
		Traversable<Integer> firstNumbersSlidingWindow = numbersIterator.next();
		assertEquals(Array.of(1,2,4), firstNumbersSlidingWindow);
		Traversable<Integer> secondNumbersSlidingWindow = numbersIterator.next();
		assertEquals(Array.of(10,11), secondNumbersSlidingWindow);
		Traversable<Integer> thirdNumbersSlidingWindow = numbersIterator.next();
		assertEquals(Array.of(3,4), thirdNumbersSlidingWindow);
		assertFalse(numbersIterator.hasNext());

		// sliding(int, int)
		Iterator<? extends Traversable<Character>> slidingIterator = SOME_CHARACTERS.sliding(2, 3);
		Traversable<Character> firstSlidingWindow = slidingIterator.next();
		assertEquals(Array.of('a', 'b'), firstSlidingWindow);
		Traversable<Character> secondSlidingWindow = slidingIterator.next();
		assertEquals(Array.of('$'), secondSlidingWindow);
		assertFalse(slidingIterator.hasNext());

		// sliding(int): same as sliding(int, 1)
		Iterator<? extends Traversable<Character>> slidingByOneIterator = SOME_CHARACTERS.sliding(3);
		Traversable<Character> firstSlidingByOneWindow = slidingByOneIterator.next();
		assertEquals(Array.of('a', 'b', 'c'), firstSlidingByOneWindow);
		Traversable<Character> secondSlidingByOneWindow = slidingByOneIterator.next();
		assertEquals(Array.of('b', 'c', '$'), secondSlidingByOneWindow);
		assertFalse(slidingByOneIterator.hasNext());
	}

	@Test
	void numericOperations() {
		final Traversable<Double> SOME_NUMBERS = Array.of(0.0, -3.0, 2.0, 0.0);
		final Traversable<Double> NONE_NUMBERS = Array.of();

		// average()
		assertEquals(Option.some(-0.25), SOME_NUMBERS.average());
		assertEquals(Option.none(), NONE_NUMBERS.average());
		// max()
		assertEquals(Option.some(2.0), SOME_NUMBERS.max());
		assertEquals(Option.none(), NONE_NUMBERS.max());
		// maxBy(Comparator)
		Comparator<Double> compareByAbsoluteValue = (x, y) -> Math.abs(x) > Math.abs(y) ? 1 : x == y ? 0 : -1;
		assertEquals(Option.some(-3.0), SOME_NUMBERS.maxBy(compareByAbsoluteValue));
		assertEquals(Option.none(), NONE_NUMBERS.maxBy(compareByAbsoluteValue));
		// maxBy(Function)
		Function<Double, String> mapToString = x -> String.valueOf(x);
		assertEquals(Option.some(2.0), SOME_NUMBERS.maxBy(mapToString)); // numbers are mapped to "0.0", "-3.0", "2.0"... and then strings are compared
		assertEquals(Option.none(), NONE_NUMBERS.maxBy(mapToString));
		// min()
		assertEquals(Option.some(-3.0), SOME_NUMBERS.min());
		assertEquals(Option.none(), NONE_NUMBERS.min());
		// minBy(Comparator)
		assertEquals(Option.some(0.0), SOME_NUMBERS.minBy(compareByAbsoluteValue));
		assertEquals(Option.none(), NONE_NUMBERS.minBy(compareByAbsoluteValue));
		// minBy(Function)
		assertEquals(Option.some(-3.0), SOME_NUMBERS.minBy(mapToString)); // numbers are mapped to "0.0", "-3.0", "2.0"... and then strings are compared
		assertEquals(Option.none(), NONE_NUMBERS.minBy(mapToString));
		// sum()
		assertEquals(-1.0, SOME_NUMBERS.sum());
		assertEquals(0, NONE_NUMBERS.sum());
		// product
		assertEquals(-0.0, SOME_NUMBERS.product());
		assertEquals(1, NONE_NUMBERS.product());
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