package com.realtime.app.Storage;

import com.realtime.app.Model.PlayerModel;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;


public interface PlayerStorage extends CrudRepository<PlayerModel, String> {

    Optional<PlayerModel> findByUserName(String username);
}

