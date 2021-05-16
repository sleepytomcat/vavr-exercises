package tests;

import io.vavr.Lazy;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TestLazy {
	@Test
	void creation() {
		Lazy<Integer> lazyNumber = Lazy.of(() -> fibonacci(10));
		assertTrue(lazyNumber.isLazy());
		assertFalse(lazyNumber.isEvaluated());

		int number = lazyNumber.get();
		assertEquals(55, number);
		assertTrue(lazyNumber.isEvaluated());
	}

	@Test
	void mapping() {
		Lazy<Integer> lazyNumber = Lazy.of(() -> fibonacci(10));
		Lazy<Integer> anotherLazyNumber = lazyNumber.map(x -> x + 1);

		assertFalse(lazyNumber.isEvaluated());
		assertFalse(anotherLazyNumber.isEvaluated());

		int number = anotherLazyNumber.get();

		assertEquals(56, number);
		assertTrue(lazyNumber.isEvaluated());
		assertTrue(anotherLazyNumber.isEvaluated());
	}

	private int fibonacci(int x) {
		if (x < 1)
			throw new IllegalArgumentException();
		else if (x == 1)
			return 1;
		else if (x == 2)
			return 1;
		else
			return fibonacci(x - 1) + fibonacci(x - 2);
	}
}
