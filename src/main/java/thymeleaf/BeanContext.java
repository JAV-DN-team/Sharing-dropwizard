package thymeleaf;

import io.dropwizard.views.View;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.LazyContextVariable;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class BeanContext extends AbstractContext {

    public BeanContext(final View view, final Locale locale) {
        super(locale);
        initVariables(view);
    }

    private void initVariables(final View view) {

        PropertyDescriptor[] propertyDescs = new PropertyDescriptor[0];
        try {
            propertyDescs = Introspector.getBeanInfo(view.getClass(), View.class).getPropertyDescriptors();
        } catch (final IntrospectionException e) {
            throw new RuntimeException(e);
        }

        for (PropertyDescriptor desc : propertyDescs) {

            final String propName = desc.getDisplayName();
            final Method method = desc.getReadMethod();

            setVariable(propName, new LazyContextVariable() {
                @Override
                protected Object loadValue() {
                    try {
                        return method.invoke(view, new Object[0]);
                    } catch (final InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
