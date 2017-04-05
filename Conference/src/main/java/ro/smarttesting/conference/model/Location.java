package ro.smarttesting.conference.model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by carmen.sighiartau on 12/30/2016.
 */
public class Location {

    @NotEmpty(message = "Venue name cannot be empty!")
    public String venueName;

    @NotEmpty
    public String city;

    @NotEmpty
    public String country;

    public String url;

    public String getVenueName() {
        return venueName;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
