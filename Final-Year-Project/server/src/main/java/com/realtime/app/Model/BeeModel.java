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
        double angle = Math.atan2((target.getYPos() - yPos),(target.getXPos() - xPos));
        xPos += (delta/5 * Math.cos(angle));
        yPos += (delta/5 * Math.sin(angle));
    }

    public void checkIfReachedTarget() {
        if (Math.abs(yPos - target.getYPos()) < length &&
                Math.abs(xPos - target.getXPos()) < length) {
            target.playerHit(points, length);
            isAlive = false;
        }
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
            if (!player.equals(creator) && Point2D.distance(xPos, yPos, player.getXPos(), player.getYPos()) < (length + player.getLength())) {
                player.playerHit(points, length);
                isAlive = false;
            }
        });
    }
}
