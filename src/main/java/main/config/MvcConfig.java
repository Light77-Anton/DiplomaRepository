package main.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("**").addResourceLocations("classpath:/upload/");
        registry.addResourceHandler("**").addResourceLocations("classpath:/avatars/");
        registry.addResourceHandler("js/**").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("css/**").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("fonts/**").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("img/**").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("**").addResourceLocations("classpath:/resources/");
    }
}
