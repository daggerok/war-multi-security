package daggerok.multi.web.config;

import daggerok.multi.data.user.User;
import daggerok.multi.data.user.UserRepository;
import daggerok.multi.web.WebApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Arrays;

@Configuration
public class Initializer extends SpringBootServletInitializer {
    @Bean
    public CommandLineRunner testData(UserRepository userRepository) {
        return args -> Arrays.asList("max,dag,bax".split(","))
                .forEach(name -> userRepository.save(User.of(name, name)));
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean
    public ServletRegistrationBean dispatcherServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet(), "/*");

        registration.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
        return registration;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebApplication.class);
    }
}