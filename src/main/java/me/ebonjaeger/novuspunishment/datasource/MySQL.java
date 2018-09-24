package me.ebonjaeger.novuspunishment.datasource;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import me.ebonjaeger.novuspunishment.ConsoleLogger;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import me.ebonjaeger.novuspunishment.action.Kick;
import me.ebonjaeger.novuspunishment.action.PermanentBan;
import me.ebonjaeger.novuspunishment.action.TemporaryBan;
import me.ebonjaeger.novuspunishment.action.Warning;
import me.ebonjaeger.novuspunishment.configuration.DatabaseSettings;
import me.ebonjaeger.novuspunishment.configuration.SettingsManager;

import javax.inject.Inject;
import java.sql.*;

/**
 * MySQL data source class.
 *
 * This is the class you want if you want to interact with the database.
 */
public class MySQL {

	private NovusPunishment plugin;

	private String hostname;
	private String port;
	private String username;
	private String password;
	private String database;
	private String prefix;

	private HikariDataSource dataSource;

	@Inject
	public MySQL(NovusPunishment plugin, SettingsManager settings) {
		this.plugin = plugin;

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
		dataSource.addDataSourceProperty("encoding","UTF-8");
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

		statement.addBatch(MySqlStatements.createWarningsTable(prefix));
		statement.addBatch(MySqlStatements.createKicksTable(prefix));
		statement.addBatch(MySqlStatements.createTempbansTable(prefix));
		statement.addBatch(MySqlStatements.createBansTable(prefix));

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
		try (Connection conn = getConnection();
			 PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveWarningStmt(prefix))) {
			statement.setString(1, warning.getPlayerUUID().toString());
			statement.setString(2, warning.getStaffUUID().toString());
			statement.setTimestamp(3, Timestamp.from(warning.getTimestamp()));
			statement.setString(4, warning.getReason());

			statement.executeUpdate();
		} catch (SQLException ex) {
			ConsoleLogger.severe("Unable to save warning in database:", ex);
		}
	}

	/**
	 * Save a player kick to the database.
	 *
	 * @param kick The {@link Kick} being stored
	 */
	public void saveKick(Kick kick) {
		try (Connection conn = getConnection();
			 PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveKickStmt(prefix))) {
			statement.setString(1, kick.getPlayerUUID().toString());
			statement.setString(2, kick.getStaffUUID().toString());
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
		try (Connection conn = getConnection();
			 PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveTempbanStmt(prefix))) {
			statement.setString(1, tempban.getPlayerUUID().toString());
			statement.setString(2, tempban.getStaffUUID().toString());
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
		try (Connection conn = getConnection();
			 PreparedStatement statement = conn.prepareStatement(MySqlStatements.saveBanStmt(prefix))) {
			statement.setString(1, ban.getPlayerUUID().toString());
			statement.setString(2, ban.getStaffUUID().toString());
			statement.setTimestamp(3, Timestamp.from(ban.getTimestamp()));
			statement.setString(3, ban.getReason());

			statement.executeUpdate();
		} catch (SQLException ex) {
			ConsoleLogger.severe("Unable to save permanent ban in database:", ex);
		}
	}

	/*
	 * Methods for getting data from the database.
	 */


}
