package com.ylinor.storyteller.data.access;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.beans.ObjectiveBean;
import com.ylinor.storyteller.data.handlers.DatabaseHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.naming.ServiceUnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class ObjectiveDao {
    private static String errorDatabasePrefix = "Error while connecting to database : ";

    @Inject
    private DatabaseHandler databaseHandler;
    public ObjectiveDao() {
    }

    /**
     * Generate database tables if they do not exist
     */
    public void createTableIfNotExist() {
        String query = "CREATE TABLE IF NOT EXISTS objective (id INT PRIMARY KEY, player VARCHAR(70), objective VARCHAR(255),state INT)";
        databaseHandler.execute(query);
    }

    /**
     * Add an objective into database
     * @param objective Objective to add
     */
    public void insertObjective(ObjectiveBean objective) {
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
        } catch (ServiceUnavailableException e) {
            Storyteller.getLogger().error(errorDatabasePrefix.concat(e.getMessage()));
        } catch (SQLException e) {
            Storyteller.getLogger().error(e.getSQLState());
        } finally {
            databaseHandler.closeConnection(connection,statement,null);
        }
    }

    /**
     * Update an objective state into database
     * @param objective Objective to update
     */
    public void updateObjectiveState(ObjectiveBean objective) {
        String query = "UPDATE objective set state = ? WHERE player = ? AND objective = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = databaseHandler.getDatasource().getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, objective.getState());
            statement.setString(2, objective.getPlayer());
            statement.setString(3, objective.getObjective());
            statement.execute();
            statement.close();
        } catch (ServiceUnavailableException e) {
            Storyteller.getLogger().error(errorDatabasePrefix.concat(e.getMessage()));
        } catch (SQLException e) {
            Storyteller.getLogger().error(e.getSQLState());
        } finally {
            databaseHandler.closeConnection(connection,statement,null);
        }
    }

    /**
     * Return objective by its name and its player, if it exists
     * @param name Label of the objective
     * @param player Player associated to the objective
     * @return Optional of an objective
     */
    public Optional<ObjectiveBean> getObjectiveByNameAndPlayer(String name, String player) {
        Optional<ObjectiveBean> objective = Optional.empty();
        String query = "SELECT player, objective, state FROM objective WHERE objective = ? AND player = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet results = null;
        try {
            connection = databaseHandler.getDatasource().getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, player);
            results = statement.executeQuery();
            while (results.next()) {
                objective = Optional.of(new ObjectiveBean(results.getString("player"), results.getString("objective"), results.getInt("state")));
            }
        } catch (ServiceUnavailableException e) {
            Storyteller.getLogger().error(errorDatabasePrefix.concat(e.getMessage()));
        } catch (SQLException e) {
            Storyteller.getLogger().error(e.getSQLState());
        } finally {
            try {
                if (results != null) {
                    results.close();
                }
            } catch (SQLException e) {
                Storyteller.getLogger().error(e.getSQLState());
            }
            databaseHandler.closeConnection(connection,statement,null);
        }
        return objective;
    }
}
