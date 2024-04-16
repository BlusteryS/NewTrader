package ru.newplugin.newtrader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.newplugin.newtrader.db.ConnectionPool;
import ru.newplugin.newtrader.listeners.EventListener;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends JavaPlugin {
	private FileConfiguration config;

	@Override
	public void onEnable() {
		config = getConfig();

		// секция создана для возможно дальнейшего расширения конфига
		config.addDefault("database.host", "localhost:5432");
		config.addDefault("database.database", "server");
		config.addDefault("database.user", "user");
		config.addDefault("database.password", "12345");
		config.addDefault("database.params.cachePrepStmts", true);
		config.options().copyDefaults(true);

		saveConfig();
		try {
			initBase();
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}

	private void initBase() throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");

		ConnectionPool.init(
			config.getString("database.host"),
			config.getString("database.database"),
			config.getString("database.user"),
			config.getString("database.password"),
			config.getConfigurationSection("database.params").getValues(false)
		);

		try (
			final Connection conn = ConnectionPool.getConnection();
			final Statement stmt = conn.createStatement()
		) {
			stmt.executeUpdate("""
				CREATE TABLE IF NOT EXISTS users (
					villager VARCHAR(36) NOT NULL,
					nick VARCHAR(16) NOT NULL,
					count INT DEFAULT 1 NOT NULL
				)"""
			);
		} catch (final SQLException error) {
			error.printStackTrace();
		}
	}
}
