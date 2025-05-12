package integrationTest.Bug258;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

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
}
