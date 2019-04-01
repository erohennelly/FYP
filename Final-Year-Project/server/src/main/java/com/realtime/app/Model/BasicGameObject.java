package com.realtime.app.Model;

import lombok.Data;

import java.util.UUID;

@Data
public class BasicGameObject {
    protected double xPos;
    protected double yPos;
    protected double length;
    protected UUID id = UUID.randomUUID();
    protected double points;
    protected boolean isAlive = true;
}
