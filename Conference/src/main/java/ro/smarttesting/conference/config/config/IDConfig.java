package ro.smarttesting.conference.config.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import ro.smarttesting.conference.model.Conference;

/**
 * Created by andreicontan on 23/01/2017.
 */
@Configuration
public class IDConfig extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        super.configureRepositoryRestConfiguration(config);
        config.exposeIdsFor(Conference.class);
    }
}
