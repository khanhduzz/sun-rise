package com.fjb.sunrise.config;

import static com.fjb.sunrise.utils.Constants.ApiConstant.AUTH_VIEW;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.fjb.sunrise"})
public class WebServletConfig implements WebMvcConfigurer {
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(@Qualifier("templateResolver")
                                               SpringResourceTemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.setEnableSpringELCompiler(true);
        engine.addDialect(new SpringSecurityDialect());
        engine.addDialect(new LayoutDialect());
        return engine;
    }

    @Bean
    public ViewResolver viewResolver(@Qualifier("templateEngine") SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/webjars/**", "/img/**", "/css/**", "/js/**", "/vendor/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/",
                "classpath:/static/img/", "classpath:/static/css/", "classpath:/static/js/",
                "classpath:/static/bootstrapv5/css/", "classpath:/static/bootstrapv5/js/",
                "classpath:/static/vendor/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/health").setViewName("health");
        registry.addViewController("/auth/login").setViewName(AUTH_VIEW);
        registry.addViewController("/auth/register").setViewName(AUTH_VIEW);
        registry.addViewController("/category/index").setViewName("category/index");
        registry.addViewController("/medias").setViewName("media/index");

        registry.addViewController("/v2/home").setViewName("home-v2");
    }
}
