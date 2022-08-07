package main.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/upload/**").addResourceLocations("file:/upload/");
        registry.addResourceHandler("/avatars/**").addResourceLocations("file:/avatars/");
        //registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        //registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        //registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/");
        //registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
        //registry.addResourceHandler("/favicon.ico").addResourceLocations("file:/");
        //registry.addResourceHandler("/default-1.png").addResourceLocations("file:/");
    }
}
