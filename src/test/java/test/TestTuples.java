package test;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.testng.annotations.Test;
import org.testng.Assert;

public class TestTuples {
	@Test
	public void tupleCreation() {
		Tuple2<String, String> tuple = Tuple.of("hello", "world");

		Assert.assertEquals(tuple._1, "hello");
		Assert.assertEquals(tuple._2, "world");
		Assert.assertEquals(tuple.arity(), 2);
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
