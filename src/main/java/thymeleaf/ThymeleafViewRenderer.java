package thymeleaf;

import io.dropwizard.views.View;
import io.dropwizard.views.ViewRenderer;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class ThymeleafViewRenderer implements ViewRenderer {

    private final ClassResourceTemplateResolver templateResolver;
    private final TemplateEngine templateEngine;
    private final Optional<Function<View, String>> templatePathProvider;
    private ThymeleafConfigurator config;

    public ThymeleafViewRenderer() {
        this(new ClassResourceTemplateResolver(), new TemplateEngine(), Optional.empty());
    }

    /**
     *  All necessary dependencies for custom {@link ThymeleafViewRenderer}.
     *
     * @param templateResolver Resolver instance for template loading
     * @param templateEngine Engine for template rendering
     * @param templatePathProvider path provider for given view
     */
    public ThymeleafViewRenderer(final ClassResourceTemplateResolver templateResolver,
            final TemplateEngine templateEngine,
            final Optional<Function<View, String>> templatePathProvider) {
        this.templateResolver = templateResolver;
        this.templateEngine = templateEngine;
        this.templatePathProvider = templatePathProvider;
    }

    /**
     * Determine if given {@link View} is renderable with Thymeleaf.
     * <p>Configured option for {@link ThymeleafConfigurator#THYMELEAF_SUFFIX_KEY} (Default: '.html') is essential for the decision.</p>
     *
     * @param  view next view to render
     * @return true if view is renderable with Thymeleaf
     */
    public boolean isRenderable(final View view) {
        return view.getTemplateName().endsWith(this.config.getSuffix());
    }

    public void render(final View view, final Locale locale, final OutputStream output) throws IOException {
        final String templateNameWithPath = templatePathProvider.map(t -> t.apply(view))
                .orElseGet(() -> this.config.getPrefix() + view.getTemplateName().replace("/view", "").replace(this.config.getSuffix(), ""));
        final OutputStreamWriter writer = new OutputStreamWriter(output, view.getCharset().orElse(StandardCharsets.UTF_8));
        templateEngine.process(templateNameWithPath, new BeanContext(view, locale), writer);
        writer.flush();
    }

    /**
     * Used by {@link io.dropwizard.views.ViewBundle#run}
     * <p>Valid option keys are declared in {@link ThymeleafConfigurator} (e.g. <i>THYMELEAF_CACHE_KEY</i> ) as static fields.</p>
     *
     * @param options - user initialized map to override default options
     */
    public void configure(final Map<String, String> options) {
        config = new ThymeleafConfigurator(options);
        config.apply(this.templateResolver, this.templateEngine);
    }

    /**
     *  Used by {@link io.dropwizard.views.ViewBundle#run} to determine, which options should be injected into {@link #configure} method
     **/
    public String getSuffix() {
        return ".html";
    }
}
