package me.ebonjaeger.novuspunishment.datasource;

class MySqlStatements {

	/*
	 * Table creation statements.
	 */

	static String createActionsTable(String prefix) {
		return "CREATE TABLE IF NOT EXISTS " + prefix + "player_actions (" +
				Columns.ID + " INT UNSIGNED NOT NULL AUTO_INCREMENT," +
				Columns.UUID + " CHAR(36) NOT NULL," +
				Columns.STAFF_UUID + " CHAR(36)," +
				Columns.TIMESTAMP + " TIMESTAMP NOT NULL," +
				Columns.EXPIRES + " TIMESTAMP," +
				Columns.REASON + " VARCHAR(255)," +
				Columns.TYPE + " ENUM('warning', 'mute', 'kick', 'tempban', 'permban') NOT NULL," +
				"PRIMARY KEY (" + Columns.ID + "));";
	}

	/*
	 * Save punishments statements.
	 */

	static String saveWarningStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_actions VALUES(null,?,?,?,NULL,?,'warning');";
	}

	static String saveMuteStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_actions VALUES(null,?,?,?,?,?,'mute');";
	}

	static String saveKickStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_actions VALUES(null,?,?,?,NULL,?,'kick');";
	}

	static String saveTempbanStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_actions VALUES(null,?,?,?,?,?,'tempban');";
	}

	static String saveBanStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_actions VALUES(null,?,?,?,NULL,?,'permban');";
	}

	/*
	 * Get punishments statements.
	 */

	static String getActionsStmt(String prefix, int pageSize, int offset) {
		return "SELECT " + Columns.STAFF_UUID + ", " + Columns.TIMESTAMP + ", " + Columns.EXPIRES + ", " +
				Columns.REASON + ", " + Columns.TYPE + " " +
				"FROM " + prefix + "player_actions " +
				"WHERE " +  Columns.UUID + "=? " +
				"ORDER BY " + Columns.ID + "," + Columns.TIMESTAMP + " DESC " +
				"LIMIT " + pageSize + " OFFSET " + offset + ";";
	}
}
