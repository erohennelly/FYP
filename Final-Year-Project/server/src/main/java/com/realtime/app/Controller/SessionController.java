package com.realtime.app.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtime.app.Model.JwtResponse;
import com.realtime.app.Model.PlayerList;
import com.realtime.app.Model.PlayerModel;
import com.realtime.app.Service.SecretService;
import com.realtime.app.Storage.PlayerStorage;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class SessionController extends BaseController {

    @Autowired
    PlayerStorage playerStorage;
    @Autowired
    SecretService secretService;

    @RequestMapping(value = "/register", method = POST)
    public JwtResponse register(HttpServletRequest request) {
        String payloadRequest = null;
        try {
            payloadRequest = getBody(request);
            ObjectMapper mapper = new ObjectMapper();
            PlayerModel playerModel = mapper.readValue(payloadRequest, PlayerModel.class);
            if (!playerStorage.findByUserName(playerModel.getUserName()).isPresent()) {
                playerModel.setTotalPoints(0);
                playerStorage.save(playerModel);
                Optional<PlayerModel> result = playerStorage.findByUserName(playerModel.getUserName());
                if (result.isPresent()) {
                    return generateResponse(result.get());
                }
            }
        } catch (IOException e) {
            log.info("error processing body");
            e.printStackTrace();

        }
        return null;
    }


    @RequestMapping(value = "/login", method = POST)
    public JwtResponse login(HttpServletRequest request) {
        String payloadRequest = null;
        try {
            payloadRequest = getBody(request);
            ObjectMapper mapper = new ObjectMapper();
            PlayerModel playerModel = mapper.readValue(payloadRequest, PlayerModel.class);
            Optional<PlayerModel> result = playerStorage.findByUserName(playerModel.getUserName());
            if (result.isPresent() && playerModel.getPassword().equals(result.get().getPassword())) {
                return generateResponse(result.get());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/players", method = GET)
    public PlayerList getAll() {
        Iterable<PlayerModel> playerModels = playerStorage.findAll();
        ArrayList<PlayerModel> playerModelArrayList = new ArrayList<>();
        playerModels.forEach(playerModel -> playerModelArrayList.add(playerModel));

        Collections.sort(playerModelArrayList, new Comparator<PlayerModel>(){
            public int compare(PlayerModel p1, PlayerModel p2) {
                return (int) (p2.getTotalPoints() - p1.getTotalPoints());
            }
        });

        PlayerList playerList = new PlayerList();
        playerList.setPlayerLists(playerModelArrayList);

        return playerList;
    }

    public JwtResponse generateResponse(PlayerModel playerModel) {
        String jws = Jwts.builder()
                .setIssuer("server")
                .claim("name", playerModel.getUserName())
                .claim("scope", "player")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 1000 * 60 * 30))
                .signWith(SignatureAlgorithm.HS256, secretService.getHS256SecretBytes())
                .compact();

        JwtResponse jwtResponse = new JwtResponse(jws);
        jwtResponse.setUser(playerModel);

        return jwtResponse;
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
}
