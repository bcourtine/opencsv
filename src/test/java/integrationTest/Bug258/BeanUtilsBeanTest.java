package integrationTest.Bug258;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class BeanUtilsBeanTest {

    @Test
    @Disabled("Disabled as this was just to test what was returned.")
    @DisplayName("What does getProperties return on a new instance?")
    public void whenNewInstance_thenGetPropertiesReturnOnNewInstance() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        //ConvertUtilsBean readConverter = BeanUtilsBean.getInstance().getConvertUtils();
        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        PropertyUtilsBean pb = beanUtilsBean.getPropertyUtils();
        Assertions.assertNotNull(pb);
    }

    public static boolean isStaticFieldInitialized(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true); // Allow access to private fields
                Object value = field.get(null); // Pass null for static fields
                return value != null;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Handle exceptions as needed
            e.printStackTrace();
        }
        return false;
    }

    @Test()
    @DisplayName("Check that the static field is initialized.")
    public void checkStaticFieldInitialized() {
        Assertions.assertTrue(isStaticFieldInitialized(BeanUtilsBean.class, "BEANS_BY_CLASSLOADER"));
    }
}
