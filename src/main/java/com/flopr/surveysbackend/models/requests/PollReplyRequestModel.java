package com.flopr.surveysbackend.models.requests;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class PollReplyRequestModel {

    @NotEmpty
    private String user;

    @NotNull
    @Positive
    private long poll;

    @Valid
    @NotEmpty
    private List<PollReplyDetailRequestModel> pollReplies;
}
