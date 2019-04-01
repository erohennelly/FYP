package com.realtime.app.controller;

import com.corundumstudio.socketio.SocketIOServer;
import com.realtime.app.Controller.GameController;
import com.realtime.app.RealTimeServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Slf4j
@SpringBootTest(classes = RealTimeServerApplication.class)
@TestPropertySource(locations = "classpath:application-integration.properties")
@RunWith(SpringRunner.class)
public class GameControllerIT {
    @Autowired
    SocketIOServer socketIOServer;

    @Autowired
    private GameController gameController;

    @Test
    public void test() {
        //Todo: write tests for this
    }
}
