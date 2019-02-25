package tests;

import io.vavr.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestFunctions {
	@Test
	public void functionCreation() {
		// Function0
		Function0<Integer> f0 = () -> 1;
		Assert.assertEquals(f0.apply(), Integer.valueOf(1));
		Assert.assertEquals(f0.arity(), 0); // number of args

		// Function3
		Function3<Boolean, Long, Character, String> f3 = (x, y, z) -> x.toString() + y.toString() + z.toString();
		Assert.assertEquals(f3.apply(true, 1L, 'A'), "true1A");

		// of
		Function1<Integer, String> f1 = Function1.of(TestFunctions::helperMethod);
		Assert.assertEquals(f1.apply(10), "10");
	}

	@Test
	public void functionComposition() {
		Function2<Integer, Integer, Integer> sum = (x, y) -> x + y;
		Function1<Integer, Integer> multiplyBy3 = x -> x * 3;
		Function2<Integer, Integer, Integer> sumAndMultiplyBy2 = sum.andThen(multiplyBy3);
		Assert.assertEquals(sumAndMultiplyBy2.apply(1, 2), Integer.valueOf((1+2)*3));
	}

	@Test
	public void functionTransformations() {
		// reversed order of args
		Function2<Float, Float, Float> div = (x, y) -> x / y;
		Function2<Float, Float, Float> reversedDiv = div.reversed();

		Assert.assertEquals(div.apply(2f, 3f), Float.valueOf(2f/3f));
		Assert.assertEquals(reversedDiv.apply(2f, 3f), Float.valueOf(3f/2f));

		// curried function
		Function1<Float, Function1<Float, Float>> curriedDiv = div.curried();
		Assert.assertEquals(curriedDiv.apply(3f).apply(4f), Float.valueOf(3f/4f));

		// tupled
		Function1<Tuple2<Float, Float>, Float> tupledDiv = div.tupled();
		Tuple2<Float, Float> args =  Tuple.of(2f, 3f);
		Assert.assertEquals(tupledDiv.apply(args), Float.valueOf(2f/3f));
	}

	private static String helperMethod(Integer number) {
		return String.valueOf(number);
	}
}
