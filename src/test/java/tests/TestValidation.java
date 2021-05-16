package tests;

import io.vavr.collection.CharSeq;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestValidation {
	@Test
	void validating() {
		Validation<String, Integer> ageValidation = validateAge(-10);
		Validation<String, String> nameValidation = validateName("$53$#(%*#");

		Validation<Seq<String>, Person> validatedPerson = Validation.combine(nameValidation, ageValidation).ap(Person::new);

		assertNotNull(validatedPerson.isInvalid());
		assertTrue(validatedPerson.getError().contains("Age cannot be negative"));
		assertTrue(validatedPerson.getError().contains("Name contains invalid characters: #$%(*35"));
	}

	private Validation<String, Integer> validateAge(Integer age) {
		return age > 0
				? Validation.valid(age)
				: Validation.invalid("Age cannot be negative");
	}

	private Validation<String, String> validateName(String name) {
		final String VALID_NAME_CHARS = "[a-zA-Z ]";
		return CharSeq.of(name).replaceAll(VALID_NAME_CHARS, "").transform(
				seq -> seq.isEmpty()
					? Validation.valid(name)
					: Validation.invalid("Name contains invalid characters: " + seq.distinct().sorted())
		);
	}

	static class Person {
		final String name;
		final Integer age;
		public Person(String name, Integer age) {
			this.name = name;
			this.age = age;
		}
	}
}