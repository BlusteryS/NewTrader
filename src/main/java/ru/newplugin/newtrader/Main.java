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
		config.addDefault("database.host", "jdbc:postgresql://localhost:5432");
		config.addDefault("database.database", "server");
		config.addDefault("database.user", "user");
		config.addDefault("database.password", "12345");
		config.options().copyDefaults(true);

		saveConfig();
		initBase();

		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}

	private void initBase() {
		ConnectionPool.init(
			config.getString("host"),
			config.getString("database"),
			config.getString("user"),
			config.getString("password"),
			config.getConfigurationSection("params").getValues(false)
		);

		// try-with-resources автоматически закрывает соединение
		try (final Connection conn = ConnectionPool.getConnection()) {
			final Statement stmt = conn.createStatement();
			stmt.executeUpdate("""
				CREATE TABLE IF NOT EXISTS users (
					villager VARCHAR(36) NOT NULL,
					nick VARCHAR(16) NOT NULL,
					count INT DEFAULT 1 NOT NULL
				)"""
			);
			stmt.close();
		} catch (final SQLException error) {
			error.printStackTrace();
		}
	}
}
