package ro.smarttesting.conference.api;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ro.smarttesting.conference.model.Conference;
import ro.smarttesting.conference.model.Rating;
import ro.smarttesting.conference.model.Speaker;
import ro.smarttesting.conference.model.UserRating;
import ro.smarttesting.conference.service.AdminService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by carmen.sighiartau on 12/31/2016.
 */
@RestController
public class ConferenceController {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    AdminService adminService;

    @Autowired
    private AccountResolver accountResolver;


    @ApiOperation(value="findByC4PDate", nickname="findByC4PDate")
    @ApiImplicitParams({
            @ApiImplicitParam(name= "c4PDate", value="c4PDate", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Conference.class),
//            @ApiResponse(code = 401, message = "Unauthorized"),
//            @ApiResponse(code = 403, message = "Forbidden"),
//            @ApiResponse(code = 404, message = "Not Found"),
//            @ApiResponse(code = 500, message = "Failure")
    })
    @RequestMapping(value = "/conferences/search/findByC4PDate", method = RequestMethod.GET)
    public List<Conference> findByC4PDate(@Param("c4PDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date c4PDate){
        Query query = new Query(
                where("c4pStartDate").lte(c4PDate).andOperator(where("c4pEndDate").gte(c4PDate)));
        return mongoTemplate.find(query,
                Conference.class);
    }

    @ApiOperation(value="findConferenceByDate", nickname="findConferenceByDate")
    @ApiImplicitParams({
            @ApiImplicitParam(name= "date", value="date", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Conference.class),
//            @ApiResponse(code = 401, message = "Unauthorized"),
//            @ApiResponse(code = 403, message = "Forbidden"),
//            @ApiResponse(code = 404, message = "Not Found"),
//            @ApiResponse(code = 500, message = "Failure")
    })
    @RequestMapping(value = "/conferences/search/findByDate", method = RequestMethod.GET)
    public List<Conference> findByDate(@Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date){
        Query query = new Query(
                where("startDate").lte(date).andOperator(where("endDate").gte(date)));
        return mongoTemplate.find(query,
                Conference.class);
    }

    @ApiOperation(value="deleteSpeaker", nickname="deleteSpeaker")
    @ApiImplicitParams({
            @ApiImplicitParam(name= "name", value="speaker name", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Conference.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})

    @RequestMapping(value = "/conferences/{conferenceId}/speakers", method = RequestMethod.DELETE)
    public void deleteSpeakerByName(@PathVariable("conferenceId") String conferenceId, @Param("name") String name
            , HttpServletResponse response) throws IOException {
        System.out.println("lolo: "+name);
        adminService.ensureAdmin();
        Conference conference = mongoTemplate.findById(conferenceId, Conference.class);
        if(conference != null) {
            for(Speaker speaker: conference.speakers) {
                if(speaker.name.equalsIgnoreCase(name)) {
                    conference.speakers.remove(speaker);
                    mongoTemplate.save(conference);
                    return;
                }
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Speaker with name " + name + " not found");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Conference with id " + conferenceId + " not found");
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Conference.class),
            })
    @ApiOperation(value="deleteTag", nickname="deleteTag")
    @ApiImplicitParams({
            @ApiImplicitParam(name= "tag", value="tag", required = true)
    })
    @RequestMapping(value = "/conferences/{conferenceId}/tags", method = RequestMethod.DELETE)
    public void deleteTag(@PathVariable("conferenceId") String conferenceId, @Param("tag") String tag
            , HttpServletResponse response) throws IOException {
        adminService.ensureAdmin();
        Conference conference = mongoTemplate.findById(conferenceId, Conference.class);
        if(conference != null) {
            for(String conferenceTag: conference.tags) {
                if(conferenceTag.equalsIgnoreCase(tag)) {
                    conference.tags.remove(conferenceTag);
                    mongoTemplate.save(conference);
                    return;
                }
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tag " + tag + " not found");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Conference with id " + conferenceId + " not found");
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Conference.class),
//            @ApiResponse(code = 401, message = "Unauthorized"),
//            @ApiResponse(code = 403, message = "Forbidden"),
//            @ApiResponse(code = 404, message = "Not Found"),
//            @ApiResponse(code = 500, message = "Failure")
 })
    @ApiOperation(value="addConferenceRating", nickname="add    RatingToConference")
    @ApiImplicitParams({
            @ApiImplicitParam(name= "rating", value="rating", required = true)
    })
    @RequestMapping(value = "/conferences/{conferenceId}/ratings", method = RequestMethod.PATCH)
    public void addUserRating(@PathVariable("conferenceId") String conferenceId, @RequestBody final Rating rating
            , HttpServletRequest request, HttpServletResponse response) throws IOException {
        Conference conference = mongoTemplate.findById(conferenceId, Conference.class);
        Account account = accountResolver.getAccount(request);
        if(conference != null) {
            if(conference.ratings != null) {
                Optional<UserRating> userRating =
                        conference.ratings.stream()
                                .filter(r -> r.user.equalsIgnoreCase(account.getUsername()))
                                .findFirst();
                if(userRating.isPresent()){
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Duplicate rating for user "
                            + account.getUsername());
                    return;
                }
            } else {
                conference.ratings = new ArrayList<>();
            }
            UserRating userRating = new UserRating();
            userRating.user = account.getUsername();
            userRating.rating = rating.rating;
            userRating.comment = rating.comment;
            conference.ratings.add(userRating);
            conference.avgRating =
                conference.ratings.stream().mapToDouble(f -> f.rating).sum()
                / conference.ratings.size();
            mongoTemplate.save(conference);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Conference with id " + conferenceId + " not found");
        }


    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Conference.class),
            })
    @ApiOperation(value="addSpeaker", nickname="addSpeakerToConference")
    @ApiImplicitParams({
            @ApiImplicitParam(name= "speaker", value="name", required = true)
    })

    @RequestMapping(value = "/conferences/{conferenceId}/speakers", method = RequestMethod.PATCH)
    public void addSpeaker(@PathVariable("conferenceId") String conferenceId, @RequestBody final Speaker speaker
            , HttpServletResponse response) throws IOException {
        adminService.ensureAdmin();
        Conference conference = mongoTemplate.findById(conferenceId, Conference.class);
        if(conference != null) {
            if(conference.speakers != null) {
                Optional<Speaker> speakerOptional =
                        conference.speakers.stream()
                                .filter(r -> r.name.equalsIgnoreCase(speaker.name))
                                .findFirst();
                if(speakerOptional.isPresent()){
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Duplicate speaker with name " + speaker.name);
                    return;
                }
            } else {
                conference.speakers = new ArrayList<>();
            }
            conference.speakers.add(speaker);
            mongoTemplate.save(conference);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Conference with id " + conferenceId + " not found");
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Conference.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})

    @RequestMapping(value = "/conferences/{conferenceId}/tags", method = RequestMethod.PATCH)
    @ApiOperation(value="addTag", nickname="addTagToConference")
    @ApiImplicitParams({
            @ApiImplicitParam(name= "tag", value="tag", required = true)
    })
    public void addTag(@PathVariable("conferenceId") String conferenceId, @RequestBody final String tag
            , HttpServletResponse response) throws IOException {
        adminService.ensureAdmin();
        Conference conference = mongoTemplate.findById(conferenceId, Conference.class);
        if(conference != null) {
            if(conference.tags != null) {
                Optional<String> tagOptional =
                        conference.tags.stream()
                                .filter(r -> r.equalsIgnoreCase(tag))
                                .findFirst();
                if(tagOptional.isPresent()){
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Duplicate tag  " + tag);
                    return;
                }
            } else {
                conference.speakers = new ArrayList<>();
            }
            conference.tags.add(tag);
            mongoTemplate.save(conference);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Conference with id " + conferenceId + " not found");
        }
    }

    @RequestMapping(value = "/conferences/{conferenceId}", method = RequestMethod.DELETE)
    public void deleteConference(@PathVariable("conferenceId") String conferenceId
            , HttpServletResponse response) throws IOException {
        adminService.ensureAdmin();
        Conference conference = mongoTemplate.findById(conferenceId, Conference.class);
        if(conference != null) {
            mongoTemplate.remove(conference);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Conference with id " + conferenceId + " not found");
        }
    }

    @RequestMapping(value = "/conferences/{conferenceId}", method = RequestMethod.GET)
    public Conference getConference(@PathVariable("conferenceId") String conferenceId
            , HttpServletResponse response) throws IOException {
        Conference conference = mongoTemplate.findById(conferenceId, Conference.class);
        if(conference == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Conference with id " + conferenceId + " not found");
        }
        return conference;
    }

    @RequestMapping(value = "/conferences", method = RequestMethod.POST)
    public Conference addConference(@Valid @RequestBody Conference conference
            , HttpServletResponse response) throws IOException {
        adminService.ensureAdmin();
        if(conference.getStartDate().compareTo(conference.getEndDate()) > 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "startDate cannot be after endDate");
        } else
        if(conference.getC4pStartDate().compareTo(conference.getC4pEndDate()) > 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "c4pStartDate cannot be after c4PEndDate");
        } else {
            mongoTemplate.insert(conference);
        }
        return conference;
    }


    @RequestMapping(value = "/conferences", method = RequestMethod.GET)
    public List<Conference> getAllConferences() throws IOException {
        return mongoTemplate.findAll(Conference.class, "conferences");
    }


//    @InitBinder
//    public void initBinder(WebDataBinder binder){
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
//    }
}
