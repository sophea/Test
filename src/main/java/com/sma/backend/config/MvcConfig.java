package com.sma.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.sma.backend.service.CategoryService;
import com.sma.backend.service.CategoryServiceImpl;
import com.sma.backend.service.backendService;
import com.sma.backend.web.CategoryController;
import com.wadpam.oauth2.service.OAuth2OpenUserService;
import com.wadpam.open.config.DomainConfig;
import com.wadpam.open.json.SkipNullObjectMapper;
import com.wadpam.open.user.config.UserConfig;
import com.wadpam.open.web.RestJsonExceptionResolver;

/**
 *
 * @author sophea
 */
@EnableWebMvc
@Configuration
@Import(value={
    DomainConfig.class,
    UserConfig.class
    })
@ImportResource(value={"classpath:/backend-service-spring-dao.xml","classpath:/oauth2-client-context.xml","classpath:/interceptor-security.xml"})
public class MvcConfig extends WebMvcConfigurerAdapter {
    
    // -------------- Services -----------------------
    
    @Bean
    public OAuth2OpenUserService oauth2UserService() {
        // the openUserService will be auto-wired
        return new OAuth2OpenUserService();
    }
    
    @Bean(initMethod = "init")
    public backendService tutorialService() {
        // domainService and factoryService will be auto-wired
        return new backendService();
    }
    
    @Bean
    public CategoryService categoryService() {
        // domainService and factoryService will be auto-wired
        return new CategoryServiceImpl();
    }
    
    @Bean
    public CategoryController categoryController() {
        CategoryController controller = new CategoryController();
        controller.setService(categoryService());
        return controller;
    }
    // -------------- Message Converters ----------------------

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        SkipNullObjectMapper skipNullMapper = new SkipNullObjectMapper();
        skipNullMapper.init();
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
        converter.setObjectMapper(skipNullMapper);
        converters.add(converter);
    }

    // interceptors configured in interceptor-security.xml
    
    // -------------- Serving Resources ----------------------

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("/static/")
                .addResourceLocations("classpath:/static/");
    }
    @Bean
    public InternalResourceViewResolver htmlViewResolver() {
        final InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(InternalResourceView.class);
        bean.setOrder(999);
        bean.setPrefix("/WEB-INF/jsp/");
        bean.setSuffix(".jsp");
        return bean;
    }
    
   /* <!--Basic application beans. -->
    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/" />
        <property name="suffix" value=".jsp" />
    </bean>*/
    
    // -------------- Controllers ----------------------
    
    
    // -------------- View Stuff -----------------------

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(restJsonExceptionResolver());
    }
    
    public @Bean RestJsonExceptionResolver restJsonExceptionResolver() {
        final RestJsonExceptionResolver bean = new RestJsonExceptionResolver();
        bean.setOrder(100);
        return bean;
    }
    
}
