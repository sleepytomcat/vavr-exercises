package tests;

import io.vavr.Tuple;
import io.vavr.Tuple1;
import io.vavr.Tuple2;
import io.vavr.Tuple8;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTuples {
	@Test
	void of() {
		// Tuple2
		Tuple2<String, String> tuple = Tuple.of("hello", "world");

		assertEquals(tuple._1, "hello");
		assertEquals(tuple._2(), "world");
		assertEquals(tuple.arity(), 2);

		// Tuple8
		Tuple8<Boolean, Byte, Character, Short, Integer, Long, Float, Double> longTuple
				= Tuple.of(true, (byte)0, 'a', (short)1, (int)2, (long)3, (float)1.1, (double)2.2);

		assertEquals(longTuple._1, Boolean.TRUE);
		assertEquals(longTuple._2.byteValue(), 0);
		assertEquals(longTuple._3.charValue(), 'a');
		assertEquals(longTuple._4.shortValue(), 1);
		assertEquals(longTuple._5.intValue(), 2);
		assertEquals(longTuple._6.longValue(), 3);
		assertEquals(longTuple._7.floatValue(), 1.1f);
		assertEquals(longTuple._8.doubleValue(), 2.2d);
	}

	@Test
	void map() {
		Tuple2<String, String> tuple = Tuple.of("hello", "world");

		// map, with BiFunction<>
		Tuple2<Integer, Integer> lengthTuple = tuple.map((a,b) -> Tuple.of(a.length(), b.length()));
		assertEquals(lengthTuple._1.intValue(), "hello".length());
		assertEquals(lengthTuple._2.intValue(), "world".length());

		// map, with two Function<>
		Tuple2<Integer, Integer> anotherLengthTuple = tuple.map(String::length, String::length);
		assertEquals(anotherLengthTuple._1.intValue(), "hello".length());
		assertEquals(anotherLengthTuple._2.intValue(), "world".length());

		// map1
		Tuple2<Integer, String> firstValueMappedTuple = tuple.map1(String::length);
		assertEquals(firstValueMappedTuple._1.intValue(), "hello".length());
		assertEquals(firstValueMappedTuple._2, "world");

		// map2
		Tuple2<String, Integer> secondValueMappedTuple = tuple.map2(String::length);
		assertEquals(secondValueMappedTuple._1, "hello");
		assertEquals(secondValueMappedTuple._2.intValue(), "world".length());
	}

	@Test
	void apply() {
		Tuple2<String, String> tuple = Tuple.of("hello", "world");

		String evaluated = tuple.apply((a, b) -> "first value " + a + " second value " + b);
		assertEquals(evaluated, "first value hello second value world");
	}
	
	@Test
	void update() {
		Tuple2<String, String> tuple = Tuple.of("hello", "world");

		// update1
		Tuple2<String, String> goodbyTuple = tuple.update1("goodby");
		assertEquals(goodbyTuple._1, "goodby");
		assertEquals(goodbyTuple._2, "world");

		// update2
		Tuple2<String, String> everyoneTuple = tuple.update2("everyone");
		assertEquals(everyoneTuple._1, "hello");
		assertEquals(everyoneTuple._2, "everyone");
   }

	@Test
	void concat() {
		Tuple1<String> first = Tuple.of("answer");
		Tuple1<Integer> second = Tuple.of(42);

		Tuple2 both = first.concat(second);
		assertEquals(both._1, "answer");
		assertEquals(both._2, 42);
	}

	@Test
	void swap() {
		Tuple2<String, Integer> tuple = Tuple.of("answer", 42);

		assertEquals(tuple._1, tuple.swap()._2);
		assertEquals(tuple._2, tuple.swap()._1);
	}

	@Test
	void sequence() {
		Iterable<Tuple2<String, Integer>> indexedDays = Arrays.asList(
				Tuple.of("Monday", 1),
				Tuple.of("Tuesday", 2),
				Tuple.of("Wednesday", 3),
				Tuple.of("Thursday", 4),
				Tuple.of("Friday", 5),
				Tuple.of("Saturday", 6),
				Tuple.of("Sunday", 7)
		);

		Tuple2<Seq<String>, Seq<Integer>> days = Tuple.sequence2(indexedDays);
		Seq<String> dayNames = days._1;
		Seq<Integer> dayIndexes = days._2;
		assertEquals(7, dayNames.size());
		assertEquals(7, dayIndexes.size());
	}
}
