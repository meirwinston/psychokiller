package com.psychokiller.db;

import com.psychokiller.wire.messages.Account;
import org.skife.jdbi.v2.sqlobject.*;

public interface AccountDao {
    @SqlQuery("select id from accounts where phoneNumber = :phoneNumber")
    long getAccountId(@Bind("phoneNumber") String phoneNumber);

    @GetGeneratedKeys
    @SqlUpdate("insert into accounts(phoneNumber, timestamp) values (:phoneNumber, :createdTime)")
    long insert(@BindBean Account req);
}
