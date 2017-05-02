package com.psychokiller.ws;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import io.dropwizard.validation.ConstraintViolations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.*;

/**
 * @author Meir Winston
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

    private static final Splitter LINE_SPLITTER = Splitter.on("\n").trimResults();
    private static final String DETAILS = "details";
    public static final String INTERNAL_SERVER_ERROR_KEY = "internal.server.error";
    public static final String CONSTRAINT_VALIDATION_ERROR_KEY = "validation.failed";
    public static final String METHOD_NOT_ALLOWED_ERROR_KEY = "method.not.allowed";
    public static final String TOO_MANY_REQUESTS_ERROR_KEY = "too.many.requests";
    public static final String UNKNOWN_WEB_APP_ERROR_KEY = "unknown.error.type";


    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(Throwable t) {
        int statusCode = -1;
        String message = t.getMessage();

        Map<String, Object> responseMap = new HashMap<String, Object>();

        if (t instanceof ConstraintViolationException) {
            ConstraintViolationException iee = (ConstraintViolationException) t;
            statusCode = 422;
            message = CONSTRAINT_VALIDATION_ERROR_KEY;
            ImmutableList<String> errors = ConstraintViolations.formatUntyped(iee.getConstraintViolations());
            addDetails(errors, responseMap);
            logger.debug("Validation error(s) from the client '{}'", errors);

        } else if (t instanceof JsonGenerationException) {
            logger.warn("Error generating JSON", t);
            message = INTERNAL_SERVER_ERROR_KEY;
            statusCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        } else if (t instanceof JsonProcessingException) {
            JsonProcessingException jpe = (JsonProcessingException) t;

            if (message.startsWith("No suitable constructor found")) {
                String guid = UUID.randomUUID().toString();
                message = String.format("Error ID: %s - Unable to deserialize the specific type", guid);
                logger.error(message, jpe);
                statusCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
                addDetails(String.format("See error ID: %s", guid), responseMap);

            } else {
                logger.debug("Unable to process JSON", jpe);
                statusCode = Response.Status.BAD_REQUEST.getStatusCode();
                message = stripLocation(message);
            }

            // Web application exception is created SOMEWHERE and I've not been able to track down how it's getting
            // turned into HTML automatically. The data is lost in that conversion so until we figure that out,
            // we're stuck with this nasty hack.
        } else if (t instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) t;
            statusCode = wae.getResponse().getStatus();
            Response.Status fromCode = Response.Status.fromStatusCode(statusCode);
            if (fromCode != null) {
                message = fromCode.toString();
            } else if (statusCode == 405) {
                message = METHOD_NOT_ALLOWED_ERROR_KEY;
                addDetails("Likely caused by using the incorrect HTTP verb", responseMap);
            } else if (statusCode == 429) {
                message = TOO_MANY_REQUESTS_ERROR_KEY;
                String guid = UUID.randomUUID().toString();
                logger.warn(String.format("Error ID: %s - Too many requests from %s", guid, wae.getResponse().getMetadata().get("details").get(0)));
                addDetails(String.format("See error ID: %s", guid), responseMap);
            } else {
                message = UNKNOWN_WEB_APP_ERROR_KEY;
                String guid = UUID.randomUUID().toString();
                logger.error(String.format("Error ID: %s - WebApplicationException with null message caught", guid), wae);
                addDetails(String.format("See error ID: %s", guid), responseMap);
            }

            // Catch all for anything else unhandled
        } else {
            String guid = UUID.randomUUID().toString();
            logger.error(String.format("Error ID: %s - Unhandled exception was caught by the GenericExceptionMapper", guid), t);
            statusCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            message = t.getMessage();
            addDetails(String.format("See error ID: %s", guid), responseMap);
        }

        // Put the message in the map
        responseMap.put("message", message);

        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(responseMap)
                .build();
    }

    private void addDetails(String details, Map<String, Object> map) {
        if(details == null || details.length() == 0) {
            return;
        }
        List<String> list = new ArrayList<>();
        list.add(details);
        addDetails(list, map);
    }

    private void addDetails(List<String> details, Map<String, Object> map) {
        if(details == null || details.size() == 0) {
            return;
        }
        map.put(DETAILS, details);
    }

    private void addExtraFields(Map<String,Object> fields, Map<String,Object> responseMap) {
        if(fields == null || fields.isEmpty()) {
            return;
        }
        for(Map.Entry<String,Object> field : fields.entrySet()) {
            responseMap.put(field.getKey(), field.getValue());
        }
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private String stripLocation(String message) {
        for (String s : LINE_SPLITTER.split(message)) {
            return s;
        }
        return message;
    }
}
