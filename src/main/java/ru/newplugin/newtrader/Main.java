package ru.newplugin.newtrader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.newplugin.newtrader.db.Base;
import ru.newplugin.newtrader.listeners.EventListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main extends JavaPlugin {
	private static Main instance;

	private FileConfiguration config;

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		config = getConfig();
		config.addDefault("host", "localhost:5432");
		config.addDefault("database", "server");
		config.addDefault("user", "user");
		config.addDefault("password", "12345");
		config.options().copyDefaults(true);
		saveConfig();

		initBase(List.of(
			"villager TEXT NOT NULL",
			"nick TEXT NOT NULL",
			"count INT DEFAULT 1 NOT NULL"
		));

		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}

	private void initBase(final List<String> table) {
		final StringBuilder cmd = new StringBuilder("CREATE TABLE IF NOT EXISTS users (");
		int i = 0;
		for (String query : table) {
			if (i != 0) {
				query = ", " + query;
			}
			cmd.append(query);
			i++;
		}
		cmd.append(");");

		final Connection sql;
		try {
			Class.forName("org.postgresql.Driver");

			sql = DriverManager.getConnection("jdbc:postgresql://" +
				config.getString("host") + "/" +
				config.getString("database") +
				"?user=" + config.getString("user") +
				"&password=" + config.getString("password")
			);

			final Statement stmt = sql.createStatement();
			stmt.executeUpdate(cmd.toString());
			stmt.close();
		} catch (final SQLException | ClassNotFoundException error) {
			error.printStackTrace();
			return;
		}

		Base.sql = sql;
	}
}
