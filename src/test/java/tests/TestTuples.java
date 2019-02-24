package tests;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple8;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTuples {
	@Test
	public void tupleCreation() {
		// Tuple2
		Tuple2<String, String> tuple = Tuple.of("hello", "world");

		Assert.assertEquals(tuple._1, "hello");
		Assert.assertEquals(tuple._2, "world");
		Assert.assertEquals(tuple.arity(), 2);

		// Tuple8
		Tuple8<Boolean, Byte, Character, Short, Integer, Long, Float, Double> longTuple
				= Tuple.of(true, (byte)0, 'a', (short)1, (int)2, (long)3, (float)1.1, (double)2.2);

		Assert.assertEquals(longTuple._1, Boolean.TRUE);
		Assert.assertEquals(longTuple._2.byteValue(), 0);
		Assert.assertEquals(longTuple._3.charValue(), 'a');
		Assert.assertEquals(longTuple._4.shortValue(), 1);
		Assert.assertEquals(longTuple._5.intValue(), 2);
		Assert.assertEquals(longTuple._6.longValue(), 3);
		Assert.assertEquals(longTuple._7.floatValue(), 1.1f);
		Assert.assertEquals(longTuple._8.doubleValue(), 2.2d);
	}

	@Test
	public void tupleTransform() {
		Tuple2<String, String> tuple = Tuple.of("hello", "world");

		// map
		Tuple2<Integer, Integer> lengthTuple = tuple.map((a,b) -> Tuple.of(a.length(), b.length()));
		Assert.assertEquals(lengthTuple._1.intValue(), "hello".length());
		Assert.assertEquals(lengthTuple._2.intValue(), "world".length());

		// map1
		Tuple2<Integer, String> firstValueMappedTuple = tuple.map1(String::length);
		Assert.assertEquals(firstValueMappedTuple._1.intValue(), "hello".length());
		Assert.assertEquals(firstValueMappedTuple._2, "world");

		// map2
		Tuple2<String, Integer> secondValueMappedTuple = tuple.map2(String::length);
		Assert.assertEquals(secondValueMappedTuple._1, "hello");
		Assert.assertEquals(secondValueMappedTuple._2.intValue(), "world".length());

		// apply
		String evaluated = tuple.apply((a, b) -> "first value " + a + " second value " + b);
		Assert.assertEquals(evaluated, "first value hello second value world");

		// update1
		Tuple2<String, String> goodbyTuple = tuple.update1("goodby");
		Assert.assertEquals(goodbyTuple._1, "goodby");
		Assert.assertEquals(goodbyTuple._2, "world");

		// update2
		Tuple2<String, String> everyoneTuple = tuple.update2("everyone");
		Assert.assertEquals(everyoneTuple._1, "hello");
		Assert.assertEquals(everyoneTuple._2, "everyone");
   }
}
