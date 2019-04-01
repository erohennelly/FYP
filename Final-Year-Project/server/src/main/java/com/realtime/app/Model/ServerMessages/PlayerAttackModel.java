package com.realtime.app.Model.ServerMessages;

import com.realtime.app.Model.PlayerModel;
import lombok.Data;

@Data
public class PlayerAttackModel {
    private PlayerModel sender;
    private PlayerModel target;
}
