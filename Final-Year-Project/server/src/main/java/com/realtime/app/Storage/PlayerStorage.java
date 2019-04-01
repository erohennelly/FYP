package com.realtime.app.Storage;

import com.realtime.app.Model.PlayerModel;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class PlayerStorage {

    private static Map<String, PlayerModel> playerRecords;

    static {
        playerRecords = new HashMap<String, PlayerModel>() {
            {
                PlayerModel playerModel = new PlayerModel();
                playerModel.setId(UUID.fromString("b91d1b6a-8ffb-4c42-8007-830adfc7b4c6"));
                playerModel.setPassword("password");
                put("username", playerModel);
            }
        };
    }

    public Collection<PlayerModel> getAllPlayers(){
        return playerRecords.values();
    }

    public PlayerModel getPlayerById(String id){
        return playerRecords.get(id);
    }
}
