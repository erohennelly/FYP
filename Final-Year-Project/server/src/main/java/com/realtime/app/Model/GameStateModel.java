package com.realtime.app.Model;

import com.realtime.app.Model.ServerMessages.ServerResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Data
public class GameStateModel {
    private HashMap<UUID, PlayerModel> players;
    private ArrayList<FlowerModel> flowers;
    private ArrayList<BeeModel> bees;

    public GameStateModel() {
        players = new HashMap<>();
        flowers = new ArrayList<>();
        bees = new ArrayList<>();
    }

    public ServerResponse socketMessage() {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setFlowers(flowers);
        serverResponse.setBees(bees);
        ArrayList<PlayerModel> playerModels = new ArrayList<>();
        players.values()
                .stream()
                .filter(playerModel -> playerModel.isAlive())
                .forEach(item -> playerModels.add(item));

        serverResponse.setPlayers(playerModels);
        return serverResponse;
    }
}
