package cz.zcu.kiv.nlp.ir.utils;

public class ValidationUtils {

  public static <T> void checkNotNull(final T obj, final String objectName) throws IllegalArgumentException {
    if (obj == null) {
      final String name = objectName != null ? objectName : "Parameter";
      throw new IllegalArgumentException(name + "may not be null");
    }
  }
}
