package com.example.findPartnerbackend.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -2294285447999716550L;

    /**
     *
     */
    private Long teamId;
}
