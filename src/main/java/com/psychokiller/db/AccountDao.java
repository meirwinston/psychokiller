package com.psychokiller.db;

import com.psychokiller.db.entities.Account;
import org.skife.jdbi.v2.sqlobject.*;

public interface AccountDao {
    @SqlQuery("select id from accounts where username = :username and password = :password")
    long getAccountId(@Bind("username") String username, @Bind("password") String password);

    @GetGeneratedKeys
    @SqlUpdate("insert into accounts(username, password, phoneNumber, countryCode, createdDate) values (:username, :password, :phoneNumber, :countryCode, :createdDate)")
    long insert(@BindBean Account req);
}
