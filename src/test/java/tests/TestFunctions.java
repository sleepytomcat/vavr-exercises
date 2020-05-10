package tests;

import io.vavr.*;
import io.vavr.control.Option;
import io.vavr.control.Try;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestFunctions {
	@Test
	public void functionCreation() {
		// Function0
		Function0<Integer> f0 = () -> 1;
		assertEquals(f0.apply(), Integer.valueOf(1));
		assertEquals(f0.arity(), 0); // number of args

		// Function3
		Function3<Boolean, Long, Character, String> f3 = (x, y, z) -> x.toString() + y.toString() + z.toString();
		assertEquals(f3.apply(true, 1L, 'A'), "true1A");

		// of
		Function1<Integer, String> f1 = Function1.of(TestFunctions::helperMethod);
		assertEquals(f1.apply(10), "10");
	}

	@Test
	public void functionComposition() {
		Function2<Integer, Integer, Integer> sum = (x, y) -> x + y;
		Function1<Integer, Integer> multiplyBy3 = x -> x * 3;
		Function2<Integer, Integer, Integer> sumAndMultiplyBy2 = sum.andThen(multiplyBy3);
		assertEquals(sumAndMultiplyBy2.apply(1, 2), Integer.valueOf((1 + 2) * 3));
	}

	@Test
	public void functionTransformations() {
		// reversed order of args
		Function2<Float, Float, Float> div = (x, y) -> x / y;
		Function2<Float, Float, Float> reversedDiv = div.reversed();
		assertEquals(div.apply(2f, 3f), Float.valueOf(2f / 3f));
		assertEquals(reversedDiv.apply(2f, 3f), Float.valueOf(3f / 2f));

		// curried function
		Function1<Float, Function1<Float, Float>> curriedDiv = div.curried();
		assertEquals(curriedDiv.apply(3f).apply(4f), Float.valueOf(3f / 4f));

		// tupled
		Function1<Tuple2<Float, Float>, Float> tupledDiv = div.tupled();
		Tuple2<Float, Float> args = Tuple.of(2f, 3f);
		assertEquals(tupledDiv.apply(args), Float.valueOf(2f / 3f));

		// partially applied
		Function3<Integer, Integer, Integer, Integer> sum = (x, y, z) -> x + y + z;
		Function2<Integer, Integer, Integer> f = sum.apply(10); // partially applied, first argument bound
		assertEquals(f.apply(3, 4), Integer.valueOf(10 + 3 + 4));

		Function0<Double> randomNumbers = Math::random;
		Double value1 = randomNumbers.apply();
		Double value2 = randomNumbers.apply();
		Double value3 = randomNumbers.apply();
		// all values are (most probably) different random numbers
		assertNotEquals(value1, value2);
		assertNotEquals(value1, value3);

		Function0<Double> memoizedRandomNumbers = randomNumbers.memoized();
		Double valueA = memoizedRandomNumbers.apply();
		Double valueB = memoizedRandomNumbers.apply();
		Double valueC = memoizedRandomNumbers.apply();
		// memoized function evaluated only once; subsequent calls return same result for same input
		assertTrue(memoizedRandomNumbers.isMemoized());
		assertEquals(valueA, valueB);
		assertEquals(valueA, valueC);
	}

	private static String helperMethod(Integer number) {
		return String.valueOf(number);
	}

	@Test
	public void functionLifting() {
		Function2<Integer, Integer, Integer> div = (x, y) -> x / y;

		// div is partial function (not defined for some input values)
		assertThrows(ArithmeticException.class, () -> div.apply(1, 0));

		// lifted to Option<Integer>
		Function2<Integer, Integer, Option<Integer>> liftedToOption = Function2.lift(div);
		Option<Integer> maybeNumber = liftedToOption.apply(1, 0);
		assertTrue(maybeNumber.isEmpty());

		// lifted to Try<Integer>
		Function2<Integer, Integer, Try<Integer>> liftedToTry = Function2.liftTry(div);
		Try<Integer> tryNumber = liftedToTry.apply(1, 0);
		assertTrue(tryNumber.isFailure());
	}
}