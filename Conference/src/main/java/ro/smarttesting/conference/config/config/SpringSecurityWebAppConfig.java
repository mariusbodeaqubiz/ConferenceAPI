package ro.smarttesting.conference.config.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;

/**
 * Created by andreicontan on 04/01/2017.
 */
@Configuration
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    Roles roles;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String adminAuthority = "hasAuthority('" + roles.ADMIN + "')";
        http.apply(stormpath()).and()
                .authorizeRequests()
                .antMatchers("/conferences/search/*").permitAll()
                .antMatchers(HttpMethod.GET, "/conferences/*").permitAll()
//                .antMatchers(HttpMethod.DELETE, "/conferences/*").access(adminAuthority)
                .antMatchers(HttpMethod.PATCH, "/conferences/*/tags").access(adminAuthority)
                .antMatchers(HttpMethod.PATCH, "/conferences/*").access(adminAuthority)
                .antMatchers(HttpMethod.GET, "/conferences").permitAll()
                .antMatchers("/").fullyAuthenticated();
        http.servletApi().rolePrefix("");
    }


}
