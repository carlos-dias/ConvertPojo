package br.com.convertpojo.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.com.convertpojo.annotations.TypeConverter;
import br.com.convertpojo.annotations.TypesConverter;
import br.com.convertpojo.enums.Type;

public class PojoConverter {

	private static Logger logger = Logger.getLogger("br.com.convertpojo.business");

	public static OutputStream toCsv(File file, List<?> listPojo) {
		if (listPojo.isEmpty()) {
			logger.warning("List pojo is empty.");
			throw new IllegalStateException("List pojo is empty.");
		}
		OutputStream outputStream = null;

		converter(outputStream, file, listPojo.toArray());

		return outputStream;
	}

	public static OutputStream toCsv(File file, Object pojo) {
		OutputStream outputStream = null;

		converter(outputStream, file, pojo);

		return outputStream;
	}

	public static OutputStream toXml(File file, Object pojo) {
		logger.warning("Method not implemented in this version.");
		throw new UnsupportedOperationException("Method not implemented in this version.");
	}

	public static OutputStream toJson(File file, Object pojo) {
		logger.warning("Method not implemented in this version.");
		throw new UnsupportedOperationException("Method not implemented in this version.");
	}

	private static void converter(OutputStream outputStream, File file, Object... listPojo) {
		Class<?> clazz = listPojo[0].getClass();

		logger.info("Validating the Pojo fields...");
		List<Field> validateFields = validateFields(clazz.getDeclaredFields(), Type.CSV);

		if (validateFields.isEmpty()) {
			logger.warning("No field to convert.");
			return;
		}

		try {
			outputStream = new FileOutputStream(file);
			logger.info("Writing file header...");
			doHeader(outputStream, clazz, validateFields);

			logger.info("Writing file detail...");
			for (Object pojo : listPojo) {
				doDetail(outputStream, validateFields, pojo);
			}
			outputStream.close();
			logger.info("Finished successfully.");
		} catch (FileNotFoundException e) {
			logger.warning("Path \"" + file.getAbsolutePath() + "\" not found.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static List<Field> validateFields(Field[] fields, Type type) {
		List<Field> validateFields = new ArrayList<Field>(fields.length);
		for (Field field : fields) {
			if (field.isAnnotationPresent(TypeConverter.class)) {
				TypeConverter typeConverter = field.getAnnotation(TypeConverter.class);

				if (typeConverter.type() == type) {
					validateFields.add(field);
				}
			} else if (field.isAnnotationPresent(TypesConverter.class)) {
				TypesConverter typesConverter = field.getAnnotation(TypesConverter.class);
				for (Type t : typesConverter.types()) {
					if (t == type) {
						validateFields.add(field);
					}
				}
			}
		}
		return validateFields;
	}

	private static void doHeader(OutputStream outputStream, Class<?> clazz, List<Field> validateFields)
			throws IOException {
		for (Field field : clazz.getDeclaredFields()) {
			if (validateFields.contains(field)) {
				outputStream.write(field.getName().getBytes());
				outputStream.write(";".getBytes());
			}
		}
		outputStream.write("\n".getBytes());
	}

	private static void doDetail(OutputStream outputStream, List<Field> validateFields, Object pojo)
			throws IOException, IllegalArgumentException, IllegalAccessException {
		for (Field field : pojo.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (validateFields.contains(field)) {
				outputStream.write(String.valueOf(field.get(pojo)).getBytes());
				outputStream.write(";".getBytes());
			}
		}
		outputStream.write("\n".getBytes());
	}

}
