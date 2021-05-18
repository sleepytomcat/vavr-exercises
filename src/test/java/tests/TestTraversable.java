package tests;

import io.vavr.PartialFunction;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.*;
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
	static final Traversable<Double> SOME_NUMBERS = Array.of(0.0, -3.0, 2.0, 0.0);
	static final Traversable<Double> NONE_NUMBERS = Array.of();

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
		// average()
		assertEquals(Option.some(-0.25), SOME_NUMBERS.average());
		assertEquals(Option.none(), NONE_NUMBERS.average());

		// max()
		assertEquals(Option.some(2.0), SOME_NUMBERS.max());
		assertEquals(Option.none(), NONE_NUMBERS.max());

		// maxBy(Comparator)
		Comparator<Double> compareByAbsoluteValue = (x, y) -> Math.abs(x) > Math.abs(y) ? 1 : Math.abs(x) == Math.abs(y) ? 0 : -1;
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
		// count(Predicate)
		assertEquals(3, SOME_NUMBERS.count(x -> x <= 0.0));

		// fold(Object, BiFunction)
		assertEquals(-1.0, SOME_NUMBERS.fold(0.0, (x,y) -> x + y)); // folding elements into object of same type

		// foldLeft(Object, BiFunction): starts from head of Traversable, uses provided 'zero' value as a starting accumulator value
		assertEquals(13, SOME_NUMBERS.foldLeft(0, (x,y) -> x + String.valueOf(y).length()));

		// foldRight(Object, BiFunction): starts from tail of Traversable, uses provided 'zero' value as a starting accumulator value
		assertEquals(13, SOME_NUMBERS.foldRight(0, (x,y) -> String.valueOf(x).length() + y));

		// reduce(BiFunction)
		assertEquals(-1.0, SOME_NUMBERS.reduce((x,y) -> x + y)); // reducing elements into one
		assertThrows(NoSuchElementException.class, () -> NONE_NUMBERS.reduce((x,y) -> x + y));

		// reduceOption(BiFunction)
		assertEquals(Option.some(-1.0), SOME_NUMBERS.reduceOption((x,y) -> x + y)); // reducing elements into one
		assertEquals(Option.none(), NONE_NUMBERS.reduceOption((x,y) -> x + y));

		// reduceLeft(BiFunction): starts from head, reduces elements into one
		assertEquals(-1.0, SOME_NUMBERS.reduceLeft((x,y) -> x + y)); // reducing elements into one
		assertThrows(NoSuchElementException.class, () -> NONE_NUMBERS.reduceLeft((x,y) -> x + y));

		// reduceLeftOption(BiFunction)
		assertEquals(Option.some(-1.0), SOME_NUMBERS.reduceLeftOption((x,y) -> x + y));
		assertEquals(Option.none(), NONE_NUMBERS.reduceLeftOption((x,y) -> x + y));

		// reduceRight(BiFunction): starts from tail, reduces elements into one
		assertEquals(-1.0, SOME_NUMBERS.reduceRight((x,y) -> x + y)); // reducing elements into one
		assertThrows(NoSuchElementException.class, () -> NONE_NUMBERS.reduceRight((x,y) -> x + y));

		// reduceRightOption(BiFunction)
		assertEquals(Option.some(-1.0), SOME_NUMBERS.reduceRightOption((x,y) -> x + y));
		assertEquals(Option.none(), NONE_NUMBERS.reduceRightOption((x,y) -> x + y));

		// mkString()
		assertEquals("abc$", SOME_CHARACTERS.mkString());

		// mkString(CharSequence)
		assertEquals("a+b+c+$", SOME_CHARACTERS.mkString(CharSeq.of("+")));

		// mkString(CharSequence, CharSequence, CharSequence)
		assertEquals("[a,b,c,$]", SOME_CHARACTERS.mkString(CharSeq.of("["), CharSeq.of(","), CharSeq.of("]")));
	}

	@Test
	void selection() {
		// drop(int)
		assertEquals(Array.of('b', 'c', '$'), SOME_CHARACTERS.drop(1));
		assertEquals(Array.empty(), SOME_CHARACTERS.drop(100));

		// dropRight(int)
		assertEquals(Array.of('a', 'b', 'c'), SOME_CHARACTERS.dropRight(1));
		assertEquals(Array.empty(), SOME_CHARACTERS.dropRight(100));

		// dropUntil(Predicate)
		assertEquals(Array.of('c', '$'), SOME_CHARACTERS.dropUntil(character -> character == 'c'));
		assertEquals(Array.empty(), SOME_CHARACTERS.dropUntil(character -> character == '%'));

		// dropWhile(Predicate)
		assertEquals(Array.of('c', '$'), SOME_CHARACTERS.dropWhile(character -> (character == 'a' || character == 'b')));
		assertEquals(Array.of('a', 'b', 'c', '$'), SOME_CHARACTERS.dropWhile(character -> character == '%'));

		// filter(Predicate)
		assertEquals(Array.of('b', 'c'), SOME_CHARACTERS.filter(character -> (character == 'b' || character == 'c')));

		// reject(Predicate)
		assertEquals(Array.of('a', '$'), SOME_CHARACTERS.reject(character -> (character == 'b' || character == 'c')));

		// find(Predicate)
		assertEquals(Option.some('b'), SOME_CHARACTERS.find(character -> character == 'b'));
		assertEquals(Option.none(), SOME_CHARACTERS.find(character -> character == '%'));

		// findLast(Predicate)
		assertEquals(Option.some('c'), SOME_CHARACTERS.findLast(character -> (character == 'b' || character == 'c')));
		assertEquals(Option.none(), SOME_CHARACTERS.findLast(character -> character == '%'));

		// groupBy(Function)
		Map<String, ? extends Traversable<Character>> groups = SOME_CHARACTERS.groupBy(character -> Character.isAlphabetic(character) ? "ALPHABETIC" : "NONALPHABETIC");
		assertEquals(Array.of('a','b','c'), groups.get("ALPHABETIC").get());
		assertEquals(Array.of('$'), groups.get("NONALPHABETIC").get());

		// partition(Predicate)
		Tuple2<? extends Traversable<Character>, ? extends Traversable<Character>> partitions = SOME_CHARACTERS.partition(character -> character == 'a' || character == 'c');
		assertTrue(partitions._1().containsAll(Array.of('a', 'c')));
		assertTrue(partitions._2().containsAll(Array.of('b', '$')));

		// retainAll(Iterable)
		assertEquals(Array.of('a', '$'), SOME_CHARACTERS.retainAll(Arrays.asList('a', '$')));
		assertEquals(Array.empty(), NONE_CHARACTERS.retainAll(Arrays.asList('a', '$')));

		// take(int)
		assertEquals(Array.of('a', 'b'), SOME_CHARACTERS.take(2));
		assertEquals(Array.empty(), NONE_CHARACTERS.take(100));

		// takeRight(int)
		assertEquals(Array.of('c', '$'), SOME_CHARACTERS.takeRight(2));
		assertEquals(Array.empty(), NONE_CHARACTERS.takeRight(100));

		// takeUntil(Predicate)
		assertEquals(Array.of('a', 'b'), SOME_CHARACTERS.takeUntil(character -> character == 'c'));
		assertEquals(Array.of('a', 'b', 'c', '$'), SOME_CHARACTERS.takeUntil(character -> character == '%'));

		// takeWhile(Predicate)
		assertEquals(Array.of('a', 'b'), SOME_CHARACTERS.takeWhile(character -> (character == 'a' || character == 'b')));
		assertEquals(Array.empty(), SOME_CHARACTERS.takeWhile(character -> character == '%'));
	}

	@Test
	void tests() {
		// existsUnique(Predicate)
		assertTrue(Array.of(1,2,2,-3,3).existsUnique(x -> x < 0));
		assertFalse(Array.of(1,2,2,-3,3).existsUnique(x -> x > 0));

		// hasDefiniteSize()
		assertTrue(Array.of(1,2,3).hasDefiniteSize());
		assertFalse(Stream.iterate(() -> Option.some(0)).hasDefiniteSize()); // infinite stream

		// isDistinct()
		assertFalse(Array.of(1,2,3).isDistinct()); // i.e. Array does not enforce element to be distinct
		assertTrue(HashSet.of(1,2,3).isDistinct()); // i.e. HashSet does enforce element to be distinct

		// isOrdered()
		assertFalse(Array.of(1,2,3).isOrdered());
		assertTrue(TreeSet.of(1,2,3).isOrdered());

		// isSequential()
		assertTrue(Array.of(1,2,3).isSequential()); // i.e. Arrays are sequential
		assertFalse(HashSet.of(1,2,3).isSequential()); // i.e. HashSets are not sequential
		assertTrue(LinkedHashSet.of(1,2,3).isSequential()); // i.e. LinkedHashSets are sequential

		// isTraversableAgain()
		assertTrue(Array.of(1,2,3).isTraversableAgain());
		assertFalse(Iterator.of(1,2,3).isTraversableAgain()); // iterators are traversable only once
	}

	@Test
	void transformation() {
		// distinct()
		assertEquals(Array.of(1,2,3), Array.of(1,2,2,2,3).distinct());
		assertEquals(Array.empty(), Array.empty().distinct());

		// distinctBy(Function)
		assertEquals(Array.of(-2, 1), Array.of(-2, -1, 1, 2).distinctBy(x -> x > 0));

		// distinctBy(Comparator)
		Comparator<Integer> compareByAbsoluteValue = (x, y) -> Math.abs(x) > Math.abs(y) ? 1 : Math.abs(x) == Math.abs(y) ? 0 : -1;
		assertEquals(Array.of(-2, -1, 3), Array.of(-2, -1, 1, 2, 3).distinctBy(compareByAbsoluteValue));

		// flatMap(Function)
		assertEquals(Array.of(10, 11, 12, 20, 21, 22, 30, 31, 32), Array.of(10, 20, 30).flatMap(x -> Array.of(x, x+1, x+2)));

		// map(Function)
		assertEquals(Array.of(-1, -2, -3), Array.of(1, 2, 3).map(x -> -x));

		// replace(Object, Object)
		assertEquals(Array.of(1, 0, 2, 2, 3), Array.of(1, 2, 2, 2, 3).replace(2, 0));

		// replaceAll(Object, Object)
		assertEquals(Array.of(1, 0, 0, 0, 3), Array.of(1, 2, 2, 2, 3).replaceAll(2, 0));

		// scan(Object, BiFunction)
		assertEquals(Array.of(0, 1, 3, 6, 10, 15), Array.of(1, 2, 3, 4, 5).scan(0, (x,y) -> x + y));

		// scanLeft(Object, BiFunction)
		assertEquals(Array.of(0, 1, 3, 6, 10, 15), Array.of(1, 2, 3, 4, 5).scanLeft(0, (x,y) -> x + y));

		// scanRight(Object, BiFunction)
		assertEquals(Array.of(15, 14, 12, 9, 5, 0), Array.of(1, 2, 3, 4, 5).scanRight(0, (x,y) -> x + y));

		// span(Predicate)
		assertEquals(Array.of(-1, -2, -3), Array.of(-1, -2, -3, 0, -1, 5).span(x -> x < 0)._1()); // compare to partition(Predicate)
		assertEquals(Array.of(0, -1, 5), Array.of(-1, -2, -3, 0, -1, 5).span(x -> x < 0)._2());

		// unzip(Function)
		Traversable<Integer> numbers = Array.of(-1, -5, 7);
		assertEquals(Array.of(1, 5, 7), numbers.unzip(number -> Tuple.of(Math.abs(number), Math.signum(number)))._1());
		assertEquals(Array.of(-1.0f, -1.0f, 1.0f), numbers.unzip(number -> Tuple.of(Math.abs(number), Math.signum(number)))._2());

		// unzip3(Function)
		Tuple3<? extends Traversable<Integer>, ? extends Traversable<Float>, ? extends Traversable<String>>
				unzippedNumbers = numbers.unzip3(number -> Tuple.of(Math.abs(number), Math.signum(number), String.valueOf(number)));
		assertEquals(Array.of(1, 5, 7), unzippedNumbers._1());
		assertEquals(Array.of(-1.0f, -1.0f, 1.0f), unzippedNumbers._2());
		assertEquals(Array.of("-1", "-5", "7"), unzippedNumbers._3());

		// zip(Iterable)
		Traversable<Tuple2<Character, String>> zipped = SOME_CHARACTERS.zip(Array.of("one", "two", "three", "four"));
		Traversable<Tuple2<Character, String>> expectedZipped = Array.of(
				Tuple.of('a', "one"),
				Tuple.of('b', "two"),
				Tuple.of('c', "three"),
				Tuple.of('$', "four")
		);
		assertEquals(expectedZipped, zipped);

		// zipAll(Iterable, Object, Object)
		Traversable<Tuple2<Character, String>> zippedAll = SOME_CHARACTERS.zipAll(Array.of("one", "two"), '-', "padding");
		Traversable<Tuple2<Character, String>> expectedZippedAll = Array.of(
				Tuple.of('a', "one"),
				Tuple.of('b', "two"),
				Tuple.of('c', "padding"),
				Tuple.of('$', "padding")
		);
		assertEquals(expectedZippedAll, zippedAll);

		// zipWithIndex()
		Traversable<Tuple2<Character, Integer>> zippedWithIndex = SOME_CHARACTERS.zipWithIndex();
		Traversable<Tuple2<Character, Integer>> expectedZippedWithIndex = Array.of(
				Tuple.of('a', 0),
				Tuple.of('b', 1),
				Tuple.of('c', 2),
				Tuple.of('$', 3)
		);
		assertEquals(expectedZippedWithIndex, zippedWithIndex);
	}
}