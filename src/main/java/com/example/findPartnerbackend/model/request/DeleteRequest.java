package com.example.findPartnerbackend.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = -3545020171561123128L;
    private long id;
}
