package com.realtime.app.Controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.realtime.app.Model.ServerMessages.ServerMessage;
import com.realtime.app.Service.GameService;
import com.corundumstudio.socketio.listener.DataListener;
import com.realtime.app.Model.PlayerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.HashMap;
import java.util.TimerTask;

@Slf4j
@Component
public class GameController {

        @Autowired
        private GameService gameService;
        private final SocketIONamespace namespace;
        private HashMap<SocketIOClient, PlayerModel> clientIdToPlayerMapping = new HashMap<>();

        @Autowired
        public GameController(SocketIOServer server) {
        this.namespace = server.addNamespace("/game");
        this.namespace.addEventListener("playerJoined", String.class, processPlayerJoined());
        this.namespace.addEventListener("updatePlayerPosition", ServerMessage.class, processPlayerMovement());
//        this.namespace.addEventListener("playerLeft", String.class, processsPlayerLeft());
        this.namespace.addDisconnectListener(onDisconnected());
        startGame();
        }

    private DataListener<String> processPlayerJoined() {
            return (client, userName, ackSender) -> {
                PlayerModel player = gameService.addPlayer(userName);
                if(player != null) {
                    client.sendEvent("setPlayer", player);
                    clientIdToPlayerMapping.put(client, player);
                }
                namespace.getBroadcastOperations().sendEvent("mapUpdate", gameService.getGameState().socketMessage());
            };
        };

        private DataListener<ServerMessage> processPlayerMovement() {
            return (client, data, ackSender) -> {
                if (data.getPlayerMovementModel() != null)
                    gameService.updatePlayerPosition(data.getPlayerMovementModel());
                if (data.getPlayerAttackModel() != null)
                    gameService.processPlayerAction(data.getPlayerAttackModel());

                namespace.getBroadcastOperations().sendEvent("mapUpdate", gameService.getGameState().socketMessage());
            };
        };

//    private DataListener<String> processsPlayerLeft() {
//        return (client, userName, ackRequest) -> {
//            gameService.removePlayer(userName);
//
//            namespace.getBroadcastOperations().sendEvent("mapUpdate", gameService.getGameState().socketMessage());
//        };
//    };

    private DisconnectListener onDisconnected() {
        return client -> {
            PlayerModel playerModel = clientIdToPlayerMapping.get(client);
            if (playerModel != null) {
                gameService.removePlayer(playerModel.getId());
            }
        };
    }

    private void startGame() {
        Timer t = new Timer(200, e -> namespace.getBroadcastOperations()
                .sendEvent("mapUpdate", gameService.gameStateUpdate().socketMessage()));

        java.util.Timer tt = new java.util.Timer(false);
        tt.schedule(new TimerTask() {
            @Override
            public void run() {
                t.start();
            }
        }, 0);
    }
}
