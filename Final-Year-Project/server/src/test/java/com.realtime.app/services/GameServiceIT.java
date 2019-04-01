package com.realtime.app.services;

import com.realtime.app.Model.BasicGameObject;
import com.realtime.app.Model.GameStateModel;
import com.realtime.app.Model.ServerMessages.PlayerAttackModel;
import com.realtime.app.Model.PlayerModel;
import com.realtime.app.Model.ServerMessages.PlayerMovementModel;
import com.realtime.app.RealTimeServerApplication;
import com.realtime.app.Service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RealTimeServerApplication.class)
@TestPropertySource(locations = "classpath:application-integration.properties")
public class GameServiceIT {
    @Autowired
    private GameService gameService;
    private static String username = "username";

    //add method
    @Test
    public void testAddPlayer() {
        PlayerModel playerModel = gameService.addPlayer(username);
        assertEquals(username, playerModel.getUserName());
        assertNull(playerModel.getPassword());
        assertNotNull(playerModel.getId());
        assertNotNull(playerModel.getXPos());
        assertNotNull(playerModel.getYPos());
        assertEquals(0, playerModel.getPoints());
        assertTrue(gameService.gameStateUpdate().getPlayers().containsKey(username));
        assertEquals(5, gameService.gameStateUpdate().getPlayers().size());
    }

    @Test
    public void testAddPlayerDup() {
        String newUserName = "thisUser";
        assertNotNull(gameService.addPlayer(newUserName));
        assertTrue(gameService.gameStateUpdate().getPlayers().containsKey(newUserName));
        assertNull(gameService.addPlayer(newUserName));
        assertTrue(gameService.gameStateUpdate().getPlayers().containsKey(newUserName));
    }

    @Test
    public void testAddPlayerNullUsername() {
        assertNull(gameService.addPlayer(null));
        assertFalse(gameService.gameStateUpdate().getPlayers().containsKey(null));
    }

    @Test
    public void testAddPlayerEmptyString() {
        assertNull(gameService.addPlayer(""));
        assertFalse(gameService.gameStateUpdate().getPlayers().containsKey(""));
    }

    //remove method
    @Test
    public void testRemovePlayer() {
        UUID id = gameService.addPlayer(username).getId();
        assertTrue(gameService.gameStateUpdate().getPlayers().containsKey(id));

        gameService.removePlayer(id);
        assertFalse(gameService.gameStateUpdate().getPlayers().containsKey(id));
    }

    @Test
    public void testRemovePlayerNotExisted() {
        gameService.removePlayer(UUID.randomUUID());
        assertFalse(gameService.gameStateUpdate().getPlayers().containsKey(username));
    }

    @Test
    public void testRemovePlayerNullId() {
        assertNotNull(gameService.removePlayer(null));
    }

    @Test
    public void testUpdatePlayerPosition() {
        PlayerModel playerModel = gameService.addPlayer(username);
        double xPos = playerModel.getXPos();
        double yPos = playerModel.getXPos();

        PlayerMovementModel playerMovementModel = new PlayerMovementModel();
        playerMovementModel.setId(playerModel.getId().toString());
        playerMovementModel.setXMovement(1);
        playerMovementModel.setYMovement(0);

        PlayerModel currentPlayerModel = gameService.updatePlayerPosition(playerMovementModel).get(username);
        assertEquals(1, (currentPlayerModel.getXPos() - xPos));
        assertEquals(currentPlayerModel.getYPos(), yPos);
    }

    @Test
    public void testUpdatePlayerPositionNull() {
        String playerUsername = "testUpdatePlayerPositionNull";
        PlayerModel playerModel = gameService.addPlayer(playerUsername);
        double xPos = playerModel.getXPos();
        double yPos = playerModel.getYPos();

        PlayerModel currentPlayerModel = gameService.updatePlayerPosition(null).get(playerUsername);
        log.info(String.valueOf(currentPlayerModel));
        assertEquals(currentPlayerModel.getXPos(), xPos);
        assertEquals(currentPlayerModel.getYPos(), yPos);
    }

    @Test
    public void testGetGameState() {
        UUID id1 = gameService.addPlayer("player1").getId();
        UUID id2 = gameService.addPlayer("player2").getId();
        UUID id3 = gameService.addPlayer("player3").getId();

        Map<UUID, PlayerModel> playerModelMap = gameService.gameStateUpdate().getPlayers();

        assertTrue(playerModelMap.containsKey(id1));
        assertTrue(playerModelMap.containsKey(id2));
        assertTrue(playerModelMap.containsKey(id3));
    }

    @Test
    public void testGameStateUpdate() {
        String playerUsername = "username";
        int minFlowers = 5;

        PlayerModel playerModel = gameService.addPlayer(playerUsername);
        GameStateModel gameStateModel = gameService.gameStateUpdate();
        PlayerMovementModel playerMovementModel = new PlayerMovementModel();
        BasicGameObject flowerModel = gameStateModel.getFlowers().get(0);
        playerMovementModel.setId(playerModel.getId().toString());
        playerMovementModel.setXMovement((int) (playerModel.getXPos() + flowerModel.getXPos()));
        playerMovementModel.setYMovement((int) (playerModel.getYPos() + flowerModel.getYPos()));
        ArrayList<BasicGameObject> cloneOfFlowers = (ArrayList<BasicGameObject>) gameService.gameStateUpdate().getFlowers().clone();
        cloneOfFlowers.remove(flowerModel);

        assertEquals(gameStateModel.getFlowers().size(), minFlowers);

        gameService.updatePlayerPosition(playerMovementModel);

        assertEquals(gameStateModel.getFlowers().size(), minFlowers - 1);

        gameService.gameStateUpdate();

        cloneOfFlowers.forEach(flower -> assertTrue(gameStateModel.getFlowers().contains(flower)));
        assertNotEquals(playerModel.getPoints(), 0);
        assertEquals(playerModel.getPoints(), flowerModel.getPoints());
        assertEquals(gameStateModel.getFlowers().size(), minFlowers);
    }

//    @Test
//    public void testBees() {
//        //TODO improve this test
//        String playerUsername = "username";
//        int minFlowers = 5;
//
//        PlayerModel playerModel = gameService.addPlayer(playerUsername);
//        playerModel.setXPos(50);
//        playerModel.setYPos(50);
//        GameStateModel gameStateModel = gameService.gameStateUpdate();
//        PlayerMovementModel playerMovementModel = new PlayerMovementModel();
//
//        Set<UUID> playerModelHashMap = gameService.gameStateUpdate().getPlayers().keySet();
//        String AiPlayerUserName = null;
//        Iterator<UUID> playerModelIterator = playerModelHashMap.iterator();
//        while(playerModelIterator.hasNext()) {
//            UUID currentPlayerId = playerModelIterator.next();
//            if (currentPlayerUsername.contains("AI-User")){
//                AiPlayerUserName = currentPlayerUsername;
//            }
//        }
//        PlayerModel target = gameService.gameStateUpdate().getPlayers().get(AiPlayerUserName);
//
//        PlayerAttackModel playerAttackModel = new PlayerAttackModel();
//        playerAttackModel.setSender(playerModel);
//        playerAttackModel.setTarget(target);
//
//        gameService.processPlayerAction(playerAttackModel);
//
//        gameService.gameStateUpdate();
//
//        log.info(String.valueOf(gameService.gameStateUpdate().getBees()));
//
//        gameService.gameStateUpdate();
//
//        log.info(String.valueOf(gameService.gameStateUpdate().getBees()));
//    }

//    @Test
//    public void testAiPlayerMove() {
//        String playerUsername = "username";
//        PlayerModel playerModel = gameService.addPlayer(playerUsername);
//        gameService.gameStateUpdate();
//        Iterator<String> it = gameService.gameStateUpdate().getPlayers().keySet().iterator();
//
//        String usernameAIPlayer = null;
//
//        while (it.hasNext()) {
//            String itUsername = it.next();
//            if (itUsername.contains("AI-User-")) {
//              usernameAIPlayer = itUsername;
//            }
//        }
//
//
//        gameService.gameStateUpdate();
//
//    }
}
