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
        registry.addResourceHandler("/upload/**").addResourceLocations("file:upload/");
        registry.addResourceHandler("/avatars/**").addResourceLocations("file:avatars/");
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/",
                "classpath:/static/",
                "classpath:/js/",
                "classpath:/css/",
                "classpath:/fonts/");
    }
}
