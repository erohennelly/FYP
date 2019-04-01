package com.realtime.app.Service;

import com.google.common.base.Strings;
import com.realtime.app.Model.*;
import com.realtime.app.Model.ServerMessages.PlayerAttackModel;
import com.realtime.app.Model.ServerMessages.PlayerMovementModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class GameService {
    private final GameStateModel gameState;
    private final ArrayList<AiPlayerModel> AIPlayers = new ArrayList<>();
    private final HashMap<UUID, PlayerModel> flowersToAiPlayersMapping = new HashMap<>();
    private final int minFlowerCount = 5;
    private final int minNumPlayers = 3;
    private long millTimeAtLastUpdate = new Date().getTime();

    private String[][] colorsArray = {
            {"#FFFF33", "#f4414a"},
            {"#f4414a", "#ffffff"},
            {"#3a1ed8", "#e4e3ea"},
            {"#7ae283", "#ccca5f"},
            {"#cc905f", "#5f93cc"},
            {"#1714e2", "#ce4e4e"},
            {"#4eceb6", "#ce4ea7"},
    };

    public GameService() {
        gameState = new GameStateModel();
    }

    public PlayerModel addPlayer(String userName) {
        if(!Strings.isNullOrEmpty(userName)) {
            PlayerModel newPlayer = new PlayerModel();

            newPlayer.setXPos(0);
            newPlayer.setYPos(0);
            newPlayer.setUserName(userName);
            newPlayer.setColor(randomColorGenerator());
            newPlayer.setLength(15);
            newPlayer.setPoints(0);

            if (!gameState.getPlayers().containsKey(newPlayer.getUserName())) {
                gameState.getPlayers().put(newPlayer.getId(), newPlayer);
                while (gameState.getPlayers().size() < minNumPlayers) {
                    addAIPlayer();
                }
                return newPlayer;
            }
        }
        return null;
    }

    private void addAIPlayer() {
        AiPlayerModel aiPlayerModel = new AiPlayerModel();
        aiPlayerModel.setColor(randomColorGenerator());

        if (!gameState.getPlayers().containsKey(aiPlayerModel.getUserName())) {
            gameState.getPlayers().put(aiPlayerModel.getId(), aiPlayerModel);
            AIPlayers.add(aiPlayerModel);
        }
    }

    private String[] randomColorGenerator() {
        int randomIndex = (int)(Math.random() * colorsArray.length);
        return colorsArray[randomIndex];
    }

    public Map<UUID, PlayerModel> removePlayer(UUID id) {
        if (id != null && gameState.getPlayers().containsKey(id)){
            gameState.getPlayers().remove(id);
        }
        return gameState.getPlayers();
    }

    public Map<UUID, PlayerModel> updatePlayerPosition(PlayerMovementModel playerMovementModel){
        if (playerMovementModel == null) return gameState.getPlayers();
        UUID parsedId = UUID.fromString(playerMovementModel.getId());
        PlayerModel player = gameState.getPlayers().get(parsedId);
        player.updatePosition(playerMovementModel);
        checkFlowers(player);

        return gameState.getPlayers();
    }

    private void checkFlowers(PlayerModel player) {
        gameState.getFlowers().removeIf(flowerModel -> flowerModel.checkFlower(player));
    }

    private void AIPlayersMove(double delta) {
        AIPlayers.forEach((AiPlayerModel aIplayerModel) -> {
            gameState.getPlayers().forEach((s, playerModel) -> {
                if (aIplayerModel.shouldAttack(playerModel)) {
                    PlayerAttackModel playerAttackModel = new PlayerAttackModel();
                    playerAttackModel.setTarget(playerModel);
                    playerAttackModel.setSender(aIplayerModel);
                    processPlayerAction(playerAttackModel);
                }
            });

            FlowerModel target = aIplayerModel.getTarget();
            FlowerModel closestFlowerToAiPlayer = target != null
                    ? target.isAlive()
                        ? target :
                        returnFlowerClosetToPlayerNotChosenAlready(aIplayerModel)
                    : returnFlowerClosetToPlayerNotChosenAlready(aIplayerModel);

            aIplayerModel.AIPlayersMove(delta, closestFlowerToAiPlayer);
            checkFlowers(aIplayerModel);
        });
    }

    public void processPlayerAction(PlayerAttackModel playerAttackModel) {
        gameState.getBees().add(playerAttackModel.getSender().createBee(playerAttackModel.getTarget()));
    }

    private void beesMoveTowardsTarget(double delta) {
        ArrayList<BeeModel> beesModels = gameState.getBees();
        beesModels.forEach(bee -> {
            bee.beesMoveTowardsTarget(delta);
            bee.checkIfHitPlayer(gameState.getPlayers());
            bee.checkIfHitFlower(gameState.getFlowers());
        });

        beesModels.removeIf(beesModel -> !beesModel.isAlive());
    }

    public FlowerModel returnFlowerClosetToPlayerNotChosenAlready(AiPlayerModel aiPlayerModel) {
        FlowerModel target = null;
        HashMap<FlowerModel, Double> flowerModelDoubleHashMap = new HashMap<>();
        ArrayList<FlowerModel> flowerModelArrayList = (ArrayList<FlowerModel>) gameState.getFlowers().clone();
        flowerModelArrayList.removeIf(flowerModel -> this.flowersToAiPlayersMapping.containsKey(flowerModel.getId()));

        log.info("org {}", gameState.getFlowers().size());
        log.info("new {}", flowerModelArrayList.size());

        flowerModelArrayList.forEach(flowerModel -> {
            double distance = Point2D.distance(flowerModel.getXPos(), flowerModel.getYPos(), aiPlayerModel.getXPos(), aiPlayerModel.getYPos());
            flowerModelDoubleHashMap.put(flowerModel, distance);
        });

        List<Map.Entry<FlowerModel, Double> > list =
                new LinkedList(flowerModelDoubleHashMap.entrySet());

        Collections.sort(list, Comparator.comparing(Map.Entry::getValue));

        Iterator<Map.Entry<FlowerModel, Double>> targets = list.iterator();

        while (targets.hasNext()) {
            FlowerModel nextTarget = targets.next().getKey();
            this.flowersToAiPlayersMapping.put(nextTarget.getId(), aiPlayerModel);
            aiPlayerModel.setTarget(nextTarget);
            target = nextTarget;
            break;
        }

        return target;
    }

    private double getTimeInMillisecondsSinceLastUpdate() {
        long dateNow = new Date().getTime();
        double delta = dateNow - this.millTimeAtLastUpdate;
        this.millTimeAtLastUpdate = dateNow;
        return delta;
    }

    public GameStateModel gameStateUpdate() {
        double delta = getTimeInMillisecondsSinceLastUpdate();
        ArrayList<FlowerModel> flowers = gameState.getFlowers();
        AIPlayersMove(delta);
        beesMoveTowardsTarget(delta);

        while (flowers.size() < minFlowerCount) {
            FlowerModel flowerModel = new FlowerModel();
            flowerModel.setLength(45);
            flowerModel.setPoints(25);
            flowers.add(flowerModel);
        }
        return gameState;
    }

    public GameStateModel getGameState() {
        return gameState;
    }
}
