package com.psychokiller.ws.resources;

import com.google.inject.Inject;
import com.neovisionaries.i18n.CountryCode;
import com.psychokiller.db.entities.Account;
import com.psychokiller.ws.beans.LoginRequest;
import com.psychokiller.ws.beans.LoginResponse;
import com.psychokiller.ws.beans.SignupRequest;
import com.psychokiller.ws.beans.SignupResponse;
import com.psychokiller.db.AccountDao;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Enumeration;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private static final Logger logger = LoggerFactory.getLogger(AuthResource.class);

    private DBI dbi;

    @Inject
    public AuthResource(DBI dbi) {
        this.dbi = dbi;
    }

    @Path("login")
    @POST
    public LoginResponse login(LoginRequest loginRequest, @Context HttpServletRequest request) {
        Enumeration<String> e = request.getHeaderNames();
        while(e.hasMoreElements()){
            String key = e.nextElement();
            logger.info("HEADER: {}:{}", key, request.getHeader(key));
        }
        long id = dbi.open(AccountDao.class).getAccountId(loginRequest.getUsername(), loginRequest.getPassword());
        if (id <= 0) {
            throw new NotAuthorizedException("Incorrect username and/or password");
        }
        request.getSession().setAttribute("accountId",id);
        return new LoginResponse(request.getSession().getId());
    }

    @Path("signup")
    @POST
    public SignupResponse signup(SignupRequest signupRequest, @Context HttpServletRequest request) {
        Account account = new Account();
        account.setCreatedDate(new DateTime());
        account.setCountryCode(CountryCode.valueOf(signupRequest.getCountryCode()));
        account.setPassword(signupRequest.getPassword());
        account.setPhoneNumber(signupRequest.getPhoneNumber());
        account.setUsername(signupRequest.getUsername());
        long id = dbi.open(AccountDao.class).insert(account);
        if (id <= 0) {
            throw new WebApplicationException("Could not insert");
        }

        return new SignupResponse(id);
    }
}