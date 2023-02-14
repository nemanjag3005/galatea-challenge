package com.quickstep.backend.model;

import org.jooq.Attachable;
import org.jooq.UpdatableRecord;

import com.quickstep.jooq.generated.enums.ContactJobStatus;

public interface ContactJob<R extends UpdatableRecord<R>>  extends Attachable, UpdatableRecord<R> {

    ContactJobStatus getStatus();

    void setStatus(ContactJobStatus status);

    String getServerResponse();

    void setServerResponse(String serverResponse);

    Long getId();

    String getSid();

    void setSid(String sid);

    void markUpdated();

}
