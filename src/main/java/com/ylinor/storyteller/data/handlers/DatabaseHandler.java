package com.ylinor.storyteller.data.handlers;

import com.ylinor.storyteller.Storyteller;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.inject.Singleton;
import javax.naming.ServiceUnavailableException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class DatabaseHandler {
    public DatabaseHandler() {}
    private String JDBC_URL = "jdbc:sqlite:./storyteller.db";
    private SqlService sqlService;
    public DataSource getDatasource() throws SQLException, ServiceUnavailableException {
        if (sqlService == null) {
            sqlService = Sponge.getServiceManager().provide(SqlService.class).orElseThrow(() -> new ServiceUnavailableException("Sponge service manager not provided."));
        }
        return sqlService.getDataSource(JDBC_URL);
    }

    public void execute(String query){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getDatasource().getConnection();
            statement = connection.prepareStatement(query);
            statement.execute();
            statement.close();
        } catch (ServiceUnavailableException e) {
            Storyteller.getLogger().error("Error while connecting to database : " + e.getMessage());
        } catch (SQLException e) {
            Storyteller.getLogger().error("Error while creating respawning dialog table : " + e.getMessage());
        } finally {
            closeConnection(connection, statement, null);
        }
    }


    /**
     * Close a database connection
     * @param connection Connection to close
     */
    public void closeConnection(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                Storyteller.getLogger().error("Error while closing result set : " + e.getMessage());
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                Storyteller.getLogger().error("Error while closing statement : " + e.getMessage());
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Storyteller.getLogger().error("Error while closing connection : " + e.getMessage());
            }
        }
    }
}
