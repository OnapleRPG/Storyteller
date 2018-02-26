package com.ylinor.storyteller.data.access;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.handlers.DatabaseHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Singleton
public class PlayerDao {
    @Inject
    private DatabaseHandler databaseHandler;
    public PlayerDao() { }

    /**
     * Generate database tables if they do not exist
     */
    public void createTableIfNotExist() {
        String query = "CREATE TABLE IF NOT EXISTS player (id INT PRIMARY KEY, player VARCHAR(70), score INT)";
        databaseHandler.execute(query);
    }


}
