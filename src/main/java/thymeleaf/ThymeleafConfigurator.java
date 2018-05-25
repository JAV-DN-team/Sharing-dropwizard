package thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class ThymeleafConfigurator {

    public static final String THYMELEAF_CACHE_KEY = "cache";
    public static final String THYMELEAF_PREFIX_KEY = "prefix";
    public static final String THYMELEAF_SUFFIX_KEY = "suffix";
    public static final String THYMELEAF_TEMPLATEMODE_KEY = "mode";

    private static final String DEFAULT_SUFFIX = ".html";
    private static final String DEFAULT_PREFIX = "";

    private String suffix = DEFAULT_SUFFIX;

    private Boolean cache;
    private String prefix;
    private String templateMode;

    public ThymeleafConfigurator(final Map<String, String> options) {
        readOptions(options);
    }

    private void readOptions(final Map<String, String> options) {
        this.cache = options.getOrDefault(THYMELEAF_CACHE_KEY, "true").equals("true");
        this.prefix = preparePath(options.getOrDefault(THYMELEAF_PREFIX_KEY, DEFAULT_PREFIX));
        this.suffix = options.getOrDefault(THYMELEAF_SUFFIX_KEY, DEFAULT_SUFFIX);
        this.templateMode = options.getOrDefault(THYMELEAF_TEMPLATEMODE_KEY, "HTML").toUpperCase();
    }

    private String preparePath(final String path) {
        final int position = path.length() - 1;
        return position > -1 && path.charAt(position) == '/' ? path.substring(0, position) : path;
    }

    public Boolean isCacheable() {
        return cache;
    }

    public String getTemplateMode() {
        return templateMode;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void apply(final ClassResourceTemplateResolver templateResolver, final TemplateEngine templateEngine) {
        templateResolver.setCacheable(this.cache);
        templateResolver.setSuffix(this.suffix);
        templateResolver.setTemplateMode(TemplateMode.valueOf(this.templateMode));
        final Set<IDialect> dialects = new LinkedHashSet<>();
        dialects.add(new StandardDialect());
        dialects.add(new Java8TimeDialect());
        templateEngine.setDialects(dialects);
        templateEngine.setTemplateResolver(templateResolver);
    }
}
