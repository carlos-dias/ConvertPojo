package br.com.convertpojo.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.convertpojo.annotations.TypeConverter;
import br.com.convertpojo.annotations.TypesConverter;
import br.com.convertpojo.business.PojoConverter;
import br.com.convertpojo.enums.Type;

public class TestConverter {

	@Test
	public void testPojo() {
		Person person = new Person(1, 13, "João", "Silva");

		File file = new File("test1.csv");

		OutputStream out = PojoConverter.toCsv(file, person);

		assertTrue(file.exists());
	}

	@Test
	public void testListPojo() {
		Person person1 = new Person(1, 13, "João", "Silva");
		Person person2 = new Person(2, 10, "Maria", "Silva");

		List<Person> listPerson = new ArrayList<Person>();

		listPerson.add(person1);
		listPerson.add(person2);

		File file = new File("test2.csv");

		OutputStream out = PojoConverter.toCsv(file, listPerson);

		assertTrue(file.exists());
	}

	class Person {
		private Integer id;

		@TypesConverter(types = { Type.CSV })
		private Integer age;

		@TypeConverter(type = Type.CSV)
		private String firstName;

		@TypeConverter
		private String lastName;

		public Person(Integer id, Integer age, String firstName, String lastName) {
			super();
			this.id = id;
			this.age = age;
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

	}
}
