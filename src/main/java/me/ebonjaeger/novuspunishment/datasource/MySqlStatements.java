package me.ebonjaeger.novuspunishment.datasource;

class MySqlStatements {

	/*
	 * Table creation statements.
	 */

	static String createWarningsTable(String prefix) {
		return "CREATE TABLE IF NOT EXISTS " + prefix + "player_warnings (" +
				Columns.ID + " INT UNSIGNED NOT NULL AUTO_INCREMENT," +
				Columns.UUID + " CHAR(36) NOT NULL," +
				Columns.STAFF_UUID + " CHAR(36)," +
				Columns.TIMESTAMP + " TIMESTAMP NOT NULL," +
				Columns.REASON + " VARCHAR(255) NOT NULL," +
				"PRIMARY KEY (" + Columns.ID + "));";
	}

	static String createKicksTable(String prefix) {
		return "CREATE TABLE IF NOT EXISTS " + prefix + "player_kicks (" +
				Columns.ID + " INT UNSIGNED NOT NULL AUTO_INCREMENT," +
				Columns.UUID + " CHAR(36) NOT NULL," +
				Columns.STAFF_UUID + " CHAR(36)," +
				Columns.TIMESTAMP + " TIMESTAMP NOT NULL," +
				Columns.REASON + " VARCHAR(255) NOT NULL," +
				"PRIMARY KEY (" + Columns.ID + "));";
	}

	static String createTempbansTable(String prefix) {
		return "CREATE TABLE IF NOT EXISTS " + prefix + "player_tempbans (" +
				Columns.ID + " INT UNSIGNED NOT NULL AUTO_INCREMENT," +
				Columns.UUID + " CHAR(36) NOT NULL," +
				Columns.STAFF_UUID + " CHAR(36)," +
				Columns.TIMESTAMP + " TIMESTAMP NOT NULL," +
				Columns.EXPIRES + " TIMESTAMP NOT NULL," +
				Columns.REASON + " VARCHAR(255) NOT NULL," +
				"PRIMARY KEY (" + Columns.ID + "));";
	}

	static String createBansTable(String prefix) {
		return "CREATE TABLE IF NOT EXISTS " + prefix + "player_bans (" +
				Columns.ID + " INT UNSIGNED NOT NULL AUTO_INCREMENT," +
				Columns.UUID + " CHAR(36) NOT NULL," +
				Columns.STAFF_UUID + " CHAR(36)," +
				Columns.TIMESTAMP + " TIMESTAMP NOT NULL," +
				Columns.REASON + " VARCHAR(255) NOT NULL," +
				"PRIMARY KEY (" + Columns.ID + "));";
	}

	/*
	 * Save punishments statements.
	 */

	static String saveWarningStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_warnings VALUES(null,?,?,?,?);";
	}

	static String saveKickStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_kicks VALUES(null,?,?,?,?);";
	}

	static String saveTempbanStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_tempbans VALUES(null,?,?,?,?,?);";
	}

	static String saveBanStmt(String prefix) {
		return "INSERT INTO " + prefix + "player_bans VALUES(null,?,?,?,?);";
	}

	/*
	 * Get punishments statements.
	 */

	static String getReportStmt(String prefix, int pageSize, int offset) {
		return "SELECT " + Columns.STAFF_UUID + ", " + Columns.TIMESTAMP + ", " + Columns.EXPIRES + ", " + Columns.REASON + " " +
				"FROM " + prefix + "player_warnings " +
				"LEFT JOIN " + prefix + "player_kicks USING (" + Columns.UUID + ") " +
				"LEFT JOIN " + prefix + "player_tempbans USING (" + Columns.UUID + ") " +
				"LEFT JOIN " + prefix + "player_bans USING (" + Columns.UUID + ") " +
				"WHERE " +  Columns.UUID + "=? " +
				"ORDER BY " + Columns.ID + "," + Columns.TIMESTAMP + " DESC " +
				"LIMIT " + pageSize + " OFFSET " + offset + ";";
	}
}
