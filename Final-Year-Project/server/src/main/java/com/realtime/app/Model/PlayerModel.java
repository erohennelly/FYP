package com.realtime.app.Model;

import com.realtime.app.Model.ServerMessages.PlayerMovementModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Data
@Table(name="users")
@Entity
public class PlayerModel extends BasicGameObject {
    @Id
    protected String userName;
    private String password;
    protected boolean isAlive = true;
    protected String[] color;
    protected double totalPoints;

    public void updatePosition(PlayerMovementModel positionUpdate){
        xPos += positionUpdate.getXMovement();
        yPos += positionUpdate.getYMovement();

        if (xPos < 0) {
            xPos = 0;
        } else if (xPos > 1500) {
            xPos = 1500;
        }

        if (yPos < 0) {
            yPos = 0;
        } else if (yPos > 700) {
            yPos = 700;
        }
    }

    public void playerHit(double points, double length) {
        this.points -= points;
        this.length -= length;

        if (this.points < 1 && this.length < 1) {
            this.isAlive = false;
        }
    }

    public BeeModel createBee(PlayerModel target) {
        double halfPoints = this.getPoints() / 2;
        double halfLength = this.getLength() / 2;
        this.length = halfLength;
        this.points = halfPoints;

        BeeModel bee = new BeeModel();
        bee.setColor(this.color);
        bee.setLength(halfLength);
        bee.setPoints(halfPoints);
        bee.setXPos(this.xPos);
        bee.setYPos(this.yPos);
        bee.setTarget(target);
        bee.setCreator(this);
        return bee;
    }

    public void eatFlower(BasicGameObject flower) {
        this.points = this.points + flower.getPoints();
        this.length = this.length + 5;
        if (this.length > 100) {
            this.length = 100;
        }
    }
}
