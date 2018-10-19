package me.ebonjaeger.novuspunishment.datasource;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import me.ebonjaeger.novuspunishment.ConsoleLogger;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import me.ebonjaeger.novuspunishment.PlayerState;
import me.ebonjaeger.novuspunishment.action.*;
import me.ebonjaeger.novuspunishment.configuration.DatabaseSettings;
import me.ebonjaeger.novuspunishment.configuration.SettingsManager;

import javax.inject.Inject;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MySQL data source class.
 * <p>
 * This is the class you want if you want to interact with the database.
 */
public class MySQL {

    private String hostname;
    private String port;
    private String username;
    private String password;
    private String database;
    private String prefix;

    private HikariDataSource dataSource;

    @Inject MySQL(NovusPunishment plugin, SettingsManager settings) {
        this.hostname = settings.getProperty(DatabaseSettings.DATABASE_HOSTNAME);
        this.port = settings.getProperty(DatabaseSettings.DATABASE_PORT);
        this.username = settings.getProperty(DatabaseSettings.DATABASE_USER);
        this.password = settings.getProperty(DatabaseSettings.DATABASE_PASSWORD);
        this.database = settings.getProperty(DatabaseSettings.DATABASE_NAME);
        this.prefix = settings.getProperty(DatabaseSettings.TABLE_PREFIX);

        this.dataSource = new HikariDataSource();

        // Setup the database connection
        try {
            setConnectionArguments();
        } catch (IllegalArgumentException ex) {
            ConsoleLogger.severe("Invalid database arguments! Please check your configuration:", ex);
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        } catch (HikariPool.PoolInitializationException ex) {
            ConsoleLogger.severe("Can't initialize database connection! Please check your configuration:", ex);
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }

        // Create the database tables
        try (Connection conn = getConnection()) {
            createTables(conn);
        } catch (SQLException ex) {
            ConsoleLogger.severe("Unable to create the necessary database tables:", ex);
            close();
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private void setConnectionArguments() {
        dataSource.setPoolName("NovusPunishment_SQL_Pool");
        dataSource.setMaximumPoolSize(10);

        dataSource.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);

        // Authentication
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // Encoding
        dataSource.addDataSourceProperty("characterEncoding", "utf8");
        dataSource.addDataSourceProperty("encoding", "UTF-8");
        dataSource.addDataSourceProperty("useUnicode", "true");

        // Random stuff
        dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");
        dataSource.addDataSourceProperty("jdbcCompliantTruncation", "false");

        // Caching
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "275");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    private void createTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        statement.addBatch(MySqlStatements.createActionsTable(prefix));
        statement.addBatch(MySqlStatements.createStateTable(prefix));

        statement.executeBatch();
        statement.close();
    }

    /**
     * Get a connection from the Hikari connection pool.
     *
     * @return The next connection in the pool
     * @throws SQLException If there was an exception creating a connection
     */
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Close down the Hikari data source if it is still open.
     */
    public void close() {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /*
     * Methods for saving punishments into the database.
     */

    /**
     * Save a player warning to the database.
     *
     * @param warning The {@link Warning} being stored
     */
    public void saveWarning(Warning warning) {
        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveWarningStmt(prefix))
        ) {
            statement.setString(1, warning.getPlayerUUID().toString());
            statement.setString(2, warning.getStaff());
            statement.setTimestamp(3, Timestamp.from(warning.getTimestamp()));
            statement.setString(4, warning.getReason());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ConsoleLogger.severe("Unable to save warning in database:", ex);
        }
    }

    /**
     * Save a mute action to the database.
     *
     * @param mute The {@link Mute} being stored
     */
    public void saveMute(Mute mute) {
        // Timestamps from `Instant.MAX` cause a data truncation error.
        // In these cases, set it to `null` instead, which is allowed.
        Timestamp expires = null;
        if (!mute.getExpires().equals(Instant.MAX)) {
            expires = Timestamp.from(mute.getExpires());
        }

        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveMuteStmt(prefix))
        ) {
            statement.setString(1, mute.getPlayerUUID().toString());
            statement.setString(2, mute.getStaff());
            statement.setTimestamp(3, Timestamp.from(mute.getTimestamp()));
            statement.setTimestamp(4, expires);
            statement.setString(5, mute.getReason());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ConsoleLogger.severe("Unable to save mute in database:", ex);
        }
    }

    /**
     * Save a player kick to the database.
     *
     * @param kick The {@link Kick} being stored
     */
    public void saveKick(Kick kick) {
        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveKickStmt(prefix))
        ) {
            statement.setString(1, kick.getPlayerUUID().toString());
            statement.setString(2, kick.getStaff());
            statement.setTimestamp(3, Timestamp.from(kick.getTimestamp()));
            statement.setString(4, kick.getReason());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ConsoleLogger.severe("Unable to save kick in database:", ex);
        }
    }

    /**
     * Save a tempban to the database.
     *
     * @param tempban The {@link TemporaryBan} being stored
     */
    public void saveTempban(TemporaryBan tempban) {
        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveTempbanStmt(prefix))
        ) {
            statement.setString(1, tempban.getPlayerUUID().toString());
            statement.setString(2, tempban.getStaff());
            statement.setTimestamp(3, Timestamp.from(tempban.getTimestamp()));
            statement.setTimestamp(4, Timestamp.from(tempban.getExpires()));
            statement.setString(5, tempban.getReason());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ConsoleLogger.severe("Unable to save tempban in database:", ex);
        }
    }

    /**
     * Save a permanent ban to the database.
     *
     * @param ban The {@link PermanentBan} being stored
     */
    public void saveBan(PermanentBan ban) {
        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveBanStmt(prefix))
        ) {
            statement.setString(1, ban.getPlayerUUID().toString());
            statement.setString(2, ban.getStaff());
            statement.setTimestamp(3, Timestamp.from(ban.getTimestamp()));
            statement.setString(4, ban.getReason());

            statement.executeUpdate();
        } catch (SQLException ex) {
            ConsoleLogger.severe("Unable to save permanent ban in database:", ex);
        }
    }

    /**
     * Save a player's current state into the database.
     * If there is already old state in the database, the existing entries will be updated instead.
     *
     * @param playerState The {@link PlayerState} to save
     */
    public void savePlayerState(PlayerState playerState) {
        Timestamp until = null;
        if (playerState.getUntil() != null) {
            until = Timestamp.from(playerState.getUntil());
        }

        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.savePlayerStmt(prefix))
        ) {
            // INSERT values
            statement.setString(1, playerState.getUniqueID().toString());
            statement.setString(2, playerState.getUserName());
            statement.setBoolean(3, playerState.isMuted());
            statement.setTimestamp(4, until);

            // UPDATE values
            statement.setString(5, playerState.getUserName());
            statement.setBoolean(6, playerState.isMuted());
            statement.setTimestamp(7, until);

            statement.executeUpdate();
        } catch (SQLException ex) {
            ConsoleLogger.severe(String.format("Unable to save player state in database for player '%s':", playerState.getUserName()), ex);
        }
    }

    /*
     * Methods for getting data from the database.
     */

    /**
     * Get a page view of a player's past incidents.
     * <p>
     * What is returned will be limited to only the results in the requested page.
     *
     * @param playerUUID The {@link UUID} of the player to get the report for
     * @param page       The page number to get
     * @param pageSize   The number of entries in a page
     * @return A List of {@link Action}'s
     * @throws IllegalArgumentException If there is an unknown action type stored in the database.
     */
    public List<Action> getActionsAgainstUser(UUID playerUUID, int page, int pageSize) {
        List<Action> actions = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.getActionsStmt(prefix, pageSize, offset))
        ) {
            statement.setString(1, playerUUID.toString());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Put all of the row's data into objects
                String staff = resultSet.getString(Columns.STAFF_UUID);
                Instant timestamp = resultSet.getTimestamp(Columns.TIMESTAMP).toInstant();
                String reason = resultSet.getString(Columns.REASON);

                // Some actions (mutes) may not have reasons
                if (reason == null) {
                    reason = "";
                }

                // Only temporary actions have an 'expires' column entry that isn't null
                Timestamp rawExpires = resultSet.getTimestamp(Columns.EXPIRES);
                Instant expires = null;
                if (rawExpires != null) {
                    expires = rawExpires.toInstant();
                }

                String type = resultSet.getString(Columns.TYPE);
                switch (type) {
                    case "warning":
                        actions.add(new Warning(playerUUID, staff, timestamp, reason));
                        break;
                    case "mute":
                        actions.add(new Mute(playerUUID, staff, timestamp, expires, reason));
                        break;
                    case "kick":
                        actions.add(new Kick(playerUUID, staff, timestamp, reason));
                        break;
                    case "tempban":
                        actions.add(new TemporaryBan(playerUUID, staff, timestamp, expires, reason));
                        break;
                    case "permban":
                        actions.add(new PermanentBan(playerUUID, staff, timestamp, reason));
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Unknown action type in database: %s", type));
                }
            }

            resultSet.close();
        } catch (SQLException ex) {
            ConsoleLogger.severe(String.format("Unable to get past actions for player with unique ID '%s'", playerUUID.toString()), ex);
        }

        return actions;
    }

    /**
     * Load a player's currently saved state from the database. If no state
     * has been saved (e.g. the player is joining the server for the first time),
     * this method returns {@code null}.
     *
     * @param uniqueID The {@link UUID} of the player
     * @return The saved {@link PlayerState} if exists, or null
     */
    public PlayerState getPlayerState(UUID uniqueID) {
        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.getPlayerStmt(prefix))
        ) {
            statement.setString(1, uniqueID.toString());

            ResultSet result = statement.executeQuery();
            if (result != null && result.next()) {
                String userName = result.getString(Columns.PLAYERNAME);
                boolean isMuted = result.getBoolean(Columns.MUTED);
                Instant until = null;
                if (result.getTimestamp(Columns.EXPIRES) != null) {
                    until = result.getTimestamp(Columns.EXPIRES).toInstant();
                }

                result.close();

                return new PlayerState(uniqueID, userName, isMuted, until);
            }
        } catch (SQLException ex) {
            ConsoleLogger.severe(String.format("Unable to get the current state for player with unique ID '%s'", uniqueID.toString()), ex);
        }

        return null;
    }

    /**
     * Get an active {@link TemporaryBan} for a player, if one exists.
     * Returns {@code null} if the player has no active tempban.
     *
     * @param uniqueID The player's unique ID
     * @return The player's active tempban, or null
     */
    public TemporaryBan getActiveTempban(UUID uniqueID) {
        try (
            Connection conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(MySqlStatements.getLatestTempbanStmt(prefix))
        ) {
            statement.setString(1, uniqueID.toString());
            statement.setTimestamp(2, Timestamp.from(Instant.now()));

            ResultSet result = statement.executeQuery();
            if (result != null && result.next()) {
                String staff = result.getString(Columns.STAFF_UUID);
                Instant timeStamp = result.getTimestamp(Columns.TIMESTAMP).toInstant();
                Instant expires = result.getTimestamp(Columns.EXPIRES).toInstant();
                String reason = result.getString(Columns.REASON);

                return new TemporaryBan(uniqueID, staff, timeStamp, expires, reason);
            }
        } catch (SQLException ex) {
            ConsoleLogger.severe(String.format("Unable to get latest tempban for '%s':", uniqueID.toString()), ex);
        }

        return null;
    }
}
