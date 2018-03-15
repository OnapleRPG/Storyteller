package com.ylinor.storyteller.data.access;

import com.ylinor.storyteller.Storyteller;
import com.ylinor.storyteller.data.beans.KillCountBean;
import com.ylinor.storyteller.data.handlers.DatabaseHandler;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class KillCountDao {
    @Inject
    private DatabaseHandler databaseHandler;
    public KillCountDao() {
    }

    /**
     * Generate database tables if they do not exist
     */
    public void createTableIfNotExist() {
        String query = "CREATE TABLE IF NOT EXISTS killcount (id INT PRIMARY KEY, npcname VARCHAR(70), player VARCHAR(70), monstername VARCHAR(255), count INT)";
        databaseHandler.execute(query);
    }

    /**
     * Add a counter into database
     * @param killCountBean Counter to add
     */
    public void insertKillCount(KillCountBean killCountBean) {
        String query = "INSERT INTO killcount(npcname, player, monstername, count) values(?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = databaseHandler.getDatasource().getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, killCountBean.getNpcName());
            statement.setString(2, killCountBean.getPlayer());
            statement.setString(3, killCountBean.getMonsterName());
            statement.setInt(4, killCountBean.getCount());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Storyteller.getLogger().error(e.getSQLState());
        } finally {
            databaseHandler.closeConnection(connection,statement,null);
        }
    }

    /**
     * Get a counter from its name, the player name and the monster name
     * @param npcName Name of the NPC or the variable given
     * @param playerName Name of the player
     * @param monsterName Name of the monster to count
     * @return Optional of a kill counter
     */
    public Optional<KillCountBean> getKillCount(String npcName, String playerName, String monsterName) {
        Optional<KillCountBean> killCount = Optional.empty();
        String query = "SELECT npcname, player, monstername, count FROM killcount WHERE npcname = ? AND player = ? AND monstername = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = databaseHandler.getDatasource().getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, npcName);
            statement.setString(2, playerName);
            statement.setString(3, monsterName);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                killCount = Optional.of(new KillCountBean(results.getString("npcname"), results.getString("player"), results.getString("monstername"), results.getInt("count")));
            }
        } catch (SQLException e) {
            Storyteller.getLogger().error(e.getSQLState());
        } finally {
            databaseHandler.closeConnection(connection,statement,null);
        }
        return killCount;
    }

    /**
     * Delete a counter matching a name, a player name and a monster name
     * @param npcName Name of the NPC giving the counter or the variable assigned
     * @param playerName Name of the player
     * @param monsterName Name of the monster
     */
    public void deleteKillCount(String npcName, String playerName, String monsterName) {
        String query = "DELETE FROM killcount  WHERE npcname = ? AND player = ? AND monstername = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = databaseHandler.getDatasource().getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, npcName);
            statement.setString(2, playerName);
            statement.setString(3, monsterName);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Storyteller.getLogger().error(e.getSQLState());
        } finally {
            databaseHandler.closeConnection(connection,statement,null);
        }
    }

    /**
     * Increment every kill counter for a given player and a given mob name or mob type
     * @param playerName Player name
     * @param monsterName Name or type of the mob
     */
    public void incrementKillCount(String playerName, String monsterName) {
        String query = "UPDATE killcount SET count = count + 1 WHERE player = ? AND monstername = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = databaseHandler.getDatasource().getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, playerName);
            statement.setString(2, monsterName);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Storyteller.getLogger().error(e.getSQLState());
        } finally {
            databaseHandler.closeConnection(connection,statement,null);
        }
    }
}
