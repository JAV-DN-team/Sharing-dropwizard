package thymeleaf;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ClassLoaderTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.ClassLoaderUtils;

import java.util.Map;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class ClassResourceTemplateResolver extends AbstractConfigurableTemplateResolver {

    private final ClassLoader classLoader;

    public ClassResourceTemplateResolver() {
        this(ClassLoaderUtils.getClassLoader(ClassResourceTemplateResolver.class));
    }

    public ClassResourceTemplateResolver(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected ITemplateResource computeTemplateResource(final IEngineConfiguration configuration,
            final String ownerTemplate,
            final String template,
            final String resourceName,
            final String characterEncoding,
            final Map<String, Object> templateResolutionAttributes) {
        if(isFragment(ownerTemplate)) {
            final String templateWithPath = ownerTemplate.substring(0, ownerTemplate.lastIndexOf("/") + 1) + resourceName;
            return new ClassLoaderTemplateResource(this.classLoader, templateWithPath, characterEncoding);
        } else {
            return new ClassLoaderTemplateResource(this.classLoader, resourceName, characterEncoding);
        }
    }

    private boolean isFragment(final String ownerTemplate) {
        return ownerTemplate != null;
    }
}
