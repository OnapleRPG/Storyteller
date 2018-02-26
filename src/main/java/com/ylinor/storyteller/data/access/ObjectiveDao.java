package com.ylinor.storyteller.data.access;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.beans.ObjectiveBean;
import com.ylinor.storyteller.data.handlers.DatabaseHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Singleton
public class ObjectiveDao {
    @Inject
    private DatabaseHandler databaseHandler;
    public ObjectiveDao() { }

    /**
     * Generate database tables if they do not exist
     */
    public void createTableIfNotExist() {
        String query = "CREATE TABLE IF NOT EXISTS objective (id INT PRIMARY KEY, player VARCHAR(70), objective VARCHAR(255),state INT)";
        databaseHandler.execute(query);
    }

    public void insert(ObjectiveBean objective) {
        String query = "INSERT INTO objective(player,objective,state) values(?,?,?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = databaseHandler.getDatasource().getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, objective.getPlayer());
            statement.setString(2, objective.getObjective());
            statement.setInt(3, objective.getState());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Storyteller.getLogger().error(e.getSQLState());
        }finally {
            databaseHandler.closeConnection(connection,statement,null);
        }

    }
    }
