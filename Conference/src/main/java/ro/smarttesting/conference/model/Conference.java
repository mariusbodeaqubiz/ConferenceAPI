package ro.smarttesting.conference.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import ro.smarttesting.conference.config.config.JsonDateSerializer;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by carmen.sighiartau on 12/30/2016.
 */
@Document(collection = "conferences")
public class Conference {

    @Id
    public String id;

    @NotEmpty(message = "Conference name cannot be empty!")
    public String name;

    @NotNull
    public String theme;

    @Valid
    public Location location;

    @JsonSerialize(using= JsonDateSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public Date startDate;

    @JsonSerialize(using= JsonDateSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public Date endDate;

    @JsonSerialize(using= JsonDateSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public Date c4pStartDate;

    @JsonSerialize(using= JsonDateSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public Date c4pEndDate;

    public List<Speaker> speakers;

    public List<String> tags;

    public List<UserRating> ratings;

    public double avgRating;

    public Location getLocation() {
        return location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getC4pStartDate() {
        return c4pStartDate;
    }

    public Date getC4pEndDate() {
        return c4pEndDate;
    }
}
