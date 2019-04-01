package com.realtime.app.Model.ServerMessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ServerMessage {
    @JsonProperty("movement")
    private PlayerMovementModel playerMovementModel;
    @JsonProperty("attack")
    private PlayerAttackModel playerAttackModel;
}
