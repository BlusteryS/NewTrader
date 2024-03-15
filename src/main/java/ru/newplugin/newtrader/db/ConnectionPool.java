package ru.newplugin.newtrader.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class ConnectionPool {
	private static HikariDataSource ds;

	public static void init(
		final String host,
		final String database,
		final String user,
		final String password,
		final Map<String, Object> params
	) {
		final HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://" + host + "/" + database);
		config.setUsername(user);
		config.setPassword(password);
		params.forEach((key, value) -> config.addDataSourceProperty(key, value.toString()));

		ds = new HikariDataSource(config);
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}