package com.flopr.surveysbackend.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity(name = "poll_reply_detail")
@Data
public class PollReplyDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long questionId;

    private long answerId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "poll_reply_id")
    private PollReplyEntity pollReply;
}
