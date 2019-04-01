package com.realtime.app.Model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class FlowerModel extends BasicGameObject {
    public  FlowerModel() {
        xPos = Math.round(Math.random() * 1500);
        yPos = Math.round(Math.random() * 700);
    }

    public boolean checkFlower(PlayerModel player) {
        if (Math.abs(xPos - player.getXPos())  < length &&
                Math.abs(yPos - player.getYPos()) < length) {
            player.eatFlower(this);
            isAlive = false;
            return true;
        }
        return false;
    }
}
