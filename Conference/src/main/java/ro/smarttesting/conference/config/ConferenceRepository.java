package ro.smarttesting.conference.config;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ro.smarttesting.conference.model.Conference;

import java.util.List;

/**
 * Created by carmen.sighiartau on 12/30/2016.
 */
@RepositoryRestResource(collectionResourceRel = "conferences", path = "conferences")
public interface ConferenceRepository extends MongoRepository<Conference, String> {

    @Query("{ theme: { $regex: ?0, $options: \"i\" } }")
    public List<Conference> findByTheme(@Param("theme") String theme);

    @Query("{ name: { $regex: ?0, $options: \"i\" } }")
    public List<Conference> findByName(@Param("name") String name);

    @Query("{$or: [{location.city : { $regex: ?0, $options: \"i\" }}, {location.country : { $regex: ?0, $options: \"i\" }}]}")
    public List<Conference> findByLocation(@Param("location") String location);

    @Query("{speakers.name : { $regex: ?0, $options: \"i\" }}")
    public List<Conference> findBySpeaker(@Param("speaker") String speaker);

    @Query("{tags : { $regex: ?0, $options: \"i\" }}, $orderby: {startDate: -1}")
    public List<Conference> findByTag(@Param("tag") String tag);

}
