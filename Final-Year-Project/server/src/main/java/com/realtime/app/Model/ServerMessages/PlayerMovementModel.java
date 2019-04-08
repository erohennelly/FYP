package com.realtime.app.Model.ServerMessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class PlayerMovementModel {
    @JsonProperty("id")
    private String id;
    @JsonProperty("xMovement")
    private double xMovement;
    @JsonProperty("yMovement")
    private double yMovement;
}
