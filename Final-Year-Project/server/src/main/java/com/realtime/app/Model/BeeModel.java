package com.realtime.app.Model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Data
@Slf4j
public class BeeModel extends BasicGameObject {
    private String[] color;
    private PlayerModel target;
    private PlayerModel creator;

    public void beesMoveTowardsTarget(double delta) {
        if (target.isAlive == false) {
            isAlive = false;
            return;
        }
        double angle = Math.atan2((target.getYPos() - yPos),(target.getXPos() - xPos));
        xPos += (delta/5 * Math.cos(angle));
        yPos += (delta/5 * Math.sin(angle));
    }

    public void checkIfHitFlower(ArrayList<FlowerModel> flowerModels) {
        flowerModels.forEach(flower -> {
            if (Point2D.distance(xPos, yPos, flower.getXPos(), flower.getYPos()) < (length + flower.getLength())) {
                isAlive = false;
            }
        });
    }

    public void checkIfHitPlayer(HashMap<UUID, PlayerModel> players) {
        players.values().forEach(player -> {
            Double minDistance = length + player.getLength() > 20 ?  length + player.getLength() : 20;

            if (!player.equals(creator) && Point2D.distance(xPos, yPos, player.getXPos(), player.getYPos()) < minDistance) {
                player.playerHit(points, length);
                isAlive = false;
            }
        });
    }
}
