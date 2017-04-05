package ro.smarttesting.conference.config.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by andreicontan on 06/01/2017.
 */
@Component
public class Roles {


    public final String ADMIN;

    @Autowired
    public Roles(Environment env){
        ADMIN = env.getProperty("stormpath.authorized.group.admin");
    }
}
