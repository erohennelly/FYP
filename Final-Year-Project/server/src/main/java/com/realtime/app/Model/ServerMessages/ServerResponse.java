package com.realtime.app.Model.ServerMessages;

import com.realtime.app.Model.BeeModel;
import com.realtime.app.Model.FlowerModel;
import com.realtime.app.Model.PlayerModel;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ServerResponse {
    private ArrayList<PlayerModel> players;
    private ArrayList<FlowerModel> flowers;
    private ArrayList<BeeModel> bees;
}
