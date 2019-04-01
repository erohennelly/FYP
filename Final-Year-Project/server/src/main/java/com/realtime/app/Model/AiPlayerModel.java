package com.realtime.app.Model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.geom.Point2D;

@Data
@Slf4j
public class AiPlayerModel extends PlayerModel {
    private  FlowerModel target = null;

    public AiPlayerModel() {
        this.userName = "AI-User-" + Math.round(Math.random() * 1000);
        this.xPos = (Math.random() * 1500);
        this.yPos = (Math.random() * 700);
        this.points = 0;
        this.length = 15;
    }

    public void AIPlayersMove(double delta, FlowerModel closestFlowerToAiPlayer) {
        if (closestFlowerToAiPlayer != null) {
            double angle = Math.atan2((closestFlowerToAiPlayer.getYPos() - yPos), (closestFlowerToAiPlayer.getXPos() - xPos));
            xPos = xPos + (delta/50 * Math.cos(angle));
            yPos = yPos + (delta/50 * Math.sin(angle));
        }
    }

    public boolean shouldAttack(PlayerModel playerModel) {
        if (playerModel.equals(this)) {
            double distance = Point2D.distance(playerModel.getXPos(), playerModel.getYPos(), xPos, yPos);
            return distance < 100 && playerModel.getPoints() < points;
        }
        return false;
    }
}
